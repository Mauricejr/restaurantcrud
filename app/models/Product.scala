package models
import play.api.libs.json._
import play.api.libs.functional.syntax._
case class Product(id: Option[Long], price: Double, quantity: Long, name: String)

object Prodcut {
  implicit val product = Json.format[Product]
}