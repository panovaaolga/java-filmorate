package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class Genre {
    @NotNull
    private int id;
    @NotNull
    private String name;
}
