package ru.yandex.practicum.filmorate.service;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ItemNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {
    @Getter
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public void addLike(long filmId, long userId) throws ItemNotFoundException {
        if (filmStorage.get(filmId) != null && userStorage.get(userId) != null) {
            filmStorage.get(filmId).addLike(userId);
        } else {
            log.info("ItemNotFoundException: {}", "Фильм или юзер с такими id не найдены");
            throw new ItemNotFoundException("Фильм или юзер с такими id не найдены");
        }
    }

    public void deleteLike(long filmId, long userId) {
        if (filmStorage.get(filmId) != null && filmStorage.get(filmId).getLikedUsersIds().contains(userId)) {
            filmStorage.get(filmId).deleteLike(userId);
        } else {
            log.info("ItemNotFoundException: {}", "Фильм или лайк от юзера с такими id не найдены");
            throw new ItemNotFoundException("Фильм или лайк от юзера с такими id не найдены");
        }
    }

    public long getLikesAmount(long filmId) {
        if (filmStorage.get(filmId) != null) {
            return filmStorage.get(filmId).getLikes();
        } else {
            log.info("ItemNotFoundException: {}", "Фильм с такими id не найден");
            throw new ItemNotFoundException("Фильм с такими id не найден");
        }
    }

    public List<Film> getTopFilms(int count) {
        return filmStorage.getAll().stream()
                .sorted(Comparator.comparingLong(Film::getLikesCount).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

}
