package controllers

import scala.concurrent.{ExecutionContext, Future}

trait NotFoundHelpers {
  implicit class NotFoundOption[A](value: Option[A]) {
    def getOr404: A =
      value.getOrElse(throw new NotFoundHelpers.NotFoundException())
  }

  implicit class NotFoundFutureOption[A](value: Future[Option[A]]) {
    def getOr404(implicit ec: ExecutionContext): Future[A] =
      value.map(_.getOr404)
  }
}

object NotFoundHelpers {
  class NotFoundException extends Exception("The value was not found")
}
