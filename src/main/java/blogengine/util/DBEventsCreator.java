package blogengine.util;

import blogengine.models.CaptchaCode;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class
DBEventsCreator {

    private final JdbcTemplate jdbcTemplate;

    public void deleteCaptchaWhenExpired(final CaptchaCode captchaCode, final int captchaExpirationTime) {
        String query = String.format("CREATE EVENT delete_captcha_event_%s "
                + "ON SCHEDULE AT now() + interval %d second "
                + "DO BEGIN "
                + "DELETE FROM captcha_codes WHERE code=? ; "
                + "END", Instant.now().toEpochMilli(), captchaExpirationTime);

        jdbcTemplate.update(query, captchaCode.getCode());
    }

    public void deleteRestoreCodeWhenExpired(final String code, final int codeExpirationTime) {
        String query = String.format("CREATE EVENT delete_restore_code_event_%s "
                + "ON SCHEDULE AT now() + interval %d second "
                + "DO BEGIN "
                + "UPDATE users SET code = null WHERE code=? ; "
                + "END", Instant.now().getEpochSecond(), codeExpirationTime);

        jdbcTemplate.update(query, code);
    }
}
