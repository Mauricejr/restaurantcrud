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
class OrderServices {
  val userParsers: ResultSetParser[List[User]] = {
    userParser.*
  }

  val singleUserParsers: ResultSetParser[User] = {
    userParser.single
  }
  val singleUserOptionParsers: ResultSetParser[Option[User]] = {
    userParser.singleOpt
  }
  def getAllUsers(): List[User] =

    DB.withConnection("restaurant") { implicit connection =>
      val sql: SqlQuery = SQL("select * from user order by lastName asc")

      sql.as(userParsers)
    }

  def findUser(userId: Long): Option[User] = {
    DB.withConnection("restaurant") { implicit connection =>
      val sql = SQL("select * from user where id = {id}")
      sql.on("id" -> userId).as(singleUserOptionParsers)
    }
  }

  def deleteUser(userId: Long): Boolean =
    DB.withConnection("restaurant") { implicit connection =>
      val sql = SQL("delete from user where id = {id}")
      sql.on("id" -> userId).executeUpdate() == 1
    }

  def userParser: RowParser[User] = (

    get[Option[Long]]("id") ~ get[String]("firstName") ~ get[String]("lastName") ~ get[String]("email") ~ str("phone") ~ get[Option[Long]]("address_id")) map {
      case id ~ firstName ~ lastName ~ phone ~ email ~ address_id =>
        User(id, firstName, lastName, phone, email, address_id)

    }

}