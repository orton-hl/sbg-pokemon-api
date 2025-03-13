package sbg.pokeapi.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Builder
public class Pokemon {

    @Id
    private int id;
    private String name;
    private String description;

    @ElementCollection
    private List<StatEntry> stats;

    @Embedded
    private PhysicalAttributes physicalAttributes;

    @ElementCollection
    private List<Abilities> abilities;

    @Data
    @Builder
    @Embeddable
    public static class PhysicalAttributes {
        private Integer height;
        private Integer weight;
        private List<String> types;
        private String appearance;
    }

    @Data
    @Builder
    @Embeddable
    public static class StatEntry {
        private Integer baseStat;
        private Integer effort;
        private String name;
    }

    @Data
    @Builder
    @Embeddable
    public static class Abilities {
        private String name;
        private String url;
        private Integer slot;
        private Boolean isHidden;
    }
}
