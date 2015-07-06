package models.dao
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
import models.PaymentType
class PaymentDAO @Inject() {

  val singlePaymentParsers: ResultSetParser[Option[CustPaymentOrder]] = {
    paymentParser.singleOpt
  }

  val singlePaymentTypeParsers: ResultSetParser[Option[PaymentType]] = {
    paymentTypeParser.singleOpt
  }

  val paymentTypeParsers: ResultSetParser[List[PaymentType]] = {
    paymentTypeParser.*
  }

  def getAllPaymentTypes(): List[PaymentType] = {
    DB.withConnection("restaurant") { implicit connection =>
      val sql: SqlQuery = SQL("select * from PaymentType")
      sql.as(paymentTypeParsers)
    }
  }

  def addPayment(payment: CustPaymentOrder): Option[Long] = {
    val id: Option[Long] = DB.withConnection("restaurant") { implicit connection =>
      SQL("""insert
      into CustPaymentOrder
      values ({id}, {customerOrderID},{paymentTypeID},{totalPrice},{customerId},{pricePaid})""").on( //#A
        "id" -> payment.id,
        "customerOrderID" -> payment.customerOrderID,
        "paymentTypeID" -> payment.paymentTypeID,
        "totalPrice" -> payment.totalPrice,
        "customerId" -> payment.customerId,
        "pricePaid" -> payment.pricePaid).executeInsert()
    }
    id
  }

  def findUserPaymentByCustOrderId(custOrderID: Long): Option[CustPaymentOrder] = {
    DB.withConnection("restaurant") { implicit connection =>
      val sql = SQL("select * from CustPaymentOrder where customerOrderID = {customerOrderID}")
      sql.on("customerOrderID" -> custOrderID).as(singlePaymentParsers)
    }
  }

  def addPaymentType(paymentType: PaymentType): Option[Long] = {
    val id: Option[Long] = DB.withConnection("restaurant") { implicit connection =>
      SQL("""insert
      into CustPaymentOrder
      values ({id},{typeOfPaymen},{number},{name})""").on(
        "id" -> paymentType.id,
        "typeOfPayment" -> paymentType.typeOfPayment,
        "number" -> paymentType.number,
        "name" -> paymentType.name).executeInsert()
    }
    id
  }

  def updateUserPayment(payment: CustPaymentOrder) = {
    DB.withConnection("restaurant") { implicit connection =>
      SQL("""update CustPaymentOrder set
       pricePaid = {pricePaid},
       totalPrice = {totalPrice},
       paymentTypeID = {paymentTypeID}
        where customerOrderID = {customerOrderID}
       """).on(
        "pricePaid" -> payment.pricePaid,
        "totalPrice" -> payment.totalPrice,
        "customerOrderID" -> payment.customerOrderID,
        "paymentTypeID" -> payment.paymentTypeID).executeUpdate()
    }

  }
  def paymentParser: RowParser[CustPaymentOrder] = (
    get[Option[Long]]("id") ~ get[Long]("customerOrderID") ~ get[Option[Long]]("paymentTypeID") ~ get[Option[Double]]("totalPrice") ~ get[Long]("customerId") ~ get[Double]("pricePaid")) map {
      case id ~ customerOrderID ~ paymentTypeID ~ totalPrice ~ customerId ~ pricePaid =>
        CustPaymentOrder(id, customerOrderID, paymentTypeID, totalPrice, customerId, pricePaid)
    }

  def paymentTypeParser: RowParser[PaymentType] = (
    get[Option[Long]]("id") ~ get[String]("typeOfPayment") ~ get[Long]("number") ~ get[String]("name")) map {
      case id ~ typeOfPayment ~ number ~ name =>
        PaymentType(id, typeOfPayment, number, name)
    }
}