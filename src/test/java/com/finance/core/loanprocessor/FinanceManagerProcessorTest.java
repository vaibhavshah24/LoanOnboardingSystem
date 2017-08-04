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

import static com.finance.enums.ApprovalStatus.DISBURSED;
import static com.finance.enums.Zone.NORTH;

@RunWith(MockitoJUnitRunner.class)
public class FinanceManagerProcessorTest {

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
    FinanceManagerProcessor financeManagerProcessor;

    @Test
    public void shouldDisburseLoan() throws Exception {
        financeManagerProcessor.performCoreAction("1", loan, "1");
        Assert.assertEquals(loan.getApprovalStatus(), DISBURSED);
    }

    @Test
    public void shouldGoToMainMenu() throws Exception {
        financeManagerProcessor.performCoreAction("1", loan, "2");
        Mockito.verify(userInputCapture).goToMainMenu();
    }

    @Test
    public void shouldExitFromSystem() throws Exception {
        financeManagerProcessor.performCoreAction("1", loan, "3");
        Mockito.verify(userInputCapture).cleanUpAndExit();
    }

}