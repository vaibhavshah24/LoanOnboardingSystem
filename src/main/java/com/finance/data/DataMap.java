package com.finance.data;

import com.finance.enums.ApprovalStatus;
import com.finance.enums.MenuType;
import com.finance.enums.UserRole;
import org.springframework.stereotype.Component;

import java.util.HashMap;

import static com.finance.enums.ApprovalStatus.*;
import static com.finance.enums.MenuType.*;
import static com.finance.enums.UserRole.*;

@Component
public class DataMap {

    public static final String UNDERWRITER_QUEUE_NAME = "UNDERWRITER_QUEUE";
    public static final String RISK_MANAGER_QUEUE_NAME = "RISK_MANAGER_QUEUE";
    public static final String LEGAL_MANAGER_QUEUE_NAME = "LEGAL_MANAGER_QUEUE";
    public static final String FINANCE_MANAGER_QUEUE_NAME = "FINANCE_MANAGER_QUEUE";

    public static HashMap<Enum, String[]> MENU_ITEMS = new HashMap<>();
    public static HashMap<ApprovalStatus, UserRole> APPROVAL_HIERARCHY = new HashMap<>();
    public static HashMap<ApprovalStatus, String> APPROVAL_QUEUE = new HashMap<>();
    public static HashMap<UserRole, MenuType> USER_ROLE_MENU_MAPPING = new HashMap<>();

    public static void prepareDataMap() {
        prepareMenuItems();
        prepareLoanApprovalHierarchy();
        prepareLoanApprovalStatusQueueMapping();
        prepareUserRoleMenuMapping();
    }

    private static void prepareMenuItems() {
        MENU_ITEMS.put(WELCOME_MENU, new String[]{
                "1. Login as Underwriter.",
                "2. Login as Risk Manager.",
                "3. Login as Legal Manager.",
                "4. Login as Finance Manager.",
                "5. Exit from application."});

        MENU_ITEMS.put(UNDERWRITER_MENU, new String[]{
                "1. Approve current loan request.",
                "2. Reject current loan request.",
                "3. Go back to main menu.",
                "4. Exit from application."});

        MENU_ITEMS.put(RISK_MANAGER_MENU, new String[]{
                "1. Approve current loan request.",
                "2. Reject current loan request.",
                "3. Send back for clarification.",
                "4. Go back to main menu.",
                "5. Exit from application."});

        MENU_ITEMS.put(LEGAL_MANAGER_MENU, new String[]{
                "1. Approve current loan request.",
                "2. Reject current loan request.",
                "3. Send back for clarification.",
                "4. Go back to main menu.",
                "5. Exit from application."});

        MENU_ITEMS.put(FINANCE_MANAGER_MENU, new String[]{
                "1. Disburse loan.",
                "2. Go back to main menu.",
                "3. Exit from application."});
    }

    private static void prepareLoanApprovalHierarchy() {
        APPROVAL_HIERARCHY.put(PENDING, UNDERWRITER);
        APPROVAL_HIERARCHY.put(APPROVED_BY_UNDERWRITER, RISK_MANAGER);
        APPROVAL_HIERARCHY.put(APPROVED_BY_RISK_MANAGER, LEGAL_MANAGER);
        APPROVAL_HIERARCHY.put(APPROVED_BY_LEGAL_MANAGER, FINANCE_MANAGER);
        APPROVAL_HIERARCHY.put(CLARIFICATION_REQUESTED_BY_RISK_MANAGER, UNDERWRITER);
        APPROVAL_HIERARCHY.put(CLARIFICATION_REQUESTED_BY_LEGAL_MANAGER, RISK_MANAGER);
    }

    private static void prepareLoanApprovalStatusQueueMapping() {
        APPROVAL_QUEUE.put(PENDING, UNDERWRITER_QUEUE_NAME);
        APPROVAL_QUEUE.put(APPROVED_BY_UNDERWRITER, RISK_MANAGER_QUEUE_NAME);
        APPROVAL_QUEUE.put(APPROVED_BY_RISK_MANAGER, LEGAL_MANAGER_QUEUE_NAME);
        APPROVAL_QUEUE.put(APPROVED_BY_LEGAL_MANAGER, FINANCE_MANAGER_QUEUE_NAME);
        APPROVAL_QUEUE.put(CLARIFICATION_REQUESTED_BY_RISK_MANAGER, UNDERWRITER_QUEUE_NAME);
        APPROVAL_QUEUE.put(CLARIFICATION_REQUESTED_BY_LEGAL_MANAGER, RISK_MANAGER_QUEUE_NAME);
    }

    private static void prepareUserRoleMenuMapping() {
        USER_ROLE_MENU_MAPPING.put(UNDERWRITER, UNDERWRITER_MENU);
        USER_ROLE_MENU_MAPPING.put(RISK_MANAGER, RISK_MANAGER_MENU);
        USER_ROLE_MENU_MAPPING.put(LEGAL_MANAGER, LEGAL_MANAGER_MENU);
        USER_ROLE_MENU_MAPPING.put(FINANCE_MANAGER, FINANCE_MANAGER_MENU);
    }
}
