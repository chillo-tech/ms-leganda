package com.cs.ganda.document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Document(collection = "CATEGORY")
public class Category  {
    @Id
    private String id;

    @TextIndexed(weight = 2f)
    private String name;

    @TextIndexed(weight = 5f)
    private String description;

    private String icon;
}
