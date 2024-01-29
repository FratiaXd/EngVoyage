package com.example.engvoyage;

import java.util.List;

public class User {
    private String name;
    private String surname;
    private String email;

    public User() {}

    public User(String name, String surname, String email) {
        this.name = name;
        this.surname = surname;
        this.email = email;
    }

    public String getName() {
        return name;
    }
    public String getSurname() {
        return surname;
    }
    public String getEmail() {
        return email;
    }

}
