package company.ryzhkov.db

import java.sql.Connection

import org.apache.commons.dbcp2.BasicDataSource
import slick.jdbc.PostgresProfile
import slick.jdbc.PostgresProfile.api._

object AppDataSource {
  private val ds = new BasicDataSource()
  var db: PostgresProfile.backend.DatabaseDef = _

  def init(): Unit = {
    ds.setUrl("jdbc:postgresql://localhost:5432/book_shop?allowPublicKeyRetrieval=true&useSSL=false&useUnicode=true&" +
      "characterEncoding=UTF-8&serverTimezone=UTC")
    ds.setUsername("clyde")
    ds.setPassword("password")
    ds.setMinIdle(5)
    ds.setMaxIdle(10)
    ds.setMaxOpenPreparedStatements(100)
    ds.setDriverClassName("org.postgresql.Driver")
    db = Database.forDataSource(ds, Some(10))
  }

  def connection: Connection = ds.getConnection()
}
