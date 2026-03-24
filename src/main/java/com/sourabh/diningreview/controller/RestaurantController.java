package com.sourabh.diningreview.controller;

import com.sourabh.diningreview.models.Restaurant;
import com.sourabh.diningreview.repository.RestaurantRepository;
import org.springframework.http.HttpStatus;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.regex.Pattern;

@RequestMapping("/restaurants")
@RestController
public class RestaurantController {
    private final RestaurantRepository restaurantRepository;
    private final Pattern zipCodePattern = Pattern.compile("\\d{5}");

    public RestaurantController(RestaurantRepository restaurantRepository){
        this.restaurantRepository = restaurantRepository;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Restaurant addRestaurant(@RequestBody Restaurant restaurant){
        validateNewRestaurant(restaurant);
        return restaurantRepository.save(restaurant);
    }

    @GetMapping("/{id}")
    public Restaurant getRestaurant(@PathVariable Long id){
        Optional<Restaurant> restaurant = restaurantRepository.findById(id);
        if (restaurant.isPresent()){
            return restaurant.get();
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    @GetMapping
    public Iterable<Restaurant> getAllRestaurant(){
        return restaurantRepository.findAll();
    }

    @GetMapping("/{search}")
    public Iterable<Restaurant> searchRestaurants(@RequestParam String zipCode, @RequestParam String allergy){
        validateZipCode(zipCode);

        Iterable<Restaurant> restaurants = Collections.EMPTY_LIST;
        if (allergy.equalsIgnoreCase("peanut")){
            restaurants = restaurantRepository.findRestaurantsByZipCodeAndPeanutScoreNotNullOrderByPeanutScore(zipCode);
        } else if (allergy.equalsIgnoreCase("dairy")) {
            restaurants = restaurantRepository.findRestaurantsByZipCodeAndDairyScoreNotNullOrderByDairyScore(zipCode);
        } else if (allergy.equalsIgnoreCase("egg")){
            restaurants = restaurantRepository.findRestaurantsByZipCodeAndEggScoreNotNullOrderByEggScore(zipCode);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        }
        return  restaurants;
    }

    public void validateNewRestaurant(Restaurant restaurant){
        if (ObjectUtils.isEmpty(restaurant.getName())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        validateZipCode(restaurant.getZipCode());

        Optional<Restaurant> existingRestaurant =restaurantRepository.findRestaurantByNameAndZipCode(restaurant.getName(), restaurant.getZipCode());
        if (existingRestaurant.isPresent()){
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }

    }

    public void validateZipCode(String zipCode){
        if (!zipCodePattern.matcher(zipCode).matches()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }
}

