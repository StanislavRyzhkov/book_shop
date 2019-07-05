package company.ryzhkov.actors

import java.security.Key
import java.util.UUID

import akka.actor.{Actor, Props}
import akka.pattern.ask
import akka.util.Timeout
import company.ryzhkov.actors.UserService.{AnonymousUser, CreateAnonymousUser}
import company.ryzhkov.db.AppDataSource.db
import company.ryzhkov.model.Models.keyElements
import io.jsonwebtoken.security.Keys
import io.jsonwebtoken.{Jwts, SignatureAlgorithm}
import javax.crypto.spec.SecretKeySpec
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.{Failure, Success, Try}


object TokenService {
  trait TokenMessage
  case object UploadKey
  case class Token(value: String) extends TokenMessage
  case class CheckToken(token: Token)

  case object TokenOK extends TokenMessage
  case object TokenFailed extends TokenMessage

  implicit val timeout: Timeout = 5.seconds

  def props: Props = Props[UserService]
}

class TokenService {
  import TokenService._

  implicit val ex = context.dispatcher
  val userService = context.actorOf(props, "userService")

  override def receive = {
    case CreateToken =>
      val userUUID = UUID.randomUUID().toString
      val res = (userService ? CreateAnonymousUser(AnonymousUser(userUUID))).mapTo[Unit]
      res onComplete {
        case Success(_) => sender() ! createToken(userUUID)
        case Failure(e) => println(e.getMessage)
      }

    case CheckToken(token) =>
      val stringToken = token.value
      val futKey = findKey()
      for (key <- futKey) yield {
        val t = Try(Jwts.parser().setSigningKey(key).parseClaimsJws(stringToken).getBody.getSubject)
        t match {
          case Success(_) => sender() ! TokenOK
          case Failure(_) =>
            val userUUID = UUID.randomUUID().toString
            val res = (userService ? CreateAnonymousUser(AnonymousUser(userUUID))).mapTo[Unit]
            sender() ! TokenFailed
        }
      }

    case UploadKey => init()
  }






}
