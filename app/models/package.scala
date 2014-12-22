import models.User.UserSerializer
import org.json4s.DefaultFormats

/**
 * Created by michal on 17.12.14.
 */
package object models {

  /**
   * Object that should be used to contain all needed implicits
   */
  object Implicits {
    /**
     * All json4s formats just 'import models.Implicits.formats' to use
     */
    implicit val formats = DefaultFormats +
      new UserSerializer
  }
}
