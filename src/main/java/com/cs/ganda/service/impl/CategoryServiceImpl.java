package com.cs.ganda.service.impl;

import com.cs.ganda.document.Category;
import com.cs.ganda.repository.CategoryRepository;
import com.cs.ganda.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CategoryServiceImpl extends CRUDServiceImpl<Category, String> implements CategoryService {

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        super(categoryRepository);
    }
    
}
