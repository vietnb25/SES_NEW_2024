package vn.ses.s3m.plus.controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.keyvalue.MultiKey;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.ClientAnchor.AnchorType;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.streaming.SXSSFDrawing;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.zeroturnaround.zip.ZipUtil;

import vn.ses.s3m.plus.common.CommonUtils;
import vn.ses.s3m.plus.common.Constants;
import vn.ses.s3m.plus.common.Schema;
import vn.ses.s3m.plus.dao.UserMapper;
import vn.ses.s3m.plus.dto.DataLoadFrame1;
import vn.ses.s3m.plus.dto.Device;
import vn.ses.s3m.plus.dto.User;
import vn.ses.s3m.plus.dto.Warning;
import vn.ses.s3m.plus.form.UpdateWarningForm;
import vn.ses.s3m.plus.service.DataLoadFrame1Service;
import vn.ses.s3m.plus.service.DeviceService;
import vn.ses.s3m.plus.service.SettingService;
import vn.ses.s3m.plus.service.WarningService;

@RestController
@RequestMapping ("/load/warning")
public class WarningController {

    /** Logging */
    private final Log log = LogFactory.getLog(WarningController.class);

    @Autowired
    private WarningService warningService;

    @Autowired
    private DataLoadFrame1Service dataLoadFrame1Service;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private SettingService settingService;

    @Autowired
    private UserMapper userMapper;

    @Value ("${load.producer.export-folder}")
    private String folderName;

    private static final Integer PAGE_SIZE = 50;

    /**
     * Lấy thông tin cảnh báo theo thời gian.
     *
     * @param fromDate Thời gian bắt đầu.
     * @param toDate Thời gian kết thúc.
     * @param projectId Thời gian kết thúc.
     * @return Danh sách tổng cảnh báo theo từng thời điểm.
     */
    @GetMapping ("/")
    public ResponseEntity<?> getWarnings(@RequestParam ("fromDate") final String fromDate,
        @RequestParam ("toDate") final String toDate, @RequestParam ("projectId") final String projectId,
        @RequestParam ("customerId") final String customerId) {

        Map<String, Object> condition = new HashMap<>();
        condition.put("schema", Schema.getSchemas(Integer.parseInt(customerId)));
        condition.put("projectId", projectId);
        condition.put("fromDate", fromDate + " 00:00:00");
        condition.put("toDate", toDate + " 23:59:59");

        // list warning
        List<Warning> warnings = warningService.getTotalWarning(condition);

        // totalDeviceHasWarning
        Integer devicesWarning = warningService.getAllDeviceHasWarning(condition);

        // tổng cảnh báo
        Map<String, Long> warningMap = new HashMap<>();

        warningMap.put("devicesWarning", (long) devicesWarning);

        for (Warning warning : warnings) {
            Integer warningType = warning.getWarningType();
            switch (warningType) {
                case Constants.WarningType.NGUONG_AP_CAO:
                    warningMap.put("nguongApCao", warning.getTotalDevice());
                    break;
                case Constants.WarningType.NGUONG_AP_THAP:
                    warningMap.put("nguongApThap", warning.getTotalDevice());
                    break;
                case Constants.WarningType.NHIET_DO_TIEP_XUC:
                    warningMap.put("nhietDoTiepXuc", warning.getTotalDevice());
                    break;
                case Constants.WarningType.LECH_PHA:
                    warningMap.put("lechPha", warning.getTotalDevice());
                    break;
                case Constants.WarningType.NGUOC_PHA:
                    warningMap.put("nguocPha", warning.getTotalDevice());
                    break;
                case Constants.WarningType.LECH_AP_PHA:
                    warningMap.put("lechApPha", warning.getTotalDevice());
                    break;
                case Constants.WarningType.COS_THAP_TONG:
                    warningMap.put("heSoCongSuatThap", warning.getTotalDevice());
                    break;
                case Constants.WarningType.QUA_TAI:
                    warningMap.put("quaTai", warning.getTotalDevice());
                    break;
                case Constants.WarningType.TAN_SO_THAP:
                    warningMap.put("tanSoThap", warning.getTotalDevice());
                    break;
                case Constants.WarningType.TAN_SO_CAO:
                    warningMap.put("tanSoCao", warning.getTotalDevice());
                    break;
                case Constants.WarningType.MAT_NGUON_PHA:
                    warningMap.put("matNguon", warning.getTotalDevice());
                    break;
                case Constants.WarningType.NGUONG_HAI_BAC_N:
                    warningMap.put("nguongMeoSongN", warning.getTotalDevice());
                    break;
                case Constants.WarningType.NGUONG_TONG_HAI:
                    warningMap.put("nguongTongMeoSongHai", warning.getTotalDevice());
                    break;
                case Constants.WarningType.DONG_TRUNG_TINH:
                    warningMap.put("quaDongTrungTinh", warning.getTotalDevice());
                    break;
                case Constants.WarningType.DONG_TIEP_DIA:
                    warningMap.put("quaDongTiepDia", warning.getTotalDevice());
                    break;
                case Constants.WarningType.CANH_BAO_1:
                    warningMap.put("canhBao1", warning.getTotalDevice());
                    break;
                case Constants.WarningType.CANH_BAO_2:
                    warningMap.put("canhBao2", warning.getTotalDevice());
                    break;
                default:
                    break;
            }
        }

        return new ResponseEntity<>(warningMap, HttpStatus.OK);

    }

