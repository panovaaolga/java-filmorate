package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidateException;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

@RestController
@RequestMapping("/mpa")
@Slf4j
public class MpaController extends AbstractController<MpaRating>{
    private final FilmService filmService;

    public MpaController(FilmService filmService) {
        this.filmService = filmService;
    }

    @Override
    public MpaRating create(MpaRating mpaRating) throws ValidateException {
        return null;
    }

    @Override
    public MpaRating update(MpaRating mpaRating) throws ValidateException {
        return null;
    }

    @Override
    @GetMapping
    public Collection<MpaRating> getAll() {
        return null;
    }

    @Override
    @GetMapping("/{id}")
    public MpaRating get(@PathVariable long id) {
        return null;
    }

    @Override
    public void delete(long id) {

    }
}
