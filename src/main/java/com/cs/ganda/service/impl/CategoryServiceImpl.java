package com.cs.ganda.service.impl;

import com.cs.ganda.document.ActivationData;
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

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        super(categoryRepository);
        this.categoryRepository = categoryRepository;
    }


    @Override
    public List<Category> getAll() {
        return null;
    }

    @Override
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    @Override
    public void signin(AuthenticationRequest authenticationRequest, HttpServletResponse response) {

    }
}
