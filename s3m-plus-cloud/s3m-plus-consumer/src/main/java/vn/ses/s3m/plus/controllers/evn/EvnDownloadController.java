package vn.ses.s3m.plus.controllers.evn;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.map.HashedMap;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vn.ses.s3m.plus.dao.DeviceMapper;
import vn.ses.s3m.plus.dao.UserMapper;
import vn.ses.s3m.plus.dao.evn.DataInverterMapperEVN;
import vn.ses.s3m.plus.dao.evn.ScheduleMapper;
import vn.ses.s3m.plus.dto.Device;
import vn.ses.s3m.plus.dto.User;
import vn.ses.s3m.plus.dto.evn.DataInverter1EVN;
import vn.ses.s3m.plus.dto.evn.Schedule;

@RestController
@RequestMapping ("/common/download")
public class EvnDownloadController {
    @Autowired
    private DataInverterMapperEVN dataInverterMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ScheduleMapper scheduleMapper;

    @Autowired
    private DeviceMapper deviceMapper;

    @GetMapping ("/export")
    public ResponseEntity<?> exportFile(@RequestParam ("fromDate") String fromDate,
        @RequestParam ("toDate") String toDate, @RequestParam ("typeData") String typeData, HttpServletRequest request,
        HttpServletResponse response) {

        Map<String, String> condition = new HashMap<>();
        String superBigData = "";
        // format kiểu thời gian (dd-mm-yyyy)
        String[] from = fromDate.split("-");
        String[] to = toDate.split("-");
        if (from[1].length() == 1) {
            from[1] = "0" + from[1];
        }
        if (to[1].length() == 1) {
            to[1] = "0" + to[1];
        }
        fromDate = from[0] + "-" + from[1] + "-" + from[2];
        toDate = to[0] + "-" + to[1] + "-" + to[2];
        condition.put("fromDate", fromDate + " 00:00:00");
        condition.put("toDate", toDate + " 23:59:59");
        // -- format kiểu thời gian (dd-mm-yyyy)

        System.out.println("typeData and ID: " + typeData);

        List<DataInverter1EVN> dataInverterList = new ArrayList<DataInverter1EVN>();
        List<DataInverter1EVN> dataLastDateInverter = new ArrayList<DataInverter1EVN>();
        // Xử lý chuỗi nhập vào
        String text[] = typeData.split(":");
        if (text[0].equals("superManager")) {
            condition.put("superManagerId", text[1]);
        } else if (text[0].equals("manager")) {
            condition.put("managerId", text[1]);
        } else if (text[0].equals("area")) {
            condition.put("areaId", text[1]);
        }
//        dataInverterList = dataInverterMapper.getListDataInverterByProjectId(condition);
//        dataLastDateInverter = dataInverterMapper.getLastDateInverterByProjectId(condition);
        String excelFilePath = "Reviews-export.xlsx";
        List<DataInverter1EVN> listInverterExport = new ArrayList<DataInverter1EVN>();

        List<String> dateList = new ArrayList<String>();

        LocalDate startDate = LocalDate.parse(fromDate);
        LocalDate endDate = LocalDate.parse(toDate);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (LocalDate date = startDate.plusDays(-1); date.isBefore(endDate.plusDays(+1)); date = date.plusDays(1)) {
            String dateString = date.format(formatter);
            dateList.add(dateString);
        }

        List<String> deviceIdList = new ArrayList<String>();
        for (DataInverter1EVN dataInverter1 : dataInverterList) {
            String deviceId = dataInverter1.getDeviceId();
            if (!deviceIdList.contains(deviceId)) {
                deviceIdList.add(deviceId);
            }

        }

        Map<String, String> mapSchedule = new HashedMap<String, String>();
        String deviceArr[] = deviceIdList.toString()
            .split("\\[");
        String deviceArr2[] = deviceArr[1].split("\\]");
        // String deviceIdArr = deviceArr2[0];
        mapSchedule.put("fromDate", fromDate);
        mapSchedule.put("toDate", toDate);
        mapSchedule.put("typeScrop", "4");
        mapSchedule.put("deleteFlag", "0");
        // mapSchedule.put("listDeviceId", deviceIdArr);

        List<Schedule> listSchedule = scheduleMapper.getSchedulesByDeviceIds(mapSchedule);

        List<Device> deviceList = deviceMapper.getDeviceByProjectId(condition);
        Map<String, Integer> devicePower = new HashedMap<String, Integer>();
        for (int o = 0; o < deviceList.size(); o++) {
            Device device = deviceList.get(o);
            String deviceId = String.valueOf(device.getDeviceId());
            devicePower.put(deviceId, device.getPower());
        }

        // B1.2: Loop ds ở B1.1 tạo một ds Map lưu sản lượng tích lũy gần nhất của từng inverter
        Map<String, Integer> dsLastestACEnergy = new HashedMap<String, Integer>();
        for (int i = 0; i < dataLastDateInverter.size(); i++) {
            DataInverter1EVN dataInverter1 = dataLastDateInverter.get(i);

            LocalDate lastDate = dataInverter1.getSentDate()
                .toLocalDateTime()
                .toLocalDate();
            LocalDate inverterDate = LocalDate.parse(fromDate);
            if (inverterDate.isEqual(lastDate.plusDays(1))) {
                dsLastestACEnergy.put(dataInverter1.getDeviceId(), dataInverter1.getWh());
            } else {
                dsLastestACEnergy.put(dataInverter1.getDeviceId(), 0);
            }

        }

        // B1.3: Tạo một ds Map tạm để lưu thông tin sản lượng đang xử lý của từng inverter
        Map<String, Integer> dsInverterACEnergy = new HashedMap<String, Integer>();
        // B1.4: Tạo một ds Map tạm để lưu thông tin ngày đã xử lý của từng inverter
        Map<String, LocalDate> dsInverterDate = new HashedMap<String, LocalDate>();

        // B2.1: Loop ds bản tin inverter lấy ra từ DB trong khoảng thời gian fromDate ~ toDate
        if (dataInverterList.size() > 0) {
            for (int i = 0; i < dataInverterList.size(); i++) {
                DataInverter1EVN dataInverter1 = dataInverterList.get(i);
                long millisInverter = dataInverter1.getSentDate()
                    .getTime();
                DataInverter1EVN dataInverterExport = new DataInverter1EVN();

                dataInverterExport.setDeviceId(dataInverter1.getDeviceId());
                dataInverterExport.setDeviceName(dataInverter1.getDeviceName());
                dataInverterExport.setProjectName(dataInverter1.getProjectName());
                dataInverterExport.setDcw(dataInverter1.getDcw());
                dataInverterExport.setW(dataInverter1.getW());
                dataInverterExport.setWhTotal(Long.parseLong(dataInverter1.getWh()
                    .toString()));
                dataInverterExport.setSentDate(dataInverter1.getSentDate());
                dataInverterExport.setTransactionDate(dataInverter1.getTransactionDate());
                // B2.1.2: Kiểm tra xem đây có phải là bản tin đầu tiên xử lý không?
                // (dsInverterACEnergy.get(deviceId) == null)
                if (dsInverterACEnergy.get(dataInverter1.getDeviceId()) != null) {
                    // Nếu là ngày tiếp theo 1 ngày thì lưu sản lượng tích lũy ở dsInverterACEnergy ở B1.3 vào
                    // dsLastestACEnergy ở B1.2
                    if (dataInverter1.getSentDate()
                        .toLocalDateTime()
                        .toLocalDate()
                        .isEqual( (dsInverterDate.get(dataInverter1.getDeviceId())
                            .plusDays(1)))) {

                        dsLastestACEnergy.put(dataInverter1.getDeviceId(),
                            dsInverterACEnergy.get(dataInverter1.getDeviceId()));

                        // Nếu là ngày tiếp theo >= 2 ngày thì set sản lượng tích lũy bằng 0 vào dsLastestACEnergy ở
                        // B1.2
                    } else if (dataInverter1.getSentDate()
                        .toLocalDateTime()
                        .toLocalDate()
                        .compareTo(dsInverterDate.get(dataInverter1.getDeviceId())) >= 2) {
                        dsLastestACEnergy.put(dataInverter1.getDeviceId(), 0);
                    }
                }
                // B2.1.3: Thực hiện các xử lý:
                Integer energyDay = dataInverter1.getWh() - dsLastestACEnergy.get(dataInverter1.getDeviceId());
                dsInverterACEnergy.put(dataInverter1.getDeviceId(), dataInverter1.getWh());
                dsInverterDate.put(dataInverter1.getDeviceId(), dataInverter1.getSentDate()
                    .toLocalDateTime()
                    .toLocalDate());

                dataInverterExport.setWhDay(energyDay);
                if (listSchedule.size() > 0) {
                    for (int u = 0; u < listSchedule.size(); u++) {
                        Schedule schedule = listSchedule.get(u);
                        String deviceId = String.valueOf(schedule.getStt());
                        int congSuatChoPhep = schedule.getCongSuatChoPhep()
                            .intValue();
                        Date date = new Date();
                        date.setTime(schedule.getTimeView()
                            .getTime());
                        String timeView = new SimpleDateFormat("yyyy-MM-dd").format(date);
                        String fromTime = schedule.getFromTime();
                        String toTime = schedule.getToTime();
                        String scheduleFromDate = timeView + " " + fromTime + ":00";
                        String scheduleToDate = timeView + " " + toTime + ":00";

                        Timestamp timestampFromDate = Timestamp.valueOf(scheduleFromDate);
                        Timestamp timestampToDate = Timestamp.valueOf(scheduleToDate);

                        long millisFromTime = timestampFromDate.getTime();
                        long millisToTime = timestampToDate.getTime();

                        if (deviceId.equals(dataInverter1.getDeviceId())) {
                            if (millisFromTime <= millisInverter && millisInverter <= millisToTime) {
                                dataInverterExport.setCongSuatChoPhep(congSuatChoPhep);
                            }
                        }

                    }
                }

                if (dataInverterExport.getCongSuatChoPhep() == null) {
                    if (devicePower.get(dataInverter1.getDeviceId()) != null) {
                        int congSuatChoPhep = devicePower.get(dataInverter1.getDeviceId());
                        dataInverterExport.setCongSuatChoPhep(congSuatChoPhep);
                    } else {
                        dataInverterExport.setCongSuatChoPhep(0);
                    }

                }
                listInverterExport.add(dataInverterExport);
                if (i == 0) {
                    superBigData = superBigData + "{\"DATE_TIME\":" + "\"" + dataInverterExport.getSentDate() + "\","
                        + "\"PLANT_ID\":" + "\"" + dataInverterExport.getProjectName() + "\"," + "\"SOURCE_KEY\":"
                        + "\"" + dataInverterExport.getDeviceName() + "\"," + "\"DC_POWER\":" + "\""
                        + dataInverterExport.getDcw() + "\"," + "\"AC_POWER\":" + "\"" + dataInverterExport.getW()
                        + "\"," + "\"DAILY_YIELD\":" + "\"" + dataInverterExport.getWhDay() + "\"," + "\"TOTAL_YIELD\":"
                        + "\"" + dataInverterExport.getWhTotal() + "\"," + "\"CS_GIOI_HAN\":" + "\""
                        + dataInverterExport.getCongSuatChoPhep() + "\"}";
                } else {
                    superBigData = superBigData + ",{\"DATE_TIME\":" + "\"" + dataInverterExport.getSentDate() + "\","
                        + "\"PLANT_ID\":" + "\"" + dataInverterExport.getProjectName() + "\"," + "\"SOURCE_KEY\":"
                        + "\"" + dataInverterExport.getDeviceName() + "\"," + "\"DC_POWER\":" + "\""
                        + dataInverterExport.getDcw() + "\"," + "\"AC_POWER\":" + "\"" + dataInverterExport.getW()
                        + "\"," + "\"DAILY_YIELD\":" + "\"" + dataInverterExport.getWhDay() + "\"," + "\"TOTAL_YIELD\":"
                        + "\"" + dataInverterExport.getWhTotal() + "\"," + "\"CS_GIOI_HAN\":" + "\""
                        + dataInverterExport.getCongSuatChoPhep() + "\"}";
                }
            }
        }
        return new ResponseEntity<String>("[" + superBigData + "]", HttpStatus.OK);
    }

