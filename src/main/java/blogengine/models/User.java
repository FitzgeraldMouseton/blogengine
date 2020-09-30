package blogengine.models;

import blogengine.models.postconstants.UserConstraints;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.annotations.Type;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
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
    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Post> posts = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "moderator", fetch = FetchType.LAZY)
    private List<Post> postsForModeration = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Vote> votes = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//    @Fetch(value = FetchMode.SUBSELECT)
    private List<Comment> comments = new ArrayList<>();


    public void addPost(final Post post) {
        this.posts.add(post);
        post.setUser(this);
    }

    public void removePost(final Post post) {
        this.posts.remove(post);
        post.setUser(null);
    }

    public void addVote(final Vote vote) {
        this.votes.add(vote);
        vote.setUser(this);
    }

    public void removeVote(final Vote vote) {
        this.votes.remove(vote);
        vote.setUser(null);
    }

    public void addComment(final Comment comment) {
        this.comments.add(comment);
        comment.setUser(this);
    }

    public void removeComment(final Comment comment) {
        this.comments.remove(comment);
        comment.setUser(null);
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
                ", name='" + name + '\'' +
                '}';
    }

    //==================


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
