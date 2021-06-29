package de.sme.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewsUser {
    private String username;
    private String password;
    private String firstname;
    private String lastname;
    private LocalDate birthday;
}

