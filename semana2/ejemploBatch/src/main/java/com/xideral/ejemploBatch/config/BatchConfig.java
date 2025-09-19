package com.xideral.ejemploBatch.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

import com.xideral.ejemploBatch.models.Persona;
import com.xideral.ejemploBatch.repositorys.PersonaRepository;

@Configuration
@EnableBatchProcessing
public class BatchConfig {
	
	//1. leer archivo csv
	@Bean
    public FlatFileItemReader<Persona> lectorCSV() {
        return new FlatFileItemReaderBuilder<Persona>()
                .name("lectorPersona")
                .resource(new ClassPathResource("input/personas.csv"))
                .linesToSkip(1) // salta encabezado
                .lineMapper(new DefaultLineMapper<>() {{
                    setLineTokenizer(new DelimitedLineTokenizer() {{
                        setNames("nombre", "edad", "nacionalidad");
                    }});
                    setFieldSetMapper(new BeanWrapperFieldSetMapper<>() {{
                        setTargetType(Persona.class);
                    }});
                }})
                .build();
    }
	
	//2. convertimos nombre a mayusculas (solo se agrega este paso si es necesario realizar cambios)
	@Bean
	public ItemProcessor<Persona , Persona> procesador(){
		return persona -> {
			persona.setNombre(persona.getNombre().toUpperCase());
			return persona;
		};
	}
	
	//3. enviar datos a base de datos
	@Bean
	public RepositoryItemWriter<Persona> enviar(PersonaRepository personaRepo){
		RepositoryItemWriter<Persona> writer = new RepositoryItemWriter<>();
		writer.setRepository(personaRepo);
		writer.setMethodName("save");
		return writer;
	}
	
	//4. Step
	@Bean
	public Step pasoImportarPersonas(JobRepository jobRepository,
	                                 PlatformTransactionManager transactionManager,
	                                 FlatFileItemReader<Persona> reader,
	                                 ItemProcessor<Persona, Persona> processor,
	                                 RepositoryItemWriter<Persona> writer) {
	    return new StepBuilder("importar-personas-step", jobRepository)
	            .<Persona, Persona>chunk(10, transactionManager)
	            .reader(reader)
	            .processor(processor)
	            .writer(writer)
	            .build();
	}

	
	// 5. JOB
	@Bean
	public Job jobImportarPersonas(JobRepository jobRepository, Step pasoImportarPersonas) {
	    return new JobBuilder("importar-personas-job", jobRepository)
	            .incrementer(new RunIdIncrementer())
	            .start(pasoImportarPersonas)
	            .build();
	}


}


