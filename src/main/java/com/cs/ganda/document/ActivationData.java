package com.cs.ganda.document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ActivationData {
    @NotBlank
    private String itemId;
    @NotBlank
    private String token;
    @NotBlank
    private String phone;
    private String phoneIndex = "+33";
}
