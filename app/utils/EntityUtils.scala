package utils

import org.json4s.JsonAST.JValue
import org.bson.types.ObjectId
import org.json4s.JsonDSL._

/**
 * Created by michal on 28.11.14.
 */
trait EntityUtils {
  def newDocument(body: JValue): JValue = {
    val now = System.currentTimeMillis()
    val id = new ObjectId()
    body merge (("createdAt" -> now) ~ ("id" -> id.toString))
  }
}
