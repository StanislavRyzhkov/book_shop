package company.ryzhkov.actors

import java.security.Key
import java.util.UUID

import akka.actor.Actor
import akka.pattern.ask
import akka.util.Timeout
import company.ryzhkov.Context._
import company.ryzhkov.actors.KeyService.FindKey
import company.ryzhkov.actors.TokenService.Token
import company.ryzhkov.actors.UserService.CreateAnonymousUser
import io.jsonwebtoken.Jwts

import scala.concurrent.Future
import scala.concurrent.duration._


object TokenCreator {
  case object CreateToken

  implicit val timeout: Timeout = 10.seconds
}

class TokenCreator extends Actor {
  import TokenCreator._

  override def receive: Receive = {
    case CreateToken =>
      val userUUID = UUID.randomUUID().toString
      val unit = (userService ? CreateAnonymousUser(userUUID)).mapTo[Future[Unit]].flatten
      val futKey = (keyService ? FindKey).mapTo[Future[Key]].flatten
      val futToken = for {
        _ <- unit
        key <- futKey
      } yield Token(Jwts.builder().setSubject(userUUID).signWith(key).compact())
      sender() ! futToken
  }
}
