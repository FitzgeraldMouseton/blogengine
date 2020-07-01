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
    private HashMap<String, String> settings;

    @Value("${setting.multiuser}")
    private String multiuserSetting;

    public List<GlobalSetting> getSettings() {
        return settingsRepository.findAllBy();
    }

    public void save(final GlobalSetting setting) {
        settingsRepository.save(setting);
    }

    public boolean isMultiUserEnabled() {
        return settingsRepository.findByCode(multiuserSetting).get().getValue();
    }

    GlobalSetting getSettingByCode(final String code) {
        return settingsRepository.findByCode(code).orElse(null);
    }

    void fillSettings() {
        settings.forEach((k,v) -> settingsRepository.save(new GlobalSetting(k, v, false)));
    }

    GlobalSetting setSetting(final String setting, final Boolean isActive) {
        return new GlobalSetting(setting, settings.get(setting), isActive);
    }
}
