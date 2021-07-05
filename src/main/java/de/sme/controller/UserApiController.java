package de.sme.controller;

import com.fasterxml.jackson.annotation.JsonView;
import de.sme.dto.NewsUserUpdateDTO;
import de.sme.entity.NewsUser;
import de.sme.model.ReadUser;
import de.sme.model.WriteUser;
import de.sme.repo.NewsUserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.fromMethodCall;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

@RestController
@RequestMapping("/api/user")
public class UserApiController {
    NewsUserRepository newsUserRepository;
    PasswordEncoder passwordEncoder;

    public UserApiController(NewsUserRepository newsUserRepository, PasswordEncoder passwordEncoder) {
        this.newsUserRepository = newsUserRepository;
        this.passwordEncoder = passwordEncoder;
    }
    @GetMapping(params = {"page", "size", "sort"})
    @JsonView(ReadUser.class)
    public List<NewsUser> findAllPaged(Pageable pageable) {
        return newsUserRepository.findAll(pageable).getContent();
    }

    @GetMapping("/{id}")
    @JsonView(ReadUser.class)
    public ResponseEntity<NewsUser> findById(@PathVariable("id") String id) {
        return newsUserRepository.findById(id)
                .map(u -> ResponseEntity.ok(u))
                .orElse(ResponseEntity.noContent().build());
    }


    @PostMapping
    public @JsonView(ReadUser.class)
    ResponseEntity<NewsUser> insert(@RequestBody @JsonView(WriteUser.class) @Valid NewsUser user) {
        if (user.getVersion() != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        var hash = passwordEncoder.encode(user.getPassword());
        user.setPassword(hash);
        try {
            user = newsUserRepository.save(user);
        } catch (DataIntegrityViolationException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        var uri =
                fromMethodCall(on(UserApiController.class)
                        .findById(user.getUsername())).build();
        return ResponseEntity.created(uri.toUri()).body(user);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }

    @PutMapping("/{id}")
    public @JsonView(ReadUser.class)
    ResponseEntity<NewsUser> update(
            @PathVariable("id") String id,
            @RequestBody @JsonView(WriteUser.class) @Valid NewsUserUpdateDTO updateDTO) {
        if (!updateDTO.getUsername().equals(id)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return newsUserRepository.findById(updateDTO.getUsername())
                .map(u -> {
                    if (!passwordEncoder.matches(updateDTO.getOldPassword(), u.getPassword())) {
                        return ResponseEntity.badRequest().<NewsUser>build();
                    }
                    u.setFirstname(updateDTO.getFirstname());
                    u.setLastname(updateDTO.getLastname());
                    u.setVersion(updateDTO.getVersion());
                    u.setBirthday(updateDTO.getBirthday());
                    u.setPassword(passwordEncoder.encode(updateDTO.getPassword()));
                    try {
                        return ResponseEntity.ok(newsUserRepository.saveAndFlush(u));
                    } catch (ObjectOptimisticLockingFailureException |
                    DataIntegrityViolationException ex) {
                        return ResponseEntity.status(HttpStatus.CONFLICT).<NewsUser>build();
                    }
                }).orElse(ResponseEntity.notFound().build());
    }




}

