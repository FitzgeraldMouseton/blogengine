package blogengine.util;

import blogengine.models.CaptchaCode;
import blogengine.services.CaptchaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduledTasks {

    private final CaptchaService captchaService;

    // Метод удаляет капчи с истекшим сроком, если они по какой-то причине не удалились через заданное время после создания
    @Scheduled(cron = "0 0 10 * * *")  // 10 AM every day
    public void cleanExpiredCaptchaTable() {
        List<CaptchaCode> codes = captchaService.getAllCaptchaCodes();
        codes.forEach(code -> {
            if (code.getTime().plusHours(1).isBefore(LocalDateTime.now())) {
                captchaService.deleteCaptchaCodeBySecretCode(code.getSecretCode());
            }
        });
    }
}
