package com.cs.ganda.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MapBoxQueryParams {
    private String maboxTypes = "address";
    private String proxymity;
}
