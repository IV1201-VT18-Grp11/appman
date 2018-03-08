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
  * Static "landing"-style actions.
  */
@Singleton
class HomeController @Inject()(implicit jobManager: JobManager,
                               val userManager: UserManager,
                               cc: ControllerComponents,
                               executionContext: ExecutionContext)
    extends AbstractController(cc)
    with Security {

  def index() = userAction().apply { implicit request: Request[AnyContent] =>
    Ok(views.html.index())
  }

  def show500() = userAction().apply { implicit request: Request[AnyContent] =>
    throw new Exception("Whoopsie, this shouldn't have happened!")
  }
}
