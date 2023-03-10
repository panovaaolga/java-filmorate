package ru.yandex.practicum.filmorate.controllers;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.ValidateException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserControllerTest {
    static UserController userController = new UserController(new UserService(new InMemoryUserStorage()));

    @Test
    public void shouldBeSuccessfulWithCorrectData() throws ValidateException {
        final User user = new User();
        user.setEmail("test@ya.ru");
        user.setLogin("TestCorrectLogin");
        user.setName("Correct Name");
        user.setBirthday(LocalDate.of(1990, 10, 12));
        userController.create(user);
    }

    @Test
    public void shouldThrowExceptionWhenEmailIsNotCorrect() {
        final User user = new User();
        user.setEmail("testya.ru");
        user.setLogin("TestCorrectLogin");
        user.setName("Correct Name");
        user.setBirthday(LocalDate.of(1990, 10, 12));
        Exception exception = assertThrows(ValidateException.class, () -> userController.create(user));
        assertEquals("Введен некорректный email", exception.getMessage());
        user.setEmail("");
        Exception exceptionAgain = assertThrows(ValidateException.class, () -> userController.create(user));
        assertEquals("Введен некорректный email", exceptionAgain.getMessage());
    }

    @Test
    public void shouldThrowExceptionWhenLoginIsBlank() {
        final User user = new User();
        user.setEmail("test@ya.ru");
        user.setLogin("Test IncorrectLogin");
        user.setName("Correct Name");
        user.setBirthday(LocalDate.of(1990, 10, 12));
        Exception exception = assertThrows(ValidateException.class, () -> userController.create(user));
        assertEquals("Логин не может быть пустым или содержать пробелы", exception.getMessage());
        user.setLogin("");
        Exception exceptionAgain = assertThrows(ValidateException.class, () -> userController.create(user));
        assertEquals("Логин не может быть пустым или содержать пробелы", exceptionAgain.getMessage());
    }

    @Test
    public void shouldSetNameSameAsLoginWhenNameIsBlank() throws ValidateException {
        final User user = new User();
        user.setEmail("test@ya.ru");
        user.setLogin("TestCorrectLogin");
        user.setBirthday(LocalDate.of(1990, 10, 12));
        userController.create(user);
        assertEquals("TestCorrectLogin", user.getName());
    }

    @Test
    public void shouldThrowExceptionWhenBirthdayIsNotCorrect() {
        final User user = new User();
        user.setEmail("test@ya.ru");
        user.setLogin("TestCorrectLogin");
        user.setName("Correct Name");
        user.setBirthday(LocalDate.of(2990, 10, 12));
        Exception exception = assertThrows(ValidateException.class, () -> userController.create(user));
        assertEquals("Дата рождения не может быть в будущем", exception.getMessage());
    }
}
