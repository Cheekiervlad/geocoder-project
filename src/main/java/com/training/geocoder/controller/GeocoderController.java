package com.training.geocoder.controller;

import com.training.geocoder.model.AddressDTO;
import com.training.geocoder.model.CoordinatesDTO;
import com.training.geocoder.service.GeocoderService;
import com.training.geocoder.service.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/geocoder")
public class GeocoderController {

    @Autowired
    private GeocoderService geocoderService;

    @PostMapping("/coordinates")
    public AddressDTO getAddressDTO(@RequestBody CoordinatesDTO coordinates) {
        try {
            return geocoderService.reverseGeocode(coordinates);
        } catch (ServiceException e) {
            throw new GeocoderException(e.getMessage());
        }
    }

    @PostMapping("/address")
    public CoordinatesDTO getCoordinatesDTO(@RequestBody AddressDTO address) {
        try {
            return geocoderService.geocode(address);
        } catch (ServiceException e) {
            throw new GeocoderException(e.getMessage());
        }
    }
}
