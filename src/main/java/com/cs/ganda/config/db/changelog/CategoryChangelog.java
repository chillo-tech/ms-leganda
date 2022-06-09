package com.cs.ganda.config.db.changelog;

import com.cs.ganda.document.Category;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackBeforeExecution;
import io.mongock.api.annotations.RollbackExecution;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@ChangeUnit(id = "category-initializer", order = "2", author = "achille", runAlways = true)
public class CategoryChangelog {

    private final MongoTemplate mongoTemplate;

    @Value("classpath:application.yml")
    Resource resourceFile;

    public CategoryChangelog(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }


    //Note this method / annotation is Optional
    @RollbackBeforeExecution
    public void rollbackBefore() {
        mongoTemplate.dropCollection("CATEGORY");
    }

    @Execution
    public void changeSet() throws IOException {
        Resource resource = new ClassPathResource("data/categories.json");

        Type listType = new TypeToken<ArrayList<Category>>() {
        }.getType();
        byte[] bdata = FileCopyUtils.copyToByteArray(resource.getInputStream());
        String data = new String(bdata, StandardCharsets.UTF_8);
        List<Category> categoryList = new Gson().fromJson(data, listType);

        categoryList.stream().forEach(category -> mongoTemplate.save(category));
    }

    @RollbackExecution
    public void rollback() {
    }
}
