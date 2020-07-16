package io.github.dreamylost.graphql.codegen

import java.util

import com.kobylynskyi.graphql.codegen.model.{ ApiNamePrefixStrategy, ApiRootInterfaceStrategy }
import sbt._

/**
 *
 * @author liguobin@growingio.com
 * @version 1.0,2020/7/15
 */
trait GraphQLCodegenKeys {

  //与sbt key 冲突
  val genPackageName = settingKey[Option[String]]("packageName")

  //不能使用 Scala集合和asJava，后面这个使用了put方法，scala集合不支持
  val customTypesMapping = settingKey[util.Map[String, String]]("customTypesMapping")

  val apiNamePrefix = settingKey[Option[String]]("apiNamePrefix")

  val apiNameSuffix = settingKey[Option[String]]("apiNameSuffix")

  val apiRootInterfaceStrategy = settingKey[Option[ApiRootInterfaceStrategy]]("apiRootInterfaceStrategy")

  val apiNamePrefixStrategy = settingKey[Option[ApiNamePrefixStrategy]]("apiNamePrefixStrategy")

  val modelNamePrefix = settingKey[Option[String]]("modelNamePrefix")

  val modelNameSuffix = settingKey[Option[String]]("modelNameSuffix")

  val apiPackageName = settingKey[Option[String]]("apiPackageName")

  val modelPackageName = settingKey[Option[String]]("modelPackageName")

  val generateBuilder = settingKey[Option[Boolean]]("generateBuilder")

  val generateApis = settingKey[Option[Boolean]]("generateApis")

  val typeResolverPrefix = settingKey[Option[String]]("typeResolverPrefix")

  val typeResolverSuffix = settingKey[Option[String]]("typeResolverSuffix")

  val customAnnotationsMapping = settingKey[util.Map[String, String]]("customAnnotationsMapping")

  val generateEqualsAndHashCode = settingKey[Option[Boolean]]("generateEqualsAndHashCode")

  val generateImmutableModels = settingKey[Option[Boolean]]("generateImmutableModels")

  val generateToString = settingKey[Option[Boolean]]("generateToString")

  val subscriptionReturnType = settingKey[Option[String]]("subscriptionReturnType")

  val generateAsyncApi = settingKey[Option[Boolean]]("generateAsyncApi")

  val modelValidationAnnotation = settingKey[Option[String]]("modelValidationAnnotation")

  val generateParameterizedFieldsResolvers = settingKey[Option[Boolean]]("generateParameterizedFieldsResolvers")

  val generateExtensionFieldsResolvers = settingKey[Option[Boolean]]("generateParameterizedFieldsResolvers")

  val generateDataFetchingEnvironmentArgumentInApis = settingKey[Option[Boolean]]("generateDataFetchingEnvironmentArgumentInApis")

  val generateModelsForRootTypes = settingKey[Option[Boolean]]("generateModelsForRootTypes")

  val fieldsWithResolvers = settingKey[util.Set[String]]("fieldsWithResolvers")

  val fieldsWithoutResolvers = settingKey[util.Set[String]]("fieldsWithoutResolvers")

  val generateClient = settingKey[Option[Boolean]]("generateClient")

  val requestSuffix = settingKey[Option[String]]("requestSuffix")

  val responseSuffix = settingKey[Option[String]]("responseSuffix")

  val responseProjectionSuffix = settingKey[Option[String]]("responseProjectionSuffix")

  val parametrizedInputSuffix = settingKey[Option[String]]("parametrizedInputSuffix")

  val jsonConfigurationFile = settingKey[Option[String]]("jsonConfigurationFile")

  val parentInterfaces = settingKey[ParentInterfacesConfig]("parentInterfaces")

  val graphqlSchemas = settingKey[SchemaFinderConfig]("graphqlSchemas")

  val outputDir = settingKey[File]("outputDir")

  val graphqlSchemaPaths = settingKey[Array[String]]("graphqlSchemaPaths")

  val generate = taskKey[Seq[File]]("generate code task")

}
