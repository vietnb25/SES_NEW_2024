package vn.ses.s3m.plus.controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.Normalizer;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xddf.usermodel.chart.XDDFDataSource;
import org.apache.poi.xddf.usermodel.chart.XDDFDataSourcesFactory;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.zeroturnaround.zip.ZipUtil;

import vn.ses.s3m.plus.common.Constants;
import vn.ses.s3m.plus.common.DateUtils;
import vn.ses.s3m.plus.common.Constants.ES;
import vn.ses.s3m.plus.common.Schema;
import vn.ses.s3m.plus.dto.Chart;
import vn.ses.s3m.plus.dto.Customer;
import vn.ses.s3m.plus.dto.DataCombiner1;
import vn.ses.s3m.plus.dto.DataInverter1;
import vn.ses.s3m.plus.dto.DataLoadFrame1;
import vn.ses.s3m.plus.dto.DataPower;
import vn.ses.s3m.plus.dto.DataPowerResult;
import vn.ses.s3m.plus.dto.DataRmuDrawer1;
import vn.ses.s3m.plus.dto.Device;
import vn.ses.s3m.plus.dto.LandmarksPlansEnergy;
import vn.ses.s3m.plus.dto.Project;
import vn.ses.s3m.plus.dto.Setting;
import vn.ses.s3m.plus.service.ChartService;
import vn.ses.s3m.plus.service.CustomerService;
import vn.ses.s3m.plus.service.DeviceService;
import vn.ses.s3m.plus.service.LandmarksPlanssEnergyService;
import vn.ses.s3m.plus.service.ProjectService;
import vn.ses.s3m.plus.service.SettingService;

@RestController
@RequestMapping("/common/chart")
public class ChartController {

	// khai báo tham số
	private static final Integer PAGE_SIZE = 20;

	private static final String SCHEMA = "schema";

	private static final String PROJECT_ID = "projectId";

	private static final String DEVICE_ID = "deviceId";

	private static final String WARNING_TYPE = "warningType";

	private static final String SYSTEM_TYPE_ID = "systemTypeId";

	// Chưa định nghĩa trên tài liệu
	private static final Integer TYPE_DEVICE_STMV = 0;

	// Chưa định nghĩa trên tài liệu
	private static final Integer TYPE_DEVICE_SGMV = 0;

	/** Logging */
	private final Log log = LogFactory.getLog(ChartController.class);

	@Autowired
	private SettingService settingService;

	@Autowired
	private ChartService chartService;

	@Autowired
	private DeviceService deviceService;

	@Autowired
	private ProjectService projectService;

	@Autowired
	private CustomerService customerService;

	@Autowired
	private LandmarksPlanssEnergyService landmarksPlanEnergyService;

	@Value("${consumer.producer.export-folder}")
	private String folderName;

	/**
	 * Lấy thông tin công suất, năng lượng
	 *
	 * @param fromDate  Thời gian bắt đầu.
	 * @param toDate    Thời gian kết thúc.
	 * @param projectId Thời gian kết thúc.
	 * @param typeTime  là phân định kiểu thời gian (giờ, ngày, tháng,).
	 * @return Danh sách tổng công suất, năng lượng theo từng thời điểm.
	 */
	@GetMapping("/load")
	public ResponseEntity<?> getChartLoad(@RequestParam("fromDate") final String fromDate,
			@RequestParam("toDate") final String toDate, @RequestParam("customerId") final Integer customerId,
			@RequestParam("projectId") final String projectId, @RequestParam("typeTime") final Integer typeTime,
			@RequestParam("deviceId") final String deviceId, @RequestParam("systemTypeId") final String systemTypeId,
			@RequestParam(value = "ids", required = false) final String ids) {
		log.info("getChartLoad START");
		String schema = Schema.getSchemas(customerId);
		Map<String, Object> condition = new HashMap<>();
		condition.put(SCHEMA, schema);
		if (Integer.parseInt(projectId) != 0) {
			condition.put(PROJECT_ID, projectId);
		}
		if (systemTypeId != "" && systemTypeId != "0") {
			condition.put(SYSTEM_TYPE_ID, systemTypeId);
		}
		if (deviceId != "" && deviceId != "0") {
			condition.put(DEVICE_ID, deviceId);
		}
		if (ids != "" && ids != "0") {
			condition.put("ids", ids);
		}

		LocalDateTime previousDay = null;
		LocalDateTime previousWeek = null;
		LocalDateTime previousMonth = null;
		LocalDateTime previousYear = null;
		String fromLastDate = "";
		String toLastDate = "";
		// Ép kiểu ngày String sang Date
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(ES.DATETIME_FORMAT_YMDHMS);
		LocalDateTime fromDateComp = LocalDateTime.parse(fromDate, formatter);
		LocalDateTime toDateComp = LocalDateTime.parse(toDate, formatter);

		condition.put("fromDate", fromDate);
		condition.put("toDate", toDate);
		if (typeTime == 1) {
			// Lấy data năng lượng theo ngày
			condition.put("viewType", 5);
			// Lấy data năng lượng theo ngày trước
			fromLastDate = fromDateComp.minusDays(1).format(formatter);

		} else if (typeTime == 2) {
			// Lấy data năng lượng theo tháng
			condition.put("viewType", 3);
			// Lấy data năng lượng theo trước
			fromLastDate = fromDateComp.minusMonths(1).format(formatter);

		} else if (typeTime == 3) {
			// Lấy data năng lượng theo năm
			condition.put("viewType", 2);
			// Lấy data năng lượng theo năm trước
			fromLastDate = fromDateComp.minusYears(1).format(formatter);

		} else if (typeTime == 4) {
			// Lấy data năng lượng theo tổng các năm
			condition.put("viewType", 1);
			// Lấy data năng lượng theo tổng các năm trước
			fromLastDate = fromDateComp.minusYears(1).format(formatter);

		} else if (typeTime == 5) {
			// Lấy data năng lượng theo ngày bắt đầu và ngày kết thúc
			condition.put("typeTime", 5);
			condition.put("viewType", 3);
			fromLastDate = fromDateComp.minusDays(1).format(formatter);
			toLastDate = toDateComp.minusDays(1).format(formatter);

		} else if (typeTime == 6) {
			// Lấy data năng lượng theo tuần
			condition.put("typeTime", 6);
			condition.put("viewType", 3);
			// Lấy data năng lượng theo tuần trước
			fromLastDate = fromDateComp.minusWeeks(1).format(formatter);
			toLastDate = toDateComp.minusWeeks(1).format(formatter);
		}

		List<DataLoadFrame1> dataNow = chartService.getChartLoadByCustomerId(condition);
		condition.put("fromDate", fromLastDate);
		condition.put("toDate", toLastDate);
		List<DataLoadFrame1> dataComp = chartService.getChartLoadByCustomerId(condition);

		JSONArray resultArray = new JSONArray();
		Integer cumulativeTotal = 0;

		DateTimeFormatter formatterX = DateTimeFormatter.ofPattern(ES.DATE_FORMAT_YMD);

		// Chuyển dữ liệu hôm nay
		for (DataLoadFrame1 data : dataNow) {
			String name = data.getProjectName();
			Integer value = data.getEp();
			String time = data.getTime();
			String nameDevice = data.getDeviceName();
			String viewTime = data.getViewTime();

			// Tạo một đối tượng JSON đại diện cho mỗi bản ghi
			JSONObject recordObject = new JSONObject();
			recordObject.put(name, value);

			// Kiểm tra xem trường "time" đã tồn tại trong danh sách JSON chưa
			boolean timeExists = false;

			for (Object obj : resultArray) {
				if (obj instanceof JSONObject) {
					JSONObject timeObject = (JSONObject) obj;
					if (timeObject.containsKey("time") && timeObject.get("time").equals(time)) {
						Integer total = timeObject.containsKey("total") ? (int) timeObject.get("total") : 0;
						if (Integer.parseInt(projectId) != 0) {
							timeObject.put(nameDevice, value);
						} else {
							timeObject.put(name, value);
						}
						cumulativeTotal += value;
						timeObject.put("total", cumulativeTotal);
						timeExists = true;
						break;
					}
				}
			}

			if (!timeExists) {
				JSONObject timeObject = new JSONObject();

				if (typeTime == 6) {
					LocalDate dateX = LocalDate.parse(viewTime, formatterX);
					DayOfWeek dayOfWeek = dateX.getDayOfWeek();
					int dayOfWeekValue = dayOfWeek.getValue() + 1;
					if (dayOfWeekValue != 8) {
						timeObject.put("day", "Thứ " + dayOfWeekValue);
					} else {
						timeObject.put("day", "Chủ nhật");
					}

				}

				timeObject.put("time", time);
				timeObject.put("viewTime", viewTime);
				if (Integer.parseInt(projectId) != 0) {
					timeObject.put(nameDevice, value);
				} else {
					timeObject.put(name, value);
				}
				cumulativeTotal += value;
				timeObject.put("total", cumulativeTotal);
				resultArray.add(timeObject);
			}
		}

		// Chuyển dữ liệu hôm qua
		JSONArray resultArrayComp = new JSONArray();
		cumulativeTotal = 0;

		for (DataLoadFrame1 data : dataComp) {
			String name = data.getProjectName();
			Integer value = data.getEp();
			String time = data.getTime();
			String viewTime = data.getViewTime();
			String nameDevice = data.getDeviceName();

			// Tạo một đối tượng JSON đại diện cho mỗi bản ghi
			JSONObject recordObject = new JSONObject();
			recordObject.put(name, value);

			// Kiểm tra xem trường "time" đã tồn tại trong danh sách JSON chưa
			boolean timeExists = false;
			for (Object obj : resultArrayComp) {
				if (obj instanceof JSONObject) {
					JSONObject timeObject = (JSONObject) obj;
					if (timeObject.containsKey("time") && timeObject.get("time").equals(time)) {
						Integer total = timeObject.containsKey("total") ? (int) timeObject.get("total") : 0;
						if (Integer.parseInt(projectId) != 0) {
							timeObject.put(nameDevice, value);
						} else {
							timeObject.put(name, value);
						}
						cumulativeTotal += value;
						timeObject.put("total", cumulativeTotal);
						timeExists = true;
						break;

					}
				}
			}

			if (!timeExists) {
				JSONObject timeObject = new JSONObject();
				if (typeTime == 6) {
					LocalDate dateX = LocalDate.parse(viewTime, formatterX);
					DayOfWeek dayOfWeek = dateX.getDayOfWeek();
					int dayOfWeekValue = dayOfWeek.getValue() + 1;
					if (dayOfWeekValue != 8) {
						timeObject.put("day", "Thứ " + dayOfWeekValue);
					} else {
						timeObject.put("day", "Chủ nhật");
					}

				}
				timeObject.put("viewTime", viewTime);
				timeObject.put("time", time);
				if (Integer.parseInt(projectId) != 0) {
					timeObject.put(nameDevice, value);
				} else {
					timeObject.put(name, value);
				}
				cumulativeTotal += value;
				timeObject.put("total", cumulativeTotal);
				resultArrayComp.add(timeObject);
			}
		}

		Map<String, Object> mapData = new HashMap<>();
		mapData.put("dataNow", dataNow);
		mapData.put("dataComp", resultArrayComp);
		mapData.put("dataLoadAllSite", resultArray);

		log.info("getChartLoad END");

		return new ResponseEntity<>(mapData, HttpStatus.OK);
	}

	@GetMapping("/load-compare")
	public ResponseEntity<?> getChartLoadCompare(@RequestParam("fromDate") final String fromDate,
			@RequestParam("toDate") final String toDate, @RequestParam("customerId") final Integer customerId,
			@RequestParam("projectId") final String projectId, @RequestParam("typeTime") final Integer typeTime,
			@RequestParam("deviceId") final String deviceId, @RequestParam("systemTypeId") final String systemTypeId,
			@RequestParam(value = "ids", required = false) final String ids) {
		log.info("getChartLoad START");
		String schema = Schema.getSchemas(customerId);
		Map<String, Object> condition = new HashMap<>();
		condition.put(SCHEMA, schema);
		if (Integer.parseInt(projectId) != 0) {
			condition.put(PROJECT_ID, projectId);
		}
		if (systemTypeId != "" && systemTypeId != "0") {
			condition.put(SYSTEM_TYPE_ID, systemTypeId);
		}
		if (deviceId != "" && deviceId != "0") {
			condition.put(DEVICE_ID, deviceId);
		}
		if (ids != "" && ids != "0") {
			condition.put("ids", ids);
		}

		condition.put("fromDate", fromDate);
		condition.put("toDate", toDate);
		if (typeTime == 1) {
			// Lấy data năng lượng theo ngày
			condition.put("viewType", 5);

		} else if (typeTime == 2) {
			// Lấy data năng lượng theo tháng
			condition.put("viewType", 3);

		} else if (typeTime == 3) {
			// Lấy data năng lượng theo năm
			condition.put("viewType", 2);

		} else if (typeTime == 4) {
			// Lấy data năng lượng theo tổng các năm
			condition.put("viewType", 1);

		} else if (typeTime == 5) {
			// Lấy data năng lượng theo ngày bắt đầu và ngày kết thúc
			condition.put("typeTime", 5);
			condition.put("viewType", 5);

		} else if (typeTime == 6) {
			// Lấy data năng lượng theo tuần
			condition.put("typeTime", 6);
			condition.put("viewType", 3);

		}

		List<DataLoadFrame1> dataNow = chartService.getChartLoadCompare(condition);

		JSONArray resultArray = new JSONArray();
		Integer cumulativeTotal = 0;

		Map<String, Object> mapData = new HashMap<>();
		mapData.put("dataNow", dataNow);

		log.info("getChartLoadCompare END");

		return new ResponseEntity<>(mapData, HttpStatus.OK);
	}

	@GetMapping("/temperature")
	public ResponseEntity<?> getDataTemperature(@RequestParam("fromDate") final String fromDate,
			@RequestParam("toDate") final String toDate, @RequestParam("customerId") final Integer customerId,
			@RequestParam("projectId") final String projectId, @RequestParam("typeTime") final Integer typeTime,
			@RequestParam("deviceId") final String deviceId, @RequestParam("systemTypeId") final String systemTypeId,
			@RequestParam(value = "ids", required = false) final String ids) throws ParseException {

		List<Chart> respone = new ArrayList<>();

		Map<String, Object> condition = new HashMap<>();
		String schema = Schema.getSchemas(customerId);
		String conditionMinute = "15";

		SimpleDateFormat yearFormatWithTime = new SimpleDateFormat("yyyy");

		String curYear = yearFormatWithTime.format(new Date());

		condition.put(SCHEMA, schema);
		if (Integer.parseInt(projectId) != 0) {
			condition.put(PROJECT_ID, projectId);
		}
		if (systemTypeId != "" && systemTypeId != "0") {
			condition.put(SYSTEM_TYPE_ID, systemTypeId);
		}
		if (deviceId != "" && deviceId != "0") {
			condition.put(DEVICE_ID, deviceId);
		}
		if (ids != "" && ids != "0") {
			condition.put("ids", ids);
		}

		condition.put("fromDate", fromDate);
		condition.put("toDate", toDate);
		condition.put("year", curYear);
		condition.put("minute", conditionMinute);

		respone = chartService.getChartTemperature(condition);

		JSONArray resultArray = new JSONArray();
		Float cumulativeTotal = 0.0f;

		for (Chart data : respone) {
			String name = data.getProjectName();
			Float value = data.getT();
			String time = data.getViewTime();
			String nameDevice = data.getDeviceName();

			// Tạo một đối tượng JSON đại diện cho mỗi bản ghi
			JSONObject recordObject = new JSONObject();
			recordObject.put(name, value);

			// Kiểm tra xem trường "time" đã tồn tại trong danh sách JSON chưa
			boolean timeExists = false;

			for (Object obj : resultArray) {
				if (obj instanceof JSONObject) {
					JSONObject timeObject = (JSONObject) obj;
					if (timeObject.containsKey("time") && timeObject.get("time").equals(time)) {
						Float total = timeObject.containsKey("total") ? (float) timeObject.get("total") : 0;
//							if (Integer.parseInt(projectId) != 0) {
//								timeObject.put(nameDevice, value);
//							} else {
//								timeObject.put(name, value);
//							}
						timeObject.put(nameDevice, value);
						cumulativeTotal += value;
						timeObject.put("total", cumulativeTotal);
						timeExists = true;
						break;
					}
				}
			}

			if (!timeExists) {
				JSONObject timeObject = new JSONObject();
				timeObject.put("time", time);
//					if (Integer.parseInt(projectId) != 0) {
//						timeObject.put(nameDevice, value);
//					} else {
//						timeObject.put(name, value);
//					}
				timeObject.put(nameDevice, value);

				cumulativeTotal += value;
				timeObject.put("total", cumulativeTotal);
				resultArray.add(timeObject);
			}
		}

		Map<String, Object> mapData = new HashMap<>();
		mapData.put("dataTemperature", resultArray);

		return new ResponseEntity<>(mapData, HttpStatus.OK);
	}

	@GetMapping("/sankey")
	public ResponseEntity<?> getChartSankey(@RequestParam("fromDate") final String fromDate,
			@RequestParam("toDate") final String toDate, @RequestParam("customerId") final Integer customerId,
			@RequestParam("projectId") final String projectId, @RequestParam("typeTime") final Integer typeTime,
			@RequestParam("deviceId") final String deviceId, @RequestParam("systemTypeId") final String systemTypeId,
			@RequestParam(value = "ids", required = false) final String ids) {
		log.info("getChartLoad START");
		String schema = Schema.getSchemas(customerId);
		Map<String, Object> condition = new HashMap<>();
		condition.put(SCHEMA, schema);
		if (Integer.parseInt(projectId) != 0) {
			condition.put(PROJECT_ID, projectId);
		}
		if (systemTypeId != "" && systemTypeId != "0") {
			condition.put(SYSTEM_TYPE_ID, systemTypeId);
		}
		if (deviceId != "" && deviceId != "0") {
			condition.put(DEVICE_ID, deviceId);
		}
		if (ids != "" && ids != "0") {
			condition.put("ids", ids);
		}

		condition.put("fromDate", fromDate);
		condition.put("toDate", toDate);
		if (typeTime == 1) {
			condition.put("viewType", 3);
		} else if (typeTime == 2) {
			condition.put("viewType", 2);
		} else if (typeTime == 3) {
			condition.put("viewType", 1);
		} else if (typeTime == 4) {
			condition.put("viewType", 1);
		} else if (typeTime == 5) {
			condition.put("typeTime", 5);
			condition.put("viewType", 5);
		}else if (typeTime == 6) {
			condition.put("typeTime", 6);
			condition.put("viewType", 3);
		}

		List<Chart> data = chartService.getChartSankey(condition);

		Map<String, Object> mapData = new HashMap<>();
		mapData.put("data", data);

		log.info("getChartLoad END");

		return new ResponseEntity<>(mapData, HttpStatus.OK);
	}

	@GetMapping("/discharge-indicator")
	public ResponseEntity<?> getDataDischargeIndicator(@RequestParam("fromDate") final String fromDate,
			@RequestParam("toDate") final String toDate, @RequestParam("customerId") final Integer customerId,
			@RequestParam("projectId") final String projectId, @RequestParam("typeTime") final Integer typeTime,
			@RequestParam("deviceId") final String deviceId, @RequestParam("systemTypeId") final String systemTypeId,
			@RequestParam(value = "ids", required = false) final String ids) throws ParseException {

		List<Chart> responeHTR02 = new ArrayList<>();
		List<Chart> responeAMS01 = new ArrayList<>();

		Map<String, Object> condition = new HashMap<>();
		String schema = Schema.getSchemas(customerId);
		String conditionMinute = "15";

		SimpleDateFormat yearFormatWithTime = new SimpleDateFormat("yyyy");

		String curYear = yearFormatWithTime.format(new Date());

		condition.put(SCHEMA, schema);
		if (Integer.parseInt(projectId) != 0) {
			condition.put(PROJECT_ID, projectId);
		}
		if (systemTypeId != "" && systemTypeId != "0") {
			condition.put(SYSTEM_TYPE_ID, systemTypeId);
		}
		if (deviceId != "" && deviceId != "0") {
			condition.put(DEVICE_ID, deviceId);
		}
		if (ids != "" && ids != "0") {
			condition.put("ids", ids);
		}

		condition.put("fromDate", fromDate);
		condition.put("toDate", toDate);
		condition.put("year", curYear);
		condition.put("minute", conditionMinute);

		responeHTR02 = chartService.getChartDischargeIndicatorHtr02(condition);
		responeAMS01 = chartService.getChartDischargeIndicatorAms01(condition);

		JSONArray resultArrayHTR02 = new JSONArray();
		Float cumulativeTotalHTR02 = 0.0f;

		JSONArray resultArrayAMS01 = new JSONArray();
		Float cumulativeTotalAMS01 = 0.0f;

		for (Chart data : responeHTR02) {
			String name = data.getProjectName();
			Float value = data.getIndicator();
			String time = data.getViewTime();
			String nameDevice = data.getDeviceName();

			// Tạo một đối tượng JSON đại diện cho mỗi bản ghi
			JSONObject recordObject = new JSONObject();
			recordObject.put(name, value);

			// Kiểm tra xem trường "time" đã tồn tại trong danh sách JSON chưa
			boolean timeExists = false;

			for (Object obj : resultArrayHTR02) {
				if (obj instanceof JSONObject) {
					JSONObject timeObject = (JSONObject) obj;
					if (timeObject.containsKey("time") && timeObject.get("time").equals(time)) {
						Float total = timeObject.containsKey("total") ? (float) timeObject.get("total") : 0;
						timeObject.put(nameDevice, value);
						cumulativeTotalHTR02 += value;
						timeObject.put("total", cumulativeTotalHTR02);
						timeExists = true;
						break;
					}
				}
			}

			if (!timeExists) {
				JSONObject timeObject = new JSONObject();
				timeObject.put("time", time);
				timeObject.put(nameDevice, value);

				cumulativeTotalHTR02 += value;
				timeObject.put("total", cumulativeTotalHTR02);
				resultArrayHTR02.add(timeObject);
			}
		}

		for (Chart data : responeAMS01) {
			String name = data.getProjectName();
			Float value = data.getIndicator();
			String time = data.getViewTime();
			String nameDevice = data.getDeviceName();

			// Tạo một đối tượng JSON đại diện cho mỗi bản ghi
			JSONObject recordObject = new JSONObject();
			recordObject.put(name, value);

			// Kiểm tra xem trường "time" đã tồn tại trong danh sách JSON chưa
			boolean timeExists = false;

			for (Object obj : resultArrayAMS01) {
				if (obj instanceof JSONObject) {
					JSONObject timeObject = (JSONObject) obj;
					if (timeObject.containsKey("time") && timeObject.get("time").equals(time)) {
						Float total = timeObject.containsKey("total") ? (float) timeObject.get("total") : 0;
						timeObject.put(nameDevice, value);
						cumulativeTotalAMS01 += value;
						timeObject.put("total", cumulativeTotalAMS01);
						timeExists = true;
						break;
					}
				}
			}

			if (!timeExists) {
				JSONObject timeObject = new JSONObject();
				timeObject.put("time", time);
				timeObject.put(nameDevice, value);

				cumulativeTotalAMS01 += value;
				timeObject.put("total", cumulativeTotalAMS01);
				resultArrayAMS01.add(timeObject);
			}
		}

		Map<String, Object> mapData = new HashMap<>();
		mapData.put("dataHTR02", resultArrayHTR02);
		mapData.put("dataAMS01", resultArrayAMS01);
		return new ResponseEntity<>(mapData, HttpStatus.OK);
	}

