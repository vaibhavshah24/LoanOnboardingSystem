package com.finance.core.loanprocessor;

import com.finance.core.input.UserInputCapture;
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

import static com.finance.data.DataMap.MENU_ITEMS;
import static com.finance.data.DataMap.prepareDataMap;
import static com.finance.enums.MenuType.UNDERWRITER_MENU;
import static com.finance.enums.UserRole.UNDERWRITER;
import static com.finance.enums.Zone.NORTH;

@RunWith(MockitoJUnitRunner.class)
public class RoleProcessorTest {

    @InjectMocks
    private RoleProcessor roleProcessor;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserInputCapture userInputCapture;

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private UnderwriterProcessor underwriterProcessor;

    private static List<Loan> loanList = new ArrayList<>();

    @BeforeClass
    public static void setUp() throws Exception {
        prepareDataMap();
        Loan loan1 = new Loan(new Customer("Jay", "PAN", "XYZ"), "1", "TV", new BigDecimal("100"), 24, NORTH);
        Loan loan2 = new Loan(new Customer("Jay", "PAN", "XYZ"), "1", "TV", new BigDecimal("100"), 36,NORTH);
        loanList.add(loan1);
        loanList.add(loan2);
    }

    @Test
    public void shouldLoginSuccessfullyAndDisplayLoans() throws Exception {
        Mockito.when(userInputCapture.getUserInput()).thenReturn("1");
        Mockito.when(userInputCapture.validateMenuSelection(MENU_ITEMS.get(UNDERWRITER_MENU),"1")).thenReturn(true);
        Mockito.when(userInputCapture.getLoginCredentials()).thenReturn(new String[] {"A", "A"});
        Mockito.when(userRepository.findByUsernameAndPasswordAndUserRole("A", "A", UNDERWRITER)).thenReturn(new User("1", "A", "A", NORTH, UNDERWRITER, "UserA"));
        Mockito.when(loanRepository.findByAssignee("1")).thenReturn(loanList);
        roleProcessor.performLogin(UNDERWRITER, underwriterProcessor);
        Mockito.verify(userInputCapture, Mockito.never()).goToMainMenu();
    }

    @Test
    public void shouldFailLoginAndGoToMainMenu() throws Exception {
        Mockito.when(userInputCapture.getLoginCredentials()).thenReturn(new String[] {"A", "B"});
        Mockito.when(userRepository.findByUsernameAndPasswordAndUserRole("A", "B", UNDERWRITER)).thenReturn(null);
        roleProcessor.performLogin(UNDERWRITER, underwriterProcessor);
        Mockito.verify(userInputCapture, Mockito.times(1)).goToMainMenu();
    }

    @Test
    public void shouldSaveLoanRecord() throws Exception {
        Mockito.when(userInputCapture.getUserInput()).thenReturn("1");
        Mockito.when(userInputCapture.validateMenuSelection(MENU_ITEMS.get(UNDERWRITER_MENU),"1")).thenReturn(true);
        Loan loan = new Loan(new Customer("Jay", "PAN", "XYZ"), "1", "TV", new BigDecimal("100"), 24, NORTH);
        roleProcessor.updateLoanAndDisplayPendingLoans(loan,"A", UNDERWRITER, underwriterProcessor);
        Mockito.verify(loanRepository).save(Mockito.any(Loan.class));
    }

}