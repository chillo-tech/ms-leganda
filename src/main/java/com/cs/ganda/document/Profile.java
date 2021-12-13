package com.cs.ganda.document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Document(value = "PROFILE")
public class Profile {
    @Id
    private String id;
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    private String email;
    @Indexed(unique = true)
    @NotBlank
    private String phone;
    private String phoneIndex = "+33";
    private boolean active;

}
