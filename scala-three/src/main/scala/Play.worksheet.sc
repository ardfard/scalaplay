for 
  x <- 1 to 100
  y <- 2 to 10
yield
  x + y

type LeafElem[X] = X match
  case String => Char
  case Array[t] => LeafElem[t]
  case Iterable[t] => LeafElem[t]
  case AnyVal => X

def leafElem[X](x: X): LeafElem[X] = ???

