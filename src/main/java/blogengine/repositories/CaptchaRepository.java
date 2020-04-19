package blogengine.repositories;

import blogengine.models.CaptchaCode;
import org.springframework.data.repository.CrudRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CaptchaRepository extends CrudRepository<CaptchaCode, Integer> {

    Optional<CaptchaCode> findBySecretCode(String code);
    Optional<CaptchaCode> findByCode(String code);
    @Scheduled
    void deleteCaptchaCodeBySecretCode(String secretCode);
}
