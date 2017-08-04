package com.finance.core.loanprocessor;

import com.finance.core.input.UserInputCapture;
import com.finance.model.Loan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.finance.enums.ApprovalStatus.APPROVED_BY_UNDERWRITER;
import static com.finance.enums.ApprovalStatus.REJECTED_BY_UNDERWRITER;
import static com.finance.enums.UserRole.UNDERWRITER;

@Component
public class UnderwriterProcessor extends RoleProcessor implements LoanAction {

    @Autowired
    private UserInputCapture userInputCapture;

    @Override
    public void performCoreAction(String userId, Loan loan, String selectedOption) {

        switch (Integer.parseInt(selectedOption)) {
            case 1: // loan approved
                loan.setApprovalStatus(APPROVED_BY_UNDERWRITER);
                updateLoanAndDisplayPendingLoans(loan, userId, UNDERWRITER, this);
                break;
            case 2: // loan rejected
                loan.setApprovalStatus(REJECTED_BY_UNDERWRITER);
                updateLoanAndDisplayPendingLoans(loan, userId, UNDERWRITER, this);
                break;
            case 3: // go back to main menu
                userInputCapture.goToMainMenu();
                break;
            case 4: // exit from system
                userInputCapture.cleanUpAndExit();
                break;
        }
    }
}
