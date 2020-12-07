package io.github.dreamylost.service

import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLRequest
import io.github.dreamylost.api.QueryResolver
import io.github.dreamylost.model._
import io.github.dreamylost.OkHttp
import io.github.dreamylost.model.EpisodeDO.EpisodeDO

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

/**
 * This is scala style deserialization
 *
 * @author liguobin@growingio.com
 * @version 1.0,2020/11/27
 */
class QueryResolverImpl extends QueryResolver {

  @throws[Exception]
  def hero(episode: EpisodeDO): CharacterDO = {
    val heroQueryRequest = HeroQueryRequest()
    heroQueryRequest.setEpisode(episode)
    val characterResponseProjection = new CharacterResponseProjection().all$(1)
    val graphQLRequest = new GraphQLRequest(heroQueryRequest, characterResponseProjection)
    val retFuture = OkHttp.executeRequest[HeroQueryResponse](graphQLRequest)
    val ret = Await.result(retFuture, Duration.Inf)
    ret.hero()
  }

  @throws[Exception]
  def human(id: String): HumanDO = {
    val humanQueryRequest = HumanQueryRequest()
    humanQueryRequest.setId(id)
    val humanResponseProjection = new HumanResponseProjection().all$(1)
    val graphQLRequest = new GraphQLRequest(humanQueryRequest, humanResponseProjection)
    val retFuture = OkHttp.executeRequest[HumanQueryResponse](graphQLRequest)
    val ret = Await.result(retFuture, Duration.Inf)
    ret.human()
  }

  @throws[Exception]
  def humans: Seq[HumanDO] = {
    val humanQueryRequest = HumansQueryRequest()
    val humanResponseProjection = new HumanResponseProjection().all$(1)
    val graphQLRequest = new GraphQLRequest(humanQueryRequest, humanResponseProjection)
    val retFuture = OkHttp.executeRequest[HumansQueryResponse](graphQLRequest)
    val ret = Await.result(retFuture, Duration.Inf)
    ret.humans()
  }

  @throws[Exception]
  def droid(id: String): DroidDO = {
    val productByIdQueryRequest = DroidQueryRequest()
    productByIdQueryRequest.setId(id)
    val droidResponseProjection = new DroidResponseProjection().all$(1)
    val graphQLRequest = new GraphQLRequest(productByIdQueryRequest, droidResponseProjection)
    val retFuture = OkHttp.executeRequest[DroidQueryResponse](graphQLRequest)
    val ret = Await.result(retFuture, Duration.Inf)
    ret.droid()
  }
}
