# Loan Onboarding System

## Objective:
This program implements the loan approval workflow. Each loan needs to pass through following levels of approvals respectively: Underwriter, Risk Manager, Legal Manager. All 3 can approve or reject the loan. If approved from one level, the loan goes to next approval level. If rejected, the loan does not go for any other approval. Both Risk Manager and Legal Manager can send the loan back to previous level for further clarification. Once approved from all three levels, the Finance Manager needs to disburse the loan.

## Technologies:
Spring Boot, Spring Data JPA, MongoDB, ActiveMQ (using out-of-box spring-jms jar.)

## Function:
- At the start of program, few 'User' and 'Loan' records are loaded into the MongoDB from the CSV files which are present in the project. (This is sample data, which in real time scenario would come from the staging database or flat files or 3rd party APIs.)
- If you are re-running the program and do not want to reload the sample data in database (since it will delete all previously loaded data), you need to set respective flags in the application.properties file.
- All the sample loans are created in 'New' state in DB. At the start of program, we shall change their states to 'Pending', so that they are assigned to applicable underwriters. [All users and loans have 'Zone' property, so that the loans from respective zones are assigned to the users from respective zone only.]
- Once the loan status is changed to Pending/Approved_By_Underwriter/Approved_By_Risk_Manager etc, the loan request is sent to various ActiveMQ queues depending upon the new state of the loan record. Each queue will have its JmsListener, which will listen to the incoming loan application number, and send it to the record router to assign the record to next approver, if applicable.
- For records' routing to applicable users, there can be multiple strategies possible, such as assign records to the users in round robin fashion, or assign records to the users who have least number of records in their queue. Currently these two are implemented in the program, and any one of them can be enabled using the respective flags in application.properties file.
- There will also be one background thread running at every 30 seconds, which will check for the loan records which are not updated in last 30 seconds by their assignees. If found, these loan records will be reassigned to other user with similar role and zone. [In real time scenario, this thread may run once a day and reassign the records which are not updated in past few days. This way, if one user is on leave, then his/her records will be automatically assigned to other users.]

## Steps to run the program:

**Prerequisites:** 
- You need to have the MongoDB installed on either local machine or any other machine accessible from the place where you are running this project. You can configure the MongoDB's IP address and port in the 'spring.data.mongodb.host' and 'spring.data.mongodb.port' properties respectively in application.properties file.
- Ensure that other properties in your application.properties file are configured as per the requirement.

**Execution Strategy:**
1. To run the program from IntelliJ IDEA, you can use the build configuration 'LoanProcessor'. This will run the program within the IDE, and you may debug the same too.
2. To run the program from command line, create the latest 'jar' file using the maven command (assuming maven is installed on your machine). For eg. navigate to the project folder where pom.xml file is located. Open the command prompt from here, and run the command 'mvn clean package', which will create the latest '.jar' file under the 'target' folder. Then you can execute the command 'java -jar LoanOnboardingSystem-1.0-SNAPSHOT.jar' from the 'target' folder to run the program.
