package company.ryzhkov.actors

import akka.actor.Actor
import akka.util.Timeout
import company.ryzhkov.db.AppDataSource.db
import company.ryzhkov.model.Models.{books, genres, origins, styles}
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.Future
import scala.concurrent.duration._

object BookService {
  case object GetBooks
  case class GetBookById(id: Long)
  case class BookInfo(id: Long, title: String, author: String, price: Int)
  case class BookFullInfo(id: Long, title: String, author: String, price: Int, vendorCode: String, genre: String,
                          style: String, origin: String)

  implicit val timeout: Timeout = 10.seconds
}

class BookService extends Actor {
  import BookService._

  implicit val ex = context.dispatcher

  override def receive: Receive = {
    case GetBooks => sender() ! findAll
    case GetBookById(id) => sender() ! finOneById(id)
  }

  private def findAll: Future[Vector[BookInfo]] = {
    val q = books.map(e => (e.id, e.title, e.author, e.price))
    db.run(q.result).map(_.toVector.map(e => BookInfo(e._1, e._2, e._3, e._4)))
  }

  private def finOneById(id: Long): Future[Option[BookFullInfo]] = {
    val q = for {
      b <- books if b.id === id
      s <- styles if b.styleId === s.id
      g <- genres if b.genreId === g.id
      o <- origins if b.originId === o.id
    } yield (b.id, b.title, b.author, b.price, b.vendorCode, g.name, s.name, o.name)
    db.run(q.result).map(_.map(e => BookFullInfo(e._1, e._2, e._3, e._4, e._5, e._6, e._7, e._8)).headOption)
  }
}
