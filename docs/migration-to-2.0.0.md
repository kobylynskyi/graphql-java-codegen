Some breaking changes were introduced in [Release 2.0.0](https://github.com/kobylynskyi/graphql-java-codegen/releases/tag/v2.0.0).
So if you were using version 1.x.x then please follow steps below.

## Migration steps

### 1. Update plugin and library versions
As per plugin description: [Gradle](https://github.com/kobylynskyi/graphql-java-codegen/tree/master/plugins/gradle), [Maven](https://github.com/kobylynskyi/graphql-java-codegen/tree/master/plugins/maven)

### 2. Update plugin configuration (only for client-side codegen)
If you have used `generateRequests` config option, please replace it with `generateClient`:
Plugin | Old config | Replaced by
------ | ---------- | -----------
Maven  | `<generateRequests>true</generateRequests>` | `<generateClient>true</generateClient>` |
Gradle | `generateRequests=true` | `generateClient=true` |

### 3. Regenerate the code
Run project build so that GraphQL classes are regenerated.

### 4. Rename all references to API interfaces in your resolver classes (only for server-side codegen)

If you were using generated API classes, then in your GraphQL Resolver implementation you should fix the reference to generated interfaces.
This is because by default the suffix of API interface is now by default equals to `Resolver` (configurable via `apiNameSuffix` option).
Example:
```java
// old approach
import com.example.graphql.generated.CreatePersonMutation;

@Component
public class PersonResolver implements CreatePersonMutation {

// should be replaced with:
// new approach
import com.example.graphql.generated.CreatePersonMutationResolver;

@Component
public class PersonResolver implements CreatePersonMutationResolver {
```

### 5. Get rid of `GraphQLRequest.toString` (only for client-side codegen)

If in your client code you were using `GraphQLRequest` class to build GraphQL request, then you will need to change the way how it is being serialized to string:
```java
PersonByIdQueryRequest personByIdRequest = new PersonByIdQueryRequest()...
PersonResponseProjection responseProjection = new PersonResponseProjection()...
GraphQLRequest request = new GraphQLRequest(personByIdRequest, responseProjection);
request.toString(); // OLD APPROACH
request.toHttpJsonBody(); // NEW APPROACH - if you want HTTP json body (for GraphQL POST requests)
request.toQueryString(); // NEW APPROACH - if you want raw query string (for GraphQL GET requests)
```

### 6. Replace `java.util.Collection` with `java.util.List` in your Resolver classes (only for server-side codegen)

```java
public class PersonResolver implements PersonsByIdsQueryResolver {
    @Override
    public Collection<ProductDTO> personsByIds(Collection<String> ids) { ... } // OLD APPROACH

    @Override
    public List<ProductDTO> personsByIds(List<String> ids) { ... } // NEW APPROACH
}
```

---

Feel free to ask any questions in [GitHub Discussions](https://github.com/kobylynskyi/graphql-java-codegen/discussions) or [create an issue](https://github.com/kobylynskyi/graphql-java-codegen/issues) if you discover some problems.
