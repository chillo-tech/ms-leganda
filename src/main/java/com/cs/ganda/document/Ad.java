package com.cs.ganda.document;

import com.cs.ganda.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.TextScore;

import java.time.Instant;
import java.util.List;

import static java.lang.Boolean.FALSE;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Document(value = "AD")
public class Ad {
    @Id
    private String id;
    @TextIndexed(weight = 2f)
    private String name;
    @TextIndexed(weight = 5f)
    private String description;
    @TextScore
    private Float score;
    private String image;
    private double price;
    private int views;
    private Status status;
    private Instant creation;
    private Boolean active = FALSE;

    private Category category;
    private Address address;
    private Profile profile;
    private Validity validity;
    private List<Picture> pictures;

}
