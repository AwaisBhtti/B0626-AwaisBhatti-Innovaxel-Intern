package com.example.splitwise;

public class CategorySummary {
    private String category;
    private double amount;

    public CategorySummary(String category, double amount) {
        this.category = category;
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public double getAmount() {
        return amount;
    }
}
