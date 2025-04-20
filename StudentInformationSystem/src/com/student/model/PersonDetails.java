    package com.student.model;

    public sealed interface PersonDetails permits Student, MainApp.UserRole {
        void displayDetails();
    }