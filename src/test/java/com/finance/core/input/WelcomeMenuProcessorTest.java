package com.finance.core.input;

import com.finance.core.loanprocessor.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static com.finance.enums.UserRole.*;

@RunWith(MockitoJUnitRunner.class)
public class WelcomeMenuProcessorTest {

    @InjectMocks
    private WelcomeMenuProcessor welcomeMenuProcessor;

    @Mock
    private RoleProcessor roleProcessor;

    @Mock
    private UnderwriterProcessor underwriterProcessor;

    @Mock
    private RiskManagerProcessor riskManagerProcessor;

    @Mock
    private LegalManagerProcessor legalManagerProcessor;

    @Mock
    private FinanceManagerProcessor financeManagerProcessor;

    @Mock
    private UserInputCapture userInputCapture;

    @Test
    public void shouldTriggerUnderwriterLoginMethod() throws Exception {
        welcomeMenuProcessor.processUserInput("1");
        Mockito.verify(roleProcessor).performLogin(UNDERWRITER, underwriterProcessor);
    }

    @Test
    public void shouldTriggerRiskManagerLoginMethod() throws Exception {
        welcomeMenuProcessor.processUserInput("2");
        Mockito.verify(roleProcessor).performLogin(RISK_MANAGER, riskManagerProcessor);
    }

    @Test
    public void shouldTriggerLegalManagerLoginMethod() throws Exception {
        welcomeMenuProcessor.processUserInput("3");
        Mockito.verify(roleProcessor).performLogin(LEGAL_MANAGER, legalManagerProcessor);
    }

    @Test
    public void shouldTriggerFinanceManagerLoginMethod() throws Exception {
        welcomeMenuProcessor.processUserInput("4");
        Mockito.verify(roleProcessor).performLogin(FINANCE_MANAGER, financeManagerProcessor);
    }

    @Test
    public void shouldExitFromSystem() throws Exception {
        welcomeMenuProcessor.processUserInput("5");
        Mockito.verify(userInputCapture).cleanUpAndExit();
    }

}