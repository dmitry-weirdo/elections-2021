package com.github.elections2021.pipeline;

import com.github.elections2021.JacksonUtils;
import com.github.elections2021.RuDataTsvUik;
import com.github.elections2021.UrlConstructor;
import com.github.elections2021.api.TvdChild;
import com.github.elections2021.util.FileUtils;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.util.List;

@Log4j2
public class AddUikDataToUikJson {
    public static void main(String[] args) {
        final File uiksInputFile = FileUtils.getResourceFile("json/uiks-with-tikTvd.json");
        final File uiksOutputFile = FileUtils.getResourceFile("json/uiks-with-uikLinks.json");

        final String tikChildrenRootPath = "C:\\java\\elections-2021\\.ignoreme\\tik-children\\";

        final List<RuDataTsvUik> uiks = JacksonUtils.parseList(uiksInputFile, RuDataTsvUik.class);

        log.info("Read {} UIKs from file \"{}\".", uiks.size(), uiksInputFile.getPath());

        for (int i = 0; i < uiks.size(); i++) {
            RuDataTsvUik uik = uiks.get(i);

            log.info("============================================");
            log.info("Handling uik {} ({} / {} UIKs)...", uik.getUik(), i + 1, uiks.size());

            // for UIK's parent TIK, load TIK children data from file by tikTvd
            final Long tikTvd = uik.getTikTvd();
            final String tikChildrenFilePath = LoadTikChildren.getTikChildrenFilePath(tikChildrenRootPath, tikTvd);
            final File tikChildrenFile = new File(tikChildrenFilePath);

            final List<TvdChild> tikChildren = JacksonUtils.parseList(tikChildrenFile, TvdChild.class);

            // find TIK children by current UIK name
            final String uikName = uik.getUik();

            final TvdChild tikChildForUik = tikChildren
                .stream()
                .filter(tikChild -> tikChild.getIsUik() && tikChild.getText().equals(uikName)) // uik.uik = tikChild.text
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(String.format(
                    "Cannot find TIK child for UIK \"%s\" of TIK \"%s\" with TIK tvd = %s",
                    uikName,
                    uik.getTik(),
                    tikTvd
                )));

            log.info(
                "Successfully loaded TIK child {} for UIK \"{}\" of TIK \"{}\", TIK tvd = {}.",
                tikChildForUik.getId(),
                uikName,
                uik.getTik(),
                tikTvd
            );

            final Long uikTvd = tikChildForUik.getId();
            final String uikUrlFederal = UrlConstructor.uikUrlFederal(uikTvd);
            final String uikUrlOneMandate = UrlConstructor.uikUrlOneMandate(uikTvd);

            uik.setUikTvd(uikTvd);
            uik.setUikUrlFederal(uikUrlFederal);
            uik.setUikUrlOneMandate(uikUrlOneMandate);
        }

        JacksonUtils.serialize(uiksOutputFile, uiks);
        log.info("Saved {} UIKs with filled links to file \"{}\".", uiks.size(), uiksOutputFile);
    }
}
