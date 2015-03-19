package models.binders.traits

import com.novus.salat.dao.ValidationError
import play.api.libs.json.Json._

trait TraitBase {

  /**
   * Get the right map through filter
   * @param params map of all parameters in query string
   * @return filter or throw error
   */
/*  def binder(params: Map[String, Seq[String]]): Map[String, Any] = {
    
    /** check if length of the query is not too long */
    val all = params.foldLeft(0)((sum, el) => sum + el._1.length + el._2.foldLeft(0)((sum, el) => sum + el.length))
    
    if (all > 1000) {
      throw ValidationError(Some(parse("""{"error":"Query too long"}""")))
      
    }
  }*/
}
