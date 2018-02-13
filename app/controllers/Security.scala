package controllers

import database.{Id, Role, User, UserSession}
import models.UserManager
import play.api.libs.typedmap.TypedKey
import play.api.mvc._
import scala.concurrent.{ExecutionContext, Future}

trait SecurityHelpers {
  def getSessionId(request: RequestHeader): Option[Id[UserSession]] =
    request.session
      .get(Security.sessionKey)
      .map(id => Id[UserSession](id.toLong))

  def getUserSession(request: RequestHeader): Option[UserSession] =
    request.attrs
      .get(Security.session)
      .getOrElse(throw new NoSessionLoaderException())

  def getUser(request: RequestHeader): Option[User] =
    request.attrs
      .get(Security.user)
      .getOrElse(throw new NoSessionLoaderException)

  def clearUser(response: Result, request: RequestHeader): Result =
    response.removingFromSession(Security.sessionKey)(request)

  def setUserSessionId(response: Result,
                       request: RequestHeader,
                       sessionId: Id[UserSession]): Result =
    response.addingToSession(Security.sessionKey -> sessionId.raw.toString())(
      request
    )

  def setUserSession(response: Result,
                     request: RequestHeader,
                     session: UserSession): Result =
    setUserSessionId(response, request, session.id)

  implicit class UserReqHeader(private val req: RequestHeader) {
    def user: Option[User]               = getUser(req)
    def userSession: Option[UserSession] = getUserSession(req)
    def userRole: Role                   = user.map(_.role).getOrElse(Role.Anonymous)
    def loggedIn: Boolean                = user.isDefined
  }
}

class NoSessionLoaderException
    extends Exception(
      "attempted to access session before it was loaded, use Security.userAction or checkUser instead of Action"
    )

trait Security extends SecurityHelpers {
  protected def userManager: UserManager
  protected def Action: ActionBuilder[Request, AnyContent]

  def checkUser(implicit ec: ExecutionContext) =
    new ActionTransformer[Request, Request] {
      override def executionContext = ec
      override def transform[A](request: Request[A]) =
        findUser(request).map(
          session =>
            request
              .addAttr(Security.user, session.map(_._1))
              .addAttr(Security.session, session.map(_._2))
        )
    }

  def requireRole(role: Role)(implicit ec: ExecutionContext) =
    new ActionFilter[Request] {
      override def executionContext = ec
      override def filter[A](request: Request[A]) = Future.successful {
        if (request.userRole >= role) {
          None
        } else {
          Some(
            Results.Redirect(
              routes.LoginController.login(target = Some(request.uri))
            )
          )
        }
      }
    }

  def userAction(
    requiredRole: Role = Role.Anonymous
  )(implicit ec: ExecutionContext) =
    requireRole(requiredRole) compose checkUser compose Action

  def findUser(
    request: RequestHeader
  )(implicit ec: ExecutionContext): Future[Option[(User, UserSession)]] =
    getSessionId(request) match {
      case Some(id) =>
        userManager.findSession(id)
      case None =>
        Future.successful(None)
    }
}

object Security extends SecurityHelpers {
  val user    = TypedKey[Option[User]]("user")
  val session = TypedKey[Option[UserSession]]("session")

  val sessionKey = "SESSION"
}
