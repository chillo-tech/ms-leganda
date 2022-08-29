package com.cs.ganda.service.impl;

import com.cs.ganda.document.Category;
import com.cs.ganda.dto.AuthenticationRequest;
import com.cs.ganda.repository.CategoryRepository;
import com.cs.ganda.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Slf4j
@Service
public class CategoryServiceImpl extends CRUDServiceImpl<Category, String> implements CategoryService {
    CategoryRepository categoryRepository;

    public CategoryServiceImpl(final CategoryRepository categoryRepository) {
        super(categoryRepository);
        this.categoryRepository = categoryRepository;
    }


    @Override
    public List<Category> getAll() {
        return null;
    }

    @Override
    public List<Category> findAll() {
        log.info("Liste des catégories");
        return this.categoryRepository.findAll();
    }

    @Override
    public void signin(final AuthenticationRequest authenticationRequest, final HttpServletResponse response) {

    }
}
