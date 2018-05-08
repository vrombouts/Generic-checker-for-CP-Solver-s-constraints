package checker.statistics

import java.io._

import checker.TestArgs
import checker.incremental.{BranchOp, Pop, Push}

abstract class Statistics(var filename: String) {

  // stats about the number of executed tests
  private[this] var nbExecutedTests: Int = 0
  private[this] var nbNoSolutionTests: Int = 0
  private[this] var nbFailedNoSolutionTests: Int = 0

  protected[this] var filenameStats: File = new File("out/statistics/" + filename + "/statistics.txt")
  filenameStats.getParentFile.mkdirs

  protected[this] var filenamePassed: File = new File("out/statistics/" + filename + "/passedTests.txt")
  filenamePassed.getParentFile.mkdirs

  protected[this] var filenameFailed: File = new File("out/statistics/" + filename + "/failedTests.txt")
  filenameFailed.getParentFile.mkdirs

  // stats about the generator
  protected[this] var generatorUsed: TestArgs = _

  protected[this] var nbPush: Int = 0
  protected[this] var nbPop: Int = 0
  protected[this] var nbRestriction: Int = 0

  private[this] var nbTestCases: Int = 0

  def setFileName(filename: String):Unit = {
    filenameStats = new File("out/statistics/" + filename + "/statistics.txt")
    filenameStats.getParentFile.mkdirs
    filenamePassed = new File("out/statistics/" + filename + "/passedTests.txt")
    filenamePassed.getParentFile.mkdirs
    filenameFailed = new File("out/statistics/" + filename + "/failedTests.txt")
    filenameFailed.getParentFile.mkdirs
    this.filename = filename
  }

  def incNbExecutedTests(): Unit = nbExecutedTests += 1

  def incNbNoSolutionTests(): Unit = nbNoSolutionTests += 1

  def incNbFailedNoSolutionTests(): Unit = nbFailedNoSolutionTests += 1

  def getNbExecutedTests: Int = nbExecutedTests

  def getNbNoSolutionTests: Int = nbNoSolutionTests

  def getNbFailedNoSolutionTests: Int = nbFailedNoSolutionTests

  def getGenerator: TestArgs = generatorUsed

  def nbFailedTests: Int


  def setGenerator(gen: TestArgs): Unit = generatorUsed = gen

  def globalStatsToString(isInc: Boolean): String

  protected[this] var testsPassed: Array[(Array[Set[Int]], Array[Set[Int]], Array[Set[Int]])] = Array()

  protected[this] var testsFailed: Array[(Array[Set[Int]], Array[Set[Int]], Array[Set[Int]])] = Array()

  protected[this] var storedResults: Array[(BranchOp, Array[Set[Int]], Array[Set[Int]])] = Array()

  protected[this] var testsIncPassed: Array[Array[(BranchOp, Array[Set[Int]], Array[Set[Int]])]] = Array()

  protected[this] var testsIncFailed: Array[Array[(BranchOp, Array[Set[Int]], Array[Set[Int]])]] = Array()

  private[this] def updateBranching(b: List[BranchOp]): Unit = {
    if (b != null && b.nonEmpty) {
      b.head match {
        case _: Push => nbPush += 1
        case _: Pop => nbPop += 1
        case _ => nbRestriction += 1
      }
    }
  }

  def printNumber(nb: Int): String = {
    val nbOfChars: Int = nb.toString.length
    var s: String = " " + nb
    for (_ <- 1 to 10 - nbOfChars) {
      s = s + " "
    }
    s
  }


  def branchingStatsToString(): String = {
    var nbPushOp: Int = 0
    var nbPopOp: Int = 0
    var nbRestrictOp: Int = 0
    if (nbPush != 0) nbPushOp = nbPush / nbTestCases
    if (nbPop != 0) nbPopOp = nbPop / nbTestCases
    if (nbRestriction != 0) nbRestrictOp = nbRestriction / nbTestCases
    "The average number of Push operation per test is " + nbPushOp + "\n" +
      "The average number of Pop operation per test is " + nbPopOp + "\n" +
      "The average number of Restrict Domain operation per test is " + nbRestrictOp + "\n"
  }

  def print(implicit isInc: Boolean = false): Unit = {
    val prWriterStats = new PrintWriter(filenameStats)
    val prWriterPassed = new PrintWriter(filenamePassed)
    val prWriterFailed = new PrintWriter(filenameFailed)
    printStats(isInc, prWriterStats)
    prWriterStats.close()
    printTests(prWriterPassed, testsPassed)
    printTests(prWriterFailed, testsFailed, passed = false)
    printIncStats(prWriterPassed, testsIncPassed)
    printIncStats(prWriterFailed, testsIncFailed, passed = false)
    prWriterFailed.close()
    prWriterPassed.close()
  }

  private[this] def printStats(implicit isInc: Boolean = false, prWriter: PrintWriter): Unit = {
    prWriter.write(globalStatsToString(isInc))
    if (isInc)
      prWriter.write(branchingStatsToString())

    if (!(generatorUsed == null))
      prWriter.write(generatorUsed.toString)
  }

  def domainToString(dom: Set[Int]): String = {
    var result: String = "["
    var first: Boolean = true
    for (elem <- dom) {
      if (first) {
        result += elem
        first = false
      }
      else result += ", " + elem
    }
    result += "]"
    result
  }

  private[this] def extendString(s: String, l: Int): String = {
    var result: String = s
    if (s.length != l) {
      for (_ <- s.length until l)
        result += " "
    }
    result
  }

