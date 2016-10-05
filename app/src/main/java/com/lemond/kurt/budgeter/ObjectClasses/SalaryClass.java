package com.lemond.kurt.budgeter.ObjectClasses;

import java.util.Date;

/**
 * Created by kurt_capatan on 1/15/2016.
 */
public class SalaryClass {
    private int salaryId;
    private double salaryAmount;
    private String salaryDateChanged;

    public SalaryClass(int salaryId, double salaryAmount, String salaryDateChanged) {
        this.salaryId = salaryId;
        this.salaryAmount = salaryAmount;
        this.salaryDateChanged = salaryDateChanged;
    }

    public SalaryClass(double salaryAmount, String salaryDateChanged) {
        this.salaryAmount = salaryAmount;
        this.salaryDateChanged = salaryDateChanged;
    }

    public int getSalaryId() {
        return salaryId;
    }

    public void setSalaryId(int salaryId) {
        this.salaryId = salaryId;
    }

    public double getSalaryAmount() {
        return salaryAmount;
    }

    public void setSalaryAmount(double salaryAmount) {
        this.salaryAmount = salaryAmount;
    }

    public String getSalaryDateChanged() {
        return salaryDateChanged;
    }

    public void setSalaryDateChanged(String salaryDateChanged) {
        this.salaryDateChanged = salaryDateChanged;
    }
}
