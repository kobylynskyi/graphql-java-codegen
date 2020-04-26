package io.github.kobylynskyi.graphql.codegen

import com.kobylynskyi.graphql.codegen.GraphQLCodegen
import com.kobylynskyi.graphql.codegen.model.DefaultMappingConfigValues;
import com.kobylynskyi.graphql.codegen.model.MappingConfig
import com.kobylynskyi.graphql.codegen.supplier.MappingConfigSupplier
import com.kobylynskyi.graphql.codegen.supplier.SchemaFinder

import scala.collection.JavaConverters

import sbt._
import sbt.Keys._

object GraphQLCodegenSbtPlugin extends AutoPlugin {
  // by defining autoImport, the settings are automatically imported into user's `*.sbt`
  object autoImport {
    // configuration points, like the built-in `version`, `libraryDependencies`, or `compile`
    val graphql = taskKey[Seq[File]]("Generate GraphQL code.")
    val graphqlSchemaPaths = settingKey[Seq[String]]("Locations of GraphQL schemas.")
    val graphqlModelNamePrefix = settingKey[String]("Suffix to append to the model class names.")
    val graphqlModelNameSuffix = settingKey[String]("Suffix to append to the model class names.")
    val graphqlApiPackageName = settingKey[String]("Java package to use when generating the API classes.")
    val graphqlModelPackageName = settingKey[String]("Java package to use when generating the model classes.")
    val graphqlGenerateBuilder = settingKey[Boolean]("Specifies whether generated model classes should have builder.")
    val graphqlGenerateApis = settingKey[Boolean]("Specifies whether api classes should be generated as well as model classes.")
    val graphqlGenerateEqualsAndHashCode = settingKey[Boolean]("Specifies whether generated model classes should have equals and hashCode methods defined.")
    val graphqlGenerateToString = settingKey[Boolean]("Specifies whether generated model classes should have toString method defined.")
    val graphqlGenerateAsyncApi = settingKey[Boolean]("If true, then wrap type into java.util.concurrent.CompletableFuture or subscriptionReturnType")
    val graphqlModelValidationAnnotation = settingKey[String]("Annotation for mandatory (NonNull) fields. Can be null/empty.")
    val graphqlGenerateParameterizedFieldsResolvers = settingKey[Boolean]("If true, then generate separate Resolver interface for parametrized fields. If false, then add field to the type definition and ignore field parameters.")
    val graphqlGenerateExtensionFieldsResolvers = settingKey[Boolean]("Specifies whether all fields in extensions (extend type and extend interface) should be present in Resolver interface instead of the type class itself.")
    val graphqlGenerateDataFetchingEnvArgInApis = settingKey[Boolean]("If true, then graphql.schema.DataFetchingEnvironment env will be added as a last argument to all methods of root type resolvers and field resolvers.")
    val graphqlGenerateRequests = settingKey[Boolean]("Specifies whether client-side classes should be generated for each query, mutation and subscription. This includes: Request class (contains input data) and ResponseProjection class (contains response fields).")

    val graphqlRequestSuffix = settingKey[String]("Specifies whether client-side classes should be generated for each query, mutation and subscription. This includes: Request class (contains input data) and ResponseProjection class (contains response fields).")
    val graphqlResponseProjectionSuffix = settingKey[String]("Specifies whether client-side classes should be generated for each query, mutation and subscription. This includes: Request class (contains input data) and ResponseProjection class (contains response fields).")

    // default values for the tasks and settings
    lazy val baseGraphQLSettings: Seq[Def.Setting[_]] = Seq(
      graphql := {
        Codegen(
          (sourceManaged in graphql).value,
          (graphqlSchemaPaths in graphql).value,
          sourceDirectory.value / "resources",
          (graphqlModelNamePrefix in graphql).value,
          (graphqlModelNameSuffix in graphql).value,
          (graphqlApiPackageName in graphql).value,
          (graphqlModelPackageName in graphql).value,
          (graphqlGenerateBuilder in graphql).value,
          (graphqlGenerateApis in graphql).value,
          (graphqlModelValidationAnnotation in graphql).value,
          (graphqlGenerateEqualsAndHashCode in graphql).value,
          (graphqlGenerateToString in graphql).value,
          (graphqlGenerateAsyncApi in graphql).value,
          (graphqlGenerateParameterizedFieldsResolvers in graphql).value,
          (graphqlGenerateExtensionFieldsResolvers in graphql).value,
          (graphqlGenerateDataFetchingEnvArgInApis in graphql).value,
          (graphqlGenerateRequests in graphql).value,
          (graphqlRequestSuffix in graphql).value,
          (graphqlResponseProjectionSuffix in graphql).value
        )
      },
      graphqlSchemaPaths in graphql := Seq((sourceDirectory.value / "resources/schema.graphql").getCanonicalPath),
      graphqlGenerateBuilder in graphql := DefaultMappingConfigValues.DEFAULT_BUILDER,
      graphqlGenerateApis in graphql := DefaultMappingConfigValues.DEFAULT_GENERATE_APIS,
      graphqlModelValidationAnnotation in graphql := DefaultMappingConfigValues.DEFAULT_VALIDATION_ANNOTATION,
      graphqlGenerateEqualsAndHashCode in graphql := DefaultMappingConfigValues.DEFAULT_EQUALS_AND_HASHCODE,
      graphqlGenerateToString in graphql := DefaultMappingConfigValues.DEFAULT_TO_STRING,
      graphqlGenerateAsyncApi in graphql := DefaultMappingConfigValues.DEFAULT_GENERATE_ASYNC_APIS,
      graphqlGenerateParameterizedFieldsResolvers in graphql := DefaultMappingConfigValues.DEFAULT_GENERATE_PARAMETERIZED_FIELDS_RESOLVERS,
      graphqlGenerateExtensionFieldsResolvers in graphql := DefaultMappingConfigValues.DEFAULT_GENERATE_EXTENSION_FIELDS_RESOLVERS,
      graphqlGenerateDataFetchingEnvArgInApis in graphql := DefaultMappingConfigValues.DEFAULT_GENERATE_DATA_FETCHING_ENV,
      graphqlGenerateRequests in graphql := DefaultMappingConfigValues.DEFAULT_GENERATE_REQUESTS,
      graphqlRequestSuffix in graphql := DefaultMappingConfigValues.DEFAULT_REQUEST_SUFFIX,
      graphqlResponseProjectionSuffix in graphql := DefaultMappingConfigValues.DEFAULT_RESPONSE_PROJECTION_SUFFIX,

      // This follows the output directory structure recommended by sbt team
      // https://github.com/sbt/sbt/issues/1664#issuecomment-213057686
      sourceManaged in graphql := crossTarget.value / "src_managed_graphql",
      managedSourceDirectories in Compile += (sourceManaged in graphql).value,
      Compile / sourceGenerators += graphql.taskValue
    )
  }

