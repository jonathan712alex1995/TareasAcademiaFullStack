package com.xideral.ejemploBatch.controller;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/importar")
public class BatchController {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job jobImportarPersonas;

    @PostMapping
    public ResponseEntity<String> lanzarBatch() {
        try {
            // Siempre usar parámetros únicos para evitar conflictos
            JobParameters params = new JobParametersBuilder()
                    .addString("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                    .toJobParameters();

            jobLauncher.run(jobImportarPersonas, params);

            return ResponseEntity.ok("✅ Job lanzado correctamente.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("❌ Error al lanzar el job: " + e.getMessage());
        }
    }
}
