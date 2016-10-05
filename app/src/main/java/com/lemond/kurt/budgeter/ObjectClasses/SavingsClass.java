package com.lemond.kurt.budgeter.ObjectClasses;

/**
 * Created by kurt_capatan on 3/11/2016.
 */
public class SavingsClass {
    private int savingsId;
    private double savingsAmount;
    private String savingsDateOrDuration;

    public SavingsClass(int savingsId, double savingsAmount, String savingsDateOrDuration) {
        this.savingsId = savingsId;
        this.savingsAmount = savingsAmount;
        this.savingsDateOrDuration = savingsDateOrDuration;
    }

    public SavingsClass(double savingsAmount, String savingsDateOrDuration) {
        this.savingsAmount = savingsAmount;
        this.savingsDateOrDuration = savingsDateOrDuration;
    }

    public int getSavingsId() {
        return savingsId;
    }

    public void setSavingsId(int savingsId) {
        this.savingsId = savingsId;
    }

    public double getSavingsAmount() {
        return savingsAmount;
    }

    public void setSavingsAmount(double savingsAmount) {
        this.savingsAmount = savingsAmount;
    }


    public String getSavingsDateOrDuration() {
        return savingsDateOrDuration;
    }

    // month: yyyy-MMM             day: yyyy-MM-dd
    public void setSavingsDateOrDuration(String savingsDateOrDuration) {
        this.savingsDateOrDuration = savingsDateOrDuration;
    }
}
