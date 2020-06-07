package blogengine.services;

import blogengine.models.CaptchaCode;
import blogengine.models.dto.authdto.CaptchaDto;
import blogengine.repositories.CaptchaRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class CaptchaService {

    private final CaptchaRepository captchaRepository;
    private final TaskScheduler taskScheduler;

    @Value("${captcha.expiration.time}")
    private Integer captchaExistenceTime;

    public CaptchaCode findBySecretCode(String code){
        return captchaRepository.findBySecretCode(code).orElse(null);
    }

    public CaptchaCode findByCode(String code){
        return captchaRepository.findByCode(code).orElse(null);
    }


    public void save(CaptchaCode captchaCode){
        captchaRepository.save(captchaCode);
    }

    @Transactional
    public void delete(CaptchaCode captchaCode) {
        captchaRepository.delete(captchaCode);
    }

    @Transactional
    public void deleteCaptchaCodeBySecretCode(String secretCode) {
        captchaRepository.deleteBySecretCode(secretCode);
    }

    public List<CaptchaCode> getAllCaptchaCodes(){
        return captchaRepository.findAllBy();
    }

    public CaptchaDto generateCaptcha(){

        CaptchaDto captchaDto = new CaptchaDto();
        String secretCode = RandomStringUtils.randomAlphanumeric(15);
        String code = RandomStringUtils.randomAlphanumeric(4);
        BufferedImage image = generateCaptchaImage(code);

        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", os);
            captchaDto.setSecret(secretCode);
            captchaDto.setImage("data:image/png;charset=utf-8;base64, " +
                    java.util.Base64.getEncoder().encodeToString(os.toByteArray()));

        } catch (IOException e) {
            e.printStackTrace();
        }

        CaptchaCode captchaCode = new CaptchaCode(code, secretCode, LocalDateTime.now());
        captchaRepository.save(captchaCode);
        taskScheduler.schedule(() -> captchaRepository.deleteBySecretCode(secretCode),
                Instant.now().plusSeconds(captchaExistenceTime));
        return captchaDto;
    }

    private BufferedImage generateCaptchaImage(String code){

        int width = 100;
        int height = 35;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.OPAQUE);
        Graphics graphics = image.createGraphics();
        graphics.setFont(new Font("Arial", Font.BOLD, 15));
        graphics.setColor(new Color(169, 169, 169));
        graphics.fillRect(0, 0, width, height);
        graphics.setColor(new Color(255, 255, 255));
        graphics.drawString(code, 40, 20);
        return image;
    }
}
