package daos

import java.util.UUID

import com.mohiva.play.silhouette.api.LoginInfo

import models.Account
import com.google.inject.ImplementedBy

import scala.concurrent.Future

/**
  * Give access to LoginInfo object
  */
@ImplementedBy(classOf[LoginInfoDAOImpl])
trait LoginInfoDAO {

  /**
    * Get list of user authentication methods providers
    *
    * @param email user email
    * @return
    */
  def getAuthenticationProviders(email: String): Future[Seq[String]]

  /**
    * Finds a user and login info pair by userID and login info providerID
    *
    * @param userId     user id
    * @param providerId provider id
    * @return Some(User, LoginInfo) if there is a user by userId which has login method for provider by provider ID, otherwise None
    */
  def find(
      userId: UUID,
      providerId: String
  ): Future[Option[(Account, LoginInfo)]]

  /**
    * Saves a login info for user
    *
    * @param userID The user id.
    * @param loginInfo login info
    * @return unit
    */
  def saveAccountLoginInfo(userID: UUID, loginInfo: LoginInfo): Future[Unit]
}
