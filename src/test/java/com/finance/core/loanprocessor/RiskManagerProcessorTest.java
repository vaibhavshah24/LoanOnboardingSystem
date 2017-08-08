package com.finance.core.loanprocessor;

import com.finance.core.input.UserInputCapture;
import com.finance.model.Customer;
import com.finance.model.Loan;
import com.finance.respository.LoanRepository;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;

import static com.finance.enums.ApprovalStatus.*;
import static com.finance.enums.Zone.NORTH;

@RunWith(MockitoJUnitRunner.class)
public class RiskManagerProcessorTest {
    private static Loan loan = null;

    @BeforeClass
    public static void setUp() throws Exception {
        loan = new Loan(new Customer("Jay", "PAN", "XYZ"), "1", "TV", new BigDecimal("100"), 24, NORTH);
    }

    @Mock
    private UserInputCapture userInputCapture;

    @Mock
    private LoanRepository loanRepository; // required for RoleProcessor.updateLoanAndDisplayPendingLoans method.

    @InjectMocks
    RiskManagerProcessor riskManagerProcessor;

    @Test
    public void shouldApproveLoan() throws Exception {
        riskManagerProcessor.performCoreAction("1", loan, "1");
        Assert.assertEquals(loan.getApprovalStatus(), APPROVED_BY_RISK_MANAGER);
    }

    @Test
    public void shouldRejectLoan() throws Exception {
        riskManagerProcessor.performCoreAction("1", loan, "2");
        Assert.assertEquals(loan.getApprovalStatus(), REJECTED_BY_RISK_MANAGER);
    }

    @Test
    public void shouldSendLoanForClarification() throws Exception {
        riskManagerProcessor.performCoreAction("1", loan, "3");
        Assert.assertEquals(loan.getApprovalStatus(), CLARIFICATION_REQUESTED_BY_RISK_MANAGER);
    }

    @Test
    public void shouldGoToMainMenu() throws Exception {
        riskManagerProcessor.performCoreAction("1", loan, "4");
        Mockito.verify(userInputCapture).goToMainMenu();
    }

    @Test
    public void shouldExitFromSystem() throws Exception {
        riskManagerProcessor.performCoreAction("1", loan, "5");
        Mockito.verify(userInputCapture).cleanUpAndExit();
    }

}