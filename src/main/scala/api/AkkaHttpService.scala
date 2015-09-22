package api

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.server.Directives._
import akka.stream.{ActorFlowMaterializer, FlowMaterializer}
import db.MongoPersistence
import model.Customer
import reactivemongo.api.commands.WriteResult
import spray.json.DefaultJsonProtocol

import scala.concurrent._
import scala.util.{Failure, Success}



/**
 * Created by ngu04 on 07/08/2015.
 */

trait Service {
  implicit val system: ActorSystem

  implicit def executor: ExecutionContext

  implicit val materializer: FlowMaterializer

}

object JsonProtocol extends DefaultJsonProtocol {
  implicit val requestFormat = jsonFormat2(Customer.apply)
}

object AkkaHttpService extends App with Service {

  override implicit val system = ActorSystem()
  override implicit val executor = system.dispatcher
  override implicit val materializer = ActorFlowMaterializer()


   def customerToString(customer : Customer) : String = {
    customer.firstName + " " + customer.lastName
  }


  val routes = {
    import JsonProtocol._

    pathPrefix("customer") {
      get {

        // GET /customer
        pathEnd {
          // Push the handling to another context so that we don't block
          complete {
            MongoPersistence.getListWithoutRoute("customer").map(_.length.toString())

            }
          }
        } ~
        //GET /customer/{firstName}
          path(Rest)   {
            firstName =>
            complete {
              val f:Future[List[Customer]] = MongoPersistence.getCustomerListWithoutRoute("customer",firstName)

              f.map(customerList => customerList.foldLeft(List[String]()) { (z, f) =>
                z :+ "firstName => " + s"${f.firstName}" + " lastName => " + s"${f.lastName}"
              })

            }
         }
      } ~
      post{
        //import JsonProtocol._
        entity(as[Customer]) { customer : Customer =>
           val f:Future[WriteResult] = MongoPersistence.insertInCollectionWithoutRoute("customer",customer)

          complete{
              f onComplete{
                case Success(writeResult) => writeResult.hasErrors match {
                  case true => {
                    println ("{\"status\":\""+writeResult.getMessage()+"\"}")
                  }

                  case false => {
                   println ("{\"status\":\"Created\"}")

                  }

                }
                case Failure(e) =>{

                  println(e)
                }
              }


            f
          }
        }
      }
  }


  Http().bindAndHandle(routes,"localhost",8080)

}