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
    request.attrs.get(Security.user)

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

trait Security extends SecurityHelpers { this: BaseController =>
  protected def userManager: UserManager

  def checkUser(implicit ec: ExecutionContext) = new ActionRefiner[Request, Request] {
    override def executionContext = ec
    override def refine[A](request: Request[A]) = {
      findUser(request).map {
        case Some(user) =>
          Right(request.addAttr(Security.user, user))
        case None =>
          Right(request)
      }
    }
  }

  def userAction(implicit ec: ExecutionContext) = checkUser compose Action

  def findUser(request: RequestHeader): Future[Option[User]] =
    getUserId(request) match {
      case Some(id) =>
        userManager.find(id)
      case None =>
        Future.successful(None)
    }
}

object Security extends SecurityHelpers {
  val user = TypedKey[User]("user")

  val sessionKey = "USER"
}
