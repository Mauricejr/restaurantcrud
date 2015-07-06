package models

import play.api.libs.json._
case class UserInformation(user: User, address: Address)
case class Family(name: String, id: Long)
case class Address(id: Option[Long], house_number: String, street: String, city: String, state: String, country: String, user_id: Option[Long])
case class User(id: Option[Long], firstName: String, lastName: String, phone: String, email: String, address_id: Option[Long])

object UserInformation {
  implicit val userAddress = Json.format[Address]
  implicit val userI = Json.format[User]
  implicit val userInfo = Json.format[UserInformation]

}

object User {
  implicit val customerOder = Json.format[User]
}
