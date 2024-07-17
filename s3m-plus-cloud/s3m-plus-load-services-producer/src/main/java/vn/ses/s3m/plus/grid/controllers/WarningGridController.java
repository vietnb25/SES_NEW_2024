package vn.ses.s3m.plus.grid.controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.Year;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import vn.ses.s3m.plus.common.Constants;
import vn.ses.s3m.plus.common.Schema;
import vn.ses.s3m.plus.controllers.WarningController;
import vn.ses.s3m.plus.dao.UserMapper;
import vn.ses.s3m.plus.dto.DataRmuDrawer1;
import vn.ses.s3m.plus.dto.Device;
import vn.ses.s3m.plus.dto.Setting;
import vn.ses.s3m.plus.dto.User;
import vn.ses.s3m.plus.dto.Warning;
import vn.ses.s3m.plus.form.UpdateWarningForm;
import vn.ses.s3m.plus.grid.service.OperationRmuDrawerService;
import vn.ses.s3m.plus.grid.service.WarningGridService;
import vn.ses.s3m.plus.service.DeviceService;
import vn.ses.s3m.plus.service.SettingService;

@RestController
@RequestMapping ("/grid/warning")
public class WarningGridController {

    // khai báo tham số
    private static final Integer PAGE_SIZE = 20;

    private static final String SCHEMA = "schema";

    private static final String PROJECT_ID = "projectId";

    private static final String DEVICE_ID = "deviceId";

    private static final String WARNING_TYPE = "warningType";

    private static final Integer TYPE_GRID = 5;

    private static final Integer TYPE_DEVICE_RMU = 1;

    // Chưa định nghĩa trên tài liệu
    private static final Integer TYPE_DEVICE_STMV = 0;

    // Chưa định nghĩa trên tài liệu
    private static final Integer TYPE_DEVICE_SGMV = 0;

    /** Logging */
    private final Log log = LogFactory.getLog(WarningController.class);

    @Autowired
    private WarningGridService warningGridService;

    @Autowired
    private OperationRmuDrawerService operationRmuDrawerService;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private SettingService settingService;

    @Autowired
    private UserMapper userMapper;

