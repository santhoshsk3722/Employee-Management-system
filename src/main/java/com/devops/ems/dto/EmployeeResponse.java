package com.devops.ems.dto;

import com.devops.ems.model.Department;
import com.devops.ems.model.Employee;
import com.devops.ems.model.EmployeeStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class EmployeeResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private Department department;
    private String jobTitle;
    private Double salary;
    private LocalDate hireDate;
    private EmployeeStatus status;
    private Long managerId;
    private String managerName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static EmployeeResponse fromEntity(Employee e) {
        EmployeeResponse r = new EmployeeResponse();
        r.id = e.getId();
        r.firstName = e.getFirstName();
        r.lastName = e.getLastName();
        r.email = e.getEmail();
        r.phoneNumber = e.getPhoneNumber();
        r.department = e.getDepartment();
        r.jobTitle = e.getJobTitle();
        r.salary = e.getSalary();
        r.hireDate = e.getHireDate();
        r.status = e.getStatus();
        r.createdAt = e.getCreatedAt();
        r.updatedAt = e.getUpdatedAt();
        if (e.getManager() != null) {
            r.managerId = e.getManager().getId();
            r.managerName = e.getManager().getFirstName() + " " + e.getManager().getLastName();
        }
        return r;
    }

    // ---------- Getters ----------

    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public Department getDepartment() {
        return department;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public Double getSalary() {
        return salary;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public EmployeeStatus getStatus() {
        return status;
    }

    public Long getManagerId() {
        return managerId;
    }

    public String getManagerName() {
        return managerName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
