package checker.scheduling

import checker.{Generators, NoSolutionException}
import org.scalacheck.Prop.forAll
import scala.collection.mutable

object Scheduling {
  
  private def clone(activities:Array[Activity]): Array[Activity] =
    activities.map(x => new Activity(x.start,x.duration,x.end))

  def checkAC(filteringTested:Array[Activity] => Array[Activity]) : Unit={
    forAll(Generators.scheduling) { x =>
      x.isEmpty || x.exists(_.isEmpty) || checkNotFirst(x.toArray, filteringTested)
    }.check
  }

  def checkScheduling(variables: Array[Activity],
                      constraintTested: Array[Activity] => Array[Activity]) : Boolean = {
    true
  }

  def checkNotFirst(activities: Array[Activity],
                    constraintTested:Array[Activity] => Array[Activity]): Boolean = {
    var ourReducedDomains: Array[Activity] = Array()
    var theirReducedDomains: Array[Activity] = Array()
    var ourError = false
    var theirError = false
    try {
      ourReducedDomains = notFirst(clone(activities))
    } catch {
      case _: NoSolutionException => ourError = true
    }
    try {
      theirReducedDomains = constraintTested(activities)
    } catch {
      case _: NoSolutionException => theirError = true
    }
    if (ourError && theirError) return true
    if (ourError && !theirError) {
      for (activity <- theirReducedDomains) {
        if (activity.isEmpty)
          return true
      }
      println("Failed for " + activities.foreach(println))
      println("You should return an exception")
      println("but you have " + theirReducedDomains.foreach(println))
      return false
    }
    if (!ourError && theirError) {
      for (activity <- ourReducedDomains) {
        if (activity.isEmpty)
          return true
      }
      println("Failed for " + activities.foreach(println))
      println("You shoud have " + ourReducedDomains.foreach(println))
      println("But you returned an exception")
      return false
    }
    if (ourReducedDomains.length != theirReducedDomains.length) {
      println("You don't return the correct number of activities in your solution!")
      return false
    }
    for(i <- ourReducedDomains.indices){
      if(!ourReducedDomains(i).equals(theirReducedDomains(i))) {
        println("Failed for " + activities.foreach(println))
        println("You shoud have " + ourReducedDomains.foreach(println))
        println("But you had " + theirReducedDomains.foreach(println))
        return false
      }
    }
    true
  }

  def overloadChecking(activities: Array[Activity]): Boolean = {
    omegas(activities).foreach{ set =>
      println(set.toList)
      if( est(set) + p(set) > lct(set)) return false
    }
    true
  }

  @throws[NoSolutionException]
  def notFirst(activities: Array[Activity]): Array[Activity] = {
    omegas(activities).foreach { set =>
      for (activity <- activities) {
        if (!set.contains(activity) && activity.ect > lct(set) - p(set)) {
          activity.estReduce(minEct(set))
          if (activity.isEmpty)
            throw new NoSolutionException
        }
      }
    }
    activities
  }

  @throws[NoSolutionException]
  def notLast(activities: Array[Activity]): Array[Activity] = {
    omegas(activities).foreach{ set => for(activity <- activities){
        if(!set.contains(activity) && est(set) + p(set)>activity.lst) {
          activity.lctReduce(maxLst(set))
          if(activity.end.isEmpty)
            throw new NoSolutionException
        }
      }
    }
    activities
  }



  @throws[NoSolutionException]
  def edgeFinding(activities: Array[Activity]) : Array[Activity] = {
    omegas(activities).foreach { set =>
      for (activity <- activities) {
        if (!set.contains(activity)) {
          if (est(set, activity) + p(set, activity) > lct(set)) {
            activity.estReduce(ect(set))
            if (activity.start.isEmpty)
              throw new NoSolutionException
          }
          if(lct(set,activity)-p(set,activity) < est(set)) {
            activity.lctReduce(lst(set))
            if (activity.end.isEmpty)
              throw new NoSolutionException
          }
        }
      }
    }
    activities
  }


  def init(activity: Activity):Stream[(mutable.Map[Int,Int],List[FixedActivity])]={
    var solutions : List[(mutable.Map[Int,Int],List[FixedActivity])] = List()
    for(start <- activity.start){
      val f:FixedActivity = new FixedActivity(start,start+activity.dur)
      val fixedActivities:List[FixedActivity] = List(f)
      val map:mutable.Map[Int,Int]=mutable.Map()
      for(j <- f.start to f.end)
        map.put(j, activity.height)
      solutions=(map,fixedActivities)+:solutions
    }
    solutions.toStream
  }

