package checker.statistics

class StrongerStatistics(filename: String) extends Statistics("stronger/" + filename) {

  def this() = this("")

  private[this] var nbCorrectTestsWithSolution: Int = 0
  private[this] var canBeMoreFiltered: Int = 0
  private[this] var canBeMoreFilteredAndHasNoSol: Int = 0
  private[this] var canBeMoreFilteredAndIsSol: Int = 0

  override def setFileName(filename: String): Unit = super.setFileName("stronger/" + filename)

  def getNbCorrectTestsWithSolution: Int = nbCorrectTestsWithSolution

  def getCanBeMoreFiltered: Int = canBeMoreFiltered

  def getCanBeMoreFilteredAndHasNoSol: Int = canBeMoreFilteredAndHasNoSol

  def getCanBeMoreFilteredAndIsSol: Int = canBeMoreFilteredAndIsSol

  /*
  def globStatsToString(): String = {
    ""
  }
  */

  def globalStatsToString(isInc: Boolean): String = {
    var str = "Depending on the constraint being tested, two kinds of tests are possible : \n Tests having no solution. \n Tests having solutions. \n"
    if (isInc) str += "Note that since we make " + " branchings per test, the total number of tests will be >= " + generatorUsed.getNbTests + "\n"
    str + "Here are some stats of the tests being executed : \n\n" +
      "------------------------------------------------------------ \n" +
      "Comparisons            |   Passed  |   Failed  |   Total   | \n" +
      "-----------------------|-----------|-----------|-----------| \n" +
      "Without solution       |" + printNumber(getNbNoSolutionTests - getNbFailedNoSolutionTests) + "|" + printNumber(getNbFailedNoSolutionTests) + "|" + printNumber(getNbNoSolutionTests) + "| \n" +
      "With solution          |" + printNumber(nbCorrectTestsWithSolution) + "|" + printNumber(getNbExecutedTests - (getNbNoSolutionTests + nbCorrectTestsWithSolution)) + "|" + printNumber(getNbExecutedTests - getNbNoSolutionTests) + "| \n" +
      "Count                  |" + printNumber(getNbExecutedTests - nbFailedTests) + "|" + printNumber(nbFailedTests) + "|" + printNumber(getNbExecutedTests) + "| \n" +
      "------------------------------------------------------------ \n" +
      "Number of tests that can be more filtered : " + canBeMoreFiltered + " (among them, " + canBeMoreFilteredAndHasNoSol + " have no solution and " + canBeMoreFilteredAndIsSol + " are reduced to a single solution) \n"
  }

  def nbFailedTests: Int = getNbExecutedTests - getNbNoSolutionTests - nbCorrectTestsWithSolution + getNbFailedNoSolutionTests


  def strictDomainComparison(ourReducedDomains: Array[Set[Int]], reducedDomains: Array[Set[Int]], init: Array[Set[Int]], result: Boolean): Unit = {
    if ((ourReducedDomains zip reducedDomains).forall(x => x._1.subsetOf(x._2))) {
      nbCorrectTestsWithSolution += 1
      if ((ourReducedDomains zip reducedDomains).exists(x => !x._1.equals(x._2))) {
        canBeMoreFiltered += 1
        if (ourReducedDomains.exists(x => x.isEmpty))
          canBeMoreFilteredAndHasNoSol += 1
        if (ourReducedDomains.forall(x => x.size == 1))
          canBeMoreFilteredAndIsSol += 1
      }
    }
  }

  def correctDomains(solutionDoms: Array[Set[Int]], userReducedDomains: Array[Set[Int]]): Boolean = {
    if (solutionDoms.exists(x => x.isEmpty)) {
      // check that if no solution can be found, either you still have unfixed variables
      // or if all variables are instantiated, you should find there is no solution
      !userReducedDomains.forall(x => x.size == 1)
    }
    else {
      !(solutionDoms zip userReducedDomains).exists(x => !x._1.subsetOf(x._2))
    }
  }
}