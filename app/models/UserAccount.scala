package models

import play.api.libs.json._

case class UserAccount(
                        _id: String,
                        login: String,
                        password_hash: String,
                        membershipTypeTag: String,        // todo add Enum
                        courseRole: String,               // todo add Enum
                        paymentRole: String,              // todo add Enum
                        warehouseRole: String,            // todo add Enum

                        user_id: String
                      )

object UserAccount {
  implicit val userAccountReads: Reads[UserAccount] = Json.reads[UserAccount]

  implicit val userAccountWrites: Writes[UserAccount] = Json.writes[UserAccount]
}