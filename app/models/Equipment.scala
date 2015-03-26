package models

import org.bson.types.ObjectId
import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._

case class EquipmentHire(
                          _id: String,          //
                          reservationDate: Option[String],  // todo add dateFormat
                          hireDate: Option[String],         // todo add dateFormat
                          returnDate: Option[String],    // todo add dateFormat
                          conditionStatus: Option[String],  // todo add Enums

                          user_id: String,
                          warehouseman_id: String
                        )

case class Equipment(
                      _id: String,
                      serialNumber: String,
                      name: String,
                      isAvailable: Boolean,
                      isServicing: Boolean,
                      isReserved: Boolean,
                      isHired: Boolean,
                      allowedFor: List[String],               // todo add Enums
                      purchaseDate: Option[String],           // todo add dateFormat
                      nextInspectionDate: Option[String],     // todo add dateFormat
                      price: Option[Int],
                      description: Option[String],
                      hireHistory: List[EquipmentHire]
                    )

object EquipmentHire {
  implicit val equipmentHireReads: Reads[EquipmentHire] = (
  ((__ \ "_id").read[String] orElse Reads.pure(new ObjectId().toString)) ~
    (__ \ "reservationDate").readNullable[String] ~
    (__ \ "hireDate").readNullable[String] ~
    (__ \ "returnDate").readNullable[String] ~
    (__ \ "conditionStatus").readNullable[String] ~
    (__ \ "user_id").read[String] ~
    (__ \ "warehouseman_id").read[String]
  )(EquipmentHire.apply _)
  implicit val equipmentHireWrites: Writes[EquipmentHire] = Json.writes[EquipmentHire]
}

object Equipment {
  implicit val equipmentReads: Reads[Equipment] = (
    ((__ \ "_id").read[String] orElse Reads.pure(new ObjectId().toString)) ~
      (__ \ "serialNumber").read[String] ~
      (__ \ "name").read[String] ~
      (__ \ "isAvailable").read[Boolean] ~
      (__ \ "isServicing").read[Boolean] ~
      (__ \ "isReserved").read[Boolean] ~
      (__ \ "isHired").read[Boolean] ~
      (__ \ "allowedFor").read[List[String]] ~
      (__ \ "purchaseDate").readNullable[String] ~
      (__ \ "nextInspectionDate").readNullable[String] ~
      (__ \ "price").readNullable[Int] ~
      (__ \ "description").readNullable[String] ~
      (__ \ "hireHistory").read[List[EquipmentHire]]
    )(Equipment.apply _)
  implicit val equipmentWrites: Writes[Equipment] = Json.writes[Equipment]
}