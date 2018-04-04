package unit

import checker.constraints.Checker
import checker.constraints.incremental.{BranchOp, RestrictDomain}

class CheckConstraintBranchTests extends UnitSpec {

  private[this] object DummyCheck extends Checker {
    def applyConstraint(variable: Array[Set[Int]]): Array[Set[Int]] = variable
  }

  DummyCheck.gen.setSeed(100)

  "checkConstraint with an empty domain or empty" should "return false if init return non-empty domains" in {
    assert(!DummyCheck.checkConstraint(Array(), _ => Array(Set(1)), _ => Array()))
    assert(!DummyCheck.checkConstraint(Array(Set()), _ => Array(Set(1)), _ => Array()))
  }

  "checkConstraint with an empty domain or empty" should "return true if init return empty or empty domains" in {
    assert(DummyCheck.checkConstraint(Array(), _ => Array(), _ => Array()))
    assert(DummyCheck.checkConstraint(Array(Set()), _ => Array(Set()), _ => Array()))
  }

  "CheckConstraint with init returning an array of different size" should "be false" in {
    assert(!DummyCheck.checkConstraint(Array(Set(5)), _ => Array(Set(5), Set(5)), _ => Array()))
    assert(!DummyCheck.checkConstraint(Array(Set(5), Set(5)), _ => Array(Set(5)), _ => Array()))
    assert(!DummyCheck.checkConstraint(Array(), _ => Array(Set(5), Set(5)), _ => Array()))
    assert(!DummyCheck.checkConstraint(Array(Set(5)), _ => Array(), _ => Array()))

  }

  "CheckConstraint with applyConstraint as init and filtering" should "always be true" in {
    assert(DummyCheck.checkConstraint(Array(),
      DummyCheck.applyConstraint,
      DummyCheck.applyConstraint))
    assert(DummyCheck.checkConstraint(Array(Set()),
      DummyCheck.applyConstraint,
      DummyCheck.applyConstraint))
    assert(DummyCheck.checkConstraint(Array(Set(1)),
      DummyCheck.applyConstraint,
      DummyCheck.applyConstraint))
    assert(DummyCheck.checkConstraint(Array(Set(1, 2, 3), Set(1, 2, 3), Set(1, 2, 3), Set(1, 2, 3), Set(1, 2, 3)),
      DummyCheck.applyConstraint,
      DummyCheck.applyConstraint))
    assert(DummyCheck.checkConstraint(Array(Set(1, 4, 5), Set()),
      DummyCheck.applyConstraint,
      DummyCheck.applyConstraint))
    assert(DummyCheck.checkConstraint(Array(Set(1, 4, 5, 8, 7), Set(1, 4, 5, 8, 7, 9)),
      DummyCheck.applyConstraint,
      DummyCheck.applyConstraint))
  }

  "checkConstraint with applyConstraint as init and a dummy filtering" should "return false because of filtering" in {
    var i = 0
    val a = Array(Set(1, 2), Set(1, 2))

    def f(b: BranchOp): Array[Set[Int]] = {
      if (b.isInstanceOf[RestrictDomain]) i += 1
      a
    }
    //f should be called once (when restrictDomain happen)
    assert(!DummyCheck.checkConstraint(a, DummyCheck.applyConstraint, f))
    assert(i == 1)
  }

  "checkConstraint with init throwing an exception" should "consider it as a NoSolutionException" in {
    val a = Array(Set(1), Set(2))
    val b: Array[Set[Int]] = Array(Set())

    def init(v: Array[Set[Int]]): Array[Set[Int]] = throw new Exception()

    assert(!DummyCheck.checkConstraint(a,
      init,
      _ => Array()))
    assert(DummyCheck.checkConstraint(b, init, _ => Array()))
  }

  "checkConstraint with filtering throwing an exception" should "consider it as a NoSolutionException" in {
    val a = Array(Set(1,2), Set(1,2))
    def filtering(v: BranchOp): Array[Set[Int]] = throw new Exception()
    assert(!DummyCheck.checkConstraint(a,
      DummyCheck.applyConstraint,
      filtering))
  }
}