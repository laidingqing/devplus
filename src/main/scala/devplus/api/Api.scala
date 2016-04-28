package com.devplus.api

import akka.actor.Actor.Receive
import akka.actor.{ActorLogging, ActorRef, Props}
import akka.io.IO
import com.devplus.{Core, CoreActors, Services}
import com.devplus.util.ConfigHolder
import spray.can.Http
import spray.http.HttpHeaders.{`Access-Control-Allow-Credentials`, `Access-Control-Allow-Headers`, `Access-Control-Allow-Methods`, `Access-Control-Allow-Origin`}
import spray.http.{HttpOrigin, SomeOrigins, StatusCodes}
import spray.http.HttpMethods._
import spray.httpx.SprayJsonSupport
import spray.json.DefaultJsonProtocol
import spray.routing.{Directives, HttpServiceActor, Route, RouteConcatenation}
import spray.util.LoggingContext
import spray.http._
import scala.concurrent.ExecutionContext.Implicits.global

trait CORSSupport extends Directives {
  private val CORSHeaders = List(
    `Access-Control-Allow-Methods`(GET, POST, PUT, DELETE, OPTIONS),
    `Access-Control-Allow-Headers`("Origin, X-Requested-With, Content-Type, Accept, Accept-Encoding, Accept-Language, Host, Referer, User-Agent"),
    `Access-Control-Allow-Credentials`(true)
  )

  def respondWithCORS(origin: String)(routes: => Route) = {
    val originHeader = `Access-Control-Allow-Origin`(SomeOrigins(Seq(HttpOrigin(origin))))

    respondWithHeaders(originHeader :: CORSHeaders) {
      routes ~ options { complete(StatusCodes.OK) }
    }
  }
}

trait Api extends Directives with RouteConcatenation with CORSSupport with ConfigHolder {
  this: CoreActors with Core =>

  val routes =
    respondWithCORS(config.getString("origin.domain")) {
      respondWithMediaType(MediaTypes.`application/json`) {
        pathPrefix("api") {
          new UserRoutes(services).route ~
            new ResumeRoutes(services).route
        }
      }
    }

  val rootService = system.actorOf(ApiService.props(config.getString("hostname"), config.getInt("port"), routes))
}

object ApiService {
  def props(hostname: String, port: Int, routes: Route) = Props(classOf[ApiService], hostname, port, routes)
}

class ApiService(hostname: String, port: Int, route: Route) extends HttpServiceActor with ActorLogging {
  IO(Http)(context.system) ! Http.Bind(self, hostname, port)

  def receive: Receive = runRoute(route)
}

object ApiRoute {
  case class Message(message: String)

  object ApiRouteProtocol extends DefaultJsonProtocol {
    implicit val messageFormat = jsonFormat1(Message)
  }

  object ApiMessages {
    val UnknownException = "Unknown exception"
    val UnsupportedService = "Sorry, provided service is not supported."
  }
}

abstract class ApiRoute(services: Services = Services.empty)(implicit log: LoggingContext) extends Directives with SprayJsonSupport {

  import com.devplus.api.ApiRoute.{ApiMessages, Message}
  import com.devplus.api.ApiRoute.ApiRouteProtocol._

  def withService(id: String)(action: ActorRef => Route) = {
    services.get(id) match {
      case Some(provider) =>
        action(provider)

      case None =>
        log.error(s"Unsupported service: $id")
        complete(StatusCodes.BadRequest, Message(ApiMessages.UnsupportedService))
    }
  }
}