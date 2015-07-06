package models.dao
import models.Order
import anorm._
import anorm.SqlParser._
import play.api.db.DB
import play.api.Play.current
import models._
import anorm.RowParser
import anorm.ResultSetParser
import anorm.SQL
import anorm.SqlQuery
import javax.inject.Inject
class OrderServicesDAO @Inject() {

  val orderParsers: ResultSetParser[List[Order]] = {
    orderParser.*
  }

  val singleOrderParsers: ResultSetParser[Order] = {
    orderParser.single
  }
  val singleOrderOptionParsers: ResultSetParser[Option[Order]] = {
    orderParser.singleOpt
  }

  val singleCDROrderOptionParsers: ResultSetParser[Option[CustomerOrder]] = {
    customerOrderParser.singleOpt
  }

  val singleCDROrderItemOptionParsers: ResultSetParser[Option[Order]] = {
    orderParser.singleOpt
  }

  def findUserOrderByOrderId(orderId: Long): Option[CustomerOrder] = {
    DB.withConnection("restaurant") { implicit connection =>
      val sql = SQL("select * from custOrder where id = {id}")
      sql.on("id" -> orderId).as(singleCDROrderOptionParsers)
    }
  }

  val customerOrderParsers: ResultSetParser[List[CustomerOrder]] = {
    customerOrderParser.*
  }

  def findUserOrderByUserID(userId: Long): List[CustomerOrder] = {
    DB.withConnection("restaurant") { implicit connection =>
      val sql = SQL("select * from custOrder where user_id = {user_id}")
      sql.on("user_id" -> userId).as(customerOrderParsers)
    }
  }

  def findUserOrderItem(userId: Long, cusrOrder: CustomerOrder): Option[CustomerOrders] = {
    DB.withConnection("restaurant") { implicit connection =>
      val sql = SQL("select * from customerOrderItems where customerOrder_id = {customerOrder_id}")
      val results: List[Order] =
        sql.on("customerOrder_id" -> userId).as(orderParsers)
      val cs = Option(CustomerOrders(cusrOrder, results.toList))
      cs
    }
  }

  def getAllUsers(): List[Order] =
    DB.withConnection("restaurant") { implicit connection =>
      val sql: SqlQuery = SQL("select * from order")
      sql.as(orderParsers)
    }

  def custorOrderandItemsOrderParser: RowParser[(CustomerOrder, Order)] = {
    customerOrderParser ~ orderParser map { flatten }
  }

  def getALLCustomersOrder: Map[CustomerOrder, List[Order]] = {
    DB.withConnection("restaurant") { implicit connection =>
      val sql = SQL("select cdr.*, c.* " +
        "from custOrder cdr " +
        "inner join customerOrderItems c on (cdr.id = c.customerOrder_id)")
      val results: List[(CustomerOrder, Order)] =
        sql.as(custorOrderandItemsOrderParser *)
      results.groupBy { _._1 }.mapValues { _.map { _._2 } }
    }
  }

  def findUserOrderItemt(userId: Long, cusrOrder: CustomerOrder): Option[CustomerOrders] = {
    DB.withConnection("restaurant") { implicit connection =>
      val sql = SQL("select * from customerOrderItems where customerOrder_id = {customerOrder_id}")
      val results: List[Order] =
        sql.on("customerOrder_id" -> userId).as(orderParsers)
      val cs = Option(CustomerOrders(cusrOrder, results.toList))
      cs
    }
  }

  def getCustomerOrder(orderID: Long): Map[CustomerOrder, List[Order]] = {
    DB.withConnection("restaurant") { implicit connection =>
      val sql = SQL("select cdr.*, c.* " +
        "from custOrder cdr " +
        "left join customerOrderItems c on (cdr.id = c.customerOrder_id) ")
      val results: List[(CustomerOrder, Order)] =
        sql.on("customerOrder_id" -> orderID).as(custorOrderandItemsOrderParser *)
      results.groupBy { _._1 }.mapValues { _.map { _._2 } }
    }
  }

  def createOrder(custOrder: CustomerOrder) = {
    val id: Option[Long] = DB.withConnection("restaurant") { implicit connection =>
      SQL("""insert
      into custOrder
      values ({id}, {number_of_people}, {server_Id}, {table_Id},{user_Id}, {comments})""").on(
        "id" -> custOrder.id,
        "number_of_people" -> custOrder.number_of_people,
        "server_Id" -> custOrder.server_Id,
        "table_Id" -> custOrder.table_Id,
        "user_Id" -> custOrder.user_Id,
        "comments" -> custOrder.comments).executeInsert()
    }
    id
  }

  def updateCustOrder(custOrder: CustomerOrder) = {
    val custId: Option[Long] = DB.withConnection("restaurant") { implicit connection =>
      SQL(""" update
       custOrder set
       values ({id}, {number_of_people}, {server_Id}, {table_Id},{user_Id}, {comments})""").on(
        "id" -> custOrder.id,
        "number_of_people" -> custOrder.number_of_people,
        "server_Id" -> custOrder.server_Id,
        "table_Id" -> custOrder.table_Id,
        "user_Id" -> custOrder.user_Id,
        "comments" -> custOrder.comments).executeInsert()
    }
    custId
  }

  def createOrderItems(orderId: Long, orderItems: List[Order]) = {
    orderItems map { orderItem =>
      val orderItems = DB.withConnection("restaurant") { implicit connection =>
        SQL("""insert
      into customerOrderItems
      values ({id}, {item_id}, {item_quantity},{customerOrder_Id}, {comments})""").on(
          "id" -> orderItem.id,
          "item_id" -> orderItem.item_id,
          "item_quantity" -> orderItem.item_quantity,
          "customerOrder_Id" -> orderId,
          "comments" -> orderItem.comments).executeInsert()
      }
      orderItems
    }
  }

  def createOrderItem(orderId: Long, orderItems: Order) = {
    val productId = DB.withConnection("restaurant") { implicit connection =>
      SQL("""insert
      into customerOrderItems
      values ({id}, {item_id}, {item_quantity},{item_quantityt},{customerOrder_Id}, {comments})""").on(
        "id" -> orderItems.id,
        "item_id" -> orderItems.item_id,
        "item_quantity" -> orderItems.item_quantity,
        "customerOrder_Id" -> orderId,
        "comments" -> orderItems.comments).executeInsert()
    }
    productId
  }

  def orderParser: RowParser[Order] = (
    get[Option[Long]]("id") ~ get[Long]("item_id") ~ get[Long]("item_quantity") ~ get[Option[Long]]("customerOrder_Id") ~ get[Option[String]]("comments")) map {
      case id ~ item_id ~ item_quantity ~ customerOrder_Id ~ comments =>
        Order(id, item_id, item_quantity, customerOrder_Id, comments)
    }

  def customerOrderParser: RowParser[CustomerOrder] = (
    get[Option[Long]]("id") ~ get[Long]("number_of_people") ~ get[Option[Long]]("server_Id") ~ get[Option[Long]]("table_Id") ~ get[Long]("user_Id") ~ get[Option[String]]("comments")) map {
      case id ~ number_of_people ~ server_Id ~ table_Id ~ user_Id ~ comments =>
        CustomerOrder(id, number_of_people, server_Id, table_Id, user_Id, comments)
    }

}