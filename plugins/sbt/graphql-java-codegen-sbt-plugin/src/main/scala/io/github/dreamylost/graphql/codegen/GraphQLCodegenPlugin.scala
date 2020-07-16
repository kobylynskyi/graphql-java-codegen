package io.github.dreamylost.graphql.codegen

import java.nio.file.{ Path, Paths }
import java.util

import com.kobylynskyi.graphql.codegen.{ GraphQLCodegen, GraphQLCodegenValidate }
import com.kobylynskyi.graphql.codegen.model._
import com.kobylynskyi.graphql.codegen.supplier.{ JsonMappingConfigSupplier, SchemaFinder }
import sbt.{ AutoPlugin, Def, PluginTrigger, _ }
import sbt.Keys.sLog
import sbt.internal.util.complete.DefaultParsers.spaceDelimited

import scala.collection.JavaConverters._

/**
 *
 * @author liguobin@growingio.com
 * @version 1.0,2020/7/15
 */
object GraphQLCodegenPlugin extends AutoPlugin {

  //TODO if impl GraphQLCodegenConfiguration, can not use settingKey in override method

  private val codegen = "2.2.1";
  private val jvalidation = "2.0.1.Final"

  object autoImport extends GraphQLCodegenKeys {

    //for auto import
    val GraphQLCodegen: Seq[ModuleID] = Seq(
      "io.github.kobylynskyi" % "graphql-java-codegen" % codegen,
      "javax.validation" % "validation-api" % jvalidation
    )
  }

  override def trigger: PluginTrigger = noTrigger


  import autoImport._

  private val defaultResourcesPath = "src/main/resources"
  private val defaultSourcePath = "src/main/java"

  override def globalSettings: Seq[Def.Setting[_]] = Seq(
    graphqlSchemas := new SchemaFinderConfig()

    , outputDir := {
      val file = new File(defaultSourcePath)
      if (!file.exists()) {
        file.createNewFile()
      }
      sLog.value.info(s"Default outputDir is <${file.getAbsolutePath}>")
      file
    }

    , graphqlSchemaPaths := Seq.empty

    , graphqlSchemaValidate := Seq.empty

    , genPackageName := None

    , customTypesMapping := new util.HashMap[String, String]()

    , apiNamePrefix := None

    , apiNameSuffix := None

    , apiRootInterfaceStrategy := None

    , apiNamePrefixStrategy := None

    , modelNamePrefix := None

    , modelNameSuffix := None

    , apiPackageName := None

    , modelPackageName := None

    , generateBuilder := None

    , generateApis := None

    , typeResolverPrefix := None

    , typeResolverSuffix := None

    , customAnnotationsMapping := new util.HashMap[String, String]()

    , generateEqualsAndHashCode := None

    , generateImmutableModels := None

    , generateToString := None

    , subscriptionReturnType := None

    , generateAsyncApi := None

    , modelValidationAnnotation := None

    , generateParameterizedFieldsResolvers := None

    , generateExtensionFieldsResolvers := None

    , generateDataFetchingEnvironmentArgumentInApis := None

    , generateModelsForRootTypes := None

    , fieldsWithResolvers := new util.HashSet[String]()

    , fieldsWithoutResolvers := new util.HashSet[String]()

    , generateClient := None

    , requestSuffix := None

    , responseSuffix := None

    , responseProjectionSuffix := None

    , parametrizedInputSuffix := None

    , jsonConfigurationFile := None

    , parentInterfaces := new ParentInterfacesConfig()
  )


