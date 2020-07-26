package blogengine.models;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@Table(name = "post_votes")
public class Vote {

    public Vote(@NotNull byte value) {
        this.value = value;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull
    @ManyToOne
    private User user;

    @NotNull
    @ManyToOne
    private Post post;

    @NotNull
    private LocalDateTime time;

    @NotNull
    private byte value;
}
