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
import models.TableSit
import javax.inject.Inject

class TableDAO @Inject() {

  val singleTablesitParsers: ResultSetParser[Option[TableSit]] = {
    tableParser.singleOpt
  }

  val multTablesitParsers: ResultSetParser[List[TableSit]] = {
    tableParser.*
  }

  def findTable(id: Long): Option[TableSit] = {
    DB.withConnection("restaurant") { implicit connection =>
      val sql = SQL("select * from tableSit where is occupied")
      sql.on("id" -> id).as(singleTablesitParsers)
    }
  }

  def addTable(tbl: TableSit): Option[Long] = {
    println(tbl)
    val id: Option[Long] = DB.withConnection("restaurant") { implicit connection =>
      SQL("""insert
      into TableSit
      values ({id}, {maxCapacity},{ minCapacity},{name},{occupied})""").on(
        "id" -> tbl.id,
        "maxCapacity" -> tbl.maxCapacity,
        "minCapacity" -> tbl.minCapacity,
        "name" -> tbl.name,
        "occupied" -> 0).executeInsert()
    }
    id
  }

  def updateTable(tbl: TableSit) = {
    DB.withConnection("restaurant") { implicit connection =>
      SQL("""update tableSit set
        occupied = {occupied},
         where id = {id}
       """).on(
        "id" -> tbl.id,
        "occupied" -> tbl.occupied).executeUpdate()
    }
  }
  def tableParser: RowParser[TableSit] = (
    get[Option[Long]]("id") ~ get[Long]("maxCapacity") ~ get[Long]("minCapacity") ~ get[String]("name") ~ get[Option[Boolean]]("occupied")) map {
      case id ~ maxCapacity ~ minCapacity ~ name ~ occupied =>
        TableSit(id, maxCapacity, minCapacity, name, occupied)
    }

}