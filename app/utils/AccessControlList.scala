package utils

trait AccessControlList {

  private val Id = "[a-z0-9]*{25}".r

  private val GET = "GET"
  private val POST = "POST"
  private val PUT = "PUT"
  private val DELETE = "DELETE"

  private val SuperUser = "SuperUser"
  private val CourseMember = "CourseMember"
  private val CourseManager = "CourseManager"
  private val Instructor = "Instructor"
  private val Accounter = "Accounter"
  private val Warehouseman = "Warehouseman"
  private val Candidate = "Candidate"
  
  /** access control list is a nested map
    * key is a path
    * value is a map where
    * * key is a role 
    * * value is a list of HTTP methods which has access to that resource
    */
  private val acl: Map[String, Map[String, List[String]]] = Map(
        
    "/logout" -> Map(
      SuperUser -> List(POST),
      CourseMember -> List(POST),
      CourseManager -> List(POST),
      Instructor -> List(POST),
      Accounter -> List(POST),
      Warehouseman -> List(POST),
      Candidate -> List(POST)
    ),

    "/currentUser" -> Map(
      SuperUser -> List(GET, PUT),
      CourseMember -> List(GET, PUT),
      CourseManager -> List(GET, PUT),
      Instructor -> List(GET, PUT),
      Accounter -> List(GET, PUT),
      Warehouseman -> List(GET, PUT),
      Candidate -> List(GET, PUT)
    ),
  
    "/users" -> Map(
      SuperUser -> List(GET, POST),
      CourseMember -> List(),
      CourseManager -> List(),
      Instructor -> List(),
      Accounter -> List(),
      Warehouseman -> List(),
      Candidate -> List()
    ),
    "/users/$id" -> Map(
      SuperUser -> List(GET, PUT, DELETE),
      CourseMember -> List(),
      CourseManager -> List(),
      Instructor -> List(),
      Accounter -> List(),
      Warehouseman -> List(),
      Candidate -> List()
    ),
    "/users/$id/courses" -> Map(
      SuperUser -> List(GET),
      CourseMember -> List(),
      CourseManager -> List(),
      Instructor -> List(),
      Accounter -> List(),
      Warehouseman -> List(),
      Candidate -> List()
    ),
    "/users/$id/equipments" -> Map(
      SuperUser -> List(GET),
      CourseMember -> List(),
      CourseManager -> List(),
      Instructor -> List(),
      Accounter -> List(),
      Warehouseman -> List(),
      Candidate -> List()
    ),
    "/users/$id/payments" -> Map(
      SuperUser -> List(GET),
      CourseMember -> List(),
      CourseManager -> List(),
      Instructor -> List(),
      Accounter -> List(),
      Warehouseman -> List(),
      Candidate -> List()
    ),

    "/courses" -> Map(
      SuperUser -> List(GET, POST),
      CourseMember -> List(GET),
      CourseManager -> List(GET, POST),
      Instructor -> List(GET),
      Accounter -> List(GET),
      Warehouseman -> List(GET),
      Candidate -> List(GET)
    ),
    "/courses/$id" -> Map(
      SuperUser -> List(GET, PUT, DELETE),
      CourseMember -> List(GET),
      CourseManager -> List(GET, PUT, DELETE),
      Instructor -> List(GET),
      Accounter -> List(GET),
      Warehouseman -> List(GET),
      Candidate -> List(GET)
    ),
    "/courses/$id/meetings" -> Map(
      SuperUser -> List(GET, POST),
      CourseMember -> List(GET),
      CourseManager -> List(GET, POST),
      Instructor -> List(GET, POST),
      Accounter -> List(GET),
      Warehouseman -> List(GET),
      Candidate -> List()
    ),
    "/courses/$id/meetings/$id" -> Map(
      SuperUser -> List(GET, PUT, DELETE),
      CourseMember -> List(GET),
      CourseManager -> List(GET, PUT, DELETE),
      Instructor -> List(GET, PUT, DELETE),
      Accounter -> List(GET),
      Warehouseman -> List(GET),
      Candidate -> List()
    ),
    "/courses/$id/meetings/$id/members" -> Map(
      SuperUser -> List(GET),
      CourseMember -> List(GET),
      CourseManager -> List(GET),
      Instructor -> List(GET),
      Accounter -> List(GET),
      Warehouseman -> List(GET),
      Candidate -> List()
    ),
    "/courses/$id/meetings/$id/members/$id" -> Map(
      SuperUser -> List(PUT, DELETE),
      CourseMember -> List(),
      CourseManager -> List(PUT, DELETE),
      Instructor -> List(PUT, DELETE),
      Accounter -> List(),
      Warehouseman -> List(),
      Candidate -> List()
    ),
    "/courses/$id/meetings/$id/instructor" -> Map(
      SuperUser -> List(GET, DELETE),
      CourseMember -> List(GET),
      CourseManager -> List(GET, DELETE),
      Instructor -> List(GET),
      Accounter -> List(GET),
      Warehouseman -> List(GET),
      Candidate -> List()
    ),
    "/courses/$id/meetings/$id/instructor/$id" -> Map(
      SuperUser -> List(PUT),
      CourseMember -> List(),
      CourseManager -> List(PUT),
      Instructor -> List(),
      Accounter -> List(),
      Warehouseman -> List(),
      Candidate -> List()
    ),
    "/courses/$id/members" -> Map(
      SuperUser -> List(GET),
      CourseMember -> List(GET),
      CourseManager -> List(GET),
      Instructor -> List(GET),
      Accounter -> List(GET),
      Warehouseman -> List(GET),
      Candidate -> List()
    ),
    "/courses/$id/members/$id" -> Map(
      SuperUser -> List(PUT, DELETE),
      CourseMember -> List(),
      CourseManager -> List(PUT, DELETE),
      Instructor -> List(),
      Accounter -> List(),
      Warehouseman -> List(),
      Candidate -> List()
    ),
    "/courses/$id/gradMembers" -> Map(
      SuperUser -> List(GET),
      CourseMember -> List(GET),
      CourseManager -> List(GET),
      Instructor -> List(GET),
      Accounter -> List(GET),
      Warehouseman -> List(GET),
      Candidate -> List()
    ),
    "/courses/$id/gradMembers/$id" -> Map(
      SuperUser -> List(PUT, DELETE),
      CourseMember -> List(),
      CourseManager -> List(PUT, DELETE),
      Instructor -> List(),
      Accounter -> List(),
      Warehouseman -> List(),
      Candidate -> List()
    ),
    "/courses/$id/instructors" -> Map(
      SuperUser -> List(GET),
      CourseMember -> List(GET),
      CourseManager -> List(GET),
      Instructor -> List(GET),
      Accounter -> List(GET),
      Warehouseman -> List(GET),
      Candidate -> List()
    ),
    "/courses/$id/instructors/$id" -> Map(
      SuperUser -> List(PUT, DELETE),
      CourseMember -> List(),
      CourseManager -> List(PUT, DELETE),
      Instructor -> List(),
      Accounter -> List(),
      Warehouseman -> List(),
      Candidate -> List()
    ),
    "/courses/$id/manager" -> Map(
      SuperUser -> List(GET, DELETE),
      CourseMember -> List(GET),
      CourseManager -> List(GET, DELETE),
      Instructor -> List(GET),
      Accounter -> List(GET),
      Warehouseman -> List(GET),
      Candidate -> List()
    ),
    "/courses/$id/manager/$id" -> Map(
      SuperUser -> List(PUT),
      CourseMember -> List(),
      CourseManager -> List(PUT),
      Instructor -> List(),
      Accounter -> List(),
      Warehouseman -> List(),
      Candidate -> List()
    ),

    "/equipments" -> Map(
      SuperUser -> List(GET, POST),
      CourseMember -> List(GET),
      CourseManager -> List(GET),
      Instructor -> List(GET),
      Accounter -> List(GET),
      Warehouseman -> List(GET, POST),
      Candidate -> List(GET)
    ),
    "/equipments/$id" -> Map(
      SuperUser -> List(GET, PUT, DELETE),
      CourseMember -> List(GET),
      CourseManager -> List(GET),
      Instructor -> List(GET),
      Accounter -> List(GET),
      Warehouseman -> List(GET, PUT, DELETE),
      Candidate -> List(GET)
    ),
    "/equipments/$id/hires" -> Map(
      SuperUser -> List(GET, POST),
      CourseMember -> List(GET, POST),
      CourseManager -> List(GET, POST),
      Instructor -> List(GET, POST),
      Accounter -> List(GET, POST),
      Warehouseman -> List(GET, POST),
      Candidate -> List()
    ),
    "/equipments/$id/hires/$id" -> Map(
      SuperUser -> List(GET, PUT, DELETE),
      CourseMember -> List(GET, PUT, DELETE),
      CourseManager -> List(GET, PUT, DELETE),
      Instructor -> List(GET, PUT, DELETE),
      Accounter -> List(GET, PUT, DELETE),
      Warehouseman -> List(GET, PUT, DELETE),
      Candidate -> List()
    ),
    "/equipments/$id/hires/$id/user" -> Map(
      SuperUser -> List(GET),
      CourseMember -> List(GET),
      CourseManager -> List(GET),
      Instructor -> List(GET),
      Accounter -> List(GET),
      Warehouseman -> List(GET),
      Candidate -> List()
    ),
    "/equipments/$id/hires/$id/warehouseman" -> Map(
      SuperUser -> List(GET),
      CourseMember -> List(GET),
      CourseManager -> List(GET),
      Instructor -> List(GET),
      Accounter -> List(GET),
      Warehouseman -> List(GET),
      Candidate -> List()
    ),

    "/payments" -> Map(
      SuperUser -> List(GET, POST),
      CourseMember -> List(GET),
      CourseManager -> List(GET),
      Instructor -> List(GET),
      Accounter -> List(GET, POST),
      Warehouseman -> List(GET),
      Candidate -> List()
    ),
    "/payments/$id" -> Map(
      SuperUser -> List(GET, PUT, DELETE),
      CourseMember -> List(GET),
      CourseManager -> List(GET),
      Instructor -> List(GET),
      Accounter -> List(GET, PUT, DELETE),
      Warehouseman -> List(GET),
      Candidate -> List()
    ),
    "/payments/$id/user" -> Map(
      SuperUser -> List(GET),
      CourseMember -> List(),
      CourseManager -> List(),
      Instructor -> List(),
      Accounter -> List(GET),
      Warehouseman -> List(),
      Candidate -> List()
    ),
    "/payments/$id/course" -> Map(
      SuperUser -> List(GET),
      CourseMember -> List(),
      CourseManager -> List(),
      Instructor -> List(),
      Accounter -> List(GET),
      Warehouseman -> List(),
      Candidate -> List()
    ),
    "/payments/$id/accounter" -> Map(
      SuperUser -> List(GET),
      CourseMember -> List(),
      CourseManager -> List(),
      Instructor -> List(),
      Accounter -> List(GET),
      Warehouseman -> List(),
      Candidate -> List()
    )
  )

  private def matchesPath(aclPath: String, path: String): Boolean = {
    val splitAclPath = aclPath.drop(1).split("/")
    val splitPath = path.drop(1).split("/")

    splitAclPath.size == splitPath.size &&
      splitAclPath.zip(splitPath).count(elem =>
        elem._1 == "$id" && Id.pattern.matcher(elem._2).matches() ||
          elem._1 == elem._2
      ) == splitAclPath.size
  }

  private def onList(path: String, role: String, method: String): Boolean = {
    acl.find(resource => matchesPath(resource._1, path)).map { resource =>
      resource._2.find(roleAccess => roleAccess._1 == role).exists { roleAccess =>
        roleAccess._2.contains(method)
      }
    }.getOrElse(throw new RuntimeException("Resource " + path + " does not exist"))
  }
  
  def hasAccess(roles: List[String], path: String, method: String): Boolean = {
    roles.filter(role => onList(path, role, method)).nonEmpty
  }
}
