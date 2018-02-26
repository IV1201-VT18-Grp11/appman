package controllers

import scala.concurrent.{ExecutionContext, Future}

/**
  * Extension methods for Option[A] and Future[Option[A]] that show a
  *  404 Not Found error page if the Option is not defined (None).
  */
trait NotFoundHelpers {
  implicit class NotFoundOption[A](value: Option[A]) {
    def getOr404: A =
      value.getOrElse(throw new NotFoundException())
  }

  implicit class NotFoundFutureOption[A](value: Future[Option[A]]) {
    def getOr404(implicit ec: ExecutionContext): Future[A] =
      value.map(_.getOr404)
  }
}

/**
  * A special exception that shows a 404 Not Found error page.
  */
class NotFoundException extends Exception("The value was not found")
