package models

import play.api.libs.json._

case class UserAccount(
                        _id: String,
                        login: String,
                        password_hash: String,
                        membershipTypeTag: String,
                        courseRole: String,                 
                        paymentRole: String,

                        user_id: String
                      )

object UserAccount {
  implicit val userAccountReads: Reads[UserAccount] = Json.reads[UserAccount]

  implicit val userAccountWrites: Writes[UserAccount] = Json.writes[UserAccount]
}