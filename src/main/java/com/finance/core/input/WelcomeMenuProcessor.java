package com.finance.core.input;

import com.finance.core.loanprocessor.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.finance.enums.UserRole.*;

@Component
public class WelcomeMenuProcessor implements UserInputProcessor {

    @Autowired
    private UnderwriterProcessor underwriterProcessor;

    @Autowired
    private RiskManagerProcessor riskManagerProcessor;

    @Autowired
    private LegalManagerProcessor legalManagerProcessor;

    @Autowired
    private FinanceManagerProcessor financeManagerProcessor;

    @Autowired
    private UserInputCapture userInputCapture;

    @Autowired
    private RoleProcessor roleProcessor;

    @Override
    public void processUserInput(String selectedOption) {
        switch (Integer.parseInt(selectedOption)) {
            case 1: // login as underwriter
                roleProcessor.performLogin(UNDERWRITER, underwriterProcessor);
                break;
            case 2: // login as risk manager
                roleProcessor.performLogin(RISK_MANAGER, riskManagerProcessor);
                break;
            case 3: // login as legal manager
                roleProcessor.performLogin(LEGAL_MANAGER, legalManagerProcessor);
                break;
            case 4: // login as finance manager
                roleProcessor.performLogin(FINANCE_MANAGER, financeManagerProcessor);
                break;
            case 5: // exit from system
                userInputCapture.cleanUpAndExit();
                break;
        }
    }
}
