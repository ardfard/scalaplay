def f(total: Int, lst: List[Int]): Int = {
  if (total < 0 || lst.isEmpty) 0
  else if (total == 0) 1
  else f(total, lst.tail) + f(total - lst.head, lst)
}

f(500, List(20, 30, 40, 50, 80))
