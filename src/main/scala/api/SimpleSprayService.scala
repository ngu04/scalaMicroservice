package api

import akka.actor.ActorSystem
import db.MongoPersistence
import model.{Json4sProtocol, Customer, Person}
import spray.http.MediaTypes._
import spray.routing.SimpleRoutingApp
import spray.httpx.SprayJsonSupport._
/**
 * Created by ngu04 on 03/08/2015.
 */
object SimpleSprayService extends App with SimpleRoutingApp {

  implicit val systemActor = ActorSystem()


  startServer(interface = "localhost", port = 8080) {

    get {
      path("person") {
        ctx => ctx.complete("Welcome to person")
      }
    } ~
      post {
        path("person" / "add") {
          import Json4sProtocol._

          entity(as[Person]) { person: Person =>
            respondWithMediaType(`application/json`) {
              MongoPersistence.insertInPersonCollection("person", person)
            }
          }
        }
      }

  }

}