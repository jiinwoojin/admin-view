package com.jiin.admin.website.util;

import com.jiin.admin.website.model.LayerRowModel;
import lombok.extern.slf4j.Slf4j;
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
// Excel 버전은 2010 이후를 기준으로 한다.
@Slf4j
public class SpreadSheetUtil {
    private static final List<String> LAYER_FIELDS = Arrays.asList("name", "description", "projection", "middleFolder", "type", "originalDataPath");
    public static List<LayerRowModel> loadLayerRowModelListBySpreadSheet(MultipartFile file){
        if (file == null) {
            return new ArrayList<>();
        }

        String fileExt = FileSystemUtil.loadFileExtensionName(file.getOriginalFilename());
        List<LayerRowModel> rows = new ArrayList<>();
        if (!fileExt.equalsIgnoreCase("xlsx")) {
            return rows;
        } else {
            try {
                XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
                XSSFSheet sheet = workbook.getSheetAt(0);
                for(int i = 1; i <= sheet.getLastRowNum(); i++){
                    XSSFRow row = sheet.getRow(i);
                    if (row != null) {
                        LayerRowModel model = new LayerRowModel();
                        Class clazz = model.getClass();
                        XSSFCell cell;
                        String context;
                        for(int j = 0; j < LAYER_FIELDS.size(); j++){
                            cell = row.getCell(j);
                            if(cell != null){
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
