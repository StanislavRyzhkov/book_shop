package company.ryzhkov

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer

object Context {
  implicit val system = ActorSystem()
  implicit val mat = ActorMaterializer()
  implicit val executionContext = system.dispatcher
}
