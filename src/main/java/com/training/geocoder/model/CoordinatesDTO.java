package com.training.geocoder.model;

import lombok.*;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class CoordinatesDTO {
    private List<Double> coordinates;
}
