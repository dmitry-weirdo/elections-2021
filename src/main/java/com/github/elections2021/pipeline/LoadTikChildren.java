package com.github.elections2021.pipeline;

import com.github.elections2021.HttpClientWrapper;
import com.github.elections2021.JacksonUtils;
import com.github.elections2021.RuDataTsvUik;
import com.github.elections2021.UrlConstructor;
import com.github.elections2021.api.TvdChild;
import com.github.elections2021.util.FileUtils;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Log4j2
public class LoadTikChildren {
    public static void main(String[] args) {
        final File uiksInputFile = FileUtils.getResourceFile("json/uiks-with-tikTvd.json");

        final List<RuDataTsvUik> uiks = JacksonUtils.parseList(uiksInputFile, RuDataTsvUik.class);

        log.info("Read {} UIKs from file \"{}\".", uiks.size(), uiksInputFile.getPath());

        final Set<Long> tikTvdsSet = uiks
            .stream()
            .map(RuDataTsvUik::getTikTvd)
            .collect(Collectors.toSet());

        final List<Long> tikTvdIdsSorted = tikTvdsSet
            .stream()
            .sorted()
            .collect(Collectors.toList());

        log.info("Total different TIK tvds: {}", tikTvdsSet.size());

        String childrenRootDir = "C:\\java\\elections-2021\\src\\main\\resources\\json\\tik-children\\"; // todo: nicer path handling

        for (int i = 0; i < tikTvdIdsSorted.size(); i++) {
            Long tikTvd = tikTvdIdsSorted.get(i);

            log.info("============================================");
            log.info("Handling tikTvd {} ({} / {} TIKs)...", tikTvd, i + 1, tikTvdIdsSorted.size());

            final String loadTikChildrenUrl = UrlConstructor.loadChildrenUrl(tikTvd);

            final List<TvdChild> tikChildren = HttpClientWrapper.loadChildren(loadTikChildrenUrl);

            final String tikChildrenFilePath = getTikChildrenFilePath(childrenRootDir, tikTvd);

            final File tikChildrenFile = new File(tikChildrenFilePath);
            JacksonUtils.serialize(tikChildrenFile, tikChildren);

            log.info("Written children of TIK tvd {} to file {}", tikTvd, tikChildrenFilePath);

//            if (i > 10) { // todo: remove this
//                return;
//            }
        }
    }

    public static String getTikChildrenFilePath(String rootDir, Long tikTvd) {
        return rootDir + tikTvd + ".json";
    }
}
