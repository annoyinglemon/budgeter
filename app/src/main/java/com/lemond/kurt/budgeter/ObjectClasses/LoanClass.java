package com.lemond.kurt.budgeter.ObjectClasses;

/**
 * Created by kurt_capatan on 2/15/2016.
 */
public class LoanClass {
    private int id;
    private String lenderName;
    private double loanAmount;
    private String loanDate;

    public LoanClass(int id, String lenderName, double loanAmount, String loanDate) {
        this.id = id;
        this.lenderName = lenderName;
        this.loanAmount = loanAmount;
        this.loanDate = loanDate;
    }

    public LoanClass(String lenderName, double loanAmount, String loanDate) {
        this.lenderName = lenderName;
        this.loanAmount = loanAmount;
        this.loanDate = loanDate;
    }

    public LoanClass(int id, String lenderName, double loanAmount) {
        this.id = id;
        this.lenderName = lenderName;
        this.loanAmount = loanAmount;
    }

    public LoanClass(String lenderName, double loanAmount) {
        this.lenderName = lenderName;
        this.loanAmount = loanAmount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLenderName() {
        return lenderName;
    }

    public void setLenderName(String lenderName) {
        this.lenderName = lenderName;
    }

    public double getLoanAmount() {
        return loanAmount;
    }

    public void setLoanAmount(double loanAmount) {
        this.loanAmount = loanAmount;
    }

    public String getLoanDate() {
        return loanDate;
    }

    public void setLoanDate(String loanDate) {
        this.loanDate = loanDate;
    }
}
