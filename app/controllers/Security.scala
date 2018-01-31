package controllers

import database.{ Id, User }
import models.UserManager
import play.api.libs.typedmap.TypedKey
import play.api.mvc._
import scala.concurrent.{ ExecutionContext, Future }

trait SecurityHelpers {
  def getUserId(request: RequestHeader): Option[Id[User]] =
    request.session.get(Security.sessionKey).map(id => Id[User](id.toLong))

  def getUser(request: RequestHeader): Option[User] =
    request.attrs
      .get(Security.user)
      .getOrElse(throw new NoSessionLoaderException)

  def clearUser(response: Result, request: RequestHeader): Result =
    response.removingFromSession(Security.sessionKey)(request)

  def setUserId(response: Result, request: RequestHeader, userId: Id[User]): Result =
    response.addingToSession(Security.sessionKey -> userId.raw.toString())(request)

  def setUser(response: Result, request: RequestHeader, user: User): Result =
    setUserId(response, request, user.id)

  implicit class UserReqHeader(private val req: RequestHeader) {
    def user: Option[User] = getUser(req)
    def loggedIn: Boolean = user.isDefined
  }
}

class NoSessionLoaderException extends Exception(
  "attempted to access session before it was loaded, use Security.userAction or checkUser instead of Action")

trait Security extends SecurityHelpers {
  protected def userManager: UserManager
  protected def Action: ActionBuilder[Request, AnyContent]

  def checkUser(implicit ec: ExecutionContext) = new ActionTransformer[Request, Request] {
    override def executionContext = ec
    override def transform[A](request: Request[A]) =
      findUser(request).map(user =>
        request.addAttr(Security.user, user))
  }

  def requireUser(implicit ec: ExecutionContext) = new ActionFilter[Request] {
    override def executionContext = ec
    override def filter[A](request: Request[A]) = Future.successful {
      getUser(request) match {
        case Some(_) => None
        case None => Some(Results.Redirect(routes.LoginController.login(target = Some(request.uri))))
      }
    }
  }

  def userAction(implicit ec: ExecutionContext) = checkUser compose Action
  def userRequiredAction(implicit ec: ExecutionContext) = requireUser compose checkUser compose Action

  def findUser(request: RequestHeader): Future[Option[User]] =
    getUserId(request) match {
      case Some(id) =>
        userManager.find(id)
      case None =>
        Future.successful(None)
    }
}

object Security extends SecurityHelpers {
  val user = TypedKey[Option[User]]("user")

  val sessionKey = "USER"
}
