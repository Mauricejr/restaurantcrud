package controllers
import play.api.libs.json._
import play.api.libs.functional.syntax._
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc._
import models.dao._
import models.User
import models.UserInformation
import models.Address
import controllers.helper.FutureHelper._
import javax.inject.Inject

class UserServices @Inject() (orderServicesDAO: OrderServicesDAO, userServicesDAO: UserServicesDAO) extends Controller with Results {

  def getAllUsers = Action.async { implicit request =>
    implicit val customerOder = Json.format[User]
    implicit val users: Future[List[User]] = scala.concurrent.Future {
      userServicesDAO.getAllUsers
    }
    users.map { user =>
      Ok(Json.toJson(user))
    }
  }

  def findUser(userId: Long) = Action.async { implicit request =>
    implicit val address = Json.format[Address]
    implicit val user = Json.format[User]
    implicit val customerOder = Json.format[UserInformation]
    getFutureWithOption(userId, userServicesDAO.findUser(userId)) { c =>
      getConvertToFutureResult(userServicesDAO.findAddress(userId)) { x =>
        Future.successful {
          Ok(Json.toJson((UserInformation(c, x))))
        }
      }
    }

  }

  def createUser = Action.async(BodyParsers.parse.json) { implicit request =>
    val userInformation = request.body.validate[UserInformation]
    userInformation.fold(
      errors => {
        val bad = Future("Ok" -> "ok")
        bad.map(x =>
          BadRequest(Json.obj("message" -> JsError.toJson(errors))))
      },
      user => {
        getConvertToFutureResult(userServicesDAO.addUser(user.user)) { userId =>
          getConvertToFutureResult(userServicesDAO.addAddress(user.address.copy(user_id = Some(userId)))) { addressId =>
            Future.successful {
              userServicesDAO.updateUser(userId, addressId)
              Ok(Json.toJson((userId)))
            }
          }
        }

      })
  }

  def deleteUser(userId: Long) = Action.async { implicit request =>
    implicit val hasUserDeleted: Future[Boolean] = scala.concurrent.Future {
      userServicesDAO.deleteUser(userId)
    }
    hasUserDeleted map { b =>
      b match {
        case false => Ok(Json.obj("User not" -> "User not deleted"))
        case true => Ok(s"User ID $Json.toJson(userId) succeful deleted" + userServicesDAO.deleteAddres(userId))
      }
    }

  }
}