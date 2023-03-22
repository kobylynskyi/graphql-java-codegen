package io.github.dreamylost.graphql.codegen

import com.kobylynskyi.graphql.codegen.GraphQLCodegenValidate
import com.kobylynskyi.graphql.codegen.java.JavaGraphQLCodegen
import com.kobylynskyi.graphql.codegen.model._
import com.kobylynskyi.graphql.codegen.model.exception.LanguageNotSupportedException
import com.kobylynskyi.graphql.codegen.model.GeneratedLanguage._
import com.kobylynskyi.graphql.codegen.scala.ScalaGraphQLCodegen
import com.kobylynskyi.graphql.codegen.supplier._
import sbt.{ AutoPlugin, PluginTrigger, _ }
import sbt.Keys.{ sLog, sourceManaged, _ }
import sbt.internal.util.complete.DefaultParsers.spaceDelimited
import com.kobylynskyi.graphql.codegen.kotlin.KotlinGraphQLCodegen

import java.nio.file.{ Path, Paths }
import java.util.{ HashMap => JHashMap, HashSet => JHashSet, List => JList }
import scala.collection.JavaConverters._
import sbt.Def

/** @author
 *    梦境迷离
 *  @version 1.0,2020/7/15
 */
object GraphQLCodegenPlugin extends GraphQLCodegenPlugin(Compile, configurationPostfix = "-main") {
  // for auto import
  val autoImport = GlobalImport
}

