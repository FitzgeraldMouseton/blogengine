package blogengine.models;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name = "post_comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.PRIVATE)
    private int id;

    @OneToMany(mappedBy = "comment")
    private List<Comment> comments = new ArrayList<>();

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Comment comment;

    @NotNull
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Post post;

    @NotNull
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private User user;

    @NotNull
    private LocalDateTime time;

    private String text;
}
