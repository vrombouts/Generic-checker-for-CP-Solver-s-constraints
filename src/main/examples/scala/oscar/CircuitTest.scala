package oscar

import checker._
import checker.constraints.Constraint
import oscar.algo.Inconsistency
import oscar.cp.{CPIntVar, CPSolver}
import oscar.cp.circuit


object CircuitTest extends App {
  implicit private var solver: CPSolver = new CPSolver
  private var currentVars: Array[CPIntVar] = _

  private def circuitFiltering(vars: Array[Set[Int]]): Array[Set[Int]] = {
    solver = CPSolver()
    currentVars = vars.map(x => CPIntVar(x))
    val ad = circuit(currentVars)
    try {
      solver.post(ad)
    } catch {
      case _: Inconsistency => throw new NoSolutionException
    }
    currentVars.map(x => x.toArray.toSet)
  }

  def checker(sol: Array[Int]): Boolean = {
    val visited: Array[Boolean] = Array.fill(sol.length)(false)

    def internal(index: Int, acc: Int): Boolean = {
      if (sol(index) < 0 || sol(index) >= sol.length)
        return false
      if (visited(sol(index)))
        return false
      visited(sol(index)) = true
      if (acc == sol.length) {
        if (sol(index) == 0)
          return true
        else
          return false
      }
      internal(sol(index), acc + 1)
    }
    // change it ! GetNVar ne variarera pas qd scalacheck reduit les tests
    if (sol.length != currentVars.length)
      return true
    internal(0, 1)
  }

  val c = new Constraint
  c.gen.setRangeForAll((0, 4))
  c.gen.setDensityForAll(0.8)
  c.check(circuitFiltering, checker)
  val ci:Filter = new Filter{
    def filter(variables: Array[Set[Int]]): Array[Set[Int]] = circuitFiltering(variables)
  }
  implicit val generator: VariablesGenerator = new VariablesGenerator
  CPChecker.stronger(new ACFiltering(checker _), ci)
}