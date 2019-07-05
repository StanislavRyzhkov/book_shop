package company.ryzhkov.actors

import akka.actor.Actor

object TokenSession {
  case class MemoryRepository(o: Map[String, Any])
}

class TokenSession extends Actor {
  override def receive = ???
}
