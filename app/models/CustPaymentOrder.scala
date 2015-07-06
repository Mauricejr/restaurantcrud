package models
//import java.util.Date
//import java.sql.Timestamp
import play.api.libs.json._
import play.api.libs.functional.syntax._
case class CustPaymentOrder(id: Option[Long], customerOrderID: Long, paymentTypeID: Option[Long], totalPrice: Option[Double], customerId: Long, pricePaid: Double)

object CustPaymentOrder {
  implicit val customerOder = Json.format[CustPaymentOrder]
}