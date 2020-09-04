package utils.auth

import com.mohiva.play.silhouette.api.Env
import com.mohiva.play.silhouette.impl.authenticators.SessionAuthenticator
import models.Account

trait DefaultEnv extends Env {
  type I = Account
  type A = SessionAuthenticator
}
