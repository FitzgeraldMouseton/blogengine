package blogengine.models;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@Entity
@NoArgsConstructor(force = true)
@Table(name = "captcha_codes")
public class CaptchaCode {

    public CaptchaCode(@NotNull String code, @NotNull String secretCode, @NotNull Date time) {
        this.code = code;
        this.secretCode = secretCode;
        this.time = time;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.PRIVATE)
    private int id;

    @NotNull
    private String code;

    @NotNull
    private String secretCode;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    private Date time;
}
