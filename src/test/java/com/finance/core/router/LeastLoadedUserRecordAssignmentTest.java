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
import org.springframework.data.mongodb.core.MongoTemplate;

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
public class LeastLoadedUserRecordAssignmentTest {

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private LoanUpdateListener loanUpdateListener;

    @Mock
    private MongoTemplate mongoTemplate;

    @InjectMocks
    private LeastLoadedUserRecordAssignment leastLoadedUserRecordAssignment;

    private static List<Loan> loanList = new ArrayList<>();
    private static List<User> userList = new ArrayList<>();

    @BeforeClass
    public static void setUp() throws Exception {
        prepareDataMap();
        Loan loan1 = new Loan(new Customer("Jay", "PAN", "XYZ"), "1", "TV", new BigDecimal("100"), 24, NORTH);
        Loan loan2 = new Loan(new Customer("Jay", "PAN", "XYZ"), "1", "TV", new BigDecimal("100"), 36, NORTH);
        loanList.add(loan1);
        loanList.add(loan2);
        User user1 = new User("1", "A", "A", NORTH, UNDERWRITER, "UserA");
        User user2 = new User("2", "B", "B", NORTH, UNDERWRITER, "UserB");
        userList.add(user1);
        userList.add(user2);
    }

    @Test
    public void shouldLoadNewRecordsFromDatabaseAndAssignToUsers() throws Exception {
        Mockito.when(loanRepository.findByApprovalStatus(NEW)).thenReturn(loanList);
        leastLoadedUserRecordAssignment.assignRecords();
        Mockito.verify(loanRepository, atLeastOnce()).save(Mockito.any(Loan.class));
    }

    @Test
    public void shouldGetLoanByLoanIdAndCallAssignMethod() throws Exception {
        Mockito.when(loanRepository.findOne("1")).thenReturn(loanList.get(0));
        LeastLoadedUserRecordAssignment spyRecord = Mockito.spy(leastLoadedUserRecordAssignment);
        spyRecord.assignLoanToUser("1");
        Mockito.verify(spyRecord).assignLoanToUser(Mockito.any(Loan.class));
    }

    /*@Test
    public void shouldAssignLoanToAvailableUserWithLeastRecords() throws Exception {
        Loan loan = loanList.get(1);
        loan.setApprovalStatus(PENDING);
        Mockito.when(userRepository.findByUserRoleAndZone(UNDERWRITER, NORTH)).thenReturn(userList);

        BasicDBObject dbObject = new BasicDBObject("1", "1.0");
        List<XObject> tempList = new ArrayList<>();
        GroupByResults<XObject> x = new GroupByResults<>(tempList, dbObject);
        List<String> userIDs = new ArrayList<>();
        userIDs.add("1");
        userIDs.add("2");

        Mockito.when(mongoTemplate.group(Criteria.where("assignee").in(userIDs), "loan",
                GroupBy.key("assignee").initialDocument("{ count: 0 }").reduceFunction("function(doc, prev) { prev.count += 1 }"),
                XObject.class)).thenReturn(x);

        leastLoadedUserRecordAssignment.assignLoanToUser(loan);
        Mockito.verify(loanRepository).save(Mockito.any(Loan.class));
        Mockito.verify(userRepository).save(Mockito.any(User.class));
        assertEquals("1", loan.getAssignee());
    }*/

    @Test
    public void shouldNotAssignLoanToAnyoneIfNoUserAvailable() throws Exception {
        Loan loan = loanList.get(1);
        loan.setApprovalStatus(PENDING);
        loan.setAssignee(null);
        List<User> users = new ArrayList<>();
        Mockito.when(userRepository.findByUserRoleAndZone(UNDERWRITER, NORTH)).thenReturn(users);
        leastLoadedUserRecordAssignment.assignLoanToUser(loan);
        Mockito.verify(loanRepository, Mockito.never()).save(Mockito.any(Loan.class));
        Mockito.verify(userRepository, Mockito.never()).save(Mockito.any(User.class));
        assertEquals(null, loan.getAssignee());
    }

    @Test
    public void shouldReassignIdleLoansInQueue() throws Exception {
        loanList.get(0).setAssignee("1");
        loanList.get(1).setAssignee("1");
        User user = userList.get(0);
        Mockito.when(loanRepository.findByUpdatedOnLessThan(Mockito.any())).thenReturn(loanList);
        Mockito.when(userRepository.findOne("1")).thenReturn(userList.get(0));
        Mockito.when(userRepository.findByUserRoleAndZoneAndUserIdNot(user.getUserRole(), user.getZone(), user.getUserId())).thenReturn(userList.subList(1, 2));
        leastLoadedUserRecordAssignment.reassignIdleLoansInQueue();
        Mockito.verify(loanRepository, Mockito.times(2)).save(Mockito.any(Loan.class));
        Mockito.verify(userRepository, Mockito.times(2)).save(Mockito.any(User.class));
        assertEquals("2", loanList.get(0).getAssignee());
        assertEquals("2", loanList.get(1).getAssignee());
    }

}