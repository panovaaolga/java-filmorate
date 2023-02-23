package ru.yandex.practicum.filmorate.service;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ItemNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Service
@Slf4j
public class UserService {
    @Getter
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }


    public void makeFriendship(long receiverId, long senderId) {
        if (userStorage.get(receiverId) != null && userStorage.get(senderId) != null) {
            userStorage.get(receiverId).addFriend(senderId);
            userStorage.get(senderId).addFriend(receiverId);
        } else {
            log.info("ItemNotFoundException: {}", "Пользователи с такими id не найдены");
            throw new ItemNotFoundException("Пользователи с такими id не найдены");
        }
    }

    public void deleteFriends(long receiverId, long senderId) {
        if (userStorage.get(receiverId) != null && userStorage.get(senderId) != null) {
            userStorage.get(receiverId).removeFriend(senderId);
            userStorage.get(senderId).removeFriend(receiverId);
        } else {
            log.info("ItemNotFoundException: {}", "Пользователи с такими id не найдены");
            throw new ItemNotFoundException("Пользователи с такими id не найдены");
        }
    }

    public List<User> getAllFriends(long userId) {
        List<User> allFriendsList = new ArrayList<>();
        for (long id : userStorage.get(userId).getFriends()) {
            allFriendsList.add(userStorage.get(id));
        }
        return allFriendsList;
    }

    public List<User> getMutualFriends(long userId, long friendId) {
        List<User> mutualFriends = new ArrayList<>();
        for (User user : getAllFriends(userId)) {
            if (getAllFriends(friendId).contains(user)) {
                mutualFriends.add(user);
            }
        }
        return mutualFriends;
    }
}
