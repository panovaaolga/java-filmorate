package ru.yandex.practicum.filmorate.controllers;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.ValidateException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FilmControllerTest {
    static FilmController filmController = new FilmController(new FilmService(new InMemoryFilmStorage(),
            new InMemoryUserStorage()));

    @Test
    public void shouldAddNewFilmWithCorrectData() throws ValidateException {
        final Film film = new Film();
        film.setName("Correct Name");
        film.setDescription("Correct Description");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(120);
        filmController.create(film);
    }

    @Test
    public void shouldThrowExcWhenNameIsBlank() throws ValidateException {
        final Film film = new Film();
        film.setName(" ");
        film.setDescription("Correct Description");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(120);
        Exception exception = assertThrows(ValidateException.class, () -> filmController.create(film));
        assertEquals("Название не может быть пустым", exception.getMessage());
    }

    @Test
    public void shouldThrowExcWhenDescriptionIsLong() throws ValidateException {
        final Film film = new Film();
        film.setName("Correct Name");
        film.setDescription("It could be a correct description, though I prefer make it longer to see what is gonna happen. " +
                "So, maybe next time it will fit.............................................................................");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(120);
        Exception exception = assertThrows(ValidateException.class, () -> filmController.create(film));
        assertEquals("Максимальная длина описания — 200 символов", exception.getMessage());
    }

    @Test
    public void shouldThrowExcWhenDateIsEarly() throws ValidateException {
        final Film film = new Film();
        film.setName("Correct Name");
        film.setDescription("Correct Description");
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        film.setDuration(120);
        Exception exception = assertThrows(ValidateException.class, () -> filmController.create(film));
        assertEquals("Дата релиза должна быть не раньше 28.12.1895", exception.getMessage());
    }

    @Test
    public void shouldThrowExcWhenDurationIsNegative() throws ValidateException {
        final Film film = new Film();
        film.setName("Correct Name");
        film.setDescription("Correct Description");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(-3);
        Exception exception = assertThrows(ValidateException.class, () -> filmController.create(film));
        assertEquals("Продолжительность должна быть положительной", exception.getMessage());
    }

}
