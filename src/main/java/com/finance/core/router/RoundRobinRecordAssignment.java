package com.finance.core.router;

import com.finance.enums.UserRole;
import com.finance.model.Loan;
import com.finance.model.User;
import com.finance.respository.LoanRepository;
import com.finance.respository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

import static com.finance.data.DataMap.APPROVAL_HIERARCHY;
import static com.finance.enums.ApprovalStatus.NEW;
import static com.finance.enums.ApprovalStatus.PENDING;

@Component
@ConditionalOnProperty(name = "loan.routing.strategy.roundRobinRecordAssignment", matchIfMissing = true)
public class RoundRobinRecordAssignment implements RecordAssignment {

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LoanUpdateListener loanUpdateListener;

    @Override
    public void assignRecords() {
        List<Loan> newLoans = loanRepository.findByApprovalStatus(NEW);
        for (Loan newLoan : newLoans) {
            newLoan.attachListener(loanUpdateListener);
            newLoan.setApprovalStatus(PENDING); // set the approval process in motion by changing status from New to Pending.
            loanRepository.save(newLoan); // save new status of record in DB.
            newLoan.notifyListeners();
        }
    }

    @Override
    public void assignLoanToUser(String applicationNo) {
        Loan loan = loanRepository.findOne(applicationNo); // get this loan from DB to assign it to the next approver.
        assignLoanToUser(loan);
    }

    @Override
    public void assignLoanToUser(Loan loan) {
        UserRole nextApprovalLevel = APPROVAL_HIERARCHY.get(loan.getApprovalStatus());
        if (nextApprovalLevel != null) {
            User user = userRepository.findFirstByUserRoleAndZoneOrderByLastRecordAssignedOnAsc(nextApprovalLevel, loan.getZone());
            if (user != null) {
                loan.setAssignee(user.getUserId());
                loan.setUpdatedOn(LocalDateTime.now());
                loanRepository.save(loan);
                user.setLastRecordAssignedOn(LocalDateTime.now());
                userRepository.save(user);
                System.out.println("Assigned loan application " + loan.getApplicationNo() + " to the user " + user.getFullName() + ".");
            }
        }
    }

    @Override
    public void reassignIdleLoansInQueue() {
        List<Loan> loans = loanRepository.findByUpdatedOnLessThan(LocalDateTime.now().minusSeconds(30));
        for (Loan loan : loans) {
            User currentUser = userRepository.findOne(loan.getAssignee());
            User newUserInSameQueue = userRepository.findFirstByUserRoleAndZoneAndUserIdNotOrderByLastRecordAssignedOnAsc(currentUser.getUserRole(), currentUser.getZone(), currentUser.getUserId());
            if (newUserInSameQueue != null) {
                loan.setAssignee(newUserInSameQueue.getUserId());
                loan.setUpdatedOn(LocalDateTime.now());
                loanRepository.save(loan);
                newUserInSameQueue.setLastRecordAssignedOn(LocalDateTime.now());
                userRepository.save(newUserInSameQueue);
                System.out.println("Reassigned loan application " + loan.getApplicationNo() + " to the user " + newUserInSameQueue.getFullName() + ".");
            }
        }
    }
}
