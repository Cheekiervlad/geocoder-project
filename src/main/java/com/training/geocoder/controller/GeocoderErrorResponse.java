package com.training.geocoder.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeocoderErrorResponse {
    private int status;
    private String message;
    private LocalDateTime timeStamp;
}
