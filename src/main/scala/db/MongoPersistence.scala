package db

import model.{Person, Customer}
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.api.commands.WriteResult
import reactivemongo.bson.{BSONValue, BSONDocument}
import spray.httpx.marshalling.{ToResponseMarshaller, Marshaller}
import spray.routing._
import scala.concurrent.{Future}
import scala.util.Success
import spray.http.StatusCodes._
import scala.util.Failure
import spray.json._
import DefaultJsonProtocol._

/**
 * Created by ngu04 on 27/07/2015.
 */
//class MongoPersistence(configuration: Configuration) extends ActorSubscriber {
//
//  private val mongoHost = configuration.getString("mongo.host").get
//  private val mongoDb = configuration.getString("mongo.db").get
//  private val mongoUser = configuration.getString("mongo.user")
//  private val mongoPassword = configuration.getString("mongo.password").get
//  private implicit val dispatcher = context.dispatcher
//  private val mongoConnection = (new MongoDriver).connection(nodes = List(mongoHost), authentications = List(Authenticate(mongoDb, mongoUser, mongoPassword)))
//
//  private val mongoDatabase = mongoConnection(mongoDb)
//  private val metrics: BSONCollection = mongoDatabase("metrics")
//
//
//  override protected def requestStrategy: RequestStrategy = new WatermarkRequestStrategy(1024)
//
//  override def receive: Receive = {
//    case OnNext(c: model.Counter) => metrics.insert(c)
//    case OnNext(v: model.Value) => metrics.insert(v)
//    case OnComplete => self ! PoisonPill
//
//  }
//}
//
//object MongoPersistence{
//  def props(configuration: Configuration) : Props = Props(new MongoPersistence(configuration))
//}


object MongoPersistence{
  import reactivemongo.api._

  import scala.concurrent.ExecutionContext.Implicits.global

  //import scala.concurrent.ExecutionContext

  val driver = new MongoDriver
  val connection = driver.connection(List("localhost"))

  // Gets a reference to the database "plugin"
  val dataBase = connection("plugin")

  def getListWithoutRoute(collectionName : String): Future[List[Customer]] = {
    val collection :BSONCollection = dataBase(collectionName)

    val query = BSONDocument()
    val f = collection.find(query).cursor[Customer]().collect[List]()

    f
  }

  def getCustomerListWithoutRoute(collectionName : String,firstName : String ): Future[List[Customer]] = {
    val collection :BSONCollection = dataBase(collectionName)

    val query = BSONDocument("firstName" ->  firstName)

    val f = collection.find(query).cursor[Customer]().collect[List]()

    f
  }

  def insertInCollectionWithoutRoute(collectionName : String ,collectionData : Customer): Future[WriteResult] = {

    val collection :BSONCollection = dataBase(collectionName)

    val f : Future[WriteResult] = collection.insert(collectionData)

   f
  }

  def getListCollection(collectionName : String)(implicit marshaller: Marshaller[Customer]): Route = ctx =>{
    val collection :BSONCollection = dataBase(collectionName)

    val query = BSONDocument()
    val f = collection.find(query).cursor().collect[List]()

    f onComplete{
      case Success(list) =>{
        implicit def x = marshaller.asInstanceOf[Marshaller[Customer]]
        ctx.complete("{\"size\":\""+list.size+"\"}")(ToResponseMarshaller.fromMarshaller(OK))
      }

      case Failure(e) =>{
        ctx.complete(e.getMessage)(ToResponseMarshaller.fromMarshaller(InternalServerError))
      }

    }

  }



  def getCustomerCollection(collectionName : String,firstName : String )(implicit marshaller: Marshaller[Customer]): Route = ctx =>{
    val collection :BSONCollection = dataBase(collectionName)

    val query = BSONDocument("firstName" ->  firstName)

    val f = collection.find(query).cursor[Customer]().collect[List]()



      f onComplete{
      case Success(list) =>{
        implicit def x = marshaller.asInstanceOf[Marshaller[Customer]]

          ctx.complete("{" + "\"size\":\""+list.size+"\""+"customerList : [" +list +"]}")(ToResponseMarshaller.fromMarshaller(OK))
      }

      case Failure(e) =>{
        ctx.complete(e.getMessage)(ToResponseMarshaller.fromMarshaller(InternalServerError))
      }

    }

  }


  def insertInCollection(collectionName : String ,collectionData : Customer)(implicit marshaller: Marshaller[Customer]): Route = ctx => {

     val collection :BSONCollection = dataBase(collectionName)

    val f : Future[WriteResult] = collection.insert(collectionData)

    f onComplete{
         case Success(writeResult) => writeResult.hasErrors match {
           case true => {
             println("**********Inside Success Future has error true ************")
             ctx.complete("{\"status\":\""+writeResult.getMessage()+"\"}")(ToResponseMarshaller.fromMarshaller(InternalServerError))
           }

           case false => {
             println("**********Inside Success Future has error false ************")
             ctx.complete("{\"status\":\"Created\"}")(ToResponseMarshaller.fromMarshaller(Created))

           }

         }

         case Failure(e) =>{
           println("*************+Failure+************************")

           ctx.complete(e.getMessage)(ToResponseMarshaller.fromMarshaller(InternalServerError))

           println(e)
         }
     }
   }

  def insertInPersonCollection[T](collectionName : String ,collectionData : Person)(implicit marshaller: Marshaller[Person]): Route = ctx => {

    val collection :BSONCollection = dataBase(collectionName)

    val f : Future[WriteResult] = collection.insert(collectionData)

    f onComplete{
      case Success(writeResult) => writeResult.hasErrors match {
        case true => {
          println("**********Inside Success Future has error true ************")
          ctx.complete("{\"status\":\""+writeResult.getMessage()+"\"}")(ToResponseMarshaller.fromMarshaller(InternalServerError))
        }

        case false => {
          println("**********Inside Success Future has error false ************")
          ctx.complete("{\"status\":\"Created\"}")(ToResponseMarshaller.fromMarshaller(Created))

        }

      }

      case Failure(e) =>{
        println("*************+Failure+************************")

        ctx.complete(e.getMessage)(ToResponseMarshaller.fromMarshaller(InternalServerError))

        println(e)
      }
    }
  }


}
