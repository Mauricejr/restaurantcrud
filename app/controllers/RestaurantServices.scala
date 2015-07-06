package controllers
import javax.inject.Named

import javax.inject.Inject
import play.api.libs.json._
import play.api.libs.functional.syntax._

import scala.concurrent.Future
import play.api.mvc._
import models.dao.ProductDAO
import models.dao.TableDAO
import models.dao.PaymentDAO
import models.Product
import models.TableSit
import models.PaymentType
import scala.concurrent.Future
import scala.concurrent.ExecutionContext
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import controllers.helper.FutureHelper._
class RestaurantServices @Inject() (productDAO: ProductDAO, tableDAO: TableDAO, paymentDAO: PaymentDAO) extends Controller {

  def createProducts = Action.async(BodyParsers.parse.json) { implicit request =>
    implicit val customerOder = Json.format[Product]
    val newProduct = request.body.validate[Product]
    newProduct.fold(
      errors => {
        val error = Future("Ok" -> "Error validating Json")
        error.map(x =>
          BadRequest(Json.obj("message" -> JsError.toJson(errors))))
      },
      product => {
        getConvertToFuturet(productDAO.addProduct(product)) { productId =>
          Future.successful {
            Ok(Json.toJson((s"successfully inserting new product to databas $productId")))
          }
        }
      })
  }

  def createPaymentType = Action.async(BodyParsers.parse.json) { implicit request =>
    val newPaymentType = request.body.validate[PaymentType]
    newPaymentType.fold(
      errors => {
        val error = Future("Ok" -> "Error validating Json")
        error.map(x =>
          BadRequest(Json.obj("message" -> JsError.toJson(errors))))
      },

      paymentType => {
        getConvertToFuturet(paymentDAO.addPaymentType(paymentType)) { productId =>
          Future.successful {
            Ok(Json.toJson((s"successfully inserting new product to databas $productId")))
          }
        }
      })
  }

  def createTable = Action.async(BodyParsers.parse.json) { implicit request =>
    val newTable = request.body.validate[TableSit]
    newTable.fold(
      errors => {
        val error = Future("Ok" -> "Error validating Json")
        error.map(x =>
          BadRequest(Json.obj("message" -> JsError.toJson(errors))))
      },
      table => {
        getConvertToFuturet(tableDAO.addTable(table)) { tableId =>
          Future.successful {
            Ok(Json.toJson((s"successfully inserting new table to databas $tableId")))
          }
        }
      })

  }

}