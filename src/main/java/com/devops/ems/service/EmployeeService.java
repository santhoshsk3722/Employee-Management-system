package com.devops.ems.service;

import com.devops.ems.dto.EmployeeRequest;
import com.devops.ems.dto.EmployeeResponse;
import com.devops.ems.model.Department;
import com.devops.ems.model.EmployeeStatus;

import java.util.List;
import java.util.Map;

public interface EmployeeService {

    EmployeeResponse createEmployee(EmployeeRequest request);

    EmployeeResponse getEmployeeById(Long id);

    List<EmployeeResponse> getAllEmployees();

    List<EmployeeResponse> getEmployeesByDepartment(Department department);

    List<EmployeeResponse> getEmployeesByStatus(EmployeeStatus status);

    List<EmployeeResponse> getDirectReports(Long managerId);

    List<EmployeeResponse> searchEmployees(String name);

    EmployeeResponse updateEmployee(Long id, EmployeeRequest request);

    EmployeeResponse patchStatus(Long id, EmployeeStatus status);

    void deleteEmployee(Long id);

    Map<String, Long> getHeadcountByDepartment();

    Double getAverageSalaryByDepartment(Department department);
}
