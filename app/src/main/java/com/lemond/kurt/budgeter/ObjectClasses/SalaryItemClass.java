package com.lemond.kurt.budgeter.ObjectClasses;

import java.io.Serializable;

/**
 * Created by lemond on 1/24/16.
 */
public class SalaryItemClass implements Serializable{
    private int salaryItemId;
    private String salaryItemName;
    private double salaryItemPrice;
    private int salaryItemQuantity;
    private String salaryItemDuration;

    /*
    * DURATION TYPES
    * */
    public static final String DAILY = "daily", MULTIPLIED_DAILY = "multiplied_daily", MONTHLY = "monthly";

    public SalaryItemClass(int salaryItemId, String salaryItemName, double salaryItemPrice, int salaryItemQuantity, String salaryItemDuration) {
        this.salaryItemId = salaryItemId;
        this.salaryItemName = salaryItemName;
        this.salaryItemPrice = salaryItemPrice;
        this.salaryItemQuantity = salaryItemQuantity;
        this.salaryItemDuration = salaryItemDuration;
    }

    public SalaryItemClass(String salaryItemName, double salaryItemPrice, int salaryItemQuantity, String salaryItemDuration) {
        this.salaryItemName = salaryItemName;
        this.salaryItemPrice = salaryItemPrice;
        this.salaryItemQuantity = salaryItemQuantity;
        this.salaryItemDuration = salaryItemDuration;
    }

    public int getSalaryItemId() {
        return salaryItemId;
    }

    public void setSalaryItemId(int salaryItemId) {
        this.salaryItemId = salaryItemId;
    }

    public String getSalaryItemName() {
        return salaryItemName;
    }

    public void setSalaryItemName(String salaryItemName) {
        this.salaryItemName = salaryItemName;
    }

    public double getSalaryItemPrice() {
        return salaryItemPrice;
    }

    public void setSalaryItemPrice(double salaryItemPrice) {
        this.salaryItemPrice = salaryItemPrice;
    }

    public int getSalaryItemQuantity() {
        return salaryItemQuantity;
    }

    public void setSalaryItemQuantity(int salaryItemQuantity) {
        this.salaryItemQuantity = salaryItemQuantity;
    }

    public String getSalaryItemDuration() {
        return salaryItemDuration;
    }

    public void setSalaryItemDuration(String salaryItemDuration) {
        this.salaryItemDuration = salaryItemDuration;
    }
}
