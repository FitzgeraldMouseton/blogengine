package blogengine.services;

import blogengine.models.GlobalSetting;
import blogengine.repositories.SettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SettingService {

    private final SettingsRepository settingsRepository;

    private Map<String, String> settingsData = Map.of("MULTIUSER_MODE", "Многопользовательский режим",
                                                        "POST_PREMODERATION", "Премодерация постов",
                                                        "STATISTICS_IS_PUBLIC", "Показывать всем статистику блога");

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
        settingsData.forEach((k,v) -> settingsRepository.save(new GlobalSetting(k, v, false)));
    }

    public GlobalSetting setSetting(String setting, Boolean isActive){
        return new GlobalSetting(setting, settingsData.get(setting), isActive);
    }
}