  private def getMappingConfig(): Def.Initialize[MappingConfig] = {
    Def.setting[MappingConfig] {

      //TODO use builder
      val mappingConfig = new MappingConfig

      mappingConfig.setPackageName(genPackageName.value.orNull)

      mappingConfig.setCustomTypesMapping(customTypesMapping.value)

      mappingConfig.setApiNameSuffix(apiNameSuffix.value.getOrElse(MappingConfigConstants.DEFAULT_RESOLVER_SUFFIX))

      mappingConfig.setApiNamePrefix(apiNamePrefix.value.orNull)

      mappingConfig.setApiRootInterfaceStrategy(apiRootInterfaceStrategy.value.getOrElse(ApiRootInterfaceStrategy.valueOf(MappingConfigConstants.DEFAULT_API_ROOT_INTERFACE_STRATEGY_STRING)))

      mappingConfig.setApiNamePrefixStrategy(apiNamePrefixStrategy.value.getOrElse(ApiNamePrefixStrategy.valueOf(MappingConfigConstants.DEFAULT_API_NAME_PREFIX_STRATEGY_STRING)))

      mappingConfig.setModelNamePrefix(modelNamePrefix.value.orNull)

      mappingConfig.setModelNameSuffix(modelNameSuffix.value.orNull)

      mappingConfig.setApiPackageName(apiPackageName.value.orNull)

      mappingConfig.setModelPackageName(modelPackageName.value.orNull)

      mappingConfig.setGenerateBuilder(generateBuilder.value.getOrElse[Boolean](MappingConfigConstants.DEFAULT_BUILDER_STRING.toBoolean))

      mappingConfig.setGenerateApis(generateApis.value.getOrElse[Boolean](MappingConfigConstants.DEFAULT_GENERATE_APIS_STRING.toBoolean))

      mappingConfig.setTypeResolverSuffix(typeResolverSuffix.value.getOrElse(MappingConfigConstants.DEFAULT_RESOLVER_SUFFIX))

      mappingConfig.setTypeResolverPrefix(typeResolverPrefix.value.orNull)

      mappingConfig.setModelValidationAnnotation(modelValidationAnnotation.value.getOrElse(MappingConfigConstants.DEFAULT_VALIDATION_ANNOTATION))

      mappingConfig.setCustomAnnotationsMapping(customAnnotationsMapping.value)

      mappingConfig.setGenerateEqualsAndHashCode(generateEqualsAndHashCode.value.getOrElse[Boolean](MappingConfigConstants.DEFAULT_EQUALS_AND_HASHCODE_STRING.toBoolean))

      mappingConfig.setGenerateImmutableModels(generateImmutableModels.value.getOrElse[Boolean](MappingConfigConstants.DEFAULT_GENERATE_IMMUTABLE_MODELS_STRING.toBoolean))

      mappingConfig.setGenerateToString(generateToString.value.getOrElse[Boolean](MappingConfigConstants.DEFAULT_TO_STRING_STRING.toBoolean))

      mappingConfig.setSubscriptionReturnType(subscriptionReturnType.value.orNull)

      mappingConfig.setGenerateAsyncApi(generateAsyncApi.value.getOrElse[Boolean](MappingConfigConstants.DEFAULT_GENERATE_ASYNC_APIS_STRING.toBoolean))

      mappingConfig.setGenerateParameterizedFieldsResolvers(generateParameterizedFieldsResolvers.value.getOrElse[Boolean](MappingConfigConstants.DEFAULT_GENERATE_PARAMETERIZED_FIELDS_RESOLVERS_STRING.toBoolean))

      mappingConfig.setGenerateDataFetchingEnvironmentArgumentInApis(generateDataFetchingEnvironmentArgumentInApis.value.getOrElse[Boolean](MappingConfigConstants.DEFAULT_GENERATE_DATA_FETCHING_ENV_STRING.toBoolean))

      mappingConfig.setGenerateExtensionFieldsResolvers(generateExtensionFieldsResolvers.value.getOrElse[Boolean](MappingConfigConstants.DEFAULT_GENERATE_EXTENSION_FIELDS_RESOLVERS_STRING.toBoolean))

      mappingConfig.setGenerateModelsForRootTypes(generateModelsForRootTypes.value.getOrElse[Boolean](MappingConfigConstants.DEFAULT_GENERATE_MODELS_FOR_ROOT_TYPES_STRING.toBoolean))

      mappingConfig.setFieldsWithResolvers(fieldsWithResolvers.value)

      mappingConfig.setFieldsWithoutResolvers(fieldsWithoutResolvers.value)

      mappingConfig.setGenerateClient(generateClient.value.getOrElse[Boolean](MappingConfigConstants.DEFAULT_GENERATE_CLIENT_STRING.toBoolean))

      mappingConfig.setRequestSuffix(requestSuffix.value.getOrElse(MappingConfigConstants.DEFAULT_REQUEST_SUFFIX))

      mappingConfig.setResponseSuffix(responseSuffix.value.getOrElse(MappingConfigConstants.DEFAULT_RESPONSE_SUFFIX))

      mappingConfig.setResponseProjectionSuffix(responseProjectionSuffix.value.getOrElse(MappingConfigConstants.DEFAULT_RESPONSE_PROJECTION_SUFFIX))

      mappingConfig.setParametrizedInputSuffix(parametrizedInputSuffix.value.getOrElse(MappingConfigConstants.DEFAULT_PARAMETRIZED_INPUT_SUFFIX))

      mappingConfig.setResolverParentInterface(parentInterfaces.value.resolver)

      mappingConfig.setQueryResolverParentInterface(parentInterfaces.value.queryResolver)

      mappingConfig.setMutationResolverParentInterface(parentInterfaces.value.mutationResolver)

      mappingConfig.setSubscriptionResolverParentInterface(parentInterfaces.value.subscriptionResolver)

      mappingConfig
    }
  }

