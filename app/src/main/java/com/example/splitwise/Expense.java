package com.example.splitwise;

import java.io.Serializable;

/**
 * Data model for an Expense.
 */
public class Expense implements Serializable {
    private String id;
    private String title;
    private double amount;
    private String category;
    private String date;
    private String notes;

    public Expense() {
        // Default constructor required for Firebase
    }

    public Expense(String id, String title, double amount, String category, String date, String notes) {
        this.id = id;
        this.title = title;
        this.amount = amount;
        this.category = category;
        this.date = date;
        this.notes = notes;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
