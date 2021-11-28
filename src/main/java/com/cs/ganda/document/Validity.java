package com.cs.ganda.document;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Document(value = "VALIDITY")
public class Validity {
    @Id
    private String id;
    @JsonFormat(pattern="yyyy-MM-dd")
    private Date date;
    private String start;
    private String end;
}
