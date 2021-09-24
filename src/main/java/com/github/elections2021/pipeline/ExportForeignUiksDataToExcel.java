package com.github.elections2021.pipeline;

import com.github.elections2021.JacksonUtils;
import com.github.elections2021.RuDataTsvUik;
import com.github.elections2021.util.FileUtils;
import com.github.elections2021.util.NameUtils;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
public class ExportForeignUiksDataToExcel {

    public static void main(String[] args) {
        final File uiksInputFile = FileUtils.getResourceFile("json/uiks-with-uikLinks.json");
//        final File uiksInputFile = FileUtils.getResourceFile("json/uiks-with-uikLinks-small.json");

        final File uiksOutputFile = FileUtils.getResourceFile("xlsx/uiks-foreign.xlsx");

        final List<RuDataTsvUik> uiks = JacksonUtils.parseList(uiksInputFile, RuDataTsvUik.class);
        log.info("Read {} UIKs from file \"{}\".", uiks.size(), uiksInputFile.getPath());

        final List<RuDataTsvUik> foreignUiks = uiks
            .stream()
            .filter(uik -> NameUtils.isForeign(uik.getUik())) // only non-Russian (aka foreign) UIKs
            .sorted(Comparator.comparing(RuDataTsvUik::getUik))
            .collect(Collectors.toList());

        // 356 foreign UIKs
        final List<String> uikNames = foreignUiks
            .stream()
            .map(RuDataTsvUik::getUik)
            .sorted()
            .collect(Collectors.toList());

        log.info("Total foreign UIKs: {}.", foreignUiks.size());

        log.info("Total UIK names: {}", uikNames.size());
        log.info("Foreign UIK names: \n{}", uikNames);

        writeToExcel(uiksOutputFile, foreignUiks);
    }

    private static void writeToExcel(final File outputFile, final List<RuDataTsvUik> foreignUiks) {
        // see https://www.baeldung.com/java-microsoft-excel
        // todo: set better styles for Excel columns
        // todo: refactor
        XSSFWorkbook workbook = new XSSFWorkbook();

        Sheet sheet = workbook.createSheet("УИКи за рубежом");
        sheet.setColumnWidth(0, 4000);
        sheet.setColumnWidth(1, 4000);
        sheet.setColumnWidth(2, 6000);
        sheet.setColumnWidth(3, 7000);
        sheet.setColumnWidth(4, 7000);

        Row header = sheet.createRow(0);

        CellStyle headerStyle = workbook.createCellStyle();
//        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        XSSFFont font = workbook.createFont();
        font.setFontName("Calibri");
        font.setFontHeightInPoints((short) 11);
        font.setBold(true);
        headerStyle.setFont(font);

        Cell headerCell = header.createCell(0);
        headerCell.setCellValue("Номер УИК");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(1);
        headerCell.setCellValue("Имя УИК");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(2);
        headerCell.setCellValue("TVD УИК");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(3);
        headerCell.setCellValue("Избирком ОМ (type = 463)");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(4);
        headerCell.setCellValue("Избирком ФЕД (type = 242)");
        headerCell.setCellStyle(headerStyle);

        final XSSFCellStyle numberStyle = workbook.createCellStyle();
        final XSSFDataFormat numberDataFormat = workbook.createDataFormat();
//        numberStyle.setDataFormat(numberDataFormat.getFormat("0")); // todo: find how to format as integer number
//        numberStyle.setDataFormat(numberDataFormat.getFormat("#,##0")); // todo: find how to format as integer number
        numberStyle.setDataFormat(numberDataFormat.getFormat("############################")); // todo: find how to format as integer number
//        numberStyle.setDataFormat(numberDataFormat.getFormat("General")); // todo: find how to format as integer number

        XSSFCellStyle linkStyle = workbook.createCellStyle();
        XSSFFont linkFont = workbook.createFont();

        // Setting the Link Style
        linkFont.setUnderline(XSSFFont.U_SINGLE);
        linkFont.setColor(HSSFColor.HSSFColorPredefined.BLUE.getIndex());
        linkStyle.setFont(linkFont);

        for (int i = 0; i < foreignUiks.size(); i++) {
            RuDataTsvUik uik = foreignUiks.get(i);
            Row row = sheet.createRow(i + 1);

            Cell cell;
            int columnNumber = 0;

            final int uikNumber = NameUtils.getUikNumber(uik.getUik());

            cell = row.createCell(columnNumber++);
            cell.setCellValue(uikNumber);

            cell = row.createCell(columnNumber++);
            cell.setCellValue(uik.getUik());

            cell = row.createCell(columnNumber++);
            cell.setCellStyle(numberStyle);
            cell.setCellValue(uik.getUikTvd().toString()); // format Number as String
//            cell.setCellValue(uik.getUikTvd()); // todo: find how to use data format

            // link one-mandate
            final XSSFHyperlink oneMandateHyperlink = workbook.getCreationHelper().createHyperlink(HyperlinkType.URL);
            oneMandateHyperlink.setAddress(uik.getUikUrlOneMandate());

            cell = row.createCell(columnNumber++);
            cell.setHyperlink(oneMandateHyperlink);
            cell.setCellValue("Одномандатники " + uikNumber);
            cell.setCellStyle(linkStyle);

            // link federal
            final XSSFHyperlink federalHyperlnk = workbook.getCreationHelper().createHyperlink(HyperlinkType.URL);
            federalHyperlnk.setAddress(uik.getUikUrlFederal());

            cell = row.createCell(columnNumber++);
            cell.setHyperlink(federalHyperlnk);
            cell.setCellValue("Федералы " + uikNumber);
            cell.setCellStyle(linkStyle);
        }

        try {
            FileOutputStream outputStream = new FileOutputStream(outputFile);
            workbook.write(outputStream);
            workbook.close();

            log.info("Exported {} foreign UIKs to file \"{}\".", foreignUiks.size(), outputFile.getPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
