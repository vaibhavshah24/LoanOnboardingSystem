package com.finance;

import com.finance.core.input.UserInputCapture;
import com.finance.core.router.RecordAssignment;
import com.finance.data.DataLoader;
import com.finance.data.DataMap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class LoanProcessorTest {

    @Mock
    private DataLoader dataloader;

    @Mock
    private RecordAssignment recordAssignment;

    @Mock
    private UserInputCapture userInputCapture;

    @Mock
    private DataMap dataMap;

    @InjectMocks
    LoanProcessor loanProcessor;

    @Test
    public void shouldLoadDataFromCSVFile() throws Exception {
        loanProcessor.run(new String(""));
        Mockito.verify(dataloader).loadData();
        Mockito.verify(recordAssignment).assignRecords();
        Mockito.verify(userInputCapture).goToMainMenu();
    }

}