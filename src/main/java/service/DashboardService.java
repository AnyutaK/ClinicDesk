package service;

import dao.DashboardDAO;

public class DashboardService {

    private final DashboardDAO dashboardDAO;

    public DashboardService(DashboardDAO dashboardDAO) {
        this.dashboardDAO = dashboardDAO;
    }


    public int getTodayAppointments() {
        return dashboardDAO.getTodayAppointments();
    }


    public int getWaitingPatients() {
        return dashboardDAO.getWaitingPatients();
    }


    public int getDoctorsOnDuty() {
        return dashboardDAO.getDoctorsOnDuty();
    }


    public double getUtilization() {
        return dashboardDAO.getUtilization();
    }
}