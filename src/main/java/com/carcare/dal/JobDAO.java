package com.carcare.dal;

import com.carcare.model.JobCard;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object: JobDAO
 */
public class JobDAO extends DBContext {

    // TODO: Triển khai truy vấn JDBC khi CSDL hoàn thiện
    public JobCard findById(int jobId) {
        return null;
    }

    public List<JobCard> findAll() {
        return new ArrayList<>();
    }

    public boolean insert(JobCard item) {
        return false;
    }

    public boolean update(JobCard item) {
        return false;
    }

    public boolean delete(int jobId) {
        return false;
    }
}
