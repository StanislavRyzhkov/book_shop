package company.ryzhkov.actors

import akka.actor.Actor
import company.ryzhkov.actors.OrderService._

import scala.concurrent.Future

trait OrderRepository {
  def saveOrder(orderItem: OrderItem): Future[Unit]
}

object OrderService {
  case class AddToCart(order: OrderItem, token: String)
  case class OrderItem(userId: Long, bookId: Long, amount: Int)
}

class OrderService extends Actor {
  override def receive: Receive = {
    case AddToCart(orderItem, _) =>
      println(orderItem.amount)
  }
}
