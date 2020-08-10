Some breaking changes were introduced in [Release 3.0.0](https://github.com/kobylynskyi/graphql-java-codegen/releases/tag/v3.0.0).
So if you were using version 1.x.x or 2.x.x then please follow steps below.

## Migration steps

### 1. Update plugin and library versions
As per plugin description: [Gradle](https://github.com/kobylynskyi/graphql-java-codegen/tree/master/plugins/gradle), [Maven](https://github.com/kobylynskyi/graphql-java-codegen/tree/master/plugins/maven)

### 2. Update plugin configuration for `customAnnotationsMapping`
If you have used `customAnnotationsMapping` config option, then it should be fixed by providing an array in the following format:

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
            <annotation>com.fasterxml.jackson.annotation.JsonTypeInfo(use = com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME, property = "__typename")</annotation>
            <annotation>com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver(io.github.kobylynskyi.order.external.starwars.CharacterTypeResolver.class)</annotation>
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

### 3. Regenerate the code
Run project build so that GraphQL classes are regenerated.


---

Feel free to ask any questions in [Gitter community](https://gitter.im/graphql-java-codegen/community) or [create an issue](https://github.com/kobylynskyi/graphql-java-codegen/issues) if you discover some problems.
