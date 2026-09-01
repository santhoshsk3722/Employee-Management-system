package com.devops.ems.service;

import com.devops.ems.dto.EmployeeRequest;
import com.devops.ems.dto.EmployeeResponse;
import com.devops.ems.exception.DuplicateResourceException;
import com.devops.ems.exception.ResourceNotFoundException;
import com.devops.ems.model.Department;
import com.devops.ems.model.Employee;
import com.devops.ems.model.EmployeeStatus;
import com.devops.ems.repository.EmployeeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public EmployeeResponse createEmployee(EmployeeRequest request) {
        if (employeeRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("An employee with email '" + request.getEmail() + "' already exists");
        }

        Employee employee = new Employee();
        mapRequestToEntity(request, employee);

        Employee saved = employeeRepository.save(employee);
        return EmployeeResponse.fromEntity(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeResponse getEmployeeById(Long id) {
        return EmployeeResponse.fromEntity(findEmployeeOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeResponse> getAllEmployees() {
        return employeeRepository.findAll().stream()
                .map(EmployeeResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeResponse> getEmployeesByDepartment(Department department) {
        return employeeRepository.findByDepartment(department).stream()
                .map(EmployeeResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeResponse> getEmployeesByStatus(EmployeeStatus status) {
        return employeeRepository.findByStatus(status).stream()
                .map(EmployeeResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeResponse> getDirectReports(Long managerId) {
        findEmployeeOrThrow(managerId); // ensures manager exists
        return employeeRepository.findByManagerId(managerId).stream()
                .map(EmployeeResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeResponse> searchEmployees(String name) {
        return employeeRepository
                .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(name, name).stream()
                .map(EmployeeResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public EmployeeResponse updateEmployee(Long id, EmployeeRequest request) {
        Employee employee = findEmployeeOrThrow(id);

        // If email is changing, make sure the new one isn't taken by someone else
        if (!employee.getEmail().equalsIgnoreCase(request.getEmail())
                && employeeRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("An employee with email '" + request.getEmail() + "' already exists");
        }

        mapRequestToEntity(request, employee);
        Employee saved = employeeRepository.save(employee);
        return EmployeeResponse.fromEntity(saved);
    }

    @Override
    public EmployeeResponse patchStatus(Long id, EmployeeStatus status) {
        Employee employee = findEmployeeOrThrow(id);
        employee.setStatus(status);
        return EmployeeResponse.fromEntity(employeeRepository.save(employee));
    }

    @Override
    public void deleteEmployee(Long id) {
        Employee employee = findEmployeeOrThrow(id);
        employeeRepository.delete(employee);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getHeadcountByDepartment() {
        Map<String, Long> result = new LinkedHashMap<>();
        for (Object[] row : employeeRepository.countEmployeesByDepartment()) {
            result.put(((Department) row[0]).name(), (Long) row[1]);
        }
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Double getAverageSalaryByDepartment(Department department) {
        Double avg = employeeRepository.averageSalaryByDepartment(department);
        return avg == null ? 0.0 : avg;
    }

    // ---------- helpers ----------

    private Employee findEmployeeOrThrow(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
    }

    private void mapRequestToEntity(EmployeeRequest request, Employee employee) {
        employee.setFirstName(request.getFirstName());
        employee.setLastName(request.getLastName());
        employee.setEmail(request.getEmail());
        employee.setPhoneNumber(request.getPhoneNumber());
        employee.setDepartment(request.getDepartment());
        employee.setJobTitle(request.getJobTitle());
        employee.setSalary(request.getSalary());
        employee.setHireDate(request.getHireDate());
        employee.setStatus(request.getStatus() != null ? request.getStatus() : EmployeeStatus.ACTIVE);

        if (request.getManagerId() != null) {
            if (request.getManagerId().equals(employee.getId())) {
                throw new IllegalArgumentException("An employee cannot be their own manager");
            }
            Employee manager = employeeRepository.findById(request.getManagerId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Manager not found with id: " + request.getManagerId()));
            employee.setManager(manager);
        } else {
            employee.setManager(null);
        }
    }
}
