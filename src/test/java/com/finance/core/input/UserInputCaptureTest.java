package com.finance.core.input;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static com.finance.data.DataMap.MENU_ITEMS;
import static com.finance.data.DataMap.prepareDataMap;
import static com.finance.enums.MenuType.WELCOME_MENU;
import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class UserInputCaptureTest {

    private static final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    @Before
    public void setUp() throws Exception {
        System.setOut(new PrintStream(outContent));
        prepareDataMap();
    }

    @Mock
    private WelcomeMenuProcessor welcomeMenuProcessor;

    @Mock
    BufferedReader br;

    @InjectMocks
    private UserInputCapture userInputCapture;

    @Test
    public void displayValidMenuOptions() throws Exception {
        userInputCapture.displayMenu(WELCOME_MENU);
        String printedMessage = outContent.toString();
        boolean isWelcomeMenu = printedMessage.contains("1. Login as Underwriter.");
        assertEquals(true, isWelcomeMenu);
    }

    @Test
    public void shouldReturnTrueForValidMenuOptionSelection() throws Exception {
        boolean isValidInput = userInputCapture.validateMenuSelection(MENU_ITEMS.get(WELCOME_MENU), "1");
        assertEquals(true, isValidInput);
    }

    @Test
    public void shouldReturnFalseForInvalidMenuItemNumber() throws Exception {
        boolean isValidInput = userInputCapture.validateMenuSelection(MENU_ITEMS.get(WELCOME_MENU), "10");
        assertEquals(false, isValidInput);
    }

    @Test
    public void shouldReturnFalseForInvalidCharacterEntry() throws Exception {
        boolean isValidInput = userInputCapture.validateMenuSelection(MENU_ITEMS.get(WELCOME_MENU), "ABC");
        assertEquals(false, isValidInput);
    }

}