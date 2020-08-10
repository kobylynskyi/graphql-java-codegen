package io.github.dreamylost.graphql.codegen

import java.nio.file.{ Path, Paths }
import java.util

import com.kobylynskyi.graphql.codegen.{ GraphQLCodegen, GraphQLCodegenValidate }
import com.kobylynskyi.graphql.codegen.model._
import com.kobylynskyi.graphql.codegen.supplier.{ JsonMappingConfigSupplier, SchemaFinder }
import sbt.{ AutoPlugin, Def, PluginTrigger, _ }
import sbt.Keys.{ sLog, sourceManaged, _ }
import sbt.internal.util.complete.DefaultParsers.spaceDelimited

import scala.collection.JavaConverters._

/**
 *
 * @author liguobin@growingio.com
 * @version 1.0,2020/7/15
 */
object GraphQLCodegenPlugin extends GraphQLCodegenPlugin(Compile) {
  //for auto import
  val autoImport = GlobalImport
}

class GraphQLCodegenPlugin(configuration: Configuration) extends AutoPlugin with Compat {

  //TODO if impl GraphQLCodegenConfiguration, can not use settingKey in override method

  //override this by graphqlJavaCodegenVersion and javaxValidationApiVersion
  private val jValidation = "2.0.1.Final"
  private val codegen = "2.4.1"

  object GlobalImport extends GraphQLCodegenKeys {

    //should look for a way to automatically add to the classpath
    lazy val GraphQLCodegenPluginDependencies: Def.Setting[Seq[ModuleID]] = libraryDependencies ++= Seq(
      "javax.validation" % "validation-api" % javaxValidationApiVersion.value.getOrElse(jValidation),
      "io.github.kobylynskyi" % "graphql-java-codegen" % graphqlJavaCodegenVersion.value.getOrElse(codegen)
    )

    lazy val schemaFinderConfig: SchemaFinderConfig = SchemaFinderConfig(null)
    lazy val parentInterfacesConfig: ParentInterfacesConfig = ParentInterfacesConfig()
  }

  //no Auto trigger
  //Eventually I decided not to use auto trigger
  override def trigger: PluginTrigger = noTrigger

  override def requires = sbt.plugins.JvmPlugin

  import GlobalImport._

