package com.cs.ganda.document;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.net.URI;

@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Picture {
    private String name;
    // @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) était la  raison pour laquelle les images ne s'affichait pas
    @JsonProperty(access = JsonProperty.Access.READ_WRITE)
    private String base64;
    private URI uri;
    private int width;
    private int height;

}
