package com.training.geocoder.repository;

import com.training.geocoder.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LocationRepository extends JpaRepository<Location, Long> {

    Optional<Location> findFirstByFormattedAddress(String formattedAddress);

    Optional<Location> findByLongitudeAndLatitude(double longitude, double latitude);

}