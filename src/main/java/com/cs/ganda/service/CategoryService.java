package com.cs.ganda.service;

import com.cs.ganda.document.ActivationData;
import com.cs.ganda.document.Category;
import com.cs.ganda.dto.AuthenticationRequest;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface CategoryService extends CRUDService<Category, String> {

    List<Category> getAll();

    List<Category> findAll();

    void signin(AuthenticationRequest authenticationRequest, HttpServletResponse response);
}

