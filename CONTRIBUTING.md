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

1. Clone the repository and open it in your favourite IDE.
2. Make code changes to the core library of `graphql-java-codegen`.
3. If changes are required in the plugin code, then **build** and **install** `graphql-java-codegen` first.

   ```shell script
   # This will install the library (including your recent changes) in your local maven repository.
   ./gradlew clean build publishToMavenLocal
   ```
   
4. Build the plugin project with updated `graphql-java-codegen` library.

   ```shell script
   # Build Gradle plugin
   ./gradlew -p plugins/gradle/graphql-java-codegen-gradle-plugin clean build
   
   # Build Maven plugin
   cd plugins/maven/graphql-java-codegen-maven-plugin
   mvn clean verify 
   ```

5. Make changes to the plugin code
6. Install the plugin (copy to your local maven repository).

   ```shell script
   # Install Gradle plugin
   ./gradlew -p plugins/gradle/graphql-java-codegen-gradle-plugin clean build publishToMavenLocal
   
   # Install Maven plugin
   cd plugins/maven/graphql-java-codegen-maven-plugin
   mvn clean install 
   ```

7. Make sure that `example` projects are compiling and running.
