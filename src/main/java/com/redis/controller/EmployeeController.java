package com.redis.controller;

import com.redis.entity.Employee;
import com.redis.service.EmployeeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/employee")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService){
        this.employeeService = employeeService;
    }


    @PostMapping("/create")
    public ResponseEntity<Employee> create(@RequestBody Employee employee){
        employeeService.createEmployee(employee);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/getemployee/{id}")
    public Employee getEmployeeById(@PathVariable Integer id){

        Employee employeeById = employeeService.getEmployeeById(id);

        return  employeeById;
    }

    @PutMapping("/update/{id}")
    public Employee updateEmployee(@PathVariable Integer id,@RequestBody Employee employee){

        Employee updatedEmployee = employeeService.updateEmployee(id, employee);
        return  updatedEmployee;
    }


    @DeleteMapping("/delete/{id}")
    public String deletEmployeeById(@PathVariable Integer id){
        employeeService.deleteEmployee(id);
        return "Deleted SuccessFully";
    }
}
