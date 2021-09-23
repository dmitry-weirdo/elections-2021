package com.github.elections2021;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RuDataTsvUik {
    private String fileName; // metadata

    private String level;
    private String reg;
    private String oik;
    private String tik;
    private String uik;

    // todo: other columns in between

    private String url;
}
