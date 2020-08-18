package daos

import java.util.UUID

import com.google.inject.Inject
import play.api.db.slick.DatabaseConfigProvider
import scala.concurrent.ExecutionContext
import com.mohiva.play.silhouette.api.LoginInfo
import models.Account

class AccountDAOImpl @Inject() (
    protected val dbConfigProvider: DatabaseConfigProvider
)(implicit ec: ExecutionContext)
    extends AccountDAO
    with DAOSlick {

  import profile.api._

  def find(loginInfo: LoginInfo) = {
    val accountQuery = for {
      dbLoginInfo <- loginInfoQuery(loginInfo)
      dbAccountLoginInfo <-
        slickAccountLoginInfos.filter(_.loginInfoId === dbLoginInfo.id)
      dbAccount <- slickAccounts.filter(_.id === dbAccountLoginInfo.accountID)
    } yield dbAccount
    db.run(accountQuery.result.headOption).map { dbAccountOption =>
      dbAccountOption.map { account =>
        Account(
          account.accountID,
          account.activated,
          account.email,
          account.fullName,
          account.avatarURL
        )
      }
    }
  }

  def find(accountID: UUID) = {
    val query = slickAccounts.filter(_.id === accountID)
    db.run(query.result.headOption).map { resultOption =>
      resultOption.map(DBAccount.toAccount)
    }
  }

  def save(account: Account) = {
    val dbAccount = DBAccount.fromAccount(account)
    val actions = (for {
      _ <- slickAccounts.insertOrUpdate(dbAccount)
    } yield ()).transactionally
    db.run(actions).map(_ => account)
  }

  def findByEmail(email: String) = {
    db.run(slickAccounts.filter(_.email === email).take(1).result.headOption)
      .map(_ map DBAccount.toAccount)
  }

}
