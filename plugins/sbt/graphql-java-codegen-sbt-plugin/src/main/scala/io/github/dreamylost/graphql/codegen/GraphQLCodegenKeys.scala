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

  //Conflict with SBT key
  val generatePackageName = settingKey[Option[String]]("generatePackageName")

  //Scala collection and asJava cannot be used. The latter one uses the put method, which is not supported by Scala collection
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

  val graphqlSchemaPaths = settingKey[Seq[String]]("graphqlSchemaPaths")

  //use different paths
  val graphqlSchemaValidate = inputKey[Seq[String]]("graphqlSchemaValidatePaths")

  val graphqlCodegen = taskKey[Seq[File]]("generate code task")

  val graphqlCodegenValidate = taskKey[Unit]("graphql validate task")

  //for version
  val graphqlJavaCodegenVersion = settingKey[Option[String]]("graphql-java-codegen version")
  val javaxValidationApiVersion = settingKey[Option[String]]("javax-validation-api version")

}
