package ru.yandex.practicum.filmorate.dal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ItemNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidateException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@Primary
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    @Override
    public void create(User user) throws ValidateException {
        validate(user);
//        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
//                .withTableName("users")
//                .usingGeneratedKeyColumns("user_id");
        String sql = "insert into users (user_name, email, login, birthday) values (?, ?, ?, ?)";
        jdbcTemplate.update(sql, user.getName(), user.getEmail(), user.getLogin(), user.getBirthday());
//        user.setId(simpleJdbcInsert.executeAndReturnKey(user.toMap()).longValue());
    }

    @Override
    public void update(User user) throws ValidateException {
        validate(user);
        String sql = "update users set user_name = ?, email = ?, login = ?, birthday = ? where user_id = ?";
        int result = jdbcTemplate.update(sql, user.getName(), user.getEmail(), user.getLogin(), user.getBirthday(), user.getId());
        if (result == 0) {
            log.info("Пользователь с id {} не найден", user.getId());
            throw new ItemNotFoundException("Пользователь с таким id не найден");
        }
    }

    @Override
    public void delete(long id) {
        String sql = "delete from users where user_id = ?";
        int result = jdbcTemplate.update(sql, id);
        if (result == 0) {
            log.info("Пользователь с id {} не найден", id);
            throw new ItemNotFoundException("Пользователь с таким id не найден");
        }
    }

    @Override
    public Collection<User> getAll() {
        String sql = "select * from users";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs));
    }

    private User makeUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("user_id"));
        user.setName(rs.getString("user_name"));
        user.setEmail(rs.getString("email"));
        user.setLogin(rs.getString("login"));
        user.setBirthday(rs.getDate("birthday").toLocalDate());
        return user;
    }

    @Override
    public User get(long id) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from users where user_id = ?", id);

        if (userRows.next()) {
            User user = new User();
            user.setId(userRows.getLong("user_id"));
            user.setName(userRows.getString("user_name"));
            user.setEmail(userRows.getString("email"));
            user.setLogin(userRows.getString("login"));
            user.setBirthday(userRows.getDate("birthday").toLocalDate());
            log.info("Найден пользователь с id {}", user.getId());
            return user;
        }
        log.info("Пользователь с id {} не найден", id);
        throw new ItemNotFoundException("Пользователь с таким id не найден");
    }

    @Override
    public void validate(User user) throws ValidateException {
        if (user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.info("ValidateException: {}", "Введен некорректный email");
            throw new ValidateException("Введен некорректный email");
        }
        if (user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.info("ValidateException: {}", "Логин не может быть пустым или содержать пробелы");
            throw new ValidateException("Логин не может быть пустым или содержать пробелы");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("В качестве имени пользователя установлен логин {}", user.getLogin());
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.info("ValidateException: {}", "Дата рождения не может быть в будущем");
            throw new ValidateException("Дата рождения не может быть в будущем");
        }
    }
}
