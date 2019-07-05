package company.ryzhkov.repo

import company.ryzhkov.db.AppDataSource.db
import company.ryzhkov.model.Models._
import company.ryzhkov.actors.{OrderRepository, OrderService}
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.Future

object OrderRepositorySlick extends OrderRepository {
  override def saveOrder(orderItem: OrderService.OrderItem): Future[Unit] = {
    val insertAction = DBIO.seq(
      orderItems += (orderItem.amount, orderItem.bookId, orderItem.userId)
    )
    db.run(insertAction)
  }
}
