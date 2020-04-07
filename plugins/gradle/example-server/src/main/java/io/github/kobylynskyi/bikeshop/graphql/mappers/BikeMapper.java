package io.github.kobylynskyi.bikeshop.graphql.mappers;

import io.github.kobylynskyi.bikeshop.model.Bike;
import io.github.kobylynskyi.bikeshop.model.BikeType;
import io.github.kobylynskyi.bikeshop.graphql.model.BikeInputTO;
import io.github.kobylynskyi.bikeshop.graphql.model.BikeTO;
import io.github.kobylynskyi.bikeshop.graphql.model.BikeTypeTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BikeMapper {

    BikeTO map(Bike from);

    @Mapping(target = "id", ignore = true) // auto-generated
    @Mapping(target = "addedDateTime", ignore = true)
        // set in the service
    Bike mapInput(BikeInputTO from);

    BikeType mapInputType(BikeTypeTO type);

}
