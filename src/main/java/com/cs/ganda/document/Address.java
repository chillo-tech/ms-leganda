package com.cs.ganda.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;

@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Document(value = "ADDRESS")
public class Address {
    @Id
    private String id;
    private int num;
    @NotBlank
    private String street;
    private int zip;
    private String city;
    private String description;
    @NotBlank
    private Location coordinates;
}
