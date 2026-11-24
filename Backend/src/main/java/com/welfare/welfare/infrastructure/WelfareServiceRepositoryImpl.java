package com.welfare.welfare.infrastructure;

import com.welfare.welfare.domain.WelfareService;
import com.welfare.welfare.infrastructure.projection.WelfareListProjection;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

@RequiredArgsConstructor
public class WelfareServiceRepositoryImpl implements WelfareServiceRepositoryCustom {

    private final EntityManager entityManager;

    @Override
    public Page<WelfareListProjection> findList(Specification<WelfareService> specification, Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<WelfareListProjection> query = cb.createQuery(WelfareListProjection.class);
        Root<WelfareService> root = query.from(WelfareService.class);
        query.select(cb.construct(
                WelfareListProjection.class,
                root.get("id"),
                root.get("servNm"),
                root.get("servDgst"),
                root.get("onapPsbltYn"),
                root.get("ctpvNm"),
                root.get("sggNm"),
                root.get("wlfareInfoOutlCn")
        ));

        Predicate predicate = toPredicate(specification, root, query, cb);
        if (predicate != null) {
            query.where(predicate);
        }
        Sort sort = pageable.getSort();
        if (sort.isSorted()) {
            query.orderBy(toOrders(sort, root, cb));
        }

        TypedQuery<WelfareListProjection> typedQuery = entityManager.createQuery(query);
        if (pageable.isPaged()) {
            typedQuery.setFirstResult(Math.toIntExact(pageable.getOffset()));
            typedQuery.setMaxResults(pageable.getPageSize());
        }
        List<WelfareListProjection> content = typedQuery.getResultList();
        return new PageImpl<>(content, pageable, count(specification));
    }

    private long count(Specification<WelfareService> specification) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<WelfareService> root = query.from(WelfareService.class);
        query.select(cb.count(root.get("id")));
        Predicate predicate = toPredicate(specification, root, query, cb);
        if (predicate != null) {
            query.where(predicate);
        }
        return entityManager.createQuery(query).getSingleResult();
    }

    private Predicate toPredicate(
            Specification<WelfareService> specification,
            Root<WelfareService> root,
            CriteriaQuery<?> query,
            CriteriaBuilder cb
    ) {
        if (specification == null) {
            return null;
        }
        return specification.toPredicate(root, query, cb);
    }

    private List<Order> toOrders(Sort sort, Root<WelfareService> root, CriteriaBuilder cb) {
        List<Order> orders = new ArrayList<>();
        for (Sort.Order order : sort) {
            if (order.isAscending()) {
                orders.add(cb.asc(root.get(order.getProperty())));
            } else {
                orders.add(cb.desc(root.get(order.getProperty())));
            }
        }
        return orders;
    }
}
