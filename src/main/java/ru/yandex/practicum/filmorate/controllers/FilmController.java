package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidateException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController extends AbstractController<Film> {
    private Map<Integer, Film> films = new HashMap<>();

    @Override
    @PostMapping
    public Film create(@RequestBody @Valid final Film film) throws ValidateException {
        validate(film);
        increaseCount();
        film.setId(getCount());
        films.put(film.getId(), film);
        log.info("Добавление фильма {}", film);
        return film;
    }

    @Override
    @PutMapping
    public Film update(@RequestBody @Valid final Film film) throws ValidateException {
        validate(film);
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            log.info("Обновление данных о фильме {}", film);
            return film;
        } else {
            log.info("ValidateException: {}", "Фильма с таким id пока нет в нашей коллекции");
            throw new ValidateException("Фильма с таким id пока нет в нашей коллекции");
        }
    }

    @Override
    @GetMapping
    public List<Film> get() {
        return new ArrayList<>(films.values());
    }

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
}
