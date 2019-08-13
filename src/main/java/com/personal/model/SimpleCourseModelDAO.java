package com.personal.model;

import java.util.ArrayList;
import java.util.List;

public class SimpleCourseModelDAO implements CourseModelDAO {
    private List<CourseModel> courseList;

    public SimpleCourseModelDAO() {
        this.courseList = new ArrayList<>();
    }

    @Override
    public boolean add(CourseModel course) {
        return this.courseList.add(course);
    }

    @Override
    public List<CourseModel> findAll() {
        return new ArrayList<>(courseList);
    }

    @Override
    public CourseModel findBySlug(String slug) {
        return courseList.stream()
                .filter(course -> course.getSlug().equals(slug)).findFirst()
                .orElseThrow(NotFoundException::new);
    }
}