  private[this] def printTests(prWriter: PrintWriter, tests: Array[(Array[Set[Int]], Array[Set[Int]], Array[Set[Int]])], isInc: Boolean = false, passed: Boolean = true): Unit = {
    for ((init, ourDom, yourDom) <- tests) {
      var ourTitle: String = "Filtered domains "
      var yourTitle: String = "Your filtered domains "
      if (isInc) {
        ourTitle = "Correct domains "
        yourTitle = "Your domains "
      }
      var maxLength: Int = ourTitle.length
      init.foreach(t => if (t.size > maxLength) maxLength = domainToString(t).length)
      prWriter.write(extendString("Initial domains ", maxLength) + "|" + extendString(ourTitle, maxLength))
      prWriter.write("|" + yourTitle)
      prWriter.write("\n")
      for (i <- init.indices) {
        prWriter.write(extendString(domainToString(init(i)), maxLength) + "|" + extendString(domainToString(ourDom(i)), maxLength))
        if (yourDom != null && yourDom(i) != null) prWriter.write("|" + domainToString(yourDom(i)))
        else prWriter.write("|" + "null")
        prWriter.write("\n")
      }
      prWriter.write("\n")
    }
  }

  private[this] def printOnALine(domains: Array[Set[Int]]): String = {
    var s: String = "["
    for (dom <- domains) {
      s += "["
      if (dom.nonEmpty) {
        s += dom.head
        for (elem <- dom.tail) {
          s += "," + elem
        }
      }
      s += "]"
    }
    s += "]"
    s
  }

  private[this] def printIncStats(prWriter: PrintWriter, tests: Array[Array[(BranchOp, Array[Set[Int]], Array[Set[Int]])]], passed: Boolean = true): Unit = {
    //val prWriter = new PrintWriter(f)
    if (tests.isEmpty) {
      return
    }
    var lastTest: Array[Set[Int]] = null
    for (i <- tests.indices) {
      if (!tests(i).isEmpty) {
        if (!passed) prWriter.write("TEST FAILED : \n")
        else prWriter.write("TEST PASSED : \n")
        val (_, initial, _) = tests(i)(0)
        prWriter.write("Init : " + printOnALine(initial) + "\n")
        for (j <- 0 until tests(i).length - 1) {
          val (b, _, curr) = tests(i)(j)
          prWriter.write(b.toString + ": " + printOnALine(curr) + "\n")
          lastTest = curr
        }
        val (b, unk, curr) = tests(i)(tests(i).length - 1)
        if (!passed) {
          // in this case, unk refers to reducedDomains
          prWriter.write(b.toString + " failed: \n" + "After the application of " + b.toString + ": \n")
          printTests(prWriter, Array((lastTest, curr, unk)), isInc = true)
        }
        prWriter.write("------------------------------\n")
      }
    }
    if (storedResults.nonEmpty) {
      testsIncPassed = testsIncPassed :+ storedResults.clone()
      storedResults = Array()
    }
  }

  def updateStats(bugFreeReducedDomains: Array[Set[Int]], reducedDomains: Array[Set[Int]], init: Array[Set[Int]], result: Boolean): Unit

  def correctDomains(bugFreeReducedDomains: Array[Set[Int]], reducedDomains: Array[Set[Int]]): Boolean

  /*
   * returns true if the domains that have been reduced by our function are the same that the domains being reduced by the user function
   */
  def comparison(returnValues: Array[Array[Set[Int]]], b: List[BranchOp] = null): Boolean = {
    if (b != null && b.size == 1) {
      nbTestCases += 1
      testsIncPassed = testsIncPassed :+ storedResults.clone()
      storedResults = Array()
    }
    val bugFreeReducedDomains: Array[Set[Int]] = returnValues(2)
    var reducedDomains: Array[Set[Int]] = returnValues(1)
    val init: Array[Set[Int]] = returnValues(0)
    var result: Boolean = true
    if (reducedDomains == null) {
      println("You returned a null array instead of an array of filtered domains")
      result = false
    }
    else if (bugFreeReducedDomains.length != reducedDomains.length) {
      println("Incorrect output format : you don't return the correct number of domains variables")
      if (reducedDomains.length < bugFreeReducedDomains.length)
        reducedDomains = reducedDomains ++ Array.fill(bugFreeReducedDomains.length - reducedDomains.length)(null)
      result = false
    }
    else if (bugFreeReducedDomains.exists(_.isEmpty) && reducedDomains.exists(_.isEmpty)) {
      result = true
    }
    else if (bugFreeReducedDomains.forall(_.nonEmpty) && reducedDomains.exists(_.isEmpty)) {
      result = false
    }
    else {
      if (!correctDomains(bugFreeReducedDomains, reducedDomains)) {
        result = false
      }
    }
    incNbExecutedTests()
    if (bugFreeReducedDomains.exists(_.isEmpty)) {
      incNbNoSolutionTests()
      if (!result) incNbFailedNoSolutionTests()
    }
    else
      updateStats(bugFreeReducedDomains, reducedDomains, init, result)
    if (result) {
      if (b == null)
        testsPassed = testsPassed :+ (init, bugFreeReducedDomains, reducedDomains)
      else {
        storedResults = storedResults :+ (b.head, init, bugFreeReducedDomains)
      }
    }
    else {
      if (b == null)
        testsFailed = testsFailed :+ (init, bugFreeReducedDomains, reducedDomains)
      else {
        storedResults = storedResults :+ (b.head, reducedDomains, bugFreeReducedDomains)
        testsIncFailed = testsIncFailed :+ storedResults.clone()
        storedResults = Array()
      }
    }

    updateBranching(b)
    result
  }
}
