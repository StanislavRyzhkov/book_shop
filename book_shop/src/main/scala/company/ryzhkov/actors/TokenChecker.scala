package company.ryzhkov.actors

import java.security.Key

import akka.actor.Actor
import akka.pattern.ask
import akka.util.Timeout
import company.ryzhkov.Context._
import company.ryzhkov.actors.KeyService.FindKey
import io.jsonwebtoken.Jwts
import slick.dbio.FailureAction

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.{Failure, Success, Try}

object TokenChecker {
  case class CheckToken(token: String)
  case class TokenIsOK(message: String)


  implicit val timeout: Timeout = 10.seconds
}

class TokenChecker extends Actor {
  import TokenChecker._

  override def receive: Receive = {
    case CheckToken(token) =>
      val futRes = tryParse(token)
      for (res <- futRes) yield {
        res match {
          case Success(v) =>
          case Failure(e: Exception) =>
        }
      }

  }

  def tryParse(token: String): Future[Try[String]] = {
    val futKey = (keyService ? FindKey).mapTo[Future[Key]].flatten
    val result = for {
      key <- futKey
    } yield Try(Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody.getSubject)
    result
  }
}
