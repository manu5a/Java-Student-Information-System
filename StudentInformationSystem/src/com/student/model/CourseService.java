package com.student.model;

import java.util.ArrayList;
import java.util.List;

public class CourseService {
    private List<Course> courses = new ArrayList<>();

    public void addCourse(Course course) {
        if (course.getCourseName() == null || course.getCourseName().isEmpty()) {
            throw new IllegalArgumentException("Course name cannot be empty!");
        }
        courses.add(course);
        System.out.println("Course added successfully!");
    }

    public void displayAllCourses() {
        if (courses.isEmpty()) {
            System.out.println("No courses to display.");
        } else {
            courses.forEach(System.out::println);
        }
    }
}