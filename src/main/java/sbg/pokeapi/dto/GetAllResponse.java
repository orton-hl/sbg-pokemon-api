package sbg.pokeapi.dto;


import lombok.Builder;
import lombok.Data;
import sbg.pokeapi.model.Pokemon;
import sbg.pokeapi.model.PokemonEntry;

import java.util.List;

@Data
@Builder
public class GetAllResponse {
    private Integer count;
    private List<PokemonEntry> pokemon;
}
