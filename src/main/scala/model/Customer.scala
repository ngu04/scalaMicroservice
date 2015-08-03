package model

import reactivemongo.bson.{BSONDocumentReader, BSONDocument, BSONDocumentWriter}
import spray.json.{DefaultJsonProtocol, JsValue, RootJsonFormat,_}


case class Customer (val firstName : String, val lastName: String)

object Customer extends DefaultJsonProtocol{

  implicit object CustomerWriter extends BSONDocumentWriter[Customer] {
    def write(customer: Customer): BSONDocument =  BSONDocument(
          "firstName" -> customer.firstName,
          "lastName" -> customer.lastName)
      }

  implicit object CustomerReader extends BSONDocumentReader[Customer] {
    def read(doc: BSONDocument): Customer = {
      val firstName = doc.getAs[String]("firstName").get
      val lastName = doc.getAs[String]("lastName").get
      //val age = doc.getAs[Int]("age").get

      Customer(firstName, lastName)
    }
  }



//  implicit val format = new RootJsonFormat[Customer] {
//
//    override def write(customer: Customer): JsValue = {
//         customer.toJson
//      }
//
//    override def read(json: JsValue): Customer = {
//      json.convertTo[Customer]
//    }
//  }

}







