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
import models.Product
import javax.inject.Inject
class ProductDAO @Inject() {

  val singleProductParsers: ResultSetParser[Option[Product]] = {
    productParser.singleOpt
  }

  def findProduct(productId: Long): Option[Product] = {
    DB.withConnection("restaurant") { implicit connection =>
      val sql = SQL("select * from products where id = {id}")
      sql.on("id" -> productId).as(singleProductParsers)
    }
  }

  def addProduct(product: Product): Option[Long] = {
    val id: Option[Long] = DB.withConnection("restaurant") { implicit connection =>
      SQL("""insert
      into products
      values ({id}, {price},{quantity},{name})""").on(
        "id" -> product.id,
        "price" -> product.price,
        "quantity" -> product.quantity,
        "name" -> product.name).executeInsert()
    }
    id
  }

  def updateUserPayment(payment: CustPaymentOrder) = {
    println(payment)
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

  def productParser: RowParser[Product] = (
    get[Option[Long]]("id") ~ get[Double]("price") ~ get[Long]("quantity") ~ get[String]("name")) map {
      case id ~ price ~ quantity ~ name =>
        Product(id, price, quantity, name)
    }
}

