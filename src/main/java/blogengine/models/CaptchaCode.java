package blogengine.models;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor(force = true)
@Table(name = "captcha_codes")
public class CaptchaCode {

    public CaptchaCode(@NotNull String code, @NotNull String secretCode, @NotNull LocalDateTime time) {
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
    private LocalDateTime time;
}
