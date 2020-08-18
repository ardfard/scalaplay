package play
import zio.console._

object Play extends zio.App {

  val myAppLogic = 
    for {
      _ <- putStrLn("Hello")
      muhName <- getStrLn
      _ <- putStrLn(s"gobs $muhName")
    } yield ()
  
  def run(args: List[String]) = 
    myAppLogic.exitCode

}
