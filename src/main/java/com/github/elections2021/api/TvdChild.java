package com.github.elections2021.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true) // do not fail on unknonwn fields like "children"
public class TvdChild {

    private Long id;
    private String text;
    private String href;
    private String prver;
    private Boolean isUik;
    private Boolean selected;
    private Boolean load_on_demand;

    // todo: may also add a children, it is soe array
}
