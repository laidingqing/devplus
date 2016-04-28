package com.devplus.actors

import java.util.UUID

import com.devplus.actors.UserProtocol.{Normal, User}
import spray.httpx.SprayJsonSupport
import spray.json.DefaultJsonProtocol

/**
  * Created by skylai on 16/4/26.
  */
object UserProtocol {
  case class GetUserInfo(id: String)
  case class AddUser(user: User)

  sealed trait UserStatus

  case class Normal() extends UserStatus
  case class Locked() extends UserStatus
  case class Auditing()  extends UserStatus


  case class User(login: String, id: String = UUID.randomUUID().toString)


}

object UserJsonSupport extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val userFormats = jsonFormat2(User)
}
