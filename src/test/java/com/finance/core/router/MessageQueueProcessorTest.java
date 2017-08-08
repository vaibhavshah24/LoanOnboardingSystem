package com.finance.core.router;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class MessageQueueProcessorTest {

    @Mock
    private RecordAssignment recordAssignment;

    @InjectMocks
    MessageQueueProcessor messageQueueProcessor;

    @Test
    public void shouldAssignLoanToUnderwriter() throws Exception {
        messageQueueProcessor.assignLoanToUnderwriter("1");
        Mockito.verify(recordAssignment).assignLoanToUser("1");
    }

    @Test
    public void shouldAssignLoanToRiskManager() throws Exception {
        messageQueueProcessor.assignLoanToRiskManager("1");
        Mockito.verify(recordAssignment).assignLoanToUser("1");
    }

    @Test
    public void shouldAssignLoanToLegalManager() throws Exception {
        messageQueueProcessor.assignLoanToLegalManager("1");
        Mockito.verify(recordAssignment).assignLoanToUser("1");
    }

    @Test
    public void shouldAssignLoanToFinanceManager() throws Exception {
        messageQueueProcessor.assignLoanToFinanceManager("1");
        Mockito.verify(recordAssignment).assignLoanToUser("1");
    }

}