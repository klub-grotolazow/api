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
                 paymentRole: String,
                 warehouseRole: String,
                 email: String,
                 peselNr: Option[String],          // todo add Regex
                 idCardNr: Option[String],         // todo add Regex
                 feeStatus: String,                // todo add Regex
                 hoursPoints: Int,
                 address: Option[Address],
                 gender: Option[String],    // todo add Enum
                 age: Option[Int],
                 phoneNr: Option[String],   // todo add Regex
                 indexNr: Option[String],          // for student
                 instructorCard: Option[String],          // for instructor
                 instructorSpecialization: Option[String],// for instructor
                 
                 currentCourses_ids: List[String],
                 loanEquipments_ids: List[String]
               )

object Address {
  implicit val addressReads: Reads[Address] = Json.reads[Address]
//    (
//    (__ \ "voivodeship").readNullable[String] ~
//    (__ \ "town").read[String] ~
//    (__ \ "street").read[String] ~
//    (__ \ "buildingNr").read[Int] ~
//    (__ \ "apartmentNr").readNullable[Int] ~
//    (__ \ "zipCode").readNullable[String]
//  )(Address.apply _)
  
  implicit val addressWrites: Writes[Address] = Json.writes[Address] 
//    (
//    (__ \ "voivodeship").writeNullable[String] ~
//    (__ \ "town").write[String] ~
//    (__ \ "street").write[String] ~
//    (__ \ "buildingNr").write[Int] ~
//    (__ \ "apartmentNr").writeNullable[Int] ~
//    (__ \ "zipCode").writeNullable[String]
//  )(unlift(Address.unapply))

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
    (__ \ "loanEquipments_ids").read[List[String]]
  )(User.apply _)

  implicit val userWrites: Writes[User] = Json.writes[User]
//    (
//    (__ \ "_id").write[String] ~
//      (__ \ "firstName").write[String] ~
//      (__ \ "lastName").write[String] ~
//      (__ \ "membershipTypeTag").write[String] ~
//      (__ \ "courseRole").write[String] ~
//      (__ \ "paymentRole").write[String] ~
//      (__ \ "warehouseRole").write[String] ~
//      (__ \ "email").write[String] ~
//      (__ \ "peselNr").writeNullable[String] ~
//      (__ \ "idCardNr").writeNullable[String] ~
//      (__ \ "feeStatus").write[String] ~
//      (__ \ "hoursPoints").write[Int] ~
//      (__ \ "address").writeNullable[Address] ~
//      (__ \ "gender").writeNullable[String] ~
//      (__ \ "age").writeNullable[Int] ~
//      (__ \ "phoneNr").writeNullable[String] ~
//      (__ \ "indexNr").writeNullable[String] ~
//      (__ \ "instructorCard").writeNullable[String] ~
//      (__ \ "instructorSpecialization").writeNullable[String] ~
//      (__ \ "currentCourses_ids").write[List[String]] ~
//      (__ \ "loanEquipments_ids").write[List[String]]
//  )(unlift(User.unapply))

  implicit val userFormat: Format[User] =
  Format(userReads, userWrites)
}