package blogengine.models.dto;

import lombok.Data;

@Data
public class SettingsDto {

    private boolean MULTIUSER_MODE = false;
    private boolean POST_PREMODERATION = true;
    private boolean STATISTICS_IS_PUBLIC = true;
}
