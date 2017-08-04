package com.finance.core.loanprocessor;

import com.finance.core.input.UserInputCapture;
import com.finance.model.Loan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.finance.enums.ApprovalStatus.*;
import static com.finance.enums.UserRole.LEGAL_MANAGER;

@Component
public class FinanceManagerProcessor extends RoleProcessor implements LoanAction {

    @Autowired
    private UserInputCapture userInputCapture;

    @Override
    public void performCoreAction(String userId, Loan loan, String selectedOption) {

        switch (Integer.parseInt(selectedOption)) {
            case 1: // loan disbursed
                loan.setApprovalStatus(DISBURSED);
                updateLoanAndDisplayPendingLoans(loan, userId, LEGAL_MANAGER, this);
                break;
            case 2: // go back to main menu
                userInputCapture.goToMainMenu();
                break;
            case 3: // exit from system
                userInputCapture.cleanUpAndExit();
                break;
        }
    }
}
