package com.cs.ganda.document;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.Indexed;

import javax.validation.constraints.NotBlank;

@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Address {
    private String id;
    private int num;
    @NotBlank
    @Indexed
    private String street;
    private int zip;
    private String city;
    private String description;
    @NotBlank
    private Location location;
}
