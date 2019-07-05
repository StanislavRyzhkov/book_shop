package company.ryzhkov.model

import slick.jdbc.PostgresProfile.api._

object Models {
  val books = TableQuery[Books]
  val genres = TableQuery[Genres]
  val origins = TableQuery[Origins]
  val styles = TableQuery[Styles]
  val users = TableQuery[Users]
  val orderItems = TableQuery[OrderItems]
  val keyElements = TableQuery[KeyElements]

  class Genres(tag: Tag) extends Table[(Long, String)](tag, "app_genre") {
    def id = column[Long]("id", O.PrimaryKey)
    def name = column[String]("name")
    def * = (id, name)
  }

  class Origins(tag: Tag) extends Table[(Long, String)](tag, "app_origin") {
    def id = column[Long]("id", O.PrimaryKey)
    def name = column[String]("name")
    def * = (id, name)
  }

  class Styles(tag: Tag) extends Table[(Long, String)](tag, "app_style") {
    def id = column[Long]("id", O.PrimaryKey)
    def name = column[String]("name")
    def * = (id, name)
  }

  class Books(tag: Tag) extends Table[(Long, String, String, Int, String, Long, Long, Long)](tag, "app_book") {
    def id = column[Long]("id", O.PrimaryKey)
    def title = column[String]("title")
    def author = column[String]("author")
    def price = column[Int]("price")
    def vendorCode = column[String]("vendor_code")
    def originId = column[Long]("origin_id")
    def styleId = column[Long]("style_id")
    def genreId = column[Long]("genre_id")
    def * = (id, title, author, price, vendorCode, originId, styleId, genreId)
    def origin = foreignKey("fk_book_app_origin", originId, origins)(_.id)
    def style = foreignKey("fk_book_app_style", styleId, styles)(_.id)
    def genre = foreignKey("fk_book_app_genre", genreId, genres)(_.id)
  }

  class Users(tag: Tag) extends Table[(Long, String, String, String, String)](tag, "app_user") {
    def id = column[Long]("id", O.PrimaryKey)
    def userUUID = column[String]("user_uuid")
    def username = column[String]("username")
    def email = column[String]("email")
    def passwordHash = column[String]("passwordhash")
    def role = column[String]("role")
    def address = column[String]("address")
    def phoneNumber = column[String]("phone_number")
    def * = (id, username, email, passwordHash, role)
  }

  class OrderItems(tag: Tag) extends Table[(Int, Long, Long)](tag, "app_order") {
    def amount = column[Int]("amount")
    def bookId = column[Long]("book_id")
    def userId = column[Long]("user_id")
    def * = (amount, bookId, userId)
    def book = foreignKey("fk_order_app_book", bookId, books)(_.id)
    def user = foreignKey("fk_order_app_user", userId, users)(_.id)
  }

  class KeyElements(tag: Tag) extends Table[(Long, Short, Short)](tag, "app_key_element") {
    def id = column[Long]("id", O.PrimaryKey)
    def element = column[Short]("element")
    def elementIndex = column[Short]("element_index")
    def * = (id, element, elementIndex)
  }
}
