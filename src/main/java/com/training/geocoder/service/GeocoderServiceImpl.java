package com.training.geocoder.service;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.AddressComponent;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import com.training.geocoder.model.AddressDTO;
import com.training.geocoder.model.CoordinatesDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

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
    private GeoApiContext context;

    @Override
    @Cacheable("coordinatesDTO")
    public CoordinatesDTO geocode(AddressDTO addressDTO) throws ServiceException {
        System.out.println(1);
        CoordinatesDTO coordinatesDTO;

        try {
            GeocodingResult[] results = GeocodingApi.geocode(context, addressDTO.getAddress()).language(LANGUAGE_RU).await();

            if (results.length == LENGTH_OF_EMPTY_ARRAY) {
                throw new ServiceException("The request did not encounter any errors, but returned no results");
            }

            GeocodingResult result = results[FIRST_ELEMENT];

            if (result != null && result.geometry != null && result.geometry.location != null) {
                LatLng latLng = result.geometry.location;

                coordinatesDTO = CoordinatesDTO.builder().coordinates(Arrays.asList(latLng.lng, latLng.lat)).build();

            } else {
                throw new ServiceException("Geocode of geocoding API returned null");
            }
        } catch (InterruptedException | IOException | ApiException e) {
            throw new ServiceException("Geocode of geocoding API communication failure");
        }

        return coordinatesDTO;
    }

    @Override
    @Cacheable("addressDTO")
    public AddressDTO reverseGeocode(CoordinatesDTO coordinatesDTO) throws ServiceException {
        System.out.println(2);
        double longitude = coordinatesDTO.getCoordinates().get(FIRST_ELEMENT);
        double latitude = coordinatesDTO.getCoordinates().get(SECOND_ELEMENT);
        AddressDTO addressDTO;

        try {
            LatLng latLng = new LatLng(latitude, longitude);

            GeocodingResult[] results = GeocodingApi.reverseGeocode(context, latLng).language(LANGUAGE_RU).await();

            if (results.length == LENGTH_OF_EMPTY_ARRAY) {
                throw new ServiceException("The request did not encounter any errors, but returned no results");
            }

            GeocodingResult result = results[FIRST_ELEMENT];
            if (result != null) {
                String formattedAddress = createFormattedAddress(result);

                addressDTO = AddressDTO.builder().address(formattedAddress).build();

            } else {
                throw new ServiceException("ReverseGeocode of geocoding API returned null");
            }
        } catch (InterruptedException | IOException | ApiException e) {
            throw new ServiceException("ReverseGeocode of geocoding API communication failure");
        }

        return addressDTO;
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