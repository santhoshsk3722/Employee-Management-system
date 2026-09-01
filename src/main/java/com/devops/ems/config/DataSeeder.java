package com.devops.ems.config;

import com.devops.ems.model.Department;
import com.devops.ems.model.Employee;
import com.devops.ems.model.EmployeeStatus;
import com.devops.ems.repository.EmployeeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.LocalDate;

/**
 * Seeds a handful of sample employees so the API has data to explore
 * immediately after startup. Only runs on the 'dev' (H2) profile.
 */
@Configuration
@Profile("dev")
public class DataSeeder {

    @Bean
    CommandLineRunner seedData(EmployeeRepository repository) {
        return args -> {
            if (repository.count() > 0) {
                return;
            }

            Employee cto = new Employee();
            cto.setFirstName("Asha");
            cto.setLastName("Rao");
            cto.setEmail("asha.rao@example.com");
            cto.setPhoneNumber("9876500001");
            cto.setDepartment(Department.DEVOPS);
            cto.setJobTitle("Head of Engineering");
            cto.setSalary(180000.0);
            cto.setHireDate(LocalDate.of(2019, 3, 1));
            cto.setStatus(EmployeeStatus.ACTIVE);
            repository.save(cto);

            Employee devops1 = new Employee();
            devops1.setFirstName("Karthik");
            devops1.setLastName("Subramaniam");
            devops1.setEmail("karthik.s@example.com");
            devops1.setPhoneNumber("9876500002");
            devops1.setDepartment(Department.DEVOPS);
            devops1.setJobTitle("DevOps Engineer");
            devops1.setSalary(95000.0);
            devops1.setHireDate(LocalDate.of(2022, 7, 15));
            devops1.setStatus(EmployeeStatus.ACTIVE);
            devops1.setManager(cto);
            repository.save(devops1);

            Employee eng1 = new Employee();
            eng1.setFirstName("Priya");
            eng1.setLastName("Menon");
            eng1.setEmail("priya.menon@example.com");
            eng1.setPhoneNumber("9876500003");
            eng1.setDepartment(Department.ENGINEERING);
            eng1.setJobTitle("Backend Developer");
            eng1.setSalary(88000.0);
            eng1.setHireDate(LocalDate.of(2023, 1, 10));
            eng1.setStatus(EmployeeStatus.ACTIVE);
            eng1.setManager(cto);
            repository.save(eng1);

            Employee hr1 = new Employee();
            hr1.setFirstName("Divya");
            hr1.setLastName("Nair");
            hr1.setEmail("divya.nair@example.com");
            hr1.setPhoneNumber("9876500004");
            hr1.setDepartment(Department.HUMAN_RESOURCES);
            hr1.setJobTitle("HR Manager");
            hr1.setSalary(75000.0);
            hr1.setHireDate(LocalDate.of(2021, 11, 5));
            hr1.setStatus(EmployeeStatus.ON_LEAVE);
            repository.save(hr1);
        };
    }
}
