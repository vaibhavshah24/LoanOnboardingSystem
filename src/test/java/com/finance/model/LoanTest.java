package com.finance.model;

import com.finance.core.router.LoanApprovalProcessor;
import com.finance.core.router.LoanUpdateListener;
import com.finance.enums.ApprovalStatus;
import com.finance.enums.Zone;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.finance.enums.ApprovalStatus.APPROVED_BY_UNDERWRITER;
import static com.finance.enums.ApprovalStatus.PENDING;
import static com.finance.enums.Zone.NORTH;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class LoanTest {

    private static Loan loan;

    @Before
    public void setUp() throws Exception {
        loan = new Loan(new Customer("Jay", "PAN", "XYZ"), "1", "TV", new BigDecimal("100"), 24, NORTH);
    }

    @Mock
    LoanUpdateListener loanUpdateListener;

    @Mock
    LoanApprovalProcessor loanApprovalProcessor;

    @Test
    public void shouldNotifyEachListener() throws Exception {
        Loan spyLoan = Mockito.spy(loan);
        spyLoan.attachListener(loanApprovalProcessor);
        spyLoan.notifyListeners();
        Mockito.verify(loanApprovalProcessor).update(spyLoan);
    }

    @Test
    public void shouldAttachLoanUpdateListener() throws Exception {
        loan.attachListener(loanApprovalProcessor);
        assertEquals(1, loan.getLoanUpdateListenerList().size());
    }

    @Test
    public void shouldRemoveLoanUpdateListeners() throws Exception {
        loan.attachListener(loanApprovalProcessor);
        loan.removeListeners();
        assertEquals(0, loan.getLoanUpdateListenerList().size());
    }

    @Test
    public void shouldFetchValidPropertyValues() throws Exception {
        LocalDateTime dateTime = LocalDateTime.now();
        loan.setUpdatedOn(dateTime);
        loan.setApprovalStatus(PENDING);
        loan.setAssignee("11");
        assertEquals("Jay", loan.getCustomer().getName());
        assertEquals(dateTime, loan.getUpdatedOn());
        assertEquals("TV", loan.getItem());
        assertEquals(PENDING, loan.getApprovalStatus());
        assertEquals("11", loan.getAssignee());
        assertEquals(NORTH, loan.getZone());
        assertEquals(new BigDecimal(100), loan.getAmount());
    }

}