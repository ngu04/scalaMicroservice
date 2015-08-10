package api

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshalling.ToResponseMarshaller
import akka.http.scaladsl.marshalling._

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.model.{HttpResponse, HttpRequest}
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.unmarshalling.Unmarshal

import akka.http.scaladsl.model.{HttpEntity, HttpResponse}
import akka.http.scaladsl.server.directives.RouteDirectives
import akka.stream.actor.ActorPublisherMessage.Request
import akka.stream.{ActorFlowMaterializer, FlowMaterializer}
import db.MongoPersistence
import model.Customer
import akka.http.scaladsl.server.Directives._
import reactivemongo.api.commands.WriteResult
import reactivemongo.bson.BSONDocument

import scala.concurrent._
import scala.util.{Failure, Success}
import akka.http.scaladsl.unmarshalling.{ Unmarshal, Unmarshaller }
import scala.concurrent.duration._
import scala.util.parsing._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport

import spray.json.DefaultJsonProtocol



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


  val routes = {

    pathPrefix("customer") {


      get {
        // GET /customer
        pathEnd {
          // Push the handling to another context so that we don't block
          complete {

              val f : Future[List[BSONDocument]] =  MongoPersistence.getListWithoutRoute("customer")

              var a : String = null

              f onComplete {
                case Success(list : List[BSONDocument]) =>{

                  println("*****************" + list.size)
                  a = "{\"size\":\""+list.size+"\"})"

                }
                case Failure(e) =>{
                 a = e.getMessage
                }
              }

            if(f.isCompleted){
               "Completed without future completed "

             }else{

               Await.result(f, 10 seconds)
               "Completed with " + a

             }


            }
          }
        } ~
        //GET /customer/{firstName}
          path(Rest)   { firstName =>
            val f:Future[List[Customer]] = MongoPersistence.getCustomerListWithoutRoute("customer",firstName)

            complete {

              var a : List[String] = null

              f onComplete {
                case Success(list : List[Customer]) =>{
                  a = list.map(c =>   "\nfirstName => " + c.firstName + " lastName => " +c.lastName )

                }
                case Failure(e) =>{
                  a = List(e.getMessage)
                }
              }

              Await.result(f, 10 seconds)

              "Following is list of customer \n" + a

            }
         }
      } ~
      post{
        import JsonProtocol._
        entity(as[Customer]) { customer : Customer =>
           val f:Future[WriteResult] = MongoPersistence.insertInCollectionWithoutRoute("customer",customer)

          var a : String =null
          complete{
              f onComplete{
                case Success(writeResult) => writeResult.hasErrors match {
                  case true => {
                    a ="{\"status\":\""+writeResult.getMessage()+"\"}"
                  }

                  case false => {
                   a ="{\"status\":\"Created\"}"

                  }

                }
                case Failure(e) =>{

                  a =e.getMessage

                  println(e)
                }
              }
            Await.result(f, 10 seconds)

            a
          }
        }
      }
  }


  Http().bindAndHandle(routes,"localhost",8080)

}