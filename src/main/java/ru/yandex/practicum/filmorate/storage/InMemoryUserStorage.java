package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ValidateException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

@Component
@Slf4j
public class InMemoryUserStorage extends AbstractInMemoryStorage<User> implements UserStorage {

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