class GraphQLCodegenPlugin(configuration: Configuration, private[codegen] val configurationPostfix: String = "")
    extends AutoPlugin
    with Compat {
  self =>

  private val jValidation = BuildInfo.jValidationVersion
  private val codegen     = BuildInfo.version

  object GlobalImport extends GraphQLCodegenKeys {

    lazy val GraphQLCodegenPluginDependencies: Def.Setting[Seq[ModuleID]] = libraryDependencies ++= Seq(
      "javax.validation"      % "validation-api"       % javaxValidationApiVersion.value.getOrElse(jValidation),
      "io.github.kobylynskyi" % "graphql-java-codegen" % graphqlJavaCodegenVersion.value.getOrElse(codegen)
    )

    lazy val schemaFinderConfig: SchemaFinderConfig         = SchemaFinderConfig(null)
    lazy val parentInterfacesConfig: ParentInterfacesConfig = ParentInterfacesConfig()
    lazy val defaultRelayConfig   = new RelayConfig() // for auto import which can change it by `set` methods.
    lazy val GraphQLCodegenConfig = self.GraphQLCodegenConfig

  }

  override def trigger: PluginTrigger = noTrigger

  override def requires = sbt.plugins.JvmPlugin

  override def projectConfigurations: Seq[Configuration] = GraphQLCodegenConfig :: Nil

  import GlobalImport._

  // With the implementation of some other plugins, initialization is not necessary,
  // but maybe should be related to the dependency of key. For convenience, this is a conservative operation.
  override lazy val globalSettings: Seq[Def.Setting[_]] = Seq(
    graphqlQueryIntrospectionResultPath := None,
    graphqlSchemas                      := schemaFinderConfig,
    configurationFiles                  := Seq.empty[String],
    graphqlSchemaPaths                  := Seq.empty,
    graphqlSchemaValidate               := Seq.empty,
    generateJacksonTypeIdResolver       := MappingConfigConstants.DEFAULT_GENERATE_JACKSON_TYPE_ID_RESOLVER,
    customTypesMapping                  := new JHashMap[String, String](), // TODO use scala Map, convert to java Map
    customAnnotationsMapping            := new JHashMap[String, JList[String]](),
    customTemplates                     := new JHashMap[String, String](),
    directiveAnnotationsMapping         := new JHashMap[String, JList[String]](),
    javaxValidationApiVersion           := None,
    graphqlJavaCodegenVersion           := None,
    // suffix/prefix/strategies:
    generateModelOpenClasses := MappingConfigConstants.DEFAULT_GENERATE_MODEL_OPEN_CLASSES,
    generateSealedInterfaces := MappingConfigConstants.DEFAULT_GENERATE_SEALED_INTERFACES,
    generatedLanguage        := MappingConfigConstants.DEFAULT_GENERATED_LANGUAGE,
    apiNamePrefix            := None,
    apiNameSuffix            := MappingConfigConstants.DEFAULT_RESOLVER_SUFFIX,
    apiRootInterfaceStrategy := ApiRootInterfaceStrategy.valueOf(
      MappingConfigConstants.DEFAULT_API_ROOT_INTERFACE_STRATEGY_STRING
    ),
    apiNamePrefixStrategy := ApiNamePrefixStrategy.valueOf(
      MappingConfigConstants.DEFAULT_API_NAME_PREFIX_STRATEGY_STRING
    ),
    modelNamePrefix                        := None,
    modelNameSuffix                        := None,
    requestSuffix                          := MappingConfigConstants.DEFAULT_REQUEST_SUFFIX,
    responseSuffix                         := MappingConfigConstants.DEFAULT_RESPONSE_SUFFIX,
    responseProjectionSuffix               := MappingConfigConstants.DEFAULT_RESPONSE_PROJECTION_SUFFIX,
    parametrizedInputSuffix                := MappingConfigConstants.DEFAULT_PARAMETRIZED_INPUT_SUFFIX,
    useObjectMapperForRequestSerialization := new JHashSet[String](),
    typeResolverPrefix                     := None,
    typeResolverSuffix                     := MappingConfigConstants.DEFAULT_RESOLVER_SUFFIX,
    subscriptionReturnType                 := None,
    modelValidationAnnotation              := MappingConfigConstants.DEFAULT_VALIDATION_ANNOTATION,
    apiReturnType                          := None,
    apiReturnListType                      := None,
    apiInterfaceStrategy                   := MappingConfigConstants.DEFAULT_API_INTERFACE_STRATEGY,
    useOptionalForNullableReturnTypes      := MappingConfigConstants.DEFAULT_USE_OPTIONAL_FOR_NULLABLE_RETURN_TYPES,
    generateApisWithThrowsException        := MappingConfigConstants.DEFAULT_GENERATE_APIS_WITH_THROWS_EXCEPTION,
    addGeneratedAnnotation                 := MappingConfigConstants.DEFAULT_ADD_GENERATED_ANNOTATION,
    generatedAnnotation                    := None,
    typesAsInterfaces                      := new JHashSet[String](),
    relayConfig                            := defaultRelayConfig,
    // package name configs:
    apiPackageName   := None,
    modelPackageName := None,
    // field resolvers configs:
    fieldsWithResolvers    := new JHashSet[String](),
    fieldsWithoutResolvers := new JHashSet[String](),
    // various toggles:
    generateClient                       := MappingConfigConstants.DEFAULT_GENERATE_CLIENT,
    generateParameterizedFieldsResolvers := MappingConfigConstants.DEFAULT_GENERATE_PARAMETERIZED_FIELDS_RESOLVERS,
    generateExtensionFieldsResolvers     := MappingConfigConstants.DEFAULT_GENERATE_EXTENSION_FIELDS_RESOLVERS,
    generateDataFetchingEnvironmentArgumentInApis := MappingConfigConstants.DEFAULT_GENERATE_DATA_FETCHING_ENV,
    generateModelsForRootTypes                    := MappingConfigConstants.DEFAULT_GENERATE_MODELS_FOR_ROOT_TYPES,
    generatePackageName                           := None,
    generateBuilder                               := MappingConfigConstants.DEFAULT_BUILDER,
    generateApis                                  := MappingConfigConstants.DEFAULT_GENERATE_APIS,
    generateEqualsAndHashCode                     := MappingConfigConstants.DEFAULT_EQUALS_AND_HASHCODE,
    generateImmutableModels := MappingConfigConstants.DEFAULT_GENERATE_IMMUTABLE_MODELS, // TODO change default value
    generateToString        := MappingConfigConstants.DEFAULT_TO_STRING,
    // parent interfaces configs:
    parentInterfaces               := parentInterfacesConfig,
    generateAllMethodInProjection  := MappingConfigConstants.DEFAULT_GENERATE_ALL_METHOD,
    responseProjectionMaxDepth     := MappingConfigConstants.DEFAULT_RESPONSE_PROJECTION_MAX_DEPTH,
    supportUnknownFields           := MappingConfigConstants.DEFAULT_SUPPORT_UNKNOWN_FIELDS,
    unknownFieldsPropertyName      := MappingConfigConstants.DEFAULT_UNKNOWN_FIELDS_PROPERTY_NAME,
    generateNoArgsConstructorOnly  := MappingConfigConstants.DEFAULT_GENERATE_NOARGS_CONSTRUCTOR_ONLY,
    generateModelsWithPublicFields := MappingConfigConstants.DEFAULT_GENERATE_MODELS_WITH_PUBLIC_FIELDS,
    skip                           := false
  )

  private def getMappingConfig(): Def.Initialize[MappingConfig] = Def.setting {
    val mappingConfig = new MappingConfig
    mappingConfig.setPackageName((GraphQLCodegenConfig / generatePackageName).value.orNull)
    mappingConfig.setCustomTypesMapping((GraphQLCodegenConfig / customTypesMapping).value)
    mappingConfig.setApiNameSuffix((GraphQLCodegenConfig / apiNameSuffix).value)
    mappingConfig.setApiNamePrefix((GraphQLCodegenConfig / apiNamePrefix).value.orNull)
    mappingConfig.setApiRootInterfaceStrategy((GraphQLCodegenConfig / apiRootInterfaceStrategy).value)
    mappingConfig.setApiNamePrefixStrategy((GraphQLCodegenConfig / apiNamePrefixStrategy).value)
    mappingConfig.setModelNamePrefix((GraphQLCodegenConfig / modelNamePrefix).value.orNull)
    mappingConfig.setModelNameSuffix((GraphQLCodegenConfig / modelNameSuffix).value.orNull)
    mappingConfig.setApiPackageName((GraphQLCodegenConfig / apiPackageName).value.orNull)
    mappingConfig.setModelPackageName((GraphQLCodegenConfig / modelPackageName).value.orNull)
    mappingConfig.setGenerateBuilder((GraphQLCodegenConfig / generateBuilder).value)
    mappingConfig.setGenerateApis((GraphQLCodegenConfig / generateApis).value)
    mappingConfig.setTypeResolverSuffix((GraphQLCodegenConfig / typeResolverSuffix).value)
    mappingConfig.setTypeResolverPrefix((GraphQLCodegenConfig / typeResolverPrefix).value.orNull)
    mappingConfig.setModelValidationAnnotation((GraphQLCodegenConfig / modelValidationAnnotation).value)
    mappingConfig.setCustomAnnotationsMapping((GraphQLCodegenConfig / customAnnotationsMapping).value)
    mappingConfig.setCustomTemplates((GraphQLCodegenConfig / customTemplates).value)
    mappingConfig.setGenerateEqualsAndHashCode((GraphQLCodegenConfig / generateEqualsAndHashCode).value)
    mappingConfig.setGenerateImmutableModels((GraphQLCodegenConfig / generateImmutableModels).value)
    mappingConfig.setGenerateToString((GraphQLCodegenConfig / generateToString).value)
    mappingConfig.setSubscriptionReturnType((GraphQLCodegenConfig / subscriptionReturnType).value.orNull)
    mappingConfig.setGenerateParameterizedFieldsResolvers(
      (GraphQLCodegenConfig / generateParameterizedFieldsResolvers).value
    )
    mappingConfig.setGenerateDataFetchingEnvironmentArgumentInApis(
      (GraphQLCodegenConfig / generateDataFetchingEnvironmentArgumentInApis).value
    )
    mappingConfig.setGenerateExtensionFieldsResolvers((GraphQLCodegenConfig / generateExtensionFieldsResolvers).value)
    mappingConfig.setGenerateModelsForRootTypes((GraphQLCodegenConfig / generateModelsForRootTypes).value)
    mappingConfig.setFieldsWithResolvers((GraphQLCodegenConfig / fieldsWithResolvers).value)
    mappingConfig.setFieldsWithoutResolvers((GraphQLCodegenConfig / fieldsWithoutResolvers).value)
    mappingConfig.setTypesAsInterfaces((GraphQLCodegenConfig / typesAsInterfaces).value)
    mappingConfig.setGenerateClient((GraphQLCodegenConfig / generateClient).value)
    mappingConfig.setRequestSuffix((GraphQLCodegenConfig / requestSuffix).value)
    mappingConfig.setResponseSuffix((GraphQLCodegenConfig / responseSuffix).value)
    mappingConfig.setResponseProjectionSuffix((GraphQLCodegenConfig / responseProjectionSuffix).value)
    mappingConfig.setParametrizedInputSuffix((GraphQLCodegenConfig / parametrizedInputSuffix).value)
    mappingConfig.setUseObjectMapperForRequestSerialization(
      (GraphQLCodegenConfig / useObjectMapperForRequestSerialization).value
    )
    mappingConfig.setResolverParentInterface((GraphQLCodegenConfig / parentInterfaces).value.resolver)
    mappingConfig.setQueryResolverParentInterface((GraphQLCodegenConfig / parentInterfaces).value.queryResolver)
    mappingConfig.setMutationResolverParentInterface((GraphQLCodegenConfig / parentInterfaces).value.mutationResolver)
    mappingConfig.setSubscriptionResolverParentInterface(
      (GraphQLCodegenConfig / parentInterfaces).value.subscriptionResolver
    )
    mappingConfig.setApiReturnType((GraphQLCodegenConfig / apiReturnType).value.orNull)
    mappingConfig.setApiReturnListType((GraphQLCodegenConfig / apiReturnListType).value.orNull)
    mappingConfig.setDirectiveAnnotationsMapping((GraphQLCodegenConfig / directiveAnnotationsMapping).value)
    mappingConfig.setApiInterfaceStrategy((GraphQLCodegenConfig / apiInterfaceStrategy).value)
    mappingConfig.setUseOptionalForNullableReturnTypes((GraphQLCodegenConfig / useOptionalForNullableReturnTypes).value)
    mappingConfig.setGenerateApisWithThrowsException((GraphQLCodegenConfig / generateApisWithThrowsException).value)
    mappingConfig.setAddGeneratedAnnotation((GraphQLCodegenConfig / addGeneratedAnnotation).value)
    mappingConfig.setGeneratedAnnotation((GraphQLCodegenConfig / generatedAnnotation).value.orNull)
    mappingConfig.setGenerateAllMethodInProjection((GraphQLCodegenConfig / generateAllMethodInProjection).value)
    mappingConfig.setResponseProjectionMaxDepth((GraphQLCodegenConfig / responseProjectionMaxDepth).value)
    mappingConfig.setRelayConfig((GraphQLCodegenConfig / relayConfig).value)
    mappingConfig.setGeneratedLanguage((GraphQLCodegenConfig / generatedLanguage).value)
    mappingConfig.setGenerateModelOpenClasses((GraphQLCodegenConfig / generateModelOpenClasses).value)
    mappingConfig.setGenerateJacksonTypeIdResolver((GraphQLCodegenConfig / generateJacksonTypeIdResolver).value);
    mappingConfig.setGenerateNoArgsConstructorOnly((GraphQLCodegenConfig / generateNoArgsConstructorOnly).value);
    mappingConfig.setGenerateModelsWithPublicFields((GraphQLCodegenConfig / generateModelsWithPublicFields).value);

    mappingConfig.setSupportUnknownFields((GraphQLCodegenConfig / supportUnknownFields).value)
    mappingConfig.setUnknownFieldsPropertyName((GraphQLCodegenConfig / unknownFieldsPropertyName).value)

    mappingConfig
  }

  override lazy val projectSettings: Seq[Def.Setting[_]] = inConfig(GraphQLCodegenConfig) {
    Seq(
      // `generateCodegenTargetPath` not support playframework, https://github.com/kobylynskyi/graphql-java-codegen/issues/551
      // There may be some problems that have not been found at present :)
      generateCodegenTargetPath  := crossTarget.value / "src_managed_graphql",
      sourceManaged              := generateCodegenTargetPath.value,
      configuration / javaSource := (GraphQLCodegenConfig / sourceManaged).value,
      configuration / managedSourceDirectories ++= Seq((GraphQLCodegenConfig / sourceManaged).value),
      managedClasspath := {
        Classpaths.managedJars(
          GraphQLCodegenConfig,
          (GraphQLCodegenConfig / classpathTypes).value,
          (GraphQLCodegenConfig / update).value
        )
      },
      outputDir := {
        val file = (configuration / javaSource).value
        if (!file.exists()) {
          file.mkdirs()
        }
        sLog.value.info(s"current outputDir: ${file.getAbsolutePath}")
        file
      },
      graphqlCodegenValidate := {
        val schemas = if (graphqlSchemaPaths.value.isEmpty) {
          Seq(((configuration / resourceDirectory).value / "schema.graphql").getCanonicalPath).asJava
        } else {
          graphqlSchemaPaths.value.asJava
        }
        new GraphQLCodegenValidate(schemas).validate()
      },
      graphqlSchemaValidate := {
        val args: Seq[String] = spaceDelimited("<arg>").parsed
        new GraphQLCodegenValidate(args.asJava).validate()
        args.foreach(a => sLog.value.info(s"obtain args: $a"))
        args
      },
      graphqlCodegen := {
        sLog.value.info(s"Generating files: ${BuildInfo.toString}")
        val mappingConfigSupplier = buildJsonSupplier(configurationFiles.value)
        val language = mappingConfigSupplier.map(_.get()).map(_.getGeneratedLanguage).getOrElse(generatedLanguage.value)
        var result   = Seq.empty[File]
        try {
          val _outputDir           = outputDir.value
          val _introspectionResult = graphqlQueryIntrospectionResultPath.value.orNull
          lazy val instantiateCodegen = (mappingConfig: MappingConfig) =>
            language match {
              case JAVA =>
                new JavaGraphQLCodegen(
                  getSchemas(),
                  _introspectionResult,
                  _outputDir,
                  mappingConfig,
                  mappingConfigSupplier.orNull
                )
              case SCALA =>
                new ScalaGraphQLCodegen(
                  getSchemas(),
                  _introspectionResult,
                  _outputDir,
                  mappingConfig,
                  mappingConfigSupplier.orNull
                )
              case KOTLIN =>
                new KotlinGraphQLCodegen(
                  getSchemas(),
                  _introspectionResult,
                  _outputDir,
                  mappingConfig,
                  mappingConfigSupplier.orNull
                )
              case _ =>
                throw new LanguageNotSupportedException(language)
            }
          if (skip.value) {
            sLog.value.info("Skipping code generation")
          } else {
            result = instantiateCodegen(getMappingConfig().value: @sbtUnchecked).generate.asScala
            for (file <- result)
              sLog.value.info(s"${file.getName}")
            sLog.value.success(s"Total files: ${result.length}")
          }
        } catch {
          case e: Exception =>
            (configuration / logLevel).?.value.orElse(state.value.get(logLevel.key)) match {
              case Some(Level.Debug) => e.printStackTrace()
              case _                 => throw new Exception(s"${e.getLocalizedMessage}")
            }
        }

        def getSchemas(): JList[String] =
          if (
            graphqlSchemaPaths.value != null &&
            graphqlSchemaPaths.value.nonEmpty
          ) {
            graphqlSchemaPaths.value.asJava
          } else if (
            graphqlQueryIntrospectionResultPath.value != null &&
            graphqlQueryIntrospectionResultPath.value.isDefined
          ) {
            Seq.empty[String].asJava
          } else {
            val schemasRootDir = getSchemasRootDir
            val finder         = new SchemaFinder(schemasRootDir)
            finder.setRecursive(graphqlSchemas.value.recursive)
            finder.setIncludePattern(graphqlSchemas.value.includePattern)
            finder.setExcludedFiles(graphqlSchemas.value.excludedFiles.asJava)
            finder.findSchemas
          }

        def getSchemasRootDir: Path = {
          val rootDir = graphqlSchemas.value.rootDir
          if (rootDir == null) {
            val default = getDefaultResourcesDirectory()
            if (default == null)
              throw new IllegalStateException(
                "Default resource folder not found, please provide <rootDir> in <graphqlSchemas>"
              )
            else default
          } else {
            Paths.get(rootDir)
          }
        }

        def getDefaultResourcesDirectory(): Path = {
          val file = (configuration / resourceDirectory).value
          if (!file.exists()) {
            file.mkdirs()
          }
          val path = Paths.get(file.getPath)
          sLog.value.info(s"default resources path: $path")
          path
        }

        result
      }
      // watch graphql schema source, I'm not sure if this will be mutually exclusive with the deletion of codegen.
    ) ++ watchSourcesSetting ++ Seq(cleanFiles += generateCodegenTargetPath.value)
  }

  protected def buildJsonSupplier(configurationFiles: Seq[String]): Option[MergeableMappingConfigSupplier] =
    if (configurationFiles != null && configurationFiles.nonEmpty)
      Some(new MergeableMappingConfigSupplier(configurationFiles.asJava))
    else None

}