    @GetMapping ("/getTypeData")
    public ResponseEntity<?> getData(@RequestParam ("username") String username) {
        User user = userMapper.getUserByUsername(username);
        return new ResponseEntity<User>(user, HttpStatus.OK);
    }

    @SuppressWarnings ("unused")
    private void writeHeaderLine(XSSFSheet sheet) {

        Row headerRow = sheet.createRow(0);

        Cell headerCell = headerRow.createCell(0);
        headerCell.setCellValue("DATE_TIME");

        headerCell = headerRow.createCell(1);
        headerCell.setCellValue("PLANT_ID");

        headerCell = headerRow.createCell(2);
        headerCell.setCellValue("SOURCE_KEY");

        headerCell = headerRow.createCell(3);
        headerCell.setCellValue("DC_POWER");

        headerCell = headerRow.createCell(4);
        headerCell.setCellValue("AC_POWER");

        headerCell = headerRow.createCell(5);
        headerCell.setCellValue("DAILY_YIELD");

        headerCell = headerRow.createCell(6);
        headerCell.setCellValue("TOTAL_YIELD");

        headerCell = headerRow.createCell(7);
        headerCell.setCellValue("CS_GIOI_HAN");
    }

    private void writeDataLines(List<DataInverter1EVN> dataInverterList, XSSFWorkbook workbook, XSSFSheet sheet) {
        int rowCount = 1;
        if (dataInverterList.size() > 0) {
            for (int i = 0; i < dataInverterList.size(); i++) {
                DataInverter1EVN dataInverter1 = dataInverterList.get(i);
                String sentDate = dataInverter1.getSentDate()
                    .toString();
                Row row = sheet.createRow(rowCount++);

                int columnCount = 0;
                Cell cell = row.createCell(columnCount++);
                cell.setCellValue(sentDate);

                cell = row.createCell(columnCount++);
                cell.setCellValue(dataInverter1.getProjectName());

                cell = row.createCell(columnCount++);
                cell.setCellValue(dataInverter1.getDeviceName());

                cell = row.createCell(columnCount++);
                cell.setCellValue(dataInverter1.getDcw());

                cell = row.createCell(columnCount++);
                cell.setCellValue(dataInverter1.getW());

                cell = row.createCell(columnCount++);
                cell.setCellValue(dataInverter1.getWhDay());

                cell = row.createCell(columnCount++);
                cell.setCellValue(dataInverter1.getWhTotal());

                cell = row.createCell(columnCount++);
                if (dataInverter1.getCongSuatChoPhep() != null) {
                    cell.setCellValue(dataInverter1.getCongSuatChoPhep());
                } else {
                    cell.setCellValue(0);
                }

            }
        }
    }
}
