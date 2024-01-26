package com.example.engvoyage;

import java.util.List;

public class User {
    private String name;
    private String surname;
    private String email;
    private List<UserCourses> userCoursesList;

    public User() {}

    public User(String name, String surname, String email) {
        this.name = name;
        this.surname = surname;
        this.email = email;
    }

    public void setUserCoursesList(List<UserCourses> userCoursesList) {
        this.userCoursesList = userCoursesList;
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
    public List<UserCourses> getUserCoursesList() { return userCoursesList; }

}
