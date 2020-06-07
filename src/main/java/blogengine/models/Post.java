package blogengine.models;

import lombok.*;
import org.hibernate.annotations.Type;
import org.springframework.beans.factory.annotation.Value;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Entity
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Table(name = "posts")
public class Post {

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
    @Size(min = 10)
    private String title;

    @NotNull
    @Column(columnDefinition = "TEXT")
    @Size(min = 100)
    private String text;

    @NotNull
    @Column(name = "view_count")
    private int viewCount;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Vote> votes = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Comment> comments = new ArrayList<>();

    @ManyToMany(cascade = CascadeType.PERSIST)
    private Set<Tag> tags = new HashSet<>();

    public void addVote(Vote vote){
        this.votes.add(vote);
        vote.setPost(this);
    }

    public void addComment(Comment comment) {
        this.comments.add(comment);
        comment.setPost(this);
    }

    public void addTag(Tag tag){
        this.tags.add(tag);
        tag.getPosts().add(this);
    }

    public void addTags(Set<Tag> tags){
        this.tags = tags;
        tags.forEach(tag -> tag.getPosts().add(this));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Post post = (Post) o;

        if (id != post.id) return false;
        if (active != post.active) return false;
        if (viewCount != post.viewCount) return false;
        if (moderationStatus != post.moderationStatus) return false;
        if (!user.equals(post.user)) return false;
        if (!time.equals(post.time)) return false;
        if (!title.equals(post.title)) return false;
        return text.equals(post.text);
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (active ? 1 : 0);
        result = 31 * result + moderationStatus.hashCode();
        result = 31 * result + user.hashCode();
        result = 31 * result + time.hashCode();
        result = 31 * result + title.hashCode();
        result = 31 * result + text.hashCode();
        result = 31 * result + viewCount;
        return result;
    }

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", active=" + active +
                ", moderationStatus=" + moderationStatus +
                ", user=" + user +
                ", moderator=" + moderator +
                ", time=" + time +
                ", title='" + title + '\'' +
                ", text='" + text + '\'' +
                ", viewCount=" + viewCount +
                '}';
    }
}
