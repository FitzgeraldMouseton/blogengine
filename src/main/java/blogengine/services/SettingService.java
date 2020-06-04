package blogengine.services;

import blogengine.models.GlobalSetting;
import blogengine.repositories.SettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SettingService {

    private final SettingsRepository settingsRepository;

    @Value("#{${settings}}")
    HashMap<String, String> settings;

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
        settings.forEach((k,v) -> settingsRepository.save(new GlobalSetting(k, v, false)));
    }

    public GlobalSetting setSetting(String setting, Boolean isActive){
        return new GlobalSetting(setting, settings.get(setting), isActive);
    }
}
