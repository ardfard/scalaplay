import quickcheck._

2 + 2

object Test extends IntHeap with Bogus4BinomialHeap {
  def main = {
    val h = insert(10, empty)
    val h1 = insert(9, h)
    val h2 = insert(8, h1)
    val h3 = insert(90, h2)
    val h4 = insert(1, h3)
    val h5 = insert(-1, h4)
    val h6 = insert(-1, h5)
    val h7 = insert(100, h5)
    deleteMin(h7)
    findMin(h7) == findMin(deleteMin(h7))
  }
}

Test.main
