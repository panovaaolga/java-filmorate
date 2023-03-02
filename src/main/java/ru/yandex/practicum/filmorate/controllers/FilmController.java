package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ItemNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidateException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController extends AbstractController<Film> {
    private final FilmService filmService;
    private final static int DEFAULT_TOP_FILMS = 10;

    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @Override
    @PostMapping
    public Film create(@RequestBody @Valid final Film film) throws ValidateException {
        log.info("Добавление фильма {}", film);
        filmService.getFilmStorage().create(film);
        return film;
    }

    @Override
    @PutMapping
    public Film update(@RequestBody @Valid final Film film) throws ValidateException {
        log.info("Обновление данных о фильме {}", film);
        filmService.getFilmStorage().update(film);
        return film;
    }

    @Override
    @GetMapping
    public Collection<Film> getAll() {
        log.info("Получение списка всех фильмов");
        return filmService.getFilmStorage().getAll();
    }

    @Override
    @GetMapping("/{id}")
    public Film get(@PathVariable long id) {
        log.info("Получение фильма с id {}", id);
        return filmService.getFilmStorage().get(id);
    }

    @Override
    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        log.info("Удаление фильма с id {}", id);
        filmService.getFilmStorage().delete(id);
    }

    @PutMapping("{id}/like/{userId}")
    public void addLike(@PathVariable("id") long id, @PathVariable("userId") long userId) throws ItemNotFoundException {
        log.info("Добавление лайка фильму с id {} от пользователя с id {}", id, userId);
        filmService.addLike(id, userId);
    }

    @DeleteMapping("{id}/like/{userId}")
    public void deleteLike(@PathVariable("id") long id, @PathVariable("userId") long userId) {
        log.info("Удаление лайка пользователя с id {} с фильма с id {}", userId, id);
        filmService.deleteLike(id, userId);
    }

    @GetMapping("{id}/likes")
    public long getLikes(@PathVariable long id) {
        log.info("Отображение количества лайков у фильма с id {}", id);
        return filmService.getLikesAmount(id);
    }

    @GetMapping("/popular")
    public List<Film> getPopular(@RequestParam Optional<Integer> count) {
        log.info("Получение списка наиболее популярных фильмов");
        if (count.isPresent()) {
            return filmService.getTopFilms(count.get());
        }
        return filmService.getTopFilms(DEFAULT_TOP_FILMS);
    }

}
