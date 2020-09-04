package services

import javax.inject.Inject
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.{LoginInfo, AuthInfo}
import daos._
import models._
import scala.concurrent.ExecutionContext
import java.util.UUID
import scala.concurrent.Future
import com.mohiva.play.silhouette.api.util.Credentials
import com.mohiva.play.silhouette.impl.exceptions.IdentityNotFoundException
import com.mohiva.play.silhouette.impl.exceptions.InvalidPasswordException
import play.api.data.validation.Invalid

class AuthenticationService @Inject() (
    credentialProvider: CredentialsProvider,
    accountService: AccountService,
    authInfoRepository: AuthInfoRepository,
    loginInfoDAO: LoginInfoDAO
)(implicit ec: ExecutionContext) {
  def credentials(
      email: String,
      password: String
  ): Future[Either[AuthenticationError, (Account, LoginInfo)]] = {
    val credentials = Credentials(email, password)
    credentialProvider
      .authenticate(credentials)
      .flatMap { loginInfo =>
        accountService.retrieve(loginInfo).map {
          case Some(account) =>
            Right((account, loginInfo))
          case None =>
            Left(AccountNotFound)
        }
      }
      .recoverWith {
        case _: IdentityNotFoundException =>
          Future.successful(Left(AccountNotFound))
        case _: InvalidPasswordException =>
          Future.successful(Left(InvalidPassword))
        case e =>
          Future.failed(e)
      }
  }
  def addAuthenticationMethod[T <: AuthInfo](
      accountId: UUID,
      loginInfo: LoginInfo,
      authInfo: T
  ): Future[Unit] = {
    for {
      _ <- loginInfoDAO.saveAccountLoginInfo(accountId, loginInfo)
      _ <- authInfoRepository.add(loginInfo, authInfo)
    } yield ()
  }
}

sealed trait AuthenticationError
object InvalidPassword extends AuthenticationError
object AccountNotFound extends AuthenticationError
