package forms

import play.api.data.Form
import play.api.data.Forms._

object RegisterForm {
  val form = Form(
    mapping(
      "fullName" -> nonEmptyText,
      "email" -> email,
      "password" -> nonEmptyText
    )(Data.apply)(Data.unapply)
  )

  case class Data(
      fullName: String,
      email: String,
      password: String
  )
}
