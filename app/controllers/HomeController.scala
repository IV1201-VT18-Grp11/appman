package controllers

import javax.inject._
import models.UserManager

import scala.concurrent.ExecutionContext
import database.PgProfile
import models.JobManager
import play.api._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.mvc._

/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class HomeController @Inject()(implicit jobManager: JobManager,
                               val userManager: UserManager,
                               cc: ControllerComponents,
                               executionContext: ExecutionContext)
    extends AbstractController(cc)
    with Security {

  /**
    * Create an Action to render an HTML page.
    *
    * The configuration in the `routes` file means that this method
    * will be called when the application receives a `GET` request with
    * a path of `/`.
    */
  def index() = userAction.apply { implicit request: Request[AnyContent] =>
    Ok(views.html.index())
  }
  def joblist() = userAction.async { implicit request: Request[AnyContent] =>
    for {
      listings <- jobManager.jobListings()
    } yield Ok(views.html.joblist(listings))
  }
}
