package com.finance.model;

import com.finance.core.router.LoanUpdateListener;
import com.finance.enums.ApprovalStatus;
import com.finance.enums.Zone;
import org.springframework.data.annotation.Id;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.finance.enums.ApprovalStatus.NEW;

public class Loan {

    @Id
    private String applicationNo;

    public String getApplicationNo() {
        return applicationNo;
    }

    private Customer customer;
    private String item;
    private BigDecimal amount;
    private Zone zone;
    private int tenure;
    private List<LoanUpdateListener> loanUpdateListenerList = new ArrayList<>();
    private ApprovalStatus approvalStatus;
    private LocalDateTime createdOn; // record created on
    private LocalDateTime updatedOn; // last updated on
    private String assignee; // to store the user id of the user who is currently working on the loan

    public Loan(Customer customer, String applicationNo, String item, BigDecimal amount, int tenure, Zone zone) {
        this.customer = customer;
        this.applicationNo = applicationNo;
        this.item = item;
        this.amount = amount;
        this.tenure = tenure;
        this.zone = zone;
        this.approvalStatus = NEW; // default approval status is new when the loan record is created in DB.
        this.createdOn = LocalDateTime.now();
    }

    public Customer getCustomer() {
        return customer;
    }

    public String getItem() {
        return item;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public ApprovalStatus getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(ApprovalStatus approvalStatus) {
        if (this.approvalStatus != approvalStatus) {
            this.approvalStatus = approvalStatus;
            System.out.println("Loan Application No " + getApplicationNo() + "'s status changed to " + approvalStatus + ".");
        }
    }

    public LocalDateTime getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(LocalDateTime updatedOn) {
        this.updatedOn = updatedOn;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public Zone getZone() {
        return zone;
    }

    public List<LoanUpdateListener> getLoanUpdateListenerList() {
        return loanUpdateListenerList;
    }

    public void notifyListeners() {
        loanUpdateListenerList.forEach(p -> p.update(this));
    }

    public void attachListener(LoanUpdateListener listener) {
        loanUpdateListenerList.add(listener);
    }

    public void removeListeners() {
        loanUpdateListenerList.clear();
    }

    @Override
    public String toString() {
        StringBuilder loanInformation = new StringBuilder();
        loanInformation.append("Application No: " + this.applicationNo);
        loanInformation.append("\n");
        loanInformation.append("Loan Amount: " + this.amount);
        loanInformation.append("\n");
        loanInformation.append("Loan Tenure: " + this.tenure + " Months");
        loanInformation.append("\n");
        loanInformation.append("Loan Status: " + this.getApprovalStatus());
        loanInformation.append("\n");
        loanInformation.append("Loan on item: " + this.item);
        loanInformation.append("\n");
        loanInformation.append("Customer Name: " + this.customer.getName());
        loanInformation.append("\n");
        loanInformation.append("Customer ID Type: " + this.customer.getIdType());
        loanInformation.append("\n");
        loanInformation.append("Customer ID Number: " + this.customer.getIdNumber());
        loanInformation.append("\n");
        return loanInformation.toString();
    }
}

