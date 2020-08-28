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
object GraphQLCodegenPlugin extends GraphQLCodegenPlugin(Compile, configurationPostfix = "-main") {
  //for auto import
  val autoImport = GlobalImport
}

class GraphQLCodegenPlugin(configuration: Configuration, private[codegen] val configurationPostfix: String = "") extends AutoPlugin with Compat {
  self =>

  //TODO if impl GraphQLCodegenConfiguration, can not use settingKey in override method

  //override this by graphqlJavaCodegenVersion and javaxValidationApiVersion
  private val jValidation = "2.0.1.Final"
  private val codegen = "3.0.1-SNAPSHOT"

  object GlobalImport extends GraphQLCodegenKeys {

    //should look for a way to automatically add to the classpath
    lazy val GraphQLCodegenPluginDependencies: Def.Setting[Seq[ModuleID]] = libraryDependencies ++= Seq(
      "javax.validation" % "validation-api" % javaxValidationApiVersion.value.getOrElse(jValidation),
      "io.github.kobylynskyi" % "graphql-java-codegen" % graphqlJavaCodegenVersion.value.getOrElse(codegen)
    )

    lazy val schemaFinderConfig: SchemaFinderConfig = SchemaFinderConfig(null)
    lazy val parentInterfacesConfig: ParentInterfacesConfig = ParentInterfacesConfig()
    lazy val GraphQLCodegenConfig = self.GraphQLCodegenConfig

  }

  //no Auto trigger
  //Eventually I decided not to use auto trigger
  override def trigger: PluginTrigger = noTrigger

  override def requires = sbt.plugins.JvmPlugin

  override def projectConfigurations: Seq[Configuration] = GraphQLCodegenConfig :: Nil

  import GlobalImport._

  //With the implementation of some other plugins, initialization is not necessary,
  //but maybe should be related to the dependency of key. For convenience, this is a conservative operation
  override lazy val globalSettings: Seq[Def.Setting[_]] = Seq(
    graphqlQueryIntrospectionResultPath := None,
    graphqlSchemas := schemaFinderConfig,
    jsonConfigurationFile := None,
    graphqlSchemaPaths := Seq.empty,
    graphqlSchemaValidate := Seq.empty,
    customTypesMapping := new util.HashMap[String, String](), //TODO use scala Map, convert to java Map
    customAnnotationsMapping := new util.HashMap[String, util.List[String]](),
    directiveAnnotationsMapping := new util.HashMap[String, util.List[String]](),
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
    apiInterfaceStrategy := MappingConfigConstants.DEFAULT_API_INTERFACE_STRATEGY,
    useOptionalForNullableReturnTypes := MappingConfigConstants.DEFAULT_USE_OPTIONAL_FOR_NULLABLE_RETURN_TYPES,
    // package name configs:
    apiPackageName := None,
    modelPackageName := None,
    // field resolvers configs:
    fieldsWithResolvers := new util.HashSet[String](),
    fieldsWithoutResolvers := new util.HashSet[String](),
    // various toggles:
    generateClient := MappingConfigConstants.DEFAULT_GENERATE_CLIENT,
    generateParameterizedFieldsResolvers := MappingConfigConstants.DEFAULT_GENERATE_PARAMETERIZED_FIELDS_RESOLVERS,
    generateExtensionFieldsResolvers := MappingConfigConstants.DEFAULT_GENERATE_EXTENSION_FIELDS_RESOLVERS,
    generateDataFetchingEnvironmentArgumentInApis := MappingConfigConstants.DEFAULT_GENERATE_DATA_FETCHING_ENV,
    generateModelsForRootTypes := MappingConfigConstants.DEFAULT_GENERATE_MODELS_FOR_ROOT_TYPES,
    generatePackageName := None,
    generateBuilder := MappingConfigConstants.DEFAULT_BUILDER,
    generateApis := MappingConfigConstants.DEFAULT_GENERATE_APIS,
    generateEqualsAndHashCode := MappingConfigConstants.DEFAULT_EQUALS_AND_HASHCODE,
    generateImmutableModels := MappingConfigConstants.DEFAULT_GENERATE_IMMUTABLE_MODELS,
    generateToString := MappingConfigConstants.DEFAULT_TO_STRING,
    // parent interfaces configs:
    parentInterfaces := parentInterfacesConfig,
    responseProjectionMaxDepth := MappingConfigConstants.DEFAULT_PROJECTION_MAX_DEPTH
  )

