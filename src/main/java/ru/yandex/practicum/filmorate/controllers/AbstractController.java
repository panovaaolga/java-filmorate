package ru.yandex.practicum.filmorate.controllers;

import ru.yandex.practicum.filmorate.exceptions.ValidateException;

import java.util.Collection;

public abstract class AbstractController<T> {

    public abstract T create(final T t) throws ValidateException;

    public abstract T update(final T t) throws ValidateException;

    public abstract Collection<T> getAll();

    public abstract T get(long id);

    public abstract void delete(long id);



}
