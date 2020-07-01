package blogengine.models;

import blogengine.models.postconstants.UserConstraints;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.PRIVATE)
    private int id;

    @NotNull
    @Column(name = "is_moderator")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private boolean isModerator;

    @NotNull
    @Column(name = "reg_time")
    private LocalDateTime regTime;

    @NotNull
    @Size(min = UserConstraints.MIN_USER_NAME_LENGTH)
    private String name;

    @NotNull
    @Column(unique = true)
    private String email;

    @NotNull
    @Size(min = UserConstraints.MIN_PASSWORD_LENGTH)
    private String password;

    private String code;

    @Column(columnDefinition = "TEXT")
    private String photo;

    @JsonIgnore
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Post> posts = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "moderator", fetch = FetchType.LAZY)
    private List<Post> postsForModeration = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Vote> votes = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Comment> comments = new ArrayList<>();


    public void addPost(final Post post) {
        this.posts.add(post);
        post.setUser(this);
    }

    public void addVote(final Vote vote) {
        this.votes.add(vote);
        vote.setUser(this);
    }

    public void addComment(final Comment comment) {
        this.comments.add(comment);
        comment.setUser(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (id != user.id) return false;
        if (!regTime.equals(user.regTime)) return false;
        if (!name.equals(user.name)) return false;
        if (!email.equals(user.email)) return false;
        return password.equals(user.password);
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + regTime.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + email.hashCode();
        result = 31 * result + password.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", isModerator=" + isModerator +
                ", regTime=" + regTime +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
