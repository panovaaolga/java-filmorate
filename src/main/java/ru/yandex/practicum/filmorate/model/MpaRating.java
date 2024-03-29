package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class MpaRating {
    @NotNull
    private int id;
    @NotNull
    private String name;

    public MpaRating(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