  private def getMappingConfig(): Def.Initialize[MappingConfig] = Def.setting[MappingConfig] {

    //TODO use builder
    val mappingConfig = new MappingConfig
    mappingConfig.setPackageName((generatePackageName in GraphQLCodegenConfig).value.orNull)
    mappingConfig.setCustomTypesMapping((customTypesMapping in GraphQLCodegenConfig).value)
    mappingConfig.setApiNameSuffix((apiNameSuffix in GraphQLCodegenConfig).value)
    mappingConfig.setApiNamePrefix((apiNamePrefix in GraphQLCodegenConfig).value.orNull)
    mappingConfig.setApiRootInterfaceStrategy((apiRootInterfaceStrategy in GraphQLCodegenConfig).value)
    mappingConfig.setApiNamePrefixStrategy((apiNamePrefixStrategy in GraphQLCodegenConfig).value)
    mappingConfig.setModelNamePrefix((modelNamePrefix in GraphQLCodegenConfig).value.orNull)
    mappingConfig.setModelNameSuffix((modelNameSuffix in GraphQLCodegenConfig).value.orNull)
    mappingConfig.setApiPackageName((apiPackageName in GraphQLCodegenConfig).value.orNull)
    mappingConfig.setModelPackageName((modelPackageName in GraphQLCodegenConfig).value.orNull)
    mappingConfig.setGenerateBuilder((generateBuilder in GraphQLCodegenConfig).value)
    mappingConfig.setGenerateApis((generateApis in GraphQLCodegenConfig).value)
    mappingConfig.setTypeResolverSuffix((typeResolverSuffix in GraphQLCodegenConfig).value)
    mappingConfig.setTypeResolverPrefix((typeResolverPrefix in GraphQLCodegenConfig).value.orNull)
    mappingConfig.setModelValidationAnnotation((modelValidationAnnotation in GraphQLCodegenConfig).value)
    mappingConfig.setCustomAnnotationsMapping((customAnnotationsMapping in GraphQLCodegenConfig).value)
    mappingConfig.setGenerateEqualsAndHashCode((generateEqualsAndHashCode in GraphQLCodegenConfig).value)
    mappingConfig.setGenerateImmutableModels((generateImmutableModels in GraphQLCodegenConfig).value)
    mappingConfig.setGenerateToString((generateToString in GraphQLCodegenConfig).value)
    mappingConfig.setSubscriptionReturnType((subscriptionReturnType in GraphQLCodegenConfig).value.orNull)
    mappingConfig.setGenerateParameterizedFieldsResolvers((generateParameterizedFieldsResolvers in GraphQLCodegenConfig).value)
    mappingConfig.setGenerateDataFetchingEnvironmentArgumentInApis((generateDataFetchingEnvironmentArgumentInApis in GraphQLCodegenConfig).value)
    mappingConfig.setGenerateExtensionFieldsResolvers((generateExtensionFieldsResolvers in GraphQLCodegenConfig).value)
    mappingConfig.setGenerateModelsForRootTypes((generateModelsForRootTypes in GraphQLCodegenConfig).value)
    mappingConfig.setFieldsWithResolvers((fieldsWithResolvers in GraphQLCodegenConfig).value)
    mappingConfig.setFieldsWithoutResolvers((fieldsWithoutResolvers in GraphQLCodegenConfig).value)
    mappingConfig.setGenerateClient((generateClient in GraphQLCodegenConfig).value)
    mappingConfig.setRequestSuffix((requestSuffix in GraphQLCodegenConfig).value)
    mappingConfig.setResponseSuffix((responseSuffix in GraphQLCodegenConfig).value)
    mappingConfig.setResponseProjectionSuffix((responseProjectionSuffix in GraphQLCodegenConfig).value)
    mappingConfig.setParametrizedInputSuffix((parametrizedInputSuffix in GraphQLCodegenConfig).value)
    mappingConfig.setResolverParentInterface((parentInterfaces in GraphQLCodegenConfig).value.resolver)
    mappingConfig.setQueryResolverParentInterface((parentInterfaces in GraphQLCodegenConfig).value.queryResolver)
    mappingConfig.setMutationResolverParentInterface((parentInterfaces in GraphQLCodegenConfig).value.mutationResolver)
    mappingConfig.setSubscriptionResolverParentInterface((parentInterfaces in GraphQLCodegenConfig).value.subscriptionResolver)
    mappingConfig.setApiReturnType((apiReturnType in GraphQLCodegenConfig).value.orNull)
    mappingConfig.setApiReturnListType((apiReturnListType in GraphQLCodegenConfig).value.orNull)
    mappingConfig.setDirectiveAnnotationsMapping((directiveAnnotationsMapping in GraphQLCodegenConfig).value)
    mappingConfig.setApiInterfaceStrategy((apiInterfaceStrategy in GraphQLCodegenConfig).value)
    mappingConfig.setUseOptionalForNullableReturnTypes((useOptionalForNullableReturnTypes in GraphQLCodegenConfig).value)
    mappingConfig.setResponseProjectionMaxDepth((responseProjectionMaxDepth in GraphQLCodegenConfig).value)

    sLog.value.debug(s"Current mapping config is <$mappingConfig>")
    mappingConfig
  }

