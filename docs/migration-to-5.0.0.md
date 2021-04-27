Some breaking changes were introduced
in [Release 5.0.0](https://github.com/kobylynskyi/graphql-java-codegen/releases/tag/v5.0.0). So if you were using
version 4.x.x then please follow steps below. Note: if you are migrating from version 3.x.x or earlier, then please also
follow [4.0.0 migration guide](migration-to-4.0.0.md) first.

## NOTE
**Migration from 4.x.x to 5.0.0 is required only if you were using 
[external mapping configuration](codegen-options.md#external-mapping-configuration)**. 
Otherwise - feel free to use graphql-java-codegen-5.0.0 without any breaking changes.

## Migration steps

### 1. Update plugin and library versions

As per plugin description: 
[Gradle](https://github.com/kobylynskyi/graphql-java-codegen/tree/master/plugins/gradle), 
[Maven](https://github.com/kobylynskyi/graphql-java-codegen/tree/master/plugins/maven),
[SBT](https://github.com/kobylynskyi/graphql-java-codegen/tree/master/plugins/sbt)

### 2. Rename jsonConfigurationFile => configurationFiles

#### Maven

```xml
<configuration>
    <!--OLD APPROACH-->
    <jsonConfigurationFile>src/main/resources/mappingConfig.json</jsonConfigurationFile>
    <!--OLD APPROACH-->

    <!--NEW APPROACH-->
    <configurationFiles>
      <configurationFile>src/main/resources/mappingConfig.json</configurationFile>
    </configurationFiles>
    <!--NEW APPROACH-->
</configuration>
```

#### Gradle

```groovy
// OLD APPROACH
jsonConfigurationFile="src/main/resources/mappingConfig.json"
// OLD APPROACH

// NEW APPROACH
configurationFiles=["src/main/resources/mappingConfig.json"]
// NEW APPROACH
```

#### SBT

```sbt
// OLD APPROACH
jsonConfigurationFile := Some("src/main/resources/mappingConfig.json")
// OLD APPROACH

// NEW APPROACH
configurationFiles := List("src/main/resources/mappingConfig.json")
// NEW APPROACH
```

### 3. Regenerate the code

Run project build so that GraphQL classes are regenerated and your code compiles.


---

Feel free to ask any questions in [GitHub Discussions](https://github.com/kobylynskyi/graphql-java-codegen/discussions)
or [create an issue](https://github.com/kobylynskyi/graphql-java-codegen/issues) if you discover some problems.
