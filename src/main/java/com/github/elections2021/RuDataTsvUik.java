package com.github.elections2021;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RuDataTsvUik {
    private String fileName; // metadata

    private String level; // ЦИК России

    /**
     * Субъект РФ.
     */
    private String reg; // Алтайский край

    /**
     * Округ (ОИК).
     */
    private String oik; // Алтайский край – Барнаульский

    /**
     * ТИК.
     */
    private String tik; // Алтайская

    /**
     * Имя УИК.
     */
    private String uik; // УИК №592

    // todo: other columns in between

    /**
     * Ссылка на ТИК.
     */
    private String url;

    /**
     * {@code tvd} для ТИК.
     */
    private Long tikTvd;

    // added from uik based on TIK children data
    /**
     * {@code tvd} для УИК.
     */
    private Long uikTvd;

    /**
     * Ссылка на итоги выборов по федералам для УИК.
     */
    private String uikUrlFederal;

    /**
     * Ссылка на итоги выборов по одномандатникам для УИК.
     */
    private String uikUrlOneMandate;
}
