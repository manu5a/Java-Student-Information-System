package com.student.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.Locale;

public class MainApp {
    public sealed interface UserRole extends PersonDetails permits StudentUser, StaffUser {
    }

    public static final class StudentUser implements UserRole {
        private final Student student;
        private final String password;

        StudentUser(Student student, String password) {
            this.student = student;
            this.password = password;
        }

        public Student getStudent() {
            return student;
        }

        boolean authenticate(String password) {
            return this.password.equals(password);
        }

        @Override
        public void displayDetails() {
            student.displayDetails();
        }

        void editPersonalInfo(Scanner scanner) {
            System.out.println("Edit Personal Info (leave blank to keep unchanged):");
            System.out.print("Name [" + student.getName() + "]: ");
            String name = scanner.nextLine();
            if (!name.isBlank()) student.setName(name);

            System.out.print("Email [" + student.getEmail() + "]: ");
            String email = scanner.nextLine();
            if (!email.isBlank() && email.endsWith("@gmail.com")) student.setEmail(email);

            System.out.print("Street [" + student.getAddress().street() + "]: ");
            String street = scanner.nextLine();
            street = street.isBlank() ? student.getAddress().street() : street;

            System.out.print("City [" + student.getAddress().city() + "]: ");
            String city = scanner.nextLine();
            city = city.isBlank() ? student.getAddress().city() : city;

            System.out.print("Postal Code [" + student.getAddress().postalCode() + "]: ");
            String postalCode = scanner.nextLine();
            postalCode = postalCode.isBlank() ? student.getAddress().postalCode() : postalCode;

            student.setAddress(new Address(street, city, postalCode));

            System.out.print("Gender (MALE/FEMALE/OTHER) [" + student.getGender() + "]: ");
            String genderInput = scanner.nextLine().toUpperCase();
            if (!genderInput.isBlank()) {
                try {
                    student.setGender(Gender.valueOf(genderInput));
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid gender; unchanged.");
                }
            }
            System.out.println("Information updated.");
        }
    }

    public static final class StaffUser implements UserRole {
        private final String password;

        StaffUser(String password) {
            this.password = password;
        }

        boolean authenticate(String password) {
            return this.password.equals(password);
        }

        @Override
        public void displayDetails() {
            System.out.println("Staff User");
        }
    }

    public static void main(String[] args) {
        // Load both English and Spanish resource bundles
        ResourceBundle bundleEn = ResourceBundle.getBundle("com.student.model.messages", new Locale("en", "US"));
        ResourceBundle bundleEs = ResourceBundle.getBundle("com.student.model.messages", new Locale("es", "ES"));

        StudentService studentService = new StudentService();
        CourseService courseService = new CourseService();
        Scanner scanner = new Scanner(System.in);
        List<StudentUser> studentUsers = new ArrayList<>();
        StaffUser staff = new StaffUser("admin123");
        StudentAnalyzer analyzer = new StudentAnalyzer(studentService);

        while (true) {
            // Print welcome message in both languages
            System.out.println("\n" + bundleEn.getString("welcome.message"));
            System.out.println(bundleEs.getString("welcome.message"));
            System.out.println("1. Login / Iniciar sesión");
            System.out.println("2. Exit / Salir");
            System.out.print(bundleEn.getString("login.choice") + " / " + 
                             getStringOrFallback(bundleEs, "login.choice", bundleEn) + " ");
            int loginChoice = scanner.nextInt();
            scanner.nextLine();

            if (loginChoice == 2) {
                System.out.println(bundleEn.getString("exit.message"));
                System.out.println(getStringOrFallback(bundleEs, "exit.message", bundleEn));
                break;
            }

            if (loginChoice != 1) {
                System.out.println(bundleEn.getString("invalid.choice"));
                System.out.println(getStringOrFallback(bundleEs, "invalid.choice", bundleEn));
                continue;
            }

            System.out.print(bundleEn.getString("role.prompt"));
            System.out.println(getStringOrFallback(bundleEs, "role.prompt", bundleEn));
            String role = scanner.nextLine().toLowerCase();
            System.out.print(bundleEn.getString("username.prompt"));
            System.out.println(getStringOrFallback(bundleEs, "username.prompt", bundleEn));
            String username = scanner.nextLine();
            System.out.print(bundleEn.getString("password.prompt"));
            System.out.println(getStringOrFallback(bundleEs, "password.prompt", bundleEn));
            String password = scanner.nextLine();

            String loginResult = switch (role) {
                case "student" -> {
                    if (studentService.validateStudentLogin(username, password)) {
                        Optional<StudentUser> user = studentUsers.stream()
                            .filter(u -> u.getStudent().getRollNumber().equals(username))
                            .findFirst();
                        if (user.isPresent()) {
                            yield handleStudentMenu(user.get(), scanner, analyzer, bundleEn, bundleEs);
                        }
                    }
                    yield "Invalid student credentials. / Credenciales de estudiante inválidas.";
                }
                case "staff" -> {
                    if (username.equals("admin") && staff.authenticate(password)) {
                        yield handleStaffMenu(studentService, courseService, scanner, studentUsers, analyzer, bundleEn, bundleEs);
                    }
                    yield "Invalid staff credentials. / Credenciales de personal inválidas.";
                }
                default -> "Invalid role. / Rol inválido.";
            };
            System.out.println(loginResult);
        }

        scanner.close();
    }

