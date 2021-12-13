package com.cs.ganda.service.impl;

import com.cs.ganda.service.CRUDService;
import com.google.common.base.Throwables;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Objects;

import static com.cs.ganda.datas.Constant.MISSING_FIELD;


@Slf4j
public abstract class CRUDServiceImpl<T, ID> implements CRUDService<T, ID> {
    private final MongoRepository<T, ID> repo;

    public CRUDServiceImpl(MongoRepository<T, ID> repo) {
        this.repo = repo;
    }


    @Override
    public T create(T t) {
        this.repo.save(t);
        return null;
    }

    @Override
    public T read(ID id) {
        return this.repo.findById(id)
                .orElseThrow(() -> new NullPointerException("Aucune entite ne correspond à l'id " + id));
    }

    @Override
    public void delete(ID id) {

        Objects.requireNonNull(id, String.format(MISSING_FIELD, "Nom"));
        T entity = this.read(id);
        this.repo.delete(entity);
    }

    @Override
    public void update(T t, ID id) {
        log.debug("modifier() of id#{} with body {}", id, t);
        log.debug("T json is of type {}", t.getClass());

        T entity = this.read(id);
        try {
            BeanUtils.copyProperties(
                    t,
                    entity,
                    "maj",
                    "creation"
            );
        } catch (Exception e) {
            log.warn("while copying properties", e);
            // @TODO corriger
            throw Throwables.propagate(e);
        }
        log.debug("merged entity: {}", entity);

        T updated = this.repo.save(entity);

        log.debug("updated enitity: {}", updated);
    }

}
