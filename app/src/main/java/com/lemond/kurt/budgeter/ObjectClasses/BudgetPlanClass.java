package com.lemond.kurt.budgeter.ObjectClasses;

/**
 * Created by kurt_capatan on 8/30/2016.
 */
public class BudgetPlanClass {

    private int plan_id;
    private String plan_name;
    private double budget_ammount;

    public BudgetPlanClass(int plan_id, String plan_name, double budget_ammount) {
        this.plan_id = plan_id;
        this.plan_name = plan_name;
        this.budget_ammount = budget_ammount;
    }

    public BudgetPlanClass(String plan_name, double budget_ammount) {
        this.plan_name = plan_name;
        this.budget_ammount = budget_ammount;
    }

    public int getPlan_id() {
        return plan_id;
    }

    public void setPlan_id(int plan_id) {
        this.plan_id = plan_id;
    }

    public String getPlan_name() {
        return plan_name;
    }

    public void setPlan_name(String plan_name) {
        this.plan_name = plan_name;
    }

    public double getBudget_ammount() {
        return budget_ammount;
    }

    public void setBudget_ammount(double budget_ammount) {
        this.budget_ammount = budget_ammount;
    }
}
