Some breaking changes were introduced in [Release 4.0.0](https://github.com/kobylynskyi/graphql-java-codegen/releases/tag/v4.0.0).
So if you were using version 3.x.x then please follow steps below.
Note: if you are migrating from version 2.x.x, then please also follow [3.0.0 migration guide](migration-to-3.0.0.md) first.

## Migration steps

### 1. Update plugin and library versions
As per plugin description: [Gradle](https://github.com/kobylynskyi/graphql-java-codegen/tree/master/plugins/gradle), [Maven](https://github.com/kobylynskyi/graphql-java-codegen/tree/master/plugins/maven)


### 2. Change GraphQL Resolvers containing non-null GraphQL types to primitive Java types

This effects the following types:
* GraphQL `Int`
   * Non-null -> Java `int`
   * Nullable -> Java `Integer`
* GraphQL `Float`
   * Non-null -> Java `double`
   * Nullable -> Java `Double`
* GraphQL `Boolean`
   * Non-null -> Java `boolean`
   * Nullable -> Java `Boolean` 

#### Example
Let's suppose there is a graphql schema:
```graphql
type Query {
    diffTypes(intNonNull: Int!,
              boolNonNull: Boolean!,
              floatNonNull: Float!,
              intNullable: Int,
              boolNullable: Boolean,
              floatNullable: Float): Int!
}
```
And your GraphQL resolver (in version 3.x.x and below) looks like the following:
```java
public class DiffTypesQueryResolverImpl implements DiffTypesQueryResolver {
    @Override
    public Integer diffTypes(Integer intNonNull,
                             Boolean boolNonNull,
                             Double floatNonNull,
                             Integer intNullable,
                             Boolean boolNullable,
                             Double floatNullable) { return null; }
}
```
Now in version 4.0.0 the FeedsQueryResolver interface will be generated **with primitive types** for non-null GraphQL types:
```java
public interface DiffTypesQueryResolver {
    int diffTypes(int intNonNull,
                  boolean boolNonNull,
                  double floatNonNull,
                  Integer intNullable,
                  Boolean boolNullable,
                  Double floatNullable);
}
```
So you should change your resolver implementation to:
```java
public class DiffTypesQueryResolverImpl implements DiffTypesQueryResolver {
    @Override
    public int diffTypes(int intNonNull, // should be changed: Integer -> int
                         boolean boolNonNull, // should be changed: Boolean -> boolean
                         double floatNonNull, // should be changed: Double -> double
                         Integer intNullable, // should not be changed because it is nullable 
                         Boolean boolNullable, // should not be changed because it is nullable
                         Double floatNullable) {  // should not be changed because it is nullable
        return 0;
    }
}
```

### 3. Regenerate the code
Run project build so that GraphQL classes are regenerated and your code compiles.


---

Feel free to ask any questions in [GitHub Discussions](https://github.com/kobylynskyi/graphql-java-codegen/discussions) or [create an issue](https://github.com/kobylynskyi/graphql-java-codegen/issues) if you discover some problems.
