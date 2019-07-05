package company.ryzhkov

import akka.actor.{ActorSystem, Props}
import akka.stream.ActorMaterializer
import company.ryzhkov.actors.{BookService, KeyService, OrderService, TokenChecker, TokenCreator, UserService}

object Context {
  implicit val system = ActorSystem()
  implicit val mat = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  val bookService = system.actorOf(Props[BookService], "bookService")
  val tokenCreator = system.actorOf(Props[TokenCreator], name = "tokenCreator")
  val tokenChecker = system.actorOf(Props[TokenChecker], name = "tokenChecker")
  val keyService = system.actorOf(Props[KeyService], name = "keyService")
  val userService = system.actorOf(Props[UserService], name = "userService")
  val cartService = system.actorOf(Props[OrderService], name = "cartService")
}
