package com.devplus.api

import spray.routing.{HttpService, RoutingSettings}
import spray.util.LoggingContext

import scala.concurrent.ExecutionContext

trait StaticRoutes extends HttpService {

  val staticRoute = pathPrefix("") {
    getFromResourceDirectory("www/")
  }

}