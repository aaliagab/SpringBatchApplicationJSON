package com.batch.config;

import com.batch.entities.Person;
import com.batch.steps.PersonItemProcessor;
import com.batch.steps.PersonItemWriter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.json.builder.JsonItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableBatchProcessing
public class BatchConfig {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    @Bean
    public PersonItemWriter personItemWriter(){
        return new PersonItemWriter();
    }
    @Bean
    public PersonItemProcessor personItemProcessor(){return new PersonItemProcessor();}

    @Bean
    public Step readFile(){
        return stepBuilderFactory.get("readFile")
                .<Person,Person>chunk(1000)//tamanho de lote
                .reader(jsonItemReader())
                .processor(personItemProcessor())
                .writer(personItemWriter())
                .taskExecutor(taskExecutor())
                .build();
    }

    @Bean
    public JsonItemReader<Person> jsonItemReader() {
        return new JsonItemReaderBuilder<Person>()
                .jsonObjectReader(new JacksonJsonObjectReader<>(Person.class))
                .resource(new ClassPathResource("persons.json"))
                .name("personsJsonItemReader")
                .build();
    }

    @Bean
    public Job job(){
        return jobBuilderFactory.get("readFileWithChunk")
                .start(readFile())
                .build();
    }


    @Bean
    public TaskExecutor taskExecutor(){ //configurando manejo con thread para las tareas
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(1);//que inicie con un solo thread por defecto serian 2
                                        //lo que haria que se dupliquen los datos

        taskExecutor.setMaxPoolSize(2);//maximo de thread concurrentes en caso que el inicial no
                                       //se baste solo para la ejecucion
        taskExecutor.setQueueCapacity(5);//tareas en cola

        return taskExecutor;
    }

}
