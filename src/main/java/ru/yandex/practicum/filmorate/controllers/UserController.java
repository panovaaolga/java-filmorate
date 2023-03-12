package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidateException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController extends AbstractController<User> {
    private final UserService userService;

    public UserController(UserService userService) {

        this.userService = userService;
    }

    @Override
    @PostMapping
    public User create(@RequestBody @Valid final User user) throws ValidateException {
        log.info("Создание пользователя {}", user);
        userService.getUserStorage().create(user);
        return user;
    }

    @Override
    @PutMapping
    public User update(@RequestBody @Valid final User user) throws ValidateException {
        log.info("Обновление пользователя {}", user);
        userService.getUserStorage().update(user);
        return user;
    }

    @Override
    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        log.info("Удаление пользователя с id {}", id);
        userService.getUserStorage().delete(id);
    }

    @Override
    @GetMapping
    public Collection<User> getAll() {
        log.info("Получение списка всех пользователей");
        return userService.getUserStorage().getAll();
    }

    @Override
    @GetMapping("/{id}")
    public User get(@PathVariable long id) {
        log.info("Получение пользователя с id {}", id);
        return  userService.getUserStorage().get(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable("id") long id, @PathVariable("friendId") long friendId) {
        log.info("Добавление пользователя {} в друзья к пользователю {}", friendId, id);
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable("id") long id, @PathVariable("friendId") long friendId) {
        log.info("Удаление пользователя {} из друзей пользователя {}", friendId, id);
        userService.deleteFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public Collection<User> getFriends(@PathVariable long id) {
        log.info("Получение списка всех друзей ползьвателя {}", id);
        return userService.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> getCommonFriends(@PathVariable("id") long id, @PathVariable("otherId") long otherId) {
        log.info("Получение списка общих друзей пользователей {} и {}", id, otherId);
        return userService.getCommonFriends(id, otherId);
    }
}
