package com.cs.ganda.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Document(value = "PICTURE")
public class Picture {

    @Id
    private String id;
    private String name;
    private String base64;
    private int width;
    private int height;

}
