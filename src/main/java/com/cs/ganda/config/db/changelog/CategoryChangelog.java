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

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.file.Files;
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

        Reader reader = Files.newBufferedReader(resource.getFile().toPath());
        Type listType = new TypeToken<ArrayList<Category>>() {
        }.getType();
        List<Category> categoryList = new Gson().fromJson(reader, listType);

        categoryList.stream().forEach(category -> mongoTemplate.save(category));
    }

    @RollbackExecution
    public void rollback() {
    }
}
