package models

import java.util.UUID

import play.api.libs.json.Json

import com.mohiva.play.silhouette.api.{Identity, LoginInfo}
import com.mohiva.play.silhouette.api.util.PasswordInfo

/**
  * The Account object
  *
  * @param accountId account id
  * @param loginInfo
  * @param confirmed
  * @param email
  * @param fullName
  * @param passwordInfo
  * @param avatarUrl
  */
final case class Account(
    accountId: UUID,
    activated: Boolean,
    email: Option[String],
    fullName: Option[String],
    avatarUrl: Option[String]
) extends Identity

object Account {
  implicit val passwordInfoJsonFormat = Json.format[PasswordInfo]
  implicit val accountJsonFormat = Json.format[Account]
}
