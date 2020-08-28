package io.github.dreamylost.service

import io.github.dreamylost.model.EpisodeDO

import scala.collection.JavaConverters._

/**
 *
 * @author liguobin@growingio.com
 * @version 1.0,2020/7/21
 */
object QueryResolverImplMain extends App {

  val droidResolver = new QueryResolverImpl
  println("=======QueryResolverImpl=======")
  // need typename on Projection
  println("=======get droid id 2001=========")
  val d = droidResolver.droid("2001")
  println(d)

  println("=======get humans all=======")
  val hums = droidResolver.humans
  for (h <- hums.asScala) {
    println(h)
  }

  println("=======get human id 1002=======")
  val hum = droidResolver.human("1002")
  println(hum)

  println("=======get hero Episode.EMPIRE=======")
  val character = droidResolver.hero(EpisodeDO.EMPIRE)
  println(character)

  println("=======QueryResolverImpl2=======")
  /**
   * MAX DEPTH = 4
   * gql:{{{
   * { id name friends {
   *     id name friends {
   *         id name friends {
   *             id name friends {
   *                 id name friends {
   *                      id name appearsIn secretBackstory __typename }
   *                 appearsIn secretBackstory __typename }
   *             appearsIn secretBackstory __typename }
   *         appearsIn secretBackstory __typename }
   *     appearsIn secretBackstory __typename }
   * appearsIn primaryFunction secretBackstory __typename }
   * }}}
   */
  val droidResolver2 = new QueryResolverImpl2
  println("=======2get droid id 2001=========")
  val d2 = droidResolver2.droid("2001")
  println(d2)

  println("=======2get humans all=======")
  val hums2 = droidResolver2.humans
  for (h <- hums2.asScala) {
    println(h)
  }

  println("=======2get human id 1002=======")
  val hum2 = droidResolver2.human("1002")
  println(hum2)

  println("=======2get hero Episode.EMPIRE=======")
  val character2 = droidResolver2.hero(EpisodeDO.EMPIRE)
  println(character2)

}