	@GetMapping("/export-data-chart-load")
	public ResponseEntity<?> exportDataChartLoad(@RequestParam("fromDate") final String fromDate,
			@RequestParam("toDate") final String toDate, @RequestParam("customerId") final Integer customerId,
			@RequestParam("projectId") final String projectId, @RequestParam("typeTime") final Integer typeTime,
			@RequestParam("deviceId") final String deviceId, @RequestParam("systemTypeId") final String systemTypeId,
			@RequestParam(value = "ids", required = false) final String ids) {
		log.info("getChartLoad START");
		String schema = Schema.getSchemas(customerId);
		Map<String, Object> condition = new HashMap<>();
		condition.put(SCHEMA, schema);
		if (Integer.parseInt(projectId) != 0) {
			condition.put(PROJECT_ID, projectId);
		}
		if (systemTypeId != "" && systemTypeId != "0") {
			condition.put(SYSTEM_TYPE_ID, systemTypeId);
		}
		if (deviceId != "" && deviceId != "0") {
			condition.put(DEVICE_ID, deviceId);
		}
		if (ids != "" && ids != "0") {
			condition.put("ids", ids);
		}

		condition.put("fromDate", fromDate);
		condition.put("toDate", toDate);
		if (typeTime == 1) {
			// Lấy data năng lượng theo ngày
			condition.put("viewType", 5);

		} else if (typeTime == 2) {
			// Lấy data năng lượng theo tháng
			condition.put("viewType", 3);

		} else if (typeTime == 3) {
			// Lấy data năng lượng theo năm
			condition.put("viewType", 2);

		} else if (typeTime == 4) {
			// Lấy data năng lượng theo tổng các năm
			condition.put("viewType", 1);

		} else if (typeTime == 5) {
			// Lấy data năng lượng theo ngày bắt đầu và ngày kết thúc
			condition.put("typeTime", 5);
			condition.put("viewType", 3);

		} else if (typeTime == 6) {
			// Lấy data năng lượng theo tuần
			condition.put("typeTime", 6);
			condition.put("viewType", 3);

		}
		List<DataLoadFrame1> dataComp = chartService.getChartLoadByCustomerId(condition);
		List<DataLoadFrame1> dataNow = chartService.getChartLoadByCustomerId(condition);

		JSONArray resultArray = new JSONArray();

		for (DataLoadFrame1 data : dataComp) {
			String name = data.getProjectName();
			Integer value = data.getEp();
			String time = data.getTime();
			String nameDevice = data.getDeviceName();

			// Tạo một đối tượng JSON đại diện cho mỗi bản ghi
			JSONObject recordObject = new JSONObject();
			recordObject.put(name, value);

			// Kiểm tra xem trường "time" đã tồn tại trong danh sách JSON chưa
			boolean timeExists = false;

			for (Object obj : resultArray) {
				if (obj instanceof JSONObject) {
					JSONObject timeObject = (JSONObject) obj;
					if (timeObject.containsKey("time") && timeObject.get("time").equals(time)) {
						timeExists = true;
						
						if (Integer.parseInt(projectId) != 0) {
							timeObject.put(nameDevice, value);
						} else {
							timeObject.put(name, value);
						}

						int total = timeObject.containsKey("total") ? (int) timeObject.get("total") : 0;
						total += value;
						timeObject.put("total", total);
//						timeExists = true;
						break;
					}
				}
			}

			if (!timeExists) {
				JSONObject timeObject = new JSONObject();
				timeObject.put("time", time);

				if (Integer.parseInt(projectId) != 0) {
					timeObject.put(nameDevice, value);
				} else {
					timeObject.put(name, value);
				}

				timeObject.put("total", value);
				resultArray.add(timeObject);
			}
		}

		Map<String, Object> mapData = new HashMap<>();
		mapData.put("dataLoadAllSite", resultArray);

		log.info("getChartLoad END");

		String reportName = "Dữ liệu năng lượng";

		Date dateNow = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		String strDate = formatter.format(dateNow);

		String projectNameExport = "";
		String fromTimeExport = "";
		String toTimeExport = "";
		String timeNameFile = fromDate + toDate;

		// get Customer
		Map<String, String> cus = new HashMap<>();
		cus.put("customerId", customerId + "");
		Customer custtomer = customerService.getCustomer(cus);
		// systemType
		String moduleName = "";
		Integer system = Integer.valueOf(systemTypeId);
		if (system == 1) {
			moduleName = "TẢI ĐIỆN";
		} else if (system == 2) {
			moduleName = "ĐIỆN MẶT TRỜI";
		} else if (system == 3) {
			moduleName = "ĐIỆN GIÓ";
		} else if (system == 4) {
			moduleName = "PIN LƯU TRỮ";
		} else if (system == 5) {
			moduleName = "LƯỚI ĐIỆN";
		}

		// getProject
		Map<String, String> pro = new HashMap<>();
		pro.put("projectId", projectId + "");
		if (Integer.valueOf(projectId) == 0) {
			projectNameExport = "";
		} else {
			Project projectExport = projectService.getProject(pro);
			projectNameExport = projectExport.getProjectName();
		}

		List<DataLoadFrame1> listName = new ArrayList<>();

		if (Integer.parseInt(projectId) != 0) {
			listName = dataComp.stream().collect(Collectors.toMap(obj -> obj.getDeviceName(), Function.identity(),
					(existing, replacement) -> existing)).values().stream().collect(Collectors.toList());
		} else {
			listName = dataComp.stream().collect(Collectors.toMap(obj -> obj.getProjectName(), Function.identity(),
					(existing, replacement) -> existing)).values().stream().collect(Collectors.toList());
		}

		long miliseconds = new Date().getTime();
		String path = this.folderName + File.separator + "DNLN" + "_"
				+ convertToCamelCase(custtomer.getCustomerName()).toUpperCase() + "_"
				+ convertToCamelCase(moduleName).toUpperCase() + "_" + "NANGLUONG" + "_"
				+ convertToCamelCase(timeNameFile) + "_" + miliseconds;

		String fileNameExcel = "DNLN" + "_" + convertToCamelCase(custtomer.getCustomerName()).toUpperCase() + "_"
				+ convertToCamelCase(moduleName).toUpperCase() + "_" + "NANGLUONG" + "_"
				+ convertToCamelCase(timeNameFile) + "_" + miliseconds;

		if (resultArray.size() <= 0) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
		try {
			createDataEnergyExcel(resultArray, custtomer.getCustomerName().toUpperCase(), custtomer.getDescription(),
					typeTime, reportName, Integer.valueOf(systemTypeId), moduleName, projectNameExport, fromDate,
					toDate, strDate, path, fileNameExcel, listName, projectId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		log.info("ChartController.downloadDataChart() START");
		File f = new File(path);
		if (f.exists()) {
			log.info("ReportController.downloadReport() check file exists");
			String contentType = "application/zip";
			String headerValue = "attachment; filename=" + f.getName() + ".zip";
			Path realPath = Paths.get(path + ".zip");
			Resource resource = null;
			try {
				resource = new UrlResource(realPath.toUri());

			} catch (Exception e) {
				e.printStackTrace();
			}
			log.info("ChartController.downloadDataChart() END");
			return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
					.header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
					.header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION).body(resource);

		} else {
			log.info("ReportController.downloadDataChart() error");
			return new ResponseEntity<Resource>(HttpStatus.BAD_REQUEST);
		}
//        return new ResponseEntity<>(mapData, HttpStatus.OK);
	}

	@GetMapping("/load-power")
	public ResponseEntity<?> getChartLoadPower(@RequestParam("fromDate") final String fromDate,
			@RequestParam("toDate") final String toDate, @RequestParam("customerId") final Integer customerId,
			@RequestParam("projectId") final String projectId, @RequestParam("typeTime") final Integer typeTime,
			@RequestParam("deviceId") final String deviceId, @RequestParam("systemTypeId") final String systemTypeId,
			@RequestParam(value = "ids", required = false) final String ids) {
		log.info("getChartLoadPower START");
		String schema = Schema.getSchemas(customerId);
		Map<String, Object> condition = new HashMap<>();
		condition.put(SCHEMA, schema);
		if (Integer.parseInt(projectId) != 0) {
			condition.put(PROJECT_ID, projectId);
		}
		if (systemTypeId != "" && systemTypeId != "0") {
			condition.put(SYSTEM_TYPE_ID, systemTypeId);
		}
		if (deviceId != "" && deviceId != "0") {
			condition.put(DEVICE_ID, deviceId);
		}
		if (ids != "" && ids != "0") {
			condition.put("ids", ids);
		}
		List<Chart> dataComp = new ArrayList<>();
		List<Chart> dataNow = new ArrayList<>();
		condition.put("fromDate", fromDate);
		condition.put("toDate", toDate);

		if (typeTime == 1) {
			condition.put("viewType", 5);
			condition.put("typeTime", 5);
		} else if (typeTime == 2) {
			condition.put("typeTime", 5);
			condition.put("viewType", 5);
			condition.put("toDate", toDate);
		} else if (typeTime == 3) {
			condition.put("viewType", 2);
		} else if (typeTime == 4) {
			condition.put("viewType", 1);
		} else if (typeTime == 5) {
			condition.put("typeTime", 5);
			condition.put("viewType", 5);
			condition.put("toDate", toDate);
		} else if (typeTime == 6) {
			condition.put("typeTime", 6);
			condition.put("viewType", 5);
			condition.put("toDate", toDate);
		}
		if (typeTime == 5 || typeTime == 3 || typeTime == 2) {
			condition.put("fromDate", fromDate);
		}
		dataComp = chartService.getChartLoadPower(condition);

		dataNow = chartService.getChartLoadPower(condition);
		for (Chart data : dataComp) {
			if (data.getPTotal() == null) {
				data.setPTotal(Float.valueOf(0));
			}
		}

		Float cumulativeTotal = 0.00f;
		JSONArray resultArray = new JSONArray();
		for (Chart data : dataComp) {
			String name = data.getProjectName();
			Float value = data.getPTotal();
			String time = data.getTime();
			String nameDevice = data.getDeviceName();
			// Tạo một đối tượng JSON đại diện cho mỗi bản ghi
			JSONObject recordObject = new JSONObject();
			recordObject.put(name, value);

			// Kiểm tra xem trường "time" đã tồn tại trong danh sách JSON chưa
			boolean timeExists = false;
			for (Object obj : resultArray) {
				if (obj instanceof JSONObject) {
					JSONObject timeObject = (JSONObject) obj;
					if (timeObject.containsKey("time") && timeObject.get("time").equals(time)) {
						Float total = timeObject.containsKey("total") ? (float) timeObject.get("total") : 0;
						if (Integer.parseInt(projectId) != 0) {
							timeObject.put(nameDevice, value);
						} else {
							timeObject.put(name, value);
						}
						cumulativeTotal += value;
						timeObject.put("total", cumulativeTotal);
						timeExists = true;
						break;
					}
				}
			}
			// Nếu trường "time" chưa tồn tại, tạo một đối tượng JSON mới và thêm vào danh
			// sách
			if (!timeExists) {
				JSONObject timeObject = new JSONObject();
				timeObject.put("time", time);
				if (Integer.parseInt(projectId) != 0) {
					timeObject.put(nameDevice, value);
				} else {
					timeObject.put(name, value);
				}
				cumulativeTotal += value;
				timeObject.put("total", cumulativeTotal);
				resultArray.add(timeObject);
			}
		}
		Map<String, Object> mapData = new HashMap<>();
		mapData.put("dataNow", dataNow);
		mapData.put("dataComp", dataComp);
		mapData.put("dataLoadAllSite", resultArray);
		log.info("getChartLoadPower END");

		return new ResponseEntity<>(mapData, HttpStatus.OK);
	}

	@GetMapping("/export-data-chart-power")
	public ResponseEntity<?> exportDataChartPower(@RequestParam("fromDate") final String fromDate,
			@RequestParam("toDate") final String toDate, @RequestParam("customerId") final Integer customerId,
			@RequestParam("projectId") final String projectId, @RequestParam("typeTime") final Integer typeTime,
			@RequestParam("deviceId") final String deviceId, @RequestParam("systemTypeId") final String systemTypeId,
			@RequestParam(value = "ids", required = false) final String ids) {
		log.info("getChartLoadPower START");
		String schema = Schema.getSchemas(customerId);
		Map<String, Object> condition = new HashMap<>();
		condition.put(SCHEMA, schema);
		if (Integer.parseInt(projectId) != 0) {
			condition.put(PROJECT_ID, projectId);
		}
		if (systemTypeId != "" && systemTypeId != "0") {
			condition.put(SYSTEM_TYPE_ID, systemTypeId);
		}
		if (deviceId != "" && deviceId != "0") {
			condition.put(DEVICE_ID, deviceId);
		}
		if (ids != "" && ids != "0") {
			condition.put("ids", ids);
		}
		List<Chart> dataComp = new ArrayList<>();
		List<Chart> dataNow = new ArrayList<>();
		condition.put("fromDate", fromDate);
		condition.put("toDate", toDate);

		if (typeTime == 1) {
			// typeTime = 1 là so sánh theo hôm nay - hôm qua, lấy giữ liệu giờ trong ngày
			condition.put("viewType", 5);
			condition.put("typeTime", 5);
		} else if (typeTime == 2) {
			// typeTime = 2 là so sánh theo tháng này - tháng trước, lấy giữ liệu ngày trong
			// tháng
			condition.put("typeTime", 5);
			condition.put("viewType", 5);
			condition.put("toDate", toDate);
		} else if (typeTime == 3) {
			// typeTime = 3 là so sánh theo năm nay - năm sau, lấy giữ liệu tháng trong năm
			condition.put("viewType", 2);
		} else if (typeTime == 4) {
			// typeTime = 4 là so sánh theo năm
			condition.put("viewType", 1);
		} else if (typeTime == 5) {
			// Tùy chọn
			condition.put("typeTime", 5);
			condition.put("viewType", 5);
			condition.put("toDate", toDate);
		} else if (typeTime == 6) {
			condition.put("typeTime", 6);
			condition.put("viewType", 5);
			condition.put("toDate", toDate);
		}
		if (typeTime == 5 || typeTime == 3 || typeTime == 2) {
			condition.put("fromDate", fromDate);
		}
		dataComp = chartService.getChartLoadPower(condition);

		dataNow = chartService.getChartLoadPower(condition);
		for (Chart data : dataComp) {
			if (data.getPTotal() == null) {
				data.setPTotal(Float.valueOf(0));
			}
		}

		Float cumulativeTotal = 0.00f;
		JSONArray resultArray = new JSONArray();
		for (Chart data : dataComp) {
			String name = data.getProjectName();
			Float value = data.getPTotal();
			String time = data.getTime();
			String nameDevice = data.getDeviceName();
			// Tạo một đối tượng JSON đại diện cho mỗi bản ghi
			JSONObject recordObject = new JSONObject();
			recordObject.put(name, value);

			// Kiểm tra xem trường "time" đã tồn tại trong danh sách JSON chưa
			boolean timeExists = false;
			for (Object obj : resultArray) {
				if (obj instanceof JSONObject) {
					JSONObject timeObject = (JSONObject) obj;
					if (timeObject.containsKey("time") && timeObject.get("time").equals(time)) {
						timeExists = true;
						
						if (Integer.parseInt(projectId) != 0) {
							timeObject.put(nameDevice, value);
						} else {
							timeObject.put(name, value);
						}
						Float total = timeObject.containsKey("total") ? (Float) timeObject.get("total") : 0;
						total += value;
						timeObject.put("total", total);
//						timeExists = true;
						break;
					}
				}
			}
			// Nếu trường "time" chưa tồn tại, tạo một đối tượng JSON mới và thêm vào danh
			// sách
			if (!timeExists) {
				JSONObject timeObject = new JSONObject();
				timeObject.put("time", time);
				if (Integer.parseInt(projectId) != 0) {
					timeObject.put(nameDevice, value);
				} else {
					timeObject.put(name, value);
				}
				timeObject.put("total", value);
				resultArray.add(timeObject);
			}
		}
		Map<String, Object> mapData = new HashMap<>();
		mapData.put("dataLoadAllSite", resultArray);
		log.info("getChartLoadPower END");

		String reportName = "Dữ liệu công suất";

		Date dateNow = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		String strDate = formatter.format(dateNow);

		String projectNameExport = "";
		String fromTimeExport = "";
		String toTimeExport = "";
		String timeNameFile = fromDate + toDate;
		// get Customer
		Map<String, String> cus = new HashMap<>();
		cus.put("customerId", customerId + "");
		Customer custtomer = customerService.getCustomer(cus);
		// systemType
		String moduleName = "";
		Integer system = Integer.valueOf(systemTypeId);
		if (system == 1) {
			moduleName = "TẢI ĐIỆN";
		} else if (system == 2) {
			moduleName = "ĐIỆN MẶT TRỜI";
		} else if (system == 3) {
			moduleName = "ĐIỆN GIÓ";
		} else if (system == 4) {
			moduleName = "PIN LƯU TRỮ";
		} else if (system == 5) {
			moduleName = "LƯỚI ĐIỆN";
		}

		// getProject
		Map<String, String> pro = new HashMap<>();
		pro.put("projectId", projectId + "");
		if (Integer.valueOf(projectId) == 0) {
			projectNameExport = "";
		} else {
			Project projectExport = projectService.getProject(pro);
			projectNameExport = projectExport.getProjectName();
		}

		List<Chart> listName = new ArrayList<>();

		if (Integer.parseInt(projectId) != 0) {
			listName = dataComp.stream().collect(Collectors.toMap(obj -> obj.getDeviceName(), Function.identity(),
					(existing, replacement) -> existing)).values().stream().collect(Collectors.toList());
		} else {
			listName = dataComp.stream().collect(Collectors.toMap(obj -> obj.getProjectName(), Function.identity(),
					(existing, replacement) -> existing)).values().stream().collect(Collectors.toList());
		}

		long miliseconds = new Date().getTime();
		String path = this.folderName + File.separator + "DLCS" + "_"
				+ convertToCamelCase(custtomer.getCustomerName()).toUpperCase() + "_"
				+ convertToCamelCase(moduleName).toUpperCase() + "_" + "CONGSUAT" + "_"
				+ convertToCamelCase(timeNameFile) + "_" + miliseconds;

		String fileNameExcel = "DLCS" + "_" + convertToCamelCase(custtomer.getCustomerName()).toUpperCase() + "_"
				+ convertToCamelCase(moduleName).toUpperCase() + "_" + "CONGSUAT" + "_"
				+ convertToCamelCase(timeNameFile) + "_" + miliseconds;

		if (resultArray.size() <= 0) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
		try {
			createDataPowerExcel(resultArray, custtomer.getCustomerName().toUpperCase(), custtomer.getDescription(),
					typeTime, reportName, Integer.valueOf(systemTypeId), moduleName, projectNameExport, fromDate,
					toDate, strDate, path, fileNameExcel, listName, projectId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		log.info("ChartController.downloadDataChartPower() START");
		File f = new File(path);
		if (f.exists()) {
			log.info("ChartController.downloadDataChartPower() check file exists");
			String contentType = "application/zip";
			String headerValue = "attachment; filename=" + f.getName() + ".zip";
			Path realPath = Paths.get(path + ".zip");
			Resource resource = null;
			try {
				resource = new UrlResource(realPath.toUri());

			} catch (Exception e) {
				e.printStackTrace();
			}
			log.info("ChartController.downloadReport() END");
			return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
					.header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
					.header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION).body(resource);

		} else {
			log.info("ReportController.downloadReport() error");
			return new ResponseEntity<Resource>(HttpStatus.BAD_REQUEST);
		}

//		return new ResponseEntity<>(mapData, HttpStatus.OK);
	}

	@GetMapping("/load-cost")
	public ResponseEntity<?> getChartLoadCost(@RequestParam("fromDate") final String fromDate,
			@RequestParam("toDate") final String toDate, @RequestParam("customerId") final Integer customerId,
			@RequestParam("projectId") final String projectId, @RequestParam("typeTime") final Integer typeTime,
			@RequestParam("deviceId") final String deviceId, @RequestParam("systemTypeId") final String systemTypeId,
			@RequestParam(value = "ids", required = false) final String ids) {
		log.info("getChartCostLoad START");
		String schema = Schema.getSchemas(customerId);
		Map<String, Object> condition = new HashMap<>();
		Map<String, Object> mapData = new HashMap<>();
		List<Chart> dataComp = new ArrayList<>();
		List<Chart> chart = new ArrayList<>();

		condition.put(SCHEMA, schema);
		if (Integer.parseInt(projectId) != 0) {
			condition.put(PROJECT_ID, projectId);
		}
		if (systemTypeId != "" && systemTypeId != "0") {
			condition.put(SYSTEM_TYPE_ID, systemTypeId);
		}
		if (deviceId != "" && deviceId != "0") {
			condition.put(DEVICE_ID, deviceId);
		}
		if (ids != "" && ids != "0") {
			condition.put("ids", ids);
		}
		condition.put("fromDate", fromDate);
		condition.put("toDate", toDate);

		if (typeTime == 1) {
			condition.put("viewType", 5);
		} else if (typeTime == 5) {
			condition.put("viewType", 3);
			condition.put("typeTime", 5);
		} else if (typeTime == 2) {
			condition.put("viewType", 3);
		} else if (typeTime == 3) {
			condition.put("viewType", 2);
		} else if (typeTime == 4) {
			condition.put("viewType", 1);
		} else if (typeTime == 6) {
			condition.put("viewType", 3);
			condition.put("typeTime", 6);
		}
		dataComp = chartService.getChartLoadCostByCustomerId(condition);

		List<Chart> chartLoadCost = new ArrayList<>();

		for (Chart chartX : dataComp) {
			float cost = (chartX.getCostHighIn() == null ? 0 : chartX.getCostHighIn())
					+ (chartX.getCostMediumIn() == null ? 0 : chartX.getCostMediumIn())
					+ (chartX.getCostLowIn() == null ? 0 : chartX.getCostLowIn());
			chartX.setCost(cost);
			chartLoadCost.add(chartX);
		}

		JSONArray resultArray = new JSONArray();
		Float cumulativeTotal = 0.00f;
		for (Chart data : chartLoadCost) {
			String name = data.getProjectName();
			String nameEp = data.getProjectName() + "EpChartCostData";
			float value = data.getCost();
			float valueEP = data.getEpIn() != null ? data.getEpIn() : 0.0f;
			String time = data.getViewTime();
			String nameDevice = data.getDeviceName();
			String nameDeviceEp = data.getDeviceName() + "EpChartCostData";
			// Tạo một đối tượng JSON đại diện cho mỗi bản ghi
			JSONObject recordObject = new JSONObject();
			recordObject.put(name, value);

			// Kiểm tra xem trường "time" đã tồn tại trong danh sách JSON chưa
			boolean timeExists = false;
			for (Object obj : resultArray) {
				if (obj instanceof JSONObject) {
					JSONObject timeObject = (JSONObject) obj;
					if (timeObject.containsKey("time") && timeObject.get("time").equals(time)) {
						Float total = timeObject.containsKey("total") ? (Float) timeObject.get("total") : 0;
						if (Integer.parseInt(projectId) != 0) {
							timeObject.put(nameDevice, value);
							timeObject.put(nameDeviceEp, valueEP);
						} else {
							timeObject.put(nameEp, valueEP);
							timeObject.put(name, value);
						}

						cumulativeTotal += value;
						timeObject.put("total", cumulativeTotal);
						timeExists = true;
						break;
					}
				}
			}

			// Nếu trường "time" chưa tồn tại, tạo một đối tượng JSON mới và thêm vào danh
			// sách

			if (!timeExists) {
				JSONObject timeObject = new JSONObject();
				timeObject.put("time", time);
				if (Integer.parseInt(projectId) != 0) {
					timeObject.put(nameDevice, value);
					timeObject.put(nameDeviceEp, valueEP);
				} else {
					timeObject.put(nameEp, valueEP);
					timeObject.put(name, value);
				}
				cumulativeTotal += value;
				timeObject.put("total", cumulativeTotal);
				resultArray.add(timeObject);
			}
		}

		mapData.put("data", resultArray);
		mapData.put("chart", chart);
		mapData.put("dataComp", dataComp);
		log.info("getChartCostLoad END");
		return new ResponseEntity<>(mapData, HttpStatus.OK);
	}

	@GetMapping("/export-data-chart-cost")
	public ResponseEntity<?> exportDataChartCost(@RequestParam("fromDate") final String fromDate,
			@RequestParam("toDate") final String toDate, @RequestParam("customerId") final Integer customerId,
			@RequestParam("projectId") final String projectId, @RequestParam("typeTime") final Integer typeTime,
			@RequestParam("deviceId") final String deviceId, @RequestParam("systemTypeId") final String systemTypeId,
			@RequestParam(value = "ids", required = false) final String ids) {
		log.info("getChartCostLoad START");
		String schema = Schema.getSchemas(customerId);
		Map<String, Object> condition = new HashMap<>();
		Map<String, Object> mapData = new HashMap<>();
		List<Chart> dataComp = new ArrayList<>();
		List<Chart> chart = new ArrayList<>();

		condition.put(SCHEMA, schema);
		if (Integer.parseInt(projectId) != 0) {
			condition.put(PROJECT_ID, projectId);
		}
		if (systemTypeId != "" && systemTypeId != "0") {
			condition.put(SYSTEM_TYPE_ID, systemTypeId);
		}
		if (deviceId != "" && deviceId != "0") {
			condition.put(DEVICE_ID, deviceId);
		}

		if (ids != "" && ids != "0") {
			condition.put("ids", ids);
		}
		condition.put("fromDate", fromDate);
		condition.put("toDate", toDate);
		if (typeTime == 1) {
			condition.put("viewType", 5);
		} else if (typeTime == 5) {
			condition.put("viewTime", 3);
			condition.put("typeTime", 5);
		} else if (typeTime == 2) {
			condition.put("viewType", 3);
		} else if (typeTime == 3) {
			condition.put("viewType", 2);
		} else if (typeTime == 4) {
			condition.put("viewType", 1);
		} else if (typeTime == 6) {
			condition.put("viewType", 3);
			condition.put("typeTime", 6);
		}

		dataComp = chartService.getChartLoadCostByCustomerId(condition);

		List<Chart> chartLoadCost = new ArrayList<>();

		for (Chart chartX : dataComp) {
			float cost = (chartX.getCostHighIn() == null ? 0 : chartX.getCostHighIn())
					+ (chartX.getCostMediumIn() == null ? 0 : chartX.getCostMediumIn())
					+ (chartX.getCostLowIn() == null ? 0 : chartX.getCostLowIn());
			chartX.setCost(cost);
			chartLoadCost.add(chartX);
		}

		JSONArray resultArray = new JSONArray();
		Float cumulativeTotal = 0.00f;
		for (Chart data : chartLoadCost) {
			String name = data.getProjectName();
			float value = data.getCost();
			String time = data.getViewTime();
			String nameDevice = data.getDeviceName();
			// Tạo một đối tượng JSON đại diện cho mỗi bản ghi
			JSONObject recordObject = new JSONObject();
			recordObject.put(name, value);

			// Kiểm tra xem trường "time" đã tồn tại trong danh sách JSON chưa
			boolean timeExists = false;
			for (Object obj : resultArray) {
				if (obj instanceof JSONObject) {
					JSONObject timeObject = (JSONObject) obj;
					if (timeObject.containsKey("time") && timeObject.get("time").equals(time)) {
						timeExists = true;
						if (Integer.parseInt(projectId) != 0) {
							timeObject.put(nameDevice, value);
						} else {
							timeObject.put(name, value);
						}

						float total = timeObject.containsKey("total") ? (float) timeObject.get("total") : 0;
						total += value;
						timeObject.put("total", total);
//						timeExists = true;
						break;
					}
				}
			}

			// Nếu trường "time" chưa tồn tại, tạo một đối tượng JSON mới và thêm vào danh
			// sách

			if (!timeExists) {
				JSONObject timeObject = new JSONObject();
				timeObject.put("time", time);
				if (Integer.parseInt(projectId) != 0) {
					timeObject.put(nameDevice, value);
				} else {
					timeObject.put(name, value);
				}
				timeObject.put("total", value);
				resultArray.add(timeObject);
			}
		}

		mapData.put("data", resultArray);

		log.info("getChartCostLoad END");

		String reportName = "Dữ liệu tiền điện";

		Date dateNow = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		String strDate = formatter.format(dateNow);

		String projectNameExport = "";
		String fromTimeExport = "";
		String toTimeExport = "";
		String timeNameFile = fromDate + toDate;

		// get Customer
		Map<String, String> cus = new HashMap<>();
		cus.put("customerId", customerId + "");
		Customer custtomer = customerService.getCustomer(cus);
		// systemType
		String moduleName = "";
		Integer system = Integer.valueOf(systemTypeId);
		if (system == 1) {
			moduleName = "TẢI ĐIỆN";
		} else if (system == 2) {
			moduleName = "ĐIỆN MẶT TRỜI";
		} else if (system == 3) {
			moduleName = "ĐIỆN GIÓ";
		} else if (system == 4) {
			moduleName = "PIN LƯU TRỮ";
		} else if (system == 5) {
			moduleName = "LƯỚI ĐIỆN";
		}

		// getProject
		Map<String, String> pro = new HashMap<>();
		pro.put("projectId", projectId + "");
		if (Integer.valueOf(projectId) == 0) {
			projectNameExport = "";
		} else {
			Project projectExport = projectService.getProject(pro);
			projectNameExport = projectExport.getProjectName();
		}

		List<Chart> listName = new ArrayList<>();

		if (Integer.parseInt(projectId) != 0) {
			listName = dataComp.stream().collect(Collectors.toMap(obj -> obj.getDeviceName(), Function.identity(),
					(existing, replacement) -> existing)).values().stream().collect(Collectors.toList());
		} else {
			listName = dataComp.stream().collect(Collectors.toMap(obj -> obj.getProjectName(), Function.identity(),
					(existing, replacement) -> existing)).values().stream().collect(Collectors.toList());
		}

		long miliseconds = new Date().getTime();
		String path = this.folderName + File.separator + "DLTD" + "_"
				+ convertToCamelCase(custtomer.getCustomerName()).toUpperCase() + "_"
				+ convertToCamelCase(moduleName).toUpperCase() + "_" + "TIENDIEN" + "_"
				+ convertToCamelCase(timeNameFile) + "_" + miliseconds;

		String fileNameExcel = "DLTD" + "_" + convertToCamelCase(custtomer.getCustomerName()).toUpperCase() + "_"
				+ convertToCamelCase(moduleName).toUpperCase() + "_" + "TIENDIEN" + "_"
				+ convertToCamelCase(timeNameFile) + "_" + miliseconds;

		if (resultArray.size() <= 0) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
		try {
			createDataCostExcel(resultArray, custtomer.getCustomerName().toUpperCase(), custtomer.getDescription(),
					typeTime, reportName, Integer.valueOf(systemTypeId), moduleName, projectNameExport, fromDate,
					toDate, strDate, path, fileNameExcel, listName, projectId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		log.info("ChartController.downloadDataCost() START");
		File f = new File(path);
		if (f.exists()) {
			log.info("ChartController.downloadDataCost check file exists");
			String contentType = "application/zip";
			String headerValue = "attachment; filename=" + f.getName() + ".zip";
			Path realPath = Paths.get(path + ".zip");
			Resource resource = null;
			try {
				resource = new UrlResource(realPath.toUri());

			} catch (Exception e) {
				e.printStackTrace();
			}
			log.info("ChartController.downloadDataCost END");
			return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
					.header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
					.header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION).body(resource);

		} else {
			log.info("ChartController.downloadDataCost error");
			return new ResponseEntity<Resource>(HttpStatus.BAD_REQUEST);
		}
	}

	public String switchViewTime(Integer value, String year) {
		switch (value) {
		case 1:
			return "01-" + year;
		case 2:
			return "02-" + year;
		case 3:
			return "03-" + year;
		case 4:
			return "04-" + year;
		case 5:
			return "05-" + year;
		case 6:
			return "06-" + year;
		case 7:
			return "07-" + year;
		case 8:
			return "08-" + year;
		case 9:
			return "09-" + year;
		case 10:
			return "10-" + year;
		case 11:
			return "11-" + year;
		case 12:
			return "12-" + year;
		default:
			return "Invalid input";
		}
	}

	public String switchViewYear(Integer value, String year) {
		switch (value) {
		case 1:
			return year + "-01";
		case 2:
			return year + "-02";
		case 3:
			return year + "-03";
		case 4:
			return year + "-04";
		case 5:
			return year + "-05";
		case 6:
			return year + "-06";
		case 7:
			return year + "-07";
		case 8:
			return year + "-08";
		case 9:
			return year + "-09";
		case 10:
			return year + "-10";
		case 11:
			return year + "-11";
		case 12:
			return year + "-12";
		default:
			return "Invalid input";
		}
	}

	@GetMapping("/energy-plan")
	public ResponseEntity<?> getChartEnergyPlan(@RequestParam("customerId") final Integer customerId,
			@RequestParam(value = "projectId", required = false) final Integer projectId,
			@RequestParam(value = "time", required = false) final Integer time,
			@RequestParam(value = "typeModule", required = false) final Integer type,
			@RequestParam(value = "fDate", required = false) final String fDate,
			@RequestParam(value = "tDate", required = false) final String tDate,
			@RequestParam(value = "deviceId", required = false) final String deviceId,
			@RequestParam(value = "ids", required = false) final String ids) {
		log.info("getChartEnergyPlan START");
		List<Object> result = new ArrayList<>();

		String day = DateUtils.toString(new Date(), Constants.ES.DATE_FORMAT_YMD);
		String month = DateUtils.toString(new Date(), Constants.ES.DATE_FORMAT_YM_02);
		String currMonth = DateUtils.toString(new Date(), "MM");
		String year = DateUtils.toString(new Date(), "yyyy");
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat formatterMonth = new SimpleDateFormat("yyyy-MM");

		HashMap<String, String> condition = new HashMap<>();
		String proIds = "";

		if (projectId == 0) {

			condition.put("customerId", String.valueOf(customerId));
			if (ids == "") {
				proIds = null;
			} else {
				proIds = ids;
			}
			List<Project> listProject = projectService.getListProjectByCustomerId(String.valueOf(customerId), proIds);

			for (Project project : listProject) {

				DataPowerResult obj = new DataPowerResult();
				obj.setName(project.getProjectName());
				List<DataPower> listPower = new ArrayList<>();
				// List<LandmarksPlansEnergy> listPower = new ArrayList<>();
				List<LandmarksPlansEnergy> listPowerLandmark = new ArrayList<>();
				List<LandmarksPlansEnergy> listEnergyPower = new ArrayList<>();
				condition.put("projectId", String.valueOf(project.getProjectId()));
				condition.put("schema", Schema.getSchemas(customerId));
				condition.put("day", day);
				condition.put("month", month);
				condition.put("year", year);
				condition.put("energy", "energy");

				if (time == 0) {
					Calendar calendar = Calendar.getInstance();
					Date date = calendar.getTime();
					String currDayOfWeek = new SimpleDateFormat("dd", Locale.ENGLISH).format(date.getTime());
					String currentMonth = new SimpleDateFormat("MMM", Locale.ENGLISH).format(date.getTime());
					String currentYear = new SimpleDateFormat("YYYY", Locale.ENGLISH).format(date.getTime());
					String dayOrFdate = new SimpleDateFormat("YYYY-MM-dd", Locale.ENGLISH).format(date.getTime());

					if (fDate != null) {
						condition.put("day", fDate);
						try {
							date = formatter.parse(fDate);
							currDayOfWeek = new SimpleDateFormat("dd", Locale.ENGLISH).format(date.getTime());
							currentMonth = new SimpleDateFormat("MMM", Locale.ENGLISH).format(date.getTime());
							currentYear = fDate.substring(0, 4);
							dayOrFdate = new SimpleDateFormat("YYYY-MM-dd", Locale.ENGLISH).format(date.getTime());
						} catch (ParseException e) {
							e.printStackTrace();
						}
					}

					List<DataPower> listTimePower = new ArrayList<>();
					for (int hour = 0; hour < 24; hour++) {
						LocalTime viewTime = LocalTime.of(hour, 0, 0);
						DateTimeFormatter formatTime = DateTimeFormatter.ofPattern("HH:mm:ss");
						DataPower data = new DataPower(dayOrFdate + " " + (viewTime).format(formatTime));
						listTimePower.add(data);
					}

					if (type == 1) {
						listPower = deviceService.getListEpLoadByDay(condition);

						// get plan energy
						/*-- Get data user input  */
						HashMap<String, String> condi = new HashMap<>();
						condi.put("schema", Schema.getSchemas(customerId));
						condi.put("currDayOfWeek", currDayOfWeek);
						condi.put("currentMonth", currentMonth);
						condi.put("projectId", String.valueOf(project.getProjectId()));
						condi.put("systemTypeId", String.valueOf(type));
						condi.put("currentYear", currentYear);
						Double planEnergyInput = landmarksPlanEnergyService.getEnergyByDayAndMonth(condi);
						Double targetEnergyInput = landmarksPlanEnergyService.getLandmarksEnergyByDayAndMonth(condi);

						if (planEnergyInput == null) {
							planEnergyInput = 0.0;
						}

						if (targetEnergyInput == null) {
							targetEnergyInput = 0.0;
						}

						if ((planEnergyInput != null && planEnergyInput != 0)
								|| (targetEnergyInput != null && targetEnergyInput != 0)) {
							if (targetEnergyInput != null && targetEnergyInput != 0) {
								Double accumulationPlan = 0.0;
								Double accumulationTarget = 0.0;

								for (DataPower data : listTimePower) {
									if (listPower.size() > 0) {
										for (DataPower dataPower : listPower) {
											if (dataPower.getViewTime().equals(data.getViewTime())) {
												data.setPower(dataPower.getPower());
											}
										}
									}
									data.setTargetEnergy(accumulationTarget);
									if (targetEnergyInput != null && targetEnergyInput != 0) {
										data.setPlanEnergy(accumulationPlan);
									}
									if (planEnergyInput == null || planEnergyInput == 0) {
										data.setPlanEnergy(accumulationTarget != null ? accumulationTarget * 0.95 : 0);
									}
									accumulationPlan = accumulationPlan + (planEnergyInput / 23);
									accumulationTarget = accumulationTarget + (targetEnergyInput / 23);
								}
							} else {
								Double accumulationPlan = 0.0;
								Double accumulationTarget = 0.0;

								for (DataPower data : listTimePower) {
									for (DataPower dataPower : listPower) {
										if (dataPower.getViewTime().equals(data.getViewTime())) {
											data.setPower(dataPower.getPower());
										}
									}
									data.setPlanEnergy(accumulationPlan / 0.95);
									data.setTargetEnergy(accumulationPlan);

									accumulationPlan = accumulationPlan + (planEnergyInput / 23);
									accumulationTarget = accumulationTarget + (targetEnergyInput / 23);
								}

							}
						} else {
							listTimePower = listPower;
						}

						obj.setListDataPower(listTimePower);
					}

				}

				if (time == 1) {

					Calendar calendar = Calendar.getInstance();
					Date date = calendar.getTime();
					String currentMonth = new SimpleDateFormat("MMM", Locale.ENGLISH).format(date.getTime());
					String currentYear = new SimpleDateFormat("YYYY", Locale.ENGLISH).format(date.getTime());
					if (fDate != null) {
						condition.put("month", fDate);
						try {
							date = formatterMonth.parse(fDate);
							currentMonth = new SimpleDateFormat("MMM", Locale.ENGLISH).format(date.getTime());
							currMonth = DateUtils.toString(date, "MM");
							currentYear = new SimpleDateFormat("YYYY", Locale.ENGLISH).format(date.getTime());
						} catch (ParseException e) {
							e.printStackTrace();
						}
					}

					if (type == 1) {
						HashMap<String, String> condi = new HashMap<>();
						condi.put("schema", Schema.getSchemas(customerId));
						condi.put("currentMonth", currentMonth);
						condi.put("projectId", String.valueOf(project.getProjectId()));
						condi.put("systemTypeId", String.valueOf(type));
						condi.put("currentYear", currentYear);
						listPowerLandmark = deviceService.getListEpLoadByMonthLandmark(condition);
						float accumulatedElectricity = 0.0f;
						for (LandmarksPlansEnergy x : listPowerLandmark) {
							if (x.getPower() != null && x.getPower() != 0)
								accumulatedElectricity += x.getPower();
							x.setPower(accumulatedElectricity);
						}
						List<LandmarksPlansEnergy> listEnergy = landmarksPlanEnergyService.getEnergyMonth(condi);
						List<LandmarksPlansEnergy> listEnergyPlan = landmarksPlanEnergyService
								.getEnergyMonthPlan(condi);

						listEnergyPower = landmarksPlanEnergyService.getEnergyMonth(condi);
						SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
						Calendar calendar1 = Calendar.getInstance();
						calendar1.set(Calendar.YEAR, Integer.parseInt(currentYear));
						calendar1.set(Calendar.MONTH, Integer.parseInt(currMonth) - 1);
						if (listEnergy.size() > 0) {
							// 31 trường
							for (int i = 0; i < listEnergyPower.size(); i++) {

								LandmarksPlansEnergy data = listEnergyPower.get(i);
								calendar1.set(Calendar.DAY_OF_MONTH, i + 1);
								data.setDateOfMonth(dateFormat.format(calendar1.getTime()));

								// 8 trường
								for (int j = 0; j < listPowerLandmark.size(); j++) {
									LandmarksPlansEnergy landmarkPowerData = listPowerLandmark.get(j);
									if (data.getDateOfMonth().equals(landmarkPowerData.getViewTime())) {
										data.setPower(landmarkPowerData.getPower());
										break;
									}
								}

								if (i < listEnergy.size()) {
									LandmarksPlansEnergy energy = listEnergy.get(i);
									Integer valueEnergy = energy.getValueEnergy();
									data.setTargetEnergy(Double.parseDouble(valueEnergy.toString()));
									Double planEnergy = 0.0;
									if (i < listEnergyPlan.size()) {
										LandmarksPlansEnergy energyPlan = listEnergyPlan.get(i);
										Integer valueEnergyPlan = energyPlan.getValueEnergy();
										if (valueEnergyPlan != null) {
											planEnergy = Double.parseDouble(valueEnergyPlan.toString());
										} else if (valueEnergy != null) {
											planEnergy = valueEnergy * 0.95;
										}
									} else if (valueEnergy != null) {
										planEnergy = valueEnergy * 0.95;
									}

									data.setPlanEnergy(planEnergy);
								}
							}
						}

						List<DataPower> listPowerPush = new ArrayList<>();
						for (LandmarksPlansEnergy x : listEnergyPower) {
							DataPower data = new DataPower();
							data.setDateOfWeek(x.getDateOfWeek());
							data.setPower(x.getPower());
							data.setPlanEnergy(x.getPlanEnergy());
							data.setTargetEnergy(x.getTargetEnergy());
							data.setValueEnergy(x.getValueEnergy());
							data.setViewTime(x.getDateOfMonth());
							listPowerPush.add(data);
						}
						obj.setListDataPower(listPowerPush);
//						obj.setListDataPowerTab2(listEnergyPower);
					}

				}

				if (time == 2) {

					Calendar calendar = Calendar.getInstance();
					Date date = calendar.getTime();

					if (fDate != null) {
						condition.put("year", fDate);

						year = fDate;
					}

					if (type == 1) {
						HashMap<String, String> condi = new HashMap<>();
						String currentMonth = new SimpleDateFormat("MMM", Locale.ENGLISH).format(date.getTime());
						String currDayOfMonth = new SimpleDateFormat("dd", Locale.ENGLISH).format(date.getTime());
						String currentYear = new SimpleDateFormat("YYYY", Locale.ENGLISH).format(date.getTime());
						condi.put("schema", Schema.getSchemas(customerId));
						condi.put("projectId", String.valueOf(project.getProjectId()));
						condi.put("systemTypeId", String.valueOf(type));
						condi.put("currentMonth", currentMonth);
						condi.put("currDayOfMonth", currDayOfMonth);
						if (fDate != null) {
							condi.put("year", fDate);
						} else {
							condi.put("year", currentYear);
						}
						listPowerLandmark = deviceService.getListEpLoadByYear(condition);
						float accumulatedElectricity = 0.0f;
						for (LandmarksPlansEnergy x : listPowerLandmark) {
							if (x.getPower() != null && x.getPower() != 0)
								accumulatedElectricity += x.getPower();
							x.setPower(accumulatedElectricity);
						}
						List<LandmarksPlansEnergy> listEnergy = new ArrayList<>();
						listEnergy = landmarksPlanEnergyService.getEnergyYear(condi);
						List<LandmarksPlansEnergy> listEnergyPlan = new ArrayList<>();
						listEnergyPlan = landmarksPlanEnergyService.getEnergyYearPlan(condi);

						List<LandmarksPlansEnergy> listPowerLandmarkX = new ArrayList<>();

						if (listPowerLandmark.size() > -1) {
							for (int i = 1; i <= 12; i++) {
								LandmarksPlansEnergy x = new LandmarksPlansEnergy();
								// if(i <= listPowerLandmark.size()){
								// x.setPower(listPowerLandmark.get(0).getPower());
								// }else{
								// x.setPower(null);
								// }
								// if(switchViewTime(i, year) == )
								x.setViewTime(switchViewTime(i, year));
								for (LandmarksPlansEnergy y : listPowerLandmark) {
									if (y.getViewTime().contains(switchViewYear(i, year))) {
										x.setPower(y.getPower());
									}
								}

								switch (i) {
								case 1:
									if (listEnergyPlan.get(0) == null) {
										if (listEnergy.get(0) != null) {
											x.setSumEnergy(listEnergy.get(0).getSumT1() * 0.95);
											x.setSumLandmark(listEnergy.get(0).getSumT1());
										} //
									} else {
										x.setSumEnergy(listEnergyPlan.get(0).getSumT1Plan());
										x.setSumLandmark(listEnergy.get(0).getSumT1());
									}

									break;
								case 2:
									if (listEnergyPlan.get(0) == null) {
										if (listEnergy.get(0) != null) {
											x.setSumEnergy(listEnergy.get(0).getSumT2() * 0.95);
											x.setSumLandmark(listEnergy.get(0).getSumT2());
										}

									} else {
										x.setSumEnergy(listEnergyPlan.get(0).getSumT2Plan());
										x.setSumLandmark(listEnergy.get(0).getSumT2());
									}

									break;

								case 3:
									if (listEnergyPlan.get(0) == null) {
										if (listEnergy.get(0) != null) {
											x.setSumEnergy(listEnergy.get(0).getSumT3() * 0.95);
											x.setSumLandmark(listEnergy.get(0).getSumT3());
										}
									} else {
										x.setSumEnergy(listEnergyPlan.get(0).getSumT3Plan());
										x.setSumLandmark(listEnergy.get(0).getSumT3());
									}
									break;

								case 4:
									if (listEnergyPlan.get(0) == null) {
										if (listEnergy.get(0) != null) {
											x.setSumEnergy(listEnergy.get(0).getSumT4() * 0.95);
											x.setSumLandmark(listEnergy.get(0).getSumT4());
										}
									} else {
										x.setSumEnergy(listEnergyPlan.get(0).getSumT4Plan());
										x.setSumLandmark(listEnergy.get(0).getSumT4());
									}
									break;

								case 5:
									if (listEnergyPlan.get(0) == null) {
										if (listEnergy.get(0) != null) {
											x.setSumEnergy(listEnergy.get(0).getSumT5() * 0.95);
											x.setSumLandmark(listEnergy.get(0).getSumT5());
										}
									} else {
										x.setSumEnergy(listEnergyPlan.get(0).getSumT5Plan());
										x.setSumLandmark(listEnergy.get(0).getSumT5());
									}
									break;

								case 6:
									if (listEnergyPlan.get(0) == null) {
										if (listEnergy.get(0) != null) {
											x.setSumEnergy(listEnergy.get(0).getSumT6() * 0.95);
											x.setSumLandmark(listEnergy.get(0).getSumT6());
										}
									} else {
										x.setSumEnergy(listEnergyPlan.get(0).getSumT6Plan());
										x.setSumLandmark(listEnergy.get(0).getSumT6());
									}
									break;

								case 7:
									if (listEnergyPlan.get(0) == null) {
										if (listEnergy.get(0) != null) {
											x.setSumEnergy(listEnergy.get(0).getSumT7() * 0.95);
											x.setSumLandmark(listEnergy.get(0).getSumT7());
										}
									} else {
										x.setSumEnergy(listEnergyPlan.get(0).getSumT7Plan());
										x.setSumLandmark(listEnergy.get(0).getSumT7());
									}
									break;

								case 8:
									if (listEnergyPlan.get(0) == null) {
										if (listEnergy.get(0) != null) {
											x.setSumEnergy(listEnergy.get(0).getSumT8() * 0.95);
											x.setSumLandmark(listEnergy.get(0).getSumT8());
										}
									} else {
										x.setSumEnergy(listEnergyPlan.get(0).getSumT8Plan());
										x.setSumLandmark(listEnergy.get(0).getSumT8());
									}
									break;

								case 9:
									if (listEnergyPlan.get(0) == null) {
										if (listEnergy.get(0) != null) {
											x.setSumEnergy(listEnergy.get(0).getSumT9() * 0.95);
											x.setSumLandmark(listEnergy.get(0).getSumT9());
										}
									} else {
										x.setSumEnergy(listEnergyPlan.get(0).getSumT9Plan());
										x.setSumLandmark(listEnergy.get(0).getSumT9());
									}
									break;

								case 10:
									if (listEnergyPlan.get(0) == null) {
										if (listEnergy.get(0) != null) {
											x.setSumEnergy(listEnergy.get(0).getSumT10() * 0.95);
											x.setSumLandmark(listEnergy.get(0).getSumT10());
										}
									} else {
										x.setSumEnergy(listEnergyPlan.get(0).getSumT10Plan());
										x.setSumLandmark(listEnergy.get(0).getSumT10());
									}
									break;

								case 11:
									if (listEnergyPlan.get(0) == null) {
										if (listEnergy.get(0) != null) {
											x.setSumEnergy(listEnergy.get(0).getSumT11() * 0.95);
											x.setSumLandmark(listEnergy.get(0).getSumT11());
										}
									} else {
										x.setSumEnergy(listEnergyPlan.get(0).getSumT11Plan());
										x.setSumLandmark(listEnergy.get(0).getSumT11());
									}
									break;

								case 12:
									if (listEnergyPlan.get(0) == null) {
										if (listEnergy.get(0) != null) {
											x.setSumEnergy(listEnergy.get(0).getSumT12() * 0.95);
											x.setSumLandmark(listEnergy.get(0).getSumT12());
										}
									} else {
										x.setSumEnergy(listEnergyPlan.get(0).getSumT12Plan());
										x.setSumLandmark(listEnergy.get(0).getSumT12());
									}
									break;

								}

								listPowerLandmarkX.add(x);
							}
						}
						List<DataPower> listPowerPush = new ArrayList<>();
						for (LandmarksPlansEnergy x : listPowerLandmarkX) {
							DataPower data = new DataPower();
							data.setDateOfWeek(x.getDateOfWeek());
							data.setPower(x.getPower());
							data.setPlanEnergy(x.getPlanEnergy());
							data.setTargetEnergy(x.getTargetEnergy());
							data.setValueEnergy(x.getValueEnergy());
							data.setViewTime(x.getViewTime());
							listPowerPush.add(data);
						}
						obj.setListDataPower(listPowerPush);
//						obj.setListDataPowerTab2(listPowerLandmarkX);
					}
				}

				if (time == 3) {
					if (type == 1) {
						listPower = deviceService.getListEpLoadAll(condition);
						obj.setListDataPower(listPower);
					}
					if (type == 2) {
						listPower = deviceService.getListEpSolarAll(condition);
						obj.setListDataPower(listPower);
					}
					if (type == 3) {
						// listPower = deviceService.getListDataPowerBatteruByDay(condition);
					}
					if (type == 4) {
						// listPower = deviceService.getListDataPowerWindByDay(condition);
					}
					if (type == 5) {
						listPower = deviceService.getListEpGridAll(condition);
						obj.setListDataPower(listPower);
					}
					if (type == 6) {
						// listPower = deviceService.getListDataPowerLoadByDay(condition);
					}
				}

				result.add(obj);
			}

			// Project != null
		} else {
			condition.put("projectId", String.valueOf(projectId));
			condition.put("schema", Schema.getSchemas(customerId));
			condition.put("day", day);
			condition.put("month", month);
			condition.put("year", year);
			condition.put("calculateFlag", String.valueOf(1));
			condition.put("deleteFlag", String.valueOf(0));
			condition.put("energy", "energy");

			if (deviceId != "" && deviceId != "0") {
				condition.put(DEVICE_ID, deviceId);
			}

			Project project = projectService.getProject(condition);
			DataPowerResult obj = new DataPowerResult();
			obj.setName(project.getProjectName());

			condition.put("customerId", String.valueOf(customerId));
			if (ids == "") {
				proIds = null;
			} else {
				proIds = ids;
			}

			List<DataPower> listPower = new ArrayList<>();
			// List<LandmarksPlansEnergy> listPower = new ArrayList<>();z
			List<LandmarksPlansEnergy> listPowerLandmark = new ArrayList<>();
			List<LandmarksPlansEnergy> listEnergyPower = new ArrayList<>();
			condition.put("projectId", String.valueOf(project.getProjectId()));
			condition.put("schema", Schema.getSchemas(customerId));
			condition.put("day", day);
			condition.put("month", month);
			condition.put("year", year);
			condition.put("energy", "energy");

			if (time == 0) {
				Calendar calendar = Calendar.getInstance();
				Date date = calendar.getTime();
				String currDayOfWeek = new SimpleDateFormat("dd", Locale.ENGLISH).format(date.getTime());
				String currentMonth = new SimpleDateFormat("MMM", Locale.ENGLISH).format(date.getTime());
				String currentYear = new SimpleDateFormat("YYYY", Locale.ENGLISH).format(date.getTime());
				String dayOrFdate = new SimpleDateFormat("YYYY-MM-dd", Locale.ENGLISH).format(date.getTime());

				if (fDate != null) {
					condition.put("day", fDate);
					try {
						date = formatter.parse(fDate);
						currDayOfWeek = new SimpleDateFormat("dd", Locale.ENGLISH).format(date.getTime());
						currentMonth = new SimpleDateFormat("MMM", Locale.ENGLISH).format(date.getTime());
						currentYear = new SimpleDateFormat("YYYY", Locale.ENGLISH).format(date.getTime());
						dayOrFdate = new SimpleDateFormat("YYYY-MM-dd", Locale.ENGLISH).format(date.getTime());

					} catch (ParseException e) {
						e.printStackTrace();
					}
				}

				List<DataPower> listTimePower = new ArrayList<>();
				for (int hour = 0; hour < 24; hour++) {
					LocalTime viewTime = LocalTime.of(hour, 0, 0);
					DateTimeFormatter formatTime = DateTimeFormatter.ofPattern("HH:mm:ss");
					DataPower data = new DataPower(dayOrFdate + " " + (viewTime).format(formatTime));
					listTimePower.add(data);
				}

				if (type == 1) {
					listPower = deviceService.getListEpLoadByDay(condition);
					HashMap<String, String> condi = new HashMap<>();
					condi.put("schema", Schema.getSchemas(customerId));
					condi.put("currDayOfWeek", currDayOfWeek);
					condi.put("currentMonth", currentMonth);
					condi.put("projectId", String.valueOf(project.getProjectId()));
					condi.put("systemTypeId", String.valueOf(type));
					condi.put("currentYear", currentYear);
					Double planEnergyInput = landmarksPlanEnergyService.getEnergyByDayAndMonth(condi);
					Double targetEnergyInput = landmarksPlanEnergyService.getLandmarksEnergyByDayAndMonth(condi);

					if (planEnergyInput == null) {
						planEnergyInput = 0.0;
					}

					if (targetEnergyInput == null) {
						targetEnergyInput = 0.0;
					}

					if ((planEnergyInput != null && planEnergyInput != 0)
							|| (targetEnergyInput != null && targetEnergyInput != 0)) {
						if (targetEnergyInput != null && targetEnergyInput != 0) {
							Double accumulationPlan = 0.0;
							Double accumulationTarget = 0.0;

							for (DataPower data : listTimePower) {
								if (listPower.size() > 0) {
									for (DataPower dataPower : listPower) {
										if (dataPower.getViewTime().equals(data.getViewTime())) {
											data.setPower(dataPower.getPower());
										}
									}
								}
								data.setTargetEnergy((double) Math.round(accumulationTarget));
								if (targetEnergyInput != null && targetEnergyInput != 0) {
									data.setPlanEnergy((double) Math.round(accumulationPlan));
								}
								if (planEnergyInput == null || planEnergyInput == 0) {
									data.setPlanEnergy(
											accumulationTarget != null ? (double) Math.round(accumulationTarget * 0.95)
													: 0);
								}
								accumulationPlan = accumulationPlan + (planEnergyInput / 23);
								accumulationTarget = accumulationTarget + (targetEnergyInput / 23);
							}
						} else {
							Double accumulationPlan = 0.0;
							Double accumulationTarget = 0.0;

							for (DataPower data : listTimePower) {
								for (DataPower dataPower : listPower) {
									if (dataPower.getViewTime().equals(data.getViewTime())) {
										data.setPower(dataPower.getPower());
									}
								}
								data.setPlanEnergy((double) Math.round(accumulationPlan / 0.95));
								data.setTargetEnergy((double) Math.round(accumulationPlan));

								accumulationPlan = accumulationPlan + (planEnergyInput / 23);
								accumulationTarget = accumulationTarget + (targetEnergyInput / 23);
							}
						}
					} else {
						listTimePower = listPower;
					}

					obj.setListDataPower(listTimePower);
				}

			}

			if (time == 1) {

				Calendar calendar = Calendar.getInstance();
				Date date = calendar.getTime();
				String currentMonth = new SimpleDateFormat("MMM", Locale.ENGLISH).format(date.getTime());
				String currentYear = new SimpleDateFormat("YYYY", Locale.ENGLISH).format(date.getTime());
				if (fDate != null) {
					condition.put("month", fDate);
					try {
						date = formatterMonth.parse(fDate);
						currentMonth = new SimpleDateFormat("MMM", Locale.ENGLISH).format(date.getTime());
						currMonth = DateUtils.toString(date, "MM");
						currentYear = new SimpleDateFormat("YYYY", Locale.ENGLISH).format(date.getTime());
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}

				if (type == 1) {
					HashMap<String, String> condi = new HashMap<>();
					condi.put("schema", Schema.getSchemas(customerId));
					condi.put("currentMonth", currentMonth);
					condi.put("projectId", String.valueOf(project.getProjectId()));
					condi.put("systemTypeId", String.valueOf(type));
					condi.put("currentYear", currentYear);

					List<LandmarksPlansEnergy> listEnergy = landmarksPlanEnergyService.getEnergyMonth(condi);
					List<LandmarksPlansEnergy> listEnergyPlan = landmarksPlanEnergyService.getEnergyMonthPlan(condi);
					listPowerLandmark = deviceService.getListEpLoadByMonthLandmark(condition);
					float accumulatedElectricity = 0.0f;
					for (LandmarksPlansEnergy x : listPowerLandmark) {
						if (x.getPower() != null && x.getPower() != 0)
							accumulatedElectricity += x.getPower();
						x.setPower(accumulatedElectricity);
					}
					listEnergyPower = landmarksPlanEnergyService.getEnergyMonth(condi);
					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
					Calendar calendar1 = Calendar.getInstance();
					calendar1.set(Calendar.YEAR, Integer.parseInt(currentYear));
					calendar1.set(Calendar.MONTH, Integer.parseInt(currMonth) - 1);
					if (listEnergy.size() > 0) {
						// 31 trường
						for (int i = 0; i < listEnergyPower.size(); i++) {

							LandmarksPlansEnergy data = listEnergyPower.get(i);
							calendar1.set(Calendar.DAY_OF_MONTH, i + 1);
							data.setDateOfMonth(dateFormat.format(calendar1.getTime()));

							// 8 trường
							for (int j = 0; j < listPowerLandmark.size(); j++) {
								LandmarksPlansEnergy landmarkPowerData = listPowerLandmark.get(j);
								if (data.getDateOfMonth().equals(landmarkPowerData.getViewTime())) {
									data.setPower(landmarkPowerData.getPower());
									break;
								}
							}

							if (i < listEnergy.size()) {
								LandmarksPlansEnergy energy = listEnergy.get(i);
								Integer valueEnergy = energy.getValueEnergy();
								data.setTargetEnergy(Double.parseDouble(valueEnergy.toString()));
								Double planEnergy = 0.0;
								if (i < listEnergyPlan.size()) {
									LandmarksPlansEnergy energyPlan = listEnergyPlan.get(i);
									Integer valueEnergyPlan = energyPlan.getValueEnergy();
									if (valueEnergyPlan != null) {
										planEnergy = Double.parseDouble(valueEnergyPlan.toString());
									} else if (valueEnergy != null) {
										planEnergy = valueEnergy * 0.95;
									}
								} else if (valueEnergy != null) {
									planEnergy = valueEnergy * 0.95;
								}

								data.setPlanEnergy(planEnergy);
							}
						}
					}
					List<DataPower> listPowerPush = new ArrayList<>();
					for (LandmarksPlansEnergy x : listEnergyPower) {
						DataPower data = new DataPower();
						data.setDateOfWeek(x.getDateOfWeek());
						data.setPower(x.getPower());
						data.setPlanEnergy(x.getPlanEnergy());
						data.setTargetEnergy(x.getTargetEnergy());
						data.setValueEnergy(x.getValueEnergy());
						data.setViewTime(x.getDateOfMonth());
						listPowerPush.add(data);
					}
					obj.setListDataPower(listPowerPush);
//					obj.setListDataPowerTab2(listEnergyPower);
				}

			}

			if (time == 2) {

				Calendar calendar = Calendar.getInstance();
				Date date = calendar.getTime();

				if (fDate != null) {
					condition.put("year", fDate);

					year = fDate;
				}

				if (type == 1) {
					HashMap<String, String> condi = new HashMap<>();
					String currentMonth = new SimpleDateFormat("MMM", Locale.ENGLISH).format(date.getTime());
					String currDayOfMonth = new SimpleDateFormat("dd", Locale.ENGLISH).format(date.getTime());
					String currentYear = new SimpleDateFormat("YYYY", Locale.ENGLISH).format(date.getTime());
					condi.put("schema", Schema.getSchemas(customerId));
					condi.put("projectId", String.valueOf(project.getProjectId()));
					condi.put("systemTypeId", String.valueOf(type));
					condi.put("currentMonth", currentMonth);
					condi.put("currDayOfMonth", currDayOfMonth);
					if (fDate != null) {
						condi.put("year", fDate);
					} else {
						condi.put("year", currentYear);
					}
					List<LandmarksPlansEnergy> listEnergy = new ArrayList<>();
					listEnergy = landmarksPlanEnergyService.getEnergyYear(condi);
					List<LandmarksPlansEnergy> listEnergyPlan = new ArrayList<>();
					listEnergyPlan = landmarksPlanEnergyService.getEnergyYearPlan(condi);
					listPowerLandmark = deviceService.getListEpLoadByYear(condition);
					float accumulatedElectricity = 0.0f;
					for (LandmarksPlansEnergy x : listPowerLandmark) {
						if (x.getPower() != null && x.getPower() != 0)
							accumulatedElectricity += x.getPower();
						x.setPower(accumulatedElectricity);
					}
					List<LandmarksPlansEnergy> listPowerLandmarkX = new ArrayList<>();

					if (listPowerLandmark.size() > 0) {
						for (int i = 1; i <= 12; i++) {
							LandmarksPlansEnergy x = new LandmarksPlansEnergy();
							x.setViewTime(switchViewTime(i, year));
							for (LandmarksPlansEnergy y : listPowerLandmark) {
								if (y.getViewTime().contains(switchViewYear(i, year))) {
									x.setPower(y.getPower());
								}
							}
//							if (listEnergyPlan.get(0) != null) {
							switch (i) {
							case 1:
								if (listEnergyPlan.get(0) != null && listEnergy.get(0) != null) {
									if (listEnergyPlan.get(0).getSumT1Plan() == 0) {
										x.setSumEnergy(listEnergy.get(0).getSumT1() * 0.95);
									} else {
										x.setSumEnergy(listEnergyPlan.get(0).getSumT1Plan());
									}
									x.setSumLandmark(listEnergy.get(0).getSumT1());
								} else if (listEnergyPlan.get(0) != null && listEnergy.get(0) == null) {
									x.setSumEnergy(listEnergyPlan.get(0).getSumT1Plan());
									x.setSumLandmark(listEnergyPlan.get(0).getSumT1Plan() / 0.95);
								} else if (listEnergyPlan.get(0) == null && listEnergy.get(0) != null) {
									x.setSumEnergy(listEnergy.get(0).getSumT1() * 0.95);
									x.setSumLandmark(listEnergy.get(0).getSumT1());
								}

								break;
							case 2:
								if (listEnergyPlan.get(0) != null && listEnergy.get(0) != null) {
									if (listEnergyPlan.get(0).getSumT2Plan()
											.equals(listEnergyPlan.get(0).getSumT1Plan())) {
										x.setSumEnergy(listEnergy.get(0).getSumT2() * 0.95);
									} else {
										x.setSumEnergy(listEnergyPlan.get(0).getSumT2Plan());
									}
									x.setSumLandmark(listEnergy.get(0).getSumT2());
								} else if (listEnergyPlan.get(0) != null && listEnergy.get(0) == null) {
									x.setSumEnergy(listEnergyPlan.get(0).getSumT2Plan());
									x.setSumLandmark(listEnergyPlan.get(0).getSumT2Plan() / 0.95);
								} else if (listEnergyPlan.get(0) == null && listEnergy.get(0) != null) {
									x.setSumEnergy(listEnergy.get(0).getSumT2() * 0.95);
									x.setSumLandmark(listEnergy.get(0).getSumT2());
								}
								break;

							case 3:
								if (listEnergyPlan.get(0) != null && listEnergy.get(0) != null) {
									if (listEnergyPlan.get(0).getSumT3Plan()
											.equals(listEnergyPlan.get(0).getSumT2Plan())) {
										x.setSumEnergy(listEnergy.get(0).getSumT3() * 0.95);
									} else {
										x.setSumEnergy(listEnergyPlan.get(0).getSumT3Plan());
									}
									x.setSumLandmark(listEnergy.get(0).getSumT3());
								} else if (listEnergyPlan.get(0) != null && listEnergy.get(0) == null) {
									x.setSumEnergy(listEnergyPlan.get(0).getSumT3Plan());
									x.setSumLandmark(listEnergyPlan.get(0).getSumT3Plan() / 0.95);
								} else if (listEnergyPlan.get(0) == null && listEnergy.get(0) != null) {
									x.setSumEnergy(listEnergy.get(0).getSumT3() * 0.95);
									x.setSumLandmark(listEnergy.get(0).getSumT3());
								}
								break;

							case 4:
								if (listEnergyPlan.get(0) != null && listEnergy.get(0) != null) {
									if (listEnergyPlan.get(0).getSumT4Plan()
											.equals(listEnergyPlan.get(0).getSumT3Plan())) {
										x.setSumEnergy(listEnergy.get(0).getSumT4() * 0.95);
									} else {
										x.setSumEnergy(listEnergyPlan.get(0).getSumT4Plan());
									}
									x.setSumLandmark(listEnergy.get(0).getSumT4());
								} else if (listEnergyPlan.get(0) != null && listEnergy.get(0) == null) {
									x.setSumEnergy(listEnergyPlan.get(0).getSumT4Plan());
									x.setSumLandmark(listEnergyPlan.get(0).getSumT4Plan() / 0.95);
								} else if (listEnergyPlan.get(0) == null && listEnergy.get(0) != null) {
									x.setSumEnergy(listEnergy.get(0).getSumT4() * 0.95);
									x.setSumLandmark(listEnergy.get(0).getSumT4());
								}
								break;

							case 5:
								if (listEnergyPlan.get(0) != null && listEnergy.get(0) != null) {
									if (listEnergyPlan.get(0).getSumT5Plan()
											.equals(listEnergyPlan.get(0).getSumT4Plan())) {
										x.setSumEnergy(listEnergy.get(0).getSumT5() * 0.95);
									} else {
										x.setSumEnergy(listEnergyPlan.get(0).getSumT5Plan());
									}
									x.setSumLandmark(listEnergy.get(0).getSumT5());
								} else if (listEnergyPlan.get(0) != null && listEnergy.get(0) == null) {
									x.setSumEnergy(listEnergyPlan.get(0).getSumT5Plan());
									x.setSumLandmark(listEnergyPlan.get(0).getSumT5Plan() / 0.95);
								} else if (listEnergyPlan.get(0) == null && listEnergy.get(0) != null) {
									x.setSumEnergy(listEnergy.get(0).getSumT5() * 0.95);
									x.setSumLandmark(listEnergy.get(0).getSumT5());
								}
								break;
//								// Các case từ 6 đến 11
							case 6:
								if (listEnergyPlan.get(0) != null && listEnergy.get(0) != null) {
									if (listEnergyPlan.get(0).getSumT6Plan()
											.equals(listEnergyPlan.get(0).getSumT5Plan())) {
										x.setSumEnergy(listEnergy.get(0).getSumT6() * 0.95);
									} else {
										x.setSumEnergy(listEnergyPlan.get(0).getSumT6Plan());
									}
									x.setSumLandmark(listEnergy.get(0).getSumT6());
								} else if (listEnergyPlan.get(0) != null && listEnergy.get(0) == null) {
									x.setSumEnergy(listEnergyPlan.get(0).getSumT6Plan());
									x.setSumLandmark(listEnergyPlan.get(0).getSumT6Plan() / 0.95);
								} else if (listEnergyPlan.get(0) == null && listEnergy.get(0) != null) {
									x.setSumEnergy(listEnergy.get(0).getSumT6() * 0.95);
									x.setSumLandmark(listEnergy.get(0).getSumT6());
								}
								break;

							case 7:
								if (listEnergyPlan.get(0) != null && listEnergy.get(0) != null) {
									if (listEnergyPlan.get(0).getSumT7Plan()
											.equals(listEnergyPlan.get(0).getSumT6Plan())) {
										x.setSumEnergy(listEnergy.get(0).getSumT7() * 0.95);
									} else {
										x.setSumEnergy(listEnergyPlan.get(0).getSumT7Plan());
									}
									x.setSumLandmark(listEnergy.get(0).getSumT7());
								} else if (listEnergyPlan.get(0) != null && listEnergy.get(0) == null) {
									x.setSumEnergy(listEnergyPlan.get(0).getSumT7Plan());
									x.setSumLandmark(listEnergyPlan.get(0).getSumT7Plan() / 0.95);
								} else if (listEnergyPlan.get(0) == null && listEnergy.get(0) != null) {
									x.setSumEnergy(listEnergy.get(0).getSumT7() * 0.95);
									x.setSumLandmark(listEnergy.get(0).getSumT7());
								}
								break;

							case 8:
								if (listEnergyPlan.get(0) != null && listEnergy.get(0) != null) {
									if (listEnergyPlan.get(0).getSumT8Plan()
											.equals(listEnergyPlan.get(0).getSumT7Plan())) {
										x.setSumEnergy(listEnergy.get(0).getSumT8() * 0.95);
									} else {
										x.setSumEnergy(listEnergyPlan.get(0).getSumT8Plan());
									}
									x.setSumLandmark(listEnergy.get(0).getSumT8());
								} else if (listEnergyPlan.get(0) != null && listEnergy.get(0) == null) {
									x.setSumEnergy(listEnergyPlan.get(0).getSumT8Plan());
									x.setSumLandmark(listEnergyPlan.get(0).getSumT8Plan() / 0.95);
								} else if (listEnergyPlan.get(0) == null && listEnergy.get(0) != null) {
									x.setSumEnergy(listEnergy.get(0).getSumT8() * 0.95);
									x.setSumLandmark(listEnergy.get(0).getSumT8());
								}
								break;

							case 9:
								if (listEnergyPlan.get(0) != null && listEnergy.get(0) != null) {
									if (listEnergyPlan.get(0).getSumT9Plan()
											.equals(listEnergyPlan.get(0).getSumT8Plan())) {
										x.setSumEnergy(listEnergy.get(0).getSumT9() * 0.95);
									} else {
										x.setSumEnergy(listEnergyPlan.get(0).getSumT9Plan());
									}
									x.setSumLandmark(listEnergy.get(0).getSumT9());
								} else if (listEnergyPlan.get(0) != null && listEnergy.get(0) == null) {
									x.setSumEnergy(listEnergyPlan.get(0).getSumT9Plan());
									x.setSumLandmark(listEnergyPlan.get(0).getSumT9Plan() / 0.95);
								} else if (listEnergyPlan.get(0) == null && listEnergy.get(0) != null) {
									x.setSumEnergy(listEnergy.get(0).getSumT9() * 0.95);
									x.setSumLandmark(listEnergy.get(0).getSumT9());
								}
								break;

							case 10:
								if (listEnergyPlan.get(0) != null && listEnergy.get(0) != null) {
									if (listEnergyPlan.get(0).getSumT10Plan()
											.equals(listEnergyPlan.get(0).getSumT9Plan())) {
										x.setSumEnergy(listEnergy.get(0).getSumT10() * 0.95);
									} else {
										x.setSumEnergy(listEnergyPlan.get(0).getSumT10Plan());
									}
									x.setSumLandmark(listEnergy.get(0).getSumT10());
								} else if (listEnergyPlan.get(0) != null && listEnergy.get(0) == null) {
									x.setSumEnergy(listEnergyPlan.get(0).getSumT10Plan());
									x.setSumLandmark(listEnergyPlan.get(0).getSumT10Plan() / 0.95);
								} else if (listEnergyPlan.get(0) == null && listEnergy.get(0) != null) {
									x.setSumEnergy(listEnergy.get(0).getSumT10() * 0.95);
									x.setSumLandmark(listEnergy.get(0).getSumT10());
								}
								break;

							case 11:
								if (listEnergyPlan.get(0) != null && listEnergy.get(0) != null) {
									if (listEnergyPlan.get(0).getSumT11Plan()
											.equals(listEnergyPlan.get(0).getSumT10Plan())) {
										x.setSumEnergy(listEnergy.get(0).getSumT11() * 0.95);
									} else {
										x.setSumEnergy(listEnergyPlan.get(0).getSumT11Plan());
									}
									x.setSumLandmark(listEnergy.get(0).getSumT11());
								} else if (listEnergyPlan.get(0) != null && listEnergy.get(0) == null) {
									x.setSumEnergy(listEnergyPlan.get(0).getSumT11Plan());
									x.setSumLandmark(listEnergyPlan.get(0).getSumT11Plan() / 0.95);
								} else if (listEnergyPlan.get(0) == null && listEnergy.get(0) != null) {
									x.setSumEnergy(listEnergy.get(0).getSumT11() * 0.95);
									x.setSumLandmark(listEnergy.get(0).getSumT11());
								}
								break;

							case 12:
								if (listEnergyPlan.get(0) != null && listEnergy.get(0) != null) {
									if (listEnergyPlan.get(0).getSumT12Plan()
											.equals(listEnergyPlan.get(0).getSumT11Plan())) {
										x.setSumEnergy(listEnergy.get(0).getSumT12() * 0.95);
									} else {
										x.setSumEnergy(listEnergyPlan.get(0).getSumT12Plan());
									}
									x.setSumLandmark(listEnergy.get(0).getSumT12());
								} else if (listEnergyPlan.get(0) != null && listEnergy.get(0) == null) {
									x.setSumEnergy(listEnergyPlan.get(0).getSumT12Plan());
									x.setSumLandmark(listEnergyPlan.get(0).getSumT12Plan() / 0.95);
								} else if (listEnergyPlan.get(0) == null && listEnergy.get(0) != null) {
									x.setSumEnergy(listEnergy.get(0).getSumT12() * 0.95);
									x.setSumLandmark(listEnergy.get(0).getSumT12());
								}
								break;
							}
//							}

							listPowerLandmarkX.add(x);
						}
					}
					List<DataPower> listPowerPush = new ArrayList<>();
					for (LandmarksPlansEnergy x : listPowerLandmarkX) {
						DataPower data = new DataPower();
						data.setDateOfWeek(x.getDateOfWeek());
						data.setPower(x.getPower());
						data.setPlanEnergy(x.getPlanEnergy());
						data.setTargetEnergy(x.getTargetEnergy());
						data.setValueEnergy(x.getValueEnergy());
						data.setViewTime(x.getViewTime());
						listPowerPush.add(data);
					}
					obj.setListDataPower(listPowerPush);
//					obj.setListDataPowerTab2(listPowerLandmarkX);
				}
			}

			if (time == 3) {
				if (type == 1) {
					listPower = deviceService.getListEpLoadAll(condition);
					obj.setListDataPower(listPower);
				}
				if (type == 2) {
					listPower = deviceService.getListEpSolarAll(condition);
					obj.setListDataPower(listPower);
				}
				if (type == 3) {
					// listPower = deviceService.getListDataPowerBatteruByDay(condition);
				}
				if (type == 4) {
					// listPower = deviceService.getListDataPowerWindByDay(condition);
				}
				if (type == 5) {
					listPower = deviceService.getListEpGridAll(condition);
					obj.setListDataPower(listPower);
				}
				if (type == 6) {
					// listPower = deviceService.getListDataPowerLoadByDay(condition);
				}
			}

			if (time == 5) {
				Calendar calendar = Calendar.getInstance();
				Date date = calendar.getTime();
				String currentMonth = new SimpleDateFormat("MMM", Locale.ENGLISH).format(date.getTime());
				String currentYear = new SimpleDateFormat("YYYY", Locale.ENGLISH).format(date.getTime());

				if (type == 1) {
					HashMap<String, String> condi = new HashMap<>();
					condi.put("schema", Schema.getSchemas(customerId));
					condi.put("currentMonth", currentMonth);
					condi.put("projectId", String.valueOf(project.getProjectId()));
					condi.put("systemTypeId", String.valueOf(type));
					condi.put("currentYear", currentYear);

					List<LandmarksPlansEnergy> listEnergy = landmarksPlanEnergyService.getEnergyMonth(condi);
					List<LandmarksPlansEnergy> listEnergyPlan = landmarksPlanEnergyService.getEnergyMonthPlan(condi);
					listPowerLandmark = deviceService.getListEpLoadByMonthLandmark(condition);
					float accumulatedElectricity = 0.0f;
					for (LandmarksPlansEnergy x : listPowerLandmark) {
						if (x.getPower() != null && x.getPower() != 0)
							accumulatedElectricity += x.getPower();
						x.setPower(accumulatedElectricity);
					}
					listEnergyPower = landmarksPlanEnergyService.getEnergyMonth(condi);
					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
					Calendar calendar1 = Calendar.getInstance();
					calendar1.set(Calendar.YEAR, Integer.parseInt(currentYear));
					calendar1.set(Calendar.MONTH, Integer.parseInt(currMonth) - 1);
					if (listEnergy.size() > 0) {
						// 31 trường
						for (int i = 0; i < listEnergyPower.size(); i++) {

							LandmarksPlansEnergy data = listEnergyPower.get(i);
							calendar1.set(Calendar.DAY_OF_MONTH, i + 1);
							data.setDateOfMonth(dateFormat.format(calendar1.getTime()));

							// 8 trường
							for (int j = 0; j < listPowerLandmark.size(); j++) {
								LandmarksPlansEnergy landmarkPowerData = listPowerLandmark.get(j);
								if (data.getDateOfMonth().equals(landmarkPowerData.getViewTime())) {
									data.setPower(landmarkPowerData.getPower());
									break;
								}
							}

							if (i < listEnergy.size()) {
								LandmarksPlansEnergy energy = listEnergy.get(i);
								Integer valueEnergy = energy.getValueEnergy();
								data.setTargetEnergy(Double.parseDouble(valueEnergy.toString()));
								Double planEnergy = 0.0;
								if (i < listEnergyPlan.size()) {
									LandmarksPlansEnergy energyPlan = listEnergyPlan.get(i);
									Integer valueEnergyPlan = energyPlan.getValueEnergy();
									if (valueEnergyPlan != null) {
										planEnergy = Double.parseDouble(valueEnergyPlan.toString());
									} else if (valueEnergy != null) {
										planEnergy = valueEnergy * 0.95;
									}
								} else if (valueEnergy != null) {
									planEnergy = valueEnergy * 0.95;
								}

								data.setPlanEnergy(planEnergy);
							}
						}
					}
					List<LandmarksPlansEnergy> dataWeek = new ArrayList<>();
					List<LandmarksPlansEnergy> listPowerLandmarkX = new ArrayList<>();
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

					try {
						Date fromDate = sdf.parse(fDate);
						Date toDate = sdf.parse(tDate);
						for (LandmarksPlansEnergy x : listEnergyPower) {
							Date dateOfMonth = sdf.parse(x.getDateOfMonth());
							if ((dateOfMonth.equals(fromDate) || dateOfMonth.after(fromDate))
									&& (dateOfMonth.equals(toDate) || dateOfMonth.before(toDate))) {
								dataWeek.add(x);
							}
						}
					} catch (ParseException e) {
						e.printStackTrace();
					}

					DateTimeFormatter formatterX = DateTimeFormatter.ofPattern(ES.DATE_FORMAT_YMD);
					for (LandmarksPlansEnergy item : dataWeek) {
						String dayWeek = "";
						LocalDate dateX = LocalDate.parse(item.getDateOfMonth(), formatterX);
						DayOfWeek dayOfWeek = dateX.getDayOfWeek();
						int dayOfWeekValue = dayOfWeek.getValue() + 1;

						if (dayOfWeekValue != 8) {
							dayWeek = "Thứ " + dayOfWeekValue;
						} else {
							dayWeek = "Chủ nhật";
						}
						item.setDay(item.getDateOfMonth());
						item.setDateOfMonth(dayWeek);
					}
					List<DataPower> listPowerPush = new ArrayList<>();
					for (LandmarksPlansEnergy x : dataWeek) {
						DataPower data = new DataPower();
						data.setDateOfWeek(x.getDateOfWeek());
						data.setPower(x.getPower());
						data.setPlanEnergy(x.getPlanEnergy());
						data.setTargetEnergy(x.getTargetEnergy());
						data.setValueEnergy(x.getValueEnergy());
						data.setViewTime(x.getDateOfMonth());
						listPowerPush.add(data);
					}
					obj.setListDataPower(listPowerPush);
//					obj.setListDataPowerTab2(dataWeek);
				}
			}
			result.add(obj);

		}
		log.info("getChartEnergyPlan END");
		return new ResponseEntity<Object>(result, HttpStatus.OK);

	}

	@GetMapping("/solar")
	public ResponseEntity<?> getChartSolar(@RequestParam("fromDate") final String fromDate,
			@RequestParam("toDate") final String toDate, @RequestParam("customerId") final Integer customerId,
			@RequestParam("projectId") final String projectId, @RequestParam("typeTime") final Integer typeTime,
			@RequestParam("deviceId") final String deviceId) {
		log.info("getChartSolar START");
		String schema = Schema.getSchemas(customerId);
		Map<String, Object> condition = new HashMap<>();
		condition.put(SCHEMA, schema);
		if (Integer.parseInt(projectId) != 0) {
			condition.put(PROJECT_ID, projectId);
		}
		if (deviceId != "" && deviceId != "0") {
			condition.put(DEVICE_ID, deviceId);
		}
		condition.put("fromDate", fromDate);
		condition.put("toDate", toDate);
		if (typeTime == 1) {
			// typeTime = 1 là so sánh theo hôm nay - hôm qua, lấy giữ liệu giờ trong ngày
			condition.put("viewType", 5);
		} else if (typeTime == 2) {
			// typeTime = 2 là so sánh theo tháng này - tháng trước, lấy giữ liệu ngày trong
			// tháng
			condition.put("viewType", 3);
		} else if (typeTime == 3) {
			// typeTime = 3 là so sánh theo năm nay - năm sau, lấy giữ liệu tháng trong năm
			condition.put("viewType", 2);
			condition.put("toDate", toDate);
		} else if (typeTime == 4) {
			// typeTime = 4 là so sánh theo năm
			condition.put("viewType", 1);
		} else if (typeTime == 5) {
			// Tùy chọn
			condition.put("typeTime", 5);
			condition.put("viewType", 5);
			condition.put("toDate", toDate);
		}

		List<DataInverter1> dataComp = chartService.getChartSolarByCustomerId(condition);
		List<DataCombiner1> dataCombinerComp = chartService.getChartCombinerByCustomerId(condition);
		if (typeTime == 5) {
			condition.put("fromDate", fromDate);
		} else {
//			condition.put("fromDate", toDate);
		}
		List<DataInverter1> dataNow = chartService.getChartSolarByCustomerId(condition);
		List<DataCombiner1> dataCombinerNow = chartService.getChartCombinerByCustomerId(condition);

		int sizeComp = dataComp.size();
		int sizeNow = dataNow.size();
		int sizeCombinerComp = dataCombinerComp.size();
		int sizeCombinerNow = dataCombinerNow.size();
		for (int i = 0; i < sizeComp; i++) {
			for (int j = 0; j < sizeCombinerComp; j++) {
				if (dataComp.get(i).getViewTime().equals(dataCombinerComp.get(j).getViewTime())) {
					dataComp.get(i).setEp(dataComp.get(i).getEp() + dataCombinerComp.get(j).getEpCombiner());
				}
			}
		}

		for (int i = 0; i < sizeNow; i++) {
			for (int j = 0; j < sizeCombinerNow; j++) {
				if (dataNow.get(i).getViewTime().equals(dataCombinerNow.get(j).getViewTime())) {
					dataNow.get(i).setEp(dataNow.get(i).getEp() + dataCombinerNow.get(j).getEpCombiner());
				}
			}
		}
		JSONArray resultArray = new JSONArray();
		float cumulativeTotal = 0;
		for (DataInverter1 data : dataComp) {
			String name = data.getProjectName();
			Float value = data.getEp();
			String time = data.getTime();
			String nameDevice = data.getDeviceName();
			// Tạo một đối tượng JSON đại diện cho mỗi bản ghi
			JSONObject recordObject = new JSONObject();
			recordObject.put(name, value);

			// Kiểm tra xem trường "time" đã tồn tại trong danh sách JSON chưa
			boolean timeExists = false;

			for (Object obj : resultArray) {
				if (obj instanceof JSONObject) {
					JSONObject timeObject = (JSONObject) obj;
					if (timeObject.containsKey("time") && timeObject.get("time").equals(time)) {
						Float total = timeObject.containsKey("total") ? (Float) timeObject.get("total") : 0;
						total += value;
						if (Integer.parseInt(projectId) != 0) {
							timeObject.put(nameDevice, value);
						} else {
							timeObject.put(name, value);
						}
						cumulativeTotal += value;
						timeObject.put("total", cumulativeTotal);
						timeExists = true;
						break;
					}
				}
			}

			// Nếu trường "time" chưa tồn tại, tạo một đối tượng JSON mới và thêm vào danh
			// sách
			if (!timeExists) {
				JSONObject timeObject = new JSONObject();
				timeObject.put("time", time);
				if (Integer.parseInt(projectId) != 0) {
					timeObject.put(nameDevice, value);
				} else {
					timeObject.put(name, value);
				}
				cumulativeTotal += value;
				timeObject.put("total", cumulativeTotal);
				resultArray.add(timeObject);
			}
		}

		Map<String, Object> mapData = new HashMap<>();
		mapData.put("dataNow", dataNow);
		mapData.put("dataComp", dataComp);
		mapData.put("dataLoadAllSite", resultArray);
		log.info("getChartSolar END");

		return new ResponseEntity<>(mapData, HttpStatus.OK);
	}

	@GetMapping("/solar-power")
	public ResponseEntity<?> getChartSolarPower(@RequestParam("fromDate") final String fromDate,
			@RequestParam("toDate") final String toDate, @RequestParam("customerId") final Integer customerId,
			@RequestParam("projectId") final String projectId, @RequestParam("typeTime") final Integer typeTime,
			@RequestParam("deviceId") final String deviceId) {
		log.info("getChartSolarPower START");
		String schema = Schema.getSchemas(customerId);
		Map<String, Object> condition = new HashMap<>();
		condition.put(SCHEMA, schema);
		if (Integer.parseInt(projectId) != 0) {
			condition.put(PROJECT_ID, projectId);
		}
		if (deviceId != "" && deviceId != "0") {
			condition.put(DEVICE_ID, deviceId);
		}
		List<Chart> dataComp = new ArrayList<>();
		List<Chart> dataNow = new ArrayList<>();
		condition.put("fromDate", fromDate);
		condition.put("toDate", toDate);

		if (typeTime == 1) {
			// typeTime = 1 là so sánh theo hôm nay - hôm qua, lấy giữ liệu giờ trong ngày
			condition.put("viewType", 5);
			condition.put("typeTime", 5);
		} else if (typeTime == 2) {
			// typeTime = 2 là so sánh theo tháng này - tháng trước, lấy giữ liệu ngày trong
			// tháng
			condition.put("typeTime", 5);
			condition.put("viewType", 5);
			condition.put("toDate", toDate);
		} else if (typeTime == 3) {
			// typeTime = 3 là so sánh theo năm nay - năm sau, lấy giữ liệu tháng trong năm
			condition.put("viewType", 2);
		} else if (typeTime == 4) {
			// typeTime = 4 là so sánh theo năm
			condition.put("viewType", 1);
		} else if (typeTime == 5) {
			// Tùy chọn
			condition.put("typeTime", 5);
			condition.put("viewType", 5);
			condition.put("toDate", toDate);
		}
		if (typeTime == 5 || typeTime == 3 || typeTime == 2) {
			condition.put("fromDate", fromDate);
		} else {
//			condition.put("fromDate", toDate);
		}
		dataComp = chartService.getChartInverterPower(condition);

		dataNow = chartService.getChartInverterPower(condition);

		Float cumulativeTotal = 0.00f;
		JSONArray resultArray = new JSONArray();
		for (Chart data : dataComp) {
			String name = data.getProjectName();
			Float value = data.getPTotal();
			String time = data.getTime();
			String nameDevice = data.getDeviceName();
			// Tạo một đối tượng JSON đại diện cho mỗi bản ghi
			JSONObject recordObject = new JSONObject();
			recordObject.put(name, value);

			// Kiểm tra xem trường "time" đã tồn tại trong danh sách JSON chưa
			boolean timeExists = false;
			for (Object obj : resultArray) {
				if (obj instanceof JSONObject) {
					JSONObject timeObject = (JSONObject) obj;
					if (timeObject.containsKey("time") && timeObject.get("time").equals(time)) {
						Float total = timeObject.containsKey("total") ? (float) timeObject.get("total") : 0;
						if (Integer.parseInt(projectId) != 0) {
							timeObject.put(nameDevice, value);
						} else {
							timeObject.put(name, value);
						}
						cumulativeTotal += value;
						timeObject.put("total", cumulativeTotal);
						timeExists = true;
						break;
					}
				}
			}
			// Nếu trường "time" chưa tồn tại, tạo một đối tượng JSON mới và thêm vào danh
			// sách
			if (!timeExists) {
				JSONObject timeObject = new JSONObject();
				timeObject.put("time", time);
				if (Integer.parseInt(projectId) != 0) {
					timeObject.put(nameDevice, value);
				} else {
					timeObject.put(name, value);
				}
				cumulativeTotal += value;
				timeObject.put("total", cumulativeTotal);
				resultArray.add(timeObject);
			}
		}

		Map<String, Object> mapData = new HashMap<>();
		mapData.put("dataNow", dataNow);
		mapData.put("dataComp", dataComp);
		mapData.put("dataLoadAllSite", resultArray);
		log.info("getChartSolarPower END");

		return new ResponseEntity<>(mapData, HttpStatus.OK);
	}

	@GetMapping("/solar-cost")
	public ResponseEntity<?> getChartSolarCost(@RequestParam("fromDate") final String fromDate,
			@RequestParam("toDate") final String toDate, @RequestParam("customerId") final Integer customerId,
			@RequestParam("projectId") final String projectId, @RequestParam("typeTime") final Integer typeTime,
			@RequestParam("deviceId") final String deviceId) {
		log.info("getChartCostSolar START");
		String schema = Schema.getSchemas(customerId);
		Map<String, Object> condition = new HashMap<>();
		List<Chart> dataComp = new ArrayList<>();
		List<Chart> chart = new ArrayList<>();
		condition.put(SCHEMA, schema);
		if (Integer.parseInt(projectId) != 0) {
			condition.put(PROJECT_ID, projectId);
		}
		if (deviceId != "" && deviceId != "0") {
			condition.put(DEVICE_ID, deviceId);
		}
		condition.put("fromDate", fromDate);
		condition.put("toDate", toDate);

		// typeTime(1-hôm nay, 2-hôm qua), lấy giữ liệu giờ, phút trong ngày
		if (typeTime == 1) {
			condition.put("viewType", 5);
			dataComp = chartService.getChartSolarSumCostByDay(condition);
			for (Chart item : dataComp) {
				setCost(item, typeTime, 2);
			}

			chart = sumByHour(dataComp, 2);
			for (Chart item : chart) {
				if (item.getCostLowOut() == 0) {
					item.setCostLowOut(null);
				}
				if (item.getCostMediumOut() == 0) {
					item.setCostMediumOut(null);
				}
				if (item.getCostHighOut() == 0) {
					item.setCostHighOut(null);
				}
			}

		} else if (typeTime == 5) {
			condition.put("viewTime", 5);
			condition.put("typeTime", 5);
			dataComp = chartService.getChartSolarCostByCustomerId(condition);
			for (Chart item : dataComp) {
				setCost(item, typeTime, 2);
			}
			chart = sumByHour(dataComp, 2);
			for (Chart item : chart) {
				if (item.getCostLowOut() == 0) {
					item.setCostLowOut(null);
				}
				if (item.getCostMediumOut() == 0) {
					item.setCostMediumOut(null);
				}
				if (item.getCostHighOut() == 0) {
					item.setCostHighOut(null);
				}
			}
		}
		// 3-tháng này || 4- tháng trước || 5-3 tháng trước || 6 - 6 tháng trước lấy dữ
		// liệu sum (ep) các thiết bị trong
		// cùng 1 project
		else if (typeTime == 2) {
			condition.put("viewType", 5);
			dataComp = chartService.getChartSolarSumCostByDay(condition);
			// START--Hoàn thành data giá tiền điện trong 1 ngày
			for (Chart item : dataComp) {
				// ngày chủ nhật không có giờ cao điểm
				getDayOfWeek(item, 2);

				// lấy setting từng khung giờ của dự án
				condition.put("projectId", item.getProjectId());
				condition.put("settingMstId", Constants.settingCostEnergy.SOLAR_LOW_COST_OUT);
				Setting setting1 = settingService.getSettingByDevice(condition);
				float low = Float.parseFloat(setting1.getSettingValue());

				condition.put("settingMstId", Constants.settingCostEnergy.SOLAR_MEDIUM_COST_OUT);
				Setting setting2 = settingService.getSettingByDevice(condition);
				float medium = Float.parseFloat(setting2.getSettingValue());

				condition.put("settingMstId", Constants.settingCostEnergy.SOLAR_HIGH_COST_OUT);
				Setting setting3 = settingService.getSettingByDevice(condition);
				float high = Float.parseFloat(setting3.getSettingValue());

				item.setCostHighOut(item.getCostHighOut() * high);
				item.setCostMediumOut(item.getCostMediumOut() * medium);
				item.setCostLowOut(item.getCostLowOut() * low);
				item.setViewTime(item.getViewTime().substring(0, 10));
//				item.setViewTime(item.getViewTime());
			}
		}
		// END--Hoàn thành data giá tiền điện trong 1 ngày

		// Lấy ngày kiểu yyyy-mm-dd trong fromdate, todate

		// 3 năm
		else if (typeTime == 3) {
			condition.put("viewType", 5);
			dataComp = chartService.getChartSolarSumCostByYear(condition);
			// START--Hoàn thành data giá tiền điện trong 1 ngày
			for (Chart item : dataComp) {
				// ngày chủ nhật không có giờ cao điểm
				getDayOfWeek(item, 2);

				// lấy setting từng khung giờ của dự án
				condition.put("projectId", item.getProjectId());
				condition.put("settingMstId", Constants.settingCostEnergy.SOLAR_LOW_COST_OUT);
				Setting setting1 = settingService.getSettingByDevice(condition);
				float low = Float.parseFloat(setting1.getSettingValue());

				condition.put("settingMstId", Constants.settingCostEnergy.SOLAR_MEDIUM_COST_OUT);
				Setting setting2 = settingService.getSettingByDevice(condition);
				float medium = Float.parseFloat(setting2.getSettingValue());

				condition.put("settingMstId", Constants.settingCostEnergy.SOLAR_HIGH_COST_OUT);
				Setting setting3 = settingService.getSettingByDevice(condition);
				float high = Float.parseFloat(setting3.getSettingValue());

				item.setCostHighOut(item.getCostHighOut() * high);
				item.setCostMediumOut(item.getCostMediumOut() * medium);
				item.setCostLowOut(item.getCostLowOut() * low);
				item.setViewTime(item.getViewTime().substring(0, 10));

			}

		} else if (typeTime == 4) {
			condition.put("viewType", 5);
			dataComp = chartService.getChartSolarSumCostByYear(condition);

			// START--Hoàn thành data giá tiền điện trong 1 ngày
			for (Chart item : dataComp) {
				// ngày chủ nhật không có giờ cao điểm
				getDayOfWeek(item, 2);

				// lấy setting từng khung giờ của dự án
				condition.put("projectId", item.getProjectId());
				condition.put("settingMstId", Constants.settingCostEnergy.SOLAR_LOW_COST_OUT);
				Setting setting1 = settingService.getSettingByDevice(condition);
				float low = Float.parseFloat(setting1.getSettingValue());

				condition.put("settingMstId", Constants.settingCostEnergy.SOLAR_MEDIUM_COST_OUT);
				Setting setting2 = settingService.getSettingByDevice(condition);
				float medium = Float.parseFloat(setting2.getSettingValue());

				condition.put("settingMstId", Constants.settingCostEnergy.SOLAR_HIGH_COST_OUT);
				Setting setting3 = settingService.getSettingByDevice(condition);
				float high = Float.parseFloat(setting3.getSettingValue());

				item.setCostHighOut(item.getCostHighOut() * high);
				item.setCostMediumOut(item.getCostMediumOut() * medium);
				item.setCostLowOut(item.getCostLowOut() * low);
				item.setViewTime(item.getViewTime().substring(0, 4));
			}
			// END--Hoàn thành data giá tiền điện trong 1 ngày
		}

		List<Chart> chartLoadCost = new ArrayList<>();

		for (Chart chartX : dataComp) {
			float cost = (chartX.getCostHighIn() == null ? 0 : chartX.getCostHighIn())
					+ (chartX.getCostMediumIn() == null ? 0 : chartX.getCostMediumIn())
					+ (chartX.getCostLowIn() == null ? 0 : chartX.getCostLowIn());
			chartX.setCost(cost);
			chartLoadCost.add(chartX);
		}

		JSONArray resultArray = new JSONArray();
		Float cumulativeTotal = 0.00f;
		for (Chart data : chartLoadCost) {
			String name = data.getProjectName();
			float value = data.getCost();
			String time = data.getViewTime();
			String nameDevice = data.getDeviceName();
			// Tạo một đối tượng JSON đại diện cho mỗi bản ghi
			JSONObject recordObject = new JSONObject();
			recordObject.put(name, value);

			// Kiểm tra xem trường "time" đã tồn tại trong danh sách JSON chưa
			boolean timeExists = false;
			for (Object obj : resultArray) {
				if (obj instanceof JSONObject) {
					JSONObject timeObject = (JSONObject) obj;
					if (timeObject.containsKey("time") && timeObject.get("time").equals(time)) {
						Float total = timeObject.containsKey("total") ? (Float) timeObject.get("total") : 0;
						if (Integer.parseInt(projectId) != 0) {
							timeObject.put(nameDevice, value);
						} else {
							timeObject.put(name, value);
						}

						cumulativeTotal += value;
						timeObject.put("total", cumulativeTotal);
						timeExists = true;
						break;
					}
				}
			}

			// Nếu trường "time" chưa tồn tại, tạo một đối tượng JSON mới và thêm vào danh
			// sách

			if (!timeExists) {
				JSONObject timeObject = new JSONObject();
				timeObject.put("time", time);
				if (Integer.parseInt(projectId) != 0) {
					timeObject.put(nameDevice, value);
				} else {
					timeObject.put(name, value);
				}
				cumulativeTotal += value;
				timeObject.put("total", cumulativeTotal);
				resultArray.add(timeObject);
			}
		}

		Map<String, Object> mapData = new HashMap<>();
		mapData.put("data", resultArray);
		mapData.put("chart", chart);
		mapData.put("dataComp", dataComp);

		log.info("getChartCostSolar END");

		return new ResponseEntity<>(mapData, HttpStatus.OK);
	}

	@GetMapping("/grid")
	public ResponseEntity<?> getChartGrid(@RequestParam("fromDate") final String fromDate,
			@RequestParam("toDate") final String toDate, @RequestParam("customerId") final Integer customerId,
			@RequestParam("projectId") final String projectId, @RequestParam("typeTime") final Integer typeTime,
			@RequestParam("deviceId") final String deviceId) {
		log.info("getChartGrid START");
		String schema = Schema.getSchemas(customerId);
		Map<String, Object> condition = new HashMap<>();
		condition.put(SCHEMA, schema);
		if (Integer.parseInt(projectId) != 0) {
			condition.put(PROJECT_ID, projectId);
		}
		if (deviceId != "" && deviceId != "0") {
			condition.put(DEVICE_ID, deviceId);
		}
		condition.put("fromDate", fromDate);
		condition.put("toDate", toDate);
		if (typeTime == 1) {
			// typeTime = 1 là so sánh theo hôm nay - hôm qua, lấy giữ liệu giờ trong ngày
			condition.put("viewType", 5);
		} else if (typeTime == 2) {
			// typeTime = 2 là so sánh theo tháng này - tháng trước, lấy giữ liệu ngày trong
			// tháng
			condition.put("viewType", 3);
		} else if (typeTime == 3) {
			// typeTime = 3 là so sánh theo năm nay - năm sau, lấy giữ liệu tháng trong năm
			condition.put("viewType", 2);
			condition.put("toDate", toDate);
		} else if (typeTime == 4) {
			// typeTime = 4 là so sánh theo năm
			condition.put("viewType", 1);
		} else if (typeTime == 5) {
			// Tùy chọn
			condition.put("typeTime", 5);
			condition.put("viewType", 5);
		}

		List<DataRmuDrawer1> dataComp = chartService.getChartRmuByCustomerId(condition);
		for (DataRmuDrawer1 item : dataComp) {
			if (item.getPTotal() < -6000000 || item.getPTotal() > 6000000) {
				item.setPTotal(null);
			}
		}

		List<DataRmuDrawer1> dataNow = chartService.getChartRmuByCustomerId(condition);

		for (DataRmuDrawer1 item : dataNow) {
			if (item.getPTotal() < -6000000 || item.getPTotal() > 6000000) {
				item.setPTotal(null);
			}
		}

		JSONArray resultArray = new JSONArray();
		Integer cumulativeTotal = 0;

		for (DataRmuDrawer1 data : dataComp) {
			String name = data.getProjectName();
			Integer value = data.getEp();
			String time = data.getTime();
			String nameDevice = data.getDeviceName();

			// Tạo một đối tượng JSON đại diện cho mỗi bản ghi
			JSONObject recordObject = new JSONObject();
			recordObject.put(name, value);

			// Kiểm tra xem trường "time" đã tồn tại trong danh sách JSON chưa
			boolean timeExists = false;

			for (Object obj : resultArray) {
				if (obj instanceof JSONObject) {
					JSONObject timeObject = (JSONObject) obj;
					if (timeObject.containsKey("time") && timeObject.get("time").equals(time)) {
						Integer total = timeObject.containsKey("total") ? (int) timeObject.get("total") : 0;
						if (Integer.parseInt(projectId) != 0) {
							timeObject.put(nameDevice, value);
						} else {
							timeObject.put(name, value);
						}
						cumulativeTotal += value;
						timeObject.put("total", cumulativeTotal);
						timeExists = true;
						break;
					}
				}
			}

			// Nếu trường "time" chưa tồn tại, tạo một đối tượng JSON mới và thêm vào danh

			if (!timeExists) {
				JSONObject timeObject = new JSONObject();
				timeObject.put("time", time);
				if (Integer.parseInt(projectId) != 0) {
					timeObject.put(nameDevice, value);
				} else {
					timeObject.put(name, value);
				}
				cumulativeTotal += value;
				timeObject.put("total", cumulativeTotal);
				resultArray.add(timeObject);
			}
		}

		Map<String, Object> mapData = new HashMap<>();
		mapData.put("dataNow", dataNow);
		mapData.put("dataComp", dataComp);
		mapData.put("dataLoadAllSite", resultArray);
		log.info("getChartGrid END");

		return new ResponseEntity<>(mapData, HttpStatus.OK);
	}

	@GetMapping("/grid-power")
	public ResponseEntity<?> getChartGridPower(@RequestParam("fromDate") final String fromDate,
			@RequestParam("toDate") final String toDate, @RequestParam("customerId") final Integer customerId,
			@RequestParam("projectId") final String projectId, @RequestParam("typeTime") final Integer typeTime,
			@RequestParam("deviceId") final String deviceId) {
		log.info("getChartGridPower START");
		String schema = Schema.getSchemas(customerId);
		Map<String, Object> condition = new HashMap<>();
		condition.put(SCHEMA, schema);
		if (Integer.parseInt(projectId) != 0) {
			condition.put(PROJECT_ID, projectId);
		}
		if (deviceId != "" && deviceId != "0") {
			condition.put(DEVICE_ID, deviceId);
		}
		List<Chart> dataComp = new ArrayList<>();
		List<Chart> dataNow = new ArrayList<>();
		condition.put("fromDate", fromDate);
		condition.put("toDate", toDate);
		if (typeTime == 1) {
			// typeTime = 1 là so sánh theo hôm nay - hôm qua, lấy giữ liệu giờ trong ngày
			condition.put("viewType", 5);
			condition.put("typeTime", 5);
		} else if (typeTime == 2) {
			// typeTime = 2 là so sánh theo tháng này - tháng trước, lấy giữ liệu ngày trong
			// tháng
			condition.put("typeTime", 5);
			condition.put("viewType", 5);
			condition.put("toDate", toDate);
		} else if (typeTime == 3) {
			// typeTime = 3 là so sánh theo năm nay - năm sau, lấy giữ liệu tháng trong năm
			condition.put("viewType", 2);
		} else if (typeTime == 4) {
			// typeTime = 4 là so sánh theo năm
			condition.put("viewType", 1);
		} else if (typeTime == 5) {
			// Tùy chọn
			condition.put("typeTime", 5);
			condition.put("viewType", 5);
			condition.put("toDate", toDate);
		}
		if (typeTime == 5 || typeTime == 3 || typeTime == 2) {
			condition.put("fromDate", fromDate);
		} else {
//			condition.put("fromDate", toDate);
		}

		dataComp = chartService.getChartRmuPower(condition);
		dataNow = chartService.getChartRmuPower(condition);

		for (Chart item : dataComp) {
			if (item.getPTotal() < 0) {
				item.setPTotal(null);
			}
		}

		for (Chart item : dataNow) {
			if (item.getPTotal() < 0) {
				item.setPTotal(null);
			}
		}

		Float cumulativeTotal = 0.00f;
		JSONArray resultArray = new JSONArray();
		for (Chart data : dataComp) {
			String name = data.getProjectName();
			Float value = data.getPTotal();
			String time = data.getTime();
			String nameDevice = data.getDeviceName();
			// Tạo một đối tượng JSON đại diện cho mỗi bản ghi
			JSONObject recordObject = new JSONObject();
			recordObject.put(name, value);

			// Kiểm tra xem trường "time" đã tồn tại trong danh sách JSON chưa
			boolean timeExists = false;
			for (Object obj : resultArray) {
				if (obj instanceof JSONObject) {
					JSONObject timeObject = (JSONObject) obj;
					if (timeObject.containsKey("time") && timeObject.get("time").equals(time)) {
						Float total = timeObject.containsKey("total") ? (float) timeObject.get("total") : 0;
						if (Integer.parseInt(projectId) != 0) {
							timeObject.put(nameDevice, value);
						} else {
							timeObject.put(name, value);
						}
						cumulativeTotal += value;
						timeObject.put("total", cumulativeTotal);
						timeExists = true;
						break;
					}
				}
			}
			// Nếu trường "time" chưa tồn tại, tạo một đối tượng JSON mới và thêm vào danh
			// sách
			if (!timeExists) {
				JSONObject timeObject = new JSONObject();
				timeObject.put("time", time);
				if (Integer.parseInt(projectId) != 0) {
					timeObject.put(nameDevice, value);
				} else {
					timeObject.put(name, value);
				}
				cumulativeTotal += value;
				timeObject.put("total", cumulativeTotal);
				resultArray.add(timeObject);
			}
		}
		Map<String, Object> mapData = new HashMap<>();
		mapData.put("dataNow", dataNow);
		mapData.put("dataComp", dataComp);
		mapData.put("dataLoadAllSite", resultArray);
		log.info("getChartGridPower END");

		return new ResponseEntity<>(mapData, HttpStatus.OK);
	}

	@GetMapping("/grid-cost")
	public ResponseEntity<?> getChartGridCost(@RequestParam("fromDate") final String fromDate,
			@RequestParam("toDate") final String toDate, @RequestParam("customerId") final Integer customerId,
			@RequestParam("projectId") final String projectId, @RequestParam("typeTime") final Integer typeTime,
			@RequestParam("deviceId") final String deviceId) {
		log.info("getChartCostGRID START");
		String schema = Schema.getSchemas(customerId);
		Map<String, Object> condition = new HashMap<>();
		List<Chart> dataComp = new ArrayList<>();
		List<Chart> chart = new ArrayList<>();
		condition.put(SCHEMA, schema);
		if (Integer.parseInt(projectId) != 0) {
			condition.put(PROJECT_ID, projectId);
		}
		if (deviceId != "" && deviceId != "0") {
			condition.put(DEVICE_ID, deviceId);
		}
		condition.put("fromDate", fromDate);
		condition.put("toDate", toDate);

		// typeTime(1-hôm nay, 2-hôm qua), lấy giữ liệu giờ, phút trong ngày
		if (typeTime == 1) {
			condition.put("viewType", 5);
			dataComp = chartService.getChartGridCostByCustomerId(condition);
			for (Chart item : dataComp) {
				setCost(item, typeTime, 5);
			}
			chart = sumByHour(dataComp, 5);
			for (Chart item : chart) {
				if (item.getCostLowIn() == 0) {
					item.setCostLowIn(null);
				}
				if (item.getCostMediumIn() == 0) {
					item.setCostMediumIn(null);
				}
				if (item.getCostHighIn() == 0) {
					item.setCostHighIn(null);
				}

			}

		} else if (typeTime == 5) {
			condition.put("viewTime", 5);
			condition.put("typeTime", 5);
			dataComp = chartService.getChartGridCostByCustomerId(condition);
			for (Chart item : dataComp) {
				setCost(item, typeTime, 5);
			}
			chart = sumByHour(dataComp, 5);
			for (Chart item : chart) {
				if (item.getCostLowIn() == 0) {
					item.setCostLowIn(null);
				}
				if (item.getCostMediumIn() == 0) {
					item.setCostMediumIn(null);
				}
				if (item.getCostHighIn() == 0) {
					item.setCostHighIn(null);
				}

			}
		}
		// 3-tháng này || 4- tháng trước || 5-3 tháng trước || 6 - 6 tháng trước lấy dữ
		// liệu sum (ep) các thiết bị trong
		// cùng 1 project
		else if (typeTime == 2) {
			condition.put("viewType", 5);
			dataComp = chartService.getChartGridSumCostByDay(condition);
			// START--Hoàn thành data giá tiền điện trong 1 ngày
			for (Chart item : dataComp) {
				// ngày chủ nhật không có giờ cao điểm
				getDayOfWeek(item, 1);

				// lấy setting từng khung giờ của dự án
				condition.put("projectId", item.getProjectId());
				condition.put("settingMstId", Constants.settingCostEnergy.SOLAR_LOW_COST_OUT);
				Setting setting1 = settingService.getSettingByDevice(condition);
				float low = Float.parseFloat(setting1.getSettingValue());

				condition.put("settingMstId", Constants.settingCostEnergy.GRID_MEDIUM_COST_IN);
				Setting setting2 = settingService.getSettingByDevice(condition);
				float medium = Float.parseFloat(setting2.getSettingValue());

				condition.put("settingMstId", Constants.settingCostEnergy.GRID_HIGH_COST_IN);
				Setting setting3 = settingService.getSettingByDevice(condition);
				float high = Float.parseFloat(setting3.getSettingValue());

				item.setCostHighIn(item.getCostHighIn() * high);
				item.setCostMediumIn(item.getCostMediumIn() * medium);
				item.setCostLowIn(item.getCostLowIn() * low);
				item.setViewTime(item.getViewTime().substring(0, 10));
//				item.setViewTime(item.getViewTime());
			}
		}
		// END--Hoàn thành data giá tiền điện trong 1 ngày

		// Lấy ngày kiểu yyyy-mm-dd trong fromdate, todate

		// 3 năm
		else if (typeTime == 3) {
			condition.put("viewType", 5);
			dataComp = chartService.getChartGridSumCostByYear(condition);
			// START--Hoàn thành data giá tiền điện trong 1 ngày
			for (Chart item : dataComp) {
				// ngày chủ nhật không có giờ cao điểm
				getDayOfWeek(item, 1);

				// lấy setting từng khung giờ của dự án
				condition.put("projectId", item.getProjectId());
				condition.put("settingMstId", Constants.settingCostEnergy.GRID_LOW_COST_IN);
				Setting setting1 = settingService.getSettingByDevice(condition);
				float low = Float.parseFloat(setting1.getSettingValue());

				condition.put("settingMstId", Constants.settingCostEnergy.GRID_MEDIUM_COST_IN);
				Setting setting2 = settingService.getSettingByDevice(condition);
				float medium = Float.parseFloat(setting2.getSettingValue());

				condition.put("settingMstId", Constants.settingCostEnergy.GRID_HIGH_COST_IN);
				Setting setting3 = settingService.getSettingByDevice(condition);
				float high = Float.parseFloat(setting3.getSettingValue());

				item.setCostHighIn(item.getCostHighIn() * high);
				item.setCostMediumIn(item.getCostMediumIn() * medium);
				item.setCostLowIn(item.getCostLowIn() * low);
				item.setViewTime(item.getViewTime().substring(0, 10));

			}

		} else if (typeTime == 4) {
			condition.put("viewType", 5);
			dataComp = chartService.getChartGridSumCostByDay(condition);

			// START--Hoàn thành data giá tiền điện trong 1 ngày
			for (Chart item : dataComp) {
				// ngày chủ nhật không có giờ cao điểm
				getDayOfWeek(item, 1);

				// lấy setting từng khung giờ của dự án
				condition.put("projectId", item.getProjectId());
				condition.put("settingMstId", Constants.settingCostEnergy.SOLAR_LOW_COST_OUT);
				Setting setting1 = settingService.getSettingByDevice(condition);
				float low = Float.parseFloat(setting1.getSettingValue());

				condition.put("settingMstId", Constants.settingCostEnergy.SOLAR_MEDIUM_COST_OUT);
				Setting setting2 = settingService.getSettingByDevice(condition);
				float medium = Float.parseFloat(setting2.getSettingValue());

				condition.put("settingMstId", Constants.settingCostEnergy.SOLAR_HIGH_COST_OUT);
				Setting setting3 = settingService.getSettingByDevice(condition);
				float high = Float.parseFloat(setting3.getSettingValue());

				item.setCostHighOut(item.getCostHighOut() * high);
				item.setCostMediumOut(item.getCostMediumOut() * medium);
				item.setCostLowOut(item.getCostLowOut() * low);
				item.setViewTime(item.getViewTime().substring(0, 4));
			}
			// END--Hoàn thành data giá tiền điện trong 1 ngày
		}

		List<Chart> chartLoadCost = new ArrayList<>();

		for (Chart chartX : dataComp) {
			float cost = (chartX.getCostHighIn() == null ? 0 : chartX.getCostHighIn())
					+ (chartX.getCostMediumIn() == null ? 0 : chartX.getCostMediumIn())
					+ (chartX.getCostLowIn() == null ? 0 : chartX.getCostLowIn());
			chartX.setCost(cost);
			chartLoadCost.add(chartX);
		}

		JSONArray resultArray = new JSONArray();
		Float cumulativeTotal = 0.00f;
		for (Chart data : chartLoadCost) {
			String name = data.getProjectName();
			float value = data.getCost();
			String time = data.getViewTime();
			String nameDevice = data.getDeviceName();
			// Tạo một đối tượng JSON đại diện cho mỗi bản ghi
			JSONObject recordObject = new JSONObject();
			recordObject.put(name, value);

			// Kiểm tra xem trường "time" đã tồn tại trong danh sách JSON chưa
			boolean timeExists = false;
			for (Object obj : resultArray) {
				if (obj instanceof JSONObject) {
					JSONObject timeObject = (JSONObject) obj;
					if (timeObject.containsKey("time") && timeObject.get("time").equals(time)) {
						Float total = timeObject.containsKey("total") ? (Float) timeObject.get("total") : 0;
						if (Integer.parseInt(projectId) != 0) {
							timeObject.put(nameDevice, value);
						} else {
							timeObject.put(name, value);
						}

						cumulativeTotal += value;
						timeObject.put("total", cumulativeTotal);
						timeExists = true;
						break;
					}
				}
			}

			// Nếu trường "time" chưa tồn tại, tạo một đối tượng JSON mới và thêm vào danh
			// sách

			if (!timeExists) {
				JSONObject timeObject = new JSONObject();
				timeObject.put("time", time);
				if (Integer.parseInt(projectId) != 0) {
					timeObject.put(nameDevice, value);
				} else {
					timeObject.put(name, value);
				}
				cumulativeTotal += value;
				timeObject.put("total", cumulativeTotal);
				resultArray.add(timeObject);
			}
		}

		Map<String, Object> mapData = new HashMap<>();
		mapData.put("data", resultArray);
		mapData.put("chart", chart);
		mapData.put("dataComp", dataComp);

		log.info("getChartCostGrid END");

		return new ResponseEntity<>(mapData, HttpStatus.OK);
	}

	// hàm tính tiền điện, giá bán điện trong 15p
	public void setCost(Chart data, Integer typeTime, Integer typeModule) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		Map<String, Object> condition = new HashMap<>();
		condition.put("projectId", data.getProjectId());

		// lấy ngày trong tuần
		if (typeTime == 1 || typeTime == 2) {
			try {
				date = formatter.parse(data.getViewTime());
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

		// lấy giờ, phút
		String[] time = data.getViewTime().split(" ");
		String[] miniTime = time[1].split(":");
		int hour = Integer.parseInt(miniTime[0] + miniTime[1]);
		// chủ nhật = 1, 2200 là 22h00p
		if (dayOfWeek == 1) {
			if (hour >= 2200 || hour < 400) {
				if (typeModule == 1) {
					condition.put("settingMstId", Constants.settingCostEnergy.LOAD_LOW_COST_IN);
					Setting setting = settingService.getSettingByDevice(condition);
					data.setCostLowIn(Float.parseFloat(setting.getSettingValue()) * data.getEpIn());
				} else if (typeModule == 2) {
					condition.put("settingMstId", Constants.settingCostEnergy.SOLAR_LOW_COST_OUT);
					Setting setting = settingService.getSettingByDevice(condition);
					data.setCostLowOut(Float.parseFloat(setting.getSettingValue()) * data.getEpOut());
				} else if (typeModule == 5) {
					condition.put("settingMstId", Constants.settingCostEnergy.GRID_LOW_COST_IN);
					Setting setting = settingService.getSettingByDevice(condition);
					data.setCostLowIn(Float.parseFloat(setting.getSettingValue()) * data.getEpIn());
				}
			} else {
				if (typeModule == 1) {
					condition.put("settingMstId", Constants.settingCostEnergy.LOAD_MEDIUM_COST_IN);
					Setting setting = settingService.getSettingByDevice(condition);
					data.setCostMediumIn(Float.parseFloat(setting.getSettingValue()) * data.getEpIn());
				} else if (typeModule == 2) {
					condition.put("settingMstId", Constants.settingCostEnergy.SOLAR_MEDIUM_COST_OUT);
					Setting setting = settingService.getSettingByDevice(condition);
					data.setCostMediumOut(Float.parseFloat(setting.getSettingValue()) * data.getEpOut());
				} else if (typeModule == 5) {
					condition.put("settingMstId", Constants.settingCostEnergy.GRID_MEDIUM_COST_IN);
					Setting setting = settingService.getSettingByDevice(condition);
					data.setCostMediumIn(Float.parseFloat(setting.getSettingValue()) * data.getEpIn());
				}
			}
		} else {
			if (hour >= 2200 || hour < 400) {
				// giờ thấp điểm
				if (typeModule == 1) {
					condition.put("settingMstId", Constants.settingCostEnergy.LOAD_LOW_COST_IN);
					Setting setting = settingService.getSettingByDevice(condition);
					data.setCostLowIn(Float.parseFloat(setting.getSettingValue()) * data.getEpIn());
				} else if (typeModule == 2) {
					condition.put("settingMstId", Constants.settingCostEnergy.SOLAR_LOW_COST_OUT);
					Setting setting = settingService.getSettingByDevice(condition);
					data.setCostLowOut(Float.parseFloat(setting.getSettingValue()) * data.getEpOut());
				} else if (typeModule == 5) {
					condition.put("settingMstId", Constants.settingCostEnergy.GRID_LOW_COST_IN);
					Setting setting = settingService.getSettingByDevice(condition);
					data.setCostLowIn(Float.parseFloat(setting.getSettingValue()) * data.getEpIn());
				}
			} else if ((hour >= 400 && hour < 930) || (hour >= 1130 && hour < 1700) || (hour >= 2000 && hour < 2200)) {
				// giờ bình thường
				if (typeModule == 1) {
					condition.put("settingMstId", Constants.settingCostEnergy.LOAD_MEDIUM_COST_IN);
					Setting setting = settingService.getSettingByDevice(condition);
					data.setCostMediumIn(Float.parseFloat(setting.getSettingValue()) * data.getEpIn());
				} else if (typeModule == 2) {
					condition.put("settingMstId", Constants.settingCostEnergy.SOLAR_MEDIUM_COST_OUT);
					Setting setting = settingService.getSettingByDevice(condition);
					data.setCostMediumOut(Float.parseFloat(setting.getSettingValue()) * data.getEpOut());
				} else if (typeModule == 5) {
					condition.put("settingMstId", Constants.settingCostEnergy.GRID_MEDIUM_COST_IN);
					Setting setting = settingService.getSettingByDevice(condition);
					data.setCostMediumIn(Float.parseFloat(setting.getSettingValue()) * data.getEpIn());
				}
			} else {
				// giờ cao điểm
				if (typeModule == 1) {
					condition.put("settingMstId", Constants.settingCostEnergy.LOAD_HIGH_COST_IN);
					Setting setting = settingService.getSettingByDevice(condition);
					data.setCostHighIn(Float.parseFloat(setting.getSettingValue()) * data.getEpIn());
				} else if (typeModule == 2) {
					condition.put("settingMstId", Constants.settingCostEnergy.SOLAR_HIGH_COST_OUT);
					Setting setting = settingService.getSettingByDevice(condition);
					data.setCostHighOut(Float.parseFloat(setting.getSettingValue()) * data.getEpOut());
				} else if (typeModule == 5) {
					condition.put("settingMstId", Constants.settingCostEnergy.GRID_HIGH_COST_IN);
					Setting setting = settingService.getSettingByDevice(condition);
					data.setCostHighIn(Float.parseFloat(setting.getSettingValue()) * data.getEpIn());
				}
			}
		}
	}

	public List<Chart> sumByHour(List<Chart> data, Integer typeModule) {
		List<Chart> dataByHour = new ArrayList<>();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		for (int i = 0; i < 24; i++) {
			Chart item = new Chart();
//            if (i < 10) {
//                item.setViewTime("0" + i + ":00:00");
//            } else {
//                item.setViewTime(i + ":00:00");
//            }
			float costHigh = 0;
			float costLow = 0;
			float costMedium = 0;
			String projectName = "";
			String deviceName = "";
			String viewTime = "";
			for (Chart itemChart : data) {
				projectName = itemChart.getProjectName();
				deviceName = itemChart.getDeviceName();
				viewTime = itemChart.getViewTime();
				Date date = new Date();
				// lấy ngày trong tuần
				try {
					date = formatter.parse(itemChart.getViewTime());
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(date);
				int hourByDay = calendar.get(Calendar.HOUR_OF_DAY);
				if (hourByDay == i) {
					if (typeModule == 1) {
						if (itemChart.getCostHighIn() != null)
							costHigh += itemChart.getCostHighIn();
						if (itemChart.getCostMediumIn() != null)
							costMedium += itemChart.getCostMediumIn();
						if (itemChart.getCostLowIn() != null)
							costLow += itemChart.getCostLowIn();
					} else if (typeModule == 2) {
						if (itemChart.getCostHighOut() != null)
							costHigh += itemChart.getCostHighOut();
						if (itemChart.getCostMediumOut() != null)
							costMedium += itemChart.getCostMediumOut();
						if (itemChart.getCostLowOut() != null)
							costLow += itemChart.getCostLowOut();
					} else if (typeModule == 5) {
						if (itemChart.getCostHighIn() != null)
							costHigh += itemChart.getCostHighIn();
						if (itemChart.getCostMediumIn() != null)
							costMedium += itemChart.getCostMediumIn();
						if (itemChart.getCostLowIn() != null)
							costLow += itemChart.getCostLowIn();
					}
				}
			}
			if (typeModule == 1) {
				item.setCostHighIn(costHigh);
				item.setCostMediumIn(costMedium);
				item.setCostLowIn(costLow);
			} else if (typeModule == 2) {
				item.setCostHighOut(costHigh);
				item.setCostMediumOut(costMedium);
				item.setCostLowOut(costLow);
			} else if (typeModule == 5) {
				item.setCostHighIn(costHigh);
				item.setCostMediumIn(costMedium);
				item.setCostLowIn(costLow);
			}
			item.setProjectName(projectName);
			item.setDeviceName(deviceName);
			dataByHour.add(item);

		}
		return dataByHour;
	}

	@GetMapping("/load-heat")
	public ResponseEntity<?> getChartLoadHeat(@RequestParam("fromDate") final String fromDate,
			@RequestParam("toDate") final String toDate, @RequestParam("customerId") final Integer customerId,
			@RequestParam("projectId") final String projectId, @RequestParam("typeTime") final Integer typeTime,
			@RequestParam("deviceId") final String deviceId, @RequestParam("systemTypeId") final String systemTypeId,
			@RequestParam(value = "ids", required = false) final String ids) {
		log.info("getChartLoadHeat START");
		String schema = Schema.getSchemas(customerId);
		Map<String, Object> condition = new HashMap<>();
		condition.put(SCHEMA, schema);
		if (Integer.parseInt(projectId) != 0) {
			condition.put(PROJECT_ID, projectId);
		}
		if (systemTypeId != "" && systemTypeId != "0") {
			condition.put(SYSTEM_TYPE_ID, systemTypeId);
		}
		if (deviceId != "" && deviceId != "0") {
			condition.put(DEVICE_ID, deviceId);
		}
		if (ids != "" && ids != "0") {
			condition.put("ids", ids);
		}
		condition.put("customerId", customerId);
		condition.put("fromDate", fromDate);
		condition.put("toDate", toDate);
		if (typeTime == 1) {
			// typeTime = 1 là lấy giữ liệu giờ trong ngày
			condition.put("viewType", 5);
			condition.put("type", 1);
		} else if (typeTime == 2 || typeTime == 5) {
			// typeTime = 2 là lấy giữ liệu ngày trong tháng
			condition.put("viewType", 5);
			condition.put("type", 2);
		} else if (typeTime == 3) {
			// typeTime = 3 là lấy giữ liệu tháng trong năm
			condition.put("viewType", 5);
			condition.put("type", 3);
		} else if (typeTime == 4) {
			// typeTime = 3 là lấy giữ liệu tháng trong năm
			condition.put("viewType", 5);
			condition.put("type", 4);
		} else if (typeTime == 6) {
			// typeTime = 6 là lấy giữ liệu tuần trong tháng
			condition.put("viewType", 5);
			condition.put("type", 2);
		}
		List<Chart> data = chartService.getChartLoadByHour(condition);
//		Float power = deviceService.sumPower(condition);
//		if (power == null || power == 0) {
//			List<Chart> dataNull = new ArrayList<>();
//			Map<String, Object> mapData = new HashMap<>();
//			mapData.put("data", dataNull);
//			log.info("power = 0, getChartLoadHeat END");
//			return new ResponseEntity<>(mapData, HttpStatus.OK);
//		} else {
		if (typeTime == 1) {
			for (Chart item : data) {
//					item.setPower(power * 1);
//					item.setRatio(item.getEpIn() / item.getPower() * 100);
				item.setHour(item.getViewTime());
				item.setViewTime(fromDate.substring(0, 10));
			}
		} else if (typeTime == 2 ||  typeTime == 5) {
			for (Chart item : data) {
//					item.setPower(power * 1);
//					item.setRatio(item.getEpIn() / item.getPower() * 100);
				String hour = item.getViewTime().substring(11, 13);
				String viewTime = item.getViewTime().substring(8, 10);
				item.setViewTime(viewTime);
				item.setHour(hour);
			}
		} else if (typeTime == 6) {
			for (Chart item : data) {
//					item.setPower(power * 1);
//					item.setRatio(item.getEpIn() / item.getPower() * 100);
				DateTimeFormatter formatterX = DateTimeFormatter.ofPattern(ES.DATETIME_FORMAT_YMDHMS);
				LocalDate dateX = LocalDate.parse(item.getViewTime(), formatterX);
				DayOfWeek dayOfWeek = dateX.getDayOfWeek();
				int dayOfWeekValue = dayOfWeek.getValue() + 1;
				String day = "";
				if (dayOfWeekValue != 8) {
					day = "Thứ " + dayOfWeekValue;
				} else {
					day = "Chủ nhật";
				}

				String hour = item.getViewTime().substring(11, 13);
				String viewTime = item.getViewTime().substring(0, 10);

				item.setViewTime(viewTime);
				item.setHour(hour);
				item.setDay(day);
			}
		} else if (typeTime == 3) {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = new Date();

			for (Chart item : data) {
				try {
					date = formatter.parse(item.getViewTime());
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(date);
				int year = calendar.get(Calendar.YEAR);
				int month = calendar.get(Calendar.MONTH);
				if (year % 4 == 0) {
					if (month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10
							|| month == 12) {
//							item.setPower(power * 31);
//							item.setRatio(item.getEpIn() / item.getPower() * 100);
					} else {
						if (month == 2) {
//								item.setPower(power * 29);
//								item.setRatio(item.getEpIn() / item.getPower() * 100);
						} else {
//								item.setPower(power * 30);
//								item.setRatio(item.getEpIn() / item.getPower() * 100);
						}
					}
				} else {
					if (month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10
							|| month == 12) {
//							item.setPower(power * 31);
//							item.setRatio(item.getEpIn() / item.getPower() * 100);
					} else {
						if (month == 2) {
//								item.setPower(power * 28);
//								item.setRatio(item.getEpIn() / item.getPower() * 100);
						} else {
//								item.setPower(power * 30);
//								item.setRatio(item.getEpIn() / item.getPower() * 100);
						}
					}
				}
				String hour = item.getViewTime().substring(11, 13);
				String viewTime = item.getViewTime().substring(5, 7);
				item.setViewTime(viewTime);
				item.setHour(hour);
			}

		}
		Map<String, Object> mapData = new HashMap<>();
		mapData.put("data", data);
		log.info("getChartLoadHeat END");

		return new ResponseEntity<>(mapData, HttpStatus.OK);
//		}
	}

	@GetMapping("/solar-heat")
	public ResponseEntity<?> getChartSolarHeat(@RequestParam("fromDate") final String fromDate,
			@RequestParam("toDate") final String toDate, @RequestParam("customerId") final Integer customerId,
			@RequestParam("projectId") final String projectId, @RequestParam("typeTime") final Integer typeTime,
			@RequestParam("deviceId") final String deviceId) {
		log.info("getChartLoadHeat START");
		String schema = Schema.getSchemas(customerId);
		Map<String, Object> condition = new HashMap<>();
		condition.put(SCHEMA, schema);
		if (Integer.parseInt(projectId) != 0) {
			condition.put(PROJECT_ID, projectId);
		}
		if (deviceId != "" && deviceId != "0") {
			condition.put(DEVICE_ID, deviceId);
		}
		condition.put("customerId", customerId);
		condition.put("fromDate", fromDate);
		condition.put("toDate", toDate);
		if (typeTime == 1 || typeTime == 2) {
			// typeTime = 1 là lấy giữ liệu giờ trong ngày
			condition.put("viewType", 5);
			condition.put("type", 1);
		} else if (typeTime == 3 || typeTime == 4) {
			// typeTime = 2 là lấy giữ liệu ngày trong tháng
			condition.put("viewType", 5);
			condition.put("type", 2);
		} else if (typeTime == 7 || typeTime == 8) {
			// typeTime = 3 là lấy giữ liệu tháng trong năm
			condition.put("viewType", 5);
			condition.put("type", 3);
		}

		List<Chart> data = chartService.getChartInverterByHour(condition);
		Float power = deviceService.sumPower(condition);
		if (power == null || power == 0) {
			List<Chart> dataNull = new ArrayList<>();
			Map<String, Object> mapData = new HashMap<>();
			mapData.put("data", dataNull);
			log.info("power = 0, getChartSolarHeat END");
			return new ResponseEntity<>(mapData, HttpStatus.OK);
		} else {
			if (typeTime == 1 || typeTime == 2) {
				for (Chart item : data) {
					item.setPower(power * 1);
					item.setRatio(item.getEpIn() / item.getPower() * 100);
					item.setHour(item.getViewTime());
					item.setViewTime(fromDate.substring(0, 10));
				}
			} else if (typeTime == 3 || typeTime == 4) {
				for (Chart item : data) {
					item.setPower(power * 1);
					item.setRatio(item.getEpIn() / item.getPower() * 100);
					String hour = item.getViewTime().substring(11, 13);
					String viewTime = item.getViewTime().substring(8, 10);
					item.setViewTime(viewTime);
					item.setHour(hour);
				}
			} else if (typeTime == 7 || typeTime == 8) {
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date date = new Date();

				for (Chart item : data) {
					try {
						date = formatter.parse(item.getViewTime());
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(date);
					int year = calendar.get(Calendar.YEAR);
					int month = calendar.get(Calendar.MONTH);
					if (year % 4 == 0) {
						if (month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10
								|| month == 12) {
							item.setPower(power * 31);
							item.setRatio(item.getEpIn() / item.getPower() * 100);
						} else {
							if (month == 2) {
								item.setPower(power * 29);
								item.setRatio(item.getEpIn() / item.getPower() * 100);
							} else {
								item.setPower(power * 30);
								item.setRatio(item.getEpIn() / item.getPower() * 100);
							}
						}
					} else {
						if (month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10
								|| month == 12) {
							item.setPower(power * 31);
							item.setRatio(item.getEpIn() / item.getPower() * 100);
						} else {
							if (month == 2) {
								item.setPower(power * 28);
								item.setRatio(item.getEpIn() / item.getPower() * 100);
							} else {
								item.setPower(power * 30);
								item.setRatio(item.getEpIn() / item.getPower() * 100);
							}
						}
					}
					String hour = item.getViewTime().substring(11, 13);
					String viewTime = item.getViewTime().substring(5, 7);
					item.setViewTime(viewTime);
					item.setHour(hour);
				}

			}
			Map<String, Object> mapData = new HashMap<>();
			mapData.put("data", data);
			log.info("getChartSolarHeat END");

			return new ResponseEntity<>(mapData, HttpStatus.OK);
		}
	}

	@GetMapping("/grid-heat")
	public ResponseEntity<?> getChartGridHeat(@RequestParam("fromDate") final String fromDate,
			@RequestParam("toDate") final String toDate, @RequestParam("customerId") final Integer customerId,
			@RequestParam("projectId") final String projectId, @RequestParam("typeTime") final Integer typeTime,
			@RequestParam("deviceId") final String deviceId) {
		log.info("getChartLoadHeat START");
		String schema = Schema.getSchemas(customerId);
		Map<String, Object> condition = new HashMap<>();
		condition.put(SCHEMA, schema);
		if (Integer.parseInt(projectId) != 0) {
			condition.put(PROJECT_ID, projectId);
		}
		if (deviceId != "" && deviceId != "0") {
			condition.put(DEVICE_ID, deviceId);
		}
		condition.put("customerId", customerId);
		condition.put("fromDate", fromDate);
		condition.put("toDate", toDate);
		if (typeTime == 1 || typeTime == 2) {
			// typeTime = 1 là lấy giữ liệu giờ trong ngày
			condition.put("viewType", 5);
			condition.put("type", 1);
		} else if (typeTime == 3 || typeTime == 4) {
			// typeTime = 2 là lấy giữ liệu ngày trong tháng
			condition.put("viewType", 5);
			condition.put("type", 2);
		} else if (typeTime == 7 || typeTime == 8) {
			// typeTime = 3 là lấy giữ liệu tháng trong năm
			condition.put("viewType", 5);
			condition.put("type", 3);
		}

		List<Chart> data = chartService.getChartRmuByHour(condition);
		Float power = deviceService.sumPower(condition);
		if (power == 0) {
			List<Chart> dataNull = new ArrayList<>();
			Map<String, Object> mapData = new HashMap<>();
			mapData.put("data", dataNull);
			log.info("power = 0, getChartSolarHeat END");
			return new ResponseEntity<>(mapData, HttpStatus.OK);
		} else {
			if (typeTime == 1 || typeTime == 2) {
				for (Chart item : data) {
					item.setPower(power * 1);
					item.setRatio(item.getEpIn() / item.getPower() * 100);
					item.setHour(item.getViewTime());
					item.setViewTime(fromDate.substring(0, 10));
				}
			} else if (typeTime == 3 || typeTime == 4) {
				for (Chart item : data) {
					item.setPower(power * 1);
					item.setRatio(item.getEpIn() / item.getPower() * 100);
					String hour = item.getViewTime().substring(11, 13);
					String viewTime = item.getViewTime().substring(8, 10);
					item.setViewTime(viewTime);
					item.setHour(hour);
				}
			} else if (typeTime == 7 || typeTime == 8) {
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date date = new Date();

				for (Chart item : data) {
					try {
						date = formatter.parse(item.getViewTime());
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(date);
					int year = calendar.get(Calendar.YEAR);
					int month = calendar.get(Calendar.MONTH);
					if (year % 4 == 0) {
						if (month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10
								|| month == 12) {
							item.setPower(power * 31);
							item.setRatio(item.getEpIn() / item.getPower() * 100);
						} else {
							if (month == 2) {
								item.setPower(power * 29);
								item.setRatio(item.getEpIn() / item.getPower() * 100);
							} else {
								item.setPower(power * 30);
								item.setRatio(item.getEpIn() / item.getPower() * 100);
							}
						}
					} else {
						if (month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10
								|| month == 12) {
							item.setPower(power * 31);
							item.setRatio(item.getEpIn() / item.getPower() * 100);
						} else {
							if (month == 2) {
								item.setPower(power * 28);
								item.setRatio(item.getEpIn() / item.getPower() * 100);
							} else {
								item.setPower(power * 30);
								item.setRatio(item.getEpIn() / item.getPower() * 100);
							}
						}
					}
					String hour = item.getViewTime().substring(11, 13);
					String viewTime = item.getViewTime().substring(5, 7);
					item.setViewTime(viewTime);
					item.setHour(hour);
				}

			}
			Map<String, Object> mapData = new HashMap<>();
			mapData.put("data", data);
			log.info("getChartSolarHeat END");

			return new ResponseEntity<>(mapData, HttpStatus.OK);
		}
	}

	// public func
	public void getDayOfWeek(Chart data, Integer typeModule) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		try {
			date = formatter.parse(data.getViewTime());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
		if (dayOfWeek == 1) {
			if (typeModule == 1) {
				data.setCostMediumIn(data.getCostHighIn() + data.getCostMediumIn());
				data.setCostHighIn(0.f);
			} else if (typeModule == 2) {
				data.setCostMediumOut(data.getCostHighOut() + data.getCostMediumOut());
				data.setCostHighOut(0.f);
			} else if (typeModule == 5) {
				data.setCostMediumIn(data.getCostHighIn() + data.getCostMediumIn());
				data.setCostHighIn(0.f);
			}
		}
	}

	private static String convertToCamelCase(String input) {
		// Xóa dấu và chuyển sang chữ thường
		String normalized = removeDiacriticalMarks(input).toLowerCase();

		// Chuyển đổi sang kiểu Camel Case
		StringBuilder camelCase = new StringBuilder();
		boolean capitalizeNext = false;
		for (char c : normalized.toCharArray()) {
			if (Character.isLetterOrDigit(c)) {
				if (capitalizeNext) {
					camelCase.append(Character.toUpperCase(c));
					capitalizeNext = false;
				} else {
					camelCase.append(c);
				}

				// Nếu là khoảng trắng, đánh dấu để chuyển ký tự tiếp theo sang chữ in hoa
				if (c == ' ') {
					capitalizeNext = true;
				}
			}
		}

		return camelCase.toString();
	}

	private static String removeDiacriticalMarks(String input) {
		// Loại bỏ dấu và chuyển về chữ thường
		String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
		Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
		String withoutDiacriticalMarks = pattern.matcher(normalized).replaceAll("");

		// Loại bỏ dấu '-' và ':'
		return withoutDiacriticalMarks.replaceAll("[-:]", "");
	}

	private void createDataCostExcel(JSONArray resultArray, String customerName, String description, Integer typeTime,
			String reportName, Integer systemTypeId, String moduleName, String siteName, String fromDate, String toDate,
			final String dateTime, final String path, final String fileNameExcel, List<Chart> listName,
			String projectId) throws Exception {
		log.info("ChartController.createDataPoweExcel(): START");

		// format dateTime
		String pattern = "yyyy-MM-dd HH:mm";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

		XSSFWorkbook wb = new XSSFWorkbook();
		XSSFSheet sheet1 = wb.createSheet("Dữ liệu tiền điện");
		Row row;
		Cell cell;
		// set font style
		// DataFormat format = wb.createDataFormat();
		CellStyle cs = wb.createCellStyle();
		Font font = wb.createFont();
		font.setFontName("Times New Roman");
		cs.setFont(font);
		cs.setAlignment(HorizontalAlignment.CENTER);
		DataFormat format = wb.createDataFormat();
		String unitTitle = "";
		if (typeTime == 0) {
			cs.setDataFormat(format.getFormat("##0,000 [$kW]"));
			unitTitle = " (VND)";
		} else {
			cs.setDataFormat(format.getFormat("##0,000 [$kW]"));
			unitTitle = " VND)";
		}
		for (int z = 0; z < 2000; z++) {
			row = sheet1.createRow(z);
			for (int j = 0; j < 120; j++) {
				row.createCell(j, CellType.BLANK).setCellStyle(cs);
			}
		}

		// set độ rộng của hàng
		Row row1 = sheet1.getRow(1);
		row1.setHeight((short) 1000);
		Row row2 = sheet1.getRow(4);
		row2.setHeight((short) 1000);

		// set độ rộng của cột
		sheet1.setColumnWidth(0, 5000);
		sheet1.setColumnWidth(1, 5000);
		sheet1.setColumnWidth(2, 5000);
		sheet1.setColumnWidth(3, 6000);
		sheet1.setColumnWidth(4, 6000);
		sheet1.setColumnWidth(5, 5000);

		// Hàng màu xanh ses
		CellRangeAddress region = new CellRangeAddress(0, 0, 0, 5);
		sheet1.addMergedRegion(region);
		cell = sheet1.getRow(0).getCell(0);
		formatExcelFill(wb, region, sheet1, cell, IndexedColors.DARK_BLUE.getIndex(), HorizontalAlignment.CENTER, 0);
		// Tên hàng khách hàng
		region = new CellRangeAddress(1, 1, 0, 5);
		sheet1.addMergedRegion(region);
		cell = sheet1.getRow(1).getCell(0);
		cell.setCellValue(customerName);
		formatExcelCustomerName(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
		// Tên hàng địa chỉ
		region = new CellRangeAddress(2, 2, 0, 5);
		sheet1.addMergedRegion(region);
		cell = sheet1.getRow(2).getCell(0);
		cell.setCellValue(description);
		formatExcelCustomerDescription(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(),
				HorizontalAlignment.LEFT, 1);
		// Tên báo cáo
		region = new CellRangeAddress(4, 4, 0, 5);
		sheet1.addMergedRegion(region);
		cell = sheet1.getRow(4).getCell(0);
		if (typeTime == 1) {
			cell.setCellValue(reportName.toUpperCase() + " THEO NGÀY");
			formatExcelReport(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
		} else if (typeTime == 2) {
			cell.setCellValue(reportName.toUpperCase() + " THEO THÁNG");
			formatExcelReport(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
		} else if (typeTime == 3) {
			cell.setCellValue(reportName.toUpperCase() + " THEO NĂM");
			formatExcelReport(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
		} else if (typeTime == 4) {
			cell.setCellValue(reportName.toUpperCase() + " THEO TỔNG");
			formatExcelReport(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
		} else if (typeTime == 6) {
			cell.setCellValue(reportName.toUpperCase() + " THEO TUẦN");
			formatExcelReport(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
		} else {
			cell.setCellValue(reportName.toUpperCase() + " THEO NGÀY");
			formatExcelReport(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
		}
		// Cột ngày tạo báo cáo
		region = new CellRangeAddress(5, 5, 0, 5);
		sheet1.addMergedRegion(region);
		cell = sheet1.getRow(5).getCell(0);
		cell.setCellValue("Ngày tạo: " + dateTime);
		formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.RIGHT, 1);
		// Cột module
		region = new CellRangeAddress(7, 7, 0, 0);
		cell = sheet1.getRow(7).getCell(0);
		cell.setCellValue("Thành phần");
		formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
		// Cột SITE
		region = new CellRangeAddress(7, 7, 1, 1);
		cell = sheet1.getRow(7).getCell(1);
		cell.setCellValue("Dự án");
		formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
		// Cột thời gian
		region = new CellRangeAddress(7, 7, 3, 3);
		cell = sheet1.getRow(7).getCell(3);
		cell.setCellValue("Thời gian");
		formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
		// Cột giá trị Module
		region = new CellRangeAddress(8, 8, 0, 0);
		cell = sheet1.getRow(8).getCell(0);
		cell.setCellValue(moduleName);
		formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
		// Cột giá trị Site
		region = new CellRangeAddress(8, 8, 1, 1);
		cell = sheet1.getRow(8).getCell(1);
		cell.setCellValue(siteName);
		formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
		// cột giá trị fromDate
		region = new CellRangeAddress(8, 8, 3, 3);
		cell = sheet1.getRow(8).getCell(3);
		cell.setCellValue("Từ: " + fromDate);
		formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
		// cột giá trị toDate
		region = new CellRangeAddress(8, 8, 4, 4);
		cell = sheet1.getRow(8).getCell(4);
		cell.setCellValue("Đến: " + toDate);
		formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

		// cột giá trị null
		region = new CellRangeAddress(9, 9, 0, 0);
		cell = sheet1.getRow(9).getCell(0);
		cell.setCellValue("");
		formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

		// format tiền điện
		Locale localeVN = new Locale("vi", "VN");
		NumberFormat currencyVN = NumberFormat.getCurrencyInstance(localeVN);

		Cell cell1;
		// Cột THỜI GIAN
		region = new CellRangeAddress(12, 12, 0, 1);
		sheet1.addMergedRegion(region);
		cell1 = sheet1.getRow(12).getCell(0);
		cell1.setCellValue("THỜI GIAN");
		formatExcelTable(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

		if (Integer.valueOf(projectId) != 0) {
			for (int i = 0; i <= listName.size(); i++) {
				if (i != listName.size()) {
					region = new CellRangeAddress(12, 12, i + 2, i + 2);
					cell1 = sheet1.getRow(12).getCell(i + 2);
					cell1.setCellValue(listName.get(i).getDeviceName() + unitTitle);

					formatExcelTable(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(),
							HorizontalAlignment.CENTER, 0);
				} else {
					region = new CellRangeAddress(12, 12, i + 2, i + 2);
					cell1 = sheet1.getRow(12).getCell(i + 2);
					cell1.setCellValue("TỔNG" + unitTitle);
					formatExcelTable(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(),
							HorizontalAlignment.CENTER, 0);
				}
			}
		} else {
			for (int i = 0; i <= listName.size(); i++) {
				if (i != listName.size()) {
					region = new CellRangeAddress(12, 12, i + 2, i + 2);
					cell1 = sheet1.getRow(12).getCell(i + 2);
					cell1.setCellValue(listName.get(i).getProjectName() + unitTitle);

					formatExcelTable(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(),
							HorizontalAlignment.CENTER, 0);
				} else {
					region = new CellRangeAddress(12, 12, i + 2, i + 2);
					cell1 = sheet1.getRow(12).getCell(i + 2);
					cell1.setCellValue("TỔNG" + unitTitle);
					formatExcelTable(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(),
							HorizontalAlignment.CENTER, 0);
				}
			}
		}

		int rowCount = 13;
		int count = 1;
		int countViewTime = 13;

		Cell cellData;

		// Cột time
		for (Object item : resultArray) {
			JSONObject timeObject = (JSONObject) item;
			String time = "";
			if (timeObject.containsKey("time")) {
				time = timeObject.get("time").toString();
			}

			final short bgColor;
			if (countViewTime % 2 != 0) {
				bgColor = IndexedColors.WHITE.getIndex();
			} else {
				bgColor = IndexedColors.GREY_25_PERCENT.getIndex();
			}

			region = new CellRangeAddress(countViewTime, countViewTime, 0, 1);
			sheet1.addMergedRegion(region);

			cellData = sheet1.getRow(countViewTime).getCell(0);
			cellData.setCellValue(time);
			formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.CENTER, 0, "");

			count += 1;
			countViewTime += 1;
		}
		;

		if (Integer.valueOf(projectId) != 0) {
			for (int x = 0; x < listName.size(); x++) {
				int rowCountX = 13;
				for (int i = 0; i < resultArray.size(); i++) {

					JSONObject recordObject = (JSONObject) resultArray.get(i);
					String name = "";
					if (recordObject.containsKey(listName.get(x).getDeviceName())) {
						name = recordObject.get(listName.get(x).getDeviceName()).toString();
					}

					final short bgColor;
					if (rowCountX % 2 != 0) {
						bgColor = IndexedColors.WHITE.getIndex();
					} else {
						bgColor = IndexedColors.GREY_25_PERCENT.getIndex();
					}

					region = new CellRangeAddress(rowCountX, rowCountX, 2 + x, 2 + x);
					cellData = sheet1.getRow(rowCountX).getCell(2 + x);
					cellData.setCellValue(Float.valueOf(name.isEmpty() ? "0" : name));
					formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");
					rowCountX += 1;
				}
			}
		} else {
			for (int x = 0; x < listName.size(); x++) {
				int rowCountX = 13;
				for (int i = 0; i < resultArray.size(); i++) {

					JSONObject recordObject = (JSONObject) resultArray.get(i);
					String name = "";
					if (recordObject.containsKey(listName.get(x).getProjectName())) {
						name = recordObject.get(listName.get(x).getProjectName()).toString();
					}

					final short bgColor;
					if (rowCountX % 2 != 0) {
						bgColor = IndexedColors.WHITE.getIndex();
					} else {
						bgColor = IndexedColors.GREY_25_PERCENT.getIndex();
					}

					region = new CellRangeAddress(rowCountX, rowCountX, 2 + x, 2 + x);
					cellData = sheet1.getRow(rowCountX).getCell(2 + x);
					cellData.setCellValue(Float.valueOf(name.isEmpty() ? "0" : name));
					formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");
					rowCountX += 1;
				}
			}
		}

		// Cột total
		Integer rowTotal = listName.size() + 2;
		for (int i = 0; i < resultArray.size(); i++) {
			final short bgColor;
			if (rowCount % 2 != 0) {
				bgColor = IndexedColors.WHITE.getIndex();
			} else {
				bgColor = IndexedColors.GREY_25_PERCENT.getIndex();
			}

			JSONObject recordObject = (JSONObject) resultArray.get(i);
			String total = "";
			if (recordObject.containsKey("total")) {
				total = recordObject.get("total").toString();
			}

			region = new CellRangeAddress(rowCount, rowCount, rowTotal, rowTotal);
			cellData = sheet1.getRow(rowCount).getCell(rowTotal);
			cellData.setCellValue(Float.valueOf(total.isEmpty() ? "0" : total));
			formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");
			rowCount += 1;
		}

		XDDFDataSource date = null;
		CellType type = CellType.ERROR;
		row = sheet1.getRow(1);
		if (row != null) {
			cell = row.getCell(0);
			if (cell != null) {
				type = cell.getCellType();
				if (type == CellType.STRING) {
					date = XDDFDataSourcesFactory.fromStringCellRange(sheet1,
							new CellRangeAddress(13, rowCount - 1, 0, 0));
				} else if (type == CellType.NUMERIC) {
					date = XDDFDataSourcesFactory.fromNumericCellRange(sheet1,
							new CellRangeAddress(21, rowCount - 1, 0, 0));
				} else if (type == CellType.FORMULA) {
					type = cell.getCachedFormulaResultType();
					if (type == CellType.STRING) {
						date = XDDFDataSourcesFactory.fromStringCellRange(sheet1,
								new CellRangeAddress(21, rowCount - 1, 0, 0));
					} else if (type == CellType.NUMERIC) {
						date = XDDFDataSourcesFactory.fromNumericCellRange(sheet1,
								new CellRangeAddress(21, rowCount - 1, 0, 0));
					}
				}
			}
		}

		// set data point colors
		// // (102, 205, 0) xanh nhạt, (255,0,0) đỏ, (255,255,0) vàng
		byte[][] colors = new byte[][] { new byte[] { (byte) 102, (byte) 205, 0 },
				new byte[] { (byte) 102, (byte) 205, 0 }, new byte[] { (byte) 102, (byte) 205, 0 },
				new byte[] { (byte) 102, (byte) 205, 0 }, new byte[] { (byte) 255, (byte) 255, 0 },
				new byte[] { (byte) 255, (byte) 255, 0 }, new byte[] { (byte) 255, (byte) 255, 0 },
				new byte[] { (byte) 255, (byte) 255, 0 }, new byte[] { (byte) 255, (byte) 255, 0 },
				new byte[] { (byte) 255, 0, 0 }, new byte[] { (byte) 255, 0, 0 }, new byte[] { (byte) 255, 0, 0 },
				new byte[] { (byte) 255, (byte) 255, 0 }, new byte[] { (byte) 255, (byte) 255, 0 },
				new byte[] { (byte) 255, (byte) 255, 0 }, new byte[] { (byte) 255, (byte) 255, 0 },
				new byte[] { (byte) 255, (byte) 255, 0 }, new byte[] { (byte) 255, 0, 0 },
				new byte[] { (byte) 255, 0, 0 }, new byte[] { (byte) 255, 0, 0 },
				new byte[] { (byte) 255, (byte) 255, 0 }, new byte[] { (byte) 255, (byte) 255, 0 },
				new byte[] { (byte) 102, (byte) 205, 0 }, new byte[] { (byte) 102, (byte) 205, 0 } };

		// export file
		// access folder export excel
		File folder = new File(path);
		if (!folder.exists()) {
			folder.mkdirs();
		}
		// Tạo file excel trong folder export

		long url = new Date().getTime();
		String exportFilePath = path + File.separator + fileNameExcel + ".xlsx";

		File file = new File(exportFilePath);
		FileOutputStream outFile = null;

		try {
			outFile = new FileOutputStream(file);
			log.info("ReportController.ReportElectricalPowerInDay(): CREATE FILE EXCEL SUCCESS");
		} catch (FileNotFoundException e) {
			log.info("ReportController.ReportElectricalPowerInDay(): ERROR FILE NOT FOUND WHILE EXPORT FILE EXCEL");
			e.printStackTrace();
		} finally {
			try {
				wb.write(outFile);
				outFile.close();
				// wb.dispose();
				wb.close();

			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}

		ZipUtil.pack(folder, new File(path + ".zip"));

	}

	private void createDataPowerExcel(JSONArray resultArray, String customerName, String description, Integer typeTime,
			String reportName, Integer systemTypeId, String moduleName, String siteName, String fromDate, String toDate,
			final String dateTime, final String path, final String fileNameExcel, List<Chart> listName,
			String projectId) throws Exception {
		log.info("ChartController.createDataPoweExcel(): START");

		// format dateTime
		String pattern = "yyyy-MM-dd HH:mm";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

		XSSFWorkbook wb = new XSSFWorkbook();
		XSSFSheet sheet1 = wb.createSheet("Dữ liệu công suất");
		Row row;
		Cell cell;
		// set font style
		// DataFormat format = wb.createDataFormat();
		CellStyle cs = wb.createCellStyle();
		Font font = wb.createFont();
		font.setFontName("Times New Roman");
		cs.setFont(font);
		cs.setAlignment(HorizontalAlignment.CENTER);
		DataFormat format = wb.createDataFormat();
		String unitTitle = "";
		if (typeTime == 0) {
			cs.setDataFormat(format.getFormat("##0,000 [$kW]"));
			unitTitle = " (kW)";
		} else {
			cs.setDataFormat(format.getFormat("##0,000 [$kW]"));
			unitTitle = " (kW)";
		}
		for (int z = 0; z < 2000; z++) {
			row = sheet1.createRow(z);
			for (int j = 0; j < 120; j++) {
				row.createCell(j, CellType.BLANK).setCellStyle(cs);
			}
		}

		// set độ rộng của hàng
		Row row1 = sheet1.getRow(1);
		row1.setHeight((short) 1000);
		Row row2 = sheet1.getRow(4);
		row2.setHeight((short) 1000);

		// set độ rộng của cột
		sheet1.setColumnWidth(0, 5000);
		sheet1.setColumnWidth(1, 5000);
		sheet1.setColumnWidth(2, 5000);
		sheet1.setColumnWidth(3, 6000);
		sheet1.setColumnWidth(4, 6000);
		sheet1.setColumnWidth(5, 5000);

		// Hàng màu xanh ses
		CellRangeAddress region = new CellRangeAddress(0, 0, 0, 5);
		sheet1.addMergedRegion(region);
		cell = sheet1.getRow(0).getCell(0);
		formatExcelFill(wb, region, sheet1, cell, IndexedColors.DARK_BLUE.getIndex(), HorizontalAlignment.CENTER, 0);
		// Tên hàng khách hàng
		region = new CellRangeAddress(1, 1, 0, 5);
		sheet1.addMergedRegion(region);
		cell = sheet1.getRow(1).getCell(0);
		cell.setCellValue(customerName);
		formatExcelCustomerName(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
		// Tên hàng địa chỉ
		region = new CellRangeAddress(2, 2, 0, 5);
		sheet1.addMergedRegion(region);
		cell = sheet1.getRow(2).getCell(0);
		cell.setCellValue(description);
		formatExcelCustomerDescription(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(),
				HorizontalAlignment.LEFT, 1);
		// Tên báo cáo
		region = new CellRangeAddress(4, 4, 0, 5);
		sheet1.addMergedRegion(region);
		cell = sheet1.getRow(4).getCell(0);
		if (typeTime == 1) {
			cell.setCellValue(reportName.toUpperCase() + " THEO NGÀY");
			formatExcelReport(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
		} else if (typeTime == 2) {
			cell.setCellValue(reportName.toUpperCase() + " THEO THÁNG");
			formatExcelReport(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
		} else if (typeTime == 3) {
			cell.setCellValue(reportName.toUpperCase() + " THEO NĂM");
			formatExcelReport(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
		} else if (typeTime == 4) {
			cell.setCellValue(reportName.toUpperCase() + " THEO TỔNG");
			formatExcelReport(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
		} else if (typeTime == 6) {
			cell.setCellValue(reportName.toUpperCase() + " THEO TUẦN");
			formatExcelReport(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
		} else {
			cell.setCellValue(reportName.toUpperCase() + " THEO NGÀY");
			formatExcelReport(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
		}
		// Cột ngày tạo báo cáo
		region = new CellRangeAddress(5, 5, 0, 5);
		sheet1.addMergedRegion(region);
		cell = sheet1.getRow(5).getCell(0);
		cell.setCellValue("Ngày tạo: " + dateTime);
		formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.RIGHT, 1);
		// Cột module
		region = new CellRangeAddress(7, 7, 0, 0);
		cell = sheet1.getRow(7).getCell(0);
		cell.setCellValue("Thành phần");
		formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
		// Cột SITE
		region = new CellRangeAddress(7, 7, 1, 1);
		cell = sheet1.getRow(7).getCell(1);
		cell.setCellValue("Dự án");
		formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
		// Cột thời gian
		region = new CellRangeAddress(7, 7, 3, 3);
		cell = sheet1.getRow(7).getCell(3);
		cell.setCellValue("Thời gian");
		formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
		// Cột giá trị Module
		region = new CellRangeAddress(8, 8, 0, 0);
		cell = sheet1.getRow(8).getCell(0);
		cell.setCellValue(moduleName);
		formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
		// Cột giá trị Site
		region = new CellRangeAddress(8, 8, 1, 1);
		cell = sheet1.getRow(8).getCell(1);
		cell.setCellValue(siteName);
		formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
		// cột giá trị fromDate
		region = new CellRangeAddress(8, 8, 3, 3);
		cell = sheet1.getRow(8).getCell(3);
		cell.setCellValue("Từ: " + fromDate);
		formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
		// cột giá trị toDate
		region = new CellRangeAddress(8, 8, 4, 4);
		cell = sheet1.getRow(8).getCell(4);
		cell.setCellValue("Đến: " + toDate);
		formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

		// cột giá trị null
		region = new CellRangeAddress(9, 9, 0, 0);
		cell = sheet1.getRow(9).getCell(0);
		cell.setCellValue("");
		formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

		// format tiền điện
		Locale localeVN = new Locale("vi", "VN");
		NumberFormat currencyVN = NumberFormat.getCurrencyInstance(localeVN);

		Cell cell1;
		// Cột THỜI GIAN
		region = new CellRangeAddress(12, 12, 0, 1);
		sheet1.addMergedRegion(region);
		cell1 = sheet1.getRow(12).getCell(0);
		cell1.setCellValue("THỜI GIAN");
		formatExcelTable(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

		if (Integer.valueOf(projectId) != 0) {
			for (int i = 0; i <= listName.size(); i++) {
				if (i != listName.size()) {
					region = new CellRangeAddress(12, 12, i + 2, i + 2);
					cell1 = sheet1.getRow(12).getCell(i + 2);
					cell1.setCellValue(listName.get(i).getDeviceName() + unitTitle);

					formatExcelTable(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(),
							HorizontalAlignment.CENTER, 0);
				} else {
					region = new CellRangeAddress(12, 12, i + 2, i + 2);
					cell1 = sheet1.getRow(12).getCell(i + 2);
					cell1.setCellValue("TỔNG" + unitTitle);
					formatExcelTable(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(),
							HorizontalAlignment.CENTER, 0);
				}
			}
		} else {
			for (int i = 0; i <= listName.size(); i++) {
				if (i != listName.size()) {
					region = new CellRangeAddress(12, 12, i + 2, i + 2);
					cell1 = sheet1.getRow(12).getCell(i + 2);
					cell1.setCellValue(listName.get(i).getProjectName() + unitTitle);

					formatExcelTable(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(),
							HorizontalAlignment.CENTER, 0);
				} else {
					region = new CellRangeAddress(12, 12, i + 2, i + 2);
					cell1 = sheet1.getRow(12).getCell(i + 2);
					cell1.setCellValue("TỔNG" + unitTitle);
					formatExcelTable(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(),
							HorizontalAlignment.CENTER, 0);
				}
			}
		}

		int rowCount = 13;
		int count = 1;
		int countViewTime = 13;

		Cell cellData;

		// Cột time
		for (Object item : resultArray) {
			JSONObject timeObject = (JSONObject) item;
			String time = "";
			if (timeObject.containsKey("time")) {
				time = timeObject.get("time").toString();
			}

			final short bgColor;
			if (countViewTime % 2 != 0) {
				bgColor = IndexedColors.WHITE.getIndex();
			} else {
				bgColor = IndexedColors.GREY_25_PERCENT.getIndex();
			}

			region = new CellRangeAddress(countViewTime, countViewTime, 0, 1);
			sheet1.addMergedRegion(region);

			cellData = sheet1.getRow(countViewTime).getCell(0);
			cellData.setCellValue(time);
			formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.CENTER, 0, "");

			count += 1;
			countViewTime += 1;
		}
		;

		if (Integer.valueOf(projectId) != 0) {
			for (int x = 0; x < listName.size(); x++) {
				int rowCountX = 13;
				for (int i = 0; i < resultArray.size(); i++) {

					JSONObject recordObject = (JSONObject) resultArray.get(i);
					String name = "";
					if (recordObject.containsKey(listName.get(x).getDeviceName())) {
						name = recordObject.get(listName.get(x).getDeviceName()).toString();
					}

					final short bgColor;
					if (rowCountX % 2 != 0) {
						bgColor = IndexedColors.WHITE.getIndex();
					} else {
						bgColor = IndexedColors.GREY_25_PERCENT.getIndex();
					}

					region = new CellRangeAddress(rowCountX, rowCountX, 2 + x, 2 + x);
					cellData = sheet1.getRow(rowCountX).getCell(2 + x);
					cellData.setCellValue(Float.valueOf(name.isEmpty() ? "0" : name));
					formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");
					rowCountX += 1;
				}
			}
		} else {
			for (int x = 0; x < listName.size(); x++) {
				int rowCountX = 13;
				for (int i = 0; i < resultArray.size(); i++) {

					JSONObject recordObject = (JSONObject) resultArray.get(i);
					String name = "";
					if (recordObject.containsKey(listName.get(x).getProjectName())) {
						name = recordObject.get(listName.get(x).getProjectName()).toString();
					}

					final short bgColor;
					if (rowCountX % 2 != 0) {
						bgColor = IndexedColors.WHITE.getIndex();
					} else {
						bgColor = IndexedColors.GREY_25_PERCENT.getIndex();
					}

					region = new CellRangeAddress(rowCountX, rowCountX, 2 + x, 2 + x);
					cellData = sheet1.getRow(rowCountX).getCell(2 + x);
					cellData.setCellValue(Float.valueOf(name.isEmpty() ? "0" : name));
					formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");
					rowCountX += 1;
				}
			}
		}

		// Cột total
		Integer rowTotal = listName.size() + 2;
		for (int i = 0; i < resultArray.size(); i++) {
			final short bgColor;
			if (rowCount % 2 != 0) {
				bgColor = IndexedColors.WHITE.getIndex();
			} else {
				bgColor = IndexedColors.GREY_25_PERCENT.getIndex();
			}

			JSONObject recordObject = (JSONObject) resultArray.get(i);
			String total = "";
			if (recordObject.containsKey("total")) {
				total = recordObject.get("total").toString();
			}

			region = new CellRangeAddress(rowCount, rowCount, rowTotal, rowTotal);
			cellData = sheet1.getRow(rowCount).getCell(rowTotal);
			cellData.setCellValue(Float.valueOf(total.isEmpty() ? "0" : total));
			formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");
			rowCount += 1;
		}

		XDDFDataSource date = null;
		CellType type = CellType.ERROR;
		row = sheet1.getRow(1);
		if (row != null) {
			cell = row.getCell(0);
			if (cell != null) {
				type = cell.getCellType();
				if (type == CellType.STRING) {
					date = XDDFDataSourcesFactory.fromStringCellRange(sheet1,
							new CellRangeAddress(13, rowCount - 1, 0, 0));
				} else if (type == CellType.NUMERIC) {
					date = XDDFDataSourcesFactory.fromNumericCellRange(sheet1,
							new CellRangeAddress(21, rowCount - 1, 0, 0));
				} else if (type == CellType.FORMULA) {
					type = cell.getCachedFormulaResultType();
					if (type == CellType.STRING) {
						date = XDDFDataSourcesFactory.fromStringCellRange(sheet1,
								new CellRangeAddress(21, rowCount - 1, 0, 0));
					} else if (type == CellType.NUMERIC) {
						date = XDDFDataSourcesFactory.fromNumericCellRange(sheet1,
								new CellRangeAddress(21, rowCount - 1, 0, 0));
					}
				}
			}
		}

		// set data point colors
		// // (102, 205, 0) xanh nhạt, (255,0,0) đỏ, (255,255,0) vàng
		byte[][] colors = new byte[][] { new byte[] { (byte) 102, (byte) 205, 0 },
				new byte[] { (byte) 102, (byte) 205, 0 }, new byte[] { (byte) 102, (byte) 205, 0 },
				new byte[] { (byte) 102, (byte) 205, 0 }, new byte[] { (byte) 255, (byte) 255, 0 },
				new byte[] { (byte) 255, (byte) 255, 0 }, new byte[] { (byte) 255, (byte) 255, 0 },
				new byte[] { (byte) 255, (byte) 255, 0 }, new byte[] { (byte) 255, (byte) 255, 0 },
				new byte[] { (byte) 255, 0, 0 }, new byte[] { (byte) 255, 0, 0 }, new byte[] { (byte) 255, 0, 0 },
				new byte[] { (byte) 255, (byte) 255, 0 }, new byte[] { (byte) 255, (byte) 255, 0 },
				new byte[] { (byte) 255, (byte) 255, 0 }, new byte[] { (byte) 255, (byte) 255, 0 },
				new byte[] { (byte) 255, (byte) 255, 0 }, new byte[] { (byte) 255, 0, 0 },
				new byte[] { (byte) 255, 0, 0 }, new byte[] { (byte) 255, 0, 0 },
				new byte[] { (byte) 255, (byte) 255, 0 }, new byte[] { (byte) 255, (byte) 255, 0 },
				new byte[] { (byte) 102, (byte) 205, 0 }, new byte[] { (byte) 102, (byte) 205, 0 } };

		// export file
		// access folder export excel
		File folder = new File(path);
		if (!folder.exists()) {
			folder.mkdirs();
		}
		// Tạo file excel trong folder export

		long url = new Date().getTime();
		String exportFilePath = path + File.separator + fileNameExcel + ".xlsx";

		File file = new File(exportFilePath);
		FileOutputStream outFile = null;

		try {
			outFile = new FileOutputStream(file);
			log.info("ReportController.ReportElectricalPowerInDay(): CREATE FILE EXCEL SUCCESS");
		} catch (FileNotFoundException e) {
			log.info("ReportController.ReportElectricalPowerInDay(): ERROR FILE NOT FOUND WHILE EXPORT FILE EXCEL");
			e.printStackTrace();
		} finally {
			try {
				wb.write(outFile);
				outFile.close();
				// wb.dispose();
				wb.close();

			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}

		ZipUtil.pack(folder, new File(path + ".zip"));

	}

	private void createDataEnergyExcel(JSONArray resultArray, String customerName, String description, Integer typeTime,
			String reportName, Integer systemTypeId, String moduleName, String siteName, String fromDate, String toDate,
			final String dateTime, final String path, final String fileNameExcel, List<DataLoadFrame1> listName,
			String projectId) throws Exception {
		log.info("ChartController.createDataEnergyExcel(): START");

		// format dateTime
		String pattern = "yyyy-MM-dd HH:mm";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

		XSSFWorkbook wb = new XSSFWorkbook();
		XSSFSheet sheet1 = wb.createSheet("Dữ liệu năng lượng");
		Row row;
		Cell cell;
		// set font style
		// DataFormat format = wb.createDataFormat();
		CellStyle cs = wb.createCellStyle();
		Font font = wb.createFont();
		font.setFontName("Times New Roman");
		cs.setFont(font);
		cs.setAlignment(HorizontalAlignment.CENTER);
		DataFormat format = wb.createDataFormat();
		String unitTitle = "";
		if (typeTime == 0) {
			cs.setDataFormat(format.getFormat("##0,000 [$kW]"));
			unitTitle = " (kW)";
		} else {
			cs.setDataFormat(format.getFormat("##0,000 [$kWh]"));
			unitTitle = " (kWh)";
		}
		for (int z = 0; z < 2000; z++) {
			row = sheet1.createRow(z);
			for (int j = 0; j < 120; j++) {
				row.createCell(j, CellType.BLANK).setCellStyle(cs);
			}
		}

		// set độ rộng của hàng
		Row row1 = sheet1.getRow(1);
		row1.setHeight((short) 1000);
		Row row2 = sheet1.getRow(4);
		row2.setHeight((short) 1000);

		// set độ rộng của cột
		sheet1.setColumnWidth(0, 5000);
		sheet1.setColumnWidth(1, 5000);
		sheet1.setColumnWidth(2, 5000);
		sheet1.setColumnWidth(3, 6000);
		sheet1.setColumnWidth(4, 6000);
		sheet1.setColumnWidth(5, 5000);

		// Hàng màu xanh ses
		CellRangeAddress region = new CellRangeAddress(0, 0, 0, 5);
		sheet1.addMergedRegion(region);
		cell = sheet1.getRow(0).getCell(0);
		formatExcelFill(wb, region, sheet1, cell, IndexedColors.DARK_BLUE.getIndex(), HorizontalAlignment.CENTER, 0);
		// Tên hàng khách hàng
		region = new CellRangeAddress(1, 1, 0, 5);
		sheet1.addMergedRegion(region);
		cell = sheet1.getRow(1).getCell(0);
		cell.setCellValue(customerName);
		formatExcelCustomerName(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
		// Tên hàng địa chỉ
		region = new CellRangeAddress(2, 2, 0, 5);
		sheet1.addMergedRegion(region);
		cell = sheet1.getRow(2).getCell(0);
		cell.setCellValue(description);
		formatExcelCustomerDescription(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(),
				HorizontalAlignment.LEFT, 1);
		// Tên báo cáo
		region = new CellRangeAddress(4, 4, 0, 5);
		sheet1.addMergedRegion(region);
		cell = sheet1.getRow(4).getCell(0);
		if (typeTime == 1) {
			cell.setCellValue(reportName.toUpperCase() + " THEO NGÀY");
			formatExcelReport(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
		} else if (typeTime == 2) {
			cell.setCellValue(reportName.toUpperCase() + " THEO THÁNG");
			formatExcelReport(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
		} else if (typeTime == 3) {
			cell.setCellValue(reportName.toUpperCase() + " THEO NĂM");
			formatExcelReport(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
		} else if (typeTime == 4) {
			cell.setCellValue(reportName.toUpperCase() + " THEO TỔNG");
			formatExcelReport(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
		} else if (typeTime == 6) {
			cell.setCellValue(reportName.toUpperCase() + " THEO TUẦN");
			formatExcelReport(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
		} else {
			cell.setCellValue(reportName.toUpperCase() + " THEO NGÀY");
			formatExcelReport(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);
		}
		// Cột ngày tạo báo cáo
		region = new CellRangeAddress(5, 5, 0, 5);
		sheet1.addMergedRegion(region);
		cell = sheet1.getRow(5).getCell(0);
		cell.setCellValue("Ngày tạo: " + dateTime);
		formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.RIGHT, 1);
		// Cột module
		region = new CellRangeAddress(7, 7, 0, 0);
		cell = sheet1.getRow(7).getCell(0);
		cell.setCellValue("Thành phần");
		formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
		// Cột SITE
		region = new CellRangeAddress(7, 7, 1, 1);
		cell = sheet1.getRow(7).getCell(1);
		cell.setCellValue("Dự án");
		formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
		// Cột thời gian
		region = new CellRangeAddress(7, 7, 3, 3);
		cell = sheet1.getRow(7).getCell(3);
		cell.setCellValue("Thời gian");
		formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
		// Cột giá trị Module
		region = new CellRangeAddress(8, 8, 0, 0);
		cell = sheet1.getRow(8).getCell(0);
		cell.setCellValue(moduleName);
		formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
		// Cột giá trị Site
		region = new CellRangeAddress(8, 8, 1, 1);
		cell = sheet1.getRow(8).getCell(1);
		cell.setCellValue(siteName);
		formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.LEFT, 1);
		// cột giá trị fromDate
		region = new CellRangeAddress(8, 8, 3, 3);
		cell = sheet1.getRow(8).getCell(3);
		cell.setCellValue("Từ: " + fromDate);
		formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);
		// cột giá trị toDate
		region = new CellRangeAddress(8, 8, 4, 4);
		cell = sheet1.getRow(8).getCell(4);
		cell.setCellValue("Đến: " + toDate);
		formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

		// cột giá trị null
		region = new CellRangeAddress(9, 9, 0, 0);
		cell = sheet1.getRow(9).getCell(0);
		cell.setCellValue("");
		formatExcel(wb, region, sheet1, cell, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 1);

		// format tiền điện
		Locale localeVN = new Locale("vi", "VN");
		NumberFormat currencyVN = NumberFormat.getCurrencyInstance(localeVN);

		Cell cell1;
		// Cột THỜI GIAN
		region = new CellRangeAddress(12, 12, 0, 1);
		sheet1.addMergedRegion(region);
		cell1 = sheet1.getRow(12).getCell(0);
		cell1.setCellValue("THỜI GIAN");
		formatExcelTable(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER, 0);

		if (Integer.valueOf(projectId) != 0) {
			for (int i = 0; i <= listName.size(); i++) {
				if (i != listName.size()) {
					region = new CellRangeAddress(12, 12, i + 2, i + 2);
					cell1 = sheet1.getRow(12).getCell(i + 2);
					cell1.setCellValue(listName.get(i).getDeviceName() + unitTitle);

					formatExcelTable(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(),
							HorizontalAlignment.CENTER, 0);
				} else {
					region = new CellRangeAddress(12, 12, i + 2, i + 2);
					cell1 = sheet1.getRow(12).getCell(i + 2);
					cell1.setCellValue("TỔNG" + unitTitle);
					formatExcelTable(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(),
							HorizontalAlignment.CENTER, 0);
				}
			}
		} else {
			for (int i = 0; i <= listName.size(); i++) {
				if (i != listName.size()) {
					region = new CellRangeAddress(12, 12, i + 2, i + 2);
					cell1 = sheet1.getRow(12).getCell(i + 2);
					cell1.setCellValue(listName.get(i).getProjectName() + unitTitle);

					formatExcelTable(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(),
							HorizontalAlignment.CENTER, 0);
				} else {
					region = new CellRangeAddress(12, 12, i + 2, i + 2);
					cell1 = sheet1.getRow(12).getCell(i + 2);
					cell1.setCellValue("TỔNG" + unitTitle);
					formatExcelTable(wb, region, sheet1, cell1, IndexedColors.WHITE.getIndex(),
							HorizontalAlignment.CENTER, 0);
				}
			}
		}

		int rowCount = 13;
		int count = 1;
		int countViewTime = 13;

		Cell cellData;

		// Cột time
		for (Object item : resultArray) {
			JSONObject timeObject = (JSONObject) item;
			String time = "";
			if (timeObject.containsKey("time")) {
				time = timeObject.get("time").toString();
			}

			final short bgColor;
			if (countViewTime % 2 != 0) {
				bgColor = IndexedColors.WHITE.getIndex();
			} else {
				bgColor = IndexedColors.GREY_25_PERCENT.getIndex();
			}

			region = new CellRangeAddress(countViewTime, countViewTime, 0, 1);
			sheet1.addMergedRegion(region);

			cellData = sheet1.getRow(countViewTime).getCell(0);
			cellData.setCellValue(time);
			formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.CENTER, 0, "");

			count += 1;
			countViewTime += 1;
		}
		;

		if (Integer.valueOf(projectId) != 0) {
			for (int x = 0; x < listName.size(); x++) {
				int rowCountX = 13;
				for (int i = 0; i < resultArray.size(); i++) {

					JSONObject recordObject = (JSONObject) resultArray.get(i);
					String name = "";
					if (recordObject.containsKey(listName.get(x).getDeviceName())) {
						name = recordObject.get(listName.get(x).getDeviceName()).toString();
					}

					final short bgColor;
					if (rowCountX % 2 != 0) {
						bgColor = IndexedColors.WHITE.getIndex();
					} else {
						bgColor = IndexedColors.GREY_25_PERCENT.getIndex();
					}

					region = new CellRangeAddress(rowCountX, rowCountX, 2 + x, 2 + x);
					cellData = sheet1.getRow(rowCountX).getCell(2 + x);
					cellData.setCellValue(Integer.valueOf(String.valueOf(name.isEmpty() ? "0" : name)));

					formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");
					rowCountX += 1;
				}
			}
		} else {
			for (int x = 0; x < listName.size(); x++) {
				int rowCountX = 13;
				for (int i = 0; i < resultArray.size(); i++) {

					JSONObject recordObject = (JSONObject) resultArray.get(i);
					String name = "";
					if (recordObject.containsKey(listName.get(x).getProjectName())) {
						name = recordObject.get(listName.get(x).getProjectName()).toString();
					}

					final short bgColor;
					if (rowCountX % 2 != 0) {
						bgColor = IndexedColors.WHITE.getIndex();
					} else {
						bgColor = IndexedColors.GREY_25_PERCENT.getIndex();
					}

					region = new CellRangeAddress(rowCountX, rowCountX, 2 + x, 2 + x);
					cellData = sheet1.getRow(rowCountX).getCell(2 + x);
//					cellData.setCellValue(String.valueOf(name.isEmpty() ? "0" : name));
					cellData.setCellValue(Integer.valueOf(String.valueOf(name.isEmpty() ? "0" : name)));
					formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");
					rowCountX += 1;
				}
			}
		}

		// Cột total
		Integer rowTotal = listName.size() + 2;
		for (int i = 0; i < resultArray.size(); i++) {
			final short bgColor;
			if (rowCount % 2 != 0) {
				bgColor = IndexedColors.WHITE.getIndex();
			} else {
				bgColor = IndexedColors.GREY_25_PERCENT.getIndex();
			}

			JSONObject recordObject = (JSONObject) resultArray.get(i);
			String total = "";
			if (recordObject.containsKey("total")) {
				total = recordObject.get("total").toString();
			}

			region = new CellRangeAddress(rowCount, rowCount, rowTotal, rowTotal);
			cellData = sheet1.getRow(rowCount).getCell(rowTotal);
			cellData.setCellValue(Integer.valueOf(total.isEmpty() ? "0" : total));
			formatExcelTableBody(wb, region, sheet1, cellData, bgColor, HorizontalAlignment.RIGHT, 1, "");
			rowCount += 1;
		}

		XDDFDataSource date = null;
		CellType type = CellType.ERROR;
		row = sheet1.getRow(1);
		if (row != null) {
			cell = row.getCell(0);
			if (cell != null) {
				type = cell.getCellType();
				if (type == CellType.STRING) {
					date = XDDFDataSourcesFactory.fromStringCellRange(sheet1,
							new CellRangeAddress(13, rowCount - 1, 0, 0));
				} else if (type == CellType.NUMERIC) {
					date = XDDFDataSourcesFactory.fromNumericCellRange(sheet1,
							new CellRangeAddress(21, rowCount - 1, 0, 0));
				} else if (type == CellType.FORMULA) {
					type = cell.getCachedFormulaResultType();
					if (type == CellType.STRING) {
						date = XDDFDataSourcesFactory.fromStringCellRange(sheet1,
								new CellRangeAddress(21, rowCount - 1, 0, 0));
					} else if (type == CellType.NUMERIC) {
						date = XDDFDataSourcesFactory.fromNumericCellRange(sheet1,
								new CellRangeAddress(21, rowCount - 1, 0, 0));
					}
				}
			}
		}

		// set data point colors
		// // (102, 205, 0) xanh nhạt, (255,0,0) đỏ, (255,255,0) vàng
		byte[][] colors = new byte[][] { new byte[] { (byte) 102, (byte) 205, 0 },
				new byte[] { (byte) 102, (byte) 205, 0 }, new byte[] { (byte) 102, (byte) 205, 0 },
				new byte[] { (byte) 102, (byte) 205, 0 }, new byte[] { (byte) 255, (byte) 255, 0 },
				new byte[] { (byte) 255, (byte) 255, 0 }, new byte[] { (byte) 255, (byte) 255, 0 },
				new byte[] { (byte) 255, (byte) 255, 0 }, new byte[] { (byte) 255, (byte) 255, 0 },
				new byte[] { (byte) 255, 0, 0 }, new byte[] { (byte) 255, 0, 0 }, new byte[] { (byte) 255, 0, 0 },
				new byte[] { (byte) 255, (byte) 255, 0 }, new byte[] { (byte) 255, (byte) 255, 0 },
				new byte[] { (byte) 255, (byte) 255, 0 }, new byte[] { (byte) 255, (byte) 255, 0 },
				new byte[] { (byte) 255, (byte) 255, 0 }, new byte[] { (byte) 255, 0, 0 },
				new byte[] { (byte) 255, 0, 0 }, new byte[] { (byte) 255, 0, 0 },
				new byte[] { (byte) 255, (byte) 255, 0 }, new byte[] { (byte) 255, (byte) 255, 0 },
				new byte[] { (byte) 102, (byte) 205, 0 }, new byte[] { (byte) 102, (byte) 205, 0 } };

		// export file
		// access folder export excel
		File folder = new File(path);
		if (!folder.exists()) {
			folder.mkdirs();
		}
		// Tạo file excel trong folder export

		long url = new Date().getTime();
		String exportFilePath = path + File.separator + fileNameExcel + ".xlsx";

		File file = new File(exportFilePath);
		FileOutputStream outFile = null;

		try {
			outFile = new FileOutputStream(file);
			log.info("ReportController.ReportElectricalPowerInDay(): CREATE FILE EXCEL SUCCESS");
		} catch (FileNotFoundException e) {
			log.info("ReportController.ReportElectricalPowerInDay(): ERROR FILE NOT FOUND WHILE EXPORT FILE EXCEL");
			e.printStackTrace();
		} finally {
			try {
				wb.write(outFile);
				outFile.close();
				// wb.dispose();
				wb.close();

			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}

		ZipUtil.pack(folder, new File(path + ".zip"));

	}

	private static List<DataPower> sumPower(List<DataPowerResult> listDataResults) {
		List<DataPower> result = new ArrayList<>();

		for (DataPowerResult dataResult : listDataResults) {
			List<DataPower> dataList = dataResult.getListDataPower();
			for (DataPower item : dataList) {
				DataPower existingItem = findItemByViewTime(result, item.getViewTime());

				if (existingItem != null) {
					existingItem.setPower(existingItem.getPower() + item.getPower());
				} else {
					result.add(new DataPower(item.getPower(), null, null, item.getViewTime()));
				}
			}
		}

		return result;
	}

	private static DataPower findItemByViewTime(List<DataPower> list, String viewTime) {
		for (DataPower item : list) {
			if (item.getViewTime().equals(viewTime)) {
				return item;
			}
		}
		return null;
	}

	private void formatExcelReport(final XSSFWorkbook wb, final CellRangeAddress region, final Sheet sheet,
			final Cell cell, final short bgColor, final HorizontalAlignment hAlign, final int indent) {

		CellStyle cs = wb.createCellStyle();

		DataFormat format = wb.createDataFormat();
		Font font = wb.createFont();
		font.setBold(true);
		font.setColor(IndexedColors.ORANGE.getIndex());
		font.setFontName("Times New Roman");
		font.setFontHeightInPoints((short) 20);
		cs.setFont(font);
		cs.setAlignment(hAlign);
		cs.setVerticalAlignment(VerticalAlignment.CENTER);
		cs.setIndention((short) indent);
		cs.setWrapText(true);
		// cs.setDataFormat(format.getFormat("0.000"));
		cell.setCellStyle(cs);
	}

	private void formatExcelTableBody(final XSSFWorkbook wb, final CellRangeAddress region, final Sheet sheet,
			final Cell cell, final short bgColor, final HorizontalAlignment hAlign, final int indent,
			final String unit) {

		CellStyle cs = wb.createCellStyle();

		DataFormat format = wb.createDataFormat();
		cs.setFillBackgroundColor(bgColor);
		cs.setFillForegroundColor(bgColor);
		cs.setFillPattern(FillPatternType.THICK_BACKWARD_DIAG);
		Font font = wb.createFont();
		font.setFontName("Times New Roman");
		cs.setFont(font);
		cs.setAlignment(hAlign);
		cs.setIndention((short) indent);
		cs.setDataFormat(format.getFormat("0.00 " + unit));
		cs.setVerticalAlignment(VerticalAlignment.CENTER);
		cs.setWrapText(true);
		// cs.setDataFormat(format.getFormat("0.000"));
		cell.setCellStyle(cs);
	}

	private void formatExcelTotal(final XSSFWorkbook wb, final CellRangeAddress region, final Sheet sheet,
			final Cell cell, final short bgColor, final HorizontalAlignment hAlign, final int indent,
			final String unit) {

		CellStyle cs = wb.createCellStyle();
		cs.setFillBackgroundColor(bgColor);
		cs.setFillForegroundColor(bgColor);
		cs.setFillPattern(FillPatternType.THICK_BACKWARD_DIAG);

		DataFormat format = wb.createDataFormat();
		Font font = wb.createFont();
		font.setBold(true);
		font.setFontName("Times New Roman");
		cs.setFont(font);
		font.setColor(IndexedColors.DARK_BLUE.getIndex());
		cs.setAlignment(hAlign);
		cs.setVerticalAlignment(VerticalAlignment.CENTER);
		cs.setIndention((short) indent);
		cs.setWrapText(true);
		cs.setDataFormat(format.getFormat("###,000 " + unit));
		// cs.setDataFormat(format.getFormat("0.000"));
		cell.setCellStyle(cs);
	}

	private void formatExcelTable(final XSSFWorkbook wb, final CellRangeAddress region, final Sheet sheet,
			final Cell cell, final short bgColor, final HorizontalAlignment hAlign, final int indent) {

		CellStyle cs = wb.createCellStyle();

		DataFormat format = wb.createDataFormat();
		Font font = wb.createFont();
		font.setBold(true);
		font.setColor(IndexedColors.DARK_BLUE.getIndex());
		font.setFontName("Times New Roman");
		cs.setFont(font);
		cs.setAlignment(hAlign);
		cs.setIndention((short) indent);
		cs.setVerticalAlignment(VerticalAlignment.CENTER);
		cs.setWrapText(true);
		// cs.setDataFormat(format.getFormat("0.000"));
		cell.setCellStyle(cs);
		RegionUtil.setBorderBottom(BorderStyle.MEDIUM, region, sheet);
	}

	private void formatExcel(final XSSFWorkbook wb, final CellRangeAddress region, final Sheet sheet, final Cell cell,
			final short bgColor, final HorizontalAlignment hAlign, final int indent) {

		CellStyle cs = wb.createCellStyle();

		DataFormat format = wb.createDataFormat();
		Font font = wb.createFont();
		font.setBold(true);
		font.setFontName("Times New Roman");
		cs.setFont(font);
		cs.setAlignment(hAlign);
		cs.setVerticalAlignment(VerticalAlignment.CENTER);
		cs.setIndention((short) indent);
		cs.setWrapText(true);
		// cs.setDataFormat(format.getFormat("0.000"));
		cell.setCellStyle(cs);
		cs.setDataFormat(format.getFormat("##0,##0"));
		// RegionUtil.setBorderTop(BorderStyle.THIN, region, sheet);
		// RegionUtil.setBorderRight(BorderStyle.THIN, region, sheet);
		// RegionUtil.setBorderBottom(BorderStyle.THIN, region, sheet);
		// RegionUtil.setBorderLeft(BorderStyle.THIN, region, sheet);
	}

	private void formatExcelFill(final XSSFWorkbook wb, final CellRangeAddress region, final Sheet sheet,
			final Cell cell, final short bgColor, final HorizontalAlignment hAlign, final int indent) {

		CellStyle cs = wb.createCellStyle();
		cs.setFillBackgroundColor(bgColor);
		cs.setFillForegroundColor(bgColor);
		cs.setFillPattern(FillPatternType.THICK_BACKWARD_DIAG);

		DataFormat format = wb.createDataFormat();
		Font font = wb.createFont();
		font.setBold(true);
		font.setFontName("Times New Roman");
		cs.setFont(font);
		cs.setAlignment(hAlign);
		cs.setVerticalAlignment(VerticalAlignment.CENTER);
		cs.setIndention((short) indent);
		cs.setWrapText(true);
		// cs.setDataFormat(format.getFormat("0.000"));
		cell.setCellStyle(cs);

		// RegionUtil.setBorderTop(BorderStyle.THIN, region, sheet);
		// RegionUtil.setBorderRight(BorderStyle.THIN, region, sheet);
		// RegionUtil.setBorderBottom(BorderStyle.THIN, region, sheet);
		// RegionUtil.setBorderLeft(BorderStyle.THIN, region, sheet);
	}

	private void formatExcelCustomerName(final XSSFWorkbook wb, final CellRangeAddress region, final Sheet sheet,
			final Cell cell, final short bgColor, final HorizontalAlignment hAlign, final int indent) {

		CellStyle cs = wb.createCellStyle();

		DataFormat format = wb.createDataFormat();
		Font font = wb.createFont();
		font.setBold(true);
		font.setFontHeightInPoints((short) 20);
		font.setColor(IndexedColors.DARK_BLUE.getIndex());
		font.setFontName("Times New Roman");
		cs.setFont(font);
		cs.setAlignment(hAlign);
		cs.setVerticalAlignment(VerticalAlignment.CENTER);
		cs.setIndention((short) indent);
		cs.setWrapText(true);
		// cs.setDataFormat(format.getFormat("0.000"));
		cell.setCellStyle(cs);
	}

	private void formatExcelCustomerDescription(final XSSFWorkbook wb, final CellRangeAddress region, final Sheet sheet,
			final Cell cell, final short bgColor, final HorizontalAlignment hAlign, final int indent) {

		CellStyle cs = wb.createCellStyle();

		DataFormat format = wb.createDataFormat();
		Font font = wb.createFont();
		font.setBold(true);
		font.setColor(IndexedColors.DARK_BLUE.getIndex());
		font.setFontName("Times New Roman");
		cs.setFont(font);
		cs.setAlignment(hAlign);
		cs.setVerticalAlignment(VerticalAlignment.CENTER);
		cs.setIndention((short) indent);
		cs.setWrapText(true);
		// cs.setDataFormat(format.getFormat("0.000"));
		cell.setCellStyle(cs);
	}

}