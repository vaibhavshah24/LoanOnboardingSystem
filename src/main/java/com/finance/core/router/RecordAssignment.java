package com.finance.core.router;

import com.finance.model.Loan;

public interface RecordAssignment {
    void assignRecords();
    void assignLoanToUser(String applicationNo);
    void assignLoanToUser(Loan loan);
    void reassignIdleLoansInQueue();
}
