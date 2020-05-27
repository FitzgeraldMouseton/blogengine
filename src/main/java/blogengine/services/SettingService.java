package blogengine.services;

import blogengine.models.GlobalSetting;
import blogengine.repositories.SettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SettingService {

    private final SettingsRepository settingsRepository;

    public List<GlobalSetting> getSettings(){
        return settingsRepository.findAllBy();
    }

    public GlobalSetting getSettingByCode(String code){
        return settingsRepository.findByCode(code).orElse(null);
    }

    public void save(GlobalSetting setting){
        settingsRepository.save(setting);
    }

    public void fillSettings(){
        settingsRepository.save(new GlobalSetting("MULTIUSER_MODE", "Многопользовательский режим", false));
        settingsRepository.save(new GlobalSetting("POST_PREMODERATION", "Премодерация постов", false));
        settingsRepository.save(new GlobalSetting("STATISTICS_IS_PUBLIC", "Показывать всем статистику блога", false));
    }
}
