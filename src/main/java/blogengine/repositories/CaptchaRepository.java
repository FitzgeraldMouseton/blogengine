package blogengine.repositories;

import blogengine.models.CaptchaCode;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface CaptchaRepository extends CrudRepository<CaptchaCode, Integer> {

    Optional<CaptchaCode> findBySecretCode(String code);

    Optional<CaptchaCode> findByCode(String code);

    List<CaptchaCode> findAllBy();

    @Transactional
    @Modifying
    @Query("delete from CaptchaCode c where c.secretCode = :secretCode")
    void deleteBySecretCode(String secretCode);
}
