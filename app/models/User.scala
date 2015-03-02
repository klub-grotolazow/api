package models

import org.bson.types.ObjectId
import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._

case class Address(
                    voivodeship: Option[String],
                    town: String,
                    street: String,
                    buildingNr: Int,
                    apartmentNr: Option[Int],
                    zipCode: Option[String]   // todo add Regex
                    )

case class User(
                 _id: String,
                 firstName: String,
                 lastName: String,
                 membershipTypeTag: String,        // todo add Enum
                 courseRole: String,               // todo add Enum
                 paymentRole: String,              // todo add Enum
                 warehouseRole: String,            // todo add Enum
                 email: String,
                 peselNr: Option[String],          // todo add Regex
                 idCardNr: Option[String],         // todo add Regex
                 feeStatus: String,                // todo add Enum
                 hoursPoints: Int,
                 address: Option[Address],
                 gender: Option[String],           // todo add Enum
                 age: Option[Int],
                 phoneNr: Option[String],
                 indexNr: Option[String],          // todo add Regex       // for student
                 instructorCard: Option[String],   // todo add Regex       // for instructor
                 instructorSpecialization: Option[String], // todo add Enum// for instructor
                 
                 currentCourses_ids: List[String],
                 hiredEquipments_ids: List[String]
               )

object Address {
  implicit val addressReads: Reads[Address] = Json.reads[Address]
  
  implicit val addressWrites: Writes[Address] = Json.writes[Address]

  implicit val addressFormat: Format[Address] =
  Format(addressReads, addressWrites)
}

object User {
  implicit val userReads: Reads[User] = (
    ((__ \ "_id").read[String] orElse Reads.pure(new ObjectId().toString)) ~
    (__ \ "firstName").read[String] ~
    (__ \ "lastName").read[String] ~
    (__ \ "membershipTypeTag").read[String] ~
    (__ \ "courseRole").read[String] ~
    (__ \ "paymentRole").read[String] ~
    (__ \ "warehouseRole").read[String] ~
    (__ \ "email").read(email) ~
    (__ \ "peselNr").readNullable[String] ~
    (__ \ "idCardNr").readNullable[String] ~
    (__ \ "feeStatus").read[String] ~
    (__ \ "hoursPoints").read[Int] ~
    (__ \ "address").readNullable[Address] ~
    (__ \ "gender").readNullable[String] ~
    (__ \ "age").readNullable[Int] ~
    (__ \ "phoneNr").readNullable[String] ~
    (__ \ "indexNr").readNullable[String] ~
    (__ \ "instructorCard").readNullable[String] ~
    (__ \ "instructorSpecialization").readNullable[String] ~
    (__ \ "currentCourses_ids").read[List[String]] ~
    (__ \ "hiredEquipments_ids").read[List[String]]
  )(User.apply _)

  implicit val userWrites: Writes[User] = Json.writes[User]

  implicit val userFormat: Format[User] =
  Format(userReads, userWrites)
}