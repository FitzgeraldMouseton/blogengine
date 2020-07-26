package blogengine.models;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Entity
@AllArgsConstructor
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
    private int id;

    @NotNull
    private String code;

    @NotNull
    private String secretCode;

    @NotNull
    private LocalDateTime time;
}
