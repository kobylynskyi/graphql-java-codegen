package io.github.dreamylost

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jsonMapper
import com.fasterxml.jackson.module.kotlin.kotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLRequest
import io.github.dreamylost.api.QueryResolver
import io.github.dreamylost.model.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

/**
 * Server example at https://github.com/jxnu-liguobin/springboot-examples/tree/master/graphql-complete
 * (only a normal graphql server implement by java )
 * @author 梦境迷离
 * @version 1.0,2020/12/14
 */
class QueryResolverImpl : QueryResolver {

    override fun hero(episode: EpisodeTO?): CharacterTO? {
        val heroQueryRequest = HeroQueryRequest()
        heroQueryRequest.setEpisode(episode)
        val characterResponseProjection = CharacterResponseProjection().`all$`(3)
        val graphQLRequest = GraphQLRequest(heroQueryRequest, characterResponseProjection)
        return getResponse<HeroQueryResponse>(graphQLRequest).hero()
    }

    override fun human(id: String?): HumanTO? {
        val humanQueryRequest = HumanQueryRequest()
        humanQueryRequest.setId(id)
        val humanResponseProjection = HumanResponseProjection().`all$`(1)
        val graphQLRequest = GraphQLRequest(humanQueryRequest, humanResponseProjection)
        return getResponse<HumanQueryResponse>(graphQLRequest).human()
    }

    override fun humans(): List<HumanTO?>? {
        val humanQueryRequest = HumansQueryRequest()
        val humanResponseProjection = HumanResponseProjection().`all$`(1)
        val graphQLRequest = GraphQLRequest(humanQueryRequest, humanResponseProjection)
        return getResponse<HumansQueryResponse>(graphQLRequest).humans()
    }

    override fun droid(id: String): DroidTO {
        val productByIdQueryRequest = DroidQueryRequest()
        productByIdQueryRequest.setId(id)
        val droidResponseProjection = DroidResponseProjection().`all$`(1)
        val graphQLRequest = GraphQLRequest(productByIdQueryRequest, droidResponseProjection)
        return getResponse<DroidQueryResponse>(graphQLRequest).droid()
    }

    // The following methods is to test whether the ReturnType in the response is correct, so there is no need to implement specific logic.
    override fun test1(id: Int?): List<DroidTO>? {
        TODO("Not yet implemented")
    }

    override fun test2(id: Int?): List<DroidTO> {
        TODO("Not yet implemented")
    }

    override fun test3(id: Int?): List<DroidTO?> {
        TODO("Not yet implemented")
    }

    override fun test4(id: Int?): List<DroidTO?>? {
        TODO("Not yet implemented")
    }
}

inline fun <reified T> getResponse(request: GraphQLRequest, url: String = "http://localhost:8080/graphql"): T {
    val json = "application/json; charset=utf-8".toMediaTypeOrNull()
    val body = request.toHttpJsonBody().toRequestBody(json)
    val client = OkHttpClient()
    val request = Request.Builder().post(body).url(url).build()
    val call = client.newCall(request)
    return Jackson.mapper.readValue<T>(call.execute().body!!.string())
}

object Jackson {
    val mapper = jsonMapper {
        addModule(kotlinModule())
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        serializationInclusion(JsonInclude.Include.NON_ABSENT)
        serializationInclusion(JsonInclude.Include.NON_NULL)
    }

}