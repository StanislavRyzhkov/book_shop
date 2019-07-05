package company.ryzhkov.actors

import java.security.Key
import java.util.UUID

import akka.actor.Actor
import akka.pattern.ask
import akka.util.Timeout
import company.ryzhkov.Context.{keyService, userService, _}
import company.ryzhkov.actors.KeyService.FindKey
import company.ryzhkov.actors.UserService.CreateAnonymousUser
import io.jsonwebtoken.Jwts

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.{Failure, Success, Try}


object TokenService {
  case object CreateToken
  case class Token(value: String)
  case class CheckToken(tokenValue: String)
  case class TokenMessage(value: String)

  implicit val timeout: Timeout = 10.seconds

}

class TokenService extends Actor {
  import TokenService._

  override def receive: Receive = {
    case CreateToken => sender() ! createToken()
    case CheckToken(tokenValue) => sender() ! send(tokenValue)
  }

  def send(tokenValue: String): Future[TokenMessage] = {
    val futRes: Future[Try[String]] = tryParse(tokenValue)
    val rx = futRes.map(bla)
    rx.flatten
  }

  def bla(e: Try[String]): Future[TokenMessage] = {
    e match {
      case Success(_) => Future {TokenMessage("OK")}
      case Failure(_: Exception) => createToken().map(v => TokenMessage(v.value))
    }
  }

  def createToken(): Future[Token] = {
    val userUUID = UUID.randomUUID().toString
    val unit = (userService ? CreateAnonymousUser(userUUID)).mapTo[Future[Unit]].flatten
    val futKey = (keyService ? FindKey).mapTo[Future[Key]].flatten
    for {
      _ <- unit
      key <- futKey
    } yield Token(Jwts.builder().setSubject(userUUID).signWith(key).compact())
  }

  def tryParse(token: String): Future[Try[String]] = {
    val futKey = (keyService ? FindKey).mapTo[Future[Key]].flatten
    for {
      key <- futKey
    } yield Try(Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody.getSubject)
  }
}
