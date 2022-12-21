package com.myreader.querydslreader.reader;

import com.myreader.querydslreader.QuerydslPagingItemReader;
import com.myreader.querydslreader.QuerydslPagingItemReaderBuilder;
import com.myreader.querydslreader.setting.entity.Manufacture;
import com.myreader.querydslreader.setting.entity.QManufacture;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManagerFactory;

import java.time.LocalDate;

import static com.myreader.querydslreader.setting.entity.QManufacture.manufacture;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class QuerydslPagingItemReaderBuilderTest {

    @Autowired
    EntityManagerFactory entityManagerFactory;

    @Test
    public void create_instance_with_builder() {

        QuerydslPagingItemReader<Manufacture> reader
                = new QuerydslPagingItemReaderBuilder<Manufacture>()
                .entityManagerFactory(entityManagerFactory)
                .pageSize(15)
                .build();

        assertThat(reader.getPageSize()).isEqualTo(15);
    }

    @Test
    public void make_lambda_with_builder() throws Exception {
        LocalDate txDate = LocalDate.of(2022, 12, 21);

        QuerydslPagingItemReader<Manufacture> reader
                = new QuerydslPagingItemReaderBuilder<Manufacture>()
                .name("lambdaBuilder")
                .entityManagerFactory(entityManagerFactory)
                .queryFunction(
                        queryFactory -> queryFactory
                                .selectFrom(manufacture)
                                .where(manufacture.createDate.eq(txDate))
                )
                .pageSize(15)
                .build();

        reader.open(new ExecutionContext());

        Manufacture read = reader.read();

        assertThat(read).isNull();
    }

}