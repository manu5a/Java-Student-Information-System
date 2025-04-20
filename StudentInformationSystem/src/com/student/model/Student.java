package com.student.model;

public non-sealed class Student extends Person implements PersonDetails {
    private String rollNumber;
    private double marks;
    private Course course;
    private Gender gender;
    private final Department department; 

    
    public Student(String name, String email, Address address, String rollNumber, 
                   double marks, Course course, Gender gender, Department department) {
        super(name, email, address);
        this.rollNumber = rollNumber;
        this.marks = marks;
        this.course = course;
        this.gender = gender;
        this.department = department;
    }

    public String getRollNumber() {
        return rollNumber;
    }

    public void setRollNumber(String rollNumber) {
        this.rollNumber = rollNumber;
    }

    public double getMarks() {
        return marks;
    }

    public void setMarks(double marks) {
        this.marks = marks;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public Department getDepartment() {
        return department;
    }

    @Override
    public void displayDetails() {
        System.out.println(this);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Student Details:\n");
        sb.append("Name: ").append(getName()).append("\n");
        sb.append("Email: ").append(getEmail()).append("\n");
        sb.append("Roll Number: ").append(rollNumber).append("\n");
        sb.append("Marks: ").append(marks).append("\n");
        sb.append("Gender: ").append(gender).append("\n");
        sb.append("Address: ").append(getAddress()).append("\n");
        sb.append("Course: ").append(course != null ? course.getCourseName() : "No course assigned").append("\n");
        sb.append("Enrollment Date: ").append(getEnrollmentDate()).append("\n");
        sb.append("Department: ").append(department).append("\n");
        return sb.toString();
    }
}