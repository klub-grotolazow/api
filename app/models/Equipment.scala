package models

import org.bson.types.ObjectId
import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._

case class RopeParameters(
                           ropeLength: Int,
                           ropeDiameter: Int,
                           ropeType: String                 // todo add Enums
                         )

case class EquipmentHire(
                          reservationDate: String,          // todo add dateFormat
                          hireDate: Option[String],         // todo add dateFormat
                          receivingDate: Option[String],    // todo add dateFormat
                          delayedDays: Option[Int],
                          conditionStatus: Option[String],  // todo add Enums

                          user_id: String,
                          warehouseman_id: String
                        )

case class Equipment(
                      _id: String,
                      serialNumber: String,
                      name: String,
                      equipmentType: String,                  // todo add Enums
                      isAvailable: Boolean,
                      isServicing: Boolean,
                      isReserved: Boolean,
                      isHired: Boolean,
                      allowedFor: List[String],               // todo add Enums
                      producer: Option[String],
                      purchaseDate: Option[String],           // todo add dateFormat
                      nextInspectionDate: Option[String],     // todo add dateFormat
                      price: Option[Int],
                      condition: Option[String],              // todo add Enums
                      description: Option[String],
                      ropeParameters: Option[RopeParameters],
                      carabinerType: Option[String],          // todo add Enums
                      hireHistory: List[EquipmentHire]
                    )

object RopeParameters {
  implicit val ropeParametersReads: Reads[RopeParameters] = Json.reads[RopeParameters]
  implicit val ropeParametersWrites: Writes[RopeParameters] = Json.writes[RopeParameters]
}

object EquipmentHire {
  implicit val equipmentHireReads: Reads[EquipmentHire] = Json.reads[EquipmentHire]
  implicit val equipmentHireWrites: Writes[EquipmentHire] = Json.writes[EquipmentHire]
}

object Equipment {
  implicit val equipmentReads: Reads[Equipment] = (
    ((__ \ "_id").read[String] orElse Reads.pure(new ObjectId().toString)) ~
      (__ \ "serialNumber").read[String] ~
      (__ \ "name").read[String] ~
      (__ \ "equipmentType").read[String] ~
      (__ \ "isAvailable").read[Boolean] ~
      (__ \ "isServicing").read[Boolean] ~
      (__ \ "isReserved").read[Boolean] ~
      (__ \ "isHired").read[Boolean] ~
      (__ \ "allowedFor").read[List[String]] ~
      (__ \ "producer").readNullable[String] ~
      (__ \ "purchaseDate").readNullable[String] ~
      (__ \ "nextInspectionDate").readNullable[String] ~
      (__ \ "price").readNullable[Int] ~
      (__ \ "condition").readNullable[String] ~
      (__ \ "description").readNullable[String] ~
      (__ \ "ropeParameters").readNullable[RopeParameters] ~
      (__ \ "carabinerType").readNullable[String] ~
      (__ \ "hireHistory").read[List[EquipmentHire]]
    )(Equipment.apply _)
  implicit val equipmentWrites: Writes[Equipment] = Json.writes[Equipment]
}