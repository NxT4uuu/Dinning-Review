package com.sourabh.diningreview.controller;

import com.sourabh.diningreview.models.Restaurant;
import com.sourabh.diningreview.models.Review;
import com.sourabh.diningreview.models.ReviewStatus;
import com.sourabh.diningreview.models.User;
import com.sourabh.diningreview.repository.RestaurantRepository;
import com.sourabh.diningreview.repository.ReviewRepository;
import com.sourabh.diningreview.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@RequestMapping("/reviews")
@RestController
public class ReviewController {
    private final ReviewRepository reviewRepository;
    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;

    public ReviewController(ReviewRepository reviewRepository, UserRepository userRepository, RestaurantRepository restaurantRepository){
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.restaurantRepository = restaurantRepository;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void addUserReview(@RequestBody Review review){
        validateUserReview(review);

        Optional<Restaurant> optionalRestaurant = restaurantRepository.findById(review.getRestaurantId());
        if (optionalRestaurant.isPresent()){
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
        }
        review.setReviewStatus(ReviewStatus.PENDING);
        reviewRepository.save(review);
    }


    public void validateUserReview(Review review){
        if (ObjectUtils.isEmpty(review.getSubmittedBy())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        if (ObjectUtils.isEmpty(review.getRestaurantId())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        if (ObjectUtils.isEmpty(review.getPeanutScore()) &&
                ObjectUtils.isEmpty(review.getDairyScore()) &&
                ObjectUtils.isEmpty(review.getEggScore())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        Optional<User> OptionalUser = userRepository.findUserByDisplayName(review.getSubmittedBy());
        if (OptionalUser.isEmpty()){
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }
}
