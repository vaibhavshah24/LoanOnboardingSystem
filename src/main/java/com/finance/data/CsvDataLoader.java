package com.finance.data;

import com.finance.enums.UserRole;
import com.finance.enums.Zone;
import com.finance.model.Customer;
import com.finance.model.Loan;
import com.finance.model.User;
import com.finance.respository.LoanRepository;
import com.finance.respository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;

@Component()
public class CsvDataLoader implements DataLoader {

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private UserRepository userRepository;

    @Value("${loan.data.refreshLoanDataFromCsv}")
    private boolean refreshLoanDataFromCsv;

    @Value("${loan.data.refreshUserDataFromCsv}")
    private boolean refreshUserDataFromCsv;

    @Value("${load.data.loanFilePath}")
    private String loanFilePath;

    @Value("${load.data.userFilePath}")
    private String userFilePath;

    @Override
    public void loadData() {
        if (refreshUserDataFromCsv) {
            prepareUserData();
        }

        if (refreshLoanDataFromCsv) {
            prepareLoanData();
        }
    }

    // Read the user data from CSV file and load it in database.
    private void prepareUserData() {

        userRepository.deleteAll(); // delete existing data from database before loading new data.

        BufferedReader bufferedReader = null;
        String line = "";
        String cvsSplitBy = ",";

        try {
            Resource resource = new ClassPathResource(userFilePath);
            bufferedReader = new BufferedReader(new InputStreamReader(resource.getInputStream()));
            if (bufferedReader.readLine() == null) { // this will read the header of the CSV file
                System.out.println("Users file is empty. Please provide a valid file.");
                System.exit(1); // exit from application since there is no user data to process.
            }

            User userRecord = null;
            while ((line = bufferedReader.readLine()) != null) {
                String[] userInfo = line.split(cvsSplitBy);
                userRecord = new User(userInfo[0], userInfo[1], userInfo[2], Zone.valueOf(userInfo[3].toUpperCase()), UserRole.valueOf(userInfo[4].toUpperCase()), userInfo[5]);
                userRepository.save(userRecord);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    // Read the loan data from CSV file and load it in database.
    private void prepareLoanData() {

        loanRepository.deleteAll(); // delete existing data from database before loading new data.

        BufferedReader bufferedReader = null;
        String line = "";
        String cvsSplitBy = ",";

        try {
            Resource resource = new ClassPathResource(loanFilePath);
            bufferedReader = new BufferedReader(new InputStreamReader(resource.getInputStream()));
            if (bufferedReader.readLine() == null) { // this will read the header of the CSV file
                System.out.println("Loans file is empty. Please provide a valid file.");
                System.exit(1); // exit from application since there is no loan data to process.
            }

            Loan loanRecord = null;
            Customer customer = null;
            while ((line = bufferedReader.readLine()) != null) {
                String[] loanInfo = line.split(cvsSplitBy);
                customer = new Customer(loanInfo[0], loanInfo[1], loanInfo[2]);
                loanRecord = new Loan(customer, loanInfo[3], loanInfo[4], new BigDecimal(loanInfo[5]), Integer.parseInt(loanInfo[6]), Zone.valueOf(loanInfo[7].toUpperCase()));
                loanRepository.save(loanRecord);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}
