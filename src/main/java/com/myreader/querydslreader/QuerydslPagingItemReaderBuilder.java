package com.myreader.querydslreader;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.batch.item.database.orm.JpaQueryProvider;
import org.springframework.util.Assert;

import javax.persistence.EntityManagerFactory;
import java.util.Map;
import java.util.function.Function;

public class QuerydslPagingItemReaderBuilder<T> {

    private int pageSize = 10;

    private EntityManagerFactory entityManagerFactory;

    private Map<String, Object> parameterValues;

    private boolean transacted = true;

    private String queryString;

    private JpaQueryProvider queryProvider;

    private boolean saveState = true;

    private String name;

    private int maxItemCount = Integer.MAX_VALUE;

    private int currentItemCount;

    private Function<JPAQueryFactory, JPAQuery<T>> queryFunction;

    /**
     * Configure if the state of org.springframework.batch.item.ItemStreamSupport should be
     * persisted within the org.springframework.batch.item.ExecutionContext for restart purposes.
     * @param saveState - defaults to true
     * @return The current instance of the builder
     */
    public QuerydslPagingItemReaderBuilder<T> saveState(boolean saveState) {
        this.saveState = saveState;

        return this;
    }

    /**
     * The name used to calculate the key within the org.springframework.batch.item.
     * ExecutionContext. Required if saveState(boolean) is set to true.
     * @param name - name of the reader instance
     * @return The current instance of the builder
     */
    public QuerydslPagingItemReaderBuilder<T> name(String name) {
        this.name = name;

        return this;
    }

    /**
     * Configure the max number of items to be read.
     * @param maxItemCount - the max items to be read
     * @return The current instance of the builder.
     */
    public QuerydslPagingItemReaderBuilder<T> maxItemCount(int maxItemCount) {
        this.maxItemCount = maxItemCount;

        return this;
    }

    /**
     * Index for the current item. Used on restarts to indicate where to start from.
     * @param currentItemCount - current index
     * @return this instance for method chaining
     */
    public QuerydslPagingItemReaderBuilder<T> currentItemCount(int currentItemCount) {
        this.currentItemCount = currentItemCount;

        return this;
    }

    /**
     * The number of records to request per page/query. Defaults to 10. Must be greater than zero.
     * @param pageSize - number of items
     * @return this instance for method chaining
     */
    public QuerydslPagingItemReaderBuilder<T> pageSize(int pageSize) {
        this.pageSize = pageSize;

        return this;
    }

    /**
     * A map of parameter values to be set on the query. The key of the map is the name of the
     * parameter to be set with the value being the value to set.
     * @param parameterValues - map of values
     * @return this instance for method chaining
     */
    public QuerydslPagingItemReaderBuilder<T> parameterValues(Map<String, Object> parameterValues) {
        this.parameterValues = parameterValues;

        return this;
    }

    /**
     * A query provider. This should be set only if queryString(String) have not been set.
     * @param queryProvider - the query provider
     * @return this instance for method chaining
     */
    public QuerydslPagingItemReaderBuilder<T> queryProvider(JpaQueryProvider queryProvider) {
        this.queryProvider = queryProvider;

        return this;
    }

    /**
     * The HQL query string to execute. This should only be set if queryProvider(JpaQueryProvider) has
     * not been set.
     * @param queryString - the HQL query
     * @return this instance for method chaining
     */
    public QuerydslPagingItemReaderBuilder<T> queryString(String queryString) {
        this.queryString = queryString;

        return this;
    }

    /**
     * Indicates if a transaction should be created around the read (true by default). Can be set to false in
     * causes where JPA implementation doesn't support a particular transaction, however this may cause
     * object inconsistency in the EntityManagerFactory.
     * @param transacted - defaults to true
     * @return this instance for method chaining
     */
    public QuerydslPagingItemReaderBuilder<T> transacted(boolean transacted) {
        this.transacted = transacted;

        return this;
    }

    /**
     * The EntityManagerFactory to be used for executing the configured queryString.
     * @param entityManagerFactory - EntityManagerFactory used to create javax.persistence.EntityManager
     * @return this instance for method chaining
     */
    public QuerydslPagingItemReaderBuilder<T> entityManagerFactory(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;

        return this;
    }

    /**
     * You can make querydsl here with lambda function
     * @param queryFunction
     * @return this instance for method chaining
     */
    public QuerydslPagingItemReaderBuilder<T> queryFunction(Function<JPAQueryFactory, JPAQuery<T>> queryFunction) {
        this.queryFunction = queryFunction;

        return this;
    }

    public QuerydslPagingItemReader<T> build() {
        Assert.isTrue(this.pageSize > 0, "pageSize must be greater than zero");
        Assert.notNull(this.entityManagerFactory, "An EntityManagerFactory is required");

        QuerydslPagingItemReader reader = new QuerydslPagingItemReader();

        reader.setQueryFunction(this.queryFunction);
        reader.setTransacted(this.transacted);
        reader.setEntityManagerFactory(this.entityManagerFactory);
        reader.setName(this.name);
        reader.setPageSize(this.pageSize);
        reader.setCurrentItemCount(this.currentItemCount);
        reader.setMaxItemCount(this.maxItemCount);
        reader.setSaveState(this.saveState);

        return reader;
    }
}
















