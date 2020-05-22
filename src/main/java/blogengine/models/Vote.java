package blogengine.models;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.apache.tomcat.jni.Local;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@Entity
@Table(name = "post_votes")
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.PRIVATE)
    private int id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    private Post post;

    @NotNull
    private LocalDateTime time;

    @NotNull
    private byte value;
}
