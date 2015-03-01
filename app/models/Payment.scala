package models

import org.bson.types.ObjectId
import play.api.libs.functional.syntax._
import play.api.libs.json._

case class Payment(
                    _id: String,
                    paymentType: String,
                    amount: Int,
                    status: String,
                    dueDate: String,

                    user_id: String,
                    course_id: String,
                    accounter_id: String
                  )

object Payment {
  implicit val paymentReads: Reads[Payment] = (
    ((__ \ "_id").read[String] orElse Reads.pure(new ObjectId().toString)) ~
    (__ \ "paymentType").read[String] ~
    (__ \ "amount").read[Int] ~
    (__ \ "status").read[String] ~
    (__ \ "dueDate").read[String] ~
    (__ \ "user_id").read[String] ~
    (__ \ "course_id").read[String] ~
    (__ \ "accounter_id").read[String]
  )(Payment.apply _)

  implicit val paymentWrites: Writes[Payment] = Json.writes[Payment]

  implicit  val paymentFormat: Format[Payment] =
    Format(paymentReads, paymentWrites)
}

