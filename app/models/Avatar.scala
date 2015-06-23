package models

import java.io.FileInputStream

import com.mongodb.casbah.MongoClient
import com.mongodb.casbah.gridfs.Imports._
import utils.MongoDatabase

case class Avatar(
                   file: FileInputStream
                 )

object Avatar {
  // todo connect with existing database connection
  // extends MongoDatabase[Avatar] {
  
  val mongoClient = MongoClient()("test")
  
  // pass the connection to the GridFS class
  val gridfs: GridFS = GridFS(mongoClient)

  def save(fileName: String) = {
    val avatar = new FileInputStream(fileName)
    val id = gridfs(avatar) { image =>
      image.filename = "fileName"
      image.contentType = "image/png"
    }
  }
  
  def get(fileName: String) = {
    val myFile = gridfs.findOne(fileName)
  }
  
  
  
}