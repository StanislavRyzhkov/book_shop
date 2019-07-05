package company.ryzhkov.actors

import akka.actor.Actor

object UserService {
  case object CreateUser
}

class UserService extends Actor {
  import UserService._

  override def receive: Receive = {
    case UserService => println()
  }
}
