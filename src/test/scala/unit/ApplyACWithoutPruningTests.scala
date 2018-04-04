package unit

import checker.NoSolutionException
import checker.constraints.{AllDifferent, Constraint}

class ApplyACWithoutPruningTests extends UnitSpec {

  "Calling applyACWithoutPruning for AllDifferent on domains [1] [1]" should "return an exception" in {
    assertThrows[NoSolutionException] {
      Constraint.applyACWithoutPruning(Array(Set(1), Set(1)), AllDifferent.allDifferent)
    }
  }

  "Calling applyACWithoutPruning for AllDifferent on domains [0,1] [0,1]" should "return domains [0,1] [0,1]" in {
    val a : Array[Set[Int]] = Constraint.applyACWithoutPruning(Array(Set(0, 1), Set(0,1)), AllDifferent.allDifferent)
    val b : Array[Set[Int]] = Array(Set(0,1),Set(0,1))
    assert((a zip b).forall(x => x._1.equals(x._2)))
  }

  "Calling applyACWithoutPruning for AllDifferent on domains [0,1] [0,1] [0,1]" should "return an exception" in {
    assertThrows[NoSolutionException] {
      Constraint.applyACWithoutPruning(Array(Set(0, 1), Set(0, 1), Set(0, 1)), AllDifferent.allDifferent)
    }
  }

  "Calling applyACWithoutPruning for AllDifferent on domains [0,1] [1]" should "return domains [0] [1]" in {
    val a : Array[Set[Int]] = Constraint.applyACWithoutPruning(Array(Set(0, 1), Set(1)), AllDifferent.allDifferent)
    val b : Array[Set[Int]] = Array(Set(0),Set(1))
    assert((a zip b).forall(x => x._1.equals(x._2)))
  }

  "Calling applyACWithoutPruning for AllDifferent on domains [0,1,2] [2,3] [1]" should "return domains [0,2] [2,3] [1]" in {
    val a : Array[Set[Int]] = Constraint.applyACWithoutPruning(Array(Set(0, 1,2), Set(2,3), Set(1)), AllDifferent.allDifferent)
    val b : Array[Set[Int]] = Array(Set(0,2),Set(2,3),Set(1))
    assert((a zip b).forall(x => x._1.equals(x._2)))
  }

  "Calling applyACWithoutPruning for AllDifferent on domains [0,1,2] [1,2] [2,3] [0]" should "return domains [1,2] [1,2] [3] [0]" in {
    val a : Array[Set[Int]] = Constraint.applyACWithoutPruning(Array(Set(0, 1,2), Set(1,2), Set(2,3), Set(0)), AllDifferent.allDifferent)
    val b : Array[Set[Int]] = Array(Set(1,2),Set(1,2), Set(3),Set(0))
    assert((a zip b).forall(x => x._1.equals(x._2)))
  }

  "Calling applyACWithoutPruning for AllDifferent on domains [1,2,3] [0,5] [1,5,6] [1,2] [2,3] [0]" should "return domains [1,2,3] [5] [6] [1,2] [2,3] [0]" in {
    val a : Array[Set[Int]] = Constraint.applyACWithoutPruning(Array(Set(1,2,3), Set(0,5), Set(1,5,6), Set(1,2), Set(2,3), Set(0)), AllDifferent.allDifferent)
    val b : Array[Set[Int]] = Array(Set(1,2,3), Set(5), Set(6), Set(1,2), Set(2,3),Set(0))
    assert((a zip b).forall(x => x._1.equals(x._2)))
  }

  "Calling applyACWithoutPruning for AllDifferent on domains [0,3,4] [1,3] [4] [1]" should "return domains [0] [3] [4] [1] " in {
    val a : Array[Set[Int]] = Constraint.applyACWithoutPruning(Array(Set(0,3,4), Set(1,3), Set(4), Set(1)), AllDifferent.allDifferent)
    val b : Array[Set[Int]] = Array(Set(0), Set(3), Set(4),Set(1))
    assert((a zip b).forall(x => x._1.equals(x._2)))
  }

  "Calling applyACWithoutPruning for AllDifferent on domains [0,3,6,8,10] [0,1] [10] [1] [6]" should "return domains [3,8] [0,1] [10] [1] [6] " in {
    val a : Array[Set[Int]] = Constraint.applyACWithoutPruning(Array(Set(0,3,6,8,10), Set(0,1), Set(10), Set(1), Set(6)), AllDifferent.allDifferent)
    val b : Array[Set[Int]] = Array(Set(3,8), Set(0), Set(10),Set(1), Set(6))
    assert((a zip b).forall(x => x._1.equals(x._2)))
  }

  "Calling applyACWithoutPruning for AllDifferent on domain [1,2]" should "return [1,2]" in {
    val a : Array[Set[Int]] = Constraint.applyACWithoutPruning(Array(Set(1,2)), AllDifferent.allDifferent)
    val b : Array[Set[Int]] = Array(Set(1,2))
    assert((a zip b).forall(x => x._1.equals(x._2)))
  }

  "Calling AC on empty domain" should "throw a NoSolutionException" in {
    assertThrows[NoSolutionException]{
      Constraint.applyACWithoutPruning(Array(), AllDifferent.allDifferent)
    }
  }

  "Calling applyACWithoutPruning" should "should filter only the complete solutions" in {
    val a : Array[Set[Int]] = Constraint.applyACWithoutPruning(Array(Set(1,2),Set(1,2), Set(1,2),Set(1,2)), x => x.length==4)
    val b : Array[Set[Int]] = Array(Set(1,2),Set(1,2), Set(1,2),Set(1,2))
    assert((a zip b).forall(x => x._1.equals(x._2)))
  }
}