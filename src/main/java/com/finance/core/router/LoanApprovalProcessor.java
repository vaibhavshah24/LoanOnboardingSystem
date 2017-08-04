package com.finance.core.router;

import com.finance.model.Loan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import static com.finance.data.DataMap.APPROVAL_QUEUE;

@Component
public class LoanApprovalProcessor implements LoanUpdateListener {

    @Autowired
    private JmsTemplate jmsTemplate;

    @Override
    public void update(Loan loan) {
        String queueName = APPROVAL_QUEUE.get(loan.getApprovalStatus());
        if (queueName != null) {
            jmsTemplate.convertAndSend(queueName, loan.getApplicationNo());
        }
    }
}
