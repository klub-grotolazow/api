package models

import play.api.libs.json.{Writes, Json, Reads}

case class DocumentTemplate(
                             _id: String,
                             name: String,
                             description: Option[String],
                             content: String
                           )

object DocumentTemplate {
  implicit val documentTemplateReads: Reads[DocumentTemplate] = Json.reads[DocumentTemplate]
  implicit val documentTemplateWrites: Writes[DocumentTemplate] = Json.writes[DocumentTemplate]
}

// todo think of serialization to blob or some bite data
