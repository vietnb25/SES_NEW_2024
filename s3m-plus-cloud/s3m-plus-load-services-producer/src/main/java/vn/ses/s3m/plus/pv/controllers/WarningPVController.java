package vn.ses.s3m.plus.pv.controllers;

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
import org.springframework.web.bind.annotation.CrossOrigin;
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
import vn.ses.s3m.plus.dto.DataCombiner1;
import vn.ses.s3m.plus.dto.DataInverter1;
import vn.ses.s3m.plus.dto.DataString1;
import vn.ses.s3m.plus.dto.Device;
import vn.ses.s3m.plus.dto.User;
import vn.ses.s3m.plus.dto.Warning;
import vn.ses.s3m.plus.form.UpdateWarningForm;
import vn.ses.s3m.plus.pv.service.DataInverterPVService;
import vn.ses.s3m.plus.pv.service.OperationCombinerPVService;
import vn.ses.s3m.plus.pv.service.OperationStringPVService;
import vn.ses.s3m.plus.pv.service.WarningPVService;
import vn.ses.s3m.plus.service.DeviceService;

@CrossOrigin
@RestController
@RequestMapping ("/pv/warning")
public class WarningPVController {

    // khai báo tham số
    private static final Integer PAGE_SIZE = 20;

    private static final String SCHEMA = "schema";

    private static final String PROJECT_ID = "projectId";

    private static final String DEVICE_ID = "deviceId";

    private static final String WARNING_TYPE = "warningType";

    private static final String WARNING_TYPE_COMBINER_STRING = "warningTypeCombinerString";

    private static final Integer TYPE_PV = 2;

    /** Logging */
    private final Log log = LogFactory.getLog(WarningController.class);

    @Autowired
    private WarningPVService warningPVService;

    @Autowired
    private DataInverterPVService dataInverterPVService;

    @Autowired
    private OperationCombinerPVService operationCombinerPVService;

    @Autowired
    private OperationStringPVService operationStringPVService;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private UserMapper userMapper;

    @Value ("${pv.producer.export-folder}")
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
        // list warning
        List<Warning> warnings = warningPVService.getTotalWarningPV(condition);
        // totalDeviceHasWarning
        Integer devicesWarning = warningPVService.getAllDeviceHasWarningPV(condition);
        // tổng cảnh báo
        Map<String, Long> warningMap = new HashMap<>();

        warningMap.put("devicesWarning", (long) devicesWarning);

        // Khai báo các biến cảnh báo
        long nhietDoCao = 0;
        long matKetNoiAC = 0;
        long matKetNoiDC = 0;
        long dienApCaoAC = 0;
        long dienApThapAC = 0;
        long tanSoThap = 0;
        long tanSoCao = 0;
        long matNguonLuoi = 0;
        long chamDat = 0;
        long hongCauChi = 0;
        long dongMoCua = 0;
        long dienApCaoDC = 0;
        long matBoNho = 0;

