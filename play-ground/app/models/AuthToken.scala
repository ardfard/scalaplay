package models

import java.util.UUID
import java.time.ZonedDateTime

/**
  * A token to authenticate a user againts an endpoint for short period of time
  *
  * @param id          The unique token ID
  * @param account_id  The unique ID of the user
  * @param expiry
  */
final case class AuthToken(id: UUID, account_id: UUID, expiry: ZonedDateTime)
