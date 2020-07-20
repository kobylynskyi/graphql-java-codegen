package io.github.kobylynskyi.graphql.test.api.impl

import io.github.kobylynskyi.graphql.test.api.GetUserQueryResolver
import io.github.kobylynskyi.graphql.test.model.User

import scala.concurrent.Future

/**
 * example, use a async api
 *
 * @author 梦境迷离 dreamylost
 * @since 2020-07-19
 * @version v1.0
 */
class GetUserQueryResolverImpl extends GetUserQueryResolver {
  override def getUser(id: _root_.java.lang.String): _root_.scala.concurrent.Future[_root_.io.github.kobylynskyi.graphql.test.model.User] = {
    Future.successful(User.builder().setId("id").setEmail("email@126.com").setUsername("user name").build())
  }
}
