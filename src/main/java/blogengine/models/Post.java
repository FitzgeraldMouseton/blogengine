package blogengine.models;

import blogengine.models.postconstants.PostConstraints;
import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull
    @Column(name = "is_active")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private boolean active;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ModerationStatus moderationStatus;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private User moderator;

    @NotNull
    private LocalDateTime time;

    @NotNull
    @Size(min = PostConstraints.MIN_TITLE_SIZE)
    private String title;

    @NotNull
    @Column(columnDefinition = "TEXT")
    @Size(min = PostConstraints.MIN_TEXT_SIZE)
    private String text;

    @Column(name = "view_count")
    private int viewCount;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
//    @ElementCollection(fetch = FetchType.EAGER)
    private List<Vote> votes = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(name = "posts_tags",
            joinColumns = {@JoinColumn(name = "post_id")},
            inverseJoinColumns = {@JoinColumn(name = "tag_id")})
    private Set<Tag> tags = new HashSet<>();

    public void addVote(final Vote vote) {
        this.votes.add(vote);
        vote.setPost(this);
    }

    public void removeVote(final Vote vote) {
        this.votes.remove(vote);
        vote.setPost(null);
    }

    public void addComment(final Comment comment) {
        this.comments.add(comment);
        comment.setPost(this);
    }

    public void removeComment(final Comment comment) {
        this.comments.remove(comment);
        comment.setPost(null);
    }

    public void addTag(final Tag tag) {
        this.tags.add(tag);
        tag.getPosts().add(this);
    }

    public void addTags(Set<Tag> tags) {
        tags.forEach(this::addTag);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Post post = (Post) o;

        if (!user.equals(post.user)) return false;
        if (!time.equals(post.time)) return false;
        if (!title.equals(post.title)) return false;
        return text.equals(post.text);
    }

    @Override
    public int hashCode() {
        int result = 31;
        result = 31 * result + user.hashCode();
        result = 31 * result + time.hashCode();
        result = 31 * result + title.hashCode();
        result = 31 * result + text.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", user=" + user.getName() +
                ", title='" + title + '\'' +
                '}';
    }
}
