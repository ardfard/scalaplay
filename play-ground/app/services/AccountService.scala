package services

import java.util.UUID

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.services.IdentityService

import models.Account

import scala.concurrent.Future

trait AccountService extends IdentityService[Account] {

  /**
    *  Retrieves a user and login info pair by userID and login info providerID
    *
    * @param id The ID to retrieve a user.
    * @param providerID The ID of login info provider.
    * @return The retrieved user or None if no user could be retrieved for the given ID.
    */
  def retrieveAccountLoginInfo(
      id: UUID,
      providerID: String
  ): Future[Option[(Account, LoginInfo)]]

  def createOrUpdate(
      loginInfo: LoginInfo,
      email: Option[String],
      fullName: Option[String],
      AvatarUrl: Option[String]
  ): Future[Account]

  def setEmailActivated(account: Account): Future[Account]

}
