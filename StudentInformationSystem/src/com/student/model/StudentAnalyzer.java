
package com.student.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class StudentAnalyzer {
    private final StudentService studentService;

    
    public StudentAnalyzer(StudentService studentService) {
        this.studentService = studentService;
    }

    private final BiFunction<Student, Double, String> studentSummary = (student, marks) ->
        student != null && marks != null
            ? String.format("Student: %s, Marks: %.2f, Enrolled: %s",
                student.getName(),
                marks,
                student.getEnrollmentDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")))
            : "Invalid student data";

    
    public void analyzeRecentStudents() {
        System.out.println("Analyzing recent students:");
        studentService.filterStudents(s -> s != null && s.getEnrollmentDate().isAfter(s.getEnrollmentDate().minusMonths(12)))
            .stream()
            .peek(s -> System.out.println("Processing: " + (s != null ? s.getName() : "Unknown")))
            .skip(1)
            .flatMap(s -> {
                Course course = s != null ? s.getCourse() : null;
                return course != null ? Stream.of(course) : Stream.empty();
            })
            .distinct()
            .forEach(course -> System.out.println("Course: " + (course != null ? course.getCourseName() : "No course")));
    }

    /**
     * Performs concurrent analysis of student data.
     *
     * @throws Exception if concurrent execution fails
     */
    public void analyzeConcurrently() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        List<Callable<String>> tasks = new ArrayList<>();

        tasks.add(() -> {
            long count = studentService.filterStudents(s -> s != null).stream().count();
            return String.format("Total students: %d", count);
        });

        tasks.add(() -> studentService.filterStudents(s -> s != null).stream()
            .max((s1, s2) -> Double.compare(s1 != null ? s1.getMarks() : 0, s2 != null ? s2.getMarks() : 0))
            .map(s -> String.format("Highest marks: %s (%.2f)", s != null ? s.getName() : "Unknown", s != null ? s.getMarks() : 0))
            .orElse("No students"));

        List<Future<String>> results = executor.invokeAll(tasks);
        for (Future<String> result : results) {
            System.out.println(result.get());
        }
        executor.shutdown();
    }

    /**
     * Saves student summaries to a file.
     */
    public void saveStudentSummaries() {
        try {
            Path path = Paths.get("student_summaries.txt");
            String data = studentService.filterStudents(s -> s != null).stream()
                .map(s -> studentSummary.apply(s, s != null ? s.getMarks() : 0.0))
                .collect(Collectors.joining("\n"));
            Files.write(path, data.getBytes());
            System.out.println("Student summaries saved to file.");
        } catch (IOException e) {
            System.out.println("Error saving file: " + e.getMessage());
        }
    }

    /**
     * Analyzes a user, displaying details and grading based on marks.
     *
     * @param user the user to analyze
     */
    public void analyzeWithPatternMatching(Object user) {
        if (user instanceof MainApp.StudentUser su && su.getStudent() != null) {
            System.out.println("Student user: " + su.getStudent().getName());
            su.displayDetails();
        } else if (user instanceof MainApp.StaffUser) {
            System.out.println("Staff user");
        } else {
            System.out.println("Unknown user type");
        }

        double marks = user instanceof MainApp.StudentUser su && su.getStudent() != null ? su.getStudent().getMarks() : 0.0;
        int markCategory = (int) marks;
        if (markCategory >= 90) {
            System.out.println("Grade: Excellent");
        } else if (markCategory >= 60) {
            System.out.println("Grade: Pass");
        } else {
            System.out.println("Grade: Fail");
        }
    }
}