package controllers

import database.User
import models.UserManager
import play.api.libs.typedmap.TypedKey
import play.api.mvc.{ Result, _ }
import scala.concurrent.{ ExecutionContext, Future }


trait Security { this: AbstractController =>
  protected def userManager: UserManager

  protected def checkUser(implicit ec: ExecutionContext) = new ActionRefiner[Request, Request] {
    override def executionContext = ec
    override def refine[A](input: Request[A]) = {
      input.session.get(Security.sessionKey) match {
        case Some(id) =>
          userManager.find(id.toLong).map(maybeUser => Right(maybeUser.fold(input)(user => input.addAttr(Security.user, user))))
        case None =>
          Future.successful(Right(input))
      }
    }
  }

  protected def setUser(respose: Result, request: RequestHeader, user: User): Result =
    respose.addingToSession(Security.sessionKey -> user.id.toString())(request)
}

object Security {
  val user = TypedKey[User]("user")

  private[Security] val sessionKey = "USER"
}
