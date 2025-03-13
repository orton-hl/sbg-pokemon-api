package sbg.pokeapi.service;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import sbg.pokeapi.dto.GetAllResponse;
import sbg.pokeapi.model.Pokemon;
import sbg.pokeapi.model.PokemonEntry;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class PokeApiV2Client {

    private static final String JSON_PATH_GET_ABILITIES_LIST = "$.abilities[*]";
    private static final String JSON_PATH_GET_ALL_LIST = "$.results[*].url";
    private static final String JSON_PATH_GET_ALL_LIST_COUNT = "$.count";
    private static final String JSON_PATH_STAT_LIST = "$.stats[*]";
    private static final String JSON_PATH_STAT_ENRTY_BASE_STAT = "$.base_stat";
    private static final String JSON_PATH_STAT_ENRTY_EFFORT_STAT = "$.effort";
    private static final String JSON_PATH_STAT_ENRTY_NAME_STAT = "$.stat.name";
    private static final String JSON_PATH_STAT_PHYSICAL_ATTRIBUTES_HEIGHT = "$.height";
    private static final String JSON_PATH_STAT_PHYSICAL_ATTRIBUTES_WEIGHT = "$.weight";
    private static final String JSON_PATH_SPRITES_OTHER_DREAM_WORLD_FRONT_DEFAULT = "$.sprites.other.dream_world.front_default";
    private static final String JSON_PATH_SPRITES_OTHER_OFFICIAL_ARTWORK_FRONT_DEFAULT = "$.sprites.other['official-artwork'].front_default";
    private static final String JSON_PATH_SPRITES_OTHER_OFFICIAL_ARTWORK_FRONT_SHINY = "$.sprites.other['official-artwork'].front_shiny";
    private static final String JSON_PATH_TYPES_LIST = "$.types[*]";
    private static final String JSON_PATH_TYPES_LIST_NAME = "$.type.name";
    private static final String JSON_PATH_POKEMON_NAME = "$.name";
    private static final String JSON_PATH_POKEMON_ID = "$.id";

    RestClient restClient = RestClient.create("https://pokeapi.co");

    public Optional<GetAllResponse> getAll() {
        return this.getAll(1, 10);
    }

    public Optional<GetAllResponse> getAll(@NonNull() Integer pageNo,@NonNull()  Integer  pageSize) {
        String response = restClient.get().uri(uri -> uri.
                        path("/api/v2/pokemon")
                        .queryParam("limit", pageSize)
                        .queryParam("offset", pageSize * (pageNo - 1))
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(String.class);


        List<String> list = JsonPath.parse(response).read(JSON_PATH_GET_ALL_LIST, List.class);
        Integer count = JsonPath.read(response, JSON_PATH_GET_ALL_LIST_COUNT);


        List<PokemonEntry> entries = list.parallelStream()
                .map(pokemonUrl -> {
                    String[] urlSections = pokemonUrl.split("/");
                    Integer id = Integer.parseInt(urlSections[urlSections.length - 1]);
                    return getPokemonDetailsById(id).get();
                })
                .map(p -> PokemonEntry.builder()
                        .avatar(p.getPhysicalAttributes().getAppearance())
                        .id(p.getId())
                        .name(p.getName())
                        .build())
                .collect(Collectors.toList());


        return Optional.of(GetAllResponse.builder()
                .count(count)
                .pokemon(entries)
                .build());
    }

    public Optional<Pokemon> getPokemonDetails(@NonNull String name) {
        String url = String.format("/api/v2/pokemon/%s", name);
        return extractPokemon(url);
    }

    public Optional<Pokemon> getPokemonDetailsById(@NonNull Integer id) {
        String url = String.format("api/v2/pokemon/%s/", id);
        return extractPokemon(url);
    }

    private Optional<Pokemon> extractPokemon(@NonNull String url) {
        String response = restClient.get().uri(url)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(String.class);

        DocumentContext docCTX = JsonPath.parse(response);

        return Optional.of(Pokemon.builder()
                .id(JsonPath.read(response, JSON_PATH_POKEMON_ID))
                .name(JsonPath.read(response, JSON_PATH_POKEMON_NAME))
                .stats(PokeApiV2Client.extractStats(docCTX))
                .physicalAttributes(PokeApiV2Client.extractPhysicalAttributes(docCTX))
                .abilities(PokeApiV2Client.extractAbilities(docCTX))
                .build());
    }

    public static List<Pokemon.StatEntry> extractStats(DocumentContext docCTX) {
        return docCTX.read(JSON_PATH_STAT_LIST, List.class).stream()
                .map(e -> {
                    DocumentContext entry = JsonPath.parse(e);
                    return Pokemon.StatEntry.builder()
                            .baseStat(entry.read(JSON_PATH_STAT_ENRTY_BASE_STAT))
                            .effort(entry.read(JSON_PATH_STAT_ENRTY_EFFORT_STAT))
                            .name(entry.read(JSON_PATH_STAT_ENRTY_NAME_STAT))
                            .build();
                })
                .toList();
    }

    public static Pokemon.PhysicalAttributes extractPhysicalAttributes(DocumentContext docCTX) {

        Pokemon.PhysicalAttributes.PhysicalAttributesBuilder physicalAttributes = Pokemon.PhysicalAttributes.builder();

        physicalAttributes.height(docCTX.read(JSON_PATH_STAT_PHYSICAL_ATTRIBUTES_HEIGHT))
                .weight(docCTX.read(JSON_PATH_STAT_PHYSICAL_ATTRIBUTES_WEIGHT));

        Optional<String> dreamWorldFrontDefault = PokeApiV2Client.readPath(docCTX, JSON_PATH_SPRITES_OTHER_DREAM_WORLD_FRONT_DEFAULT);
        Optional<String> frontDefault = PokeApiV2Client.readPath(docCTX, JSON_PATH_SPRITES_OTHER_OFFICIAL_ARTWORK_FRONT_DEFAULT);
        Optional<String> frontShiny = PokeApiV2Client.readPath(docCTX, JSON_PATH_SPRITES_OTHER_OFFICIAL_ARTWORK_FRONT_SHINY);

        List<String> types = docCTX.read(JSON_PATH_TYPES_LIST, List.class).stream()
                .map(e -> JsonPath.parse(e).read(JSON_PATH_TYPES_LIST_NAME))
                .toList();

        physicalAttributes.types(types);

        if (dreamWorldFrontDefault.isPresent()) {
            physicalAttributes.appearance(dreamWorldFrontDefault.get());
        } else if (frontDefault.isPresent()) {
            physicalAttributes.appearance(frontDefault.get());
        } else if (frontShiny.isPresent()) {
            physicalAttributes.appearance(docCTX.read(frontShiny.get()));
        } else {
            physicalAttributes.appearance("NA");
        }
        return physicalAttributes.build();
    }

    public static List<Pokemon.Abilities> extractAbilities(DocumentContext docCTX) {
        List<LinkedHashMap> list = docCTX.read(JSON_PATH_GET_ABILITIES_LIST, List.class);

        return list.stream().map(s -> {


            DocumentContext _docCTX = JsonPath.parse(s);
            return Pokemon.Abilities.builder()
                    .name(PokeApiV2Client.<String>readPath(_docCTX, "$.ability.name").orElse(null))
                    .url(PokeApiV2Client.<String>readPath(_docCTX, "$.ability.url").orElse(null))
                    .slot(PokeApiV2Client.<Integer>readPath(_docCTX, "$.slot").orElse(null))
                    .isHidden(PokeApiV2Client.<Boolean>readPath(_docCTX, "$.is_hidden").orElse(null))
                    .build();
        }).collect(Collectors.toList());
    }

    private static <T> Optional<T> readPath(DocumentContext docCTX, String path) {
        try {
            return Optional.of(docCTX.read(path));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
