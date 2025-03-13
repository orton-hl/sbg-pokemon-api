package sbg.pokeapi.controller;

import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sbg.pokeapi.dto.GetAllResponse;
import sbg.pokeapi.model.Pokemon;
import sbg.pokeapi.service.PokeApiV2Client;

import java.util.Objects;
import java.util.Optional;

@RestController()
@CrossOrigin(origins = "*")
@RequestMapping("/pokedex")
public class PokeDexController {

    @Autowired
    PokeApiV2Client pokeClient;

    @GetMapping("")
    public ResponseEntity<?> getAll(@Nullable @RequestParam("pageNo") Integer pageNo,@Nullable @RequestParam("pageSize") Integer pageSize) {
        Optional<GetAllResponse> response;
        if(Objects.isNull(pageNo) || Objects.isNull(pageSize)) {
            response = pokeClient.getAll();
        }
        else {
            response = pokeClient.getAll(pageNo, pageSize);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{name}")
    public ResponseEntity<Pokemon> whosThatPokemon(@PathVariable("name") String name) {
        Optional<Pokemon> pokemon = pokeClient.getPokemonDetails(name);
        return pokemon.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
