package com.student.model;

public class Course {
    private String courseName;
    private int durationInWeeks;

    public Course(String courseName) {
        this(courseName, 0);
    }

    public Course(String courseName, int durationInWeeks) {
        this.courseName = courseName;
        this.durationInWeeks = durationInWeeks;
    }

    public String getCourseName() {
        return courseName;
    }

    public int getDurationInWeeks() {
        return durationInWeeks;
    }

    public void setDuration(int weeks) {
        this.durationInWeeks = weeks;
    }

    public void setDuration(String weeks) {
        this.durationInWeeks = Integer.parseInt(weeks);
    }

    @Override
    public String toString() {
        var sb = new StringBuilder();
        sb.append("Course Name: ").append(courseName)
          .append(", Duration: ").append(durationInWeeks).append(" weeks");
        return sb.toString();
    }
}