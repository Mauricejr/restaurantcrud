package controllers.helper
import scala.concurrent.Future
import models.CustomerOrder
import models.CustomerOrders
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.libs.json.Json._
import play.api.mvc._
import models._
import play.api.mvc.Results._

import play.api.libs.concurrent.Execution.Implicits.defaultContext
object FutureHelper {
  implicit val customerOder = Json.format[CustomerOrder]
  implicit val order = Json.format[Order]
  def getConvertToFuturet[T](futureOptionBlock: Option[T])(success: (T => Future[Result])): Future[Result] = {

    futureOptionBlock match {
      case Some(found) =>
        success(found)
      case None =>
        Future.successful(NotFound)
    }
  }

  def getConverttoFuturet[T](futureOptionBlock: Option[T])(success: (T => Future[Result])): Future[Result] = {

    futureOptionBlock match {
      case Some(found) =>
        success(found)
      case None =>
        Future.successful(NotFound)
    }
  }

  def getConvertToFuturetBol[T](futureOptionBlock: Option[Boolean])(success: (T => Future[Result])): Future[Result] = {

    futureOptionBlock match {
      case Some(found) =>
        Future.successful(NotFound)
      case None =>
        Future.successful(NotFound)
    }
  }

  def getFutureWithOption[T](id: Long, futureOptionBlock: Option[T])(success: (T => Future[Result])): Future[Result] = {

    futureOptionBlock match {
      case Some(found) =>
        success(found)
      case None =>
        Future.successful(NotFound.apply(s"ID not found $id"))
    }
  }

  def converListtoCust(orders: List[(CustomerOrder, List[Order])]) = {
    val s = for (
      x <- orders
    ) yield (Option(CustomerOrders(x._1, x._2)))
    s
  }

  def convertoCust(orders: Map[CustomerOrder, List[Order]]) = {

    val s = for (
      x <- orders
    ) yield (((Option(CustomerOrders(x._1, x._2)))))
    s

  }

}