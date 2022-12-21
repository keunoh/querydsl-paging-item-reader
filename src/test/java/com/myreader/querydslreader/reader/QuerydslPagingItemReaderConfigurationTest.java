package com.myreader.querydslreader.reader;


import com.myreader.querydslreader.QuerydslPagingItemReaderConfiguration;
import com.myreader.querydslreader.QuerydslReaderApplication;
import com.myreader.querydslreader.QuerydslReaderApplicationTests;
import com.myreader.querydslreader.setting.entity.Manufacture;
import com.myreader.querydslreader.setting.entity.ManufactureBackup;
import com.myreader.querydslreader.setting.entity.ManufactureBackupRepository;
import com.myreader.querydslreader.setting.entity.ManufactureRepository;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static java.time.format.DateTimeFormatter.ofPattern;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBatchTest
@SpringBootTest(classes = {QuerydslReaderApplicationTests.class, QuerydslPagingItemReaderConfiguration.class })
public class QuerydslPagingItemReaderConfigurationTest {
    public static final DateTimeFormatter FORMATTER = ofPattern("yyyy-MM-dd");

    @Autowired
    private ManufactureRepository manufactureRepository;

    @Autowired
    private ManufactureBackupRepository manufactureBackupRepository;

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;


    @Test
    public void querydsl_job_test() throws Exception {
        //given
        LocalDate txDate = LocalDate.of(2022, 12, 21);
        String name = "test";
        int price1 = 1000;
        int price2 = 2000;

        manufactureRepository.save(new Manufacture(name, price1, txDate));
        manufactureRepository.save(new Manufacture(name, price2, txDate));

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("txDate", txDate.format(FORMATTER))
                .toJobParameters();

        //when
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

        //then
        assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
        List<ManufactureBackup> result = manufactureBackupRepository.findAll();
        assertThat(result.size()).isEqualTo(2);
    }


}