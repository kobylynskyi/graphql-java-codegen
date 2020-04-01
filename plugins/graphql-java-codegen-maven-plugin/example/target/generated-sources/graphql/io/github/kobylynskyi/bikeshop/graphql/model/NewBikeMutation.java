package io.github.kobylynskyi.bikeshop.graphql.model;

import java.util.*;

public interface NewBikeMutation {

    @javax.validation.constraints.NotNull
    BikeTO newBike(BikeInputTO bike) throws Exception;

}