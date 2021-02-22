package io.github.dreamylost.graphql.codegen

import com.kobylynskyi.graphql.codegen.GraphQLCodegenValidate
import com.kobylynskyi.graphql.codegen.java.JavaGraphQLCodegen
import com.kobylynskyi.graphql.codegen.model._
import com.kobylynskyi.graphql.codegen.model.exception.LanguageNotSupportedException
import com.kobylynskyi.graphql.codegen.model.GeneratedLanguage._
import com.kobylynskyi.graphql.codegen.scala.ScalaGraphQLCodegen
import com.kobylynskyi.graphql.codegen.supplier.{ JsonMappingConfigSupplier, SchemaFinder }
import sbt.{ AutoPlugin, PluginTrigger, _ }
import sbt.Keys.{ sLog, sourceManaged, _ }
import sbt.internal.util.complete.DefaultParsers.spaceDelimited

import java.nio.file.{ Path, Paths }
import java.util.{ HashMap => JHashMap, HashSet => JHashSet, List => JList }
import scala.collection.JavaConverters._
import sbt.Def

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

  private val jValidation = BuildInfo.jValidationVersion
  private val codegen = BuildInfo.version

  object GlobalImport extends GraphQLCodegenKeys {

    lazy val GraphQLCodegenPluginDependencies: Def.Setting[Seq[ModuleID]] = libraryDependencies ++= Seq(
      "javax.validation" % "validation-api" % javaxValidationApiVersion.value.getOrElse(jValidation),
      "io.github.kobylynskyi" % "graphql-java-codegen" % graphqlJavaCodegenVersion.value.getOrElse(codegen)
    )

    lazy val schemaFinderConfig: SchemaFinderConfig = SchemaFinderConfig(null)
    lazy val parentInterfacesConfig: ParentInterfacesConfig = ParentInterfacesConfig()
    lazy val defaultRelayConfig = new RelayConfig() //for auto import which can change it by `set` methods.
    lazy val GraphQLCodegenConfig = self.GraphQLCodegenConfig

  }

  override def trigger: PluginTrigger = noTrigger

  override def requires = sbt.plugins.JvmPlugin

  override def projectConfigurations: Seq[Configuration] = GraphQLCodegenConfig :: Nil

  import GlobalImport._

  //With the implementation of some other plugins, initialization is not necessary,
  //but maybe should be related to the dependency of key. For convenience, this is a conservative operation.
  override lazy val globalSettings: Seq[Def.Setting[_]] = Seq(
    graphqlQueryIntrospectionResultPath := None,
    graphqlSchemas := schemaFinderConfig,
    jsonConfigurationFile := None,
    graphqlSchemaPaths := Seq.empty,
    graphqlSchemaValidate := Seq.empty,
    customTypesMapping := new JHashMap[String, String](), //TODO use scala Map, convert to java Map
    customAnnotationsMapping := new JHashMap[String, JList[String]](),
    directiveAnnotationsMapping := new JHashMap[String, JList[String]](),
    javaxValidationApiVersion := None,
    graphqlJavaCodegenVersion := None,
    // suffix/prefix/strategies:
    generateModelOpenClasses := MappingConfigConstants.DEFAULT_GENERATE_MODEL_OPEN_CLASSES,
    generatedLanguage := MappingConfigConstants.DEFAULT_GENERATED_LANGUAGE,
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
    useObjectMapperForRequestSerialization := new JHashSet[String](),
    typeResolverPrefix := None,
    typeResolverSuffix := MappingConfigConstants.DEFAULT_RESOLVER_SUFFIX,
    subscriptionReturnType := None,
    modelValidationAnnotation := MappingConfigConstants.DEFAULT_VALIDATION_ANNOTATION,
    apiReturnType := None,
    apiReturnListType := None,
    apiInterfaceStrategy := MappingConfigConstants.DEFAULT_API_INTERFACE_STRATEGY,
    useOptionalForNullableReturnTypes := MappingConfigConstants.DEFAULT_USE_OPTIONAL_FOR_NULLABLE_RETURN_TYPES,
    generateApisWithThrowsException := MappingConfigConstants.DEFAULT_GENERATE_APIS_WITH_THROWS_EXCEPTION,
    addGeneratedAnnotation := MappingConfigConstants.DEFAULT_ADD_GENERATED_ANNOTATION,
    relayConfig := defaultRelayConfig,
    // package name configs:
    apiPackageName := None,
    modelPackageName := None,
    // field resolvers configs:
    fieldsWithResolvers := new JHashSet[String](),
    fieldsWithoutResolvers := new JHashSet[String](),
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
    generateImmutableModels := MappingConfigConstants.DEFAULT_GENERATE_IMMUTABLE_MODELS, // TODO change default value
    generateToString := MappingConfigConstants.DEFAULT_TO_STRING,
    // parent interfaces configs:
    parentInterfaces := parentInterfacesConfig,
    responseProjectionMaxDepth := MappingConfigConstants.DEFAULT_RESPONSE_PROJECTION_MAX_DEPTH
  )

  private def getMappingConfig(): Def.Initialize[MappingConfig] = Def.setting {
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
    mappingConfig.setUseObjectMapperForRequestSerialization((useObjectMapperForRequestSerialization in GraphQLCodegenConfig).value)
    mappingConfig.setResolverParentInterface((parentInterfaces in GraphQLCodegenConfig).value.resolver)
    mappingConfig.setQueryResolverParentInterface((parentInterfaces in GraphQLCodegenConfig).value.queryResolver)
    mappingConfig.setMutationResolverParentInterface((parentInterfaces in GraphQLCodegenConfig).value.mutationResolver)
    mappingConfig.setSubscriptionResolverParentInterface((parentInterfaces in GraphQLCodegenConfig).value.subscriptionResolver)
    mappingConfig.setApiReturnType((apiReturnType in GraphQLCodegenConfig).value.orNull)
    mappingConfig.setApiReturnListType((apiReturnListType in GraphQLCodegenConfig).value.orNull)
    mappingConfig.setDirectiveAnnotationsMapping((directiveAnnotationsMapping in GraphQLCodegenConfig).value)
    mappingConfig.setApiInterfaceStrategy((apiInterfaceStrategy in GraphQLCodegenConfig).value)
    mappingConfig.setUseOptionalForNullableReturnTypes((useOptionalForNullableReturnTypes in GraphQLCodegenConfig).value)
    mappingConfig.setGenerateApisWithThrowsException((generateApisWithThrowsException in GraphQLCodegenConfig).value)
    mappingConfig.setAddGeneratedAnnotation((addGeneratedAnnotation in GraphQLCodegenConfig).value)
    mappingConfig.setResponseProjectionMaxDepth((responseProjectionMaxDepth in GraphQLCodegenConfig).value)
    mappingConfig.setRelayConfig((relayConfig in GraphQLCodegenConfig).value)
    mappingConfig.setGeneratedLanguage((generatedLanguage in GraphQLCodegenConfig).value)
    mappingConfig.setGenerateModelOpenClasses((generateModelOpenClasses in GraphQLCodegenConfig).value)
    mappingConfig
  }

  override lazy val projectSettings: Seq[Def.Setting[_]] = inConfig(GraphQLCodegenConfig) {
    Seq(
      // `generateCodegenTargetPath` not support playframework, https://github.com/kobylynskyi/graphql-java-codegen/issues/551
      // There may be some problems that have not been found at present :)
      generateCodegenTargetPath := crossTarget.value / "src_managed_graphql",
      sourceManaged := generateCodegenTargetPath.value,
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
        sLog.value.info(s"current outputDir: ${file.getAbsolutePath}")
        file
      },
      graphqlCodegenValidate := {
        val schemas = if (graphqlSchemaPaths.value.isEmpty) {
          Seq(((resourceDirectory in configuration).value / "schema.graphql").getCanonicalPath).asJava
        } else {
          graphqlSchemaPaths.value.asJava
        }
        new GraphQLCodegenValidate(schemas).validate()
      },
      graphqlSchemaValidate := {
        val args: Seq[String] = spaceDelimited("<arg>").parsed
        new GraphQLCodegenValidate(args.asJava).validate()
        args.foreach(a ⇒ sLog.value.info(s"obtain args: $a"))
        args
      }, graphqlCodegen := {
        sLog.value.info(s"Generating files: ${BuildInfo.toString}")
        val mappingConfigSupplier = buildJsonSupplier(jsonConfigurationFile.value.orNull)
        var result = Seq.empty[File]
        try {
          val _outputDir = outputDir.value
          val _introspectionResult = graphqlQueryIntrospectionResultPath.value.orNull
          lazy val instantiateCodegen = (mappingConfig: MappingConfig) => {
            generatedLanguage.value match {
              case JAVA =>
                new JavaGraphQLCodegen(getSchemas(), _introspectionResult, _outputDir, mappingConfig, mappingConfigSupplier)
              case SCALA =>
                new ScalaGraphQLCodegen(getSchemas(), _introspectionResult, _outputDir, mappingConfig, mappingConfigSupplier)
              case _ =>
                throw new LanguageNotSupportedException(generatedLanguage.value)
            }
          }
          result = instantiateCodegen(getMappingConfig().value).generate.asScala
          for (file ← result) {
            sLog.value.info(s"${file.getName}")
          }
          sLog.value.success(s"Total files: ${result.length}")
        } catch {
          case e: Exception ⇒
            (logLevel in configuration).?.value.orElse(state.value.get(logLevel.key)) match {
              case Some(Level.Debug) => e.printStackTrace()
              case _                 => throw new Exception(s"${e.getLocalizedMessage}")
            }
        }

        def getSchemas(): JList[String] = {
          if (graphqlSchemaPaths.value != null &&
            graphqlSchemaPaths.value.nonEmpty) {
            graphqlSchemaPaths.value.asJava
          } else if (graphqlQueryIntrospectionResultPath.value != null &&
            graphqlQueryIntrospectionResultPath.value.isDefined) {
            Seq.empty[String].asJava
          } else {
            val schemasRootDir = getSchemasRootDir
            val finder = new SchemaFinder(schemasRootDir)
            finder.setRecursive(graphqlSchemas.value.recursive)
            finder.setIncludePattern(graphqlSchemas.value.includePattern)
            finder.setExcludedFiles(graphqlSchemas.value.excludedFiles.asJava)
            finder.findSchemas
          }
        }

        def getSchemasRootDir: Path = {
          val rootDir = graphqlSchemas.value.rootDir
          if (rootDir == null) {
            val default = getDefaultResourcesDirectory()
            if (default == null)
              throw new IllegalStateException("Default resource folder not found, please provide <rootDir> in <graphqlSchemas>")
            else default
          } else {
            Paths.get(rootDir)
          }
        }

        def getDefaultResourcesDirectory(): Path = {
          val file = (resourceDirectory in configuration).value
          if (!file.exists()) {
            file.mkdirs()
          }
          val path = Paths.get(file.getPath)
          sLog.value.info(s"default resources path: $path")
          path
        }

        result
      }
    //watch graphql schema source, I'm not sure if this will be mutually exclusive with the deletion of codegen.
    ) ++ watchSourcesSetting ++ Seq(cleanFiles += generateCodegenTargetPath.value)
  }

  protected def buildJsonSupplier(jsonConfigurationFile: String): JsonMappingConfigSupplier = {
    if (jsonConfigurationFile != null && jsonConfigurationFile.nonEmpty)
      new JsonMappingConfigSupplier(jsonConfigurationFile) else null
  }

}
