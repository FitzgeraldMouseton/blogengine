package blogengine.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@Entity
@Table(name = "post_votes")
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Setter(AccessLevel.PRIVATE)
    private int id;

    @NotNull
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private User user;

    @NotNull
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Post post;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    private Date time;

    @NotNull
    private byte value;
}
