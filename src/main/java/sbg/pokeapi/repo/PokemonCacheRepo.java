package sbg.pokeapi.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import sbg.pokeapi.model.Pokemon;

public interface PokemonCacheRepo extends JpaRepository<Pokemon, Integer> {
}
