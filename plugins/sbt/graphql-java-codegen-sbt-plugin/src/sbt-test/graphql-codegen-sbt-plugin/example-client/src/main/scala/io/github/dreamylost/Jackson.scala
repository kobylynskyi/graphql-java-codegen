package io.github.dreamylost

import tools.jackson.annotation.JsonInclude.Include
import tools.jackson.databind.{ DeserializationFeature, ObjectMapper }
import tools.jackson.module.scala.{ DefaultScalaModule, ScalaObjectMapper }

object Jackson {

  lazy val mapper: ObjectMapper with ScalaObjectMapper = {
    val mapper = new ObjectMapper() with ScalaObjectMapper
    mapper.setSerializationInclusion(Include.NON_NULL)
    mapper.setSerializationInclusion(Include.NON_ABSENT)
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    mapper.registerModule(DefaultScalaModule)
    mapper
  }

}
