package blogengine.repositories;

import blogengine.models.GlobalSetting;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SettingsRepository extends CrudRepository<GlobalSetting, Integer> {

    Optional<GlobalSetting> findByCode(String code);
    List<GlobalSetting> findAllBy();
}
