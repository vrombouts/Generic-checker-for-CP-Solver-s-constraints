package checker

import scala.collection.mutable


/*
 * This class contains all the functions used specifically to check the unary resource constraint
 *
 */
class UnaryResource {

  def overloadChecking(activities:Array[Activity]): Boolean = {
    val ectMap: mutable.Map[Array[Activity], Int] = mutable.Map[Array[Activity],Int]()
    val sortedActivities=activities.sortWith(_.lct< _.lct)
    var theta:Array[Activity]=Array()
    sortedActivities.foreach{activity =>
      theta = theta :+ activity
      if(earliestCompletionTime(theta, ectMap)> activity.lct)
        return false
    }
    true
  }

  def notLast(activities: Array[Activity]): Array[Activity] = {
    //TODO: check map is used
    //TODO: check sort activities in correct order
    val ectMap: mutable.Map[Array[Activity], Int] = mutable.Map[Array[Activity],Int]()
    val lctPrime = activities.map(x => x.lct)
    val sortedActivities = activities.sortWith(_.lst  < _.lst)
    var indexQueue: Int = 0
    var theta: Array[Activity] = Array()
    sortedActivities.foreach { activity =>
      while(activity.lct>sortedActivities(indexQueue).lst){
        if(activity!=sortedActivities(indexQueue))
          theta = theta :+ sortedActivities(indexQueue)
        indexQueue += 1
      }
      if(theta.nonEmpty && earliestCompletionTime(theta, ectMap)>activity.lst){
        theta = theta :+ activity
        val index:Int = activities.indexOf(activity)
        val lst = sortedActivities(indexQueue-1).lst
        if(lst < lctPrime(index)) lctPrime(index) = lst
      }else if(theta.isEmpty) theta = theta :+ activity
    }
    for(i <- lctPrime.indices){
      activities(i).lctReduce(lctPrime(i))
    }
    activities
  }

  def earliestCompletionTime(activities: Array[Activity],ectMap:mutable.Map[Array[Activity],Int]): Int = {
    if(ectMap.keySet.contains(activities)) ectMap(activities)
    var max = Integer.MIN_VALUE
    activities.foreach{x =>
      val act=activities.filterNot(y => y==x)
      if(act.nonEmpty) {
        val ect = earliestCompletionTime(act,ectMap)
        if(max< ect) max=ect
      }
    }
    val estPlusDuration = sumOfEstAndDuration(activities)
    ectMap(activities)=if(estPlusDuration>max) estPlusDuration else max
    ectMap(activities)
  }

  def sumOfEstAndDuration(activities: Array[Activity]): Int = {
    var min = Integer.MAX_VALUE
    var duration = 0
    activities.foreach{x =>if(min>x.est) min = x.est; duration += x.dur}
    min+duration
  }

  /*
   * From the array of activities in argument, create a queue of activities and sort it by lct-duration
   */
  def arrayToQueue(a:Array[Activity]) : mutable.Queue[Activity] = {
    var q : mutable.Queue[Activity] = mutable.Queue()
    for(i <- a.indices){
      q += a(i)
    }
    q.sortWith(_.lst < _.lst)
    q
  }

  def detectablePrecedences(activities:Array[Activity]) : Unit={
    val ectMap: mutable.Map[Array[Activity], Int] = mutable.Map[Array[Activity],Int]()
    var theta:Array[Activity] = Array()
    val queue:mutable.Queue[Activity] = arrayToQueue(activities)
    var estUpdated:Array[Int] = Array()
    val sortedActivities=activities.sortWith(_.ect< _.ect)
    sortedActivities.foreach{ activity =>
      while(queue.nonEmpty && activity.ect > queue.front.lst){
        val a:Activity = queue.dequeue()
        if(a != activity)
          theta=theta :+ a
      }
      if(activity.est < earliestCompletionTime(theta,ectMap))
        estUpdated = estUpdated :+ earliestCompletionTime(theta,ectMap)
      else
        estUpdated = estUpdated :+ activity.est
    }

    for(i <- sortedActivities.indices){
      val index:Int = activities.indexOf(sortedActivities(i))
      activities(index).estReduce(estUpdated(i))
    }

  }
}
