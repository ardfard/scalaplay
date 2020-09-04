package services

import java.util.UUID

import com.mohiva.play.silhouette.api.LoginInfo
import javax.inject.Inject
import models.Account
import daos.{LoginInfoDAO, AccountDAO}
import scala.concurrent.{ExecutionContext, Future}

class AccountServiceImpl @Inject() (
    accountDAO: AccountDAO,
    loginInfoDAO: LoginInfoDAO
)(implicit ec: ExecutionContext)
    extends AccountService {
  def retrieve(loginInfo: LoginInfo): Future[Option[Account]] =
    accountDAO.find(loginInfo)

  def retrieveAccountLoginInfo(
      id: UUID,
      providerID: String
  ): Future[Option[(Account, LoginInfo)]] =
    loginInfoDAO.find(id, providerID)

  def createOrUpdate(
      loginInfo: LoginInfo,
      email: String,
      fullName: Option[String],
      avatarUrl: Option[String]
  ): Future[Account] =
    Future
      .sequence(Seq(accountDAO.find(loginInfo), accountDAO.findByEmail(email)))
      .flatMap { accounts =>
        accounts.flatten.headOption match {
          case Some(account) =>
            accountDAO.save(
              account.copy(
                fullName = fullName,
                email = Some(email),
                avatarUrl = avatarUrl
              )
            )
          case None =>
            accountDAO.save(
              Account(
                accountId = UUID.randomUUID(),
                fullName = fullName,
                email = Some(email),
                avatarUrl = avatarUrl,
                activated = false
              )
            )
        }
      }
  def setEmailActivated(account: Account): Future[Account] =
    accountDAO.save(account.copy(activated = true))
}