  override lazy val projectSettings: Seq[Def.Setting[_]] = inConfig(GraphQLCodegenConfig) {
    Seq(
      generateCodegenTargetPath := crossTarget.value / "src_managed_graphql",
      sourceManaged := (generateCodegenTargetPath in GraphQLCodegenConfig).value,
      javaSource in configuration := (sourceManaged in GraphQLCodegenConfig).value,
      managedSourceDirectories in configuration ++= Seq((sourceManaged in GraphQLCodegenConfig).value),
      managedClasspath := {
        Classpaths.managedJars(GraphQLCodegenConfig, (classpathTypes in GraphQLCodegenConfig).value, (update in GraphQLCodegenConfig).value)
      },
      outputDir := {
        val file = (javaSource in configuration).value
        if (!file.exists()) {
          file.mkdirs()
        }
        sLog.value.info(s"Default outputDir is <${file.getAbsolutePath}>")
        file
      }, //use validate that config in build.sbt
      graphqlCodegenValidate := {
        val schemas = if ((graphqlSchemaPaths in GraphQLCodegenConfig).value.isEmpty) {
          Seq(((resourceDirectory in configuration).value / "schema.graphql").getCanonicalPath).asJava
        } else {
          (graphqlSchemaPaths in GraphQLCodegenConfig).value.asJava
        }
        new GraphQLCodegenValidate(schemas).validate() //use validate at terminal by user
      },
      graphqlSchemaValidate := {
        //use by user
        val args: Seq[String] = spaceDelimited("<arg>").parsed
        new GraphQLCodegenValidate(args.asJava).validate()
        args.foreach(a ⇒ sLog.value.info(s"Obtain args <$a>"))
        args
      }, graphqlCodegen := {
        val mappingConfigSupplier: JsonMappingConfigSupplier = buildJsonSupplier((jsonConfigurationFile in GraphQLCodegenConfig).value.orNull)
        var result: Seq[File] = Seq.empty
        try {
          result = new GraphQLCodegen(
            getSchemas,
            (graphqlQueryIntrospectionResultPath in GraphQLCodegenConfig).value.orNull,
            (outputDir in GraphQLCodegenConfig).value,
            getMappingConfig().value,
            mappingConfigSupplier).generate.asScala
          for (file ← result) {
            sLog.value.success(s"${file.getName}")
          }
        } catch {
          case e: Exception ⇒
            throw new Exception(s"${e.getLocalizedMessage}")
        }

        def getSchemas: util.List[String] = {
          if ((graphqlSchemaPaths in GraphQLCodegenConfig).value != null && (graphqlSchemaPaths in GraphQLCodegenConfig).value.nonEmpty)
            return (graphqlSchemaPaths in GraphQLCodegenConfig).value.asJava
          val schemasRootDir: Path = getSchemasRootDir
          val finder: SchemaFinder = new SchemaFinder(schemasRootDir)
          finder.setRecursive((graphqlSchemas in GraphQLCodegenConfig).value.recursive)
          finder.setIncludePattern((graphqlSchemas in GraphQLCodegenConfig).value.includePattern)
          finder.setExcludedFiles((graphqlSchemas in GraphQLCodegenConfig).value.excludedFiles.asJava)
          finder.findSchemas
        }

        def getSchemasRootDir: Path = {
          val rootDir = (graphqlSchemas in GraphQLCodegenConfig).value.rootDir
          if (rootDir == null) {
            val default = getDefaultResourcesDirectory
            if (default == null) throw new IllegalStateException("Default resource folder not found, please provide <rootDir> in <graphqlSchemas>")
            else return default
          }
          Paths.get(rootDir)
        }

        def getDefaultResourcesDirectory: Path = {
          val file = (resourceDirectory in configuration).value
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
    ) ++ watchSourcesSetting ++ Seq(cleanFiles += (generateCodegenTargetPath in GraphQLCodegenConfig).value)
  }

  private def buildJsonSupplier(jsonConfigurationFile: String): JsonMappingConfigSupplier = {
    if (jsonConfigurationFile != null && jsonConfigurationFile.nonEmpty) new JsonMappingConfigSupplier(jsonConfigurationFile) else null
  }

}
