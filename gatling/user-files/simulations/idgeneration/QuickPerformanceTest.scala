package idgeneration

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class QuickPerformanceTest extends Simulation {

   /* Load test configuration: */
   /* ------------------------------------------------ */
   val baseUrl = System.getProperty("url")
   val apiUserCount = Integer.getInteger("users", 1) // could override with JAVA_OPTS="-Dusers=5"" bin/gatling.sh
   val apiUserStartId = 0
   val rampTime = java.lang.Long.getLong("ramp", 1L)  // seconds, default is 1 sec, could override with -Dramp=3600
   val testDuration = 0  // seconds
   /* ------------------------------------------------ */

   val apiUserFeeder = Iterator.from(apiUserStartId).map(id => Map("username" -> ("gatling_user" + id + "@shutterfly.com"), "password" -> ("password")))	  
 
   val apiUsers = scenario("apiUsers")
      .feed(apiUserFeeder)
      .repeat(2) {
         POC.touchHome
         .pause(500 millis)
      }

   object POC {

      val touchHome = exec(http("POC.touchHome")
         .get("/")
//         .post("/") //api interface should be posted here
//         .body(StringBody(Body.loginBodyPOC))
         .check(status.is(session => 200)))
   }

   object Base {
      val httpProtocol = http
         .baseURL(baseUrl)
         .inferHtmlResources(BlackList( """.*.css*""", """.*.php*""", """.*.js*""", """.*.ico""", """.*.png""", """.*.gif""", """.*.jpg"""), WhiteList())
         .acceptHeader("application/json")
         .acceptEncodingHeader("gzip, deflate")
         .acceptLanguageHeader("en-US,bg;q=0.8")
         .contentTypeHeader("application/json")
         .doNotTrackHeader("1")
         .userAgentHeader("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.81 Safari/537.36")
   }

   object Body {
      val loginBodyPOC = """{"username":"${username}","password":"${password}"}"""
   }

   /* ------------ setUp the load profile------------------------ */
   setUp(
      apiUsers.inject(rampUsers(apiUserCount) over (rampTime seconds))
      ).protocols(Base.httpProtocol)
}
