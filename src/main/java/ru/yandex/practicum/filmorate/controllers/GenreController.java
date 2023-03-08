package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
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
        return null;
    }

    @Override
    public Genre update(Genre genre) throws ValidateException {
        return null;
    }

    @Override
    @GetMapping
    public Collection<Genre> getAll() {
        return null;
    }

    @Override
    @GetMapping("/{id}")
    public Genre get(@PathVariable long id) {
        return null;
    }

    @Override
    public void delete(long id) {

    }
}
