package com.training.geocoder.service;

import com.training.geocoder.model.AddressDTO;
import com.training.geocoder.model.CoordinatesDTO;

public interface GeocoderService {

    CoordinatesDTO geocode(AddressDTO addressDTO) throws ServiceException;

    AddressDTO reverseGeocode(CoordinatesDTO coordinatesDTO) throws ServiceException;
}