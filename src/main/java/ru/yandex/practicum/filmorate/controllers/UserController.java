package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidateException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController extends AbstractController<User> {
    private Map<Integer, User> users = new HashMap<>();

    @Override
    @PostMapping
    public User create(@RequestBody @Valid final User user) throws ValidateException {
        validate(user);
        increaseCount();
        user.setId(getCount());
        users.put(user.getId(), user);
        log.info("Создание пользователя {}", user);
        return user;
    }

    @Override
    @PutMapping
    public User update(@RequestBody @Valid final User user) throws ValidateException {
        validate(user);
        if (users.containsKey(user.getId())) {
           users.put(user.getId(), user);
           log.info("Обновление пользователя {}", user);
           return user;
        } else {
            log.info("ValidateException: {}", "Пользователя с таким id не существует");
            throw new ValidateException("Пользователя с таким id не существует");
        }
    }

    @Override
    @GetMapping
    public List<User> get() {
        return new ArrayList<>(users.values());
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
        if (user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("В качестве имени пользователя установлен логин {}", user.getLogin());
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.info("ValidateException: {}", "Дата рождения не может быть в будущем");
            throw new ValidateException("Дата рождения не может быть в будущем");
        }
    }
}
