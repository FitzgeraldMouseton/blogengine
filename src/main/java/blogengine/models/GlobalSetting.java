package blogengine.models;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
@Entity
@Table(name = "global_settings")
public class GlobalSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.PRIVATE)
    private int id;

    @NotNull
    private String code;

    @NotNull
    private String name;

    @NotNull
    private Boolean value;

    public GlobalSetting() {
    }

    public GlobalSetting(@NotNull String code, @NotNull String name, @NotNull Boolean value) {
        this.code = code;
        this.name = name;
        this.value = value;
    }
}
