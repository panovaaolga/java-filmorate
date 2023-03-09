package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ValidateException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;

@Component
@Slf4j
public class InMemoryFilmStorage extends AbstractInMemoryStorage<Film> implements FilmStorage {

    @Override
    public void validate(Film film) throws ValidateException {
        if (film.getName().isBlank()) {
            log.info("ValidateException: {}", "Название не может быть пустым");
            throw new ValidateException("Название не может быть пустым");
        }
        if (film.getDescription().isBlank() || film.getDescription().length() > 200) {
            log.info("ValidateException: {}", "Максимальная длина описания — 200 символов");
            throw new ValidateException("Максимальная длина описания — 200 символов");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.info("ValidateException: {}", "Дата релиза должна быть не раньше 28.12.1895");
            throw new ValidateException("Дата релиза должна быть не раньше 28.12.1895");
        }
        if (film.getDuration() <= 0) {
            log.info("ValidateException: {}", "Продолжительность должна быть положительной");
            throw new ValidateException("Продолжительность должна быть положительной");
        }
    }

    @Override
    public void addLike(long filmId, long userId) {

    }

    @Override
    public void deleteLike(long filmId, long userId) {

    }

    @Override
    public int getLikes(long filmId) {
        return 0;
    }

    @Override
    public Collection<Film> getPopular(int count) {
        return null;
    }
}