    /**
     * Lấy chi tiết cảnh báo theo id dự án và warning_type.
     *
     * @param warningType Kiểu cảnh báo.
     * @param fromDate Thời gian bắt đầu.
     * @param toDate Thời gian kết thúc.
     * @param projectId ID của dự án.
     * @param page Page muốn hiển thị dữ liệu.
     * @return Danh sách chi tiết của cảnh báo theo warning type
     */
    @GetMapping ("/type/{warningType}")
    public ResponseEntity<?> detailWarningByType(@PathVariable ("warningType") final String warningType,
        @RequestParam ("fromDate") final String fromDate, @RequestParam ("toDate") final String toDate,
        @RequestParam ("projectId") final String projectId, @RequestParam ("customerId") final String customerId,
        @RequestParam ("page") final Integer page) {

        Map<String, Object> condition = new HashMap<>();
        if (!warningType.equals("ALL")) {
            condition.put("warningType", warningType);
        }
        condition.put("fromDate", fromDate + " 00:00:00");
        condition.put("toDate", toDate + " 23:59:59");
        condition.put("projectId", projectId);
        condition.put("schema", Schema.getSchemas(Integer.parseInt(customerId)));

        condition.put("offset", (page - 1) * PAGE_SIZE);
        condition.put("pageSize", PAGE_SIZE);

        List<Warning> warnings = warningService.getDetailWarningByWarningType(condition);

        int totalData = warningService.countWarningByWarningType(condition);
        double totalPage = Math.ceil((double) totalData / PAGE_SIZE);

        Map<String, Object> mapData = new HashMap<>();
        mapData.put("totalPage", totalPage);
        mapData.put("currentPage", page);

        for (Warning warning : warnings) {
            condition.put("fromDate", warning.getFromDate());
            condition.put("toDate", warning.getToDate());
            List<Warning> countWarning = warningService.countWarnings(condition);
            for (Warning warning2 : countWarning) {
                if (warning.getDeviceId() == warning2.getDeviceId()
                    && warning.getWarningType() == warning2.getWarningType()) {
                    warning.setTotalDevice(warning2.getTotalDevice());
                }
            }
        }

        mapData.put("data", warnings);

        return new ResponseEntity<>(mapData, HttpStatus.OK);
    }

    /**
     * Hiển thị chi tiết các bản tin khi bị cảnh báo theo từng warning_type.
     *
     * @param warningType Kiểu cảnh báo.
     * @param fromDate Thời gian bắt đầu.
     * @param toDate Thời gian kết thúc.
     * @param deviceId ID thiết bị.
     * @param page Page muốn hiển thị dữ liệu.
     * @return Danh sách chi tiết của cảnh báo theo warning type
     */
    @GetMapping ("/detail")
    public ResponseEntity<?> showDataWarningByDevice(@RequestParam ("warningType") final String warningType,
        @RequestParam ("fromDate") final String fromDate, @RequestParam ("toDate") final String toDate,
        @RequestParam ("deviceId") final String deviceId, @RequestParam ("customerId") final String customerId,
        @RequestParam ("page") final Integer page) {

        int[] duration = new int[2];
        duration = CommonUtils.calculateDataIndex(fromDate, toDate);
        Map<String, Object> condition = new HashMap<>();
        condition.put("deviceId", deviceId);
        condition.put("fromDate", fromDate);
        condition.put("toDate", toDate);
        condition.put("warningType", warningType);
        List<DataLoadFrame1> dataWarning = new ArrayList<>();
        for (int index = duration[1]; index >= duration[0]; index--) {
            condition.put("schema", Schema.getSchemas(Integer.parseInt(customerId)) + Constants.DATA.DATA_TABLES
                .get(new MultiKey(Constants.DATA.tables[index], Constants.DATA.MESSAGE.FRAME1)));
            List<DataLoadFrame1> frame1s = dataLoadFrame1Service.getDataLoadWarning(condition);
            if (frame1s != null && frame1s.size() > 0) {
                for (DataLoadFrame1 dataItem : frame1s) {
                    dataWarning.add(dataItem);
                }
            }
            frame1s.clear();
        }

        String settingValue = settingService.getSettingValue(condition);

        Map<String, Object> mapData = new HashMap<>();

        mapData.put("dataWarning", dataWarning);
        mapData.put("settingValue", settingValue);

        return new ResponseEntity<>(mapData, HttpStatus.OK);
    }

    /**
     * Lấy thông tin chi tiết cảnh báo.
     *
     * @param warningId Id của cảnh báo.
     * @return Thông tin chi tiết của cảnh báo.
     */
    @GetMapping ("/update/{warningId}")
    public ResponseEntity<?> updateWarning(@PathVariable ("warningId") final Integer warningId,
        @RequestParam ("customerId") final String customerId) {
        Map<String, Object> condition = new HashMap<>();
        condition.put("warningId", warningId);
        condition.put("schema", Schema.getSchemas(Integer.parseInt(customerId)));
        Warning warning = warningService.getDetailWarningCache(condition);
        return new ResponseEntity<>(warning, HttpStatus.OK);
    }

