package ru.yandex.practicum.filmorate.dal;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ItemNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidateException;
import ru.yandex.practicum.filmorate.model.Status;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collection;

@Slf4j
@Component
@Primary
public class UserDbStorage implements UserStorage {
    @Getter
    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    @Override
    public void create(User user) throws ValidateException {
        validate(user);
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("user_id");
        user.setId(simpleJdbcInsert.executeAndReturnKey(user.toMap()).longValue());
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
    public void addFriend(long userId, long friendId) {
        try {
            String sqlAddFriend = "insert into users_friends (user_id, friend_id, status) " +
                    "values (?, ?, ?)";
            jdbcTemplate.update(sqlAddFriend, userId, friendId, Status.ACCEPTED.toString());
        } catch (Exception e) {
            try {
                String sqlQuery = "select status from users_friends where user_id = ? and friend_id = ?";
                String response = jdbcTemplate.queryForObject(sqlQuery, String.class, userId, friendId);
                if (response.equals("ACCEPTED")) {
                    log.info("Пользователь с id {} уже в друзьях у пользователя с id {}", friendId, userId);
                    throw new IllegalArgumentException("Пользователь уже у вас в друзьях");
                } else if (response.equals("NOT_ACCEPTED")) {
                    String sqlFriendAcceptance = "update users_friends set status = ? where user_id = ? and friend_id = ?";
                    jdbcTemplate.update(sqlFriendAcceptance, Status.ACCEPTED.toString(), userId, friendId);
                }
            } catch (Exception ex) {
                log.info("Пользователи с указанными id не найдены");
                throw new ItemNotFoundException("Пользователи с указанными id не найдены");
            }
        }
        log.info("Пользователь {} успешно добавлен в друзья к пользователю {}", friendId, userId);
        try {
            String sqlSendRequest = "insert into users_friends (user_id, friend_id, status) " +
                    "values (?, ?, ?)";
            jdbcTemplate.update(sqlSendRequest, friendId, userId, Status.NOT_ACCEPTED.toString());
            log.info("Заявка в друзья пользователю {} успешно отправлена", friendId);
        } catch (Exception e) {
            try {
                String sqlQuery = "select status from users_friends where user_id = ? and friend_id = ?";
                String response = jdbcTemplate.queryForObject(sqlQuery, String.class, friendId, userId);
                System.out.println("Response2: " + response);
                if (response.equals("ACCEPTED")) {
                    log.info("Пользователь с id {} уже в друзьях у пользователя с id {}", userId, friendId);
                    throw new IllegalArgumentException("Пользователь уже у вас в друзьях");
                } else if (response.equals("NOT_ACCEPTED")) {
                    log.info("Пользователь {} пока не подтвердил дружбу", friendId);
                }
            } catch (Exception ex) {
                log.info("Пользователи с указанными id не найдены");
                throw new ItemNotFoundException("Пользователи с указанными id не найдены");
            }
        }
    }

    @Override
    public void deleteFriend(long userId, long friendId) {
        try {
            String sqlQuery = "select status from users_friends where user_id = ? and friend_id = ?";
            String status = jdbcTemplate.queryForObject(sqlQuery, String.class, userId, friendId);
            if (status.equals("NOT_ACCEPTED")) {
                log.info("Пользователь {} не найден в списке ваших друзей", friendId);
                throw new ItemNotFoundException("Пользователь не найден в списке ваших друзей");
            }
            String sql = "update users_friends set status = ? where user_id = ? and friend_id = ?";
            jdbcTemplate.update(sql, Status.NOT_ACCEPTED.toString(), userId, friendId);
            log.info("Пользователь {} больше не ваш друг", friendId);
        } catch (Exception e) {
            log.info("Пользователь с id {} не найден", userId);
            throw new ItemNotFoundException("Пользователь не найден");
        }
    }

    @Override
    public Collection<User> getFriends(long userId) {
        try {
            String sql = "SELECT * " +
                    "FROM users \n" +
                    "WHERE user_id IN (\n" +
                    "SELECT friend_id\n" +
                    "FROM USERS_FRIENDS \n" +
                    "WHERE user_id = ? \n" +
                    "AND status = ?)\n" +
                    "ORDER BY user_id";
            return jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs), userId, Status.ACCEPTED.toString());
        } catch (Exception e) {
            log.info("Пользователь с id {} не найден", userId);
            throw new ItemNotFoundException("Пользователь с таким id не найден");
        }
    }

    @Override
    public Collection<User> getCommonFriends(long firstId, long secondId) {
        try {
            String sql = "SELECT * FROM users WHERE user_id IN (SELECT friend_id FROM users_friends " +
                    "WHERE user_id = ? AND friend_id IN (SELECT friend_id FROM users_friends WHERE user_id = ?" +
                    "AND status = ?)) ORDER BY user_id";
            return jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs), firstId, secondId, Status.ACCEPTED.toString());
        } catch (Exception e) {
            log.info("Пользователь с id {} или {} не найден", firstId, secondId);
            throw new ItemNotFoundException("Пользователь не найден");
        }
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
