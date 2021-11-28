package com.cs.ganda.document;

import com.cs.ganda.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

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
    private String price;
    private int vues;
    private Status status;
    private Instant creation;

    @DBRef
    private Address address;
    @DBRef
    private Profile profile;
    @DBRef
    private Validity validity;

    @DBRef
    private List<Picture> pictures;

}
