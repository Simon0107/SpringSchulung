package de.sme.dto;

import com.fasterxml.jackson.annotation.JsonView;
import de.sme.entity.NewsUser;
import de.sme.model.WriteUser;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class NewsUserUpdateDTO extends NewsUser {
    @JsonView(WriteUser.class)
    @NotBlank
    private String oldPassword;
    public NewsUserUpdateDTO(
            String username,
            String password,
            String firstname,
            String lastname,
            LocalDate birthday,
            Integer version,
            String oldPassword) {
        super(username, password, firstname, lastname, birthday);
        setVersion(version);
        setOldPassword(oldPassword);
    }
}
