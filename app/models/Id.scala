package models

import play.api.libs.json.{Json, Reads, Writes}

case class Id(id: String)

object Id {
  implicit val idReads: Reads[Id] = Json.reads[Id]
  implicit val idWrites: Writes[Id] = Json.writes[Id]
}