  //With the implementation of some other plugins, initialization is not necessary,
  //but maybe should be related to the dependency of key. For convenience, this is a conservative operation
  override def globalSettings: Seq[Def.Setting[_]] = Seq(
    graphqlSchemas := schemaFinderConfig,
    jsonConfigurationFile := None,
    graphqlSchemaPaths := Seq.empty,
    graphqlSchemaValidate := Seq.empty,
    customTypesMapping := new util.HashMap[String, String](),
    customAnnotationsMapping := new util.HashMap[String, String](),
    directiveAnnotationsMapping := new util.HashMap[String, String](),
    javaxValidationApiVersion := None,
    graphqlJavaCodegenVersion := None,
    // suffix/prefix/strategies:
    apiNamePrefix := None,
    apiNameSuffix := MappingConfigConstants.DEFAULT_RESOLVER_SUFFIX,
    apiRootInterfaceStrategy := ApiRootInterfaceStrategy.valueOf(MappingConfigConstants.DEFAULT_API_ROOT_INTERFACE_STRATEGY_STRING),
    apiNamePrefixStrategy := ApiNamePrefixStrategy.valueOf(MappingConfigConstants.DEFAULT_API_NAME_PREFIX_STRATEGY_STRING),
    modelNamePrefix := None,
    modelNameSuffix := None,
    requestSuffix := MappingConfigConstants.DEFAULT_REQUEST_SUFFIX,
    responseSuffix := MappingConfigConstants.DEFAULT_RESPONSE_SUFFIX,
    responseProjectionSuffix := MappingConfigConstants.DEFAULT_RESPONSE_PROJECTION_SUFFIX,
    parametrizedInputSuffix := MappingConfigConstants.DEFAULT_PARAMETRIZED_INPUT_SUFFIX,
    typeResolverPrefix := None,
    typeResolverSuffix := MappingConfigConstants.DEFAULT_RESOLVER_SUFFIX,
    subscriptionReturnType := None,
    modelValidationAnnotation := MappingConfigConstants.DEFAULT_VALIDATION_ANNOTATION,
    apiReturnType := None,
    apiReturnListType := None,
    // package name configs:
    apiPackageName := None,
    modelPackageName := None,
    // field resolvers configs:
    fieldsWithResolvers := new util.HashSet[String](),
    fieldsWithoutResolvers := new util.HashSet[String](),
    // various toggles:
    generateClient := MappingConfigConstants.DEFAULT_GENERATE_CLIENT_STRING.toBoolean,
    generateParameterizedFieldsResolvers := MappingConfigConstants.DEFAULT_GENERATE_PARAMETERIZED_FIELDS_RESOLVERS_STRING.toBoolean,
    generateExtensionFieldsResolvers := MappingConfigConstants.DEFAULT_GENERATE_EXTENSION_FIELDS_RESOLVERS_STRING.toBoolean,
    generateDataFetchingEnvironmentArgumentInApis := MappingConfigConstants.DEFAULT_GENERATE_DATA_FETCHING_ENV_STRING.toBoolean,
    generateModelsForRootTypes := MappingConfigConstants.DEFAULT_GENERATE_MODELS_FOR_ROOT_TYPES_STRING.toBoolean,
    generatePackageName := None,
    generateBuilder := MappingConfigConstants.DEFAULT_BUILDER_STRING.toBoolean,
    generateApis := MappingConfigConstants.DEFAULT_GENERATE_APIS_STRING.toBoolean,
    generateEqualsAndHashCode := MappingConfigConstants.DEFAULT_EQUALS_AND_HASHCODE_STRING.toBoolean,
    generateImmutableModels := MappingConfigConstants.DEFAULT_GENERATE_IMMUTABLE_MODELS_STRING.toBoolean,
    generateToString := MappingConfigConstants.DEFAULT_TO_STRING_STRING.toBoolean,
    // parent interfaces configs:
    parentInterfaces := parentInterfacesConfig
  )

  //setting key must use in Def、:=
  private def getMappingConfig(): Def.Initialize[MappingConfig] = Def.setting[MappingConfig] {

    //TODO use builder
    val mappingConfig = new MappingConfig
    mappingConfig.setPackageName(generatePackageName.value.orNull)
    mappingConfig.setCustomTypesMapping(customTypesMapping.value)
    mappingConfig.setApiNameSuffix(apiNameSuffix.value)
    mappingConfig.setApiNamePrefix(apiNamePrefix.value.orNull)
    mappingConfig.setApiRootInterfaceStrategy(apiRootInterfaceStrategy.value)
    mappingConfig.setApiNamePrefixStrategy(apiNamePrefixStrategy.value)
    mappingConfig.setModelNamePrefix(modelNamePrefix.value.orNull)
    mappingConfig.setModelNameSuffix(modelNameSuffix.value.orNull)
    mappingConfig.setApiPackageName(apiPackageName.value.orNull)
    mappingConfig.setModelPackageName(modelPackageName.value.orNull)
    mappingConfig.setGenerateBuilder(generateBuilder.value)
    mappingConfig.setGenerateApis(generateApis.value)
    mappingConfig.setTypeResolverSuffix(typeResolverSuffix.value)
    mappingConfig.setTypeResolverPrefix(typeResolverPrefix.value.orNull)
    mappingConfig.setModelValidationAnnotation(modelValidationAnnotation.value)
    mappingConfig.setCustomAnnotationsMapping(customAnnotationsMapping.value)
    mappingConfig.setGenerateEqualsAndHashCode(generateEqualsAndHashCode.value)
    mappingConfig.setGenerateImmutableModels(generateImmutableModels.value)
    mappingConfig.setGenerateToString(generateToString.value)
    mappingConfig.setSubscriptionReturnType(subscriptionReturnType.value.orNull)
    mappingConfig.setGenerateParameterizedFieldsResolvers(generateParameterizedFieldsResolvers.value)
    mappingConfig.setGenerateDataFetchingEnvironmentArgumentInApis(generateDataFetchingEnvironmentArgumentInApis.value)
    mappingConfig.setGenerateExtensionFieldsResolvers(generateExtensionFieldsResolvers.value)
    mappingConfig.setGenerateModelsForRootTypes(generateModelsForRootTypes.value)
    mappingConfig.setFieldsWithResolvers(fieldsWithResolvers.value)
    mappingConfig.setFieldsWithoutResolvers(fieldsWithoutResolvers.value)
    mappingConfig.setGenerateClient(generateClient.value)
    mappingConfig.setRequestSuffix(requestSuffix.value)
    mappingConfig.setResponseSuffix(responseSuffix.value)
    mappingConfig.setResponseProjectionSuffix(responseProjectionSuffix.value)
    mappingConfig.setParametrizedInputSuffix(parametrizedInputSuffix.value)
    mappingConfig.setResolverParentInterface(parentInterfaces.value.resolver)
    mappingConfig.setQueryResolverParentInterface(parentInterfaces.value.queryResolver)
    mappingConfig.setMutationResolverParentInterface(parentInterfaces.value.mutationResolver)
    mappingConfig.setSubscriptionResolverParentInterface(parentInterfaces.value.subscriptionResolver)
    mappingConfig.setApiReturnType(apiReturnType.value)
    mappingConfig.setApiReturnListType(apiReturnListType.value.orNull)
    mappingConfig.setDirectiveAnnotationsMapping(directiveAnnotationsMapping.value)
    sLog.value.debug(s"Current mapping config is <$mappingConfig>")
    mappingConfig
  }