    // Helper method to get string from bundle with fallback to English
    private static String getStringOrFallback(ResourceBundle bundle, String key, ResourceBundle fallback) {
        try {
            return bundle.getString(key);
        } catch (java.util.MissingResourceException e) {
            return fallback.getString(key);
        }
    }

    private static String handleStudentMenu(StudentUser user, Scanner scanner, StudentAnalyzer analyzer, 
                                           ResourceBundle bundleEn, ResourceBundle bundleEs) {
        while (true) {
            System.out.println("\n=== Student Portal / Portal de Estudiantes ===");
            System.out.println("1. View Personal Information / Ver información personal");
            System.out.println("2. Edit Personal Information / Editar información personal");
            System.out.println("3. Analyze My Profile / Analizar mi perfil");
            System.out.println("4. Logout / Cerrar sesión");
            System.out.print("Enter choice / Ingrese opción: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            String result = switch (choice) {
                case 1 -> {
                    user.displayDetails();
                    yield "Displayed information. / Información mostrada.";
                }
                case 2 -> {
                    user.editPersonalInfo(scanner);
                    yield "Information updated. / Información actualizada.";
                }
                case 3 -> {
                    analyzer.analyzeWithPatternMatching(user);
                    yield "Profile analyzed. / Perfil analizado.";
                }
                case 4 -> {
                    yield "Logged out. / Sesión cerrada.";
                }
                default -> "Invalid choice. / Opción inválida.";
            };
            System.out.println(result);
            if (choice == 4) break;
        }
        return "Student session ended. / Sesión de estudiante finalizada.";
    }

    private static String handleStaffMenu(StudentService studentService, CourseService courseService, 
                                         Scanner scanner, List<StudentUser> studentUsers, 
                                         StudentAnalyzer analyzer, ResourceBundle bundleEn, ResourceBundle bundleEs) {
        while (true) {
            System.out.println("\n=== Staff Portal / Portal de Personal ===");
            System.out.println("1. Add Student / Agregar estudiante");
            System.out.println("2. Remove Student / Eliminar estudiante");
            System.out.println("3. Search Student / Buscar estudiante");
            System.out.println("4. Display Students / Mostrar estudiantes");
            System.out.println("5. Add Course / Agregar curso");
            System.out.println("6. Display Courses / Mostrar cursos");
            System.out.println("7. Filter Students by Marks / Filtrar estudiantes por notas");
            System.out.println("8. Count Students by Department / Contar estudiantes por departamento");
            System.out.println("9. Analyze Students / Analizar estudiantes");
            System.out.println("10. Analyze Recent Students / Analizar estudiantes recientes");
            System.out.println("11. Concurrent Analysis / Análisis concurrente");
            System.out.println("12. Save Student Summaries / Guardar resúmenes de estudiantes");
            System.out.println("13. Logout / Cerrar sesión");
            System.out.print("Enter choice / Ingrese opción: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            Object action = switch (choice) {
                case 1 -> {
                    Student student = createStudent(scanner, courseService);
                    studentService.addStudent(student);
                    studentUsers.add(new StudentUser(student, "student123"));
                    yield "Student added. / Estudiante agregado.";
                }
                case 2 -> {
                    System.out.print("Enter Roll Number / Ingrese número de matrícula: ");
                    String roll = scanner.nextLine();
                    studentService.removeStudent(roll);
                    yield "Removal attempted. / Intento de eliminación.";
                }
                case 3 -> {
                    System.out.print("Enter Roll Number / Ingrese número de matrícula: ");
                    String roll = scanner.nextLine();
                    Optional<Student> student = studentService.searchStudent(roll);
                    student.ifPresent(Student::displayDetails);
                    yield student.isPresent() ? "Student found. / Estudiante encontrado." : 
                                               "Student not found. / Estudiante no encontrado.";
                }
                case 4 -> {
                    studentService.displayAllStudents();
                    yield "Displayed students. / Estudiantes mostrados.";
                }
                case 5 -> {
                    System.out.print("Enter Course Name / Ingrese nombre del curso: ");
                    String courseName = scanner.nextLine();
                    courseService.addCourse(new Course(courseName, 12));
                    yield "Course added. / Curso agregado.";
                }
                case 6 -> {
                    courseService.displayAllCourses();
                    yield "Displayed courses. / Cursos mostrados.";
                }
                case 7 -> {
                    System.out.print("Enter minimum marks / Ingrese notas mínimas: ");
                    double marks = scanner.nextDouble();
                    scanner.nextLine();
                    studentService.filterStudents(s -> s.getMarks() >= marks)
                        .forEach(Student::displayDetails);
                    yield "Filtered students. / Estudiantes filtrados.";
                }
                case 8 -> {
                    studentService.countStudentsByDepartment();
                    yield "Displayed department counts. / Conteos de departamentos mostrados.";
                }
                case 9 -> {
                    studentService.analyzeStudents();
                    yield "Analysis completed. / Análisis completado.";
                }
                case 10 -> {
                    analyzer.analyzeRecentStudents();
                    yield "Recent students analyzed. / Estudiantes recientes analizados.";
                }
                case 11 -> {
                    try {
                        analyzer.analyzeConcurrently();
                        yield "Concurrent analysis completed. / Análisis concurrente completado.";
                    } catch (Exception e) {
                        yield "Error in concurrent analysis: " + e.getMessage() + 
                              " / Error en análisis concurrente: " + e.getMessage();
                    }
                }
                case 12 -> {
                    analyzer.saveStudentSummaries();
                    yield "Student summaries saved. / Resúmenes de estudiantes guardados.";
                }
                case 13 -> {
                    yield "Logged out. / Sesión cerrada.";
                }
                default -> "Invalid choice. / Opción inválida.";
            };

            if (action instanceof String msg) {
                System.out.println(msg);
            }
            if (choice == 13) break;
        }
        return "Staff session ended. / Sesión de personal finalizada.";
    }

