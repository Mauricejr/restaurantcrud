package models
import play.api.libs.json._
import play.api.libs.functional.syntax._
case class PaymentType(id: Option[Long], typeOfPayment: String, number: Long, name: String)

object PaymentType {
  implicit val customerOrder = Json.format[PaymentType]

}