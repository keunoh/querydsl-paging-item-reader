package com.myreader.querydslreader;

import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.batch.item.database.AbstractPagingItemReader;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.util.ClassUtils;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;

public class QuerydslPagingItemReader<T> extends AbstractPagingItemReader<T> {

    private EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;
    private final Map<String, Object> jpaPropertyMap = new HashMap<>();
    private Function<JPAQueryFactory, JPAQuery<T>> queryFunction;
    private boolean transacted = true;

    public QuerydslPagingItemReader() {
        setName(ClassUtils.getShortName(QuerydslPagingItemReader.class));
    }

    private JPAQuery<T> createQuery() {
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        return queryFunction.apply(queryFactory);
    }

    /**
     * By default (true) the EntityTransaction will be started and committed around the read. Can be
     * overriden (false) in cases where the JPA implementation doesn't support a particular transaction.
     * (e.g. Hibernate with JTA transaction). NOTE: may cause problems in guaranteeing the object
     * consistency in the EntityManagerFactory.
     * @param transacted - indicator
     */
    public void setTransacted(boolean transacted) {
        this.transacted = transacted;
    }

    public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    public void setQueryFunction(Function<JPAQueryFactory, JPAQuery<T>> queryFunction) {
        this.queryFunction = queryFunction;
    }

    @Override
    protected void doOpen() throws Exception {
        super.doOpen();

        entityManager = entityManagerFactory.createEntityManager(jpaPropertyMap);
        if (entityManager == null) {
            throw new DataAccessResourceFailureException("Unable to obtain an EntityManager");
        }

    }

    @Override
    protected void doReadPage() {
        EntityTransaction tx = null;

        if (transacted) {
            tx = entityManager.getTransaction();
            tx.begin();

            entityManager.flush();
            entityManager.clear();
        }//end if

        JPQLQuery<T> query = createQuery().offset(getPage() * getPageSize()).limit(getPageSize());

        if (results == null) {
            results = new CopyOnWriteArrayList<>();
        } else {
            results.clear();
        }

        if (!transacted) {
            List<T> queryResult = query.fetch();
            for (T entity : queryResult) {
                entityManager.detach(entity);
                results.add(entity);
            }//end if
        } else {
            results.addAll(query.fetch());
            tx.commit();
        }//end if
    }

    @Override
    protected void doJumpToPage(int itemIndex) {

    }

    @Override
    protected void doClose() throws Exception {
        entityManager.close();
        super.doClose();
    }
}






