  override lazy val projectSettings: Seq[Def.Setting[_]] = Seq(
    //use validate that config in build.sbt
    graphqlCodegenValidate := new GraphQLCodegenValidate(graphqlSchemaPaths.value.asJava).validate()
    //use validate at terminal by user
    , graphqlSchemaValidate := {
      //use by user
      val args: Seq[String] = spaceDelimited("<arg>").parsed
      new GraphQLCodegenValidate(args.asJava).validate()
      args.foreach(a => sLog.value.info(s"Obtain args: $a"))
      args
    }
    , graphqlCodegen := {
      val mappingConfigSupplier = buildJsonSupplier(jsonConfigurationFile.value.orNull)
      var result: Seq[File] = Seq.empty
      try {
        result = new GraphQLCodegen(getSchemas, outputDir.value, getMappingConfig().value, mappingConfigSupplier).generate.asScala
        for (file <- result) {
          sLog.value.info(s"Finish generate code, file name is <${file.getName}>.")
        }
      }
      catch {
        case e: Exception =>
          sLog.value.info(s"Error: ${e.getLocalizedMessage}")
          throw new Exception("Code generation failed. See above for the full exception.")
      }


      def getSchemas: util.List[String] = {
        if (graphqlSchemaPaths != null && graphqlSchemaPaths.value.nonEmpty) return graphqlSchemaPaths.value.asJava
        val schemasRootDir: Path = getSchemasRootDir
        val finder: SchemaFinder = new SchemaFinder(schemasRootDir)
        finder.setRecursive(graphqlSchemas.value.recursive)
        finder.setIncludePattern(graphqlSchemas.value.includePattern)
        finder.setExcludedFiles(graphqlSchemas.value.excludedFiles.asJava)
        finder.findSchemas
      }

      def getSchemasRootDir: Path = {
        val rootDir = graphqlSchemas.value.rootDir
        if (rootDir == null) {
          val default = getDefaultResourcesDirectory
          if (default == null) throw new IllegalStateException("Default resource folder not found, please provide <rootDir> in <graphqlSchemas>")
          else return default
        }
        Paths.get(rootDir)
      }

      def getDefaultResourcesDirectory: Path = {
        val file = new File(defaultResourcesPath)
        if (!file.exists()) {
          file.createNewFile()
        }
        val path = Paths.get(file.getPath)
        sLog.value.info(s"Default resources path is <$path>")
        path
      }

      result
    }
  )


  private def buildJsonSupplier(jsonConfigurationFile: String): JsonMappingConfigSupplier = if (jsonConfigurationFile != null && jsonConfigurationFile.nonEmpty) new JsonMappingConfigSupplier(jsonConfigurationFile) else null

}
