package com.devplus.actors

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import akka.util.Timeout
import com.devplus.actors.UserProtocol.{AddUser, GetUserInfo, User}
import com.typesafe.config.ConfigFactory
import com.devplus.actors.dao.{UserDAOInMemory, UserDAOPersistent}
import akka.pattern._

import scala.concurrent.duration._

object UserActor {
  def props(property: String) = Props(classOf[UserActor], property)
}

class UserActor(property: String) extends Actor with ActorLogging{
  implicit lazy val timeout = Timeout(10 seconds)
  implicit lazy val system = ActorSystem("UserActor")
  implicit lazy val executor = system.dispatcher

  val config = ConfigFactory.load()

  def  receive = {
    case GetUserInfo(id) => (userDAOActor ? GetUserInfo(id)).pipeTo(sender)
    case AddUser(user) => {
      (userDAOActor ? AddUser(user)).pipeTo(sender)
    }
    case _ => print("error")
  }

  val userDAOActor: ActorRef = {
    if(config.getBoolean("application.inMemory")) {
      context.actorOf(Props[UserDAOInMemory])
    }
    else {
      context.actorOf(Props[UserDAOPersistent])
    }
  }
}
