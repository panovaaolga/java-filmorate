package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.exceptions.ValidateException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {

    void create(final User user) throws ValidateException;

    void update(final User user) throws ValidateException;

    void delete(final long id);

    Collection<User> getAll();

    User get(long id);

    void validate(User user) throws ValidateException;
}
