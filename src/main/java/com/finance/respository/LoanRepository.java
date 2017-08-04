package com.finance.respository;

import com.finance.enums.ApprovalStatus;
import com.finance.model.Loan;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by vashah on 2017-07-26.
 */
public interface LoanRepository extends MongoRepository<Loan, String> {

    List<Loan> findByApprovalStatus(ApprovalStatus status);
    //long countByAssignee(String userId);
    List<Loan> findByAssignee(String userId);
    List<Loan> findByUpdatedOnLessThan(LocalDateTime date);

    @Query("[{$group : {_id : \"$by_user\", num_tutorial : {$sum : 1}}}]")
    List<Loan> findByAssigneeIn(String[] assignee);
}
