package com.intouristing.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Created by Marcelo Lacroix on 18/08/2019.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchDTO {

    private List<UserDTO> users;

}
