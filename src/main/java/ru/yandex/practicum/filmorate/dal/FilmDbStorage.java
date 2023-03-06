package ru.yandex.practicum.filmorate.dal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ValidateException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@Primary
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void create(Film film) throws ValidateException {
        validate(film);

    }

    @Override
    public void update(Film film) throws ValidateException {
        validate(film);
        String sql = "update films set title = ?, description = ?, duration = ?, release_date = ?, rating_id = ? " +
                "where film_id = ?";
        jdbcTemplate.update(sql, film.getName(), film.getDescription(), film.getDuration(), film.getReleaseDate(),
                film.getRating().getId(), film.getId());
    }

    @Override
    public void delete(long id) {
        String sql = "delete from films where film_id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public Film get(long id) {
        return null;
    }

    @Override
    public Collection<Film> getAll() {
        String sql = "select * from films as f join ratings as r on f.rating_id=r.rating_id join genres as g on " +
                "f.genre_id=g.genre_id";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs));
    }

    private Film makeFilm(ResultSet rs) throws SQLException {
        Film film = new Film();
        MpaRating rating = new MpaRating();
        List<Genre> genres = new ArrayList<>();
        Genre genre = new Genre();
        film.setId(rs.getLong("film_id"));
        film.setName(rs.getString("title"));
        film.setDescription(rs.getString("description"));
        film.setDuration(rs.getInt("duration"));
        film.setReleaseDate(rs.getDate("release_date").toLocalDate());
        rating.setId(rs.getInt("raring_id"));
        rating.setName(rs.getString("rating_name"));
        film.setRating(rating);
        genre.setId(rs.getInt("genre_id"));
        genre.setName(rs.getString("genre_name"));
        film.setGenres();
        return film;
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
