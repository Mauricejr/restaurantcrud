package controllers.helper
import models.CustomerOrders
import models.Order

object CalculatePrice {

  def cal(quantity: Long, price: Option[Double]) = {
    quantity.toDouble * price.get
  }

}