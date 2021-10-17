package com.training.geocoder.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@Builder
@Table(name = "location")
@NoArgsConstructor
@AllArgsConstructor
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "l_id")
    private long id;

    @Column(name = "l_formatted_address")
    private String formattedAddress;

    @Column(name = "l_longitude")
    private double longitude;

    @Column(name = "l_latitude")
    private double latitude;
}
