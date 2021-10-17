package com.training.geocoder.configuration;

import com.google.maps.GeoApiContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class GoogleGeocodingAPI {

    private static final String API_KEY = "AIzaSyClNZJwd6Y1eB-tHaX22zP8XhgFZi2Nns4";

    private static final int GEO_API_TIMEOUT = 1;
    private static final int GEO_API_QUERY_RATE_LIMIT = 2;

    @Bean
    public GeoApiContext GeoApi() {
        return new GeoApiContext.Builder().apiKey(API_KEY)
                .queryRateLimit(GEO_API_QUERY_RATE_LIMIT)
                .connectTimeout(GEO_API_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(GEO_API_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(GEO_API_TIMEOUT, TimeUnit.SECONDS)
                .build();
    }
}