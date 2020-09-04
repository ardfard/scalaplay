package recfun

object RecFun extends RecFunInterface {

  def main(args: Array[String]): Unit = {
    println("Pascal's Triangle")
    for (row <- 0 to 10) {
      for (col <- 0 to row)
        print(s"${pascal(col, row)} ")
      println()
    }

    println(balance("(())".toList))
    println(balance("())".toList))
    println(balance("(()".toList))
    println(balance("()(".toList))
    println(countChange(0, List(1)))
    println(countChange(1, List(1)))
    println(countChange(2, List(1)))
    println(countChange(2, List(1, 2)))
    println(countChange(4, List(1, 2)))
    println(countChange(5, List(1, 2)))
  }

  /**
    * Exercise 1
    */
  def pascal(c: Int, r: Int): Int =
    if (c < 0 || r < 0) 0
    else if (c == 0 && r == 0) 1
    else pascal(c - 1, r - 1) + pascal(c, r - 1)

  /**
    * Exercise 2
    */
  def balance(chars: List[Char]): Boolean = {
    def go(st: Int, chars: List[Char]): Boolean =
      if (st < 0) false
      else if (chars.isEmpty && st > 0) false
      else if (chars.isEmpty) true
      else if (chars.head == '(') go(st + 1, chars.tail)
      else if (chars.head == ')') go(st - 1, chars.tail)
      else go(st, chars.tail)

    go(0, chars)
  }

  /**
    * Exercise 3
    */
  def countChange(money: Int, coins: List[Int]): Int =
    if (money < 0 || coins.isEmpty) 0
    else if (money == 0) 1
    else countChange(money, coins.tail) + countChange(money - coins.head, coins)
}
