package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.dal.FilmDbStorage;
import ru.yandex.practicum.filmorate.dal.UserDbStorage;
import ru.yandex.practicum.filmorate.exceptions.ItemNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidateException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FilmorateApplicationTests {
	private final UserDbStorage userStorage;
	private final FilmDbStorage filmDbStorage;

	@Test
	void contextLoads() {
	}

	@Test
	@Order(1)
	public void shouldCreateUser() throws ValidateException {
		User user = new User();
		user.setName("Test");
		user.setEmail("test@email.ru");
		user.setLogin("test_login");
		user.setBirthday(LocalDate.of(1990, 03, 01));
		userStorage.create(user);
		assertThat(user).hasFieldOrPropertyWithValue("id", 1L);
	}

	@Test
	@Order(3)
	public void shouldUpdateUser() throws ValidateException {
		User user = new User();
		user.setId(1);
		user.setName("Test updated");
		user.setEmail("test@email.ru");
		user.setLogin("test_login");
		user.setBirthday(LocalDate.of(1990, 03, 01));
		userStorage.update(user);
		assertThat(user).hasFieldOrPropertyWithValue("id", 1L)
				.hasFieldOrPropertyWithValue("name", "Test updated");
	}

	@Test
	@Order(2)
	public void testFindUserById() {

		Optional<User> userOptional = Optional.of(userStorage.get(1));

		assertThat(userOptional)
				.isPresent()
				.hasValueSatisfying(user ->
						assertThat(user).hasFieldOrPropertyWithValue("id", 1L)
								.hasFieldOrPropertyWithValue("name", "Test")
				);
	}

	@Test
	@Order(4)
	public void shouldFindAllUsers() throws ValidateException {
		User user = new User();
		user.setName("Test2");
		user.setEmail("test2@email.ru");
		user.setLogin("test2_login");
		user.setBirthday(LocalDate.of(1990, 03, 02));
		userStorage.create(user);

		Optional<Collection<User>> collectionOptional = Optional.of(userStorage.getAll());

		assertThat(collectionOptional)
				.isPresent()
				.hasValueSatisfying(users -> assertThat(users).isNotNull().isNotEmpty())
				.hasValueSatisfying(users -> assertThat(users).contains(user));
	}

	@Test
	@Order(5)
	public void shouldAddFriendToOneUser() {
		userStorage.addFriend(1, 2);
		User user = userStorage.get(2);

		Collection<User> friendsOfUser = userStorage.getFriends(1);
		assertThat(friendsOfUser).isNotEmpty()
				.isNotNull()
				.contains(user);

		Collection<User> friendsOfFriend = userStorage.getFriends(2);
		assertThat(friendsOfFriend).isNullOrEmpty();
	}

	@Test
	@Order(6)
	public void shouldReturnCommonFriends() throws ValidateException {
		User user = new User();
		user.setName("Test3");
		user.setEmail("test3@email.ru");
		user.setLogin("test3_login");
		user.setBirthday(LocalDate.of(1990, 03, 03));
		userStorage.create(user);

		userStorage.addFriend(1, 3);
		userStorage.addFriend(2, 3);

		Collection<User> commonFriends = userStorage.getCommonFriends(1, 2);
		assertThat(commonFriends).contains(user);
	}

	@Test
	@Order(7)
	public void shouldReturnAllFriends() {
		Collection<User> allFriends = userStorage.getFriends(1);
		assertThat(allFriends).contains(userStorage.get(2), userStorage.get(3));
	}

	@Test
	@Order(8)
	public void shouldDeleteFriend() {
		userStorage.deleteFriend(2, 3);
		Collection<User> friendsOfUser = userStorage.getFriends(2);
		assertThat(friendsOfUser).isNullOrEmpty();
	}

	@Test
	@Order(9)
	public void shouldThrowExceptionWhenDeleteFriend() {
		assertThatThrownBy(() -> userStorage.deleteFriend(2, 3)).isInstanceOf(ItemNotFoundException.class);
	}

	@Test
	@Order(11)
	public void shouldThrowExceptionWhenDeleteFriendUnknown() {
		assertThatThrownBy(() -> userStorage.deleteFriend(2, 5)).isInstanceOf(ItemNotFoundException.class);
	}

	@Test
	@Order(10)
	public void shouldDeleteUser() {
		userStorage.delete(2);

		assertThatThrownBy(() -> userStorage.get(2)).isInstanceOf(ItemNotFoundException.class);
	}

}
