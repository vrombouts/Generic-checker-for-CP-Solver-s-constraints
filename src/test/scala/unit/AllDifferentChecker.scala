package unit
import checker.constraints.AllDifferent

class AllDifferentChecker extends UnitSpec {
  "[1,1]" should "return false in allDifferent" in {
    assert(!AllDifferent.allDifferent(Array(1,1)))
  }

  "[1,0]" should "return true in allDifferent" in {
    assert(AllDifferent.allDifferent(Array(1,0)))
  }

  "[1,0,1]" should "return false in allDifferent" in {
    assert(!AllDifferent.allDifferent(Array(1,0,1)))
  }

  "[0,1,2,3,4,5,6,7,8,9,10,11,12,0]" should "return false in allDifferent" in {
    assert(!AllDifferent.allDifferent(Array(0,1,2,3,4,5,6,7,8,9,10,11,12,0)))
  }

  "[0,1,2,3,4,5,6,7,8,9,10,11,12]" should "return true in allDifferent" in {
    assert(AllDifferent.allDifferent(Array(0,1,2,3,4,5,6,7,8,9,10,11,12)))
  }

  "[-1,-1]" should "return false in allDifferent" in {
    assert(!AllDifferent.allDifferent(Array(-1,-1)))
  }

  "[-1,1]" should "return true in allDifferent" in {
    assert(AllDifferent.allDifferent(Array(-1,1)))
  }

  "[-1,0]" should "return true in allDifferent" in {
    assert(AllDifferent.allDifferent(Array(-1,0)))
  }
}