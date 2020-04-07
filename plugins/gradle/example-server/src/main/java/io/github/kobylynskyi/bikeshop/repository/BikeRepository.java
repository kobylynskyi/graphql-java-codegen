package io.github.kobylynskyi.bikeshop.repository;

import io.github.kobylynskyi.bikeshop.model.Bike;
import io.github.kobylynskyi.bikeshop.model.BikeType;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Collection;

public interface BikeRepository extends MongoRepository<Bike, String> {

    Collection<Bike> findByType(BikeType type);

}
