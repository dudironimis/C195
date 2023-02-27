C195 Performance Assesment

The purpose of this program is a generic scheduling app. It has the ability to
add, update, and delete customers as well as appointments.


Created by: Chris Vachon (cvachon@wgu.edu) v1.0 10/23/2022

Application created in Java (JDK-17.0.4) and JavaFX (JavaFX-SDK-19) with 
IntelliJ IDEA Community Edition 2022.2 IDE.

MySQL Connector Driver (mysql-connector-java-8.0.30)

In order to run the program, you must first connect the program to your MySQL 
database. Open the file "databaseConnection.txt". On the first line add the 
url to your MySQL database. (Note: it will only work with MySQL Databases)
On the second line add the username for you database. On the third line add
the password to your database, if one is used.
If the url, username, and password are correct you will see a login form appear.
This form is for the user to log in to the program, not the database. Once the
user is logged in they will see two tables. On the left will be all the customers.
On the right will be all the appointments. To add, update, or delete customers or
appointments, click on the buttons for the wanted option.
From there, self explanitory form or informative warnings will appear.
IMPORTANT NOTE: When adding or updating an appointment, use YOUR LOCAL TIME for the
appointment start and end times.
You will also find a button on the top right labeled "Reports." You can run three 
different reports from that window. These reports include:
	* Total number of customers appointments by type and month
	* A schedule for each contact
	* Total number of appointments each customer has had 

