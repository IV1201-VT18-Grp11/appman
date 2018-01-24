package controllers

import javax.inject._
import database.PgProfile
import play.api._
import play.api.db.slick.{ DatabaseConfigProvider, HasDatabaseConfigProvider }
import play.api.mvc._

/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class HomeController @Inject()(protected val dbConfigProvider: DatabaseConfigProvider, cc: ControllerComponents)
    extends AbstractController(cc) with HasDatabaseConfigProvider[PgProfile] {

  /**
    * Create an Action to render an HTML page.
    *
    * The configuration in the `routes` file means that this method
    * will be called when the application receives a `GET` request with
    * a path of `/`.
    */
  def index() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index())
  }

  def register() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.register())
  }

  def login() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.login())
  }

  def joblist() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.joblist())
  }

  def logout() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.logout())
  }
}