    private static Student createStudent(Scanner scanner, CourseService courseService) {
        System.out.print("Enter Name / Ingrese nombre: ");
        String name = scanner.nextLine();

        System.out.print("Enter Gender (MALE/FEMALE/OTHER) / Ingrese género (MALE/FEMALE/OTHER): ");
        Gender gender = null;
        while (gender == null) {
            try {
                String genderInput = scanner.nextLine().toUpperCase();
                gender = Gender.valueOf(genderInput);
            } catch (IllegalArgumentException e) {
                System.out.print("Invalid input! Enter again / ¡Entrada inválida! Ingrese de nuevo: ");
            }
        }

        System.out.print("Enter Email / Ingrese correo: ");
        String email = scanner.nextLine();
        while (!email.endsWith("@gmail.com")) {
            System.out.print("Invalid email! Enter again / ¡Correo inválido! Ingrese de nuevo: ");
            email = scanner.nextLine();
        }

        System.out.print("Enter Street / Ingrese calle: ");
        String street = scanner.nextLine();
        System.out.print("Enter City / Ingrese ciudad: ");
        String city = scanner.nextLine();
        System.out.print("Enter Postal Code / Ingrese código postal: ");
        String postalCode = scanner.nextLine();

        System.out.print("Enter Roll Number / Ingrese número de matrícula: ");
        String rollNumber = scanner.nextLine();
        while (!rollNumber.matches("^[a-zA-Z0-9]{8}$")) {
            System.out.print("Invalid roll number! Enter again / ¡Número de matrícula inválido! Ingrese de nuevo: ");
            rollNumber = scanner.nextLine();
        }

        System.out.print("Enter Marks / Ingrese notas: ");
        double marks = 0;
        boolean validMarks = false;
        while (!validMarks) {
            if (scanner.hasNextDouble()) {
                marks = scanner.nextDouble();
                if (marks >= 0 && marks <= 100) {
                    validMarks = true;
                } else {
                    System.out.println("Marks must be between 0 and 100. / Las notas deben estar entre 0 y 100.");
                }
            } else {
                System.out.println("Invalid input! Enter numeric marks. / ¡Entrada inválida! Ingrese notas numéricas.");
                scanner.next();
            }
        }
        scanner.nextLine();

        System.out.print("Enter Course Name / Ingrese nombre del curso: ");
        String courseName = scanner.nextLine();
        Course course = new Course(courseName, 10);
        courseService.addCourse(course);

        System.out.print("Enter Department (AI/DATA_ANALYTICS/SOFTWARE_ENGINEERING/CYBERSECURITY) / " +
                         "Ingrese departamento (AI/DATA_ANALYTICS/SOFTWARE_ENGINEERING/CYBERSECURITY): ");
        Department department = null;
        while (department == null) {
            try {
                department = Department.valueOf(scanner.nextLine().toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.print("Invalid department! Enter again / ¡Departamento inválido! Ingrese de nuevo: ");
            }
        }

        return new Student(name, email, new Address(street, city, postalCode),
                          rollNumber, marks, course, gender, department);
    }
}