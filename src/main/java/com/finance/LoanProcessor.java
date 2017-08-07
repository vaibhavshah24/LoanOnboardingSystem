package com.finance;

import com.finance.core.router.RecordAssignment;
import com.finance.core.input.UserInputCapture;
import com.finance.data.DataLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.finance.data.DataMap.*;

@SpringBootApplication
public class LoanProcessor implements CommandLineRunner {

    private DataLoader dataLoader;
    private RecordAssignment recordAssignment;
    private UserInputCapture userInputCapture;

    @Autowired
    public LoanProcessor(DataLoader dataLoader, RecordAssignment recordAssignment, UserInputCapture userInputCapture) {
        this.dataLoader = dataLoader;
        this.recordAssignment = recordAssignment;
        this.userInputCapture = userInputCapture;
    }

    public static void main(String[] args) {
        SpringApplication.run(LoanProcessor.class, args);
    }

    public void run(String... strings) throws Exception {
        // one time activity to prepare master data maps which will be used in application.
        prepareDataMap();

        // one time activity to load loan and user data from CSV file to the mongodb.
        dataLoader.loadData();

        // one time activity to fetch current loan records from database and assign them to respective users.
        recordAssignment.assignRecords();

        // create a background scheduled thread to monitor and reassign the idle loan records.
        monitorIdleRecords();

        System.out.println("Welcome to Loan Onboarding System. Please select your action:");
        userInputCapture.goToMainMenu();
    }

    private void monitorIdleRecords()
    {
        Runnable periodicTask = () -> {
            System.out.println("Reassignment thread called. Time : " + LocalDateTime.now());
            recordAssignment.reassignIdleLoansInQueue();
        };

        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(periodicTask, 30, 30, TimeUnit.SECONDS);
    }
}
