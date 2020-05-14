package blogengine.models;

import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Getter
@Setter
@Entity
@NoArgsConstructor(force = true)
@Table(name = "posts")
public class Post {

    public Post(@NotNull int id, @NotNull boolean active, @NotNull ModerationStatus moderationStatus, @NotNull User user, @NotNull User moderator, @NotNull LocalDateTime time, @NotNull String title, @NotNull String text, @NotNull int viewCount) {
        this.id = id;
        this.active = active;
        this.moderationStatus = moderationStatus;
        this.user = user;
        this.moderator = moderator;
        this.time = time;
        this.title = title;
        this.text = text;
        this.viewCount = viewCount;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.PRIVATE)
    private int id;

    @NotNull
    @Column(name = "is_active")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private boolean active;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ModerationStatus moderationStatus;

    @NotNull
    @ManyToOne
    private User user;

    @NotNull
    @ManyToOne
    private User moderator;

    @NotNull
    private LocalDateTime time;

    @NotNull
    private String title;

    @NotNull
    @Column(columnDefinition = "TEXT")
    private String text;

    @NotNull
    @Column(name = "view_count")
    private int viewCount;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Vote> votes = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Comment> comments = new ArrayList<>();

    @ManyToMany
    private Set<Tag> tags = new HashSet<>();

    public void addVote(Vote vote){
        this.votes.add(vote);
        vote.setPost(this);
    }

    public void addComment(Comment comment) {
        this.comments.add(comment);
        comment.setPost(this);
    }
}
