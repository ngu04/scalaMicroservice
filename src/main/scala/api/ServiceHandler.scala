package api

import com.github.vonnagy.service.container.http.routing.Rejection.DuplicateRejection
import db.MongoPersistence
import model.{Customer}
import spray.http.StatusCodes._
import spray.httpx.marshalling.{ToResponseMarshaller, Marshaller}
import spray.routing._



/**
 * Created by ngu04 on 28/07/2015.
 */
trait ServiceHandler {

  def createCustomer[T](customer: Customer)(implicit marshaller: Marshaller[Customer]): Route = ctx => {
     //Create a specific customer
    MongoPersistence.insertInCollection("customer",customer)

//    match {
//      case Some(w) =>{
//        ctx.complete(w)(ToResponseMarshaller.fromMarshaller(Created))
//        println("***********Inside some(w)*************")
//      }
//      case None =>{
//        ctx.reject(DuplicateRejection("The widget could not be located"))
//        println("***********Inside None*************")
//
//      }
//    }

  }




}
