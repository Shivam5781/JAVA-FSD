package com.Shivam.processor;

import com.Shivam.model.Employee;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class EmployeeProcessor implements ItemProcessor <Employee,Employee> {

    @Override
    public Employee process(Employee item) throws Exception {
        return item;
    }}