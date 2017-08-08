package com.finance.respository;

import com.finance.enums.ApprovalStatus;
import com.finance.model.Loan;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface LoanRepository extends MongoRepository<Loan, String> {
    List<Loan> findByApprovalStatus(ApprovalStatus status);

    List<Loan> findByAssignee(String userId);

    List<Loan> findByUpdatedOnLessThan(LocalDateTime date);
}
