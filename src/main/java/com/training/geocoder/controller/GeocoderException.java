package com.training.geocoder.controller;

public class GeocoderException extends RuntimeException {
    public GeocoderException() {
        super();
    }

    public GeocoderException(String message) {
        super(message);
    }

    public GeocoderException(Exception e) {
        super(e);
    }

    public GeocoderException(String message, Exception e) {
        super(message, e);
    }
}
