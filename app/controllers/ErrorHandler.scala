package controllers

import javax.inject.{Inject, Provider}
import play.api.mvc.{RequestHeader, Result}
import play.api.routing.Router
import play.api.{Configuration, Environment, OptionalSourceMapper}
import play.api.http.{DefaultHttpErrorHandler, HttpErrorHandler}
import scala.concurrent.Future

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
}
