package funsets

object Main extends App {
  import FunSets._
  println(contains(singletonSet(1), 1))
  printSet(intersect(x => x < 5 && x > 1, _ > 10))
  println(exists(_ >= 10, _ < 11))
  printSet(intersect(_ >= 10, _ < 11))
  printSet(map(x => x > 0 && x < 5, _ * 2))
}
