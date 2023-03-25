DROP TABLE IF EXISTS PUBLIC.FILMS_LIKES;
DROP TABLE IF EXISTS PUBLIC.GENRES_FILMS;
DROP TABLE IF EXISTS PUBLIC.USERS_FRIENDS;
DROP TABLE IF EXISTS PUBLIC.USERS;
DROP TABLE IF EXISTS PUBLIC.GENRES;
DROP TABLE IF EXISTS PUBLIC.FILMS;
DROP TABLE IF EXISTS PUBLIC.RATINGS;


CREATE TABLE IF NOT EXISTS PUBLIC.RATINGS (
	RATING_ID INTEGER NOT NULL AUTO_INCREMENT,
	RATING_NAME CHARACTER VARYING(5) NOT NULL,
	CONSTRAINT RATINGS_PK PRIMARY KEY (RATING_ID)
);

CREATE TABLE IF NOT EXISTS PUBLIC.GENRES (
	GENRE_ID INTEGER NOT NULL AUTO_INCREMENT,
	GENRE_NAME CHARACTER VARYING(30) NOT NULL,
	CONSTRAINT GENRES_PK PRIMARY KEY (GENRE_ID)
);

CREATE TABLE IF NOT EXISTS PUBLIC.FILMS (
	FILM_ID BIGINT NOT NULL AUTO_INCREMENT,
	TITLE CHARACTER VARYING(255) NOT NULL,
	DESCRIPTION CHARACTER VARYING(200) NOT NULL,
	DURATION INTEGER NOT NULL,
	RELEASE_DATE DATE NOT NULL,
	RATING_ID INTEGER REFERENCES PUBLIC.RATINGS(RATING_ID) ON DELETE RESTRICT ON UPDATE CASCADE,
	CONSTRAINT FILMS_PK PRIMARY KEY (FILM_ID)
);

CREATE TABLE IF NOT EXISTS PUBLIC.USERS (
	USER_ID BIGINT NOT NULL AUTO_INCREMENT,
	USER_NAME CHARACTER VARYING(255),
	EMAIL CHARACTER VARYING(150) NOT NULL,
	LOGIN CHARACTER VARYING(255) NOT NULL,
	BIRTHDAY DATE NOT NULL,
	CONSTRAINT USERS_PK PRIMARY KEY (USER_ID)
);

CREATE TABLE IF NOT EXISTS PUBLIC.USERS_FRIENDS (
	USER_ID BIGINT NOT NULL,
	FRIEND_ID BIGINT NOT NULL,
	STATUS CHARACTER VARYING(12) NOT NULL,
	CONSTRAINT USERS_FRIENDS_PK PRIMARY KEY (USER_ID,FRIEND_ID)
);

CREATE TABLE IF NOT EXISTS PUBLIC.GENRES_FILMS (
	GENRE_ID INTEGER NOT NULL,
	FILM_ID BIGINT NOT NULL,
	CONSTRAINT GENRES_FILMS_PK PRIMARY KEY (GENRE_ID,FILM_ID)
);

CREATE TABLE IF NOT EXISTS PUBLIC.FILMS_LIKES (
	FILM_ID BIGINT NOT NULL,
	USER_ID BIGINT NOT NULL,
	CONSTRAINT FILMS_LIKES_PK PRIMARY KEY (FILM_ID,USER_ID)
);

ALTER TABLE PUBLIC.FILMS ADD CONSTRAINT FILMS_RATINGS_FK FOREIGN KEY (RATING_ID)
REFERENCES PUBLIC.RATINGS(RATING_ID) ON DELETE RESTRICT ON UPDATE CASCADE;

ALTER TABLE PUBLIC.GENRES_FILMS ADD CONSTRAINT GENRES_FILMS_FK FOREIGN KEY (FILM_ID) REFERENCES PUBLIC.FILMS(FILM_ID)
ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE PUBLIC.GENRES_FILMS ADD CONSTRAINT GENRES_FILMS_GENRE_FK FOREIGN KEY (GENRE_ID)
REFERENCES PUBLIC.GENRES(GENRE_ID) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE PUBLIC.FILMS_LIKES ADD CONSTRAINT FILMS_LIKES_FK FOREIGN KEY (USER_ID) REFERENCES PUBLIC.USERS(USER_ID)
ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE PUBLIC.FILMS_LIKES ADD CONSTRAINT FILMS_LIKES_FILM_FK FOREIGN KEY (FILM_ID) REFERENCES PUBLIC.FILMS(FILM_ID)
ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE PUBLIC.USERS_FRIENDS ADD CONSTRAINT USERS_FRIENDS_FK FOREIGN KEY (USER_ID) REFERENCES PUBLIC.USERS(USER_ID)
ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE PUBLIC.USERS_FRIENDS ADD CONSTRAINT USERS_FRIENDS_FRIEND_FK FOREIGN KEY (FRIEND_ID)
REFERENCES PUBLIC.USERS(USER_ID) ON DELETE CASCADE ON UPDATE CASCADE;


