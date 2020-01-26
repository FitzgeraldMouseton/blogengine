package blogengine.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.*;

@Data
@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Setter(AccessLevel.PRIVATE)
    private int id;

    @NotNull
    @Column(name = "is_active")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private boolean isActive;

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
    @Temporal(TemporalType.TIMESTAMP)
    private Date time;

    @NotNull
    private String title;

    @NotNull
    @Column(columnDefinition = "TEXT")
    private String text;

    @NotNull
    @Column(name = "view_count")
    private int viewCount;

    @OneToMany(mappedBy = "post")
    private List<Vote> votes = new ArrayList<>();

    @OneToMany(mappedBy = "post")
    private List<Comment> comments = new ArrayList<>();

    @ManyToMany
    private Set<Tag> tags = new HashSet<>();

    public void addVote(Vote vote){
        this.votes.add(vote);
        vote.setPost(this);
    }

    public void addComment(Comment comment){
        this.comments.add(comment);
        comment.setPost(this);
    }

    public enum ModerationStatus {
        NEW, ACCEPTED, DECLINED
    }
}
