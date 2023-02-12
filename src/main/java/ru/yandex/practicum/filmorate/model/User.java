package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
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
    private LocalDate birthday;
}
