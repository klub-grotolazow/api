package models

import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.api.libs.json.Reads._

case class User(
//                 id: String,
                 firstName: String,
                 lastName: String,
                 email: String
//                 ,
                 /** add Regex */
//                 peselNr: String,
                 /** add Regex */
//                 idCardNr: String,
                 /** add Regex */
//                 feeStatus: String,
//                 hoursPoints: Int
/*,
                 /** add type address */
                 voivodeship: Option[String] = None,
                 town: Option[String] = None,
                 street: Option[String] = None,
                 buildingNr: Option[Int] = None,
                 apartmentNr: Option[Int] = None,
                 zipCode: Option[String] = None,
                 /** add Regex */
                 gender: Option[String] = None,
                 age: Option[String] = None,
                 /** add Regex */
                 indexNr: Option[String] = None,
                 /** add Regex */
                 phoneNr: Option[String] = None*/
                 )

object User {
  implicit val userReads: Reads[User] = (
    (__ \ "firstName").read[String] ~
    (__ \ "lastName").read[String] ~
    (__ \ "email").read(email)
  )(User.apply _)

  implicit val userWrites: Writes[User] = (
    (__ \ "firstName").write[String] ~
    (__ \ "lastName").write[String] ~
    (__ \ "email").write[String]
  )(unlift(User.unapply))

  implicit val userFormat: Format[User] =
  Format(userReads, userWrites)
}