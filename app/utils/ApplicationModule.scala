/*
package utils

import com.softwaremill.macwire.MacwireMacros._
import controllers._

trait ApplicationModule {
//  lazy val action = wire[Secured]
  
  lazy val applicationController = wire[ApplicationCtrl]
  lazy val courseController = wire[CourseCtrl]
  lazy val equipmentController = wire[EquipmentCtrl]
  lazy val paymentController = wire[PaymentCtrl]
  lazy val userController = wire[UserCtrl]

  val layout = Layout(layoutFixedBreadcrumbs, layoutFixedSidebar)
  val locale = Locale(localeLang, localeTimeFormat, localeDateFormat)
  val systemData = SystemData(systemDataDebug, systemDataDisableLayout, systemDataEnvironment, systemDataGoogleAnalytics)
  val app = App(Mod(mod.toList), layout, locale, systemData,new ObjectId(appId))
}
*/
