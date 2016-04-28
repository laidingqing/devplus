package com.devplus.actors

import akka.actor.{Actor, ActorLogging, Props}

object MainServiceActor {
  case class MainMessage(message: String)
  def props(property: String) = Props(classOf[MainServiceActor], property)
}

class MainServiceActor(property: String) extends Actor with ActorLogging {
  import MainServiceActor._

  def receive = {
    case MainMessage(message) => {
      log.info(s"Example $message with property $property!")

      sender() ! message
    }
  }
}
