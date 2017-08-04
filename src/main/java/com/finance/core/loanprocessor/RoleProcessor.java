package com.finance.core.loanprocessor;

import com.finance.core.input.UserInputCapture;
import com.finance.core.router.LoanUpdateListener;
import com.finance.enums.UserRole;
import com.finance.model.Loan;
import com.finance.model.User;
import com.finance.respository.LoanRepository;
import com.finance.respository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import static com.finance.data.DataMap.MENU_ITEMS;
import static com.finance.data.DataMap.*;

@Component
public class RoleProcessor {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserInputCapture userInputCapture;

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private LoanUpdateListener loanUpdateListener;

    public void performLogin(UserRole userRole, LoanAction roleProcessor) {
        String[] credentials = userInputCapture.getLoginCredentials();
        String userId = validateLoginCredentials(credentials[0], credentials[1], userRole);
        if (userId != null) {
            displayLoans(userId, userRole, roleProcessor);
        } else {
            userInputCapture.goToMainMenu();
        }
    }

    private String validateLoginCredentials(String username, String password, UserRole userRole) {
        String userId = null;

        User user = userRepository.findByUsernameAndPasswordAndUserRole(username, password, userRole);
        if (user != null) {
            userId = user.getUserId();
            System.out.println("Login successful. Welcome " + user.getFullName() + "!");
        } else {
            System.out.println("Authentication failed.");
        }

        return userId;
    }

    private void displayLoans(String userId, UserRole userRole, LoanAction roleProcessor) {
        List<Loan> loanList = loanRepository.findByAssignee(userId);
        if (!loanList.isEmpty()) {
            //for (int loanCounter = 0; loanCounter< loanList.size(); loanCounter++) {
                System.out.println("You have " + loanList.size() + " loan requests to process. Here is the first loan information: ");
                Loan loan = loanList.get(0); // display the first loan
                //Loan loan = loanList.get(loanCounter); // display the first loan
                loan.removeListeners();
                loan.attachListener(loanUpdateListener);
                System.out.println(loan.toString()); // display loan information
                userInputCapture.displayMenu(USER_ROLE_MENU_MAPPING.get(userRole));
                String selectedOption = null;
                do {
                    selectedOption = userInputCapture.getUserInput();
                }
                while (!userInputCapture.validateMenuSelection(MENU_ITEMS.get(USER_ROLE_MENU_MAPPING.get(userRole)), selectedOption));
                roleProcessor.performCoreAction(userId, loan, selectedOption);
            //}
        } else {
            System.out.println("Yayy! No pending loan requests to process.");
            userInputCapture.goToMainMenu();
        }
    }

    public void updateLoanAndDisplayPendingLoans(Loan loan, String userId, UserRole userRole, LoanAction roleProcessor) {
        loan.setAssignee(""); // make the assignee blank, because the new assignee will be assigned by record assignment algorithm
        loan.setUpdatedOn(LocalDateTime.now());
        loanRepository.save(loan); // save updated state of loan in database.
        loan.notifyListeners();
        displayLoans(userId, userRole, roleProcessor);
    }
}
