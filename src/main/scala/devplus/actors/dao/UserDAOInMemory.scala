package com.devplus.actors.dao

import akka.actor.{Actor, ActorLogging}
import com.devplus.actors.UserProtocol._

import scala.collection.mutable

/**
  * Created by skylai on 16/4/26.
  */
class UserDAOInMemory extends Actor with ActorLogging{

  val users: scala.collection.mutable.MutableList[User] = mutable.MutableList()
  def receive: Receive = {
    case AddUser(user) => {
      log.info(s"Added user $user")
      users+=user
      sender ! user
    }
    case GetUserInfo(id) => {
      sender ! users.find(_.id == id)
    }
  }

}
