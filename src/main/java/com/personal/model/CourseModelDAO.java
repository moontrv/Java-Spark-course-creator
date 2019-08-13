package com.personal.model;

import java.util.List;

public interface CourseModelDAO {
    boolean add(CourseModel course);

    List<CourseModel> findAll();

    CourseModel findBySlug(String slug);
}
