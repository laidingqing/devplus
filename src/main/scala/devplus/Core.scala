package com.devplus

import akka.actor.ActorSystem
import com.devplus.actors.MainServiceActor
import com.devplus.actors.UserActor
import com.devplus.util.ConfigHolder


trait Core{
    implicit def system: ActorSystem  

}

trait BootedCore extends Core{
    implicit lazy val system = ActorSystem("devplus-microservice")
}

trait CoreActors extends ConfigHolder {
  this: Core =>

  val mainService = system.actorOf(MainServiceActor.props("test property"), "MainService")
  val userService = system.actorOf(UserActor.props("test property"), "UserService")

  val services: Services = Map(
    "main" -> mainService,
    "userService" -> userService
  )
}