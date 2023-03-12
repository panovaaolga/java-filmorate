package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.exceptions.ValidateException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.Collection;

public interface FilmStorage {

    void create(final Film film) throws ValidateException;

    void update(final Film film) throws ValidateException;

    void delete(long id);

    Film get(long id);

    Collection<Film> getAll();

    void validate(Film film) throws ValidateException;

    void addLike(long filmId, long userId);

    void deleteLike(long filmId, long userId);

    int getLikes(long filmId);

    Collection<Film> getPopular(int count);

    Collection<Genre> getGenres();

    Genre getGenre(int genreId);

    Collection<MpaRating> getRatings();

    MpaRating getMpa(int mpaId);
}
