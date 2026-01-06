package com.redis.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.redis.controller.EmployeeController;
import com.redis.entity.Employee;
import com.redis.repository.EmployeeRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class EmployeeService {

    private final Logger log = LoggerFactory.getLogger(EmployeeService.class);


    private EmployeeRepo employeeRepo;
    private final ObjectMapper objectMapper;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    public EmployeeService(EmployeeRepo employeeRepo,ObjectMapper objectMapper){
        this.employeeRepo = employeeRepo;
        this.objectMapper = objectMapper;
    }


    //Save the employee in db as well as in redis.......................

   // @Cacheable(cacheNames = "users :")
    public Employee createEmployee(Employee employee){

        log.info("Processing with employee save. {}...." , employee.getFirstName());
        //Save into db
        Employee save = employeeRepo.save(employee);
        log.info("Employee save success.{}...." , save.getFirstName());

//        //covert obj into as string.........
        try {
            String empJson = objectMapper.writeValueAsString(save);
            ValueOperations<String, String> opsForValue = redisTemplate.opsForValue();

            log.info("Saving Employee to Redis : {} ",empJson);
            opsForValue.set("user:" + save.getId(),empJson,100, TimeUnit.SECONDS);
            log.info("Saved Success on Redis");

        } catch (JsonProcessingException e) {
            log.error("Error while saving Employee to Redis : {} ",e.getMessage());
            throw new RuntimeException(e);
        }

        log.info("Returning from create employee method");
        return  employee;
    }

    public Employee getEmployeeById(Integer id)  {

        log.info("Processing get employee by id {}...." , id);
        Employee employee = null;

        //1st Check redis...........
        log.info("Checking on redis by employee id : {}",id);
        String cachedUser = redisTemplate.opsForValue().get("user:" + id);

        //Check if cachedUser is null
        if(cachedUser != null){
            try {

                //deserialize cachedUser................
              employee =   objectMapper.readValue(cachedUser,Employee.class);
              log.info("Returning Employee from Redis : {} ",employee.getFirstName());
                return employee;

            } catch (Exception e) {
                log.error("Error while getting Employee from Redis : {} ",e.getMessage());
                throw new RuntimeException(e);
            }
        }else{

            log.info("Cache missed now fetching from DB");
            Optional<Employee> byId = employeeRepo.findById(id);

            if(byId.isPresent()){
                employee = byId.get();

                try {
                    //Now serialize and save into redis
                    String employeeById = objectMapper.writeValueAsString(employee);

                    redisTemplate.opsForValue().set("user:" + employee.getId(),employeeById,100,TimeUnit.SECONDS);
                    log.info("Fetched From DB and caching on redis");
                } catch (Exception e) {
                    log.error("Error while Caching Employee from Redis : {} ",e.getMessage());
                    throw new RuntimeException(e);
                }
            }else {
                log.error("Employee Not Found In Cached & DB");
                throw new RuntimeException("Employee Not Found With This Id :- " + id);            }
        }
        return employee;
    }

    public Employee updateEmployee(Integer id,Employee employee){

        log.info("Processing with employee update ");
        
        log.info("Finding Employee in DB by EmpId : {} ",id);
        Employee employee1 = null;
        
        Optional<Employee> empById = employeeRepo.findById(id);
        if(empById.isPresent()){
            employee1 = empById.get();
            employee1.setEmail(employee.getEmail());
            employee1.setFirstName(employee.getFirstName());
            employee1.setLastName(employee.getLastName());
            
            log.info("Now Saving the updated Employee Into DB");
            Employee updatedEmployee = employeeRepo.save(employee1);

            log.info("Employee Updated Successfully");

            //Convert updatedEmployee to String to save on redis.........
            try {
                String updatedJason = objectMapper.writeValueAsString(updatedEmployee);
                log.info("Saving On Redis");
                redisTemplate.opsForValue().set("user:"+updatedEmployee.getId(),updatedJason,100,TimeUnit.SECONDS);
                log.info("Employee Updated on Redis");
            }catch (Exception e){
                log.error("Error while updating Employee to Redis : {} ",e.getMessage());
                throw new RuntimeException(e.getMessage());
            }
        }
        return employee1;
    }

    public void deleteEmployee(Integer id){

        log.info("Processing request to delete Employee");
        log.info("Deleting Employee from DB : {} ",id);

        //check If Employee Exists By id
        if(employeeRepo.existsById(id)){

            employeeRepo.deleteById(id);
            //Now Delete also from Redis
            redisTemplate.delete("user:"+id);
        }else{
            log.info("Employee not Found While deleting With the Given id");
            throw new RuntimeException("Employee Not Found With This Id" + id);
        }
        log.info("Deleted Successfully from Redis and DB");
    }
}
