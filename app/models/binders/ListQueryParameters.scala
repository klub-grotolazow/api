package models.binders

/**
 * * 
 * @param filter map containing filtered values from traits
 */
case class ListQueryParameters(filter: Map[String, Any]) {
    
}

/*object ListQueryParameters extends Paginator with Order with Query {
  implicit val queryStringBinder = getQueryStringBinder(ListQueryParameters.apply)
  
}*/


