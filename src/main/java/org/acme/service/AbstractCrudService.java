package org.acme.service;

import io.quarkus.hibernate.panache.PanacheRepository;
import io.quarkus.panache.common.Sort;
import jakarta.persistence.OptimisticLockException;
import jakarta.transaction.Transactional;
import java.util.UUID;
import org.acme.entity.CoreEntity;

import jakarta.persistence.OptimisticLockException;
import jakarta.transaction.Transactional;
import java.util.UUID;
import java.util.function.Supplier;

import org.acme.util.PageResult;
import org.acme.util.Paging;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import org.hibernate.query.SelectionQuery;

public abstract class AbstractCrudService<E extends CoreEntity> {

}
