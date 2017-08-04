package com.finance.core.router;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import static com.finance.data.DataMap.*;

@Component
public class MessageQueueProcessor {

    @Autowired
    private RecordAssignment recordAssignment;

    @JmsListener(destination = UNDERWRITER_QUEUE_NAME, containerFactory = "myFactory")
    public void assignLoanToUnderwriter(String applicationNo) {
        //System.out.println("Received applicationNo <" + applicationNo + "> on UnderWriterQueue");
        recordAssignment.assignLoanToUser(applicationNo);
    }

    @JmsListener(destination = RISK_MANAGER_QUEUE_NAME, containerFactory = "myFactory")
    public void assignLoanToRiskManager(String applicationNo) {
        //System.out.println("Received applicationNo <" + applicationNo + "> on RiskManagerQueue ");
        recordAssignment.assignLoanToUser(applicationNo);
    }

    @JmsListener(destination = LEGAL_MANAGER_QUEUE_NAME, containerFactory = "myFactory")
    public void assignLoanToLegalManager(String applicationNo) {
        //System.out.println("Received applicationNo <" + applicationNo + "> on LegalManagerQueue");
        recordAssignment.assignLoanToUser(applicationNo);
    }

    @JmsListener(destination = FINANCE_MANAGER_QUEUE_NAME, containerFactory = "myFactory")
    public void assignLoanToFinanceManager(String applicationNo) {
        //System.out.println("Received applicationNo <" + applicationNo + "> on FinanceManagerQueue");
        recordAssignment.assignLoanToUser(applicationNo);
    }
}
