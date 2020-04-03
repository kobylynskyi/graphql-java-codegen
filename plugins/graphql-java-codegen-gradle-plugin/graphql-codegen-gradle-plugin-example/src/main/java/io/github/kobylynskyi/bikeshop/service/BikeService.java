package io.github.kobylynskyi.bikeshop.service;

import io.github.kobylynskyi.bikeshop.model.Bike;
import io.github.kobylynskyi.bikeshop.model.BikeType;
import io.github.kobylynskyi.bikeshop.repository.BikeRepository;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Date;

@Slf4j
@Service
public class BikeService {

    @Autowired
    private BikeRepository repository;

    public Collection<Bike> findAll() {
        return repository.findAll();
    }

    public Collection<Bike> findByType(@NonNull BikeType bikeType) {
        return repository.findByType(bikeType);
    }

    public Bike create(@NonNull Bike bikeInput) {
        bikeInput.setAddedDateTime(new Date());
        Bike savedBike = repository.save(bikeInput);
        log.info("Created new bike: {}", savedBike);
        return savedBike;
    }

}
