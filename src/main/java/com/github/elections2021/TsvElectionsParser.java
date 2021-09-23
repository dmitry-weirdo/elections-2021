package com.github.elections2021;

import com.univocity.parsers.tsv.TsvParser;
import com.univocity.parsers.tsv.TsvParserSettings;
import lombok.extern.log4j.Log4j2;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Log4j2
public class TsvElectionsParser {

    public static final List<String> START_COLUMNS = List.of("level", "reg", "oik", "tik", "uik");

    public static final int LEVEL_POSITION = 0;
    public static final int REG_POSITION = 1;
    public static final int OIK_POSITION = 2;
    public static final int TIK_POSITION = 3;
    public static final int UIK_POSITION = 4;

    public static void main(String[] args) {
        String rootDir = "C:\\Users\\popovdmi\\Downloads\\Telegram Desktop\\Districts\\";
//        String fileName = "Алтайский край – Барнаульский.tsv";
//        String filePath = rootDir + fileName;

        final List<RuDataTsvUik> uiksFromAllFiles = new ArrayList<>();

        final List<String> fileNames = iterateDirectory(rootDir);

        for (int i = 0; i < fileNames.size(); i++) {
            final String fileName = fileNames.get(i);

            log.info("=================================================");
            log.info("Handling file {} (file {} / {})", fileName, i + 1, fileNames.size());

            final String filePath = rootDir + fileName;

            final List<RuDataTsvUik> uiks = handleFile(fileName, filePath);

            uiksFromAllFiles.addAll(uiks);
        }

        log.info("=================================================");
        log.info("Directory {} handled. Total files: {}. Total uiks: {}", rootDir, fileNames.size(), uiksFromAllFiles.size());
    }

    private static List<RuDataTsvUik> handleFile(String fileName, String filePath) {
        final List<String[]> rows = getRows(filePath);

        final String[] headerRow = rows.get(0);
        final int urlPosition = headerRow.length - 1;

        final List<RuDataTsvUik> uiks = new ArrayList<>();

        for (int i = 1; i < rows.size(); i++) {
            String[] row = rows.get(i);

            final String level = row[LEVEL_POSITION];
            final String reg = row[REG_POSITION];
            final String oik = row[OIK_POSITION];
            final String tik = row[TIK_POSITION];
            final String uik = row[UIK_POSITION];
            // todo: we may also parse other fields, not important for now

            final String url = row[urlPosition];

            final RuDataTsvUik uikData = RuDataTsvUik
                .builder()
                .fileName(fileName)
                .level(level)
                .reg(reg)
                .oik(oik)
                .tik(tik)
                .uik(uik)
                .url(url)
                .build();

            uiks.add(uikData);
        }

        log.info("Read file {}. \nRows count: {}", filePath, rows.size());
        //        log.info("Rows: \n{}", rows);

        log.info("Uiks size: \n{}", uiks.size());
//        log.info("UIKs: \n{}", uiks);

        return uiks;
    }

    private static List<String> iterateDirectory(String rootDir) {
        // https://www.baeldung.com/java-list-directory-files
        // no nested dirs

        try (Stream<Path> stream = Files.list(Paths.get(rootDir))) {
            final List<String> fileNames = stream
                .filter(file -> !Files.isDirectory(file))
                .map(Path::getFileName)
                .map(Path::toString)
                .collect(Collectors.toList());

            log.info("File names count:\n{}", fileNames.size());
            log.info("File names:\n{}", fileNames);

            return fileNames;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<String[]> getRows(String filePath) {
        try (Reader inputReader = new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8)) {
            TsvParser parser = new TsvParser(new TsvParserSettings());
            List<String[]> parsedRows = parser.parseAll(inputReader);
            return parsedRows;
        } catch (IOException e) {
            // handle exception
            throw new RuntimeException(e);
        }
    }
}
