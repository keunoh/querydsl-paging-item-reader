package com.myreader.querydslreader;

import com.myreader.querydslreader.setting.entity.Manufacture;
import com.myreader.querydslreader.setting.entity.ManufactureBackup;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;

import static com.myreader.querydslreader.setting.entity.QManufacture.manufacture;

@RequiredArgsConstructor
@Configuration
public class QuerydslPagingItemReaderConfiguration {

    public static final String JOB_NAME = "querydslPagingReaderJob";
    public static final String STEP_NAME = "querydslPagingReaderStep";
    private final int CHUNK_SIZE = 100;

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory emf;

    @Bean
    public Job job() {
        return jobBuilderFactory.get(JOB_NAME)
                .start(step())
                .build();
    }

    @Bean
    public Step step() {
        return stepBuilderFactory.get(STEP_NAME)
                .<Manufacture, ManufactureBackup>chunk(CHUNK_SIZE)
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .build();
    }

    @Bean
    public QuerydslPagingItemReader<Manufacture> reader() {
        return new QuerydslPagingItemReaderBuilder<Manufacture>()
                .name("queryReader")
                .entityManagerFactory(emf)
                .queryFunction(
                        queryFactory -> queryFactory
                                .selectFrom(manufacture)
                                .where(manufacture.createDate.isNotNull())
                )
                .build();
    }

    public ItemProcessor<Manufacture, ManufactureBackup> processor() {
        return ManufactureBackup::new;
    }

    @Bean
    public JpaItemWriter<ManufactureBackup> writer() {
        return new JpaItemWriterBuilder<ManufactureBackup>()
                .entityManagerFactory(emf)
                .build();
    }
}
