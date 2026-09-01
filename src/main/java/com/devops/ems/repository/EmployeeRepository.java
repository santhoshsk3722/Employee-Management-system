package com.devops.ems.repository;

import com.devops.ems.model.Department;
import com.devops.ems.model.Employee;
import com.devops.ems.model.EmployeeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Optional<Employee> findByEmail(String email);

    boolean existsByEmail(String email);

    List<Employee> findByDepartment(Department department);

    List<Employee> findByStatus(EmployeeStatus status);

    List<Employee> findByManagerId(Long managerId);

    List<Employee> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
            String firstName, String lastName);

    @Query("SELECT e.department, COUNT(e) FROM Employee e GROUP BY e.department")
    List<Object[]> countEmployeesByDepartment();

    @Query("SELECT AVG(e.salary) FROM Employee e WHERE e.department = :department")
    Double averageSalaryByDepartment(@Param("department") Department department);
}
