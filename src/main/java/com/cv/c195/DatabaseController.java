package com.cv.c195;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.sql.*;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Controls all functions of the database in the application.
 * This includes opening and closing results sets, statements, and the database connection, as well as queries.
 * It also contains the observable lists for the main tables.
 * @author Chris Vachon
 */
public class DatabaseController {
    private static Statement statement;
    private static Connection conn;
    private static String database = "";
    private static String databaseUsername = "";
    private static String databasePassword = "";
    private static String databaseUser = "";
    private static ObservableList<Customer> allCustomers = FXCollections.observableArrayList();
    private static ObservableList<Appointment> allAppointments = FXCollections.observableArrayList();
    private static ObservableList<Appointment> weeklyAppointments = FXCollections.observableArrayList();
    private static ObservableList<Appointment> monthlyAppointments = FXCollections.observableArrayList();
    private static ResultSet queryResults;

    /**
     * Checks databaseConnection.txt for database connection information.
     * Uses given information to start the database connection.
     * @return True if the database was successfully connected to. Else, false.
     */
    public static boolean startDB() {
        try {
            File dbFile = new File("src/main/java/com/cv/c195/databaseConnection.txt");
            Scanner myReader = new Scanner(dbFile);

            if(myReader.hasNextLine()){
                database = myReader.nextLine();
            }
            if(myReader.hasNextLine()){
                databaseUsername = myReader.nextLine();
            }
            if(myReader.hasNextLine()){
                databasePassword = myReader.nextLine();
            }
            myReader.close();
        }
        catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        try {
            conn = DriverManager.getConnection("jdbc:mysql:" + database, databaseUsername, databasePassword);

        } catch (SQLException e) {
            System.out.println("Could not connect to database: " + e.getMessage());
            System.exit(0);
        }
        return true;
    }

    /**
     * Checks the passed in username and password arguments against the user information in the database, and logs the
     * user in if it matches. The log-in attempt is recorder whether the log-in is successful or not.
     *
     * @param un - Username for the application
     * @param pw - Password for the application
     * @return True if log-in was successful. Else, false.
     */
    public static boolean logIn(String un, String pw) {

        try{
            FileWriter loginActivity = new FileWriter("login_activity.txt", true);
            loginActivity.write(un + " " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd - HH:mm:ss")));
            loginActivity.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
        try{
            statement = conn.createStatement();
            ResultSet results = statement.executeQuery("select * from users where User_name = '" + un + "' and password = '" + pw + "'");
            if(results.next()){
                if(results.getString(2).equals(un) && results.getString(3).equals(pw)) {
                    databaseUser = un;
                    statement.close();
                    return true;
                }
            }
        }
        catch (SQLException e){
            System.out.println("Unable to log in: " + e.getMessage());
        }
        return false;
    }

    /**
     * Executes the passed in query and stores the results in queryResults
     * @param query - The query being executed
     * @return True if the query was successful. Else, false.
     */
    public static boolean query(String query){
        try{
            statement = conn.createStatement();
            statement.execute(query);
            queryResults = statement.getResultSet();
            return true;
        }
        catch (SQLException e){
            System.out.println("There was a problem with your query " + e.getMessage());
        }
        return false;
    }

    /**
     *
     * @return The list of all customers
     */
    public static ObservableList<Customer> getAllCustomers() {return allCustomers;}

    /**
     *
     * @return The list of all appointments
     */
    public static ObservableList<Appointment> getAllAppointments() {return allAppointments;}

    /**
     *
     * @return The list of all appointments from now to 7 days from now
     */
    public static ObservableList<Appointment> getWeeklyAppointments() {return weeklyAppointments;}

    /**
     *
     * @return The list of all appointments from now to 30 days from now
     */
    public static ObservableList<Appointment> getMonthlyAppointments() {return monthlyAppointments;}

    /**
     *
     * @return The results from the most recent query
     */
    public static ResultSet getQueryResults(){return queryResults;}

    /**
     * Add a customer to the list of customers
     * @param x - The customer to be added to the list
     */
    public static void addToCustomerList(Customer x){
        allCustomers.add(x);
    }

    /**
     * Replaces an old customer in the list with an updated version
     * @param old - The customer to be replaced
     * @param updated - The updated version of the customer
     */
    public static void updateCustomerList(Customer old, Customer updated){
        int x = allCustomers.indexOf(old);
        allCustomers.set(x, updated);
    }

    /**
     * Removes a customer from the list
     * @param customer - The customer to be removed from the list.
     */
    public static void deleteCustomerList(Customer customer){
        allCustomers.remove(customer);
    }

    /**
     * Removes an appointment from the appointment list, weekly appointment list, and monthly appointment list
     * @param appointment - The appointment to be removed from the lists
     */
    public static void deleteAppointmentList(Appointment appointment){
        allAppointments.remove(appointment);
        if(weeklyAppointments.contains(appointment)) {
            weeklyAppointments.remove(appointment);
        }
        if(monthlyAppointments.contains(appointment)){
            monthlyAppointments.remove(appointment);
        }
    }

    /**
     * Adds an appointment to the appointment list and applicable weekly appointment list, and monthly appointment list
     * @param x - The appointment to be added from the lists
     */
    public static void addToAppointmentList(Appointment x){

        allAppointments.add(x);

        if(!ZonedDateTime.now().isAfter(x.getStart())) {

            if (ZonedDateTime.now().plusWeeks(1).isAfter(x.getStart())) {
                weeklyAppointments.add(x);
            }
            if (ZonedDateTime.now().plusMonths(1).isAfter(x.getStart())) {
                monthlyAppointments.add(x);
            }
        }
    }

    /**
     * Replaces an old appointment with an updated version in all applicable appointment lists.
     * @param old - The appointment to be removed
     * @param updated - The updated appointment to replace it
     */
    public static void updateAppointmentList(Appointment old, Appointment updated){
        int x = allAppointments.indexOf(old);
        if(x != -1) {
            allAppointments.set(x, updated);
        }
        if(updated.getStartEST().isBefore(ZonedDateTime.now().plusDays(7)) && updated.getEndEST().isAfter(ZonedDateTime.now())){
            x = weeklyAppointments.indexOf(old);
            if(x != -1) {
                weeklyAppointments.set(x, updated);
            }
            else{
                weeklyAppointments.add(updated);
            }
        }
        else{
            x = weeklyAppointments.indexOf(old);
            weeklyAppointments.remove(old);
        }
        if(updated.getStartEST().isBefore(ZonedDateTime.now().plusDays(30)) && updated.getEndEST().isAfter(ZonedDateTime.now())){
            x = monthlyAppointments.indexOf(old);
            if(x != -1) {
                monthlyAppointments.set(x, updated);
            }
            else{
                monthlyAppointments.add(updated);
            }
        }
        else{
            x = monthlyAppointments.indexOf(old);
            monthlyAppointments.remove(old);
        }
    }

    /**
     * Closes the Resultset from the most recent query
     */
    public static void closeResultSet(){
        try {
            queryResults.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Closes the statement from the most recent query
     */
    public static void closeStatement(){
        try {
            statement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Closes the database connection
     */
    public static void closeDatabase(){
        try {
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     *
     * @return The user that logged into the application
     */
    public static String getUser(){
        return databaseUser;
    }

}
