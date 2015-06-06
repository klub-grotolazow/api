package utils

import com.softwaremill.macwire.Wired
import play.api.GlobalSettings
import com.softwaremill.macwire.MacwireMacros._
import controllers._

trait ApplicationModule {
  lazy val action = wire[AuthorizationAction]

  lazy val applicationController = wire[ApplicationCtrl]
  lazy val courseController = wire[CourseCtrl]
  lazy val equipmentController = wire[EquipmentCtrl]
  lazy val paymentController = wire[PaymentCtrl]
  lazy val userController = wire[UserCtrl]
}


object Global extends GlobalSettings {

  var instanceLookup: Wired = _
  
  override def onStart(app: play.api.Application) = {
    instanceLookup = wiredInModule(new ApplicationModule {})
  }
  
  override def getControllerInstance[A](controllerClass: Class[A]): A = {
    instanceLookup.lookupSingleOrThrow(controllerClass)
  }
}
