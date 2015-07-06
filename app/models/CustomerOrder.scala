package models
import play.api.libs.json._
case class Order(id: Option[Long], item_id: Long, item_quantity: Long, customerOrder_Id: Option[Long], comments: Option[String])
case class CustomerOrder(id: Option[Long], number_of_people: Long, server_Id: Option[Long], table_Id: Option[Long],
  user_Id: Long, comments: Option[String])
case class CustomerAllOrders(ord: List[CustomerOrder])
case class CustomerAllOrder(ord: List[CustomerAllOrders])
case class CustomerOrders(custOrder: CustomerOrder, orders: List[Order])

object CustomerOrders {
  implicit val customerOder = Json.format[CustomerOrder]
  implicit val order = Json.format[Order]
  implicit val customerOderes = Json.format[CustomerOrders]
}

object CustomerOrder {
  implicit val customerOder = Json.format[CustomerOrder]
}

object Order {
  implicit val order = Json.format[Order]
}
