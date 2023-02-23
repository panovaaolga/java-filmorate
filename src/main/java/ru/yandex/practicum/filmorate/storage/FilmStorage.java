package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.exceptions.ValidateException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {

    void create(final Film film) throws ValidateException;

    void update(final Film film) throws ValidateException;

    void delete(long id);

    Film get(long id);

    Collection<Film> getAll();

    void validate(Film film) throws ValidateException;

}
