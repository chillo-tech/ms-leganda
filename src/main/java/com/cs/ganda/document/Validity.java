package com.cs.ganda.document;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Validity {
    @Indexed
    private Instant date;
    @Indexed
    private Instant start;
    private Instant end;
}
