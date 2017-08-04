package com.finance.core.loanprocessor;

import com.finance.model.Loan;

public interface LoanAction {
    void performCoreAction(String userId, Loan loan, String selectedOption);
}
