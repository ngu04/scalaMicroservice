package api

import org.scalatest._
import spray.http.HttpResponse
import spray.routing.{RequestContext, Directives}
import spray.testkit.ScalatestRouteTest
/**
 * Created by ngu04 on 04/08/2015.
 */
class SimpleSprayServiceTest extends FlatSpec with ShouldMatchers with ScalatestRouteTest with Directives {


  it should "the most simple and direct route test" in {
    Get() ~> {
      (_: RequestContext).complete(HttpResponse())
    } ~> (_.response) shouldEqual HttpResponse()
  }

  it should "have response with 'Welcome to person'" in {
    Get("/person") ~> SimpleSprayService.simpleRoute ~> check {
      responseAs[String] should include ("Welcome to person")
    }
  }
}