        for (Warning warning : warnings) {
            Integer warningType = warning.getWarningType();
            if (warning.getDeviceType() == 1)
                switch (warningType) {
                    case Constants.WarningTypeInverter.NHIET_DO_CAO:
                        nhietDoCao = nhietDoCao + warning.getTotalDevice();
                        break;
                    case Constants.WarningTypeInverter.MAT_KET_NOI_AC:
                        matKetNoiAC = matKetNoiAC + warning.getTotalDevice();
                        break;
                    case Constants.WarningTypeInverter.MAT_KET_NOI_DC:
                        matKetNoiDC = matKetNoiDC + warning.getTotalDevice();
                        break;
                    case Constants.WarningTypeInverter.DIEN_AP_CAO_AC:
                        dienApCaoAC = dienApCaoAC + warning.getTotalDevice();
                        break;
                    case Constants.WarningTypeInverter.DIEN_AP_THAP_AC:
                        dienApThapAC = dienApThapAC + warning.getTotalDevice();
                        break;
                    case Constants.WarningTypeInverter.TAN_SO_THAP:
                        tanSoThap = tanSoThap + warning.getTotalDevice();
                        break;
                    case Constants.WarningTypeInverter.TAN_SO_CAO:
                        tanSoCao = tanSoCao + warning.getTotalDevice();
                        break;
                    case Constants.WarningTypeInverter.MAT_NGUON_LUOI:
                        matNguonLuoi = matNguonLuoi + warning.getTotalDevice();
                        break;
                    case Constants.WarningTypeInverter.CHAM_DAT:
                        chamDat = chamDat + warning.getTotalDevice();
                        break;
                    case Constants.WarningTypeInverter.HONG_CAU_CHI:
                        hongCauChi = hongCauChi + warning.getTotalDevice();
                        break;
                    case Constants.WarningTypeInverter.DONG_MO_CUA:
                        dongMoCua = dongMoCua + warning.getTotalDevice();
                        break;
                    case Constants.WarningTypeInverter.DIEN_AP_CAO_DC:
                        dienApCaoDC = dienApCaoDC + warning.getTotalDevice();
                        break;
                    case Constants.WarningTypeInverter.MEMORY_LOSS:
                        matBoNho = matBoNho + warning.getTotalDevice();
                        break;
                    default:
                        break;
                }
            else if (warning.getDeviceType() == 3) {
                switch (warningType) {
                    case Constants.WarningTypeInverter.NHIET_DO_CAO:
                        nhietDoCao = nhietDoCao + warning.getTotalDevice();
                        break;
                    case Constants.WarningTypeInverter.CHAM_DAT:
                        chamDat = chamDat + warning.getTotalDevice();
                        break;
                    case Constants.WarningTypeInverter.HONG_CAU_CHI:
                        hongCauChi = hongCauChi + warning.getTotalDevice();
                        break;
                    case Constants.WarningTypeInverter.DONG_MO_CUA:
                        dongMoCua = dongMoCua + warning.getTotalDevice();
                        break;
                    default:
                        break;
                }
            } else if (warning.getDeviceType() == 4) {
                switch (warningType) {
                    case Constants.WarningTypeInverter.NHIET_DO_CAO:
                        nhietDoCao = nhietDoCao + warning.getTotalDevice();
                        break;
                    case Constants.WarningTypeInverter.CHAM_DAT:
                        chamDat = chamDat + warning.getTotalDevice();
                        break;
                    case Constants.WarningTypeInverter.HONG_CAU_CHI:
                        hongCauChi = hongCauChi + warning.getTotalDevice();
                        break;
                    case Constants.WarningTypeInverter.DONG_MO_CUA:
                        dongMoCua = dongMoCua + warning.getTotalDevice();
                        break;
                    default:
                        break;
                }
            }
            warningMap.put("nhietDoCao", nhietDoCao);
            warningMap.put("matBoNho", matBoNho);
            warningMap.put("matKetNoiAC", matKetNoiAC);
            warningMap.put("matKetNoiDC", matKetNoiDC);
            warningMap.put("dienApCaoAC", dienApCaoAC);
            warningMap.put("dienApThapAC", dienApThapAC);
            warningMap.put("tanSoThap", tanSoThap);
            warningMap.put("tanSoCao", tanSoCao);
            warningMap.put("matNguonLuoi", matNguonLuoi);
            warningMap.put("chamDat", chamDat);
            warningMap.put("hongCauChi", hongCauChi);
            warningMap.put("dongMoCua", dongMoCua);
            warningMap.put("dienApCaoDC", dienApCaoDC);
        }
        return new ResponseEntity<>(warningMap, HttpStatus.OK);
    }

    /**
     * Lấy danh sách thiết bị cảnh báo theo id dự án và warning_type.
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
        @RequestParam ("customerId") final Integer customerId, @RequestParam ("projectId") final String projectId,
        @RequestParam ("page") final Integer page) {

        String schema = Schema.getSchemas(customerId);
        Map<String, Object> condition = new HashMap<>();

        List<Warning> warnings = new ArrayList<>();

        condition.put("fromDate", fromDate + " 00:00:00");
        condition.put("toDate", toDate + " 23:59:59");
        condition.put(SCHEMA, schema);
        condition.put(PROJECT_ID, projectId);
        condition.put("type", TYPE_PV);

        condition.put("offset", (page - 1) * PAGE_SIZE);
        condition.put("pageSize", PAGE_SIZE);

        int currentYear = Year.now()
            .getValue();
        condition.put("year", Constants.ES.UNDERSCORE_CHARACTER + currentYear);

        if (!warningType.equals("ALL")) {
            condition.put(WARNING_TYPE, warningType);
        }
        List<Warning> countListInverter = warningPVService.countWarningInverter(condition);
        List<Warning> countListCombiner = warningPVService.countWarningCombiner(condition);
        List<Warning> countListString = warningPVService.countWarningString(condition);

        warnings = warningPVService.getDetailWarningType(condition);
        // Inverter
        for (Warning item : countListInverter) {
            for (Warning count : warnings) {
                if (item.getDeviceId()
                    .equals(count.getDeviceId())
                    && item.getWarningType()
                        .equals(count.getWarningType())
                    && item.getFromDate()
                        .equals(count.getFromDate())) {
                    count.setTotal(item.getTotal());
                    break;
                }
            }
        }
        // Combiner
        for (Warning item : countListCombiner) {
            for (Warning count : warnings) {
                if (item.getDeviceId()
                    .equals(count.getDeviceId())
                    && item.getWarningType()
                        .equals(count.getWarningType())
                    && item.getFromDate()
                        .equals(count.getFromDate())) {
                    count.setTotal(item.getTotal());
                    break;
                }
            }
        }
        // String
        for (Warning item : countListString) {
            for (Warning count : warnings) {
                if (item.getDeviceId()
                    .equals(count.getDeviceId())
                    && item.getWarningType()
                        .equals(count.getWarningType())
                    && item.getFromDate()
                        .equals(count.getFromDate())) {
                    count.setTotal(item.getTotal());
                    break;
                }
            }
        }

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

        List<Warning> totalData = warningPVService.getListWarningByWarningType(condition);
        double totalPage = Math.ceil((double) totalData.size() / PAGE_SIZE);
        Map<String, Object> mapData = new HashMap<>();
        mapData.put("totalPage", totalPage);
        mapData.put("currentPage", page);
        mapData.put("data", warnings);

        return new ResponseEntity<>(mapData, HttpStatus.OK);
    }

    /**
     * Lấy danh sách thiết bị cảnh báo theo id dự án và warning_type.
     *
     * @param warningType Kiểu cảnh báo.
     * @param fromDate Thời gian bắt đầu.
     * @param toDate Thời gian kết thúc.
     * @param projectId ID của dự án.
     * @param page Page muốn hiển thị dữ liệu.
     * @return Danh sách chi tiết của cảnh báo theo warning type
     */
    @GetMapping ("/operation/type/{warningType}")
    public ResponseEntity<?> detailWarningOperationByTypePV(@PathVariable ("warningType") final String warningType,
        @RequestParam ("fromDate") final String fromDate, @RequestParam ("toDate") final String toDate,
        @RequestParam ("customerId") final Integer customerId, @RequestParam ("deviceId") final String deviceId,
        @RequestParam ("page") final Integer page) {

        String schema = Schema.getSchemas(customerId);
        Map<String, Object> condition = new HashMap<>();

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

        if (!warningType.equals("ALL")) {
            condition.put(WARNING_TYPE, warningType);
        }
        List<Warning> countListInverter = warningPVService.countWarningInverter(condition);
        List<Warning> countListCombiner = warningPVService.countWarningCombiner(condition);
        List<Warning> countListString = warningPVService.countWarningString(condition);

        warnings = warningPVService.getDetailWarningType(condition);
        // Inverter
        for (Warning item : warnings) {
            for (Warning count : countListInverter) {
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
        // Combiner
        for (Warning item : warnings) {
            for (Warning count : countListCombiner) {
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
        // String
        for (Warning item : warnings) {
            for (Warning count : countListString) {
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

        List<Warning> totalData = warningPVService.getListWarningByWarningType(condition);
        double totalPage = Math.ceil((double) totalData.size() / PAGE_SIZE);
        Map<String, Object> mapData = new HashMap<>();
        mapData.put("totalPage", totalPage);
        mapData.put("currentPage", page);
        mapData.put("data", warnings);

        return new ResponseEntity<>(mapData, HttpStatus.OK);
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
        @RequestParam ("customerId") final Integer customerId, @RequestParam ("deviceId") final String deviceId,
        @RequestParam ("page") final Integer page) {

        String schema = Schema.getSchemas(customerId);

        Map<String, Object> condition = new HashMap<>();
        condition.put(SCHEMA, schema);
        condition.put(DEVICE_ID, deviceId);
        condition.put("fromDate", fromDate);
        condition.put("toDate", toDate);
        condition.put(WARNING_TYPE, warningType);

        int currentYear = Year.now()
            .getValue();
        condition.put("year", Constants.ES.UNDERSCORE_CHARACTER + currentYear);

        // tìm thông tin loại thiết bị để xác định cảnh báo
        Device device = deviceService.getDeviceByDeviceId(condition);

        Map<String, Object> mapData = new HashMap<>();

        if (device.getDeviceType() == Constants.DeviceType.INVERTER) {
            List<DataInverter1> dataWarning = dataInverterPVService.getDataInverterByDevice(condition);
            mapData.put("dataWarning", dataWarning);
            mapData.put("deviceType", Constants.DeviceType.INVERTER);
        } else if (device.getDeviceType() == Constants.DeviceType.COMBINER) {
            List<DataCombiner1> dataWarning = operationCombinerPVService.getDataCombinerPV(condition);
            mapData.put("dataWarning", dataWarning);
            mapData.put("deviceType", Constants.DeviceType.COMBINER);
        } else if (device.getDeviceType() == Constants.DeviceType.STRING) {
            List<DataString1> dataWarning = operationStringPVService.getOperationStringPV(condition);
            mapData.put("dataWarning", dataWarning);
            mapData.put("deviceType", Constants.DeviceType.STRING);
        }

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
        Warning warning = warningPVService.getDetailWarningCachePV(condition);
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

        boolean isUpdate = warningPVService.updateWarningCachePV(condition);
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
        if (device.getDeviceType() == Constants.DeviceType.INVERTER) {
            List<DataInverter1> dataWarning = dataInverterPVService.getDataInverterByDevice(condition);
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

        } else if (device.getDeviceType() == Constants.DeviceType.COMBINER) {
            List<DataCombiner1> dataWarning = operationCombinerPVService.getDataCombinerPV(condition);
            // tạo excel
            if (dataWarning.size() > 0) {
                createOperationExcelCombiner(dataWarning, fromDate, toDate, device, path, imageData);
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

        } else {
            List<DataString1> dataWarning = operationStringPVService.getOperationStringPV(condition);
            // tạo excel
            if (dataWarning.size() > 0) {
                createOperationExcelString(dataWarning, fromDate, toDate, device, path, imageData);
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

        }

        // clear folder
        // File folderExport = new File(this.folderName);
        // if (folderExport.exists()) {
        // FileUtils.cleanDirectory(folderExport);
        // }

    }

    /**
     * Tạo excel thông tin vận hành bị cảnh báo inverter.
     *
     * @param data Danh sách bản tin bị cảnh báo vận hành.
     * @throws Exception
     */
    // CHECKSTYLE:OFF
    private void createOperationExcel(final List<DataInverter1> data, final String fromDate, final String toDate,
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

        anchorImg.setCol1(18);
        anchorImg.setCol2(20);
        anchorImg.setRow1(1);
        anchorImg.setRow2(4);

        Picture pict = drawingImg.createPicture(anchorImg, pictureIdx);

        // set độ rộng của cột
        sheet.setColumnWidth(0, 1300);
        sheet.setColumnWidth(1, 5200);
        sheet.setColumnWidth(4, 4000);
        sheet.setColumnWidth(6, 5200);
        sheet.setColumnWidth(7, 5200);
        sheet.setColumnWidth(8, 5200);
        sheet.setColumnWidth(9, 5200);
        sheet.setColumnWidth(10, 5200);
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

        CellRangeAddress region = new CellRangeAddress(0, 0, 0, 19);
        sheet.addMergedRegion(region);
        cell = sheet.getRow(0)
            .getCell(0);
        cell.setCellValue("BÁO CÁO INVERTER");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(2, 2, 0, 1);
        sheet.addMergedRegion(region);
        cell = sheet.getRow(2)
            .getCell(0);
        cell.setCellValue("Mã thiết bị");
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
        cell.setCellValue("Tên thiết bị");
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
        cell.setCellValue("Thời gian");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(2, 2, 13, 19);
        sheet.addMergedRegion(region);
        cell = sheet.getRow(2)
            .getCell(13);
        cell.setCellValue(fromDate);
        formatHeader(wb, region, sheet, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0, true);

        region = new CellRangeAddress(3, 3, 13, 19);
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
        cell.setCellValue("Điện áp");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 4, 4);
        cell = sheet.getRow(5)
            .getCell(4);
        cell.setCellValue("Dòng điện");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 5, 5);
        cell = sheet.getRow(5)
            .getCell(5);
        cell.setCellValue("Tần số");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 6, 6);
        cell = sheet.getRow(5)
            .getCell(6);
        cell.setCellValue("Công suất tác dụng");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 7, 7);
        cell = sheet.getRow(5)
            .getCell(7);
        cell.setCellValue("Công suất tác dụng tổng");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 8, 8);
        cell = sheet.getRow(5)
            .getCell(8);
        cell.setCellValue("Công suất toàn phần");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 9, 9);
        cell = sheet.getRow(5)
            .getCell(9);
        cell.setCellValue("Công suất toàn phần tổng");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 10, 10);
        cell = sheet.getRow(5)
            .getCell(10);
        cell.setCellValue("Công suất phản kháng");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 11, 11);
        cell = sheet.getRow(5)
            .getCell(11);
        cell.setCellValue("Công suất phản kháng tổng");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 12, 12);
        cell = sheet.getRow(5)
            .getCell(12);
        cell.setCellValue("PF");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 13, 13);
        cell = sheet.getRow(5)
            .getCell(13);
        cell.setCellValue("Dòng điện tổng DC");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 14, 14);
        cell = sheet.getRow(5)
            .getCell(14);
        cell.setCellValue("Điện áp DC");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 15, 15);
        cell = sheet.getRow(5)
            .getCell(15);
        cell.setCellValue("Công suất tổng DC");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 16, 16);
        cell = sheet.getRow(5)
            .getCell(16);
        cell.setCellValue("TmpCab");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 17, 17);
        cell = sheet.getRow(5)
            .getCell(17);
        cell.setCellValue("TmpSnk");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 18, 18);
        cell = sheet.getRow(5)
            .getCell(18);
        cell.setCellValue("TmpTrns");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 19, 19);
        cell = sheet.getRow(5)
            .getCell(19);
        cell.setCellValue("TmpOt");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);
        // each data.
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        int index = 6;
        for (int m = 0; m < data.size(); m++) {
            DataInverter1 item = data.get(m);
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
            cell.setCellValue(item.getVa() != null ? String.valueOf(item.getVa()) : "-");
            cell.setCellStyle(cellStyle);

            region = new CellRangeAddress(index + 1, index + 1, 3, 3);
            cell = sheet.getRow(index + 1)
                .getCell(3);
            cell.setCellValue(item.getVb() != null ? String.valueOf(item.getVb()) : "-");
            cell.setCellStyle(cellStyle);

            region = new CellRangeAddress(index + 2, index + 2, 3, 3);
            cell = sheet.getRow(index + 2)
                .getCell(3);
            cell.setCellValue(item.getVc() != null ? String.valueOf(item.getVc()) : "-");
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

            // cột tần số
            region = new CellRangeAddress(index, index + 2, 5, 5);
            cell = sheet.getRow(index)
                .getCell(5);
            cell.setCellValue(item.getF() != null ? String.valueOf(item.getF()) : "-");
            cell.setCellStyle(cellStyle);

            // cột CS tác dụng
            region = new CellRangeAddress(index, index, 6, 6);
            cell = sheet.getRow(index)
                .getCell(6);
            cell.setCellValue(item.getPa() != null ? String.valueOf(item.getPa()) : "-");
            cell.setCellStyle(cellStyle);

            region = new CellRangeAddress(index + 1, index + 1, 6, 6);
            cell = sheet.getRow(index + 1)
                .getCell(6);
            cell.setCellValue(item.getPb() != null ? String.valueOf(item.getPb()) : "-");
            cell.setCellStyle(cellStyle);

            region = new CellRangeAddress(index + 2, index + 2, 6, 6);
            cell = sheet.getRow(index + 2)
                .getCell(6);
            cell.setCellValue(item.getPc() != null ? String.valueOf(item.getPc()) : "-");
            cell.setCellStyle(cellStyle);

            // cột Ptotal
            region = new CellRangeAddress(index, index + 2, 7, 7);
            cell = sheet.getRow(index)
                .getCell(7);
            cell.setCellValue(item.getPtotal() != null ? String.valueOf(item.getPtotal()) : "-");
            cell.setCellStyle(cellStyle);

            // cột CS toàn phần
            region = new CellRangeAddress(index, index, 8, 8);
            cell = sheet.getRow(index)
                .getCell(8);
            cell.setCellValue(item.getSa() != null ? String.valueOf(item.getSa()) : "-");
            cell.setCellStyle(cellStyle);

            region = new CellRangeAddress(index + 1, index + 1, 8, 8);
            cell = sheet.getRow(index + 1)
                .getCell(8);
            cell.setCellValue(item.getSb() != null ? String.valueOf(item.getSb()) : "-");
            cell.setCellStyle(cellStyle);

            region = new CellRangeAddress(index + 2, index + 2, 8, 8);
            cell = sheet.getRow(index + 2)
                .getCell(8);
            cell.setCellValue(item.getSc() != null ? String.valueOf(item.getSc()) : "-");
            cell.setCellStyle(cellStyle);

            // cột Stotal
            region = new CellRangeAddress(index, index + 2, 9, 9);
            cell = sheet.getRow(index)
                .getCell(9);
            cell.setCellValue(item.getStotal() != null ? String.valueOf(item.getStotal()) : "-");
            cell.setCellStyle(cellStyle);

            // cột CS phản kháng
            region = new CellRangeAddress(index, index, 10, 10);
            cell = sheet.getRow(index)
                .getCell(10);
            cell.setCellValue(item.getQa() != null ? String.valueOf(item.getQa()) : "-");
            cell.setCellStyle(cellStyle);

            region = new CellRangeAddress(index + 1, index + 1, 10, 10);
            cell = sheet.getRow(index + 1)
                .getCell(10);
            cell.setCellValue(item.getQb() != null ? String.valueOf(item.getQb()) : "-");
            cell.setCellStyle(cellStyle);

            region = new CellRangeAddress(index + 2, index + 2, 10, 10);
            cell = sheet.getRow(index + 2)
                .getCell(10);
            cell.setCellValue(item.getQc() != null ? String.valueOf(item.getQc()) : "-");
            cell.setCellStyle(cellStyle);

            // cột Qtotal
            region = new CellRangeAddress(index, index + 2, 11, 11);
            cell = sheet.getRow(index)
                .getCell(11);
            cell.setCellValue(item.getQtotal() != null ? String.valueOf(item.getQtotal()) : "-");
            cell.setCellStyle(cellStyle);

            // cột PF
            region = new CellRangeAddress(index, index + 2, 12, 12);
            cell = sheet.getRow(index)
                .getCell(12);
            cell.setCellValue(item.getPF() != null ? String.valueOf(item.getPF()) : "-");
            cell.setCellStyle(cellStyle);

            // cột Idc
            region = new CellRangeAddress(index, index + 2, 13, 13);
            cell = sheet.getRow(index)
                .getCell(13);
            cell.setCellValue(item.getIdc() != null ? String.valueOf(item.getIdc()) : "-");
            cell.setCellStyle(cellStyle);

            // cột Udc
            region = new CellRangeAddress(index, index + 2, 14, 14);
            cell = sheet.getRow(index)
                .getCell(14);
            cell.setCellValue(item.getUdc() != null ? String.valueOf(item.getUdc()) : "-");
            cell.setCellStyle(cellStyle);

            // cột Pdc
            region = new CellRangeAddress(index, index + 2, 15, 15);
            cell = sheet.getRow(index)
                .getCell(15);
            cell.setCellValue(item.getPdc() != null ? String.valueOf(item.getPdc()) : "-");
            cell.setCellStyle(cellStyle);

            // cột TmpCab
            region = new CellRangeAddress(index, index + 2, 16, 16);
            cell = sheet.getRow(index)
                .getCell(16);
            cell.setCellValue(item.getTmpCab() != null ? String.valueOf(item.getTmpCab()) : "-");
            cell.setCellStyle(cellStyle);

            // cột TmpSnk
            region = new CellRangeAddress(index, index + 2, 17, 17);
            cell = sheet.getRow(index)
                .getCell(17);
            cell.setCellValue(item.getTmpSnk() != null ? String.valueOf(item.getTmpSnk()) : "-");
            cell.setCellStyle(cellStyle);

            // cột TmpTrns
            region = new CellRangeAddress(index, index + 2, 18, 18);
            cell = sheet.getRow(index)
                .getCell(18);
            cell.setCellValue(item.getTmpTrns() != null ? String.valueOf(item.getTmpTrns()) : "-");
            cell.setCellStyle(cellStyle);

            // cột TmpOt
            region = new CellRangeAddress(index, index + 2, 19, 19);
            cell = sheet.getRow(index)
                .getCell(19);
            cell.setCellValue(item.getTmpTrns() != null ? String.valueOf(item.getTmpTrns()) : "-");
            cell.setCellStyle(cellStyle);

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
     * Tạo excel thông tin vận hành bị cảnh báo combiner.
     *
     * @param data Danh sách bản tin bị cảnh báo vận hành.
     * @throws Exception
     */
    // CHECKSTYLE:OFF
    private void createOperationExcelCombiner(final List<DataCombiner1> data, final String fromDate,
        final String toDate, final Device device, final String path, final byte[] imageData) throws Exception {

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

        anchorImg.setCol1(12);
        anchorImg.setCol2(13);
        anchorImg.setRow1(2);
        anchorImg.setRow2(5);

        Picture pict = drawingImg.createPicture(anchorImg, pictureIdx);

        // set độ rộng của cột
        sheet.setColumnWidth(0, 1300);
        sheet.setColumnWidth(1, 5200);
        sheet.setColumnWidth(4, 4000);
        sheet.setColumnWidth(6, 5200);
        sheet.setColumnWidth(7, 5200);
        sheet.setColumnWidth(8, 5200);
        sheet.setColumnWidth(9, 5200);
        sheet.setColumnWidth(10, 5200);
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

        CellRangeAddress region = new CellRangeAddress(0, 0, 0, 12);
        sheet.addMergedRegion(region);
        cell = sheet.getRow(0)
            .getCell(0);
        cell.setCellValue("BÁO CÁO COMBINER");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(2, 2, 0, 1);
        sheet.addMergedRegion(region);
        cell = sheet.getRow(2)
            .getCell(0);
        cell.setCellValue("Mã thiết bị");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.LEFT, 1,
            true);

        region = new CellRangeAddress(2, 2, 2, 5);
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

        region = new CellRangeAddress(3, 3, 2, 5);
        sheet.addMergedRegion(region);
        cell = sheet.getRow(3)
            .getCell(2);
        cell.setCellValue(device.getDeviceName() != null
            ? device.getDeviceName()
                .toUpperCase()
            : "-");
        formatHeader(wb, region, sheet, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1, true);

        region = new CellRangeAddress(2, 3, 6, 7);
        sheet.addMergedRegion(region);
        cell = sheet.getRow(2)
            .getCell(6);
        cell.setCellValue("Thời gian");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(2, 2, 8, 12);
        sheet.addMergedRegion(region);
        cell = sheet.getRow(2)
            .getCell(8);
        cell.setCellValue(fromDate);
        formatHeader(wb, region, sheet, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0, true);

        region = new CellRangeAddress(3, 3, 8, 12);
        sheet.addMergedRegion(region);
        cell = sheet.getRow(3)
            .getCell(8);
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
        cell.setCellValue("Thời gian");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 2, 2);
        cell = sheet.getRow(5)
            .getCell(2);
        cell.setCellValue("DCAMax");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 3, 3);
        cell = sheet.getRow(5)
            .getCell(3);
        cell.setCellValue("Combiner Fuse Fault");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 4, 4);
        cell = sheet.getRow(5)
            .getCell(4);
        cell.setCellValue("Combiner Cabinet Open");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 5, 5);
        cell = sheet.getRow(5)
            .getCell(5);
        cell.setCellValue("Temp");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 6, 6);
        cell = sheet.getRow(5)
            .getCell(6);
        cell.setCellValue("Ground Fault");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 7, 7);
        cell = sheet.getRow(5)
            .getCell(7);
        cell.setCellValue("IdcCombiner");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 8, 8);
        cell = sheet.getRow(5)
            .getCell(8);
        cell.setCellValue("DCAH");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 9, 9);
        cell = sheet.getRow(5)
            .getCell(9);
        cell.setCellValue("VdcCombiner");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 10, 10);
        cell = sheet.getRow(5)
            .getCell(10);
        cell.setCellValue("T");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 11, 11);
        cell = sheet.getRow(5)
            .getCell(11);
        cell.setCellValue("PdcCombiner");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 12, 12);
        cell = sheet.getRow(5)
            .getCell(12);
        cell.setCellValue("PR");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        // each data.
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        int index = 6;
        for (int m = 0; m < data.size(); m++) {
            DataCombiner1 item = data.get(m);
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

            // Cột DCAMax
            region = new CellRangeAddress(index, index, 2, 2);
            cell = sheet.getRow(index)
                .getCell(2);
            cell.setCellValue(item.getDCAMax());
            cell.setCellStyle(cellStyle);

            // cột combiner_fuse_fault
            region = new CellRangeAddress(index, index, 3, 3);
            cell = sheet.getRow(index)
                .getCell(3);
            cell.setCellValue(
                item.getCOMBINER_FUSE_FAULT() != null ? String.valueOf(item.getCOMBINER_FUSE_FAULT()) : "-");
            cell.setCellStyle(cellStyle);

            // cột combiner_cabinet_open
            region = new CellRangeAddress(index, index, 4, 4);
            cell = sheet.getRow(index)
                .getCell(4);
            cell.setCellValue(
                item.getCOMBINER_CABINET_OPEN() != null ? String.valueOf(item.getCOMBINER_CABINET_OPEN()) : "-");
            cell.setCellStyle(cellStyle);

            // cột temp
            region = new CellRangeAddress(index, index, 5, 5);
            cell = sheet.getRow(index)
                .getCell(5);
            cell.setCellValue(item.getTEMP() != null ? String.valueOf(item.getTEMP()) : "-");
            cell.setCellStyle(cellStyle);

            // cột ground_fault
            region = new CellRangeAddress(index, index, 6, 6);
            cell = sheet.getRow(index)
                .getCell(6);
            cell.setCellValue(item.getGROUNDFAULT() != null ? String.valueOf(item.getGROUNDFAULT()) : "-");
            cell.setCellStyle(cellStyle);

            // cột IdcCombiner
            region = new CellRangeAddress(index, index, 7, 7);
            cell = sheet.getRow(index)
                .getCell(7);
            cell.setCellValue(item.getIdcCombiner() != null ? String.valueOf(item.getIdcCombiner()) : "-");
            cell.setCellStyle(cellStyle);

            // cột DCAh
            region = new CellRangeAddress(index, index, 8, 8);
            cell = sheet.getRow(index)
                .getCell(8);
            cell.setCellValue(item.getDCAh() != null ? String.valueOf(item.getDCAh()) : "-");
            cell.setCellStyle(cellStyle);

            // cột VdcCombiner
            region = new CellRangeAddress(index, index, 9, 9);
            cell = sheet.getRow(index)
                .getCell(9);
            cell.setCellValue(item.getVdcCombiner() != null ? String.valueOf(item.getVdcCombiner()) : "-");
            cell.setCellStyle(cellStyle);

            // cột T
            region = new CellRangeAddress(index, index, 10, 10);
            cell = sheet.getRow(index)
                .getCell(10);
            cell.setCellValue(item.getT() != null ? String.valueOf(item.getT()) : "-");
            cell.setCellStyle(cellStyle);

            // cột PdcCombiner
            region = new CellRangeAddress(index, index, 11, 11);
            cell = sheet.getRow(index)
                .getCell(11);
            cell.setCellValue(item.getPdcCombiner() != null ? String.valueOf(item.getPdcCombiner()) : "-");
            cell.setCellStyle(cellStyle);

            // cột PR
            region = new CellRangeAddress(index, index, 12, 12);
            cell = sheet.getRow(index)
                .getCell(12);
            cell.setCellValue(item.getPR() != null ? String.valueOf(item.getPR()) : "-");
            cell.setCellStyle(cellStyle);

            index += 1;
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
     * Tạo excel thông tin vận hành bị cảnh báo combiner.
     *
     * @param data Danh sách bản tin bị cảnh báo vận hành.
     * @throws Exception
     */
    // CHECKSTYLE:OFF
    private void createOperationExcelString(final List<DataString1> data, final String fromDate, final String toDate,
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

        anchorImg.setCol1(10);
        anchorImg.setCol2(11);
        anchorImg.setRow1(2);
        anchorImg.setRow2(5);

        Picture pict = drawingImg.createPicture(anchorImg, pictureIdx);

        // set độ rộng của cột
        sheet.setColumnWidth(0, 1300);
        sheet.setColumnWidth(1, 5200);
        sheet.setColumnWidth(4, 4000);
        sheet.setColumnWidth(6, 5200);
        sheet.setColumnWidth(7, 5200);
        sheet.setColumnWidth(8, 5200);
        sheet.setColumnWidth(9, 5200);
        sheet.setColumnWidth(10, 5200);
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

        CellRangeAddress region = new CellRangeAddress(0, 0, 0, 10);
        sheet.addMergedRegion(region);
        cell = sheet.getRow(0)
            .getCell(0);
        cell.setCellValue("BÁO CÁO STRING");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(2, 2, 0, 1);
        sheet.addMergedRegion(region);
        cell = sheet.getRow(2)
            .getCell(0);
        cell.setCellValue("Mã thiết bị");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.LEFT, 1,
            true);

        region = new CellRangeAddress(2, 2, 2, 5);
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

        region = new CellRangeAddress(3, 3, 2, 5);
        sheet.addMergedRegion(region);
        cell = sheet.getRow(3)
            .getCell(2);
        cell.setCellValue(device.getDeviceName() != null
            ? device.getDeviceName()
                .toUpperCase()
            : "-");
        formatHeader(wb, region, sheet, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1, true);

        region = new CellRangeAddress(2, 3, 6, 6);
        sheet.addMergedRegion(region);
        cell = sheet.getRow(2)
            .getCell(6);
        cell.setCellValue("Thời gian");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(2, 2, 7, 10);
        sheet.addMergedRegion(region);
        cell = sheet.getRow(2)
            .getCell(7);
        cell.setCellValue(fromDate);
        formatHeader(wb, region, sheet, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0, true);

        region = new CellRangeAddress(3, 3, 7, 10);
        sheet.addMergedRegion(region);
        cell = sheet.getRow(3)
            .getCell(7);
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
        cell.setCellValue("Thời gian");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 2, 2);
        cell = sheet.getRow(5)
            .getCell(2);
        cell.setCellValue("Combiner Fuse Fault");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 3, 3);
        cell = sheet.getRow(5)
            .getCell(3);
        cell.setCellValue("Combiner Cabinet Open");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 4, 4);
        cell = sheet.getRow(5)
            .getCell(4);
        cell.setCellValue("Temp");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 5, 5);
        cell = sheet.getRow(5)
            .getCell(5);
        cell.setCellValue("Ground Fault");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 6, 6);
        cell = sheet.getRow(5)
            .getCell(6);
        cell.setCellValue("IdcStr");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 7, 7);
        cell = sheet.getRow(5)
            .getCell(7);
        cell.setCellValue("InDCAhr");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 8, 8);
        cell = sheet.getRow(5)
            .getCell(8);
        cell.setCellValue("VdcStr");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 9, 9);
        cell = sheet.getRow(5)
            .getCell(9);
        cell.setCellValue("EpStr");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        region = new CellRangeAddress(5, 5, 10, 10);
        cell = sheet.getRow(5)
            .getCell(10);
        cell.setCellValue("InDCPR");
        formatHeader(wb, region, sheet, cell, IndexedColors.GREY_25_PERCENT.getIndex(), HorizontalAlignment.CENTER, 0,
            true);

        // each data.
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        int index = 6;
        for (int m = 0; m < data.size(); m++) {
            DataString1 item = data.get(m);
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

            // cột combiner_fuse_fault
            region = new CellRangeAddress(index, index, 2, 2);
            cell = sheet.getRow(index)
                .getCell(2);
            cell.setCellValue(
                item.getCOMBINER_FUSE_FAULT() != null ? String.valueOf(item.getCOMBINER_FUSE_FAULT()) : "-");
            cell.setCellStyle(cellStyle);

            // cột combiner_cabinet_open
            region = new CellRangeAddress(index, index, 3, 3);
            cell = sheet.getRow(index)
                .getCell(3);
            cell.setCellValue(
                item.getCOMBINER_CABINET_OPEN() != null ? String.valueOf(item.getCOMBINER_CABINET_OPEN()) : "-");
            cell.setCellStyle(cellStyle);

            // cột temp
            region = new CellRangeAddress(index, index, 4, 4);
            cell = sheet.getRow(index)
                .getCell(4);
            cell.setCellValue(item.getTEMP() != null ? String.valueOf(item.getTEMP()) : "-");
            cell.setCellStyle(cellStyle);

            // cột ground_fault
            region = new CellRangeAddress(index, index, 5, 5);
            cell = sheet.getRow(index)
                .getCell(5);
            cell.setCellValue(item.getGROUNDFAULT() != null ? String.valueOf(item.getGROUNDFAULT()) : "-");
            cell.setCellStyle(cellStyle);

            // cột IdcStr
            region = new CellRangeAddress(index, index, 6, 6);
            cell = sheet.getRow(index)
                .getCell(6);
            cell.setCellValue(item.getIdcStr() != null ? String.valueOf(item.getIdcStr()) : "-");
            cell.setCellStyle(cellStyle);

            // cột InDCAhr
            region = new CellRangeAddress(index, index, 7, 7);
            cell = sheet.getRow(index)
                .getCell(7);
            cell.setCellValue(item.getInDCAhr() != null ? String.valueOf(item.getInDCAhr()) : "-");
            cell.setCellStyle(cellStyle);

            // cột VdcStr
            region = new CellRangeAddress(index, index, 8, 8);
            cell = sheet.getRow(index)
                .getCell(8);
            cell.setCellValue(item.getVdcStr() != null ? String.valueOf(item.getVdcStr()) : "-");
            cell.setCellStyle(cellStyle);

            // cột EpStr
            region = new CellRangeAddress(index, index, 9, 9);
            cell = sheet.getRow(index)
                .getCell(9);
            cell.setCellValue(item.getEpStr() != null ? String.valueOf(item.getEpStr()) : "-");
            cell.setCellStyle(cellStyle);

            // cột InDCPR
            region = new CellRangeAddress(index, index, 10, 10);
            cell = sheet.getRow(index)
                .getCell(10);
            cell.setCellValue(item.getInDCPR() != null ? String.valueOf(item.getInDCPR()) : "-");
            cell.setCellStyle(cellStyle);

            index += 1;
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
}
