package de.sme.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewsUser {
    private String username;
    private String password;
    private String firstname;
    private String lastname;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;

}

