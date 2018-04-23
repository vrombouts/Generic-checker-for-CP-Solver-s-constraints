package checker



trait Base {
  val gen: VariablesGenerator = new VariablesGenerator
  var stats: Statistics
  final val notSpecified = 0
  final val AC = 1
  final val BC = 2
  protected[this] var propagation: Int = notSpecified // 0 for simple, 1 for AC, 2 for BC

  protected[this] def apply[T](size: Int, parameter: T,
                             filtering: T => Array[Set[Int]]): Array[Set[Int]] = {
    try {
      filtering(parameter)
    }
    catch {
      case _: NoSolutionException => Array.fill(size)(Set[Int]()) // doesn't catch java.lang.StackOverflowError
      case e: Exception => println(e.getClass + " " + e)
        Array.fill(size)(Set[Int]())
    }
  }

  protected[this] def checker(solution:Array[Int]): Boolean

  def applyConstraint(variables: Array[Set[Int]]): Array[Set[Int]] = {
    propagation match {
      case `notSpecified` => applyConstraintSimple(variables)
      case `AC` => applyConstraintAC(variables)
      case `BC` => applyConstraintBC(variables)
      case _ => applyConstraintSimple(variables)
    }
  }

  protected[this] def applyConstraintSimple(variables: Array[Set[Int]]): Array[Set[Int]]

  protected[this] def applyConstraintAC(variables: Array[Set[Int]]): Array[Set[Int]]

  protected[this] def applyConstraintBC(variables: Array[Set[Int]]): Array[Set[Int]]
}