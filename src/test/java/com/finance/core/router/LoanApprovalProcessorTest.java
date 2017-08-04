package com.finance.core.router;

import com.finance.core.router.LoanApprovalProcessor;
import com.finance.data.DataMap;
import com.finance.enums.ApprovalStatus;
import com.finance.enums.Zone;
import com.finance.model.Customer;
import com.finance.model.Loan;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.jms.core.JmsTemplate;

import java.math.BigDecimal;

import static com.finance.data.DataMap.*;
import static com.finance.enums.ApprovalStatus.*;
import static com.finance.enums.Zone.NORTH;

@RunWith(MockitoJUnitRunner.class)
public class LoanApprovalProcessorTest {

    private static Loan loan = null;
    @BeforeClass
    public static void setUp() throws Exception {
        prepareDataMap();
        loan = new Loan(new Customer("Jay", "PAN", "XYZ"), "1", "TV", new BigDecimal("100"), 24,NORTH);
    }

    @Mock
    JmsTemplate jmsTemplate;

    @InjectMocks
    LoanApprovalProcessor loanApprovalProcessor;

    @Test
    public void shouldSendMessageToUnderwriterQueueForNewRecord() throws Exception {
        loan.setApprovalStatus(PENDING);
        loanApprovalProcessor.update(loan);
        Mockito.verify(jmsTemplate).convertAndSend(UNDERWRITER_QUEUE_NAME, "1");
    }

    @Test
    public void shouldSendMessageToUnderwriterQueueAfterRiskManagerClarificationRequest() throws Exception {
        loan.setApprovalStatus(CLARIFICATION_REQUESTED_BY_RISK_MANAGER);
        loanApprovalProcessor.update(loan);
        Mockito.verify(jmsTemplate).convertAndSend(UNDERWRITER_QUEUE_NAME, "1");
    }

    @Test
    public void shouldSendMessageToRiskManagerQueueAfterUnderwriterApproval() throws Exception {
        loan.setApprovalStatus(APPROVED_BY_UNDERWRITER);
        loanApprovalProcessor.update(loan);
        Mockito.verify(jmsTemplate).convertAndSend(RISK_MANAGER_QUEUE_NAME, "1");
    }

    @Test
    public void shouldSendMessageToRiskManagerQueueAfterLegalManagerClarificationRequest() throws Exception {
        loan.setApprovalStatus(CLARIFICATION_REQUESTED_BY_LEGAL_MANAGER);
        loanApprovalProcessor.update(loan);
        Mockito.verify(jmsTemplate).convertAndSend(RISK_MANAGER_QUEUE_NAME, "1");
    }

    @Test
    public void shouldSendMessageToLegalManagerQueueAfterRiskManagerApproval() throws Exception {
        loan.setApprovalStatus(APPROVED_BY_RISK_MANAGER);
        loanApprovalProcessor.update(loan);
        Mockito.verify(jmsTemplate).convertAndSend(LEGAL_MANAGER_QUEUE_NAME, "1");
    }

    @Test
    public void shouldSendMessageToFinanceManagerQueueAfterLegalManagerApproval() throws Exception {
        loan.setApprovalStatus(APPROVED_BY_LEGAL_MANAGER);
        loanApprovalProcessor.update(loan);
        Mockito.verify(jmsTemplate).convertAndSend(FINANCE_MANAGER_QUEUE_NAME, "1");
    }

    @Test
    public void shouldNotSendMessageToJmsQueueAfterUnderwriterRejects() throws Exception {
        loan.setApprovalStatus(REJECTED_BY_UNDERWRITER);
        loanApprovalProcessor.update(loan);
        Mockito.verify(jmsTemplate, Mockito.never()).convertAndSend(UNDERWRITER_QUEUE_NAME, "1");
        Mockito.verify(jmsTemplate, Mockito.never()).convertAndSend(RISK_MANAGER_QUEUE_NAME, "1");
        Mockito.verify(jmsTemplate, Mockito.never()).convertAndSend(LEGAL_MANAGER_QUEUE_NAME, "1");
        Mockito.verify(jmsTemplate, Mockito.never()).convertAndSend(FINANCE_MANAGER_QUEUE_NAME, "1");
    }

    @Test
    public void shouldNotSendMessageToJmsQueueAfterRiskManagerRejects() throws Exception {
        loan.setApprovalStatus(REJECTED_BY_RISK_MANAGER);
        loanApprovalProcessor.update(loan);
        Mockito.verify(jmsTemplate, Mockito.never()).convertAndSend(UNDERWRITER_QUEUE_NAME, "1");
        Mockito.verify(jmsTemplate, Mockito.never()).convertAndSend(RISK_MANAGER_QUEUE_NAME, "1");
        Mockito.verify(jmsTemplate, Mockito.never()).convertAndSend(LEGAL_MANAGER_QUEUE_NAME, "1");
        Mockito.verify(jmsTemplate, Mockito.never()).convertAndSend(FINANCE_MANAGER_QUEUE_NAME, "1");
    }

    @Test
    public void shouldNotSendMessageToJmsQueueAfterLegalManagerRejects() throws Exception {
        loan.setApprovalStatus(REJECTED_BY_LEGAL_MANAGER);
        loanApprovalProcessor.update(loan);
        Mockito.verify(jmsTemplate, Mockito.never()).convertAndSend(UNDERWRITER_QUEUE_NAME, "1");
        Mockito.verify(jmsTemplate, Mockito.never()).convertAndSend(RISK_MANAGER_QUEUE_NAME, "1");
        Mockito.verify(jmsTemplate, Mockito.never()).convertAndSend(LEGAL_MANAGER_QUEUE_NAME, "1");
        Mockito.verify(jmsTemplate, Mockito.never()).convertAndSend(FINANCE_MANAGER_QUEUE_NAME, "1");
    }

    @Test
    public void shouldNotSendMessageToJmsQueueAfterFinanceManagerDisbursesLoan() throws Exception {
        loan.setApprovalStatus(DISBURSED);
        loanApprovalProcessor.update(loan);
        Mockito.verify(jmsTemplate, Mockito.never()).convertAndSend(UNDERWRITER_QUEUE_NAME, "1");
        Mockito.verify(jmsTemplate, Mockito.never()).convertAndSend(RISK_MANAGER_QUEUE_NAME, "1");
        Mockito.verify(jmsTemplate, Mockito.never()).convertAndSend(LEGAL_MANAGER_QUEUE_NAME, "1");
        Mockito.verify(jmsTemplate, Mockito.never()).convertAndSend(FINANCE_MANAGER_QUEUE_NAME, "1");
    }

}