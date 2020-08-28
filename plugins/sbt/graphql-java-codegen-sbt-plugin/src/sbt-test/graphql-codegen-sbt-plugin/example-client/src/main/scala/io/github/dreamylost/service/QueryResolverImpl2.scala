package io.github.dreamylost.service

import java.util

import com.fasterxml.jackson.core.`type`.TypeReference
import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLRequest
import io.github.dreamylost.api.QueryResolver
import io.github.dreamylost.model._
import io.github.dreamylost.OkHttp

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

/**
 * use all$()
 *
 * At now, we can simply construct a request and call it directly.
 *
 * Next, we'll replace the reflection in the proxy with `all$()`
 *
 * @author liguobin@growingio.com
 * @version 1.0,2020/8/27
 */
class QueryResolverImpl2 extends QueryResolver {

  @throws[Exception]
  def hero(episode: EpisodeDO): CharacterDO = {
    val heroQueryRequest = new HeroQueryRequest
    heroQueryRequest.setEpisode(episode)
    //must use typename, and add jackson annotation to support, since v2.4
    val characterResponseProjection = new CharacterResponseProjection().all$(1)
    val graphQLRequest = new GraphQLRequest(heroQueryRequest, characterResponseProjection)
    val retFuture = OkHttp.executeRequest(graphQLRequest, new TypeReference[CharacterDO] {})
    val ret = Await.result(retFuture, Duration.Inf)
    ret
  }

  @throws[Exception]
  def human(id: String): HumanDO = {
    val humanQueryRequest = new HumanQueryRequest
    humanQueryRequest.setId(id)
    //must use typename, and add jackson annotation to support, since v2.4
    val humanResponseProjection = new HumanResponseProjection().all$(1)
    val graphQLRequest = new GraphQLRequest(humanQueryRequest, humanResponseProjection)
    val retFuture = OkHttp.executeRequest(graphQLRequest, new TypeReference[HumanDO] {})
    val ret = Await.result(retFuture, Duration.Inf)
    ret
  }

  @throws[Exception]
  def humans: util.List[HumanDO] = {
    import scala.collection.JavaConverters._
    val humanQueryRequest = new HumansQueryRequest
    //must use typename, and add jackson annotation to support, since v2.4
    val humanResponseProjection = new HumanResponseProjection().all$(1)
    val graphQLRequest = new GraphQLRequest(humanQueryRequest, humanResponseProjection)
    val retFuture = OkHttp.executeRequest(graphQLRequest, new TypeReference[List[HumanDO]] {})
    val ret = Await.result(retFuture, Duration.Inf)
    ret.asJava
  }

  @throws[Exception]
  def droid(id: String): DroidDO = {
    val productByIdQueryRequest = new DroidQueryRequest
    productByIdQueryRequest.setId(id)
    //must use typename, and add jackson annotation to support, since v2.4
    val droidResponseProjection = new DroidResponseProjection().all$(1)
    val graphQLRequest = new GraphQLRequest(productByIdQueryRequest, droidResponseProjection)
    val retFuture = OkHttp.executeRequest(graphQLRequest, new TypeReference[DroidDO] {})
    val ret = Await.result(retFuture, Duration.Inf)
    ret
  }
}
