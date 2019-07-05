package company.ryzhkov.actors

import java.security.Key

import akka.actor.Actor
import company.ryzhkov.db.AppDataSource.db
import company.ryzhkov.model.Models.keyElements
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import javax.crypto.spec.SecretKeySpec
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.Future

object KeyService {
  case object CreateKey
  case object FindKey
}

class KeyService extends Actor {
  import KeyService._

  implicit val ex = context.dispatcher

  override def receive: Receive = {
    case CreateKey => createKey()
    case FindKey => sender() ! findKey
  }

  def createKey(): Future[Unit] = {
    val futLen = db.run(keyElements.result).map(_.length)
    val res = for (len <- futLen if len == 0) yield {
      val bytes = Keys.secretKeyFor(SignatureAlgorithm.HS256).getEncoded
      val key = for (i <- 0.until(bytes.length)) yield (bytes(i).toShort, i.toShort)
      val insertAction = DBIO.seq(keyElements.map(e => (e.element, e.elementIndex)) ++= key)
      db.run(insertAction)
    }
    res.flatten
  }

  def findKey: Future[Key] = {
    val q = keyElements.sortBy(_.elementIndex)
    val res = db.run(q.result).map(_.map(e => e._2.toByte).toArray)
    res.map(e => new SecretKeySpec(e, SignatureAlgorithm.HS256.getJcaName))
  }
}
