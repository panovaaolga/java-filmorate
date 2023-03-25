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
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
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

	@Test
	@Order(12)
	public void shouldCreateFilm() throws ValidateException {
		Film film = new Film();
		film.setName("Test Film");
		film.setDescription("Test description");
		film.setDuration(120);
		film.setReleaseDate(LocalDate.of(2008, 05, 05));
		film.setMpa(new MpaRating(2, null));
		filmDbStorage.create(film);
		assertThat(film).hasFieldOrPropertyWithValue("id", 1L);
		assertThat(film).hasFieldOrPropertyWithValue("name", "Test Film");
	}

	@Test
	@Order(13)
	public void shouldThrowExceptionWhenUpdateFilm() throws ValidateException {
		Film film = new Film();
		film.setName("Test Film 2.0");
		film.setDescription("Test description");
		film.setDuration(120);
		film.setReleaseDate(LocalDate.of(2008, 05, 05));
		film.setMpa(new MpaRating(1, null));
		assertThatThrownBy(() -> filmDbStorage.update(film)).isInstanceOf(ItemNotFoundException.class);
	}

	@Test
	@Order(14)
	public void shouldReturnFilmById() {
		Optional<Film> filmOptional = Optional.of(filmDbStorage.get(1));

		assertThat(filmOptional)
				.isPresent()
				.hasValueSatisfying(film ->
						assertThat(film).hasFieldOrPropertyWithValue("id", 1L)
								.hasFieldOrPropertyWithValue("description", "Test description")
				);
	}

	@Test
	@Order(15)
	public void shouldReturnAllFilms() throws ValidateException {
		Film film = new Film();
		film.setName("Test Film 2.0");
		film.setDescription("Test description 2.0");
		film.setDuration(120);
		film.setReleaseDate(LocalDate.of(2007, 05, 05));
		film.setMpa(new MpaRating(1, null));
		filmDbStorage.create(film);

		Film film2 = new Film();
		film2.setName("Test Film 3.0");
		film2.setDescription("Test description 3.0");
		film2.setDuration(160);
		film2.setReleaseDate(LocalDate.of(2006, 05, 05));
		film2.setMpa(new MpaRating(5, null));
		filmDbStorage.create(film2);

		Collection<Film> filmCollection = filmDbStorage.getAll();
		assertThat(filmCollection).isNotNull().isNotEmpty().hasSize(3);
	}

	@Test
	@Order(16)
	public void shouldAddLike() {
		filmDbStorage.addLike(2, 1);

		Film film = filmDbStorage.get(2);
		assertThat(film).hasFieldOrPropertyWithValue("likesCount", 1L);
	}

	@Test
	@Order(17)
	public void shouldThrowExceptionWhenAddLike() {
		assertThatThrownBy(() -> filmDbStorage.addLike(2, 6)).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	@Order(18)
	public void shouldReturnLikes() {
		filmDbStorage.addLike(2, 3);
		long likes = filmDbStorage.getLikes(2);
		assertThat(likes).isEqualTo(2L);
	}

	@Test
	@Order(19)
	public void shouldReturnPopular() {
		filmDbStorage.addLike(1, 1);

		Collection<Film> popularFilms = filmDbStorage.getPopular(5);

		assertThat(popularFilms).hasSize(3);
		assertThat(popularFilms.toArray()[0]).hasFieldOrPropertyWithValue("id", 2L);
		assertThat(popularFilms.toArray()[1]).hasFieldOrPropertyWithValue("id", 1L);
	}

	@Test
	@Order(20)
	public void shouldThrowExceptionWhenDeleteLike() {
		assertThatThrownBy(() -> filmDbStorage.deleteLike(2, 2)).isInstanceOf(ItemNotFoundException.class);
	}

	@Test
	@Order(21)
	public void shouldDeleteFilm() {
		filmDbStorage.delete(3);

		Collection<Film> filmCollection = filmDbStorage.getAll();
		assertThat(filmCollection).hasSize(2);
	}

	@Test
	@Order(22)
	public void shouldReturnAllGenres() {
		Collection<Genre> genres = filmDbStorage.getGenres();
		assertThat(genres).hasSize(6);
		assertThat(genres.toArray()[0]).hasFieldOrPropertyWithValue("name", "Комедия");
	}

	@Test
	@Order(23)
	public void shouldThrowExceptionWhenGetWrongGenre() {
		assertThatThrownBy(() -> filmDbStorage.getGenre(8)).isInstanceOf(ItemNotFoundException.class);
	}

	@Test
	@Order(24)
	public void shouldReturnGenreById() {
		Genre genre = filmDbStorage.getGenre(2);
		assertThat(genre).hasFieldOrPropertyWithValue("name", "Драма");
	}

	@Test
	@Order(25)
	public void shouldReturnAllRatings() {
		Collection<MpaRating> ratings = filmDbStorage.getRatings();
		assertThat(ratings).hasSize(5);
		assertThat(ratings.toArray()[4]).hasFieldOrPropertyWithValue("name", "NC-17");
	}

	@Test
	@Order(26)
	public void shouldReturnRatingById() {
		MpaRating rating = filmDbStorage.getMpa(1);
		assertThat(rating).hasFieldOrPropertyWithValue("name", "G");
	}
}
