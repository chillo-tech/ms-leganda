package com.cs.ganda.document;

import lombok.*;

import javax.validation.constraints.NotBlank;

@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Location {
    private String type;

    @NotBlank
    private double[] coordinates;
}