  def nextElement(activity:Activity, sols:Stream[(mutable.Map[Int,Int],List[FixedActivity])]) : Stream[(mutable.Map[Int,Int],List[FixedActivity])]={
    def nextStart(starts:Set[Int],str: Stream[(mutable.Map[Int,Int],List[FixedActivity])],map: mutable.Map[Int,Int],fixedActivities:List[FixedActivity] ): Stream[(mutable.Map[Int,Int],List[FixedActivity])]= {
      if (starts.isEmpty) return str
      val start = starts.last
      val f: FixedActivity = new FixedActivity(start, start + activity.dur)
      val ff: List[FixedActivity] = fixedActivities :+ f
      for (time <- f.start to f.end) {
        if (map.contains(time))
          map(time) += activity.height
        else
          map.put(time, activity.height)
      }
      (map, ff) #:: nextStart(starts - start, str, map, fixedActivities)
    }
    var str :Stream[(mutable.Map[Int,Int],List[FixedActivity])]= Stream.empty
    for((map,fixedActivities) <- sols){
      str = nextStart(activity.start,str,map, fixedActivities)
    }
    str
  }

  @throws[NoSolutionException]
  def cartesianForCumulative(activities:Array[Activity], capacity:Int) : Array[Activity]={
    var sols = init(activities(0))
    for(i <- 1 until activities.length){
      sols=nextElement(activities(i),sols).filter(x => x._1.exists(_._2>capacity))
    }
    if(sols.isEmpty) throw new NoSolutionException
    toDomains(sols)
  }


  def toDomains(sols:Stream[(mutable.Map[Int,Int],List[FixedActivity])]):Array[Activity] ={
    val act:Array[Activity]=Array.fill(sols.head._2.length)(new Activity())
    for((_,fixedActivities)<- sols){
      for(i <- fixedActivities.indices){
        act(i).start += fixedActivities(i).start
        act(i).end   += fixedActivities(i).end
        act(i).duration += fixedActivities(i).duration
      }
    }
    act
  }



  //TODO: Think if Stream would not be more efficient
  private def omegas(activities: Array[Activity]): List[Array[Activity]] = {
    def constructList(omegas: List[Array[Activity]], index:Int): List[Array[Activity]] = {
      var newOmegas: List[Array[Activity]] = omegas
      omegas.foreach{x => newOmegas = (activities(index)+:x) :: newOmegas}
      if(index>0) return constructList(newOmegas, index-1)
      newOmegas
    }
    val omegas: List[Array[Activity]] = List(Array())
    constructList(omegas,activities.length-1)
  }

  private def est(activities: Array[Activity]): Int = {
    if(activities.isEmpty) return Integer.MIN_VALUE
    var min = Integer.MAX_VALUE
    activities.foreach{act => if( act.est<min) min = act.est}
    min
  }
  private def est(activities: Array[Activity], a:Activity): Int = Math.min(est(activities), a.est)
  private def p(activities: Array[Activity])  : Int = {
    if(activities.isEmpty) return 0
    var p = 0
    activities.foreach{act => p += act.dur}
    p
  }
  private def p(activities:Array[Activity], a:Activity) : Int= p(activities)+a.dur
  private def lct(activities: Array[Activity]): Int = {
    if (activities.isEmpty) return Integer.MAX_VALUE
    var max = Integer.MIN_VALUE
    activities.foreach { act => if (act.lct > max) max = act.lct }
    max
  }
  private def lct(activities:Array[Activity], a:Activity):Int= Math.max(lct(activities), a.lct)
  private def maxLst(activities: Array[Activity]): Int = {
    if (activities.isEmpty) return Integer.MAX_VALUE
    var max = Integer.MIN_VALUE
    activities.foreach { act => if (act.lst > max) max = act.lst }
    max
  }
  private def minEct(activities: Array[Activity]): Int = {
    if (activities.isEmpty) return Integer.MIN_VALUE
    var min = Integer.MAX_VALUE
    activities.foreach { act => if (act.ect < min) min = act.ect }
    min
  }
  private def lst(activities: Array[Activity]): Int = {
    val omega:List[Array[Activity]] = omegas(activities)
    var min:Int = Integer.MAX_VALUE
    for(act <- omega){
      val v=lct(act) - p(act)
      if(v < min){
        min=v
      }
    }
    min
  }
  private def ect(activities: Array[Activity]): Int = {
    val omega:List[Array[Activity]] = omegas(activities)
    var max:Int = Integer.MIN_VALUE
    for(act <- omega){
      val v=est(act)+ p(act)
      if(v < max){
        max=v
      }
    }
    max
  }

  def main(args: Array[String]):Unit = {
    var activities: Array[Activity] = Array(
      new Activity(Set(0,1),Set(2),Set(2,3)),
      new Activity(Set(1,2),Set(3),Set(4,5))
    )

    //activities = notLast(activities)
    activities = notFirst(activities)
    activities.foreach(_.enforceCohesion())
    println(activities.toList)

    checkAC(notFirst)
  }
}