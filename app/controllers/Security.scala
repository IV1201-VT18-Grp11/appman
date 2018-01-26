package controllers

import database.{ Id, User }
import models.UserManager
import play.api.libs.typedmap.TypedKey
import play.api.mvc._
import scala.concurrent.{ ExecutionContext, Future }


trait Security {
  protected def userManager: UserManager

  protected def checkUser(implicit ec: ExecutionContext) = new ActionRefiner[Request, Request] {
    override def executionContext = ec
    override def refine[A](request: Request[A]) = {
      getUser(request).map {
        case Some(user) =>
          Right(request.addAttr(Security.user, user))
        case None =>
          Right(request)
      }
    }
  }

  def getUserId(request: RequestHeader): Option[Id[User]] =
    request.session.get(Security.sessionKey).map(id => Id[User](id.toLong))

  def getUser(request: RequestHeader): Future[Option[User]] =
    getUserId(request) match {
      case Some(id) =>
        userManager.find(id)
      case None =>
        Future.successful(None)
    }

  def setUserId(response: Result, request: RequestHeader, userId: Id[User]): Result =
    response.addingToSession(Security.sessionKey -> userId.raw.toString())(request)

  def setUser(response: Result, request: RequestHeader, user: User): Result =
    setUserId(response, request, user.id)
}

object Security {
  val user = TypedKey[User]("user")

  val sessionKey = "USER"
}
