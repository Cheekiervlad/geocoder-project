package com.training.geocoder.service;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.AddressComponent;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import com.training.geocoder.entity.Location;
import com.training.geocoder.model.AddressDTO;
import com.training.geocoder.model.CoordinatesDTO;
import com.training.geocoder.repository.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

@Service
public class GeocoderServiceImpl implements GeocoderService {

    private static final String LANGUAGE_RU = "ru";
    private static final String ATTRIBUTE_CITY = "locality";
    private static final String ATTRIBUTE_STREET = "route";
    private static final String ATTRIBUTE_STREET_NUMBER = "street_number";
    private static final String UNKNOWN = "unknown";
    private static final String UNKNOWN_ADDRESS = "unknown address";

    private static final int FIRST_ELEMENT = 0;
    private static final int SECOND_ELEMENT = 1;
    private static final int LENGTH_OF_EMPTY_ARRAY = 0;

    @Autowired
    private LocationRepository geocodingResultRepository;

    @Autowired
    private GeoApiContext context;

    @Override
    @Transactional
    public CoordinatesDTO geocode(AddressDTO addressDTO) throws ServiceException {
        Optional<Location> locationOptional = geocodingResultRepository.findFirstByFormattedAddress(addressDTO.getAddress());

        CoordinatesDTO coordinatesDTO;
        if (locationOptional.isPresent()) {
            coordinatesDTO = createCoordinatesDTO(locationOptional.get());
        } else {
            try {
                GeocodingResult[] results = GeocodingApi.geocode(context, addressDTO.getAddress()).language(LANGUAGE_RU).await();
                GeocodingResult result = results[FIRST_ELEMENT];

                if (result != null && result.geometry != null && result.geometry.location != null) {
                    LatLng latLng = result.geometry.location;

                    Location location = Location.builder().formattedAddress(addressDTO.getAddress())
                            .latitude(latLng.lat).longitude(latLng.lng).build();
                    geocodingResultRepository.save(location);
                    coordinatesDTO = createCoordinatesDTO(location);

                } else {
                    throw new ServiceException("Geocode of geocoding API returned null");
                }
            } catch (InterruptedException | IOException | ApiException e) {
                throw new ServiceException("Geocode of geocoding API communication failure");
            }
        }
        return coordinatesDTO;
    }

    private CoordinatesDTO createCoordinatesDTO(Location location) {
        return CoordinatesDTO.builder().coordinates(Arrays.asList(location.getLongitude(), location.getLatitude())).build();
    }

    @Override
    @Transactional
    public AddressDTO reverseGeocode(CoordinatesDTO coordinatesDTO) throws ServiceException {

        double longitude = coordinatesDTO.getCoordinates().get(FIRST_ELEMENT);
        double latitude = coordinatesDTO.getCoordinates().get(SECOND_ELEMENT);

        Optional<Location> locationOptional = geocodingResultRepository.findByLongitudeAndLatitude(longitude, latitude);

        AddressDTO addressDTO;
        if (locationOptional.isPresent()) {
            addressDTO = createAddressDTO(locationOptional.get());
        } else {
            try {
                LatLng latLng = new LatLng(coordinatesDTO.getCoordinates().get(SECOND_ELEMENT),
                        coordinatesDTO.getCoordinates().get(FIRST_ELEMENT));

                GeocodingResult[] results = GeocodingApi.reverseGeocode(context, latLng).language(LANGUAGE_RU).await();

                if (results.length == LENGTH_OF_EMPTY_ARRAY) {
                    throw new ServiceException("The request did not encounter any errors, but returned no results");
                }
                GeocodingResult result = results[FIRST_ELEMENT];
                System.out.println(result);

                if (result != null) {
                    String formattedAddress = createFormattedAddress(result);

                    Location location = Location.builder().formattedAddress(formattedAddress)
                            .latitude(latitude).longitude(longitude).build();
                    geocodingResultRepository.save(location);
                    addressDTO = createAddressDTO(location);

                } else {
                    throw new ServiceException("ReverseGeocode of geocoding API returned null");
                }
            } catch (InterruptedException | IOException | ApiException e) {
                throw new ServiceException("ReverseGeocode of geocoding API communication failure");
            }
        }
        return addressDTO;
    }

    private AddressDTO createAddressDTO(Location location) {
        return AddressDTO.builder().address(location.getFormattedAddress()).build();
    }

    private String createFormattedAddress(GeocodingResult result) {

        AddressComponent[] addressComponents = result.addressComponents;
        String city = getAddressComponentAttribute(addressComponents, ATTRIBUTE_CITY);
        String street = getAddressComponentAttribute(addressComponents, ATTRIBUTE_STREET);
        String streetNumber = getAddressComponentAttribute(addressComponents, ATTRIBUTE_STREET_NUMBER);

        if (!UNKNOWN.equals(streetNumber) && !UNKNOWN.equals(street) && !UNKNOWN.equals(city)) {
            return city + ", " + street + ", " + streetNumber;
        } else if (!UNKNOWN.equals(street) && !UNKNOWN.equals(city)) {
            return city + ", " + street;
        } else if (!UNKNOWN.equals(city)) {
            return city;
        } else {
            return UNKNOWN_ADDRESS;
        }
    }

    private String getAddressComponentAttribute(AddressComponent[] addressComponents, String attribute) {
        Optional<AddressComponent> addressComponent = Arrays.stream(addressComponents)
                .filter(x -> attribute.equals(x.types[FIRST_ELEMENT].toString())).findFirst();
        if (addressComponent.isPresent()) {
            return addressComponent.get().longName;
        } else {
            return UNKNOWN;
        }
    }
}