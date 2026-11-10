package com.carcare.dal;

import com.carcare.model.User;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object: UserDAO
 */
public class UserDAO extends DBContext {

    // TODO: Triển khai truy vấn JDBC khi CSDL hoàn thiện (Sprint tương ứng)
    public User findById(int userId) {
        return null;
    }

    public List<User> findAll() {
        return new ArrayList<>();
    }

    public boolean insert(User item) {
        return false;
    }

    public boolean update(User item) {
        return false;
    }

    public boolean delete(int userId) {
        return false;
    }
}
