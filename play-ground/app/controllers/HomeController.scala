package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import models._
import com.mohiva.play.silhouette.api.Silhouette
import utils.auth.DefaultEnv

/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class HomeController @Inject() (
    silhoutte: Silhouette[DefaultEnv],
    config: Configuration,
    val controllerComponents: ControllerComponents
) extends BaseController {

  /**
    * Create an Action to render an HTML page.
    *
    * The configuration in the `routes` file means that this method
    * will be called when the application receives a `GET` request with
    * a path of `/`.
    */
  def index() =
    silhoutte.UserAwareAction { implicit request =>
      Ok(views.html.index(request.identity))
    }
}
