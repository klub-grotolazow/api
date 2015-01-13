package models

import play.api.Play
import com.novus.salat.Context
import play.api.libs.json._

/**
 * Created by michal on 28.11.14.
 */

case class User(
//                 id: String,
                 firstName: String,
                 lastName: String,

                 /** add Regex */
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

/*object JsonFormats {
  import play.api.libs.json.Json

  implicit val userFormat = Json.format[User]
}*/

object User {
/*  implicit val userReads = new Reads[User] {
    def reads(js: JsValue): JsResult[User] = {
      (js \ "firstName").validate[String].flatMap { firstName =>
        (js \ "lastName").validate[String].flatMap { lastName =>
          (js \ "email").validate[String].map { email =>
            User(firstName, lastName, email)
          }
        }
      }
    }
  }*/
  import play.api.libs.functional.syntax._
  import play.api.libs.json._
  import play.api.libs.json.Reads._

  implicit val userReads: Reads[User] = (
    (__ \ "firstName").read[String] ~
    (__ \ "lastName").read[String] ~
    (__ \ "email").read(email)
  )(User.apply _)

/*  implicit val userWrites = new Writes[User] {
    def writes(u: User): JsValue = {
      Json.obj(
        "firstName" -> u.firstName,
        "lastName" -> u.lastName,
        "email" -> u.email
      )
    }
  }*/
  implicit val userWrites = (
    (__ \ "firstName").write[String] ~
    (__ \ "lastName").write[String] ~
    (__ \ "email").write[String]
  )(unlift(User.unapply))



}

/*  val collectionName = "users"
  val config = ConfigFactory.load
  val mongoDbHost = config.getString("mongodb.host")
  val mongoDbPort = config.getInt("mongodb.port")
  val dbName = config.getString("mongodb.dbname")

  val mongoClient = MongoClient(mongoDbHost, mongoDbPort)
  val db = mongoClient(dbName)
  val collection = db(collectionName)

  def create(data: JValue): DBObject = {

    collection.insert()
    val builder = MongoDBObject.newBuilder
    builder += data
//    builder += "foo" -> "bar"
//    dataMap.map((x, y) => builder += x -> y)

//    collection += MongoDBObject(user)

  }

  def pullData(data: JValue): Map[String, Any] = {
    implicit val formats = DefaultFormats

/*    val id = (data \ "id").extract[Long]
    val firstname = (data \ "firstname").extract[String]
    val lastname = (data \ "firstname").extract[String]
    val email = (data \ "email").extract[String]
    val peselNr = (data \ "peselNr").extract[String]
    val idCardNr = (data \ "idCardNr").extract[String]
    val feeStatus = (data \ "feeStatus").extract[String]
    val hoursPoints = (data \ "hoursPoints").extract[Int]

    new User(id, firstname, lastname, email, peselNr, idCardNr, feeStatus, hoursPoints)*/

    data.extract[Map[String, Any]]
  }*/
