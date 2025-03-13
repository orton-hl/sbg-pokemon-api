package sbg.pokeapi.dto;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetAllRequest {
    private Integer pageSize;
    private Integer pageNo;
}
