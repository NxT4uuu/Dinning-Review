package com.sourabh.diningreview.repository;

import com.sourabh.diningreview.models.Review;
import com.sourabh.diningreview.models.ReviewStatus;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ReviewRepository extends CrudRepository<Review, Long> {

    List<Review> findReviewsByRestaurantIdAndReviewStatus(Long restaurantId, ReviewStatus reviewStatus);

    List<Review> findReviewsByReviewStatus(ReviewStatus reviewStatus);

}
