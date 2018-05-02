package checker

import checker.constraints.incremental.{BranchOp, Pop, Push, RestrictDomain}

import scala.collection.mutable

class ACFilteringIncremental(checker: Array[Int] => Boolean) extends FilterWithState{
  private[this] var domainsStorage: mutable.Stack[Array[Set[Int]]] = _

  override def setup(variables: Array[Set[Int]]): Array[Set[Int]] = {
    domainsStorage = mutable.Stack()
    push(applyAC(variables))
  }

  override def branchAndFilter(branching: BranchOp): Array[Set[Int]] = {
    var restrictDomain: Array[Set[Int]] = Array()
    branching match {
      case _: Push => push(branching.domains)
      case _: Pop => pop(branching.domains)
      case restriction: RestrictDomain =>
        restrictDomain = restriction.applyRestriction
        applyAC(restrictDomain)
      case _ => branching.domains
    }
  }

  def push(currentDomain: Array[Set[Int]]): Array[Set[Int]] = {
    domainsStorage.push(currentDomain)
    currentDomain
  }

  def pop(currentDomain: Array[Set[Int]]): Array[Set[Int]] = {
    if (domainsStorage.nonEmpty)
      domainsStorage.pop()
    else
      currentDomain
  }

  //filtering AC

  @throws[NoSolutionException]
  def applyAC(variables: Array[Set[Int]]): Array[Set[Int]] = {
    if (variables.isEmpty) throw NoSolutionException()
    val sols: Array[Array[Int]] = solutions(variables).filter(x => checker(x))
    if (sols.isEmpty) throw NoSolutionException()
    toDomainsAC(sols)
  }


  //Generation of all the solutions
  protected[this] def solutions(variables: Array[Set[Int]]): Array[Array[Int]] = {
    val currentSol: Array[Int] = Array.fill(variables.length)(0)
    val result: mutable.Set[Array[Int]] = mutable.Set()
    setIthVariable(variables, 0, currentSol, result)
    result.toArray
  }

  private[this] def setIthVariable(variables: Array[Set[Int]], index: Int, currentSol: Array[Int], result: mutable.Set[Array[Int]]): Unit = {
    for (i <- variables(index)) {
      currentSol(index) = i
      if (index == variables.length - 1) {
        result += currentSol.clone()
      } else {
        setIthVariable(variables, index + 1, currentSol, result)
      }
    }
  }

  //Retrieval of the domains filtered
  protected[this] def toDomainsAC(solutions: Array[Array[Int]]): Array[Set[Int]] = {
    val variables: Array[Set[Int]] = Array.fill(solutions(0).length)(Set.empty)
    solutions.foreach { sol =>
      for (i <- variables.indices) {
        variables(i) += sol(i)
      }
    }
    variables
  }

}