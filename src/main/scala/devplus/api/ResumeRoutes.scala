package com.devplus.api

import com.devplus._
import spray.routing._
import spray.util.LoggingContext
import akka.util.Timeout
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext

class ResumeRoutes(services: Services)(implicit ec: ExecutionContext, log: LoggingContext) extends ApiRoute(services) {

  implicit val timeout = Timeout(10 seconds)

  val route: Route =
    path("example2") {
      get {
        complete("Done!")
      }
    }

}