package sbg.pokeapi.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PokemonEntry {
    private Integer id;
    private String name;
    private String avatar;
}
