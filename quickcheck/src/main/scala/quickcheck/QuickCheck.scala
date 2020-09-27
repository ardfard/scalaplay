package quickcheck

import org.scalacheck._
import Arbitrary._
import Gen._
import Prop._
import scala.collection.View.Empty

abstract class QuickCheckHeap extends Properties("Heap") with IntHeap {

  lazy val genHeap: Gen[H] = {
    def gen: Gen[H] =
      for {
        i <- arbitrary[Int]
        h1 = insert(i, empty)
        h2 <- oneOf(const(empty), gen, gen)
      } yield meld(h1, h2)
    oneOf(const(empty), gen, gen)
  }

  implicit lazy val arbHeap: Arbitrary[H] = Arbitrary(genHeap)

  property("min1") = forAll { a: Int =>
    val h = insert(a, empty)
    findMin(h) == a
  }
  property("gen1") = forAll { (h: H) =>
    val m = if (isEmpty(h)) 0 else findMin(h)
    findMin(insert(m, h)) == m
  }

  property("min") = forAll { (x: Int, y: Int) =>
    val h = insert(x, empty)
    val h2 = insert(y, h)
    findMin(h2) == (if (x > y) y else x)
  }

  property("min3") = forAll { (xs: Set[Int]) =>
    if (xs.isEmpty) true
    else {
      val htot = xs.foldLeft(empty) { (h, v) => insert(v, h) }
      val hpen = deleteMin(htot)
      if (isEmpty(hpen)) true else xs.min != findMin(hpen)
    }
  }

  property("empty") = forAll { (x: Int, y: Int) =>
    val h = insert(x, empty)
    val h2 = insert(y, h)
    val h3 = deleteMin(h2)
    val h4 = deleteMin(h3)

    (if (x > y) findMin(h2) == y && findMin(h3) == x
     else findMin(h2) == x && findMin(h3) == y) && isEmpty(h4)
  }

  property("sorted") = forAll { (h: H) =>
    def go(h: H, last: Int): Boolean =
      if (isEmpty(h)) true
      else {
        val cur = findMin(h)
        if (cur < last) false else go(deleteMin(h), cur)
      }

    if (isEmpty(h)) true else go(deleteMin(h), findMin(h))
  }

  property("meldmin") = forAll { (h1: H, h2: H) =>
    val min = {
      if (isEmpty(h1)) (if (isEmpty(h2)) 0 else findMin(h2))
      else if (isEmpty(h2)) findMin(h1)
      else if (findMin(h1) > findMin(h2)) findMin(h2)
      else findMin(h1)
    }
    val melded = meld(h1, h2)
    if (isEmpty(melded)) true else findMin(melded) == min
  }

}
