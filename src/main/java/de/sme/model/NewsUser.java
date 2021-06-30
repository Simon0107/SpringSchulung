package de.sme.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewsUser {
    @Size(min = 4, message = "Muss mind. {min} Zeichen lang sein.")
    private String username;
    @Size(min = 4, message = "Muss mind. {min} Zeichen lang sein.")
    private String password;
    @NotBlank
    private String firstname;
    @NotBlank
    private String lastname;
    @Past
    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;


}

