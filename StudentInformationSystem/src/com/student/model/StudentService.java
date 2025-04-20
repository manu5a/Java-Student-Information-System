package com.student.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class StudentService {
    private List<Student> students = new ArrayList<>();

    
    public boolean validateStudentLogin(String rollNumber, String password) {
        return searchStudent(rollNumber)
            .map(student -> student.getRollNumber().equals(password)) // Now uses 'student'
            .orElse(false);
    }

    public void addStudent(Student student) {
        try {
            if (student.getMarks() < 0 || student.getMarks() > 100) {
                throw new InvalidMarksException("Marks must be between 0 and 100.");
            }
            students.add(student);
            System.out.println("Student added successfully!");
        } catch (InvalidMarksException e) {
            System.out.println(e.getMessage());
        }
    }

    public void displayAllStudents() {
        if (students.isEmpty()) {
            System.out.println("No students to display.");
        } else {
            students.forEach(Student::displayDetails);
        }
    }

    public List<Student> filterStudents(Predicate<Student> criteria) {
        return students.stream().filter(criteria).toList();
    }

    public void removeStudent(String rollNumber) {
        boolean removed = students.removeIf(s -> s.getRollNumber().equals(rollNumber));
        System.out.println(removed ? "Student removed successfully!" : "Student not found.");
    }

    public Optional<Student> searchStudent(String rollNumber) {
        return students.stream()
            .filter(s -> s.getRollNumber().equals(rollNumber))
            .findFirst();
    }

    public void countStudentsByDepartment() {
        Supplier<String> noStudents = () -> "No students to display.";

        if (students.isEmpty()) {
            System.out.println(noStudents.get());
            return;
        }

        Map<Department, Long> counts = students.stream()
            .collect(Collectors.groupingBy(
                Student::getDepartment,
                Collectors.counting()
            ));
        counts.forEach((dept, count) ->
            System.out.println(dept + ": " + count + " students"));
    }

    public void analyzeStudents() {
        if (students.isEmpty()) {
            System.out.println("No students to analyze.");
            return;
        }

        System.out.println("=== Student Analysis ===");
        
        long total = students.stream().count();
        System.out.println("Total students: " + total);

        students.stream()
            .min(Comparator.comparing(Student::getMarks))
            .ifPresent(s -> System.out.println("Lowest marks: " + s.getMarks() + " (" + s.getName() + ")"));
        students.stream()
            .max(Comparator.comparing(Student::getMarks))
            .ifPresent(s -> System.out.println("Highest marks: " + s.getMarks() + " (" + s.getName() + ")"));

        students.stream()
            .filter(s -> s.getMarks() >= 60)
            .findAny()
            .ifPresentOrElse(
                s -> System.out.println("Sample passing student: " + s.getName()),
                () -> System.out.println("No passing students found.")
            );

        boolean allRecent = students.stream()
            .allMatch(s -> s.getEnrollmentDate().isAfter(s.getEnrollmentDate().minusMonths(6)));
        System.out.println("All enrolled in last 6 months? " + allRecent);

        boolean anyFailing = students.stream()
            .anyMatch(s -> s.getMarks() < 50);
        System.out.println("Any failing students (<50)? " + anyFailing);

        boolean noneInvalid = students.stream()
            .noneMatch(s -> s.getMarks() > 100);
        System.out.println("No invalid marks (>100)? " + noneInvalid);

        Map<String, String> rollToName = students.stream()
            .collect(Collectors.toMap(
                Student::getRollNumber,
                Student::getName,
                (n1, _) -> n1, // _ indicates n2 is intentionally unused
                HashMap::new
            ));
        System.out.println("Roll to Name map: " + rollToName);

        Map<Boolean, List<Student>> passing = students.stream()
            .collect(Collectors.partitioningBy(s -> s.getMarks() >= 60));
        System.out.println("Passing (>=60): " + passing.get(true).size() + " students");
        System.out.println("Failing (<60): " + passing.get(false).size() + " students");

        Function<Student, String> nameExtractor = Student::getName;
        List<String> sortedNames = students.stream()
            .map(nameExtractor)
            .distinct()
            .sorted()
            .limit(5)
            .toList();
        System.out.println("Sorted unique names (max 5): " + sortedNames);
    }
}