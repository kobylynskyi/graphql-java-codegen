package io.github.kobylynskyi.bikeshop.graphql.model;

import java.util.*;

public interface BikesByTypeQuery {

    @javax.validation.constraints.NotNull
    Collection<BikeTO> bikesByType(BikeTypeTO type) throws Exception;

}