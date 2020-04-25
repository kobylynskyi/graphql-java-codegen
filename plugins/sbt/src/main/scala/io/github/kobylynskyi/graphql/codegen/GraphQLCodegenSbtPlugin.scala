package io.github.kobylynskyi.graphql.codegen

import com.kobylynskyi.graphql.codegen.GraphQLCodegen
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
          (graphqlModelPackageName in graphql).value
        )
      },
      graphqlSchemaPaths in graphql := Seq((sourceDirectory.value / "resources/schema.graphql").getCanonicalPath),

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
      graphqlModelPackageName: String): Seq[File] = {

    val mappingConfig = new MappingConfig();
    mappingConfig.setModelNamePrefix(graphqlModelNamePrefix);
    mappingConfig.setModelNameSuffix(graphqlModelNameSuffix);
    mappingConfig.setApiPackageName(graphqlApiPackageName);
    mappingConfig.setModelPackageName(graphqlModelPackageName);

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
