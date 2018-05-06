package checker.incremental

import checker.Op
import scala.util.Random

class RestrictDomain(val doms: Array[Set[Int]], val random: Random) extends BranchOp(doms) {
  var index: Int = getIndex
  var op: String = Op.randomOp(random)
  var constant: Int = randomConstant

  private def randomConstant: Int = {
    var dom = domains(index)
    if (op.equals(Op.lesserThan) || op.equals(Op.greaterThanOrEqual)) dom = dom - dom.min
    else if (op.equals(Op.greaterThan) || op.equals(Op.lesserThanOrEqual)) dom = dom - dom.max
    val variable = dom.toArray
    variable(random.nextInt(variable.length))
  }

  private def getIndex: Int = {
    var possibleIndexes: List[Int] = List()
    for (i <- domains.indices) {
      if (domains(i).size > 1) possibleIndexes = i :: possibleIndexes
    }
    val indexOfIndex = random.nextInt(possibleIndexes.size)
    possibleIndexes(indexOfIndex)
  }

  def applyRestriction: Array[Set[Int]] = {
    var domainToReduce: Set[Int] = domains(index)
    for (i <- domains(index)) {
      if (!Op.respectOp(op, i, constant)) domainToReduce = domainToReduce - i
    }
    domains(index)= domainToReduce
    domains
  }

  override def clone: BranchOp = {
    val rd = new RestrictDomain(domains.clone, random)
    rd.index = index
    rd.constant = constant
    rd.op = op
    rd
  }

  override def toString: String = "Restriction of domains (x_" + index + op + constant+")"
}