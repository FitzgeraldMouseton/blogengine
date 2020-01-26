package blogengine.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
@Entity
@Table(name = "global_settings")
public class GlobalSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Setter(AccessLevel.PRIVATE)
    private int id;

    @NotNull
    private String code;

    @NotNull
    private String name;

    @NotNull
    private String value;
}