    @Value ("${grid.producer.export-folder}")
    private String folderName;

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
        @RequestParam ("toDate") final String toDate, @RequestParam ("customerId") final Integer customerId,
        @RequestParam ("projectId") final String projectId) {

        String schema = Schema.getSchemas(customerId);
        Map<String, Object> condition = new HashMap<>();
        condition.put(SCHEMA, schema);
        condition.put(PROJECT_ID, projectId);
        condition.put("fromDate", fromDate + " 00:00:00");
        condition.put("toDate", toDate + " 23:59:59");
        condition.put("type", TYPE_GRID);
        // list warning
        List<Warning> warnings = warningGridService.getTotalWarningGrid(condition);
        // totalDeviceHasWarning
        Integer devicesWarning = warningGridService.getAllDeviceHasWarningGrid(condition);
        // tổng cảnh báo
        Map<String, Long> warningMap = new HashMap<>();

        warningMap.put("devicesWarning", (long) devicesWarning);

        // START: Khai báo các biến cảnh báo RMU
        long dienApCao = 0;
        long dienApThap = 0;

        /* Nhiệt độ gồm: nhiệt độ tiếp xúc khoang tủ RMU, nhiệt độ khoang tủ RMU */
        long nhietDoCao = 0;

        long heSoCongSuatThap = 0;
        long quaTaiTong = 0;
        long tanSoThap = 0;
        long tanSoCao = 0;
        long matDienNhanh = 0;
        long lechPha = 0;
        long songHai = 0;
        long phongDien = 0;
        long doAm = 0;

        // END: khai báo
        for (Warning warning : warnings) {
            Integer warningType = warning.getWarningType();
            if (warning.getDeviceType() == TYPE_DEVICE_RMU)
                switch (warningType) {
                    case Constants.WARNING_RMU.NHIET_DO:
                        nhietDoCao = nhietDoCao + warning.getTotalDevice();
                        break;
                    case Constants.WARNING_RMU.DIEN_AP_CAO:
                        dienApCao = dienApCao + warning.getTotalDevice();
                        break;
                    case Constants.WARNING_RMU.DIEN_AP_THAP:
                        dienApThap = dienApThap + warning.getTotalDevice();
                        break;
                    case Constants.WARNING_RMU.QUA_TAI_TONG:
                        quaTaiTong = quaTaiTong + warning.getTotalDevice();
                        break;
                    case Constants.WARNING_RMU.TAN_SO_THAP:
                        tanSoThap = tanSoThap + warning.getTotalDevice();
                        break;
                    case Constants.WARNING_RMU.TAN_SO_CAO:
                        tanSoCao = tanSoCao + warning.getTotalDevice();
                        break;
                    case Constants.WARNING_RMU.LECH_PHA_NHANH:
                        lechPha = lechPha + warning.getTotalDevice();
                        break;
                    case Constants.WARNING_RMU.SONG_HAI:
                        songHai = songHai + warning.getTotalDevice();
                        break;
                    case Constants.WARNING_RMU.PHONG_DIEN:
                        phongDien = phongDien + warning.getTotalDevice();
                        break;
                    case Constants.WARNING_RMU.DO_AM:
                        doAm = doAm + warning.getTotalDevice();
                        break;
                    default:
                        break;
                }
            else if (warning.getDeviceType() == TYPE_DEVICE_STMV) {
                switch (warningType) {
                    default:
                        break;
                }
            } else if (warning.getDeviceType() == TYPE_DEVICE_SGMV) {
                switch (warningType) {
                    default:
                        break;
                }
            }
            warningMap.put("nhietDoCao", nhietDoCao);
            warningMap.put("dienApCao", dienApCao);
            warningMap.put("dienApThap", dienApThap);
            warningMap.put("heSoCongSuatThap", heSoCongSuatThap);
            warningMap.put("quaTaiTong", quaTaiTong);
            warningMap.put("tanSoThap", tanSoThap);
            warningMap.put("tanSoCao", tanSoCao);
            warningMap.put("matDienNhanh", matDienNhanh);
            warningMap.put("lechPha", lechPha);
            warningMap.put("songHai", songHai);
            warningMap.put("phongDien", phongDien);
            warningMap.put("doAm", doAm);
        }
        return new ResponseEntity<>(warningMap, HttpStatus.OK);
    }

    /**
     * Lấy danh sách thiết bị cảnh báo theo id dự án và warning_type.
     *
     * @param warningType Kiểu cảnh báo.
     * @param fromDate Thời gian bắt đầu.
     * @param toDate Thời gian kết thúc.
     * @param customerId ID của khách hàng.
     * @param projectId ID của dự án.
     * @param page Page muốn hiển thị dữ liệu.
     * @return Danh sách chi tiết của cảnh báo theo warning type
     */
    @GetMapping ("/type/{warningType}")
    public ResponseEntity<?> detailWarningByType(@PathVariable ("warningType") final String warningType,
        @RequestParam ("fromDate") final String fromDate, @RequestParam ("toDate") final String toDate,
        @RequestParam ("customerId") final Integer customerId, @RequestParam ("projectId") final String projectId,
        @RequestParam ("page") final Integer page) {

        String schema = Schema.getSchemas(customerId);
        Map<String, Object> condition = new HashMap<>();

        List<Warning> warningRMU = new ArrayList<>();
        List<Warning> warningSTMV = new ArrayList<>();
        List<Warning> warningSGMV = new ArrayList<>();
        List<Warning> warnings = new ArrayList<>();

        condition.put("fromDate", fromDate + " 00:00:00");
        condition.put("toDate", toDate + " 23:59:59");
        condition.put(SCHEMA, schema);
        condition.put(PROJECT_ID, projectId);
        condition.put("type", TYPE_GRID);

        condition.put("offset", (page - 1) * PAGE_SIZE);
        condition.put("pageSize", PAGE_SIZE);

        int currentYear = Year.now()
            .getValue();
        condition.put("year", Constants.ES.UNDERSCORE_CHARACTER + currentYear);

        if (!warningType.equals("ALL")) {
            condition.put(WARNING_TYPE, warningType);
            warningRMU = warningGridService.getDetailWarningType(condition);
            // warningSTMV = warningGridService.getDetailWarningTypeSTMV(condition);
            // warningSGMV = warningGridService.getDetailWarningTypeSGMV(condition);
            List<Warning> countList = warningGridService.countWarningRMU(condition);

            for (Warning item : warningRMU) {
                for (Warning count : countList) {
                    if (item.getDeviceId()
                        .equals(count.getDeviceId())
                        && item.getWarningType()
                            .equals(count.getWarningType())
                        && item.getFromDate()
                            .equals(count.getFromDate())) {
                        item.setTotal(count.getTotal());
                        break;
                    }
                }
            }
            //
        } else {
            warningRMU = warningGridService.getDetailWarningType(condition);
            // warningSTMV = warningGridService.getDetailWarningTypeSTMV(condition);
            // warningSGMV = warningGridService.getDetailWarningTypeSGMV(condition);
            List<Warning> countList = warningGridService.countWarningRMU(condition);

            for (Warning item : warningRMU) {
                for (Warning count : countList) {
                    if (item.getDeviceId()
                        .equals(count.getDeviceId())
                        && item.getWarningType()
                            .equals(count.getWarningType())
                        && item.getFromDate()
                            .equals(count.getFromDate())) {
                        item.setTotal(count.getTotal());
                        break;
                    }
                }
            }
        }

        warnings.addAll(warningRMU);
        warnings.addAll(warningSTMV);
        warnings.addAll(warningSGMV);

        Collections.sort(warnings, new Comparator<Warning>() {

            @Override
            public int compare(Warning o1, Warning o2) {
                // TODO Auto-generated method stub
                if (o1.getFromDate()
                    .compareTo(o2.getFromDate()) < 0) {
                    return 1;
                } else if (o1.getFromDate()
                    .compareTo(o2.getFromDate()) > 0) {
                    return -1;
                }
                return 0;
            }
        });

        List<Warning> totalData = warningGridService.getListWarningByWarningType(condition);
        double totalPage = Math.ceil((double) totalData.size() / PAGE_SIZE);
        Map<String, Object> mapData = new HashMap<>();
        mapData.put("totalPage", totalPage);
        mapData.put("currentPage", page);
        mapData.put("data", warnings);

        return new ResponseEntity<>(mapData, HttpStatus.OK);
    }

    /**
     * Lấy thông tin chi tiết cảnh báo.
     *
     * @param warningId Id của cảnh báo.
     * @return Thông tin chi tiết của cảnh báo.
     */
    @GetMapping ("/update/{warningType}/{deviceId}")
    public ResponseEntity<?> updateWarning(@PathVariable ("warningType") String warningType,
        @PathVariable ("deviceId") String deviceId, @RequestParam String fromDate, @RequestParam String toDate,
        @RequestParam Integer customerId) {

        String schema = Schema.getSchemas(customerId);

        Map<String, Object> condition = new HashMap<>();
        condition.put(WARNING_TYPE, warningType);
        condition.put(DEVICE_ID, deviceId);
        condition.put("fromDate", fromDate);
        condition.put("toDate", toDate);
        condition.put(SCHEMA, schema);
        Warning warning = warningGridService.getDetailWarningCacheGrid(condition);
        return new ResponseEntity<>(warning, HttpStatus.OK);
    }

    /**
     * Cập nhật thông tin chi tiết cảnh báo.
     *
     * @param form Data cần cập nhật.
     * @return Trạng thái cập nhật cảnh báo.
     */
    @PostMapping ("/update/{warningId}")
    public ResponseEntity<?> updateWarning(@RequestBody final UpdateWarningForm form) {

        String schema = Schema.getSchemas(form.getCustomerId());

        Map<String, Object> condition = new HashMap<>();
        condition.put(SCHEMA, schema);
        condition.put("status", form.getStatus());
        condition.put("username", form.getUsername());
        condition.put(WARNING_TYPE, form.getWarningType());
        condition.put(DEVICE_ID, form.getDeviceId());
        condition.put("fromDate", form.getFromDate());
        condition.put("toDate", form.getToDate());
        condition.put("description", form.getDescription());

        boolean isUpdate = warningGridService.updateWarningCacheGrid(condition);
        return new ResponseEntity<>(null, isUpdate ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    /**
     * Hiển thị chi tiết các bản tin khi bị cảnh báo theo từng thiết bị.
     *
     * @param warningType Kiểu cảnh báo.
     * @param fromDate Thời gian bắt đầu.
     * @param toDate Thời gian kết thúc.
     * @param deviceId ID thiết bị.
     * @param page Page muốn hiển thị dữ liệu.
     * @return Danh sách chi tiết của cảnh báo theo thiết bị
     */
    @GetMapping ("/detail")
    public ResponseEntity<?> showDataWarningByDevice(@RequestParam ("warningType") final String warningType,
        @RequestParam ("fromDate") final String fromDate, @RequestParam ("toDate") final String toDate,
        @RequestParam ("projectId") final Integer projectId, @RequestParam ("customerId") final Integer customerId,
        @RequestParam ("deviceId") final String deviceId, @RequestParam ("page") final Integer page) {

        String schema = Schema.getSchemas(customerId);

        Map<String, Object> condition = new HashMap<>();
        condition.put(SCHEMA, schema);
        condition.put(DEVICE_ID, deviceId);
        condition.put("fromDate", fromDate);
        condition.put("toDate", toDate);
        condition.put(WARNING_TYPE, warningType);
        condition.put(PROJECT_ID, projectId);

        int currentYear = Year.now()
            .getValue();
        condition.put("year", Constants.ES.UNDERSCORE_CHARACTER + currentYear);

        // tìm thông tin loại thiết bị để xác định cảnh báo
        Device device = deviceService.getDeviceByDeviceId(condition);

        Map<String, Object> mapData = new HashMap<>();

        if (device.getDeviceType() == TYPE_DEVICE_RMU) {
            List<DataRmuDrawer1> dataWarning = operationRmuDrawerService.getDataRmuDrawerGrid(condition);

            mapData.put("dataWarning", dataWarning);
            mapData.put("deviceType", TYPE_DEVICE_RMU);
        } else if (device.getDeviceType() == TYPE_DEVICE_STMV) {

        } else if (device.getDeviceType() == TYPE_DEVICE_SGMV) {
        }
        // Tìm cài đặt của nhiệt độ
        if (Integer.parseInt(warningType) == Constants.WARNING_RMU.NHIET_DO) {
            condition.put("type", TYPE_GRID);
            List<Setting> listSetting = settingService.getSettings(condition);
            String after = "", before = "";
            for (Setting setting : listSetting) {

                if ( (setting.getWarningType() != null) && setting.getWarningType() == Constants.WARNING_RMU.NHIET_DO) {
                    if (setting.getDescription()
                        .contains("SAW_ID1")) {
                        before = setting.getSettingValue();
                    } else {
                        after = setting.getSettingValue();
                    }
                }
            }
            String settingValue = before + "," + after;
            mapData.put("settingValue", settingValue);
        } else {
            String settingValue = settingService.getSettingValue(condition);
            mapData.put("settingValue", settingValue);
        }
        return new ResponseEntity<>(mapData, HttpStatus.OK);
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
        @RequestParam ("customerId") final Integer customerId, @RequestParam ("deviceId") final String deviceId,
        @RequestParam ("userName") final String userName) throws Exception {

        String schema = Schema.getSchemas(customerId);
        // SQL query condition
        Map<String, Object> condition = new HashMap<>();
        condition.put(SCHEMA, schema);
        condition.put(DEVICE_ID, deviceId);
        condition.put("fromDate", fromDate);
        condition.put("toDate", toDate);

        int currentYear = Year.now()
            .getValue();
        condition.put("year", Constants.ES.UNDERSCORE_CHARACTER + currentYear);
        // device
        Device device = deviceService.getDeviceByDeviceId(condition);

        // time miliseconds
        long miliseconds = new Date().getTime();

        // get url image
        User user = userMapper.getUserByUsername(userName);
        String pngImageURL = user.getImg();
        String encodingPrefix = "base64,";
        int contentStartIndex = pngImageURL.indexOf(encodingPrefix) + encodingPrefix.length();
        byte[] imageData = org.apache.commons.codec.binary.Base64
            .decodeBase64(pngImageURL.substring(contentStartIndex));

        // path folder
        String path = this.folderName + File.separator + miliseconds;

        // danh sách bản tin bị cảnh báo theo thiết bị
        if (device.getDeviceType() == TYPE_DEVICE_RMU) {
            List<DataRmuDrawer1> dataWarning = operationRmuDrawerService.getDataRmuDrawerGrid(condition);
            // tạo excel
            if (dataWarning.size() > 0) {
                createOperationExcel(dataWarning, fromDate, toDate, device, path, imageData);
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
                return ResponseEntity.badRequest()
                    .body(null);
            }

        } else if (device.getDeviceType() == TYPE_DEVICE_SGMV) {
            return ResponseEntity.badRequest()
                .body(null);
        } else if (device.getDeviceType() == TYPE_DEVICE_STMV) {
            return ResponseEntity.badRequest()
                .body(null);
        } else {
            return ResponseEntity.badRequest()
                .body(null);
        }
    }

    /**
     * Tạo excel thông tin vận hành bị cảnh báo inverter.
     *
     * @param data Danh sách bản tin bị cảnh báo vận hành.
     * @throws Exception
     */
    // CHECKSTYLE:OFF
    private void createOperationExcel(final List<DataRmuDrawer1> data, final String fromDate, final String toDate,
        final Device device, final String path, final byte[] imageData) throws Exception {

        log.info("WarningController.createOperationExcel() start");

        // Export to excel
        SXSSFWorkbook wb = new SXSSFWorkbook(data.size() + 50);
        SXSSFSheet sheet = wb.createSheet("Thông số");
        Row row;
        Cell cell;

        // Create font
        Font font = sheet.getWorkbook()
            .createFont();
        font.setFontName("Courier New");

        // Create CellStyle
        CellStyle cellStyle = sheet.getWorkbook()
            .createCellStyle();
        cellStyle.setFont(font);

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

        // add image
        int pictureIdx = wb.addPicture(imageData, wb.PICTURE_TYPE_PNG);
        SXSSFDrawing drawingImg = sheet.createDrawingPatriarch();
        CreationHelper helper = wb.getCreationHelper();
        ClientAnchor anchorImg = helper.createClientAnchor();
        anchorImg.setAnchorType(AnchorType.MOVE_DONT_RESIZE);

        anchorImg.setCol1(17);
        anchorImg.setCol2(18);
        anchorImg.setRow1(1);
        anchorImg.setRow2(4);

        Picture pict = drawingImg.createPicture(anchorImg, pictureIdx);

        // set độ rộng của cột
        sheet.setColumnWidth(0, 1300);
        sheet.setColumnWidth(1, 6300);
        sheet.setColumnWidth(3, 5000);
        sheet.setColumnWidth(4, 4000);
        sheet.setColumnWidth(5, 3000);
        sheet.setColumnWidth(6, 3000);
        sheet.setColumnWidth(7, 3000);
        sheet.setColumnWidth(8, 3000);
        sheet.setColumnWidth(9, 3000);
        sheet.setColumnWidth(10, 3000);
        sheet.setColumnWidth(11, 5200);
        sheet.setColumnWidth(12, 5200);
        sheet.setColumnWidth(13, 5200);
        sheet.setColumnWidth(14, 4000);
        sheet.setColumnWidth(15, 4000);
        sheet.setColumnWidth(16, 4000);
        sheet.setColumnWidth(17, 4000);

        // set độ rộng của hàng
        Row row1 = sheet.getRow(1);
        row1.setHeight((short) -500);
        Row row2 = sheet.getRow(2);
        row2.setHeight((short) -500);
        Row row3 = sheet.getRow(3);
        row3.setHeight((short) -500);

        CellRangeAddress region = new CellRangeAddress(0, 0, 0, 17);
        sheet.addMergedRegion(region);
        cell = sheet.getRow(0)
            .getCell(0);
        cell.setCellValue("REPORT GRID");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(2, 2, 0, 1);
        sheet.addMergedRegion(region);
        cell = sheet.getRow(2)
            .getCell(0);
        cell.setCellValue("Device ID");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.LEFT, 1,
            true);

        region = new CellRangeAddress(2, 2, 2, 10);
        sheet.addMergedRegion(region);
        cell = sheet.getRow(2)
            .getCell(2);
        cell.setCellValue(device.getDeviceId() != null ? String.valueOf(device.getDeviceId()) : "-");
        formatHeader(wb, region, sheet, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1, true);

        region = new CellRangeAddress(3, 3, 0, 1);
        sheet.addMergedRegion(region);
        cell = sheet.getRow(3)
            .getCell(0);
        cell.setCellValue("Device Name");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.LEFT, 1,
            true);

        region = new CellRangeAddress(3, 3, 2, 10);
        sheet.addMergedRegion(region);
        cell = sheet.getRow(3)
            .getCell(2);
        cell.setCellValue(device.getDeviceName() != null
            ? device.getDeviceName()
                .toUpperCase()
            : "-");
        formatHeader(wb, region, sheet, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1, true);

        region = new CellRangeAddress(2, 3, 11, 12);
        sheet.addMergedRegion(region);
        cell = sheet.getRow(2)
            .getCell(11);
        cell.setCellValue("Time");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(2, 2, 13, 16);
        sheet.addMergedRegion(region);
        cell = sheet.getRow(2)
            .getCell(13);
        cell.setCellValue(fromDate);
        formatHeader(wb, region, sheet, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0, true);

        region = new CellRangeAddress(3, 3, 13, 16);
        sheet.addMergedRegion(region);
        cell = sheet.getRow(3)
            .getCell(13);
        cell.setCellValue(toDate);
        formatHeader(wb, region, sheet, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0, true);

        // bảng thông số
        region = new CellRangeAddress(5, 5, 0, 0);
        cell = sheet.getRow(5)
            .getCell(0);
        cell.setCellValue("TT");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 1, 1);
        cell = sheet.getRow(5)
            .getCell(1);
        cell.setCellValue("Time");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 2, 2);
        cell = sheet.getRow(5)
            .getCell(2);
        cell.setCellValue("Phase");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 3, 3);
        cell = sheet.getRow(5)
            .getCell(3);
        cell.setCellValue("Voltage [V]");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 4, 4);
        cell = sheet.getRow(5)
            .getCell(4);
        cell.setCellValue("Current [A]");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 5, 5);
        cell = sheet.getRow(5)
            .getCell(5);
        cell.setCellValue("SAW_ID1");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 6, 6);
        cell = sheet.getRow(5)
            .getCell(6);
        cell.setCellValue("SAW_ID2");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 7, 7);
        cell = sheet.getRow(5)
            .getCell(7);
        cell.setCellValue("SAW_ID3");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 8, 8);
        cell = sheet.getRow(5)
            .getCell(8);
        cell.setCellValue("SAW_ID4");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 9, 9);
        cell = sheet.getRow(5)
            .getCell(9);
        cell.setCellValue("SAW_ID5");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 10, 10);
        cell = sheet.getRow(5)
            .getCell(10);
        cell.setCellValue("SAW_ID6");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 11, 11);
        cell = sheet.getRow(5)
            .getCell(11);
        cell.setCellValue("Indicator");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 12, 12);
        cell = sheet.getRow(5)
            .getCell(12);
        cell.setCellValue("Temperature");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 13, 13);
        cell = sheet.getRow(5)
            .getCell(13);
        cell.setCellValue("Humidity");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 14, 14);
        cell = sheet.getRow(5)
            .getCell(14);
        cell.setCellValue("THD Van");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 15, 15);
        cell = sheet.getRow(5)
            .getCell(15);
        cell.setCellValue("THD Vbn");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 16, 16);
        cell = sheet.getRow(5)
            .getCell(16);
        cell.setCellValue("THD Vcn");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 17, 17);
        cell = sheet.getRow(5)
            .getCell(17);
        cell.setCellValue("Frequency");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);
        // each data.
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        int index = 6;
        for (int m = 0; m < data.size(); m++) {
            DataRmuDrawer1 item = data.get(m);
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
            cell.setCellStyle(cellStyle);

            // Cột thời gian
            region = new CellRangeAddress(index, index, 1, 1);
            cell = sheet.getRow(index)
                .getCell(1);
            cell.setCellValue(sdf.format(sdf.parse(item.getSentDate())));
            cell.setCellStyle(cellStyle);

            // Cột Pha
            region = new CellRangeAddress(index, index, 2, 2);
            cell = sheet.getRow(index)
                .getCell(2);
            cell.setCellValue("A");
            cell.setCellStyle(cellStyle);

            region = new CellRangeAddress(index + 1, index + 1, 2, 2);
            cell = sheet.getRow(index + 1)
                .getCell(2);
            cell.setCellValue("B");
            cell.setCellStyle(cellStyle);

            region = new CellRangeAddress(index + 2, index + 2, 2, 2);
            cell = sheet.getRow(index + 2)
                .getCell(2);
            cell.setCellValue("C");
            cell.setCellStyle(cellStyle);

            // cột điện áp
            region = new CellRangeAddress(index, index, 3, 3);
            cell = sheet.getRow(index)
                .getCell(3);
            cell.setCellValue(item.getUan() != null ? String.valueOf(item.getUan()) : "-");
            cell.setCellStyle(cellStyle);

            region = new CellRangeAddress(index + 1, index + 1, 3, 3);
            cell = sheet.getRow(index + 1)
                .getCell(3);
            cell.setCellValue(item.getUbn() != null ? String.valueOf(item.getUbn()) : "-");
            cell.setCellStyle(cellStyle);

            region = new CellRangeAddress(index + 2, index + 2, 3, 3);
            cell = sheet.getRow(index + 2)
                .getCell(3);
            cell.setCellValue(item.getUcn() != null ? String.valueOf(item.getUcn()) : "-");
            cell.setCellStyle(cellStyle);

            // cột dòng điện
            region = new CellRangeAddress(index, index, 4, 4);
            cell = sheet.getRow(index)
                .getCell(4);
            cell.setCellValue(item.getIa() != null ? String.valueOf(item.getIa()) : "-");
            cell.setCellStyle(cellStyle);

            region = new CellRangeAddress(index + 1, index + 1, 4, 4);
            cell = sheet.getRow(index + 1)
                .getCell(4);
            cell.setCellValue(item.getIb() != null ? String.valueOf(item.getIb()) : "-");
            cell.setCellStyle(cellStyle);

            region = new CellRangeAddress(index + 2, index + 2, 4, 4);
            cell = sheet.getRow(index + 2)
                .getCell(4);
            cell.setCellValue(item.getIc() != null ? String.valueOf(item.getIc()) : "-");
            cell.setCellStyle(cellStyle);

            // cột saw_id1
            region = new CellRangeAddress(index, index + 2, 5, 5);
            cell = sheet.getRow(index)
                .getCell(5);
            cell.setCellValue(item.getSawId1() != null ? String.valueOf(item.getSawId1()) : "-");
            cell.setCellStyle(cellStyle);

            // cột saw_id2
            region = new CellRangeAddress(index, index + 2, 6, 6);
            cell = sheet.getRow(index)
                .getCell(6);
            cell.setCellValue(item.getSawId2() != null ? String.valueOf(item.getSawId2()) : "-");
            cell.setCellStyle(cellStyle);

            // cột saw_id3
            region = new CellRangeAddress(index, index + 2, 7, 7);
            cell = sheet.getRow(index)
                .getCell(7);
            cell.setCellValue(item.getSawId3() != null ? String.valueOf(item.getSawId3()) : "-");
            cell.setCellStyle(cellStyle);

            // cột saw_id4
            region = new CellRangeAddress(index, index + 2, 8, 8);
            cell = sheet.getRow(index)
                .getCell(8);
            cell.setCellValue(item.getSawId4() != null ? String.valueOf(item.getSawId4()) : "-");
            cell.setCellStyle(cellStyle);

            // cột saw_id5
            region = new CellRangeAddress(index, index + 2, 9, 9);
            cell = sheet.getRow(index)
                .getCell(9);
            cell.setCellValue(item.getSawId5() != null ? String.valueOf(item.getSawId5()) : "-");
            cell.setCellStyle(cellStyle);

            // cột saw_id6
            region = new CellRangeAddress(index, index + 2, 10, 10);
            cell = sheet.getRow(index)
                .getCell(10);
            cell.setCellValue(item.getSawId6() != null ? String.valueOf(item.getSawId6()) : "-");
            cell.setCellStyle(cellStyle);

            // cột chỉ thị
            region = new CellRangeAddress(index, index + 2, 11, 11);
            cell = sheet.getRow(index)
                .getCell(11);
            cell.setCellValue(item.getIndicator() != null ? String.valueOf(item.getIndicator()) : "-");
            cell.setCellStyle(cellStyle);

            // cột nhiệt độ
            region = new CellRangeAddress(index, index + 2, 12, 12);
            cell = sheet.getRow(index)
                .getCell(12);
            cell.setCellValue(item.getT() != null ? String.valueOf(item.getT()) : "-");
            cell.setCellStyle(cellStyle);

            // cột độ ẩm
            region = new CellRangeAddress(index, index + 2, 13, 13);
            cell = sheet.getRow(index)
                .getCell(13);
            cell.setCellValue(item.getH() != null ? String.valueOf(item.getH()) : "-");
            cell.setCellStyle(cellStyle);

            // cột THD_Van
            region = new CellRangeAddress(index, index + 2, 14, 14);
            cell = sheet.getRow(index)
                .getCell(14);
            cell.setCellValue(item.getThdVan() != null ? String.valueOf(item.getThdVan()) : "-");
            cell.setCellStyle(cellStyle);

            // cột THD_Vbn
            region = new CellRangeAddress(index, index + 2, 15, 15);
            cell = sheet.getRow(index)
                .getCell(15);
            cell.setCellValue(item.getThdVbn() != null ? String.valueOf(item.getThdVbn()) : "-");
            cell.setCellStyle(cellStyle);

            // cột THD_Vcn
            region = new CellRangeAddress(index, index + 2, 16, 16);
            cell = sheet.getRow(index)
                .getCell(16);
            cell.setCellValue(item.getThdVcn() != null ? String.valueOf(item.getThdVcn()) : "-");
            cell.setCellStyle(cellStyle);

            // cột Tần số
            region = new CellRangeAddress(index, index + 2, 17, 17);
            cell = sheet.getRow(index)
                .getCell(17);
            cell.setCellValue(item.getF() != null ? String.valueOf(item.getF()) : "-");
            cell.setCellStyle(cellStyle);

            index += 3;
        }

        // access folder export excel
        File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        // tạo file excel vào folder export
        String exportFilePath = path + File.separator + "Report-GRID.xlsx";

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
        font.setFontName("Courier New");
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

    /**
     * Lấy danh sách thiết bị cảnh báo theo id thiết bị và warning_type.
     *
     * @param warningType Kiểu cảnh báo.
     * @param fromDate Thời gian bắt đầu.
     * @param toDate Thời gian kết thúc.
     * @param customerId ID của khách hàng.
     * @param projectId ID của dự án.
     * @param page Page muốn hiển thị dữ liệu.
     * @return Danh sách chi tiết của cảnh báo theo warning type
     */
    @GetMapping ("/operation/type/{warningType}")
    public ResponseEntity<?> detailWarningOperationInformationGridByType(@PathVariable final String warningType,
        @RequestParam final String fromDate, @RequestParam final String toDate, @RequestParam final Integer customerId,
        @RequestParam final String deviceId, @RequestParam final Integer page) {

        log.info("WarningGridController.detailWarningOperationInformationGridByType() start");

        String schema = Schema.getSchemas(customerId);
        Map<String, Object> condition = new HashMap<>();

        List<Warning> warningRMU = new ArrayList<>();
        List<Warning> warningSTMV = new ArrayList<>();
        List<Warning> warningSGMV = new ArrayList<>();
        List<Warning> warnings = new ArrayList<>();

        condition.put("fromDate", fromDate + " 00:00:00");
        condition.put("toDate", toDate + " 23:59:59");
        condition.put(SCHEMA, schema);
        condition.put(DEVICE_ID, deviceId);

        condition.put("offset", (page - 1) * PAGE_SIZE);
        condition.put("pageSize", PAGE_SIZE);

        int currentYear = Year.now()
            .getValue();
        condition.put("year", Constants.ES.UNDERSCORE_CHARACTER + currentYear);
        condition.put(WARNING_TYPE, warningType);
        warningRMU = warningGridService.getDetailWarningType(condition);
        // warningSTMV = warningGridService.getDetailWarningTypeSTMV(condition);
        // warningSGMV = warningGridService.getDetailWarningTypeSGMV(condition);

        warnings.addAll(warningRMU);
        warnings.addAll(warningSTMV);
        warnings.addAll(warningSGMV);

        Collections.sort(warnings, new Comparator<Warning>() {

            @Override
            public int compare(Warning o1, Warning o2) {
                // TODO Auto-generated method stub
                if (o1.getFromDate()
                    .compareTo(o2.getFromDate()) < 0) {
                    return 1;
                } else if (o1.getFromDate()
                    .compareTo(o2.getFromDate()) > 0) {
                    return -1;
                }
                return 0;
            }
        });

        List<Warning> totalData = warningGridService.getListWarningByWarningType(condition);
        double totalPage = Math.ceil((double) totalData.size() / PAGE_SIZE);
        Map<String, Object> mapData = new HashMap<>();
        mapData.put("totalPage", totalPage);
        mapData.put("currentPage", page);
        mapData.put("data", warnings);

        log.info("WarningGridController.detailWarningOperationInformationGridByType() end");

        return new ResponseEntity<>(mapData, HttpStatus.OK);
    }

}
