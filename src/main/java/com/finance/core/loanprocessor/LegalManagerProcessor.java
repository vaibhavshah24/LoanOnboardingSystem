package com.finance.core.loanprocessor;

import com.finance.core.input.UserInputCapture;
import com.finance.model.Loan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.finance.enums.ApprovalStatus.*;
import static com.finance.enums.UserRole.LEGAL_MANAGER;
import static com.finance.enums.UserRole.RISK_MANAGER;

@Component
public class LegalManagerProcessor extends RoleProcessor implements LoanAction {

    @Autowired
    private UserInputCapture userInputCapture;

    @Override
    public void performCoreAction(String userId, Loan loan, String selectedOption) {

        switch (Integer.parseInt(selectedOption)) {
            case 1: // loan approved
                loan.setApprovalStatus(APPROVED_BY_LEGAL_MANAGER);
                updateLoanAndDisplayPendingLoans(loan, userId, LEGAL_MANAGER, this);
                break;
            case 2: // loan rejected
                loan.setApprovalStatus(REJECTED_BY_LEGAL_MANAGER);
                updateLoanAndDisplayPendingLoans(loan, userId, LEGAL_MANAGER, this);
                break;
            case 3: // loan sent for further approval
                loan.setApprovalStatus(CLARIFICATION_REQUESTED_BY_LEGAL_MANAGER);
                updateLoanAndDisplayPendingLoans(loan, userId, LEGAL_MANAGER, this);
                break;
            case 4: // go back to main menu
                userInputCapture.goToMainMenu();
                break;
            case 5: // exit from system
                userInputCapture.cleanUpAndExit();
                break;
        }
    }
}
