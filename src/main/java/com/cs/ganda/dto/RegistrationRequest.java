package com.cs.ganda.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
public class RegistrationRequest {
    @NotNull
    private String lastname;
    @NotNull
    private String firstname;
    @NotNull
    private String email;
    @NotNull
    private String phone;
}
