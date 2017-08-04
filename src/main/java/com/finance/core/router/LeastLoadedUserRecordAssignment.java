package com.finance.core.router;

import com.finance.enums.UserRole;
import com.finance.model.Loan;
import com.finance.model.User;
import com.finance.respository.LoanRepository;
import com.finance.respository.UserRepository;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.CommandResult;
import com.sun.org.apache.xpath.internal.objects.XObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapreduce.GroupBy;
import org.springframework.data.mongodb.core.mapreduce.GroupByResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.finance.data.DataMap.APPROVAL_HIERARCHY;
import static com.finance.enums.ApprovalStatus.NEW;
import static com.finance.enums.ApprovalStatus.PENDING;

@Component
@ConditionalOnProperty(name = "loan.routing.strategy.leastLoadedUserRecordAssignment", matchIfMissing = false)
public class LeastLoadedUserRecordAssignment implements RecordAssignment {

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LoanUpdateListener loanUpdateListener;

    @Autowired
    MongoTemplate mongoTemplate;

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
            List<User> applicableUsers = userRepository.findByUserRoleAndZone(nextApprovalLevel, loan.getZone());
            assignLoanToUserWithMinimumRecords(loan, applicableUsers, false);
        }
    }

    @Override
    public void reassignIdleLoansInQueue() {
        List<Loan> loans = loanRepository.findByUpdatedOnLessThan(LocalDateTime.now().minusSeconds(30));
        for (Loan loan : loans) {
            User currentUser = userRepository.findOne(loan.getAssignee());
            List<User> otherUsersInSameQueue = userRepository.findByUserRoleAndZoneAndUserIdNot(currentUser.getUserRole(), currentUser.getZone(), currentUser.getUserId());
            assignLoanToUserWithMinimumRecords(loan, otherUsersInSameQueue, true);
        }
    }

    private void assignLoanToUserWithMinimumRecords(Loan loan, List<User> applicableUsers, boolean isReassigned) {
        if (!applicableUsers.isEmpty()) {

            if (applicableUsers.size() == 1) {
                // since there is only one other valid user whom the loan can be assigned, no need to check for loans currently assigned to him/her.
                updateLoanAndUserRecords(loan, applicableUsers.get(0), isReassigned);
            } else {
                // since there are more than one valid users whom the loan can be assigned, we need to find the user with least no of loans at the moment.
                // Create a hashmap, which will store how many loans each user is working upon.
                HashMap<String, Integer> userLoanCountMap = new HashMap<>();
                applicableUsers.forEach(p -> userLoanCountMap.put(p.getUserId(), 0));

                // Create a list of user ids which will be passed to MongoDB to get loans each user is working upon.
                List<String> userIDs = applicableUsers.stream().map(p -> p.getUserId()).collect(Collectors.toList());
                GroupByResults<XObject> loansWithEachUser = mongoTemplate.group(Criteria.where("assignee").in(userIDs), "loan",
                        GroupBy.key("assignee").initialDocument("{ count: 0 }").reduceFunction("function(doc, prev) { prev.count += 1 }"),
                        XObject.class);

                if (loansWithEachUser != null) {
                    // Update hashmap with the data fetched from MongoDB.
                    BasicDBList userLoanList = ((BasicDBList) ((CommandResult) (loansWithEachUser.getRawResults())).entrySet().iterator().next().getValue());
                    if (!userLoanList.isEmpty()) {
                        for (Object x : userLoanList) {
                            userLoanCountMap.put(((BasicDBObject) x).get("assignee").toString(), (int) Double.parseDouble(((BasicDBObject) x).get("count").toString()));
                        }
                    }

                    // Find user who has minimum loans assigned to him/her, and assign him/her to current loan.
                    Map.Entry<String, Integer> userWithLeastLoans = Collections.min(userLoanCountMap.entrySet(), Comparator.comparingInt(Map.Entry::getValue));
                    User userToBeAssigned = applicableUsers.stream().filter(p -> p.getUserId().equals(userWithLeastLoans.getKey())).collect(Collectors.toList()).get(0);
                    updateLoanAndUserRecords(loan, userToBeAssigned, isReassigned);
                }
            }
        }
    }

    private void updateLoanAndUserRecords(Loan loan, User user, boolean isReassigned) {
        loan.setAssignee(user.getUserId());
        loan.setUpdatedOn(LocalDateTime.now());
        loanRepository.save(loan);
        user.setLastRecordAssignedOn(LocalDateTime.now());
        userRepository.save(user);
        if (isReassigned)
            System.out.println("Reassigned loan application " + loan.getApplicationNo() + " to the user " + user.getFullName() + ".");
        else
            System.out.println("Assigned loan application " + loan.getApplicationNo() + " to the user " + user.getFullName() + ".");
    }
}
