package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ForbiddenException;
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
        throw new ForbiddenException("Действие недоступно");
    }

    @Override
    public MpaRating update(MpaRating mpaRating) throws ValidateException {
        throw new ForbiddenException("Действие недоступно");
    }

    @Override
    @GetMapping
    public Collection<MpaRating> getAll() {
        log.info("Получение списка рейтингов");
        return filmService.getFilmStorage().getRatings();
    }

    @Override
    @GetMapping("/{id}")
    public MpaRating get(@PathVariable long id) {
        log.info("Получение рейтинга с id {}", id);
        return filmService.getFilmStorage().getMpa((int)id);
    }

    @Override
    public void delete(long id) {
        throw new ForbiddenException("Действие недоступно");
    }
}
