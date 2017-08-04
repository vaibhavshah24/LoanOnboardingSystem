package com.finance.core.loanprocessor;

import com.finance.core.input.UserInputCapture;
import com.finance.enums.Zone;
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

import static com.finance.enums.ApprovalStatus.APPROVED_BY_LEGAL_MANAGER;
import static com.finance.enums.ApprovalStatus.CLARIFICATION_REQUESTED_BY_LEGAL_MANAGER;
import static com.finance.enums.ApprovalStatus.REJECTED_BY_LEGAL_MANAGER;
import static com.finance.enums.Zone.NORTH;

@RunWith(MockitoJUnitRunner.class)
public class LegalManagerProcessorTest {

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
    LegalManagerProcessor legalManagerProcessor;

    @Test
    public void shouldApproveLoan() throws Exception {
        //Mockito.doNothing().when(userInputCapture).displayMenu(LEGAL_MANAGER_MENU);
        //Mockito.when(userInputCapture.getUserInput()).thenReturn("1");
        //Mockito.when(userInputCapture.validateMenuSelection(MENU_ITEMS.get(LEGAL_MANAGER_MENU), "1")).thenReturn(false);
        //Mockito.doNothing().when(roleProcessor).updateLoanAndDisplayPendingLoans(loan, "1", LEGAL_MANAGER, riskManagerProcessor);
        legalManagerProcessor.performCoreAction("1", loan, "1");
        Assert.assertEquals(loan.getApprovalStatus(), APPROVED_BY_LEGAL_MANAGER);
    }

    @Test
    public void shouldRejectLoan() throws Exception {
        legalManagerProcessor.performCoreAction("1", loan, "2");
        Assert.assertEquals(loan.getApprovalStatus(), REJECTED_BY_LEGAL_MANAGER);
    }

    @Test
    public void shouldSendLoanForClarification() throws Exception {
        legalManagerProcessor.performCoreAction("1", loan, "3");
        Assert.assertEquals(loan.getApprovalStatus(), CLARIFICATION_REQUESTED_BY_LEGAL_MANAGER);
    }

    @Test
    public void shouldGoToMainMenu() throws Exception {
        legalManagerProcessor.performCoreAction("1", loan, "4");
        Mockito.verify(userInputCapture).goToMainMenu();
    }

    @Test
    public void shouldExitFromSystem() throws Exception {
        legalManagerProcessor.performCoreAction("1", loan, "5");
        Mockito.verify(userInputCapture).cleanUpAndExit();
    }

}