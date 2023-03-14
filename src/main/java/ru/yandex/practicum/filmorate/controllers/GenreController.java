package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ForbiddenException;
import ru.yandex.practicum.filmorate.exceptions.ValidateException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

@RestController
@RequestMapping("/genres")
@Slf4j
public class GenreController extends AbstractController<Genre> {
    private final FilmService filmService;

    public GenreController(FilmService filmService) {
        this.filmService = filmService;
    }

    @Override
    public Genre create(Genre genre) throws ValidateException {
        throw new ForbiddenException("Действие недоступно");
    }

    @Override
    public Genre update(Genre genre) throws ValidateException {
        throw new ForbiddenException("Действие недоступно");
    }

    @Override
    @GetMapping
    public Collection<Genre> getAll() {
        log.info("Получение списка всех жанров");
        return filmService.getFilmStorage().getGenres();
    }

    @Override
    @GetMapping("/{id}")
    public Genre get(@PathVariable long id) {
        log.info("Получение жанра с id {}", id);
        return filmService.getFilmStorage().getGenre((int)id);
    }

    @Override
    public void delete(long id) {
        throw new ForbiddenException("Действие недоступно");
    }
}
