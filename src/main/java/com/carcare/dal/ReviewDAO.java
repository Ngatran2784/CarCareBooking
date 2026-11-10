package com.carcare.dal;

import com.carcare.model.Review;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object: ReviewDAO
 */
public class ReviewDAO extends DBContext {

    // TODO: Triển khai truy vấn JDBC khi CSDL hoàn thiện (Sprint tương ứng)
    public Review findById(int reviewId) {
        return null;
    }

    public List<Review> findAll() {
        return new ArrayList<>();
    }

    public boolean insert(Review item) {
        return false;
    }

    public boolean update(Review item) {
        return false;
    }

    public boolean delete(int reviewId) {
        return false;
    }
}
