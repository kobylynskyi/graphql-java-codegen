package io.github.kobylynskyi.bikeshop.graphql.resolvers;

import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import io.github.kobylynskyi.bikeshop.graphql.mappers.BikeMapper;
import io.github.kobylynskyi.bikeshop.graphql.model.BikeTO;
import io.github.kobylynskyi.bikeshop.graphql.model.BikeTypeTO;
import io.github.kobylynskyi.bikeshop.graphql.model.Query;
import io.github.kobylynskyi.bikeshop.model.BikeType;
import io.github.kobylynskyi.bikeshop.service.BikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;

import static java.util.stream.Collectors.toList;

@Component
public class QueriesResolver implements Query, GraphQLQueryResolver {

    @Autowired
    private BikeService service;
    @Autowired
    private BikeMapper mapper;

    @Override
    public Collection<BikeTO> bikes() {
        return service.findAll().stream()
                .map(mapper::map)
                .collect(toList());
    }

    @Override
    public Collection<BikeTO> bikesByType(BikeTypeTO bikeTypeTO) {
        BikeType bikeType = mapper.mapInputType(bikeTypeTO);
        return service.findByType(bikeType).stream()
                .map(mapper::map)
                .collect(toList());
    }
}
