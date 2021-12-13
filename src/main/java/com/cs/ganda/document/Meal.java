package com.cs.ganda.document;

import com.cs.ganda.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

import static java.lang.Boolean.FALSE;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Document(value = "MEAL")
public class Meal {
    @Id
    private String id;
    private String name;
    private String description;
    private String image;
    private double price;
    private int views;
    private Status status;
    private Instant creation;
    private Boolean active = FALSE;

    private Address address;
    private Profile profile;
    private Validity validity;
    private List<Picture> pictures;

}
