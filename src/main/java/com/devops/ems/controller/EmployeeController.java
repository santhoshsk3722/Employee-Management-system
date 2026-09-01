package com.devops.ems.controller;

import com.devops.ems.dto.EmployeeRequest;
import com.devops.ems.dto.EmployeeResponse;
import com.devops.ems.model.Department;
import com.devops.ems.model.EmployeeStatus;
import com.devops.ems.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    // Create
    @PostMapping
    public ResponseEntity<EmployeeResponse> createEmployee(@Valid @RequestBody EmployeeRequest request) {
        EmployeeResponse created = employeeService.createEmployee(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // Read - all, with optional filters
    @GetMapping
    public ResponseEntity<List<EmployeeResponse>> getEmployees(
            @RequestParam(required = false) Department department,
            @RequestParam(required = false) EmployeeStatus status,
            @RequestParam(required = false) String search) {

        List<EmployeeResponse> result;
        if (search != null && !search.isBlank()) {
            result = employeeService.searchEmployees(search);
        } else if (department != null) {
            result = employeeService.getEmployeesByDepartment(department);
        } else if (status != null) {
            result = employeeService.getEmployeesByStatus(status);
        } else {
            result = employeeService.getAllEmployees();
        }
        return ResponseEntity.ok(result);
    }

    // Read - single
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponse> getEmployeeById(@PathVariable Long id) {
        return ResponseEntity.ok(employeeService.getEmployeeById(id));
    }

    // Read - direct reports of a manager
    @GetMapping("/{id}/direct-reports")
    public ResponseEntity<List<EmployeeResponse>> getDirectReports(@PathVariable Long id) {
        return ResponseEntity.ok(employeeService.getDirectReports(id));
    }

    // Update - full replace
    @PutMapping("/{id}")
    public ResponseEntity<EmployeeResponse> updateEmployee(
            @PathVariable Long id, @Valid @RequestBody EmployeeRequest request) {
        return ResponseEntity.ok(employeeService.updateEmployee(id, request));
    }

    // Update - status only
    @PatchMapping("/{id}/status")
    public ResponseEntity<EmployeeResponse> updateStatus(
            @PathVariable Long id, @RequestParam EmployeeStatus status) {
        return ResponseEntity.ok(employeeService.patchStatus(id, status));
    }

    // Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }

    // Reporting - headcount per department
    @GetMapping("/stats/headcount")
    public ResponseEntity<Map<String, Long>> getHeadcountByDepartment() {
        return ResponseEntity.ok(employeeService.getHeadcountByDepartment());
    }

    // Reporting - average salary in a department
    @GetMapping("/stats/average-salary")
    public ResponseEntity<Map<String, Object>> getAverageSalary(@RequestParam Department department) {
        Double avg = employeeService.getAverageSalaryByDepartment(department);
        return ResponseEntity.ok(Map.of("department", department, "averageSalary", avg));
    }
}