    /**
     * Cập nhật thông tin chi tiết cảnh báo.
     *
     * @param form Data cần cập nhật.
     * @return Trạng thái cập nhật cảnh báo.
     */
    @PostMapping ("/update/{warningId}/{customerId}")
    public ResponseEntity<?> updateWarning(@RequestBody final UpdateWarningForm form,
        @PathVariable ("customerId") final String customerId) {
        Map<String, Object> data = new HashMap<>();
        data.put("status", form.getStatus());
        data.put("username", form.getUsername());
        data.put("id", form.getId());
        data.put("description", form.getDescription());
        data.put("schema", Schema.getSchemas(Integer.parseInt(customerId)));
        boolean isUpdate = warningService.updateWarningCache(data);

        return new ResponseEntity<>(null, isUpdate ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    /**
     * Download danh sách bản tin bị cảnh báo.
     *
     * @param warningType Kiểu cảnh báo.
     * @param fromDate Thời gian bắt đầu.
     * @param toDate Thời gian kết thúc.
     * @param deviceId ID thiết bị.
     * @return Data download.
     * @throws Exception
     */
    @GetMapping ("/download")
    public ResponseEntity<Resource> downloadWarningOperation(@RequestParam ("warningType") final String warningType,
        @RequestParam ("fromDate") final String fromDate, @RequestParam ("toDate") final String toDate,
        @RequestParam ("deviceId") final String deviceId, @RequestParam ("customerId") final String customerId,
        @RequestParam ("userName") final String userName) throws Exception {

        // get url image
        User user = userMapper.getUserByUsername(userName);
        String pngImageURL = user.getImg();
        String encodingPrefix = "base64,";
        int contentStartIndex = pngImageURL.indexOf(encodingPrefix) + encodingPrefix.length();
        byte[] imageData = org.apache.commons.codec.binary.Base64
            .decodeBase64(pngImageURL.substring(contentStartIndex));

        // SQL query condition
        int[] duration = new int[2];
        duration = CommonUtils.calculateDataIndex(fromDate, toDate);
        Map<String, Object> condition = new HashMap<>();
        condition.put("deviceId", deviceId);
        condition.put("fromDate", fromDate);
        condition.put("toDate", toDate);

        // device
        Device device = deviceService.getDeviceByDeviceId(condition);

        // danh sách bản tin bị cảnh báo theo warning type
        List<DataLoadFrame1> dataWarning = new ArrayList<>();
        for (int index = duration[1]; index >= duration[0]; index--) {
            String schema = Schema.getSchemas(Integer.parseInt(customerId)) + Constants.DATA.DATA_TABLES
                .get(new MultiKey(Constants.DATA.tables[index], Constants.DATA.MESSAGE.FRAME1));
            condition.put("schema", schema);
            List<DataLoadFrame1> frame1s = dataLoadFrame1Service.getDataLoadWarning(condition);
            if (frame1s != null && frame1s.size() > 0) {
                for (DataLoadFrame1 dataItem : frame1s) {
                    dataWarning.add(dataItem);
                }
            }
            frame1s.clear();
        }

        // time miliseconds
        long miliseconds = new Date().getTime();

        // path folder
        String path = this.folderName + File.separator + miliseconds;

        // clear folder
        // File folderExport = new File(this.folderName);
        // if (folderExport.exists()) {
        // FileUtils.cleanDirectory(folderExport);
        // }

        // tạo excel
        if (dataWarning.size() > 0) {
            if (Integer.parseInt(warningType) != Constants.WarningType.NHIET_DO_TIEP_XUC) {
                createOperationExcel(dataWarning, fromDate, toDate, device, path, imageData);
            } else {
                createTemperatureExcel(dataWarning, fromDate, toDate, device, path, imageData);
            }
            // gửi zip qua client
            String contentType = "application/zip";
            String headerValue = "attachment; filename=" + miliseconds + ".zip";

            Path realPath = Paths.get(path + ".zip");
            Resource resource = null;
            try {
                resource = new UrlResource(realPath.toUri());
            } catch (Exception e) {
                e.printStackTrace();
            }

            return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION)
                .body(resource);
        } else {
            return ResponseEntity.internalServerError()
                .body(null);
        }
    }

    /**
     * Tạo excel thông tin vận hành bị cảnh báo.
     *
     * @param data Danh sách bản tin bị cảnh báo vận hành.
     * @throws Exception
     */
    // CHECKSTYLE:OFF
    private void createOperationExcel(final List<DataLoadFrame1> data, final String fromDate, final String toDate,
        final Device device, final String path, final byte[] imageData) throws Exception {

        log.info("WarningController.createOperationExcel() start");

        // Export to excel
        SXSSFWorkbook wb = new SXSSFWorkbook(data.size() + 50);
        SXSSFSheet sheet = wb.createSheet("Thông số điện");
        Row row;
        Cell cell;

        // add image
        int pictureIdx = wb.addPicture(imageData, wb.PICTURE_TYPE_PNG);
        SXSSFDrawing drawingImg = sheet.createDrawingPatriarch();
        CreationHelper helper = wb.getCreationHelper();
        ClientAnchor anchorImg = helper.createClientAnchor();
        anchorImg.setAnchorType(AnchorType.MOVE_DONT_RESIZE);

        anchorImg.setCol1(16);
        anchorImg.setCol2(17);
        anchorImg.setRow1(1);
        anchorImg.setRow2(4);

        Picture pict = drawingImg.createPicture(anchorImg, pictureIdx);

        // Page orientation
        sheet.getPrintSetup()
            .setLandscape(false);

        // Page margins
        sheet.setMargin(Sheet.RightMargin, 0.5);
        sheet.setMargin(Sheet.LeftMargin, 0.5);
        sheet.setMargin(Sheet.TopMargin, 0.5);
        sheet.setMargin(Sheet.BottomMargin, 0.5);

        // Tạo sheet content
        for (int i = 0; i < 7; i++) {
            row = sheet.createRow(i);
            for (int j = 0; j < 36; j++) {
                row.createCell(j);
            }
        }

        // set độ rộng của cột
        sheet.setColumnWidth(0, 1300);
        sheet.setColumnWidth(1, 5200);
        sheet.setColumnWidth(4, 4000);
        sheet.setColumnWidth(16, 4000);

        // set độ rộng của hàng
        Row row1 = sheet.getRow(1);
        row1.setHeight((short) -500);
        Row row2 = sheet.getRow(2);
        row2.setHeight((short) -500);
        Row row3 = sheet.getRow(3);
        row3.setHeight((short) -500);

        CellRangeAddress region = new CellRangeAddress(0, 0, 0, 16);
        sheet.addMergedRegion(region);
        cell = sheet.getRow(0)
            .getCell(0);
        cell.setCellValue("BÁO CÁO VẬN HÀNH");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(2, 2, 0, 1);
        sheet.addMergedRegion(region);
        cell = sheet.getRow(2)
            .getCell(0);
        cell.setCellValue("Mã thiết bị");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.LEFT, 1,
            true);

        region = new CellRangeAddress(2, 2, 2, 8);
        sheet.addMergedRegion(region);
        cell = sheet.getRow(2)
            .getCell(2);
        cell.setCellValue(device.getDeviceId() != null ? String.valueOf(device.getDeviceId()) : "-");
        formatHeader(wb, region, sheet, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1, true);

        region = new CellRangeAddress(3, 3, 0, 1);
        sheet.addMergedRegion(region);
        cell = sheet.getRow(3)
            .getCell(0);
        cell.setCellValue("Tên thiết bị");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.LEFT, 1,
            true);

