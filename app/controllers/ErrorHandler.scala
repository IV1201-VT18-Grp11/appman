package controllers

import javax.inject.{Inject, Provider}

import play.api.mvc.{RequestHeader, Result, Results}
import play.api.routing.Router
import play.api.{
  Configuration,
  Environment,
  OptionalSourceMapper,
  UsefulException
}
import play.api.http.DefaultHttpErrorHandler

import scala.concurrent.Future

/**
  * Handles errors and shows corresponding messages to the user.
  */
class ErrorHandler @Inject()(env: Environment,
                             config: Configuration,
                             sourceMapper: OptionalSourceMapper,
                             router: Provider[Router])
    extends DefaultHttpErrorHandler(env, config, sourceMapper, router) {
  override def onServerError(request: RequestHeader,
                             exception: Throwable): Future[Result] =
    exception match {
      case notFound: NotFoundException =>
        onNotFound(request, notFound.getMessage)
      case _ =>
        super.onServerError(request, exception)
    }

  override protected def onNotFound(request: RequestHeader,
                                    message: String): Future[Result] =
    Future.successful(
      Results.NotFound(
        views.html.error.notFound()(Security.addNoUserToRequest(request))
      )
    )

  override protected def onProdServerError(
    request: RequestHeader,
    exception: UsefulException
  ): Future[Result] =
    Future.successful(
      Results.InternalServerError(
        views.html.error.internalError()(Security.addNoUserToRequest(request))
      )
    )
}
