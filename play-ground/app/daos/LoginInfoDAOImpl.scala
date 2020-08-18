package daos

import java.util.UUID

import com.mohiva.play.silhouette.api.LoginInfo
import javax.inject.Inject
import models.Account
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.{ExecutionContext, Future}

/**
  * Give access to the user object.
  */
class LoginInfoDAOImpl @Inject() (
    protected val dbConfigProvider: DatabaseConfigProvider
)(implicit ec: ExecutionContext)
    extends LoginInfoDAO
    with DAOSlick {
  import profile.api._

  /**
    * Saves a login info for user
    *
    * @param userID The user id.
    * @param loginInfo login info
    * @return unit
    */
  override def saveUserLoginInfo(
      accountID: UUID,
      loginInfo: LoginInfo
  ): Future[Unit] = {

    val dbLoginInfo =
      DBLoginInfo(None, loginInfo.providerID, loginInfo.providerKey)
    // We don't have the LoginInfo id so we try to get it first.
    // If there is no LoginInfo yet for this user we retrieve the id on insertion.
    val loginInfoAction = {
      val retrieveLoginInfo = slickLoginInfos
        .filter(info =>
          info.providerID === loginInfo.providerID &&
            info.providerKey === loginInfo.providerKey
        )
        .result
        .headOption
      val insertLoginInfo = slickLoginInfos
        .returning(slickLoginInfos.map(_.id))
        .into((info, id) => info.copy(id = Some(id))) += dbLoginInfo
      for {
        loginInfoOption <- retrieveLoginInfo
        dbLoginInfo <-
          loginInfoOption.map(DBIO.successful(_)).getOrElse(insertLoginInfo)
      } yield dbLoginInfo
    }

    // combine database actions to be run sequentially
    val actions = (for {
      dbLoginInfo <- loginInfoAction
      userLoginInfo = DBAccountLoginInfo(accountID, dbLoginInfo.id.get)
      exists <- existsUserLoginInfo(userLoginInfo)
      _ <-
        if (exists) DBIO.successful(())
        else slickAccountLoginInfos += userLoginInfo
    } yield ()).transactionally

    // run actions and return user afterwards
    db.run(actions)
  }

  private def existsUserLoginInfo(uli: DBAccountLoginInfo) = {
    slickAccountLoginInfos
      .filter(e =>
        e.loginInfoId === uli.loginInfoId && e.accountID === uli.accountID
      )
      .exists
      .result
  }

  /**
    * Finds a user, login info pair by userID and login info providerID
    *
    * @param userId     user id
    * @param providerId provider id
    * @return Some(User, LoginInfo) if there is a user by userId which has login method for provider by provider ID, otherwise None
    */
  def find(
      accountId: UUID,
      providerId: String
  ): Future[Option[(Account, LoginInfo)]] = {
    val action = for {
      ((_, li), u) <-
        slickAccountLoginInfos
          .filter(_.accountID === accountId)
          .join(slickLoginInfos)
          .on(_.loginInfoId === _.id)
          .join(slickAccounts)
          .on(_._1.accountID === _.id)
      if li.providerID === providerId
    } yield (u, li)

    db.run(action.result.headOption)
      .map(_.map {
        case (u, li) => (DBAccount.toAccount(u), DBLoginInfo.toLoginInfo(li))
      })
  }

  /**
    * Get list of user authentication methods providers
    *
    * @param email user email
    * @return
    */
  override def getAuthenticationProviders(
      email: String
  ): Future[Seq[String]] = {
    val action = for {
      ((_, _), li) <-
        slickAccounts
          .filter(_.email === email)
          .join(slickAccountLoginInfos)
          .on(_.id === _.accountID)
          .join(slickLoginInfos)
          .on(_._2.loginInfoId === _.id)
    } yield li.providerID

    db.run(action.result)
  }
}
