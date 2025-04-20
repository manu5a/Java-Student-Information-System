package com.student.model;

import java.util.Locale;
import java.util.ResourceBundle;

public class LocalizationDemo {
    public static void main(String[] args) {
       
        ResourceBundle bundle = ResourceBundle.getBundle("com.student.model.messages", Locale.getDefault());
        System.out.println(bundle.getString("welcome.message"));
        System.out.println(bundle.getString("login.prompt"));

        
        ResourceBundle spanishBundle = ResourceBundle.getBundle("com.student.model.messages", new Locale("es", "ES"));
        System.out.println("\n--- Spanish Locale ---");
        System.out.println(spanishBundle.getString("welcome.message"));
        System.out.println(spanishBundle.getString("login.prompt"));
    }
}
