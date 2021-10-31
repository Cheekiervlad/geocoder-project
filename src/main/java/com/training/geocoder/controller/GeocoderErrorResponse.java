package com.training.geocoder.controller;

import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class GeocoderErrorResponse {
    private int status;
    private String message;
    private LocalDateTime timeStamp;
}
