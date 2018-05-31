package checker

import org.assertj.core.api.{AbstractBooleanAssert, Assertions, ObjectAssert}

class FilterWithStateAssert(tested: FilterWithState)
  extends ObjectAssert[FilterWithState](tested) {


  def filterAs(trusted: FilterWithState)(implicit testArgs: TestArgs): FilterWithStateAssert = {
    implicit val stats: Statistics = new Statistics("")
    val result: Boolean = CPChecker.check(trusted, tested)
    var errMsg = "Tested and trusted filtering do not filter the same\n"
    //compute error message from statistics
    errMsg += stats.getErrorMsg
    val a: AbstractBooleanAssert[_] = Assertions.assertThat(result).overridingErrorMessage(errMsg)
    a.isTrue
    this
  }

  def weakerThan(trusted: FilterWithState)(implicit testArgs: TestArgs): FilterWithStateAssert = {
    implicit val stats: Statistics = new Statistics("")
    val result: Boolean = CPChecker.stronger(trusted, tested)
    var errMsg = "tested filtering is not weaker than the trusted one\n"
    //compute error message from statistics
    errMsg += stats.getErrorMsg
    val a: AbstractBooleanAssert[_] = Assertions.assertThat(result).overridingErrorMessage(errMsg)
    a.isTrue
    this
  }

}

object FilterWithStateAssert {
  def assertThat(tested: FilterWithState) = new FilterWithStateAssert(tested)
}