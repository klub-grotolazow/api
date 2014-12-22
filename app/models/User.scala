package models

import com.mongodb.casbah.MongoClient
import com.mongodb.casbah.commons.MongoDBObject
import com.typesafe.config.ConfigFactory
import org.bson.types.ObjectId
import org.json4s.{Extraction, CustomSerializer, DefaultFormats}
import org.json4s.JsonAST.{JInt, JString, JObject, JValue}
import com.mongodb.DBObject

/**
 * Created by michal on 28.11.14.
 */

case class User(
                 id: String,
                 firstName: String,
                 lastName: String,

                 /** add Regex */
                 email: String,

                 /** add Regex */
                 peselNr: String,

                 /** add Regex */
                 idCardNr: String,

                 /** add Regex */
                 feeStatus: String,
                 hoursPoints: Int,

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
                 phoneNr: Option[String] = None
                 )

object User {

  class UserSerializer extends CustomSerializer[User](implicit format => ( {
    /** serializer */
    case body: JValue =>
      val id = (body \ "id").extractOrElse(new ObjectId().toString)
      val firstName = (body \ "firstName").extract[String]
      val lastName = (body \ "lastName").extract[String]
      val email = (body \ "email").extract[String]
      val peselNr = (body \ "peselNr").extract[String]
      val idCardNr = (body \ "idCardNr").extract[String]
      val feeStatus = (body \ "feeStatus").extract[String]
      val hoursPoints = (body \ "hoursPoints").extract[Int]

      User(id, firstName, lastName, email, peselNr, idCardNr, feeStatus, hoursPoints)
  }, {
    /** deserializer */
    case User(id, firstName, lastName, email, peselNr, idCardNr, feeStatus, hoursPoints,
    None, None, None, None, None, None, None, None, None, None) =>
      JObject(
        List(
          "id" -> JString(id),
          "firstName" -> JString(firstName),
          "lastName" -> JString(lastName),
          "email" -> JString(email),
          "peselNr" -> JString(peselNr),
          "idCardNr" -> JString(idCardNr),
          "feeStatus" -> JString(feeStatus),
          "hoursPoints" -> JInt(hoursPoints)
        )
      )
  }))

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