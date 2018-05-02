package checker

import scala.collection.mutable

class ACFiltering(checker: Array[Int] => Boolean) extends Filter{
  override def filter(variables: Array[Set[Int]]): Array[Set[Int]] = {
    applyAC(variables)
  }


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

  // applyAC that seems to work in 8 lines! :)
  // inconvenient : generate all solutions and then filter. So, not slower but uses
  // a lot more memory
  /*def applyAC(variables : Array[Set[Int]]) : Array[Set[Int]] = {
   val solutions:Set[Array[Int]] = variables.foldLeft(Set[Array[Int]](Array()))((acc:Set[Array[Int]], x: Set[Int]) =>  {
      var set:Set[Array[Int]] = Set[Array[Int]]()
      x.foreach(elem => acc.foreach(y => set += y:+elem))
      set
    })
    val sols:Set[Array[Int]] = solutions.filter(x => checker(x))
    if(sols.isEmpty) return Array.fill(variables.length)(Set[Int]())
    toDomainsAC(sols.toArray)
  }*/

}
