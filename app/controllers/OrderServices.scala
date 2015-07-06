package controllers

import play.api.libs.json._
import play.api.libs.functional.syntax._
import scala.concurrent.Future
import play.api.mvc._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc._
import models.dao._
import models.Order
import models.CustomerOrder
import models.CustomerOrders
import models.User
import models.CustPaymentOrder
import controllers.helper.FutureHelper._
import javax.inject.Inject
import controllers.helper.CalculatePrice

class OrderServices @Inject() (orderServicesDAO: OrderServicesDAO, paymentDAO: PaymentDAO, productDAO: ProductDAO) extends Controller {
  def getAllUsers = Action.async { implicit request =>
    val allCustomerOrders: Future[Map[CustomerOrder, List[Order]]] = scala.concurrent.Future {
      orderServicesDAO.getALLCustomersOrder
    }
    allCustomerOrders map { order =>
      Ok(Json.toJson(convertoCust(order)))
    }
  }

  def getOrderByCustomerId(userId: Long) = Action.async { implicit request =>
    val allCustomerOrders: Future[List[CustomerOrder]] = scala.concurrent.Future {
      orderServicesDAO.findUserOrderByUserID(userId)
    }
    allCustomerOrders map { custOrder =>
      Ok(Json.toJson((custOrder)))
    }
  }

  def orderItem = Action.async(BodyParsers.parse.json) { implicit request =>
    val custOrder = request.body.validate[CustomerOrders]
    custOrder.fold(
      errors => {
        val error = Future("Ok" -> "Error validating Json")
        error.map(x =>
          BadRequest(Json.obj("message" -> JsError.toJson(errors))))
      },
      order => {
        getConvertToFutureResult(orderServicesDAO.createOrder(order.custOrder)) { custOrderId =>
          Future.successful {
            val productID = (id: Long) => productDAO.findProduct(id) map { x => x.price }
            orderServicesDAO.createOrderItems(custOrderId, order.orders)
            val productPrice = order.orders map { x => CalculatePrice.cal(x.item_quantity, productID(x.item_id)) }
            val totalPriceOwn = productPrice.foldLeft(0.0) { (a, value) => value + a }
            paymentDAO.addPayment(CustPaymentOrder(None, custOrderId, None, Some(totalPriceOwn), order.custOrder.user_Id, 0))
            Ok(Json.toJson((s"Successfully place order, customer orderId : $custOrderId")))
          }
        }
      })
  }

  def payForOrder = Action.async(BodyParsers.parse.json) { implicit request =>
    val custOrderPayment = request.body.validate[CustPaymentOrder]
    custOrderPayment.fold(
      errors => {
        val error = Future("error" -> "Error validating Json")
        error.map(x =>
          BadRequest(Json.obj("message" -> JsError.toJson(errors))))
      },
      customerpayment => {
        getFutureResult(customerpayment.customerOrderID, orderServicesDAO.findUserOrderByOrderId(customerpayment.customerOrderID)) { custOrderId =>
          Future.successful {
            val totalPriceOwn = paymentDAO.findUserPaymentByCustOrderId(customerpayment.customerOrderID).flatMap(x => x.totalPrice)
            val isPricePaidEqualToAmountOwn = (totalPriceOwn.get < customerpayment.pricePaid)
            isPricePaidEqualToAmountOwn match {
              case true => {
                paymentDAO.updateUserPayment(customerpayment.copy(totalPrice = totalPriceOwn))
                Ok(Json.toJson((s"Successfully pay for order $order.customerOrderID")))
              }
              case false =>
                Ok(Json.toJson((s"price paid lower than expecting price : $totalPriceOwn")))
            }
          }
        }
      })
  }

  def getCustomerOrder(orderId: Long) = Action.async { implicit request =>
    getFutureResult(orderId, orderServicesDAO.findUserOrderByOrderId(orderId)) { c =>
      getConvertToFutureResult(orderServicesDAO.findUserOrderItem(orderId, c)) { x =>
        Future.successful {
          Ok(Json.toJson((x)))
        }
      }
    }
  }
}
