package io.github.kobylynskyi.bikeshop.graphql.model;

import java.util.*;

public interface BikesQuery {

    @javax.validation.constraints.NotNull
    Collection<BikeTO> bikes() throws Exception;

}