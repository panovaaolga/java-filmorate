package ru.yandex.practicum.filmorate.storage;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exceptions.ItemNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidateException;
import ru.yandex.practicum.filmorate.model.AbstractItem;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public abstract class AbstractInMemoryStorage<T extends AbstractItem> {
    private final Map<Long, T> storage = new HashMap<>();
    @Getter
    @Setter
    private long count = 0;

    public void create(T item) throws ValidateException {
        if (!storage.containsKey(item.getId())) {
            validate(item);
            increaseCount();
            item.setId(getCount());
            storage.put(item.getId(), item);
        } else {
            throw new IllegalArgumentException(item.getClass().getSimpleName() + " с таким id уже существует");
        }
    }

    public void update(T item) throws ValidateException {
        validate(item);
        if (storage.containsKey(item.getId())) {
            storage.put(item.getId(), item);
        } else {
            throw new ItemNotFoundException(item.getClass().getSimpleName() + " с таким id не найден");
        }
    }
    public void delete(long id) {
        if (storage.containsKey(id)) {
            storage.remove(id);
        } else {
            log.info("Пользователь с id {} не найден", id);
            throw new ItemNotFoundException("Пользователь с id " + id + " не найден");
        }
    }

    public T get(long id) {
        if (storage.containsKey(id)) {
            return storage.get(id);
        } else {
            log.info("Пользователь с id {} не найден", id);
            throw new ItemNotFoundException("Пользователь с id " + id + " не найден");
        }
    }

    abstract void validate(T item) throws ValidateException;

    public Collection<T> getAll() {

        return storage.values();
    }

    protected void increaseCount() {
        count++;
    }
}
