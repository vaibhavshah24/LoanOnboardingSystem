package com.finance.enums;

public enum ApprovalStatus
{
    NEW,
    PENDING,
    APPROVED_BY_UNDERWRITER,
    REJECTED_BY_UNDERWRITER,
    APPROVED_BY_RISK_MANAGER,
    REJECTED_BY_RISK_MANAGER,
    APPROVED_BY_LEGAL_MANAGER,
    REJECTED_BY_LEGAL_MANAGER,
    CLARIFICATION_REQUESTED_BY_RISK_MANAGER,
    CLARIFICATION_REQUESTED_BY_LEGAL_MANAGER,
    DISBURSED
}
