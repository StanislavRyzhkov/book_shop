package company.ryzhkov.actors

import java.security.Key
import java.util.UUID

import akka.actor.Actor
import company.ryzhkov.db.AppDataSource.db
import company.ryzhkov.model.Models.keyElements
import io.jsonwebtoken.security.Keys
import io.jsonwebtoken.{Jwts, SignatureAlgorithm}
import javax.crypto.spec.SecretKeySpec
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.Future
import scala.util.{Failure, Success, Try}


object TokenService {
  trait TokenMessage
  case object CreateToken
  case object UploadKey
  case class Token(token: String) extends TokenMessage
  case class CheckToken(token: Token)

  case object TokenOK extends TokenMessage
  case object TokenFailed extends TokenMessage
}

class TokenService extends Actor {
  import TokenService._

  implicit val ex = context.dispatcher

  override def receive: Receive = {
    case CreateToken => sender() ! createToken()

    case CheckToken(token) =>
      val stringToken = token.token
      val futKey = findKey()
      for (key <- futKey) yield {
        val t = Try(Jwts.parser().setSigningKey(key).parseClaimsJws(stringToken).getBody.getSubject)
        t match {
          case Success(_) => sender() ! TokenOK
          case Failure(_) => sender() ! TokenFailed
        }
      }

    case UploadKey => init()
  }

  def saveKey(): Future[Unit] = {
    val bytes = Keys.secretKeyFor(SignatureAlgorithm.HS256).getEncoded
    val key = for (i <- 0.until(bytes.length)) yield (bytes(i).toShort, i.toShort)
    val insertAction = DBIO.seq(keyElements.map(e => (e.element, e.elementIndex)) ++= key)
    db.run(insertAction)
  }

  def findKey(): Future[Key] = {
    val q = keyElements.sortBy(_.elementIndex)
    val res = db.run(q.result).map(_.map(e => e._2.toByte).toArray)
    res.map(e => new SecretKeySpec(e, SignatureAlgorithm.HS256.getJcaName))
  }

  def createToken(): Future[Token] = {
    val anonymousId = UUID.randomUUID().toString
    val keyFut = findKey()
    for (key <- keyFut) yield Token(Jwts.builder().setSubject(anonymousId).signWith(key).compact())
  }

  def init(): Unit = {
    val len = db.run(keyElements.result).map(_.length)
    len onComplete {
      case Success(value) => if (value == 0) load()
      case Failure(e) => e.printStackTrace()
    }
  }

  def load(): Unit = {
    val res = saveKey()
    res onComplete {
      case Success(_) => println("Key saved")
      case Failure(e) => e.printStackTrace()
    }
  }
}
