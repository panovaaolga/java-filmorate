package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class Film extends AbstractItem {
    @NotBlank
    private String name;
    @Size(min = 1, max = 200)
    private String description;
    @NotNull
    private LocalDate releaseDate;
    @Min(1)
    private long duration;
    @NotNull
    private MpaRating rating;
    private List<Genre> genres;

    @JsonIgnore
    private Set<Long> likedUsersIds = new HashSet<>();
    @JsonIgnore
    private long likesCount = 0L;

    public void addLike(long userId) {
        if (!likedUsersIds.contains(userId)) {
            likedUsersIds.add(userId);
            likesCount++;
        }
    }

    public void deleteLike(long userId) {
        if (likedUsersIds.contains(userId)) {
            likedUsersIds.remove(userId);
            likesCount--;
        }
    }

    public long getLikes() {
        return likesCount;
    }
}
