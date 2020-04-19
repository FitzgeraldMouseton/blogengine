package blogengine.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User{

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

    private String name;

    @NotNull
    private String email;

    @NotNull
    private String password;

    private String code;

    @Column(columnDefinition = "TEXT")
    private String photo;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Vote> votes = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Comment> comments = new ArrayList<>();


    public void addPost(Post post){
        this.posts.add(post);
        post.setUser(this);
    }

    public void addVote(Vote vote){
        this.votes.add(vote);
        vote.setUser(this);
    }

    public void addComment(Comment comment){
        this.comments.add(comment);
        comment.setUser(this);
    }
}
