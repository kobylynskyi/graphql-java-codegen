package com.kobylynskyi.graphql.codegen.model;

import com.kobylynskyi.graphql.codegen.mapper.DataModelMapper;
import com.kobylynskyi.graphql.codegen.mapper.DataModelMapperFactory;
import com.kobylynskyi.graphql.codegen.model.definitions.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class MappingContext implements GraphQLCodegenConfiguration {

    private final MappingConfig config;
    private final ExtendedDocument document;
    private final Set<String> typesUnionsInterfacesNames;
    private final Set<String> interfacesName;
    private final Map<String, Set<String>> interfaceChildren;
    private final GeneratedInformation generatedInformation;
    private Set<String> enumImportItSelfInScala = null;
    private Map<String, Set<String>> parentInterfaceProperties = null;
    private final DataModelMapperFactory dataModelMapperFactory;

    public MappingContext(MappingConfig mappingConfig,
                          ExtendedDocument document,
                          GeneratedInformation generatedInformation, DataModelMapperFactory dataModelMapperFactory) {
        this.config = mappingConfig;
        this.document = document;
        this.typesUnionsInterfacesNames = document.getTypesUnionsInterfacesNames();
        this.interfacesName = document.getInterfacesNames();
        this.interfaceChildren = document.getInterfaceChildren();
        this.generatedInformation = generatedInformation;
        this.dataModelMapperFactory = dataModelMapperFactory;
    }

    @Override
    public GeneratedLanguage getGeneratedLanguage() {
        return config.getGeneratedLanguage();
    }

    @Override
    public Boolean isGenerateModelOpenClasses() {
        return config.isGenerateModelOpenClasses();
    }

    @Override
    public Map<String, String> getCustomTypesMapping() {
        return config.getCustomTypesMapping();
    }

    @Override
    public Map<String, List<String>> getCustomAnnotationsMapping() {
        return config.getCustomAnnotationsMapping();
    }

    @Override
    public Map<String, List<String>> getDirectiveAnnotationsMapping() {
        return config.getDirectiveAnnotationsMapping();
    }

    @Override
    public Boolean getGenerateApis() {
        return config.getGenerateApis();
    }

    @Override
    public Boolean getGenerateModelsForRootTypes() {
        return config.getGenerateModelsForRootTypes();
    }

    @Override
    public ApiRootInterfaceStrategy getApiRootInterfaceStrategy() {
        return config.getApiRootInterfaceStrategy();
    }

    @Override
    public ApiInterfaceStrategy getApiInterfaceStrategy() {
        return config.getApiInterfaceStrategy();
    }

    @Override
    public String getPackageName() {
        return config.getPackageName();
    }

    @Override
    public String getApiPackageName() {
        return config.getApiPackageName();
    }

    @Override
    public String getModelPackageName() {
        return config.getModelPackageName();
    }

    @Override
    public String getModelNamePrefix() {
        return config.getModelNamePrefix();
    }

    @Override
    public String getModelNameSuffix() {
        return config.getModelNameSuffix();
    }

    @Override
    public ApiNamePrefixStrategy getApiNamePrefixStrategy() {
        return config.getApiNamePrefixStrategy();
    }

    @Override
    public String getApiNamePrefix() {
        return config.getApiNamePrefix();
    }

    @Override
    public String getApiNameSuffix() {
        return config.getApiNameSuffix();
    }

    @Override
    public String getModelValidationAnnotation() {
        return config.getModelValidationAnnotation();
    }

    @Override
    public String getApiReturnType() {
        return config.getApiReturnType();
    }

    @Override
    public String getApiReturnListType() {
        return config.getApiReturnListType();
    }

    @Override
    public String getSubscriptionReturnType() {
        return config.getSubscriptionReturnType();
    }

    @Override
    public Boolean getGenerateBuilder() {
        return config.getGenerateBuilder();
    }

    @Override
    public Boolean getGenerateEqualsAndHashCode() {
        return config.getGenerateEqualsAndHashCode();
    }

    @Override
    public Boolean getGenerateImmutableModels() {
        return config.getGenerateImmutableModels();
    }

    @Override
    public Boolean getGenerateToString() {
        return config.getGenerateToString();
    }

    @Override
    public Boolean getGenerateParameterizedFieldsResolvers() {
        return config.getGenerateParameterizedFieldsResolvers();
    }

    @Override
    public String getTypeResolverPrefix() {
        return config.getTypeResolverPrefix();
    }

    @Override
    public String getTypeResolverSuffix() {
        return config.getTypeResolverSuffix();
    }

    @Override
    public Boolean getGenerateExtensionFieldsResolvers() {
        return config.getGenerateExtensionFieldsResolvers();
    }

    @Override
    public Boolean getGenerateDataFetchingEnvironmentArgumentInApis() {
        return config.getGenerateDataFetchingEnvironmentArgumentInApis();
    }

    @Override
    public Boolean getGenerateApisWithThrowsException() {
        return config.getGenerateApisWithThrowsException();
    }

    @Override
    public Boolean getAddGeneratedAnnotation() {
        return config.getAddGeneratedAnnotation();
    }

    @Override
    public RelayConfig getRelayConfig() {
        return config.getRelayConfig();
    }

    @Override
    public Boolean getUseOptionalForNullableReturnTypes() {
        return config.getUseOptionalForNullableReturnTypes();
    }

    @Override
    public Set<String> getFieldsWithResolvers() {
        return config.getFieldsWithResolvers();
    }

    @Override
    public Set<String> getFieldsWithoutResolvers() {
        return config.getFieldsWithoutResolvers();
    }

    @Override
    public Boolean getGenerateClient() {
        return config.getGenerateClient();
    }

    @Override
    public String getRequestSuffix() {
        return config.getRequestSuffix();
    }

    @Override
    public String getResponseSuffix() {
        return config.getResponseSuffix();
    }

    @Override
    public String getResponseProjectionSuffix() {
        return config.getResponseProjectionSuffix();
    }

    @Override
    public String getParametrizedInputSuffix() {
        return config.getParametrizedInputSuffix();
    }

    @Override
    public String getQueryResolverParentInterface() {
        return config.getQueryResolverParentInterface();
    }

    @Override
    public String getMutationResolverParentInterface() {
        return config.getMutationResolverParentInterface();
    }

    @Override
    public String getSubscriptionResolverParentInterface() {
        return config.getSubscriptionResolverParentInterface();
    }

    @Override
    public String getResolverParentInterface() {
        return config.getResolverParentInterface();
    }

    @Override
    public Integer getResponseProjectionMaxDepth() {
        return config.getResponseProjectionMaxDepth();
    }

    @Override
    public Set<String> getUseObjectMapperForRequestSerialization() {
        return config.getUseObjectMapperForRequestSerialization();
    }

    public ExtendedDocument getDocument() {
        return document;
    }

    public Set<String> getTypesUnionsInterfacesNames() {
        return typesUnionsInterfacesNames;
    }

    public Set<String> getInterfacesName() {
        return interfacesName;
    }

    public Map<String, Set<String>> getInterfaceChildren() {
        return interfaceChildren;
    }

    public GeneratedInformation getGeneratedInformation() {
        return generatedInformation;
    }

    public Set<String> getEnumImportItSelfInScala() {
        // Only for scala
        if (GeneratedLanguage.SCALA.equals(this.config.getGeneratedLanguage()) && enumImportItSelfInScala == null) {
            enumImportItSelfInScala = this.document.getEnumDefinitions().stream().map(this::getModelClassNameWithPrefixAndSuffix).collect(Collectors.toSet());
        }
        return enumImportItSelfInScala;
    }

    public Map<String, Set<String>> getParentInterfaceProperties() {
        // In this way, we no longer need to rely on the order in which files are created
        // Only for scala/kotlin
        if ((GeneratedLanguage.SCALA.equals(this.config.getGeneratedLanguage()) ||
                GeneratedLanguage.KOTLIN.equals(this.config.getGeneratedLanguage()))
                && parentInterfaceProperties == null) {
            parentInterfaceProperties = new HashMap<>();
            for (ExtendedInterfaceTypeDefinition extendedInterfaceTypeDefinition : this.document.getInterfaceDefinitions()) {
                String clazzName = getModelClassNameWithPrefixAndSuffix(extendedInterfaceTypeDefinition);
                Set<String> fields = getFields(extendedInterfaceTypeDefinition.getFieldDefinitions(),
                        extendedInterfaceTypeDefinition).stream().map(ParameterDefinition::getName).collect(Collectors.toSet());
                if (parentInterfaceProperties.containsKey(clazzName)) {
                    parentInterfaceProperties.get(clazzName).addAll(fields);
                } else {
                    parentInterfaceProperties.put(clazzName, fields);
                }
            }
        }
        return parentInterfaceProperties;
    }

    private String getModelClassNameWithPrefixAndSuffix(ExtendedEnumTypeDefinition extendedEnumTypeDefinition) {
        return DataModelMapper.getModelClassNameWithPrefixAndSuffix(this, extendedEnumTypeDefinition.getName());
    }

    private String getModelClassNameWithPrefixAndSuffix(ExtendedDefinition<?, ?> extendedDefinition) {
        return this.dataModelMapperFactory.getDataModelMapper().getModelClassNameWithPrefixAndSuffix(this, extendedDefinition);
    }

    private List<ParameterDefinition> getFields(List<ExtendedFieldDefinition> fieldDefinitions, ExtendedDefinition<?, ?> parentDefinition) {
        return this.dataModelMapperFactory.getFieldDefinitionToParameterMapper().mapFields(this, fieldDefinitions, parentDefinition);
    }

}
