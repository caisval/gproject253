package com.example.lab_rest.model;

public class Category {
    private int id;
    private String categoryName;

    // Default constructor
    public Category() {}

    // Parameterized constructor
    public Category(int id, String categoryName) {
        this.id = id;
        this.categoryName = categoryName;
    }

    // Getter and Setter for id
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    // Getter and Setter for categoryName
    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    // toString method
    @Override
    public String toString() {
        return categoryName;
    }
}
