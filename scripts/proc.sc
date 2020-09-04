import $ivy.`org.scalikejdbc::scalikejdbc:3.5.0`
import $ivy.`org.postgresql:postgresql:9.4.1212`
import scala.io.Source
import scalikejdbc._

val settings = ConnectionPoolSettings(
    maxSize = 10,
    initialSize= 1,
    connectionTimeoutMillis = 3000L,
    driverName = "org.postgresql.Driver"
  )

ConnectionPool.singleton("jdbc:postgresql://localhost/accounts_development","ardfard", "", settings)

implicit val session = AutoSession

for((line,idx) <- os.read.lines.stream(os.pwd/"test.csv").zipWithIndex) {
  val (id::date::_) = line.split(",").toList
  if (idx != 0) {
      sql"insert into breached_users values (${id.toInt}, now(), now())".update.apply()
  } 
}
