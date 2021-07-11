import zio.console._

object MyApp extends zio.App:
  val myAppLogic = 
    for 
      _    <- putStrLn("Hello! What is your name?")
      name <- getStrLn
      _    <- putStrLn(s"Hello, ${name}, welcome to ZIO!")
    yield ()
    
  def run(args: List[String]) = myAppLogic.exitCode
