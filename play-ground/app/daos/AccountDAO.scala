package daos

import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}
import javax.inject.Inject
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import com.mohiva.play.silhouette.api.LoginInfo

import models.Account

/**
  * Give access to Account object.
  */
trait AccountDAO {

  /**
    * Save the account
    *
    * @param user
    * @return
    */
  def save(account: Account): Future[Account]

  /**
    * Finds account by it's login info
    *
    * @param loginInfo the login info of the account to find
    * @return The found account or None if account cannot be found
    */
  def find(loginInfo: LoginInfo): Future[Option[Account]]

  /**
    * Finds account by it's account id
    *
    * @param accountId the account ID to be found
    * @return The found account or None if account cannot be found
    */
  def find(accountId: UUID): Future[Option[Account]]

  def findByEmail(email: String): Future[Option[Account]]

}
