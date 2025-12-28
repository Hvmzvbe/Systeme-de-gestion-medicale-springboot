package com.medical.appointmentsservice.entity;

public enum AppointmentStatus {
    SCHEDULED("Planifié"),
    COMPLETED("Complété"),
    CANCELLED("Annulé"),
    NO_SHOW("Absent");

    private final String label;

    AppointmentStatus(String label) {
        this.label = label;
    }
}