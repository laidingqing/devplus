package com.devplus.api

import akka.actor.{ActorSystem, Props}

import scala.concurrent.ExecutionContext
import spray.util.LoggingContext
import spray.routing._
import akka.util.Timeout
import com.devplus.Services
import com.devplus.actors.UserProtocol.{AddUser, GetUserInfo, User}
import akka.pattern._

import scala.concurrent.duration._

class UserRoutes(services: Services)(implicit ec: ExecutionContext, log: LoggingContext) extends ApiRoute(services){
    import com.devplus.actors.UserJsonSupport._
    implicit val timeout = Timeout(10 seconds)

    val route: Route =  {
        pathPrefix("user") {
            (get & path(Segment)) { id =>

              withService("userService"){ service =>
                  val user = (service ? GetUserInfo(id))
                  complete {
                      user.mapTo[User]
                  }
              }
            }~ (post & entity(as[String])) { userLogin =>

                withService("userService") { service =>
                    val result = (service ? AddUser(User(userLogin)))
                    complete {
                        result.mapTo[User]
                    }
                }
              }
        }

    }
}