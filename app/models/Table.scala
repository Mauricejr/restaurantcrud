package models
import play.api.libs.json._
import play.api.libs.functional.syntax._

case class TableSit(id: Option[Long], maxCapacity: Long, minCapacity: Long, name: String, occupied: Option[Boolean] = Some(false))

object TableSit {
  implicit val customerOder = Json.format[TableSit]

}