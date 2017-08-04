package com.finance.data;

import com.finance.model.Loan;
import com.finance.model.User;
import com.finance.respository.LoanRepository;
import com.finance.respository.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.atLeastOnce;

@RunWith(MockitoJUnitRunner.class)
public class CsvDataLoaderTest {

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CsvDataLoader csvDataLoader;

    @Test
    public void verifyDataLoadWhenConfigIsTrue() {
        ReflectionTestUtils.setField(csvDataLoader, "loanFilePath", "data/LoanList.csv");
        ReflectionTestUtils.setField(csvDataLoader, "userFilePath", "data/UserList.csv");
        ReflectionTestUtils.setField(csvDataLoader, "refreshLoanDataFromCsv", true);
        ReflectionTestUtils.setField(csvDataLoader, "refreshUserDataFromCsv", true);
        csvDataLoader.loadData();
        Mockito.verify(loanRepository, atLeastOnce()).save(Mockito.any(Loan.class));
        Mockito.verify(userRepository, atLeastOnce()).save(Mockito.any(User.class));
    }

    @Test
    public void verifyNoDataLoadWhenConfigIsFalse() {
        ReflectionTestUtils.setField(csvDataLoader, "loanFilePath", "data/LoanList.csv");
        ReflectionTestUtils.setField(csvDataLoader, "userFilePath", "data/UserList.csv");
        ReflectionTestUtils.setField(csvDataLoader, "refreshLoanDataFromCsv", false);
        ReflectionTestUtils.setField(csvDataLoader, "refreshUserDataFromCsv", false);
        csvDataLoader.loadData();
        Mockito.verify(loanRepository, Mockito.never()).save(Mockito.any(Loan.class));
        Mockito.verify(userRepository, Mockito.never()).save(Mockito.any(User.class));
    }
}