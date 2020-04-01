package io.github.kobylynskyi.bikeshop.graphql.model;

import java.util.*;

public interface Query {

    @javax.validation.constraints.NotNull
    Collection<BikeTO> bikes() throws Exception;

    @javax.validation.constraints.NotNull
    Collection<BikeTO> bikesByType(BikeTypeTO type) throws Exception;

}