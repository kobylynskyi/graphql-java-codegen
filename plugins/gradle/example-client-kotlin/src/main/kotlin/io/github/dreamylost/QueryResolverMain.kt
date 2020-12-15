package io.github.dreamylost

import io.github.dreamylost.model.EpisodeTO

/**
 *
 * @author 梦境迷离
 * @version 1.0,2020/12/14
 */
class QueryResolverMain {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val queryResolverImpl = QueryResolverImpl()
            println("r1")
            val r1 = queryResolverImpl.droid("2001")
            println(r1)
            println("r2")
            val r2 = queryResolverImpl.humans()
            println(r2)
            println("r3")
            val r3 = queryResolverImpl.hero(EpisodeTO.EMPIRE)
            println(r3)
            println("r4")
            val r4 = queryResolverImpl.human("1002")
            println(r4)

        }
    }
}