# Contributing guidelines

## Pull Request Checklist

Before sending your pull requests, make sure you followed this list:

- Read [Contributing Guidelines](CONTRIBUTING.md).
- Read [Code of Conduct](CODE_OF_CONDUCT.md).
- Refer to [Development process](#development-process).
- Changes are consistent with the Coding Style of the project.
- Build the project locally.
- Run Unit Tests and ensure that you have the test coverage for your code.

## Development process

Please follow the steps below in order to make the changes:

1. Clone the repository
2. Set the local java version to 1.8
3. Checkout **main** branch.
4. Open repository in your favourite IDE.
5. Enable and configure CheckStyle plugin in your IDE (for IntelliJ it is CheckStyle-IDEA).
   Import [graphql-codegen-check-style.xml](config/checkstyle/graphql-codegen-check-style.xml) as a .
6. Make code changes to the core library of `graphql-java-codegen`.
7. If changes are required in the plugin code, then **build** and **install** `graphql-java-codegen` first.

   ```shell script
   # This will install the library (including your recent changes) in your local maven repository.
   ./gradlew clean build publishToMavenLocal
   ```

8. Build the plugin project with updated `graphql-java-codegen` library.

   ```shell script
   # Build Gradle plugin
   ./gradlew -p plugins/gradle/graphql-java-codegen-gradle-plugin clean build
   
   # Build Maven plugin
   mvn clean verify -f plugins/maven/graphql-java-codegen-maven-plugin/pom.xml
   ```

9. Make changes to the plugin code
10. Install the plugin (copy to your local maven repository).

   ```shell script
   # Install Gradle plugin
   ./gradlew -p plugins/gradle/graphql-java-codegen-gradle-plugin clean build publishToMavenLocal
   
   # Install Maven plugin
   mvn clean install -f plugins/maven/graphql-java-codegen-maven-plugin/pom.xml
   ```

11. Make sure that `example` projects are compiling and running.
