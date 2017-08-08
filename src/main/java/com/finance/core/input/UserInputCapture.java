package com.finance.core.input;

import com.finance.enums.MenuType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static com.finance.data.DataMap.MENU_ITEMS;
import static com.finance.enums.MenuType.WELCOME_MENU;

@Component
public class UserInputCapture {

    @Autowired
    private WelcomeMenuProcessor welcomeMenuProcessor;

    private static BufferedReader bufferedReader = null;

    public void displayMenu(MenuType menuType) {
        for (String menuOption : MENU_ITEMS.get(menuType))
            System.out.println(menuOption);
    }

    public void goToMainMenu()
    {
        displayMenu(WELCOME_MENU);
        String selectedOption = null;
        do {
            selectedOption = getUserInput();
        } while (!validateMenuSelection(MENU_ITEMS.get(WELCOME_MENU), selectedOption));
        welcomeMenuProcessor.processUserInput(selectedOption);
    }

    public String getUserInput() {
        String input = null;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(System.in));
            input = bufferedReader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return input;
    }

    public void cleanUpAndExit() {
        if (bufferedReader != null) {
            try {
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.exit(0);
    }

    public boolean validateMenuSelection(String[] menuOptions, String input) {
        boolean isValidInput = false;
        try {
            int selectedOption = Integer.parseInt(input);
            if (selectedOption > 0 && selectedOption <= menuOptions.length) {
                isValidInput = true;
            } else {
                System.out.println("Please enter a valid number between 1 and " + menuOptions.length + ".");
            }
        } catch (Exception ex) {
            System.out.println("Please enter a valid number between 1 and " + menuOptions.length + ".");
        }
        return isValidInput;
    }

    public String[] getLoginCredentials() {
        String[] credentials = new String[2];
        System.out.println("Please enter username: ");
        String username = getUserInput();
        System.out.println("Please enter password: ");
        String password = getUserInput();
        credentials[0] = username;
        credentials[1] = password;
        return credentials;
    }
}
