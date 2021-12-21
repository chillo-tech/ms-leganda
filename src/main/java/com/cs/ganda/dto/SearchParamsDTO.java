package com.cs.ganda.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SearchParamsDTO {
    private Instant date;
    private String query;
    private double[] coordinates;
}
