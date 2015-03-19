package models.binders.traits

/**
 * Trait adds query parameter ("query" -> query)
 * It is used to specify MongoDB like query for the API,
 * for example: query={_id:{$lt:4}}
 */
trait Query extends TraitBase {
  val queryName = classOf[Query].getSimpleName.toLowerCase
  
  /*override def binder(params: Map)*/
}
