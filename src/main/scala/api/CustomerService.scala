package api

import akka.actor.{ActorRefFactory, ActorSystem}
import com.github.vonnagy.service.container.http.routing.RoutedEndpoints
import db.MongoPersistence
import model.Customer
import spray.http.MediaTypes._

class CustomerService(implicit val system: ActorSystem,actorRefFactory: ActorRefFactory) extends RoutedEndpoints{

  implicit val marshaller = jsonMarshaller
  implicit val unmarshaller = jsonUnmarshaller[Customer]

  val route = {
    pathPrefix("customer"){
      get {
        // GET /customer
        pathEnd {
          respondWithMediaType(`application/json`) {
            // Push the handling to another context so that we don't block
            MongoPersistence.getListCollection("customer")
          }
        } ~
        // GET /customer/{firstName}
        path(Rest){ firstName  =>
          respondWithMediaType(`application/json`){
            MongoPersistence.getCustomerCollection("customer",firstName)
          }

        }
      } ~
      post{
        entity(as[Customer]) { customer: Customer =>
          respondWithMediaType(`application/json`){
            MongoPersistence.insertInCollection("customer",customer)
          }
        }
      }
    }

    }
}