  import autoImport._
  override def requires = sbt.plugins.JvmPlugin

  // This plugin is automatically enabled for projects which are JvmPlugin.
  override def trigger = allRequirements

  // a group of settings that are automatically added to projects.
  override val projectSettings =
    inConfig(Compile)(baseGraphQLSettings) ++
    inConfig(Test)(baseGraphQLSettings)
}

object Codegen {
  def apply(
      outputDir: File,
      graphqlSchemaPaths: Seq[String],
      schemasRootDir: File,
      graphqlModelNamePrefix: String,
      graphqlModelNameSuffix: String,
      graphqlApiPackageName: String,
      graphqlModelPackageName: String,
      graphqlGenerateBuilder: Boolean,
      graphqlGenerateApis: Boolean,
      graphqlModelValidationAnnotation: String,
      graphqlGenerateEqualsAndHashCode: Boolean,
      graphqlGenerateToString: Boolean,
      graphqlGenerateAsyncApi: Boolean,
      graphqlGenerateParameterizedFieldsResolvers: Boolean,
      graphqlGenerateExtensionFieldsResolvers: Boolean,
      graphqlGenerateDataFetchingEnvArgInApis: Boolean,
      graphqlGenerateRequests: Boolean,
      graphqlRequestSuffix: String,
      graphqlResponseProjectionSuffix: String): Seq[File] = {

    val mappingConfig = new MappingConfig();
    mappingConfig.setModelNamePrefix(graphqlModelNamePrefix);
    mappingConfig.setModelNameSuffix(graphqlModelNameSuffix);
    mappingConfig.setApiPackageName(graphqlApiPackageName);
    mappingConfig.setModelPackageName(graphqlModelPackageName);
    mappingConfig.setGenerateBuilder(graphqlGenerateBuilder);
    mappingConfig.setGenerateApis(graphqlGenerateApis);
    mappingConfig.setModelValidationAnnotation(graphqlModelValidationAnnotation);
    mappingConfig.setGenerateEqualsAndHashCode(graphqlGenerateEqualsAndHashCode);
    mappingConfig.setGenerateToString(graphqlGenerateToString);
    mappingConfig.setGenerateAsyncApi(graphqlGenerateAsyncApi);
    mappingConfig.setGenerateParameterizedFieldsResolvers(graphqlGenerateParameterizedFieldsResolvers);
    mappingConfig.setGenerateExtensionFieldsResolvers(graphqlGenerateExtensionFieldsResolvers);
    mappingConfig.setGenerateDataFetchingEnvironmentArgumentInApis(graphqlGenerateDataFetchingEnvArgInApis);
    mappingConfig.setGenerateRequests(graphqlGenerateRequests);
    mappingConfig.setRequestSuffix(graphqlRequestSuffix);
    mappingConfig.setResponseProjectionSuffix(graphqlResponseProjectionSuffix);

    val mappingConfigSupplier = null;
    val generatedSources = new GraphQLCodegen(getSchemas(graphqlSchemaPaths, schemasRootDir), outputDir, mappingConfig, mappingConfigSupplier).generate();

    JavaConverters.collectionAsScalaIterableConverter(generatedSources).asScala.toSeq
  }

  private def getSchemas(graphqlSchemaPaths: Seq[String], schemasRootDir: File): java.util.List[String] = {
    if (!graphqlSchemaPaths.isEmpty) {
      return JavaConverters.seqAsJavaList(graphqlSchemaPaths);
    }
    val finder = new SchemaFinder(schemasRootDir.toPath);
    return finder.findSchemas();
  }
}
