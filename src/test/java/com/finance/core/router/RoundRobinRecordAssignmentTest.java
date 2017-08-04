package com.finance.core.router;

import com.finance.model.Customer;
import com.finance.model.Loan;
import com.finance.model.User;
import com.finance.respository.LoanRepository;
import com.finance.respository.UserRepository;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.finance.data.DataMap.prepareDataMap;
import static com.finance.enums.ApprovalStatus.NEW;
import static com.finance.enums.ApprovalStatus.PENDING;
import static com.finance.enums.UserRole.UNDERWRITER;
import static com.finance.enums.Zone.NORTH;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.atLeastOnce;

@RunWith(MockitoJUnitRunner.class)
public class RoundRobinRecordAssignmentTest {

    @InjectMocks
    private RoundRobinRecordAssignment roundRobinRecordAssignment;

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private LoanUpdateListener loanUpdateListener;

    private static List<Loan> loanList = new ArrayList<>();

    @BeforeClass
    public static void setUp() throws Exception {
        prepareDataMap();
        Loan loan1 = new Loan(new Customer("Jay", "PAN", "XYZ"), "1", "TV", new BigDecimal("100"), 24, NORTH);
        Loan loan2 = new Loan(new Customer("Jay", "PAN", "XYZ"), "1", "TV", new BigDecimal("100"), 36, NORTH);
        loanList.add(loan1);
        loanList.add(loan2);
    }

    @Test
    public void shouldLoadNewRecordsFromDatabaseAndAssignToUsers() throws Exception {
        Mockito.when(loanRepository.findByApprovalStatus(NEW)).thenReturn(loanList);
        roundRobinRecordAssignment.assignRecords();
        Mockito.verify(loanRepository, atLeastOnce()).save(Mockito.any(Loan.class));
    }

    @Test
    public void shouldGetLoanByLoanIdAndCallAssignMethod() throws Exception {
        Mockito.when(loanRepository.findOne("1")).thenReturn(loanList.get(0));
        RoundRobinRecordAssignment spyRecord = Mockito.spy(roundRobinRecordAssignment);
        spyRecord.assignLoanToUser("1");
        Mockito.verify(spyRecord).assignLoanToUser(Mockito.any(Loan.class));
    }

    @Test
    public void shouldAssignLoanToAvailableUsersInRoundRobinFashion() throws Exception {
        User user = new User("1", "A", "A", NORTH, UNDERWRITER, "UserA");
        Loan loan = loanList.get(1);
        loan.setApprovalStatus(PENDING);
        Mockito.when(userRepository.findFirstByUserRoleAndZoneOrderByLastRecordAssignedOnAsc(UNDERWRITER, NORTH)).thenReturn(user);
        roundRobinRecordAssignment.assignLoanToUser(loan);
        Mockito.verify(loanRepository).save(Mockito.any(Loan.class));
        Mockito.verify(userRepository).save(Mockito.any(User.class));
        assertEquals("1", loan.getAssignee());
    }

    @Test
    public void shouldNotAssignLoanToAnyoneIfNoUserAvailable() throws Exception {
        Loan loan = loanList.get(1);
        loan.setAssignee(null);
        loan.setApprovalStatus(PENDING);
        Mockito.when(userRepository.findFirstByUserRoleAndZoneOrderByLastRecordAssignedOnAsc(UNDERWRITER, NORTH)).thenReturn(null);
        roundRobinRecordAssignment.assignLoanToUser(loan);
        Mockito.verify(loanRepository, Mockito.never()).save(Mockito.any(Loan.class));
        Mockito.verify(userRepository, Mockito.never()).save(Mockito.any(User.class));
        assertEquals(null, loan.getAssignee());
    }

    @Test
    public void shouldReassignIdleLoansInQueue() throws Exception {
        loanList.get(0).setAssignee("1");
        loanList.get(1).setAssignee("1");
        User user1 = new User("1", "A", "A", NORTH, UNDERWRITER, "UserA");
        User user2 = new User("2", "B", "B", NORTH, UNDERWRITER, "UserB");
        Mockito.when(loanRepository.findByUpdatedOnLessThan(Mockito.any())).thenReturn(loanList);
        Mockito.when(userRepository.findOne("1")).thenReturn(user1);
        Mockito.when(userRepository.findFirstByUserRoleAndZoneAndUserIdNotOrderByLastRecordAssignedOnAsc(user1.getUserRole(), user1.getZone(), user1.getUserId())).thenReturn(user2);
        roundRobinRecordAssignment.reassignIdleLoansInQueue();
        Mockito.verify(loanRepository, Mockito.times(2)).save(Mockito.any(Loan.class));
        Mockito.verify(userRepository, Mockito.times(2)).save(Mockito.any(User.class));
        assertEquals("2", loanList.get(0).getAssignee());
        assertEquals("2", loanList.get(1).getAssignee());
    }

}