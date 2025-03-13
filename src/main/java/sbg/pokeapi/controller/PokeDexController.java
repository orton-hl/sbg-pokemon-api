package sbg.pokeapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @Operation(summary = "Get all Pokémon", description = "Fetches all Pokémon with optional pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of Pokémon data", content = @Content( schema = @Schema(implementation = GetAllResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid page number or page size provided"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> getAll(
            @Parameter(description = "Optional Page number", example = "1")  @Nullable @RequestParam("pageNo") Integer pageNo,
            @Parameter(description = "Optional Page size", example = "1")  @Nullable @RequestParam("pageSize") Integer pageSize) {
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
    @Operation(summary = "Get all Pokémon", description = "Fetches details about Pokémon")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval of Pokémon data", content = @Content( schema = @Schema(implementation = Pokemon.class))),
            @ApiResponse(responseCode = "400", description = "Invalid pokemon name"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Pokemon> whosThatPokemon(@Parameter(description = "pokemon name", example = "ditto") @PathVariable("name") String name) {
        Optional<Pokemon> pokemon = pokeClient.getPokemonDetails(name);
        return pokemon.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
