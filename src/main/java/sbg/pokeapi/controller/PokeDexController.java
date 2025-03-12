package sbg.pokeapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sbg.pokeapi.model.Pokemon;
import sbg.pokeapi.model.PokemonEntry;
import sbg.pokeapi.service.PokeApiV2Client;

import java.util.List;
import java.util.Optional;

@RestController()
@RequestMapping("/pokedex")
public class PokeDexController {

    @Autowired
    PokeApiV2Client pokeClient;

    @GetMapping("/")
    public ResponseEntity<?> getAll() {
        Optional<List<PokemonEntry>> pokemon = pokeClient.getAll();

        return ResponseEntity.ok(pokeClient.getAll());
    }

    @GetMapping("/{name}")
    public ResponseEntity<Pokemon> whosThatPokemon(@PathVariable("name") String name) {
        Optional<Pokemon> pokemon = pokeClient.getPokemonDetails(name);
        return pokemon.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
