package com.sourabh.diningreview.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
public class Restaurant {

    @Id
    @GeneratedValue
    private Long id;

    // name and address of restaurant
    private String name;
    private String line1;
    private String city;
    private String state;
    private String zipCode;

    // way to contact or provide service
    private String phoneNumber;
    private String webSite;

    // score or review of the available items
    private String overallScore;
    private String peanutScore;
    private String dairyScore;
    private String eggScore;
}
