package com.lemond.kurt.budgeter.ObjectClasses;

import java.io.Serializable;

/**
 * Created by kurt_capatan on 1/25/2016.
 */
public class ActualExpensesClass implements Serializable{
    private int actualExpenseId;
    private String actualExpenseName;
    private double actualExpensePrice;
    private int actualExpenseQuantity;
    private String acutalExpenseDate;
    private String actualExpenseDuration;

    public static final String DAILY = "daily", MONTHLY = "monthly";

    public ActualExpensesClass(int actualExpenseId, String actualExpenseName, double actualExpensePrice, int actualExpenseQuantity, String acutalExpenseDate, String actualExpenseDuration) {
        this.actualExpenseId = actualExpenseId;
        this.actualExpenseName = actualExpenseName;
        this.actualExpensePrice = actualExpensePrice;
        this.actualExpenseQuantity = actualExpenseQuantity;
        this.acutalExpenseDate = acutalExpenseDate;
        this.actualExpenseDuration = actualExpenseDuration;
    }

    public ActualExpensesClass(String actualExpenseName, double actualExpensePrice, int actualExpenseQuantity, String acutalExpenseDate, String actualExpenseDuration) {
        this.actualExpenseName = actualExpenseName;
        this.actualExpensePrice = actualExpensePrice;
        this.actualExpenseQuantity = actualExpenseQuantity;
        this.acutalExpenseDate = acutalExpenseDate;
        this.actualExpenseDuration = actualExpenseDuration;
    }

    public int getActualExpenseId() {
        return actualExpenseId;
    }

    public void setActualExpenseId(int actualExpenseId) {
        this.actualExpenseId = actualExpenseId;
    }

    public String getActualExpenseName() {
        return actualExpenseName;
    }

    public void setActualExpenseName(String actualExpensename) {
        this.actualExpenseName = actualExpensename;
    }

    public double getActualExpensePrice() {
        return actualExpensePrice;
    }

    public void setActualExpensePrice(double actualExpensePrice) {
        this.actualExpensePrice = actualExpensePrice;
    }

    public int getActualExpenseQuantity() {
        return actualExpenseQuantity;
    }

    public void setActualExpenseQuantity(int actualExpenseQuantity) {
        this.actualExpenseQuantity = actualExpenseQuantity;
    }

    public String getAcutalExpenseDate() {
        return acutalExpenseDate;
    }

    public void setAcutalExpenseDate(String acutalExpenseDate) {
        this.acutalExpenseDate = acutalExpenseDate;
    }

    public String getActualExpenseDuration() {
        return actualExpenseDuration;
    }

    public void setActualExpenseDuration(String actualExpenseDuration) {
        this.actualExpenseDuration = actualExpenseDuration;
    }
}
