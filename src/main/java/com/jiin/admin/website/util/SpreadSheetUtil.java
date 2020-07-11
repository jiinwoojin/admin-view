package com.jiin.admin.website.util;

import com.jiin.admin.Constants;
import com.jiin.admin.website.model.LayerRowModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// Layer Excel Upload 기능을 구현하기 위한 클래스
// Excel 버전은 2007 이후를 기준으로 한다.
@Slf4j
public class SpreadSheetUtil {
    private static final List<String> LAYER_FIELDS = Arrays.asList("name", "description", "projection", "type", "dataFileFullPath");

    // COLUMN 윗 테두리 색상 : Excel 테이블
    private static CellStyle createTableHeadStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();

        style.setAlignment(HorizontalAlignment.CENTER);
        style.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        style.setBorderTop(BorderStyle.THIN);
        style.setTopBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderBottom(BorderStyle.THIN);
        style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderLeft(BorderStyle.THIN);
        style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderRight(BorderStyle.THIN);
        style.setRightBorderColor(IndexedColors.BLACK.getIndex());

        return style;
    }

    /**
     * LAYER Model Excel 파일을 기재하기 위한 SAMPLE XLSX 파일을 생성한다.
     * @param
     */
    public static Workbook loadLayerRowModelSampleFile(String dataPath) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet();

        XSSFRow row = sheet.createRow(0);

        for (int i = 0; i < LAYER_FIELDS.size(); i++) {
            if (i < LAYER_FIELDS.size() - 1) {
                sheet.setColumnWidth(i, 7500);
            } else {
                sheet.setColumnWidth(i, 15000);
            }
        }

        CellStyle style = createTableHeadStyle(workbook);

        XSSFCell cell;
        cell = row.createCell(0);
        cell.setCellValue("LAYER 이름");
        cell.setCellStyle(style);

        cell = row.createCell(1);
        cell.setCellValue("LAYER 제원");
        cell.setCellStyle(style);

        cell = row.createCell(2);
        cell.setCellValue("좌표계 (투영)");
        cell.setCellStyle(style);

        cell = row.createCell(3);
        cell.setCellValue("LAYER 종류");
        cell.setCellStyle(style);

        cell = row.createCell(4);
        cell.setCellValue("파일 경로");
        cell.setCellStyle(style);

        row = sheet.createRow(1);

        cell = row.createCell(0);
        cell.setCellValue("[LAYER 이름]");

        cell = row.createCell(1);
        cell.setCellValue("[LAYER 제원]");

        cell = row.createCell(2);
        cell.setCellValue("[EPSG:4326 or EPSG:3857]");

        cell = row.createCell(3);
        cell.setCellValue("[RASTER,VECTOR,CADRG]");

        cell = row.createCell(4);
        cell.setCellValue(String.format("[실존하는 파일 경로 : %s 안에 있는 경로를 기준으로 입력.]", dataPath + Constants.DATA_PATH));

        return workbook;
    }

    /**
     * 업로드한 Excel 파일을 받 뒤에 해당 LAYER Model 목록을 반환한다.
     * @param file MultipartFile
     */
    public static List<LayerRowModel> loadLayerRowModelListBySpreadSheet(MultipartFile file) {
        if (file == null) {
            return new ArrayList<>();
        }

        String fileExt = FileSystemUtil.loadFileExtensionName(file.getOriginalFilename());
        List<LayerRowModel> rows = new ArrayList<>();

        // Excel 2007 버전 까지만 제공.
        if (!fileExt.equalsIgnoreCase("xlsx")) {
            return rows;
        } else {
            try {
                XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
                XSSFSheet sheet = workbook.getSheetAt(0);
                for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                    XSSFRow row = sheet.getRow(i);
                    if (row != null) {
                        LayerRowModel model = new LayerRowModel();
                        Class clazz = model.getClass();
                        XSSFCell cell;
                        String context;
                        for (int j = 0; j < LAYER_FIELDS.size(); j++) {
                            cell = row.getCell(j);
                            if (cell != null) {
                                context = cell.getStringCellValue();
                                Field field = clazz.getDeclaredField(LAYER_FIELDS.get(j));
                                field.setAccessible(true);
                                field.set(model, context);
                            }
                        }
                        rows.add(model);
                    }
                }
            } catch (IOException e) {
                log.error("ERROR - " + e.getMessage());
            } finally {
                return rows;
            }
        }
    }

}
