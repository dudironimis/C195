package com.cv.c195;
import java.time.*;
import java.time.format.DateTimeFormatter;


public class Appointment {
    private int id;
    private String customerName;
    private int customerId;
    private String contactName;
    private String title;
    private String description;
    private String location;
    private String type;
    private ZonedDateTime start;
    private ZonedDateTime end;
    private ZonedDateTime startUTC;
    private ZonedDateTime endUTC;
    private ZonedDateTime startEST;
    private ZonedDateTime endEST;
    private String startStr;
    private String endStr;
    private int userId;


    /**
     *  Constructor for the Appointment class.
     *
     * @param id - ID of the appointment
     * @param customerName - Name of the customer that holds this appointment
     * @param customerId - ID of the customer that holds this appointment
     * @param contactName - Name of the contact this appointment is with
     * @param title - Title of this appointment
     * @param description - A description of this appointment
     * @param location - The Location of this appointment
     * @param type - The type of appointment being held
     * @param start - The starting time and date of this appointment
     * @param end - The ending time and date of this appointment
     */
    public Appointment(int id, String customerName, int customerId, String contactName, String title, String description,
                       String location, String type, LocalDateTime start, LocalDateTime end, int userId){
        setId(id);
        setCustomerName(customerName);
        setCustomerId(customerId);
        setContactName(contactName);
        setTitle(title);
        setDescription(description);
        setLocation(location);
        setType(type);
        setStart(start);
        setEnd(end);
        this.userId = userId;
    }

    /**
     *
     * @return the id of this appointment
     */
    public int getId() {
        return id;
    }

    public int getUserId(){return userId;}

    /**
     * sets the ID of this appointment
     * @param id - ID of this appointment
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     *
     * @return The name of the customer that holds this appointment
     */
    public String getCustomerName() {
        return customerName;
    }

    /**
     * Sets the name of customer for this appointment
     * @param customerName - The name of the customer that holds this appointment
     */
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    /**
     *
     * @return The ID of the customer that holds this appointment
     */
    public int getCustomerId() {
        return customerId;
    }

    /**
     * Sets the ID of customer for this appointment
     * @param customerId - The ID of the customer that holds this appointment
     */
    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    /**
     *
     * @return The name of the contact this appointment is with
     */
    public String getContactName() {
        return contactName;
    }

    /**
     * Sets the name of contact for this appointment
     * @param contactName - The name of the contact in this appointment
     */
    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    /**
     *
     * @return The title of this appointment
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title for this appointment
     * @param title - The title of this appointment
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     *
     * @return the description for this appointment
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description for this appointment
     * @param description - The description for this appointment
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     *
     * @return the location of this appointment
     */
    public String getLocation() {
        return location;
    }

    /**
     * Sets the location for this appointment
     * @param location - The location of this appointment
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     *
     * @return The type of appointment this is
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the type for this appointment
     * @param type - The type of appointment this is
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     *
     * @return The start time and date for this appointment based on the user's timezone
     */
    public ZonedDateTime getStart() {
        return start;
    }

    /**
     * Sets the start time for this appointment based on the user's local system, EST and UTC.
     * @param startLocal - The starting time of the appointment based on the user's time.
     */
    public void setStart(LocalDateTime startLocal) {

        start = ZonedDateTime.of(startLocal, ZoneId.systemDefault());
        startEST = ZonedDateTime.of(startLocal, ZoneId.of("America/New_York"));
        startUTC = ZonedDateTime.of(startLocal, ZoneId.of("UTC"));

        int offsetEST = start.getOffset().compareTo(startEST.getOffset());
        int offsetUTC = start.getOffset().compareTo(startUTC.getOffset());

        startEST = start.plusSeconds(offsetEST);
        startUTC = start.plusSeconds(offsetUTC);
    }

    /**
     *
     * @return The end time and date for this appointment based on the user's time zone
     */
    public ZonedDateTime getEnd() {return end;}

    /**
     *
     * @return The start time and date for this appointment in a formatted string of "yyyy/MM/dd - HH:mm:ss"
     */
    public String getStartStr(){

        startStr = start.format(DateTimeFormatter.ofPattern("yyyy/MM/dd - HH:mm:ss"));
        return startStr;
    }
    /**
     *
     * @return The end time and date for this appointment in a formatted string of "yyyy/MM/dd - HH:mm:ss"
     */
    public String getEndStr(){

        endStr = end.format(DateTimeFormatter.ofPattern("yyyy/MM/dd - HH:mm:ss"));
        return endStr;
    }

    /**
     *
     * @return The start time and date for this appointment, in UTC time zone, in a formatted string of "yyyy/MM/dd - HH:mm:ss"
     */
    public String getStartStrUTC(){

        startStr = startUTC.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"));
        return startStr;
    }

    /**
     *
     * @return The end time and date for this appointment, for UTC time zone, in a formatted string of "yyyy/MM/dd - HH:mm:ss"
     */
    public String getEndStrUTC(){
        endStr = endUTC.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"));
        return endStr;
    }

    /**
     * Sets the end time for this appointment based on the user's local system, EST and UTC.
     * @param endLocal - The starting time of the appointment based on the user's time.
     */
    public void setEnd(LocalDateTime endLocal) {

        end = ZonedDateTime.of(endLocal, ZoneId.systemDefault());
        endEST = ZonedDateTime.of(endLocal, ZoneId.of("America/New_York"));
        endUTC = ZonedDateTime.of(endLocal, ZoneId.of("UTC"));

        int offsetEST = end.getOffset().compareTo(endEST.getOffset());
        int offsetUTC = end.getOffset().compareTo(endUTC.getOffset());

        endEST = end.plusSeconds(offsetEST);
        endUTC = end.plusSeconds(offsetUTC);
    }

    /**
     *
     * @return The start time and date for this appointment based on the EST time zone
     */
    public ZonedDateTime getStartEST(){return startEST;}
    /**
     *
     * @return The end time and date for this appointment based on the EST time zone
     */
    public ZonedDateTime getEndEST(){return endEST;}

}
