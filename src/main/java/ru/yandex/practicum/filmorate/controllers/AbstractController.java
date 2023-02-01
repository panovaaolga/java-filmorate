package ru.yandex.practicum.filmorate.controllers;

import lombok.Getter;
import lombok.Setter;
import ru.yandex.practicum.filmorate.exceptions.ValidateException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractController<T> {
    private Map<Integer, T> itemsMap = new HashMap<>();

    @Getter
    @Setter
    private int count = 0;

    public abstract T create(final T t) throws ValidateException;

    public abstract T update(final T t) throws ValidateException;

    public abstract List<T> get();

    public abstract void validate(T t) throws ValidateException;

    protected void increaseCount() {
     count++;
    }


}
