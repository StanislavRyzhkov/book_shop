package company.ryzhkov.actors

import akka.actor.Actor
import company.ryzhkov.db.AppDataSource.db
import company.ryzhkov.model.Models._
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.Future

object UserService {
  case class CreateAnonymousUser(userUUID: String)
}

class UserService extends Actor {
  import UserService._

  implicit val ex = context.dispatcher

  override def receive: Receive = {
    case CreateAnonymousUser(userUUID) => sender() ! createAnonymousUser(userUUID)
  }

  def createAnonymousUser(userUUID: String): Future[Unit] = {
    val insAction = DBIO.seq(users.map(e => e.userUUID) += userUUID)
    db.run(insAction)
  }
}
