package com.la.northwind_java.repositories;
import org.springframework.data.jpa.repository.JpaRepository;

import com.la.northwind_java.models.Employee;



public interface EmployeeRepository extends JpaRepository<Employee, Integer>{

}
