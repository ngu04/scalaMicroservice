package model

import org.json4s.{DefaultFormats, Formats}
import reactivemongo.bson.{BSONDocument, BSONDocumentReader, BSONDocumentWriter}
import spray.httpx.Json4sSupport


case class Person(firstName: String, age: Int)

//object PersonJsonProtocol extends DefaultJsonProtocol {
//  implicit val PersonFormat = jsonFormat3(Person)
//}

object Json4sProtocol extends Json4sSupport {
  implicit def json4sFormats: Formats = DefaultFormats
}

object Person {

  implicit object PersonWriter extends BSONDocumentWriter[Person] {
    def write(person: Person): BSONDocument = BSONDocument(
      "firstName" -> person.firstName,
      "age" -> person.age)
  }

  implicit object PersonReader extends BSONDocumentReader[Person] {
    def read(doc: BSONDocument): Person = {
      val firstName = doc.getAs[String]("firstName").get
      val age = doc.getAs[Int]("age").get

      Person(firstName, age)
    }
  }

}