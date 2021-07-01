package de.sme.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"version"})
// An entity must be equal to itself across all JPA object states
@Entity
@Table(name = "NEWS_USER")
public class NewsUser {
    @Id
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
    @Version
    private Integer version;
    public NewsUser(String username, String password, String firstname, String lastname, LocalDate
            birthday) {
        this.username = username;
        this.password = password;
        this.firstname = firstname;
        this.lastname = lastname;
        this.birthday = birthday;
    }
}
