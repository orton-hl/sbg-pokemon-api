package sbg.pokeapi.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class Pokemon {
    private int id;
    private String name;
    private String description;
    private List<StatEntry> stats;
    private PhysicalAttributes physicalAttributes;

    @Data
    @Builder
    public static class PhysicalAttributes {
        private Integer height;
        private Integer weight;
        private List<String> types;
        private String appearance;
    }

    @Data
    @Builder
    public static class StatEntry {
        private Integer baseStat;
        private Integer effort;
        private String name;
    }
}
