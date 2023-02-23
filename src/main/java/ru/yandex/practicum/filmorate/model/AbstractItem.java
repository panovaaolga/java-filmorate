package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public abstract class AbstractItem {
    @NotNull
    private long id;
}
