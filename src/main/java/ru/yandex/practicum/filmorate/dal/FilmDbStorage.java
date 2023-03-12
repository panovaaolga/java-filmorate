package ru.yandex.practicum.filmorate.dal;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ItemNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidateException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Slf4j
@Component
@Primary
public class FilmDbStorage implements FilmStorage {
    @Getter
    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void create(Film film) throws ValidateException {
        validate(film);
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("film_id");
        film.setId(simpleJdbcInsert.executeAndReturnKey(film.toMap()).longValue());
        if (film.getMpa() != null) {
            String sqlRating = "select rating_name from ratings where rating_id = ?";
            String ratingName = jdbcTemplate.queryForObject(sqlRating, String.class, film.getMpa().getId());
            film.getMpa().setName(ratingName);
        }
        if (film.getGenres() != null) {
            for (Genre g : film.getGenres()) {
                String sqlFilmGenre = "insert into genres_films (film_id, genre_id) " +
                        "values (?, ?)";
                jdbcTemplate.update(sqlFilmGenre, film.getId(), g.getId());

                String sqlGenres = "select genre_name from genres as g join genres_films as gf on g.genre_id = gf.genre_id " +
                        "where gf.film_id = ? and gf.genre_id = ?";
                String genreName = jdbcTemplate.queryForObject(sqlGenres, String.class, film.getId(), g.getId());
                g.setName(genreName);
            }
        }
    }

    @Override
    public void update(Film film) throws ValidateException {
        validate(film);
        String sql = "update films set title = ?, description = ?, duration = ?, release_date = ?, rating_id = ? " +
                "where film_id = ?";
        int result = jdbcTemplate.update(sql, film.getName(), film.getDescription(), film.getDuration(), film.getReleaseDate(),
                film.getMpa().getId(), film.getId());
        if (result == 0) {
            log.info("Фильм с id {} не найден", film.getId());
            throw new ItemNotFoundException("Фильм с таким id не найден");
        }
        if (film.getMpa() != null) {
            String sqlRating = "select rating_name from ratings where rating_id = ?";
            String ratingName = jdbcTemplate.queryForObject(sqlRating, String.class, film.getMpa().getId());
            film.getMpa().setName(ratingName);
        }

        String sqlDeleteGenres = "delete from genres_films where film_id = ?";
        jdbcTemplate.update(sqlDeleteGenres, film.getId());
        if (film.getGenres() != null) {
            for (Genre g : film.getGenres()) {
                String sqlFilmGenre = "merge into genres_films (film_id, genre_id) " +
                        "values (?, ?)";
                jdbcTemplate.update(sqlFilmGenre, film.getId(), g.getId());
            }
            String sqlGenres = "select g.genre_id, g.genre_name from genres as g join genres_films as gf " +
                    "on g.genre_id = gf.genre_id where gf.film_id = ?";
            film.setGenres(jdbcTemplate.query(sqlGenres, (rs, rowNum) -> makeGenre(rs), film.getId()));
        }
    }

    @Override
    public void delete(long id) {
        String sql = "delete from films where film_id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public Film get(long id) {
        try {
        String sql = "select * from films as f left join ratings as r on f.rating_id=r.rating_id where f.film_id = ?";
         return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> makeFilm(rs), id);
        } catch (Exception e) {
            log.info("Пользователь с id {} не найден", id);
            throw new ItemNotFoundException("Пользователь с таким id не найден");
        }
    }

    private List<Genre> getGenresByFilmId(long filmId) {
        String sql = "select * from genres as g join genres_films as gf on gf.genre_id=g.genre_id where gf.film_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeGenre(rs), filmId);
    }

    private Genre makeGenre(ResultSet rs) throws SQLException {
        Genre genre = new Genre();
        genre.setId(rs.getInt("genre_id"));
        genre.setName(rs.getString("genre_name"));
        return genre;
    }

    @Override
    public Collection<Film> getAll() {
        String sql = "select * from films as f left join ratings as r on f.rating_id=r.rating_id";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs));
    }

    private Film makeFilm(ResultSet rs) throws SQLException {
        Film film = new Film();
        MpaRating rating = new MpaRating(rs.getInt("rating_id"), rs.getString("rating_name"));
        film.setId(rs.getLong("film_id"));
        film.setName(rs.getString("title"));
        film.setDescription(rs.getString("description"));
        film.setDuration(rs.getInt("duration"));
        film.setReleaseDate(rs.getDate("release_date").toLocalDate());
        film.setMpa(rating);
        film.setGenres(getGenresByFilmId(film.getId()));
        film.setLikesCount(getLikes(film.getId()));
        return film;
    }

    @Override
    public void addLike(long filmId, long userId) {
        try {
            String sql = "insert into films_likes (film_id, user_id) values (?, ?)";
            jdbcTemplate.update(sql, filmId, userId);
            log.info("Лайк от пользователя {} успешно добавлен", userId);
        } catch (Exception e) {
            log.info("Лайк не был добавлен");
            throw new IllegalArgumentException("Невозможно добавить лайк");
        }
    }

    @Override
    public void deleteLike(long filmId, long userId) {
        String sql = "delete from films_likes where film_id = ? and user_id = ?";
        int result = jdbcTemplate.update(sql, filmId, userId);
        if (result == 0) {
            log.info("Невозможно удалить лайк");
            throw new ItemNotFoundException("Невозможно удалить лайк");
        }
        log.info("Лайк от пользователя {} успешно удален", userId);
    }

    @Override
    public int getLikes(long filmId) {
        String sql = "SELECT COUNT(film_id) as likes_amount\n" +
                "FROM films_likes \n" +
                "WHERE film_id = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, filmId);
    }

    @Override
    public Collection<Film> getPopular(int count) {
        String sql = "SELECT f.film_id, f.title, f.description, f.duration, f.release_date, f.rating_id, r.rating_name, " +
                "COUNT(fl.user_id) FROM films AS f LEFT JOIN films_likes AS fl ON f.film_id=fl.film_id " +
                "LEFT JOIN ratings AS r ON f.rating_id=r.rating_id GROUP BY f.film_id " +
                "ORDER BY COUNT(fl.user_id) DESC, f.title LIMIT ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs), count);
    }

    @Override
    public Collection<Genre> getGenres() {
        String sql = "select * from genres";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeGenre(rs));
    }

    @Override
    public Genre getGenre(int genreId) {
        try {
            String sql = "select * from genres where genre_id = ?";
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> makeGenre(rs), genreId);
        } catch (Exception e) {
            log.info("Жанр с id {} не найден", genreId);
            throw  new ItemNotFoundException("Жанр с таким id не найден");
        }
    }

    @Override
    public Collection<MpaRating> getRatings() {
        String sql = "select * from ratings";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new MpaRating(rs.getInt("rating_id"),
                rs.getString("rating_name")));
    }

    @Override
    public MpaRating getMpa(int mpaId) {
        try {
            String sql = "select * from ratings where rating_id = ?";
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> new MpaRating(rs.getInt("rating_id"),
                    rs.getString("rating_name")), mpaId);
        } catch (Exception e) {
            log.info("Рейтинг с id {} не найден", mpaId);
            throw new ItemNotFoundException("Рейтинг с таким id не найден");
        }
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
