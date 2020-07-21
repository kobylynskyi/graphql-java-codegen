package io.github.dreamylost.service

import io.github.dreamylost.model.EpisodeDO

/**
 *
 * @author liguobin@growingio.com
 * @version 1.0,2020/7/21
 */
object QueryResolverImplMain extends App {

  val droidResolver = new QueryResolverImpl

  // need typename on Projection
  //  println("=======get droid id 2001=========")
  //  val d = droidResolver.droid("2001")
  //  println(d)
  //
  //  println("=======get humans all=======")
  //  val hums = droidResolver.humans
  //  for (h <- hums.asScala) {
  //    println(h)
  //  }
  //
  //  println("=======get human id 1002=======")
  //  val hum = droidResolver.human("1002")
  //  println(hum)
  //
  println("=======get hero Episode.EMPIRE=======")
  val character = droidResolver.hero(EpisodeDO.EMPIRE)
  println(character)
}
