package company.ryzhkov

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import akka.util.Timeout
import ch.megard.akka.http.cors.scaladsl.CorsDirectives._
import company.ryzhkov.Context._
import company.ryzhkov.actors.BookService._
import company.ryzhkov.actors.OrderService.OrderItem
import company.ryzhkov.actors.TokenService._
import company.ryzhkov.actors.{BookService, OrderService, TokenService}
import spray.json.DefaultJsonProtocol._

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.{Failure, Success}


class RestApi(system: ActorSystem) {
  implicit val timeout: Timeout = 5.seconds
  implicit val bookInfoFormat = jsonFormat4(BookInfo)
  implicit val bookFullInfoFormat = jsonFormat8(BookFullInfo)
  implicit val cartItemFormat = jsonFormat3(OrderItem)
  implicit val tokenFormat = jsonFormat1(Token)

  val route: Route = cors() {allBooks ~ bookById ~ token ~ foo}
  val bookService = system.actorOf(Props[BookService], "bookService")
  val cartService = system.actorOf(Props[OrderService], name = "cartService")
  val tokenService = system.actorOf(Props[TokenService], name = "tokenService")

  tokenService ! UploadKey

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
              case Some(token: String) =>
                val msg = (tokenService ? CheckToken(Token(token))).mapTo[TokenMessage]
                msg onComplete {
                  case Success(v) =>
                    v match {
                      case TokenOK => complete("OK")
                      case
                    }
                  case Failure(e) => println(e)
                }
                complete("OK")
              case None =>
                val res = (tokenService ? CreateToken).mapTo[Future[Token]].flatten
                complete(res)
            }
          }
        }
      }
    }
  }

//  def addToCart = pathPrefix("cart") {
//    pathEndOrSingleSlash {
//      post {
//        entity(as[OrderItem]) {
//          cartItem => {
//            optionalHeaderValueByName("Authorization") {
//              header => {
//                header match {
//                  case Some(_: String) => complete("Hello")
//                  case None =>
//                    val res = (tokenService ? CreateToken).mapTo[Future[Token]].flatten
//                    complete(res)
//                }
//              }
//            }
//          }
//        }
//      }
//    }
//  }

  def foo = pathPrefix("bar") {
    pathEndOrSingleSlash {
      get {
//        val db = Database.forDataSource(AppDataSource.ds, Some(10))
//
//
//
//        val aaa = DBIO.seq(
//          genres.map(g => g.name) += "Лингвистика"
//        )
//        val res = db.run(aaa)


//        val q = genres
//        val action = q.result
//        val result = db.run(action)
//        result onComplete {
//          case Success(value) =>
//            value.foreach(println)
//        }



        complete("Hello")
      }
    }
  }
}
