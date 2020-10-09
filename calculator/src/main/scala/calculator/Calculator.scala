package calculator

sealed abstract class Expr
final case class Literal(v: Double) extends Expr
final case class Ref(name: String) extends Expr
final case class Plus(a: Expr, b: Expr) extends Expr
final case class Minus(a: Expr, b: Expr) extends Expr
final case class Times(a: Expr, b: Expr) extends Expr
final case class Divide(a: Expr, b: Expr) extends Expr

object Calculator extends CalculatorInterface {
  def computeValues(
      namedExpressions: Map[String, Signal[Expr]]
  ): Map[String, Signal[Double]] = {
    namedExpressions.foldLeft(Map.empty[String, Signal[Double]]) {
      case (m, (k, s)) => m.updated(k, Var(eval(s(), namedExpressions)))
    }
  }

  def checkCyclic(
      name: String,
      expr: Expr,
      ref: Map[String, Signal[Expr]]
  ): Boolean =
    expr match {
      case Literal(v)  => false
      case Plus(a, b)  => checkCyclic(name, a, ref) || checkCyclic(name, b, ref)
      case Minus(a, b) => checkCyclic(name, a, ref) || checkCyclic(name, b, ref)
      case Times(a, b) => checkCyclic(name, a, ref) || checkCyclic(name, b, ref)
      case Divide(a, b) =>
        checkCyclic(name, a, ref) || checkCyclic(name, b, ref)
      case Ref(name2) =>
        if (name == name2) true
        else checkCyclic(name, getReferenceExpr(name2, ref), ref)
    }

  def eval(expr: Expr, references: Map[String, Signal[Expr]]): Double = {
    expr match {
      case Literal(v) => v
      case Ref(name) =>
        if (checkCyclic(name, getReferenceExpr(name, references), references))
          Double.NaN
        else eval(getReferenceExpr(name, references), references)
      case Plus(a, b)   => eval(a, references) + eval(b, references)
      case Minus(a, b)  => eval(a, references) - eval(b, references)
      case Times(a, b)  => eval(a, references) * eval(b, references)
      case Divide(a, b) => eval(a, references) / eval(b, references)
    }
  }

  /** Get the Expr for a referenced variables.
    *  If the variable is not known, returns a literal NaN.
    */
  private def getReferenceExpr(
      name: String,
      references: Map[String, Signal[Expr]]
  ) = {
    references
      .get(name)
      .fold[Expr] {
        Literal(Double.NaN)
      } { exprSignal =>
        exprSignal()
      }
  }
}
