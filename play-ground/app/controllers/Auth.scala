package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import models._
import services.{AccountService, AuthenticationService}
import com.mohiva.play.silhouette.api.Silhouette
import utils.auth.DefaultEnv
import forms._
import play.api.i18n._
import scala.concurrent.Future
import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import com.mohiva.play.silhouette.api.util.PasswordHasherRegistry
import scala.concurrent.ExecutionContext
import com.mohiva.play.silhouette.api.services.AuthenticatorResult

/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class Auth @Inject() (
    silhoutte: Silhouette[DefaultEnv],
    accountService: AccountService,
    authenticationService: AuthenticationService,
    passwordHasherRegistry: PasswordHasherRegistry,
    controllerComponents: ControllerComponents
)(implicit ec: ExecutionContext)
    extends AbstractController(controllerComponents)
    with I18nSupport {

  /**
    * Create an Action to render an HTML page.
    *
    * The configuration in the `routes` file means that this method
    * will be called when the application receives a `GET` request with
    * a path of `/`.
    */
  def login() =
    silhoutte.UnsecuredAction { implicit request =>
      Ok(views.html.login(LoginForm.form))
    }

  def handleLogin() =
    Action.async { implicit request =>
      LoginForm.form
        .bindFromRequest()
        .fold(
          form => Future.successful(BadRequest(views.html.login(form))),
          data => {
            authenticationService
              .credentials(data.email, data.password)
              .flatMap {
                case Right((account, loginInfo)) =>
                  authenticateAccount(account, loginInfo)
                case Left(e) =>
                  Future.successful(
                    Redirect(routes.Auth.login()).flashing("error" -> s"$e")
                  )
              }
          }
        )
    }

  def register() =
    silhoutte.UnsecuredAction { implicit request =>
      Ok(views.html.register(RegisterForm.form))
    }

  def handleRegister() =
    Action.async { implicit request =>
      RegisterForm.form
        .bindFromRequest()
        .fold(
          form => Future.successful(BadRequest(views.html.register(form))),
          data => {
            val loginInfo = LoginInfo(CredentialsProvider.ID, data.email)
            accountService.retrieve(loginInfo).flatMap {
              case Some(account) =>
                Future.successful(
                  Redirect(routes.Auth.register())
                    .flashing("error" -> Messages("account.exists"))
                )
              case None =>
                val authInfo =
                  passwordHasherRegistry.current.hash(data.password)
                for {
                  account <- accountService.createOrUpdate(
                    loginInfo,
                    data.email,
                    Some(data.fullName),
                    None
                  )
                  _ <- authenticationService.addAuthenticationMethod(
                    account.accountId,
                    loginInfo,
                    authInfo
                  )
                  result <- authenticateAccount(account, loginInfo)
                } yield (result)

            }
          }
        )
    }

  protected def authenticateAccount(account: Account, loginInfo: LoginInfo)(
      implicit request: Request[_]
  ): Future[AuthenticatorResult] =
    for {
      authenticator <- silhoutte.env.authenticatorService.create(loginInfo)
      value <- silhoutte.env.authenticatorService.init(authenticator)
      result <- silhoutte.env.authenticatorService.embed(
        value,
        Redirect(routes.HomeController.index())
      )
    } yield (result)

}
