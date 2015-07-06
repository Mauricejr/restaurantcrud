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
class UserServicesDAO {
  val userParsers: ResultSetParser[List[User]] = {
    userParser.*
  }

  val singleUserParsers: ResultSetParser[User] = {
    userParser.single
  }
  val singleUserOptionParsers: ResultSetParser[Option[User]] = {
    userParser.singleOpt
  }

  val singleAddressParsers: ResultSetParser[Option[Address]] = {
    addressRowParser.singleOpt
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

  def findAddress(userId: Long): Option[Address] = {
    DB.withConnection("restaurant") { implicit connection =>
      val sql = SQL("select * from address where user_id = {user_id}")
      sql.on("user_id" -> userId).as(singleAddressParsers)
    }
  }

  def addAddress(address: Address): Option[Long] = {
    val id: Option[Long] = DB.withConnection("restaurant") { implicit connection =>
      SQL("""insert
      into address
      values ({id}, {house_number}, {street}, {city},{state}, {country},{user_id})""").on( //#A
        "id" -> address.id,
        "house_number" -> address.house_number,
        "street" -> address.street,
        "city" -> address.city,
        "state" -> address.state,
        "country" -> address.country,

        "user_id" -> address.user_id.get).executeInsert()
    }
    id
  }

  def addUser(user: User): Option[Long] = {
    println("success added user")
    val id: Option[Long] = DB.withConnection("restaurant") { implicit connection =>
      SQL("""insert
      into user
      values ({id}, {firstName}, {lastName}, {phone},{email}, {address_id})""").on( //#A
        "id" -> user.id,
        "firstName" -> user.firstName,
        "lastName" -> user.lastName,
        "phone" -> user.phone,
        "email" -> user.email,
        "address_id" -> user.address_id).executeInsert()

    }

    id
  }

  def updateUser(userId: Long, address_id: Long) = {
    println(userId + " " + address_id)

    DB.withConnection("restaurant") { implicit connection =>
      SQL("""update user
      set address_id = {address_id}
       where id = {id}
       """).on(
        "id" -> userId,
        "address_id" -> address_id).
        executeUpdate() == 1
    }

  }

  def addressRowParser: RowParser[Address] = {
    get[Option[Long]]("id") ~ get[String]("house_number") ~ get[String]("street") ~ get[String]("city") ~ str("state") ~ str("country") ~ get[Option[Long]]("user_id") map {
      case id ~ house_number ~ street ~ city ~ state ~ country ~ user_id =>
        Address(id, house_number, street, city, state, country, user_id)
    }
  }

  def deleteUser(userId: Long) = {
    val hasUserDeleted: Boolean = DB.withConnection("restaurant") { implicit connection =>
      val sql = SQL("delete from user where id = {id}")
      sql.on("id" -> userId).executeUpdate() == 1
    }
    //Some(hasUserDeleted)
    hasUserDeleted
  }
  def deleteAddres(userId: Long) = {
    val addressDeleted: Boolean = DB.withConnection("restaurant") { implicit connection =>
      val sql = SQL("delete from address where user_id = {user_id}")
      sql.on("user_id" -> userId).executeUpdate() == 1
    }

    // Some(addressDeleted)
    addressDeleted

  }

  def userParser: RowParser[User] = (

    get[Option[Long]]("id") ~ get[String]("firstName") ~ get[String]("lastName") ~ get[String]("email") ~ str("phone") ~ get[Option[Long]]("address_id")) map {
      case id ~ firstName ~ lastName ~ phone ~ email ~ address_id =>
        User(id, firstName, lastName, phone, email, address_id)

    }

}