        region = new CellRangeAddress(3, 3, 2, 8);
        sheet.addMergedRegion(region);
        cell = sheet.getRow(3)
            .getCell(2);
        cell.setCellValue(device.getDeviceName() != null
            ? device.getDeviceName()
                .toUpperCase()
            : "-");
        formatHeader(wb, region, sheet, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1, true);

        region = new CellRangeAddress(2, 3, 9, 10);
        sheet.addMergedRegion(region);
        cell = sheet.getRow(2)
            .getCell(11);
        cell.setCellValue("Thời gian");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(2, 2, 11, 15);
        sheet.addMergedRegion(region);
        cell = sheet.getRow(2)
            .getCell(13);
        cell.setCellValue(fromDate);
        formatHeader(wb, region, sheet, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0, true);

        region = new CellRangeAddress(3, 3, 11, 15);
        sheet.addMergedRegion(region);
        cell = sheet.getRow(3)
            .getCell(13);
        cell.setCellValue(toDate);
        formatHeader(wb, region, sheet, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0, true);

        // bảng thông số vận hành
        region = new CellRangeAddress(5, 5, 0, 0);
        cell = sheet.getRow(5)
            .getCell(0);
        cell.setCellValue("TT");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 1, 1);
        cell = sheet.getRow(5)
            .getCell(1);
        cell.setCellValue("Thời gian");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 2, 2);
        cell = sheet.getRow(5)
            .getCell(2);
        cell.setCellValue("Pha");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 3, 3);
        cell = sheet.getRow(5)
            .getCell(3);
        cell.setCellValue("Điện áp [V]");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 4, 4);
        cell = sheet.getRow(5)
            .getCell(4);
        cell.setCellValue("Dòng điện [A]");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 5, 5);
        cell = sheet.getRow(5)
            .getCell(5);
        cell.setCellValue("%");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 6, 6);
        cell = sheet.getRow(5)
            .getCell(6);
        cell.setCellValue("P [W]");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 7, 7);
        cell = sheet.getRow(5)
            .getCell(7);
        cell.setCellValue("Q [VAr]");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 8, 8);
        cell = sheet.getRow(5)
            .getCell(8);
        cell.setCellValue("S [VA]");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 9, 9);
        cell = sheet.getRow(5)
            .getCell(9);
        cell.setCellValue("PF");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 10, 10);
        cell = sheet.getRow(5)
            .getCell(10);
        cell.setCellValue("THD U [%]");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 11, 11);
        cell = sheet.getRow(5)
            .getCell(11);
        cell.setCellValue("THD I [%]");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 12, 12);
        cell = sheet.getRow(5)
            .getCell(12);
        cell.setCellValue("Phase U");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 13, 13);
        cell = sheet.getRow(5)
            .getCell(13);
        cell.setCellValue("f [Hz]");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 14, 14);
        cell = sheet.getRow(5)
            .getCell(14);
        cell.setCellValue("VU [%]");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 15, 15);
        cell = sheet.getRow(5)
            .getCell(15);
        cell.setCellValue("IU [%]");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 16, 16);
        cell = sheet.getRow(5)
            .getCell(16);
        cell.setCellValue("Active Energy [kWh]");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        // each data.
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        int index = 6;
        for (int m = 0; m < data.size(); m++) {
            DataLoadFrame1 item = data.get(m);
            for (int i = index; i < index + 3; i++) {
                row = sheet.createRow(i);
                for (int j = 0; j < 36; j++) {
                    row.createCell(j);
                }
            }

            // thứ tự
            region = new CellRangeAddress(index, index, 0, 0);
            cell = sheet.getRow(index)
                .getCell(0);
            cell.setCellValue(m + 1);

            // Cột thời gian
            region = new CellRangeAddress(index, index, 1, 1);
            cell = sheet.getRow(index)
                .getCell(1);
            cell.setCellValue(sdf.format(sdf.parse(item.getSentDate())));

            // Cột Pha
            region = new CellRangeAddress(index, index, 2, 2);
            cell = sheet.getRow(index)
                .getCell(2);
            cell.setCellValue("A");

            region = new CellRangeAddress(index + 1, index + 1, 2, 2);
            cell = sheet.getRow(index + 1)
                .getCell(2);
            cell.setCellValue("B");

            region = new CellRangeAddress(index + 2, index + 2, 2, 2);
            cell = sheet.getRow(index + 2)
                .getCell(2);
            cell.setCellValue("C");

            // cột điện áp
            region = new CellRangeAddress(index, index, 3, 3);
            cell = sheet.getRow(index)
                .getCell(3);
            cell.setCellValue(item.getUan() != null ? String.valueOf(item.getUan()) : "-");

            region = new CellRangeAddress(index + 1, index + 1, 3, 3);
            cell = sheet.getRow(index + 1)
                .getCell(3);
            cell.setCellValue(item.getUbn() != null ? String.valueOf(item.getUbn()) : "-");

            region = new CellRangeAddress(index + 2, index + 2, 3, 3);
            cell = sheet.getRow(index + 2)
                .getCell(3);
            cell.setCellValue(item.getUcn() != null ? String.valueOf(item.getUcn()) : "-");

            // cột dòng điện
            region = new CellRangeAddress(index, index, 4, 4);
            cell = sheet.getRow(index)
                .getCell(4);
            cell.setCellValue(item.getIa() != null ? String.valueOf(item.getIa()) : "-");

            region = new CellRangeAddress(index + 1, index + 1, 4, 4);
            cell = sheet.getRow(index + 1)
                .getCell(4);
            cell.setCellValue(item.getIb() != null ? String.valueOf(item.getIb()) : "-");

            region = new CellRangeAddress(index + 2, index + 2, 4, 4);
            cell = sheet.getRow(index + 2)
                .getCell(4);
            cell.setCellValue(item.getIc() != null ? String.valueOf(item.getIc()) : "-");

            // cột %
            region = new CellRangeAddress(index, index, 5, 5);
            cell = sheet.getRow(index)
                .getCell(5);
            cell.setCellValue("-");

            region = new CellRangeAddress(index + 1, index + 1, 5, 5);
            cell = sheet.getRow(index + 1)
                .getCell(5);
            cell.setCellValue("-");

            region = new CellRangeAddress(index + 2, index + 2, 5, 5);
            cell = sheet.getRow(index + 2)
                .getCell(5);
            cell.setCellValue("-");

            // cột P
            region = new CellRangeAddress(index, index, 6, 6);
            cell = sheet.getRow(index)
                .getCell(6);
            cell.setCellValue(item.getPa() != null ? String.valueOf(item.getPa()) : "-");

            region = new CellRangeAddress(index + 1, index + 1, 6, 6);
            cell = sheet.getRow(index + 1)
                .getCell(6);
            cell.setCellValue(item.getPb() != null ? String.valueOf(item.getPb()) : "-");

            region = new CellRangeAddress(index + 2, index + 2, 6, 6);
            cell = sheet.getRow(index + 2)
                .getCell(6);
            cell.setCellValue(item.getPc() != null ? String.valueOf(item.getPc()) : "-");

            // cột Q
            region = new CellRangeAddress(index, index, 7, 7);
            cell = sheet.getRow(index)
                .getCell(7);
            cell.setCellValue(item.getQa() != null ? String.valueOf(item.getQa()) : "-");

            region = new CellRangeAddress(index + 1, index + 1, 7, 7);
            cell = sheet.getRow(index + 1)
                .getCell(7);
            cell.setCellValue(item.getQb() != null ? String.valueOf(item.getQb()) : "-");

            region = new CellRangeAddress(index + 2, index + 2, 7, 7);
            cell = sheet.getRow(index + 2)
                .getCell(7);
            cell.setCellValue(item.getQc() != null ? String.valueOf(item.getQc()) : "-");

            // cột S
            region = new CellRangeAddress(index, index, 8, 8);
            cell = sheet.getRow(index)
                .getCell(8);
            cell.setCellValue(item.getSa() != null ? String.valueOf(item.getSa()) : "-");

            region = new CellRangeAddress(index + 1, index + 1, 8, 8);
            cell = sheet.getRow(index + 1)
                .getCell(8);
            cell.setCellValue(item.getSb() != null ? String.valueOf(item.getSb()) : "-");

            region = new CellRangeAddress(index + 2, index + 2, 8, 8);
            cell = sheet.getRow(index + 2)
                .getCell(8);
            cell.setCellValue(item.getSc() != null ? String.valueOf(item.getSc()) : "-");

            // cột PF
            region = new CellRangeAddress(index, index, 9, 9);
            cell = sheet.getRow(index)
                .getCell(9);
            cell.setCellValue(item.getPfa() != null ? String.valueOf(item.getPfa()) : "-");

            region = new CellRangeAddress(index + 1, index + 1, 9, 9);
            cell = sheet.getRow(index + 1)
                .getCell(9);
            cell.setCellValue(item.getPfb() != null ? String.valueOf(item.getPfb()) : "-");

            region = new CellRangeAddress(index + 2, index + 2, 9, 9);
            cell = sheet.getRow(index + 2)
                .getCell(9);
            cell.setCellValue(item.getPfc() != null ? String.valueOf(item.getPfc()) : "-");

            // cột THD U
            region = new CellRangeAddress(index, index, 10, 10);
            cell = sheet.getRow(index)
                .getCell(10);
            cell.setCellValue(item.getThdVab() != null ? String.valueOf(item.getThdVab()) : "-");

            region = new CellRangeAddress(index + 1, index + 1, 10, 10);
            cell = sheet.getRow(index + 1)
                .getCell(10);
            cell.setCellValue(item.getThdVbc() != null ? String.valueOf(item.getThdVbc()) : "-");

            region = new CellRangeAddress(index + 2, index + 2, 10, 10);
            cell = sheet.getRow(index + 2)
                .getCell(10);
            cell.setCellValue(item.getThdVca() != null ? String.valueOf(item.getThdVca()) : "-");

            // cột THD I
            region = new CellRangeAddress(index, index, 11, 11);
            cell = sheet.getRow(index)
                .getCell(11);
            cell.setCellValue(item.getThdIa() != null ? String.valueOf(item.getThdIa()) : "-");

            region = new CellRangeAddress(index + 1, index + 1, 11, 11);
            cell = sheet.getRow(index + 1)
                .getCell(11);
            cell.setCellValue(item.getThdIb() != null ? String.valueOf(item.getThdIb()) : "-");

            region = new CellRangeAddress(index + 2, index + 2, 11, 11);
            cell = sheet.getRow(index + 2)
                .getCell(11);
            cell.setCellValue(item.getThdIc() != null ? String.valueOf(item.getThdIc()) : "-");

            // cột Phase U
            region = new CellRangeAddress(index, index, 12, 12);
            cell = sheet.getRow(index)
                .getCell(12);
            cell.setCellValue("-");

            region = new CellRangeAddress(index + 1, index + 1, 12, 12);
            cell = sheet.getRow(index + 1)
                .getCell(12);
            cell.setCellValue("-");

            region = new CellRangeAddress(index + 2, index + 2, 12, 12);
            cell = sheet.getRow(index + 2)
                .getCell(12);
            cell.setCellValue("-");

            // cột f
            region = new CellRangeAddress(index, index, 13, 13);
            cell = sheet.getRow(index)
                .getCell(13);
            cell.setCellValue(item.getF() != null ? String.valueOf(item.getF()) : "-");

            // cột VU
            region = new CellRangeAddress(index, index, 14, 14);
            cell = sheet.getRow(index)
                .getCell(14);
            cell.setCellValue("-");

            // cột IU
            region = new CellRangeAddress(index, index, 15, 15);
            cell = sheet.getRow(index)
                .getCell(15);
            cell.setCellValue("-");

            // cột Active Energy
            region = new CellRangeAddress(index, index, 16, 16);
            cell = sheet.getRow(index)
                .getCell(16);
            cell.setCellValue(item.getEp() != null ? String.valueOf(item.getEp()) : "-");

            index += 3;
        }

        // access folder export excel
        File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        // tạo file excel vào folder export
        String exportFilePath = path + File.separator + "CanhBaoVanHanh.xlsx";

        File file = new File(exportFilePath);

        FileOutputStream outFile = null;

        try {
            outFile = new FileOutputStream(file);
            log.info("WarningController: Create file excel success");
        } catch (FileNotFoundException e) {
            log.error("WarningController: ERROR File Not Found while export file excel.");
        } finally {
            try {
                wb.write(outFile);
                outFile.close();
                wb.dispose();
                wb.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // zip folder
        ZipUtil.pack(folder, new File(path + ".zip"));

        log.info("WarningController.createOperationExcel() end.");
    }
    // CHECKSTYLE:ON

    /**
     * Tạo excel thông tin nhiệt độ bị cảnh báo.
     */
    // CHECKSTYLE:OFF
    private void createTemperatureExcel(final List<DataLoadFrame1> data, final String fromDate, final String toDate,
        final Device device, final String path, final byte[] imageData) throws Exception {
        log.info("WarningController.createTemperatureExcel() start");

        // Export to excel
        SXSSFWorkbook wb = new SXSSFWorkbook(data.size() + 50);
        SXSSFSheet sheet = wb.createSheet("Thông số nhiệt độ");
        Row row;
        Cell cell;

        // add image
        int pictureIdx = wb.addPicture(imageData, wb.PICTURE_TYPE_PNG);
        SXSSFDrawing drawingImg = sheet.createDrawingPatriarch();
        CreationHelper helper = wb.getCreationHelper();
        ClientAnchor anchorImg = helper.createClientAnchor();
        anchorImg.setAnchorType(AnchorType.MOVE_DONT_RESIZE);

        anchorImg.setCol1(6);
        anchorImg.setCol2(8);
        anchorImg.setRow1(0);
        anchorImg.setRow2(4);

        Picture pict = drawingImg.createPicture(anchorImg, pictureIdx);

        // Tạo sheet content
        for (int i = 0; i < 7; i++) {
            row = sheet.createRow(i);
            for (int j = 0; j < 36; j++) {
                row.createCell(j);
            }
        }

        sheet.setColumnWidth(0, 1800);
        sheet.setColumnWidth(1, 5200);
        sheet.setColumnWidth(3, 5200);
        sheet.setColumnWidth(4, 5200);
        sheet.setColumnWidth(5, 5200);

        CellRangeAddress region = new CellRangeAddress(0, 0, 0, 5);
        sheet.addMergedRegion(region);
        cell = sheet.getRow(0)
            .getCell(0);
        cell.setCellValue("BÁO CÁO NHIỆT ĐỘ");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(2, 2, 0, 1);
        sheet.addMergedRegion(region);
        cell = sheet.getRow(2)
            .getCell(0);
        cell.setCellValue("Mã thiết bị");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.LEFT, 1,
            true);

        region = new CellRangeAddress(2, 2, 2, 3);
        sheet.addMergedRegion(region);
        cell = sheet.getRow(2)
            .getCell(2);
        cell.setCellValue(device.getDeviceId());
        formatHeader(wb, region, sheet, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1, true);

        region = new CellRangeAddress(3, 3, 0, 1);
        sheet.addMergedRegion(region);
        cell = sheet.getRow(3)
            .getCell(0);
        cell.setCellValue("Tên thiết bị");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.LEFT, 1,
            true);

        region = new CellRangeAddress(3, 3, 2, 3);
        cell = sheet.getRow(3)
            .getCell(2);
        cell.setCellValue(device != null && device.getDeviceName() != null
            ? device.getDeviceName()
                .toUpperCase()
            : "");
        formatHeader(wb, region, sheet, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1, true);

        region = new CellRangeAddress(2, 3, 4, 4);
        sheet.addMergedRegion(region);
        cell = sheet.getRow(2)
            .getCell(4);
        cell.setCellValue("Thời gian");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(2, 2, 5, 5);
        cell = sheet.getRow(2)
            .getCell(5);
        cell.setCellValue(fromDate);
        formatHeader(wb, region, sheet, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0, true);

        region = new CellRangeAddress(3, 3, 5, 5);
        cell = sheet.getRow(3)
            .getCell(5);
        cell.setCellValue(toDate);
        formatHeader(wb, region, sheet, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0, true);

        // bảng thông số nhiệt độ
        region = new CellRangeAddress(5, 5, 0, 0);
        cell = sheet.getRow(5)
            .getCell(0);
        cell.setCellValue("TT");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 1, 1);
        cell = sheet.getRow(5)
            .getCell(1);
        cell.setCellValue("Thời gian");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 2, 2);
        cell = sheet.getRow(5)
            .getCell(2);
        cell.setCellValue("Pha");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 3, 3);
        cell = sheet.getRow(5)
            .getCell(3);
        cell.setCellValue("Vị trí 1");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 4, 4);
        cell = sheet.getRow(5)
            .getCell(4);
        cell.setCellValue("Vị trí 2");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 5, 5);
        cell = sheet.getRow(5)
            .getCell(5);
        cell.setCellValue("Vị trí 3");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        // each data.
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        int index = 6;
        for (int m = 0; m < data.size(); m++) {
            DataLoadFrame1 item = data.get(m);
            for (int i = index; i < index + 3; i++) {
                row = sheet.createRow(i);
                for (int j = 0; j < 36; j++) {
                    row.createCell(j);
                }
            }

            // Cột thứ tự
            region = new CellRangeAddress(index, index + 2, 0, 0);
            cell = sheet.getRow(index)
                .getCell(0);
            cell.setCellValue(m + 1);

            // Cột Thời gian
            region = new CellRangeAddress(index, index + 2, 1, 1);
            cell = sheet.getRow(index)
                .getCell(1);
            cell.setCellValue(sdf.format(sdf.parse(item.getSentDate())));

            // Cột Pha
            region = new CellRangeAddress(index, index, 2, 2);
            cell = sheet.getRow(index)
                .getCell(2);
            cell.setCellValue("A");

            region = new CellRangeAddress(index + 1, index + 1, 2, 2);
            cell = sheet.getRow(index + 1)
                .getCell(2);
            cell.setCellValue("B");

            region = new CellRangeAddress(index + 2, index + 2, 2, 2);
            cell = sheet.getRow(index + 2)
                .getCell(2);
            cell.setCellValue("C");

            // Cột Vị trí 1
            region = new CellRangeAddress(index, index, 3, 3);
            cell = sheet.getRow(index)
                .getCell(3);
            cell.setCellValue(item.getT1() != null ? String.valueOf(item.getT1()) : "-");

            region = new CellRangeAddress(index + 1, index + 1, 3, 3);
            cell = sheet.getRow(index + 1)
                .getCell(3);
            cell.setCellValue(item.getT2() != null ? String.valueOf(item.getT2()) : "-");

            region = new CellRangeAddress(index + 2, index + 2, 3, 3);
            cell = sheet.getRow(index + 2)
                .getCell(3);
            cell.setCellValue(item.getT3() != null ? String.valueOf(item.getT3()) : "-");

            // Cột Vị trí 2
            region = new CellRangeAddress(index, index, 4, 4);
            cell = sheet.getRow(index)
                .getCell(4);
            cell.setCellValue("-");

            region = new CellRangeAddress(index + 1, index + 1, 4, 4);
            cell = sheet.getRow(index + 1)
                .getCell(4);
            cell.setCellValue("-");

            region = new CellRangeAddress(index + 2, index + 2, 4, 4);
            cell = sheet.getRow(index + 2)
                .getCell(4);
            cell.setCellValue("-");

            // Cột Vị trí 3
            region = new CellRangeAddress(index, index, 5, 5);
            cell = sheet.getRow(index)
                .getCell(5);
            cell.setCellValue("-");

            region = new CellRangeAddress(index + 1, index + 1, 5, 5);
            cell = sheet.getRow(index + 1)
                .getCell(5);
            cell.setCellValue("-");

            region = new CellRangeAddress(index + 2, index + 2, 5, 5);
            cell = sheet.getRow(index + 2)
                .getCell(5);
            cell.setCellValue("-");

            index += 3;
        }

        // access folder export excel
        File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        // tạo file excel vào folder export
        String exportFilePath = path + File.separator + "CanhBaoNhietDo.xlsx";

        File file = new File(exportFilePath);

        FileOutputStream outFile = null;

        try {
            outFile = new FileOutputStream(file);
            log.info("WarningController: Create file excel success");
        } catch (FileNotFoundException e) {
            log.error("WarningController: ERROR File Not Found while export file excel.");
        } finally {
            try {
                wb.write(outFile);
                outFile.close();
                wb.dispose();
                wb.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // zip folder
        ZipUtil.pack(folder, new File(path + ".zip"));

        log.info("WarningController.createTemperatureExcel() end");
    }
    // CHECKSTYLE:ON

    /**
     * Format Header.
     */
    // CHECKSTYLE:OFF
    private void formatHeader(final SXSSFWorkbook wb, final CellRangeAddress region, final Sheet sheet, final Cell cell,
        final short bgColor, final HorizontalAlignment hAlign, final int indent, boolean isFontBold) {

        CellStyle cs = wb.createCellStyle();
        cs.setFillBackgroundColor(bgColor);
        cs.setFillForegroundColor(bgColor);
        cs.setFillPattern(FillPatternType.THICK_BACKWARD_DIAG);

        Font font = wb.createFont();
        font.setBold(isFontBold);
        cs.setFont(font);

        cs.setAlignment(hAlign);
        cs.setVerticalAlignment(VerticalAlignment.CENTER);
        cs.setIndention((short) indent);
        cs.setWrapText(true);
        cell.setCellStyle(cs);

        RegionUtil.setBorderTop(BorderStyle.THIN, region, sheet);
        RegionUtil.setBorderRight(BorderStyle.THIN, region, sheet);
        RegionUtil.setBorderBottom(BorderStyle.THIN, region, sheet);
        RegionUtil.setBorderLeft(BorderStyle.THIN, region, sheet);
    }

    private void formatStyle(final SXSSFWorkbook wb, final CellRangeAddress region, final Sheet sheet,
        final Cell cell) {
        // cell style
        CellStyle cs = wb.createCellStyle();

        cs.setAlignment(HorizontalAlignment.CENTER);
        cs.setVerticalAlignment(VerticalAlignment.CENTER);
        cell.setCellStyle(cs);

        RegionUtil.setBorderTop(BorderStyle.THIN, region, sheet);
        RegionUtil.setBorderRight(BorderStyle.THIN, region, sheet);
        RegionUtil.setBorderBottom(BorderStyle.THIN, region, sheet);
        RegionUtil.setBorderLeft(BorderStyle.THIN, region, sheet);
    }
    // CHECKSTYLE:ON
}
