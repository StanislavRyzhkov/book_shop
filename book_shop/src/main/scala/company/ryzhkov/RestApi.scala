package company.ryzhkov

import akka.actor.ActorSystem
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import akka.util.Timeout
import ch.megard.akka.http.cors.scaladsl.CorsDirectives._
import company.ryzhkov.Context._
import company.ryzhkov.actors.BookService._
import company.ryzhkov.actors.KeyService.CreateKey
import company.ryzhkov.actors.OrderService.OrderItem
import company.ryzhkov.actors.TokenService._
import spray.json.DefaultJsonProtocol._

import scala.concurrent.Future
import scala.concurrent.duration._


class RestApi(system: ActorSystem) {
  implicit val timeout: Timeout = 5.seconds
  implicit val bookInfoFormat = jsonFormat4(BookInfo)
  implicit val bookFullInfoFormat = jsonFormat8(BookFullInfo)
  implicit val cartItemFormat = jsonFormat3(OrderItem)
  implicit val tokenFormat = jsonFormat1(Token)
  implicit val tokenMessageFormat = jsonFormat1(TokenMessage)

  val route: Route = cors() {allBooks ~ bookById ~ token}


  keyService ! CreateKey

  def allBooks = pathPrefix("books") {
    pathEndOrSingleSlash {
      get {
        val books = (bookService ? GetBooks).mapTo[Future[Seq[BookInfo]]].flatten
        complete(books)
      }
    }
  }

  def bookById = pathPrefix("books" / LongNumber) {
    id => {
      pathEndOrSingleSlash {
        get {
          val maybeBook = (bookService ? GetBookById(id)).mapTo[Future[Option[BookFullInfo]]].flatten
          onSuccess(maybeBook) {
            case Some(book) => complete(book)
            case None => complete(StatusCodes.NotFound)
          }
        }
      }
    }
  }

  def token = pathPrefix("checkToken") {
    pathEndOrSingleSlash {
      get {
        optionalHeaderValueByName("Authorization") {
          header => {
            header match {
              case Some(value: String) =>
                val msg = (tokenService ? CheckToken(value)).mapTo[Future[TokenMessage]].flatten
                complete(msg)
              case None =>
                val res = (tokenService ? CreateToken).mapTo[Future[Token]].flatten
                complete(res)
            }
          }
        }
      }
    }
  }
}
