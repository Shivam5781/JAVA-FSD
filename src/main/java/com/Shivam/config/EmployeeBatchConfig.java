package com.Shivam.config;
import com.Shivam.model.Employee;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.data.MongoItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.mongodb.core.MongoTemplate;
import com.Shivam.processor.EmployeeProcessor;

@Configuration
@EnableBatchProcessing
public class EmployeeBatchConfig {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory sbBuilderFactory;
    @Autowired
    private EmployeeProcessor processor;

    @Autowired
    private MongoTemplate template;

    @Bean
    public FlatFileItemReader<Employee> reader() {
        FlatFileItemReader<Employee> reader=new FlatFileItemReader<Employee>();
        reader.setResource(new FileSystemResource("C:\\csv\\Employee.csv"));
        reader.setLineMapper(new DefaultLineMapper<Employee>(){{
            setLineTokenizer(new DelimitedLineTokenizer(){{
                setNames(new String[]{"id","name","age","address"});
            }});
            setFieldSetMapper(new BeanWrapperFieldSetMapper<Employee>(){{
                setTargetType(Employee.class);
            }});
        }});
        return reader;
    }
    //create writer
    @Bean
    public MongoItemWriter<Employee> writer(){
        MongoItemWriter<Employee> writer = new MongoItemWriter<Employee>();
        writer.setTemplate(template);
        writer.setCollection("Employee1");
        return writer;
    }

    @Bean
    public Step step(){
        return sbBuilderFactory.get("step1").<Employee,Employee>chunk(2)
                .reader(reader())
                .processor(processor)
                .writer(writer())
                .build();
    }
    @Bean
    public Job job(){
        return jobBuilderFactory.get("job")
                .incrementer(new RunIdIncrementer())
                .start(step())
                .build();
    }



}
