Some breaking changes were introduced in [Release 3.0.0](https://github.com/kobylynskyi/graphql-java-codegen/releases/tag/v3.0.0).
So if you were using version 2.x.x then please follow steps below.
Note: if you are migrating from version 1.x.x, then please also follow [2.0.0 migration guide](migration-to-2.0.0.md) first.

## Migration steps

### 1. Update plugin and library versions
As per plugin description: [Gradle](https://github.com/kobylynskyi/graphql-java-codegen/tree/master/plugins/gradle), [Maven](https://github.com/kobylynskyi/graphql-java-codegen/tree/master/plugins/maven)


### 2. Remove `generateAsyncApis` and rename `apiAsyncReturnType`/`apiAsyncReturnListType`
`generateAsyncApis` was removed because it became useless. 
So now if you want to use async return type in your APIs, simply follow the new approach by specifying only `apiReturnType`/`apiReturnListType`:

#### Maven
```xml
<configuration>
    <!--OLD APPROACH-->
    <generateAsyncApis>true</generateAsyncApis>
    <apiAsyncReturnType>reactor.core.publisher.Mono</apiAsyncReturnType>
    <apiAsyncReturnListType>reactor.core.publisher.Flux</apiAsyncReturnListType>
    <!--OLD APPROACH-->

    <!--NEW APPROACH-->
    <apiReturnType>reactor.core.publisher.Mono</apiReturnType>
    <apiReturnListType>reactor.core.publisher.Flux</apiReturnListType>
    <!--NEW APPROACH-->
</configuration>
```

#### Gradle
```groovy
// OLD APPROACH
generateAsyncApis=true
apiAsyncReturnType=reactor.core.publisher.Mono
apiAsyncReturnListType=reactor.core.publisher.Flux
// OLD APPROACH

// NEW APPROACH
apiReturnType=reactor.core.publisher.Mono
apiReturnListType=reactor.core.publisher.Flux
// NEW APPROACH
```

#### SBT
```sbt
// OLD APPROACH
generateAsyncApis := true
apiAsyncReturnType := "scala.concurrent.Future"
// OLD APPROACH

// NEW APPROACH
apiReturnType := Some("scala.concurrent.Future")
// NEW APPROACH
```

### 3. Update plugin configuration for `customAnnotationsMapping` and `directiveAnnotationsMapping`
If you have used `customAnnotationsMapping` or `directiveAnnotationsMapping` config options, then it should be updated by providing an array of annotations in the following format:

#### Maven
```xml
<configuration>
    <!--OLD APPROACH-->
    <customAnnotationsMapping>
        <Character>
            com.fasterxml.jackson.annotation.JsonTypeInfo(use = com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME, property = "__typename")
            com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver(io.github.kobylynskyi.order.external.starwars.CharacterTypeResolver.class)
        </Character>
    </customAnnotationsMapping>
    <directiveAnnotationsMapping>
        <auth>org.springframework.security.access.annotation.Secured({{roles}})</auth>
    </directiveAnnotationsMapping>
    <!--OLD APPROACH-->

    <!--NEW APPROACH-->
    <customAnnotationsMapping>
        <Character>
            <annotation1>com.fasterxml.jackson.annotation.JsonTypeInfo(use = com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME, property = "__typename")</annotation1>
            <annotation2>com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver(io.github.kobylynskyi.order.external.starwars.CharacterTypeResolver.class)</annotation2>
        </Character>
    </customAnnotationsMapping>
    <directiveAnnotationsMapping>
        <auth>
            <annotation>org.springframework.security.access.annotation.Secured({{roles}})</annotation>
        </auth>
    </directiveAnnotationsMapping>
    <!--NEW APPROACH-->
</configuration>
```

#### Gradle
```groovy
// OLD APPROACH
customAnnotationsMapping = [
    "Character": "com.fasterxml.jackson.annotation.JsonTypeInfo(use = com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME, property = \"__typename\")"
               + System.lineSeparator()
               + "com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver(io.github.kobylynskyi.order.external.starwars.CharacterTypeResolver.class)"
]
directiveAnnotationsMapping = [
    "auth": "org.springframework.security.access.annotation.Secured({{roles}})"
]
// OLD APPROACH

// NEW APPROACH
customAnnotationsMapping = [
    "Character": [
            "com.fasterxml.jackson.annotation.JsonTypeInfo(use = com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME, property = \"__typename\")",
            "com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver(io.github.kobylynskyi.order.external.starwars.CharacterTypeResolver.class)"
    ]
]
directiveAnnotationsMapping = [
    "auth": ["org.springframework.security.access.annotation.Secured({{roles}})"]
]
// NEW APPROACH
```

#### SBT
```sbt
// OLD APPROACH
customAnnotationsMapping := {
  val mapping = new util.HashMap[String, String]
  //must add this annotation
  //property is __typename and you must use with __typename while invoke, like new CharacterResponseProjection().id().name().typename()
  //and in @JsonSubTypes.Type, name is __typename's value
  mapping.put("Character",
    s"""@com.fasterxml.jackson.annotation.JsonTypeInfo(use=com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME, include=com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY,property = "__typename")${System.lineSeparator()}@com.fasterxml.jackson.annotation.JsonSubTypes(value = {
      |        @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = HumanDO.class, name = "Human"),
      |        @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = DroidDO.class, name = "Droid")})
      |""".stripMargin)
  mapping
}
// OLD APPROACH

// NEW APPROACH
customAnnotationsMapping := {
  val mapping = new util.HashMap[String, util.List[String]]
  val annotations = new util.ArrayList[String]()
  annotations.add("@com.fasterxml.jackson.annotation.JsonTypeInfo(use=com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME, include=com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY,property = \"__typename\")")
  annotations.add("""@com.fasterxml.jackson.annotation.JsonSubTypes(value = {
                    |        @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = HumanDO.class, name = "Human"),
                    |        @com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = DroidDO.class, name = "Droid")})""".stripMargin)
  //must add this annotation
  //property is __typename and you must use with __typename while invoke, like new CharacterResponseProjection().id().name().typename()
  //and in @JsonSubTypes.Type, name is __typename's value
  mapping.put("Character", annotations)
  mapping
}
// NEW APPROACH
```
`directiveAnnotationsMapping`, In the same way.


### 4. Regenerate the code
Run project build so that GraphQL classes are regenerated.


---

Feel free to ask any questions in [GitHub Discussions](https://github.com/kobylynskyi/graphql-java-codegen/discussions) or [create an issue](https://github.com/kobylynskyi/graphql-java-codegen/issues) if you discover some problems.
