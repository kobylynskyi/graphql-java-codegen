package com.kobylynskyi.graphql.codegen.generators;

import com.kobylynskyi.graphql.codegen.generators.impl.*;
import com.kobylynskyi.graphql.codegen.mapper.DataModelMapperFactory;
import com.kobylynskyi.graphql.codegen.model.MappingContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Factory for building files generators
 */
public class FilesGeneratorsFactory {

    private FilesGeneratorsFactory() {
    }

    /**
     * Factory method for building files generators
     *
     * @param context                Global mapping context
     * @param dataModelMapperFactory Data model mapper factory
     * @return a list of all files generators
     */
    public static List<FilesGenerator> getAll(MappingContext context,
                                              DataModelMapperFactory dataModelMapperFactory) {
        List<FilesGenerator> generators = new ArrayList<>();
        generators.add(new EnumsGenerator(context, dataModelMapperFactory));
        generators.add(new InterfaceGenerator(context, dataModelMapperFactory));
        generators.add(new TypeGenerator(context, dataModelMapperFactory));
        generators.add(new ResponseProjectionGenerator(context, dataModelMapperFactory));
        generators.add(new ParametrizedInputGenerator(context, dataModelMapperFactory));
        generators.add(new FieldResolversGenerator(context, dataModelMapperFactory));
        generators.add(new InputWrapperGenerator(context));
        generators.add(new InputGenerator(context, dataModelMapperFactory));
        generators.add(new UnionGenerator(context, dataModelMapperFactory));
        generators.add(new RequestResponseGenerator(context, dataModelMapperFactory));
        generators.add(new OperationsGenerator(context, dataModelMapperFactory));
        generators.add(new JacksonTypeIdResolverGenerator(context));
        return generators;
    }

}
