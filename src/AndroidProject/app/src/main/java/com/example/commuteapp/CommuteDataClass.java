package com.example.commuteapp;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "commute_data_table")
public class CommuteDataClass
{
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    public String getFromAddr() {
        return fromAddr;
    }

    public void setFromAddr(@NonNull String fromAddr) {
        this.fromAddr = fromAddr;
    }

    @NonNull
    private String fromAddr;

    public String getFromAlias() {
        return fromAlias;
    }

    public void setFromAlias(String fromAlias) {
        this.fromAlias = fromAlias;
    }

    private String fromAlias;

    @NonNull
    public String getToAddr() {
        return toAddr;
    }

    public void setToAddr(@NonNull String toAddr) {
        this.toAddr = toAddr;
    }

    @NonNull
    private String toAddr;

    public String getToAlias() {
        return toAlias;
    }

    public void setToAlias(String toAlias) {
        this.toAlias = toAlias;
    }

    private String toAlias;

    @NonNull
    public String getTransportMode() {
        return transportMode;
    }

    public void setTransportMode(@NonNull String transportMode) {
        this.transportMode = transportMode;
    }

    @NonNull
    private String transportMode;

    @NonNull
    public String getRouteTime() {
        return routeTime;
    }

    public void setRouteTime(@NonNull String routeTime) {
        this.routeTime = routeTime;
    }

    @NonNull
    private String routeTime;

    @NonNull
    public String getRouteArriveDepart() {
        return routeArriveDepart;
    }

    public void setRouteArriveDepart(@NonNull String routeArriveDepart) {
        this.routeArriveDepart = routeArriveDepart;
    }

    @NonNull
    private String routeArriveDepart;

    public Boolean getSunday() {
        return sunday;
    }

    public void setSunday(Boolean sunday) {
        this.sunday = sunday;
    }

    private Boolean sunday;

    public Boolean getMonday() {
        return monday;
    }

    public void setMonday(Boolean monday) {
        this.monday = monday;
    }

    private Boolean monday;

    public Boolean getTuesday() {
        return tuesday;
    }

    public void setTuesday(Boolean tuesday) {
        this.tuesday = tuesday;
    }

    private Boolean tuesday;

    public Boolean getWednesday() {
        return wednesday;
    }

    public void setWednesday(Boolean wednesday) {
        this.wednesday = wednesday;
    }

    private Boolean wednesday;

    public Boolean getThursday() {
        return thursday;
    }

    public void setThursday(Boolean thursday) {
        this.thursday = thursday;
    }

    private Boolean thursday;

    public Boolean getFriday() {
        return friday;
    }

    public void setFriday(Boolean friday) {
        this.friday = friday;
    }

    private Boolean friday;

    public Boolean getSaturday() {
        return saturday;
    }

    public void setSaturday(Boolean saturday) {
        this.saturday = saturday;
    }

    private Boolean saturday;

    public Boolean getReminder30() {
        return reminder30;
    }

    public void setReminder30(Boolean reminder30) {
        this.reminder30 = reminder30;
    }

    private Boolean reminder30;

    public Boolean getReminder5() {
        return reminder5;
    }

    public void setReminder5(Boolean reminder5) {
        this.reminder5 = reminder5;
    }

    private Boolean reminder5;

    public Boolean getReminderAuto() {
        return reminderAuto;
    }

    public void setReminderAuto(Boolean reminderAuto) {
        this.reminderAuto = reminderAuto;
    }

    private Boolean reminderAuto;

    public Boolean getReminderBT() {
        return reminderBT;
    }

    private Boolean reminderBT;

    public String getRouteDirectionsString() {
        return routeDirectionsString;
    }

    public void setRouteDirectionsString(String routeDirectionsString) {
        this.routeDirectionsString = routeDirectionsString;
    }

    public void setReminderBT(Boolean reminderBT) {
        this.reminderBT = reminderBT;
    }

    private String routeDirectionsString;

    public int getSnoozeDelay() {
        return snoozeDelay;
    }

    public void setSnoozeDelay(int snoozeDelay) {
        this.snoozeDelay = snoozeDelay;
    }

    private int snoozeDelay;



    public CommuteDataClass(    @NonNull String FROM,
                                String FROMALIAS,
                                @NonNull String TO,
                                String TOALIAS,
                                @NonNull String MODE,
                                @NonNull String TIME,
                                @NonNull String ARRIVEDEPART,
                                Boolean SUNDAY,
                                Boolean MONDAY,
                                Boolean TUESDAY,
                                Boolean WEDNESDAY,
                                Boolean THURSDAY,
                                Boolean FRIDAY,
                                Boolean SATURDAY,
                                Boolean REMINDER30,
                                Boolean REMINDER5,
                                Boolean REMINDERAUTO,
                                Boolean REMINDERBT
                            )
    {
        this.fromAddr = FROM;
        this.fromAlias = FROMALIAS;
        this.toAddr = TO;
        this.toAlias = TOALIAS;
        this.transportMode = MODE;
        this.routeTime = TIME;
        this.routeArriveDepart = ARRIVEDEPART;
        this.sunday = SUNDAY;
        this.monday = MONDAY;
        this.tuesday = TUESDAY;
        this.wednesday = WEDNESDAY;
        this.thursday = THURSDAY;
        this.friday = FRIDAY;
        this.saturday = SATURDAY;
        this.reminder5 = REMINDER5;
        this.reminder30 = REMINDER30;
        this.reminderAuto = REMINDERAUTO;
        this.reminderBT = REMINDERBT;
    }

    public CommuteDataClass()
    {

    }
}

