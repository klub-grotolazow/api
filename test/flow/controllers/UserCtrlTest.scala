package flow.controllers

import java.util.concurrent.TimeUnit

import controllers.UserCtrl
import org.json4s.JsonAST.JValue
import org.scalatest.WordSpec
import org.json4s.JsonDSL._
import org.json4s._
import org.json4s.jackson.JsonMethods._
import org.scalatestplus.play.PlaySpec
import play.api.test._
import play.api.test.Helpers._

import scala.concurrent.Await
import scala.concurrent.duration.FiniteDuration

/**
 * Created by michal on 02.01.15.
 */
class UserCtrlTest extends PlaySpec {

/*  val userData: JValue = render(
    ("firstName" -> "Ania") ~
      ("lastName" -> "Kijania") ~
      ("email" -> "ania@gmail.com") ~
      ("peselNr" -> "98010101011") ~
      ("idCardNr" -> "AJJ450504") ~
      ("feeStatus" -> "all paid") ~
      ("hoursPoints" -> 0)
  )

  val userMongoData: JValue = render(
    ("firstName" -> "Ania") ~
      ("lastName" -> "Kijania") ~
      ("email" -> "ania@gmail.com") ~
      ("peselNr" -> "98010101011") ~
      ("idCardNr" -> "AJJ450504") ~
      ("feeStatus" -> "all paid") ~
      ("hoursPoints" -> 0) ~
      ("id" -> 1)
  )*/

  val userData = parse("""{
    "firstName": "Ania",
    "lastName": "Kijania",
    "email": "ania@gmail.com",
    "peselNr": "98010101011",
    "idCardNr": "AJJ450504",
    "feeStatus": "all paid",
    "hoursPoints": "0"
  }""")

  val userMongoData = parse("""{
    "firstName": "Ania",
    "lastName": "Kijania",
    "email": "ania@gmail.com",
    "peselNr": "98010101011",
    "idCardNr": "AJJ450504",
    "feeStatus": "all paid",
    "hoursPoints": "0",
    "id": "1",
  }""")

  val ctrl = controllers.routes.UserCtrl
  val contentType = "application/json"
  //  val headers = Array("Content-Type" -> content, "Authorization" -> client.getId.ToString)

  "POST request /users" should {
    "create an user" in {
      running(FakeApplication()) {
        implicit val formats = DefaultFormats
        val request = FakeRequest(POST, ctrl.create().url, FakeHeaders(), userData)
//        val Some(result) = ctrl.create()(request)
        val result = new UserCtrl().create()(request)

        status(result) mustEqual CREATED
        parse(contentAsString(result)) mustEqual userMongoData
      }

    }
  }
}
