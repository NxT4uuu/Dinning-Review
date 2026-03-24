package com.sourabh.diningreview.controller;

import com.sourabh.diningreview.models.AdminReviewAction;
import com.sourabh.diningreview.models.Restaurant;
import com.sourabh.diningreview.models.Review;
import com.sourabh.diningreview.models.ReviewStatus;
import com.sourabh.diningreview.repository.RestaurantRepository;
import com.sourabh.diningreview.repository.ReviewRepository;
import com.sourabh.diningreview.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Optional;

@RequestMapping("/admin")
@RestController
public class AdminController {
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;

    private final DecimalFormat decimalFormat = new DecimalFormat("0.00");

    public AdminController(ReviewRepository reviewRepository, UserRepository userRepository, RestaurantRepository restaurantRepository){
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.restaurantRepository = restaurantRepository;
    }

    @GetMapping("/reviews")
    public List<Review> getReviewsByStatus(@RequestParam String review_status){
        ReviewStatus reviewStatus = ReviewStatus.PENDING;
        try {
            reviewStatus = ReviewStatus.valueOf(review_status.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        return reviewRepository.findReviewsByReviewStatus(reviewStatus);
    }

    @PutMapping("/reviews/{reviewId}")
    public void performAction(@PathVariable Long reviewId, @RequestBody AdminReviewAction adminReviewAction){
        Optional<Review> optionalReview = reviewRepository.findById(reviewId);
        if (optionalReview.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        Review review = optionalReview.get();
        Optional<Restaurant> optionalRestaurant = restaurantRepository.findById(review.getRestaurantId());
        if (optionalReview.isEmpty()){
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
        }

        if (adminReviewAction.getAccept()){
            review.setReviewStatus(ReviewStatus.ACCEPTED);
        } else {
            review.setReviewStatus(ReviewStatus.REJECTED);
        }

        reviewRepository.save(review);
        updateRestaurantReviewScore(optionalRestaurant.get());

    }

    public void updateRestaurantReviewScore(Restaurant restaurant){
        List<Review> reviews = reviewRepository.findReviewsByRestaurantIdAndReviewStatus(restaurant.getId(), ReviewStatus.ACCEPTED);
        if (reviews.size() == 0){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        int peanutSum = 0;
        int dairySum = 0;
        int eggSum = 0;
        int peanutCount = 0;
        int dairyCount = 0;
        int eggCount = 0;

        for (Review r: reviews){
            if (!ObjectUtils.isEmpty(r.getPeanutScore())){
                peanutSum += r.getPeanutScore();
                peanutCount++;
            }
            if (!ObjectUtils.isEmpty(r.getDairyScore())){
                dairySum += r.getDairyScore();
                dairyCount++;
            }
            if (!ObjectUtils.isEmpty(r.getEggScore())){
                eggSum += r.getEggScore();
                eggCount++;
            }
        }

        int totalSum = peanutSum + dairySum + eggSum;
        int totalCount = peanutCount + dairyCount + eggCount;

        float overallScore = (float) totalSum / totalCount;
        restaurant.setOverallScore(decimalFormat.format(overallScore));

        if (peanutCount > 0){
            float peanutScore = (float) peanutSum / peanutCount;
            restaurant.setPeanutScore(decimalFormat.format(peanutScore));
        }

        if (dairyCount > 0) {
            float dairyScore = (float) dairySum / dairyCount;
            restaurant.setDairyScore(decimalFormat.format(dairyScore));
        }

        if (eggCount > 0) {
            float eggScore = (float) eggSum / eggCount;
            restaurant.setEggScore(decimalFormat.format(eggScore));
        }

        restaurantRepository.save(restaurant);
    }
}
