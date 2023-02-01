package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class User {
    @NotNull
    private int id;
    @Email
    private String email;
    @NotBlank
    private String login;
    private String name;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate birthday;
}
