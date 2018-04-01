package unit

import checker.NoSolutionException
import checker.constraints.{AllDifferent, Constraint}

class CheckConstraint extends UnitSpec {

  def dummyConstraint(x: Array[Set[Int]]): Array[Set[Int]] = x

  @throws[NoSolutionException]
  def throwExceptionConstraint(x:Array[Set[Int]]) : Array[Set[Int]] = throw new NoSolutionException

  def nullConstraint(x:Array[Set[Int]]) : Array[Set[Int]] = null

  def badVariablesConstraint(x:Array[Set[Int]]) : Array[Set[Int]] = x.tail

  def noSolutionConstraint(x:Array[Set[Int]]) : Array[Set[Int]] = Array.fill(x.length)(Set[Int]())

  // add a constraint that makes the allDifferent constraint but only one time (no fix point reached)


  "Comparing the allDifferent constraint with the constraint that does nothing for domain variables [1] [1]" should "return false" in {
    assert(!AllDifferent.checkConstraint(Array(Set(1),Set(1)), dummyConstraint))
  }

  "Comparing the allDifferent constraint with the constraint that does nothing for domain variables [1] [0]" should "return true" in {
    assert(AllDifferent.checkConstraint(Array(Set(1),Set(0)), dummyConstraint))
  }

  "Comparing the allDifferent constraint with the constraint that does nothing for domain variables [1,0] [0,1] [1,2]" should "return false" in {
    assert(!AllDifferent.checkConstraint(Array(Set(1,0),Set(0,1),Set(1,2)), dummyConstraint))
  }

  "Comparing the allDifferent constraint with a constraint that simply returns an exception for domain variables [1] [1]" should "return true" in {
    assert(AllDifferent.checkConstraint(Array(Set(1),Set(1)), throwExceptionConstraint))
  }

  "Comparing the allDifferent constraint with a constraint that simply returns an exception for domain variables [1] [0]" should "return false" in {
    assert(!AllDifferent.checkConstraint(Array(Set(1),Set(0)), throwExceptionConstraint))
  }

  "Comparing the allDifferent constraint with a constraint that returns an exception for domain variables [1,0] [0,1] [1,2]" should "return false" in {
    assert(!AllDifferent.checkConstraint(Array(Set(1,0),Set(0,1),Set(1,2)), throwExceptionConstraint))
  }

  "Comparing the allDifferent constraint with a constraint that simply returns null for domain variables [1] [1]" should "return false" in {
    assert(!AllDifferent.checkConstraint(Array(Set(1),Set(1)), nullConstraint))
  }

  "Comparing the allDifferent constraint with a constraint that simply returns an exception for domain variables [0,1] [0,1] [0,1]" should "return true" in {
    assert(AllDifferent.checkConstraint(Array(Set(0,1),Set(0,1),Set(0,1)), throwExceptionConstraint))
  }

  "Comparing the allDifferent constraint with a constraint that simply returns null for domain variables [1] [0]" should "return false" in {
    assert(!AllDifferent.checkConstraint(Array(Set(1),Set(0)), nullConstraint))
  }

  "Comparing the allDifferent constraint with a constraint that simply returns the initial domains without the first one for domain variables [1] [1]" should "return false" in {
    assert(!AllDifferent.checkConstraint(Array(Set(1),Set(1)), badVariablesConstraint))
  }

  "Comparing the allDifferent constraint with a constraint that simply returns the initial domains without the first one for domain variables [1] [0]" should "return false" in {
    assert(!AllDifferent.checkConstraint(Array(Set(1),Set(0)), badVariablesConstraint))
  }

  "Comparing the allDifferent constraint with a constraint that simply returns all empty domains for domain variables [1] [1]" should "return true" in {
    assert(AllDifferent.checkConstraint(Array(Set(1),Set(1)), noSolutionConstraint))
  }

  "Comparing the allDifferent constraint with a constraint that simply returns all empty domains for domain variables [1] [0]" should "return false" in {
    assert(!AllDifferent.checkConstraint(Array(Set(1),Set(0)), noSolutionConstraint))
  }

  "Comparing the allDifferent constraint with a constraint that returns all empty domains for domain variables [1,0] [0,1] [1,2]" should "return false" in {
    assert(!AllDifferent.checkConstraint(Array(Set(1, 0), Set(0, 1), Set(1, 2)), noSolutionConstraint))
  }
}
