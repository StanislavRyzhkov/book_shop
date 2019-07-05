package company.ryzhkov

import akka.http.scaladsl.Http
import company.ryzhkov.Context._
import company.ryzhkov.db.AppDataSource

import scala.io.StdIn

object Main extends App {
  AppDataSource.init()
  val route = new RestApi(system).route
  val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)
  println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
  StdIn.readLine()
  bindingFuture.flatMap(_.unbind()).onComplete(_ => system.terminate())
}
