package com.mindex.challenge.data;

public class ReportingStructure {
    private Employee employee;
    private long numberOfReports;

    public ReportingStructure() {
    }

    public long getNumberOfReports() {
        return numberOfReports;
    }

    public void setNumberOfReports(long numberOfReports) {
        this.numberOfReports = numberOfReports;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }
}