  //skip test
  override lazy val projectSettings: Seq[Def.Setting[_]] = inConfig(configuration) {
    Seq(
      //must use sourceManaged in projectSettings
      outputDir := {
        val file = (sourceManaged in graphqlCodegen).value
        if (!file.exists()) {
          file.mkdirs()
        }
        sLog.value.info(s"Default outputDir is <${file.getAbsolutePath}>")
        file
      } //use validate that config in build.sbt
      , graphqlCodegenValidate := {
        val schemas = if (graphqlSchemaPaths.value.isEmpty) {
          Seq((sourceDirectory.value / "resources/schema.graphql").getCanonicalPath).asJava
        } else {
          graphqlSchemaPaths.value.asJava
        }
        new GraphQLCodegenValidate(schemas).validate() //use validate at terminal by user
      } // use a new src_managed for graphql, and must append to managedSourceDirectories
      , sourceManaged in graphqlCodegen := crossTarget.value / "src_managed_graphql" //if generate code successfully but compile failed, reimport project, because ivy cache
      , managedSourceDirectories in configuration := {
        managedSourceDirectories.value ++ Seq((sourceManaged in graphqlCodegen).value)
      }, javaSource := {
        (sourceManaged in graphqlCodegen).value
      }, managedClasspath := {
        Classpaths.managedJars(configuration, classpathTypes.value, update.value)
      }, graphqlSchemaValidate := {
        //use by user
        val args: Seq[String] = spaceDelimited("<arg>").parsed
        new GraphQLCodegenValidate(args.asJava).validate()
        args.foreach(a ⇒ sLog.value.info(s"Obtain args <$a>"))
        args
      }, graphqlCodegen := {
        val mappingConfigSupplier: JsonMappingConfigSupplier = buildJsonSupplier(jsonConfigurationFile.value.orNull)
        var result: Seq[File] = Seq.empty
        try {
          result = new GraphQLCodegen(getSchemas, outputDir.value, getMappingConfig().value, mappingConfigSupplier).generate.asScala
          for (file ← result) {
            sLog.value.success(s"${file.getName}")
          }
        } catch {
          case e: Exception ⇒
            sLog.value.debug(s"${e.getStackTrace}")
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
          val file = sourceDirectory.value / "resources"
          if (!file.exists()) {
            file.mkdirs()
          }
          val path = Paths.get(file.getPath)
          sLog.value.info(s"Default resources path <$path>")
          path
        }

        result
      }
    //watch graphql schema source
    ) ++ watchSourcesSetting
  }

  private def buildJsonSupplier(jsonConfigurationFile: String): JsonMappingConfigSupplier = {
    if (jsonConfigurationFile != null && jsonConfigurationFile.nonEmpty) new JsonMappingConfigSupplier(jsonConfigurationFile) else null
  }

}
