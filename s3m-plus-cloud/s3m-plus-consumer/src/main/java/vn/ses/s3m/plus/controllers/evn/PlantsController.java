package vn.ses.s3m.plus.controllers.evn;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.map.LinkedMap;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vn.ses.s3m.plus.common.Constants;
import vn.ses.s3m.plus.common.DateUtils;
import vn.ses.s3m.plus.common.Schema;
import vn.ses.s3m.plus.dto.Area;
import vn.ses.s3m.plus.dto.Device;
import vn.ses.s3m.plus.dto.Manager;
import vn.ses.s3m.plus.dto.Project;
import vn.ses.s3m.plus.dto.SuperManager;
import vn.ses.s3m.plus.dto.User;
import vn.ses.s3m.plus.dto.evn.DataInverter1EVN;
import vn.ses.s3m.plus.dto.evn.Plant;
import vn.ses.s3m.plus.dto.evn.PlantSend;
import vn.ses.s3m.plus.dto.evn.Schedule;
import vn.ses.s3m.plus.form.JSonPlantForm;
import vn.ses.s3m.plus.service.AreaService;
import vn.ses.s3m.plus.service.DeviceService;
import vn.ses.s3m.plus.service.ManagerService;
import vn.ses.s3m.plus.service.ProjectService;
import vn.ses.s3m.plus.service.SuperManagerService;
import vn.ses.s3m.plus.service.UserService;
import vn.ses.s3m.plus.service.evn.DataInverter1ServiceEVN;
import vn.ses.s3m.plus.service.evn.ScheduleService;

@RequestMapping("/common/evn/plant")
@RestController
public class PlantsController {
	@Autowired
	private SuperManagerService superManagerService;

	@Autowired
	private DeviceService deviceService;

	@Autowired
	private DataInverter1ServiceEVN dataInverter1Service;

	@Autowired
	private ScheduleService scheduleMapper;

	@Autowired
	private ManagerService managerService;

	@Autowired
	private AreaService areaService;

	@Autowired
	private ProjectService projectService;

	@Autowired
	private UserService userService;

	private static List<Device> deviceList;

	private static String globalTupeScope = null;

	@SuppressWarnings("unused")
	@GetMapping("/list")
	public ResponseEntity<?> plants(@RequestParam String typeScropTree, @RequestParam String userName)
			throws ParseException {

//	    String scheduleTree = request.getParameter("scheduleTree");
//		List<Plant> listPlants = new ArrayList<Plants>();
//		if (scheduleTree == null) {
//			scheduleTree = "2";
//		}
//		
//		request.setAttribute("typeScrop", typeScrop);
//		request.setAttribute("scheduleTree", scheduleTree);
		String typeScrop = null;
		String idSchedule = null;
		User user = userService.getUserByUsername(userName);

		Integer customerId = user.getCustomerId();

		String schema = Schema.getSchemas(customerId);

		if (StringUtils.equals(String.valueOf(user.getUserType()), "4")) {
			typeScrop = "1";
			globalTupeScope = typeScrop;
			idSchedule = String.valueOf(user.getTargetId());
		} else if (StringUtils.equals(String.valueOf(user.getUserType()), "5")) {
			typeScrop = "2";
			globalTupeScope = typeScrop;
			idSchedule = String.valueOf(user.getTargetId());
		} else if (StringUtils.equals(String.valueOf(user.getUserType()), "6")) {
			typeScrop = "3";
			globalTupeScope = typeScrop;
			idSchedule = String.valueOf(user.getTargetId());
		} else {
			typeScrop = "0";
			globalTupeScope = typeScrop;
		}

		if (typeScropTree.length() > 0) {
			String[] typeScropTrees = typeScropTree.split("=");
			typeScrop = typeScropTrees[0];
			globalTupeScope = typeScrop;
			idSchedule = typeScropTrees[1];
			List<Plant> pList = new ArrayList<Plant>();
			if (StringUtils.equals(typeScrop, "superManagerId")) {
				deviceList = new ArrayList<>();
				Map<String, String> conditionSuper = new HashMap<String, String>();
				conditionSuper.put("superManagerId", idSchedule);
				Long supperManagerId = Long.parseLong(idSchedule);
				SuperManager superManager = superManagerService.getSuperManagerById(supperManagerId);

				Plant plants = new Plant();

				plants.setName(superManager.getSuperManagerName());

				Map<String, String> condition = new HashMap<String, String>();
				condition.put("superManagerId", String.valueOf(superManager.getSuperManagerId()));
				//// condition.put("systemtypeId", "2");

				List<Device> devices = new ArrayList<Device>();

				condition.put("superManagerId", String.valueOf(superManager.getSuperManagerId()));
				devices = deviceService.getDeviceBySuperManagerId(condition);
				deviceList.addAll(devices);
				Double sumAcPower = null;
				if (devices.size() > 0) {
					sumAcPower = (double) Math.round(
							((devices.stream().mapToDouble(x -> x.getAcPower() == null ? 0 : x.getAcPower()).sum())
									/ 1000000) * 1000)
							/ 1000;
				}

				if (sumAcPower != null) {
					plants.setCongSuatLapDat(sumAcPower);
				}

				Map<String, String> conditionSchedule = new HashMap<String, String>();
				Calendar calendar = Calendar.getInstance();
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				String timeViews = formatter.format(calendar.getTime());
				String timeView = timeViews.split(" ")[0];
				String hourMiniView = timeViews.split(" ")[1];
				conditionSchedule.put("hourMiniView", hourMiniView);
				conditionSchedule.put("stt", String.valueOf(superManager.getSuperManagerId()));
				conditionSchedule.put("typeScrop", "0");
				conditionSchedule.put("deleteFlag", "0");
				conditionSchedule.put("date", timeView);
				List<Schedule> scheduleList = scheduleMapper.getSchedules(conditionSchedule);

				Schedule schedule = new Schedule();
				SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
				SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
				for (int j = 0; j < scheduleList.size(); j++) {
					Date date1 = sdf.parse(dateFormat.format(scheduleList.get(j).getTimeView()) + " "
							+ scheduleList.get(j).getToTime());
					long millisToTime1 = date1.getTime();
					if (schedule.getToTime() != null) {
						Date date2 = sdf.parse(dateFormat.format(schedule.getTimeView()) + " " + schedule.getToTime());
						long millisToTime2 = date2.getTime();
						if (millisToTime1 > millisToTime2) {
							schedule = scheduleList.get(j);
						}
					} else {
						schedule = scheduleList.get(j);
					}
				}
				if (schedule.getTimeView() != null) {
					plants.setFromDateTime(dateFormat.format(schedule.getTimeView()) + " " + schedule.getFromTime());
					plants.setToDateTime(dateFormat.format(schedule.getTimeView()) + " " + schedule.getToTime());
				}

				if (schedule != null && schedule.getCongSuatTietGiam() != null) {
					double congSuatTietGiam = ((double) schedule.getCongSuatTietGiam()) / 1000000;
					plants.setCongSuatTietGiam((double) Math.round(congSuatTietGiam * 1000) / 1000);
				}

				List<String> deviceIds = new ArrayList<String>();
				for (int j = 0; j < devices.size(); j++) {
					Device device = devices.get(j);
					String deviceId = String.valueOf(device.getDeviceId());

					deviceIds.add(deviceId);
				}
				Map<String, Object> conditionCode = new HashMap<String, Object>();
				conditionCode.put("deviceIds", deviceIds);
				conditionCode.put("schema", schema);
				List<DataInverter1EVN> dataInverter1s = new ArrayList<DataInverter1EVN>();
				Calendar cal = Calendar.getInstance();
				Timestamp timestamp = new Timestamp(cal.getTimeInMillis());
				long dateTimeNow = timestamp.getTime();
				if (deviceIds.size() > 0) {
					List<DataInverter1EVN> dataInverter1List = dataInverter1Service
							.getDataInverter1ByDeviceIds(conditionCode);
					for (int i = 0; i < dataInverter1List.size(); i++) {
						DataInverter1EVN dataInverter1 = dataInverter1List.get(i);
						if (dataInverter1.getW() != null) {
							DataInverter1EVN inverter1 = new DataInverter1EVN();
							long ms = dataInverter1.getSentDate().getTime();
							if ((ms + 5 * 60 * 1000) < dateTimeNow) {
								inverter1.setW(0);
							} else {
								inverter1.setW(dataInverter1.getW());
							}
							dataInverter1s.add(inverter1);
						}

					}

				}
				Double sumW = null;
				if (dataInverter1s.size() > 0) {
					double w = (dataInverter1s.stream().mapToDouble(x -> x.getW() == null ? 0 : x.getW()).sum())
							/ 1000000;
					if (sumW == null) {
						sumW = w;
					} else {
						sumW = sumW + w;
					}

				}

				if (sumW != null) {
					plants.setCongSuatHienTai((double) Math.round(sumW * 1000) / 1000);
				}

				if (plants.getCongSuatLapDat() != null) {
					if (plants.getCongSuatTietGiam() == null) {
						Double congSuatChoPhepPhat = (plants.getCongSuatLapDat());
						plants.setCongSuatChoPhepPhat((double) Math.round(congSuatChoPhepPhat * 1000) / 1000);
					} else {
						Double congSuatChoPhepPhat = (plants.getCongSuatLapDat() - plants.getCongSuatTietGiam());
						plants.setCongSuatChoPhepPhat((double) Math.round(congSuatChoPhepPhat * 1000) / 1000);
					}

				}

				if (plants.getCongSuatChoPhepPhat() != null && plants.getCongSuatHienTai() != null) {
					Integer phanTram = (int) (plants.getCongSuatHienTai() / plants.getCongSuatChoPhepPhat()) * 100
							- 100;
					if (phanTram == 5 || phanTram == -5) {
						plants.setStatus("xanh");
					} else if (phanTram > 5 && phanTram <= 10) {
						plants.setStatus("vàng");
					} else if (phanTram < 10) {
						plants.setStatus("xám");
					} else {
						plants.setStatus("đỏ");
					}
				}

				pList.add(plants);

				// List<Device> deviceList =
				// deviceService.getDeviceBySuperManagerId(conditionSuper);

				List<JSonPlantForm> chartData = chartData(deviceList, scheduleList, schema);

				Map<String, Object> result = new HashMap<>();

				result.put("chartData", chartData);
				result.put("Plants", pList);

				return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
			}
			if (StringUtils.equals(typeScrop, "ManagerId")) {
				deviceList = new ArrayList<>();
				Map<String, String> conditionManager = new HashMap<String, String>();
				conditionManager.put("managerId", idSchedule);
				Integer managerId = Integer.parseInt(idSchedule);
				Manager manager = managerService.getManagerById(managerId);

				Plant plants = new Plant();

				plants.setName(manager.getManagerName());

				Map<String, String> condition = new HashMap<String, String>();

				condition.put("managerId", String.valueOf(manager.getManagerId()));
				//// condition.put("systemtypeId", "2");

				List<Device> devices = new ArrayList<Device>();

				condition.put("managerId", String.valueOf(manager.getManagerId()));
				devices = deviceService.getDeviceByManagerId(condition);
				deviceList.addAll(devices);
				Double sumAcPower = null;
				if (devices.size() > 0) {
					sumAcPower = (double) Math.round(
							((devices.stream().mapToDouble(x -> x.getAcPower() == null ? 0 : x.getAcPower()).sum())
									/ 1000000) * 1000)
							/ 1000;
				}

				if (sumAcPower != null) {
					plants.setCongSuatLapDat(sumAcPower);
				}

				Map<String, String> conditionSchedule = new HashMap<String, String>();
				Calendar calendar = Calendar.getInstance();
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				String timeViews = formatter.format(calendar.getTime());
				String timeView = timeViews.split(" ")[0];
				String hourMiniView = timeViews.split(" ")[1];
				conditionSchedule.put("hourMiniView", hourMiniView);
				conditionSchedule.put("stt", String.valueOf(manager.getManagerId()));
				conditionSchedule.put("typeScrop", "1");
				conditionSchedule.put("date", timeView);
				conditionSchedule.put("deleteFlag", "0");
				List<Schedule> scheduleList = scheduleMapper.getSchedules(conditionSchedule);

				Schedule schedule = new Schedule();
				SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
				SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
				for (int j = 0; j < scheduleList.size(); j++) {
					Date date1 = sdf.parse(dateFormat.format(scheduleList.get(j).getTimeView()) + " "
							+ scheduleList.get(j).getToTime());
					long millisToTime1 = date1.getTime();
					if (schedule.getToTime() != null) {
						Date date2 = sdf.parse(dateFormat.format(schedule.getTimeView()) + " " + schedule.getToTime());
						long millisToTime2 = date2.getTime();
						if (millisToTime1 > millisToTime2) {
							schedule = scheduleList.get(j);
						}
					} else {
						schedule = scheduleList.get(j);
					}
				}
				if (schedule.getTimeView() != null) {
					plants.setFromDateTime(dateFormat.format(schedule.getTimeView()) + " " + schedule.getFromTime());
					plants.setToDateTime(dateFormat.format(schedule.getTimeView()) + " " + schedule.getToTime());
				}
				if (schedule != null && schedule.getCongSuatTietGiam() != null) {
					double congSuatTietGiam = ((double) schedule.getCongSuatTietGiam()) / 1000000;
					plants.setCongSuatTietGiam((double) Math.round(congSuatTietGiam * 1000) / 1000);
				}

				List<String> deviceIds = new ArrayList<String>();
				for (int j = 0; j < devices.size(); j++) {
					Device device = devices.get(j);
					String deviceId = String.valueOf(device.getDeviceId());

					deviceIds.add(deviceId);
				}
				Map<String, Object> conditionCode = new HashMap<String, Object>();
				conditionCode.put("deviceIds", deviceIds);
				conditionCode.put("schema", schema);
				List<DataInverter1EVN> dataInverter1s = new ArrayList<DataInverter1EVN>();
				Calendar cal = Calendar.getInstance();
				Timestamp timestamp = new Timestamp(cal.getTimeInMillis());
				long dateTimeNow = timestamp.getTime();
				if (deviceIds.size() > 0) {
					List<DataInverter1EVN> dataInverter1List = dataInverter1Service
							.getDataInverter1ByDeviceIds(conditionCode);
					for (int i = 0; i < dataInverter1List.size(); i++) {
						DataInverter1EVN dataInverter1 = dataInverter1List.get(i);
						if (dataInverter1.getW() != null) {
							DataInverter1EVN inverter1 = new DataInverter1EVN();
							long ms = dataInverter1.getSentDate().getTime();
							if ((ms + 5 * 60 * 1000) < dateTimeNow) {
								inverter1.setW(0);
							} else {
								inverter1.setW(dataInverter1.getW());
							}
							dataInverter1s.add(inverter1);
						}

					}

				}
				Double sumW = null;
				if (dataInverter1s.size() > 0) {
					double w = (dataInverter1s.stream().mapToDouble(x -> x.getW() == null ? 0 : x.getW()).sum())
							/ 1000000;
					if (sumW == null) {
						sumW = w;
					} else {
						sumW = sumW + w;
					}

				}

				if (sumW != null) {
					plants.setCongSuatHienTai((double) Math.round(sumW * 1000) / 1000);
				}

				if (plants.getCongSuatLapDat() != null) {
					if (plants.getCongSuatTietGiam() == null) {
						Double congSuatChoPhepPhat = (plants.getCongSuatLapDat());
						plants.setCongSuatChoPhepPhat((double) Math.round(congSuatChoPhepPhat * 1000) / 1000);
					} else {
						Double congSuatChoPhepPhat = (plants.getCongSuatLapDat() - plants.getCongSuatTietGiam());
						plants.setCongSuatChoPhepPhat((double) Math.round(congSuatChoPhepPhat * 1000) / 1000);
					}

				}

				if (plants.getCongSuatChoPhepPhat() != null && plants.getCongSuatHienTai() != null) {
					Integer phanTram = (int) (plants.getCongSuatHienTai() / plants.getCongSuatChoPhepPhat()) * 100
							- 100;
					if (phanTram == 5 || phanTram == -5) {
						plants.setStatus("xanh");
					} else if (phanTram > 5 && phanTram <= 10) {
						plants.setStatus("vàng");
					} else if (phanTram < 10) {
						plants.setStatus("xám");
					} else {
						plants.setStatus("đỏ");
					}
				}

				pList.add(plants);

				List<JSonPlantForm> chartData = chartData(deviceList, scheduleList, schema);

				Map<String, Object> result = new HashMap<>();

				result.put("chartData", chartData);
				result.put("Plants", pList);

				return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
//
			}
			if (StringUtils.equals(typeScrop, "AreaId")) {
				deviceList = new ArrayList<>();
				Map<String, String> conditionArea = new HashMap<String, String>();
				conditionArea.put("areaId", idSchedule);
				Area area = areaService.getArea(conditionArea);
				Plant plants = new Plant();

				plants.setName(area.getAreaName());

				Map<String, String> condition = new HashMap<String, String>();
				condition.put("areaId", String.valueOf(area.getAreaId()));
				//// condition.put("systemtypeId", "2");

				List<Device> devices = new ArrayList<Device>();

				condition.put("areaId", String.valueOf(area.getAreaId()));
				devices = deviceService.getDeviceByAreaId(condition);
				deviceList.addAll(devices);
				Double sumAcPower = null;
				if (devices.size() > 0) {
					sumAcPower = (double) Math.round(
							((devices.stream().mapToDouble(x -> x.getAcPower() == null ? 0 : x.getAcPower()).sum())
									/ 1000000) * 1000)
							/ 1000;
				}

				if (sumAcPower != null) {
					plants.setCongSuatLapDat(sumAcPower);
				}

				Map<String, String> conditionSchedule = new HashMap<String, String>();
				Calendar calendar = Calendar.getInstance();
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				String timeViews = formatter.format(calendar.getTime());
				String timeView = timeViews.split(" ")[0];
				String hourMiniView = timeViews.split(" ")[1];
				conditionSchedule.put("hourMiniView", hourMiniView);
				conditionSchedule.put("stt", String.valueOf(area.getAreaId()));
				conditionSchedule.put("typeScrop", "2");
				conditionSchedule.put("deleteFlag", "0");
				conditionSchedule.put("date", timeView);
				List<Schedule> scheduleList = scheduleMapper.getSchedules(conditionSchedule);

				Schedule schedule = new Schedule();
				SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
				SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
				for (int j = 0; j < scheduleList.size(); j++) {
					Date date1 = sdf.parse(dateFormat.format(scheduleList.get(j).getTimeView()) + " "
							+ scheduleList.get(j).getToTime());
					long millisToTime1 = date1.getTime();
					if (schedule.getToTime() != null) {
						Date date2 = sdf.parse(dateFormat.format(schedule.getTimeView()) + " " + schedule.getToTime());
						long millisToTime2 = date2.getTime();
						if (millisToTime1 > millisToTime2) {
							schedule = scheduleList.get(j);
						}
					} else {
						schedule = scheduleList.get(j);
					}
				}
				if (schedule.getTimeView() != null) {
					plants.setFromDateTime(dateFormat.format(schedule.getTimeView()) + " " + schedule.getFromTime());
					plants.setToDateTime(dateFormat.format(schedule.getTimeView()) + " " + schedule.getToTime());
				}

				if (schedule != null && schedule.getCongSuatTietGiam() != null) {
					double congSuatTietGiam = ((double) schedule.getCongSuatTietGiam()) / 1000000;
					plants.setCongSuatTietGiam((double) Math.round(congSuatTietGiam * 1000) / 1000);
				}

				List<String> deviceIds = new ArrayList<String>();
				for (int j = 0; j < devices.size(); j++) {
					Device device = devices.get(j);
					String deviceId = String.valueOf(device.getDeviceId());

					deviceIds.add(deviceId);
				}
				Map<String, Object> conditionCode = new HashMap<String, Object>();
				conditionCode.put("deviceIds", deviceIds);
				conditionCode.put("schema", schema);
				List<DataInverter1EVN> dataInverter1s = new ArrayList<DataInverter1EVN>();
				Calendar cal = Calendar.getInstance();
				Timestamp timestamp = new Timestamp(cal.getTimeInMillis());
				long dateTimeNow = timestamp.getTime();
				if (deviceIds.size() > 0) {
					List<DataInverter1EVN> dataInverter1List = dataInverter1Service
							.getDataInverter1ByDeviceIds(conditionCode);
					for (int i = 0; i < dataInverter1List.size(); i++) {
						DataInverter1EVN dataInverter1 = dataInverter1List.get(i);
						if (dataInverter1.getW() != null) {
							DataInverter1EVN inverter1 = new DataInverter1EVN();
							long ms = dataInverter1.getSentDate().getTime();
							if ((ms + 5 * 60 * 1000) < dateTimeNow) {
								inverter1.setW(0);
							} else {
								inverter1.setW(dataInverter1.getW());
							}
							dataInverter1s.add(inverter1);
						}

					}

				}
				Double sumW = null;
				if (dataInverter1s.size() > 0) {
					double w = (dataInverter1s.stream().mapToDouble(x -> x.getW() == null ? 0 : x.getW()).sum())
							/ 1000000;
					if (sumW == null) {
						sumW = w;
					} else {
						sumW = sumW + w;
					}

				}

				if (sumW != null) {
					plants.setCongSuatHienTai((double) Math.round(sumW * 1000) / 1000);
				}

				if (plants.getCongSuatLapDat() != null) {
					if (plants.getCongSuatTietGiam() == null) {
						Double congSuatChoPhepPhat = (plants.getCongSuatLapDat());
						plants.setCongSuatChoPhepPhat((double) Math.round(congSuatChoPhepPhat * 1000) / 1000);
					} else {
						Double congSuatChoPhepPhat = (plants.getCongSuatLapDat() - plants.getCongSuatTietGiam());
						plants.setCongSuatChoPhepPhat((double) Math.round(congSuatChoPhepPhat * 1000) / 1000);
					}

				}

				if (plants.getCongSuatChoPhepPhat() != null && plants.getCongSuatHienTai() != null) {
					Integer phanTram = (int) (plants.getCongSuatHienTai() / plants.getCongSuatChoPhepPhat()) * 100
							- 100;
					if (phanTram == 5 || phanTram == -5) {
						plants.setStatus("xanh");
					} else if (phanTram > 5 && phanTram <= 10) {
						plants.setStatus("vàng");
					} else if (phanTram < 10) {
						plants.setStatus("xám");
					} else {
						plants.setStatus("đỏ");
					}
				}

				pList.add(plants);

				List<JSonPlantForm> chartData = chartData(deviceList, scheduleList, schema);

				Map<String, Object> result = new HashMap<>();

				result.put("chartData", chartData);
				result.put("Plants", pList);

				return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
			}
			if (StringUtils.equals(typeScrop, "ProjectId")) {
				deviceList = new ArrayList<>();
				Map<String, String> conditionProject = new HashMap<String, String>();
				conditionProject.put("projectId", idSchedule);
				Project project = projectService.getProject(conditionProject);
				Plant plants = new Plant();

				plants.setName(project.getProjectName());

				Map<String, String> condition = new HashMap<String, String>();
				condition.put("projectId", String.valueOf(project.getProjectId()));
				//// condition.put("systemtypeId", "2");

				List<Device> devices = new ArrayList<Device>();

				devices = deviceService.getDeviceByProjectId(condition);
				deviceList.addAll(devices);
				Double sumAcPower = null;
				if (devices.size() > 0) {
					sumAcPower = (double) Math.round(
							((devices.stream().mapToDouble(x -> x.getAcPower() == null ? 0 : x.getAcPower()).sum())
									/ 1000) * 1000)
							/ 1000;
				}

				if (sumAcPower != null) {
					plants.setCongSuatLapDat(sumAcPower);
				}

				Map<String, String> conditionSchedule = new HashMap<String, String>();
				Calendar calendar = Calendar.getInstance();
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				String timeViews = formatter.format(calendar.getTime());
				String timeView = timeViews.split(" ")[0];
				String hourMiniView = timeViews.split(" ")[1];
				conditionSchedule.put("hourMiniView", hourMiniView);
				conditionSchedule.put("stt", String.valueOf(project.getProjectId()));
				conditionSchedule.put("typeScrop", "3");
				conditionSchedule.put("deleteFlag", "0");
				conditionSchedule.put("date", timeView);
				List<Schedule> scheduleList = scheduleMapper.getSchedules(conditionSchedule);

				Schedule schedule = new Schedule();
				SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
				SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
				for (int j = 0; j < scheduleList.size(); j++) {
					Date date1 = sdf.parse(dateFormat.format(scheduleList.get(j).getTimeView()) + " "
							+ scheduleList.get(j).getToTime());
					long millisToTime1 = date1.getTime();
					if (schedule.getToTime() != null) {
						Date date2 = sdf.parse(dateFormat.format(schedule.getTimeView()) + " " + schedule.getToTime());
						long millisToTime2 = date2.getTime();
						if (millisToTime1 > millisToTime2) {
							schedule = scheduleList.get(j);
						}
					} else {
						schedule = scheduleList.get(j);
					}
				}
				if (schedule.getTimeView() != null) {
					plants.setFromDateTime(dateFormat.format(schedule.getTimeView()) + " " + schedule.getFromTime());
					plants.setToDateTime(dateFormat.format(schedule.getTimeView()) + " " + schedule.getToTime());
				}

				if (schedule != null && schedule.getCongSuatTietGiam() != null) {
					double congSuatTietGiam = ((double) schedule.getCongSuatTietGiam()) / 1000000;
					plants.setCongSuatTietGiam((double) Math.round(congSuatTietGiam * 1000) / 1000);
				}

				List<String> deviceIds = new ArrayList<String>();
				for (int j = 0; j < devices.size(); j++) {
					Device device = devices.get(j);
					String deviceId = String.valueOf(device.getDeviceId());

					deviceIds.add(deviceId);
				}
				Map<String, Object> conditionCode = new HashMap<String, Object>();
				conditionCode.put("deviceIds", deviceIds);
				conditionCode.put("schema", schema);
				List<DataInverter1EVN> dataInverter1s = new ArrayList<DataInverter1EVN>();
				Calendar cal = Calendar.getInstance();
				Timestamp timestamp = new Timestamp(cal.getTimeInMillis());
				long dateTimeNow = timestamp.getTime();
				if (deviceIds.size() > 0) {
					List<DataInverter1EVN> dataInverter1List = dataInverter1Service
							.getDataInverter1ByDeviceIds(conditionCode);
					for (int i = 0; i < dataInverter1List.size(); i++) {
						DataInverter1EVN dataInverter1 = dataInverter1List.get(i);
						if (dataInverter1.getW() != null) {
							DataInverter1EVN inverter1 = new DataInverter1EVN();
							long ms = dataInverter1.getSentDate().getTime();
							if ((ms + 5 * 60 * 1000) < dateTimeNow) {
								inverter1.setW(0);
							} else {
								inverter1.setW(dataInverter1.getW());
							}
							dataInverter1s.add(inverter1);
						}

					}

				}
				Double sumW = null;
				if (dataInverter1s.size() > 0) {
					double w = (dataInverter1s.stream().mapToDouble(x -> x.getW() == null ? 0 : x.getW()).sum()) / 1000;
					if (sumW == null) {
						sumW = w;
					} else {
						sumW = sumW + w;
					}

				}

				if (sumW != null) {
					plants.setCongSuatHienTai((double) Math.round(sumW * 1000) / 1000);
				}

				if (plants.getCongSuatLapDat() != null) {
					if (plants.getCongSuatTietGiam() == null) {
						Double congSuatChoPhepPhat = (plants.getCongSuatLapDat());
						plants.setCongSuatChoPhepPhat((double) Math.round(congSuatChoPhepPhat * 1000) / 1000);
					} else {
						Double congSuatChoPhepPhat = (plants.getCongSuatLapDat() - plants.getCongSuatTietGiam());
						plants.setCongSuatChoPhepPhat((double) Math.round(congSuatChoPhepPhat * 1000) / 1000);
					}

				}

				if (plants.getCongSuatChoPhepPhat() != null && plants.getCongSuatHienTai() != null) {
					Integer phanTram = (int) (plants.getCongSuatHienTai() / plants.getCongSuatChoPhepPhat()) * 100
							- 100;
					if (phanTram == 5 || phanTram == -5) {
						plants.setStatus("xanh");
					} else if (phanTram > 5 && phanTram <= 10) {
						plants.setStatus("vàng");
					} else if (phanTram < 10) {
						plants.setStatus("xám");
					} else {
						plants.setStatus("đỏ");
					}
				}

				pList.add(plants);

				List<JSonPlantForm> chartData = chartData(deviceList, scheduleList, schema);

				Map<String, Object> result = new HashMap<>();

				result.put("chartData", chartData);
				result.put("Plants", pList);

				return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
			}

		} else {
			if (StringUtils.equals(typeScrop, "0")) {
				Map<String, String> conditionSuper = new HashMap<String, String>();
				List<SuperManager> superManagers = superManagerService.getSuperManagers(conditionSuper);
				List<Plant> pList = new ArrayList<Plant>();
				List<Schedule> schedules = new ArrayList<Schedule>();
				deviceList = new ArrayList<Device>();

				List<String> superManagerIds = new ArrayList<>();

				if (superManagers.size() > 0) {
					for (SuperManager superManager : superManagers) {
						String sPMI = String.valueOf(superManager.getSuperManagerId());
						superManagerIds.add(sPMI);
					}
				}

				Map<String, List<Device>> caCheMapDevices = caCheDevices(superManagerIds, typeScrop);
				Map<String, List<Schedule>> caCheMapSchedule = caCheSchedules(superManagerIds, typeScrop, null, null);
				Map<String, List<DataInverter1EVN>> caCheMapDataInverter1 = caCheData(superManagerIds, typeScrop, schema);

				for (int i = 0; i < superManagers.size(); i++) {
					SuperManager superManager = superManagers.get(i);
					Plant plants = new Plant();

					plants.setName(superManager.getSuperManagerName());
					//// condition.put("systemtypeId", "2");

					List<Device> devices = caCheMapDevices.get(String.valueOf(superManager.getSuperManagerId()));
					deviceList.addAll(devices);
					Double sumAcPower = null;
					if (devices.size() > 0) {
						sumAcPower = (double) Math.round(
								((devices.stream().mapToDouble(x -> x.getAcPower() == null ? 0 : x.getAcPower()).sum())
										/ 1000000) * 1000)
								/ 1000;
					}

					if (sumAcPower != null) {
						plants.setCongSuatLapDat(sumAcPower);
					}

					List<Schedule> scheduleList = caCheMapSchedule
							.get(String.valueOf(superManager.getSuperManagerId()));
					schedules.addAll(scheduleList);

					Schedule schedule = new Schedule();
					SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
					SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
					for (int j = 0; j < scheduleList.size(); j++) {
						Date date1 = sdf.parse(dateFormat.format(scheduleList.get(j).getTimeView()) + " "
								+ scheduleList.get(j).getToTime());
						long millisToTime1 = date1.getTime();
						if (schedule.getToTime() != null) {
							Date date2 = sdf
									.parse(dateFormat.format(schedule.getTimeView()) + " " + schedule.getToTime());
							long millisToTime2 = date2.getTime();
							if (millisToTime1 > millisToTime2) {
								schedule = scheduleList.get(j);
							}
						} else {
							schedule = scheduleList.get(j);
						}
					}

					if (schedule != null && schedule.getCongSuatTietGiam() != null) {
						double congSuatTietGiam = ((double) schedule.getCongSuatTietGiam()) / 1000000;
						plants.setCongSuatTietGiam((double) Math.round(congSuatTietGiam * 1000) / 1000);
					}

					if (schedule.getTimeView() != null) {
						plants.setFromDateTime(
								dateFormat.format(schedule.getTimeView()) + " " + schedule.getFromTime());
						plants.setToDateTime(dateFormat.format(schedule.getTimeView()) + " " + schedule.getToTime());
					}

					List<String> deviceIds = new ArrayList<String>();
					for (int j = 0; j < devices.size(); j++) {
						Device device = devices.get(j);
						String deviceId = String.valueOf(device.getDeviceId());

						deviceIds.add(deviceId);
					}
					List<DataInverter1EVN> dataInverter1s = new ArrayList<DataInverter1EVN>();
					Calendar cal = Calendar.getInstance();
					Timestamp timestamp = new Timestamp(cal.getTimeInMillis());
					long dateTimeNow = timestamp.getTime();
					if (deviceIds.size() > 0) {
						List<DataInverter1EVN> dataList = caCheMapDataInverter1
								.get(String.valueOf(superManager.getSuperManagerId()));
						for (int j = 0; j < dataList.size(); j++) {
							DataInverter1EVN dataInverter1 = dataList.get(j);
							if (dataInverter1.getW() != null) {
								DataInverter1EVN inverter1 = new DataInverter1EVN();
								long ms = dataInverter1.getSentDate().getTime();
								if ((ms + 5 * 60 * 1000) < dateTimeNow) {
									inverter1.setW(0);
								} else {
									inverter1.setW(dataInverter1.getW());
								}
								dataInverter1s.add(inverter1);
							}

						}

					}
					Double sumW = null;
					if (dataInverter1s.size() > 0) {
						double w = (dataInverter1s.stream().mapToDouble(x -> x.getW() == null ? 0 : x.getW()).sum())
								/ 1000000;
						if (sumW == null) {
							sumW = w;
						} else {
							sumW = sumW + w;
						}

					}

					if (sumW != null) {
						plants.setCongSuatHienTai((double) Math.round(sumW * 1000) / 1000);
					}

					if (plants.getCongSuatLapDat() != null) {
						if (plants.getCongSuatTietGiam() == null) {
							Double congSuatChoPhepPhat = (plants.getCongSuatLapDat());
							plants.setCongSuatChoPhepPhat((double) Math.round(congSuatChoPhepPhat * 1000) / 1000);
						} else {
							Double congSuatChoPhepPhat = (plants.getCongSuatLapDat() - plants.getCongSuatTietGiam());
							plants.setCongSuatChoPhepPhat((double) Math.round(congSuatChoPhepPhat * 1000) / 1000);
						}

					}

					if (plants.getCongSuatChoPhepPhat() != null && plants.getCongSuatHienTai() != null) {
						Double phanTram = (plants.getCongSuatHienTai() / plants.getCongSuatChoPhepPhat()) * 100 - 100;
						if (phanTram == 5 || phanTram == -5) {
							plants.setStatus("xanh");
						} else if (phanTram > 5 && phanTram <= 10) {
							plants.setStatus("vàng");
						} else if (phanTram < 10) {
							plants.setStatus("xám");
						} else {
							plants.setStatus("đỏ");
						}
					}

					pList.add(plants);
				}

				List<JSonPlantForm> chartData = chartData(deviceList, schedules, schema);

				Map<String, Object> result = new HashMap<>();

				result.put("chartData", chartData);
				result.put("Plants", pList);

				return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);

			} else if (StringUtils.equals(typeScrop, "1")) {
				Map<String, String> conditionManager = new HashMap<String, String>();
				conditionManager.put("superManagerId", idSchedule);
				List<Manager> managers = managerService.getManagers(conditionManager);
				deviceList = new ArrayList<>();
				List<String> managerIds = new ArrayList<>();

				if (managers.size() > 0) {
					for (Manager manager : managers) {
						String managerId = String.valueOf(manager.getManagerId());
						managerIds.add(managerId);
					}
				}

				Map<String, List<Device>> caCheMapDevices = caCheDevices(managerIds, typeScrop);
				Map<String, List<Schedule>> caCheMapSchedule = caCheSchedules(managerIds, typeScrop, null, null);
				Map<String, List<DataInverter1EVN>> caCheMapDataInverter1 = caCheData(managerIds, typeScrop, schema);

				List<Plant> pList = new ArrayList<Plant>();
				List<Schedule> schedules = new ArrayList<Schedule>();
				for (int i = 0; i < managers.size(); i++) {
					Manager manager = managers.get(i);
					Plant plants = new Plant();

					plants.setName(manager.getManagerName());

					// condition.put("systemtypeId", "2");

					List<Device> devices = caCheMapDevices.get(String.valueOf(manager.getManagerId()));
					deviceList.addAll(devices);
					Double sumAcPower = null;
					if (devices.size() > 0) {
						sumAcPower = (double) Math.round(
								((devices.stream().mapToDouble(x -> x.getAcPower() == null ? 0 : x.getAcPower()).sum())
										/ 1000000) * 1000)
								/ 1000;
					}

					if (sumAcPower != null) {
						plants.setCongSuatLapDat(sumAcPower);
					}

					List<Schedule> scheduleList = caCheMapSchedule.get(String.valueOf(manager.getManagerId()));
					schedules.addAll(scheduleList);
					Schedule schedule = new Schedule();
					SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
					SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
					for (int j = 0; j < scheduleList.size(); j++) {
						Date date1 = sdf.parse(dateFormat.format(scheduleList.get(j).getTimeView()) + " "
								+ scheduleList.get(j).getToTime());
						long millisToTime1 = date1.getTime();
						if (schedule.getToTime() != null) {
							Date date2 = sdf
									.parse(dateFormat.format(schedule.getTimeView()) + " " + schedule.getToTime());
							long millisToTime2 = date2.getTime();
							if (millisToTime1 > millisToTime2) {
								schedule = scheduleList.get(j);
							}
						} else {
							schedule = scheduleList.get(j);
						}
					}

					if (schedule != null && schedule.getCongSuatTietGiam() != null) {
						double congSuatTietGiam = ((double) schedule.getCongSuatTietGiam()) / 1000000;
						plants.setCongSuatTietGiam((double) Math.round(congSuatTietGiam * 1000) / 1000);
					}
					if (schedule.getTimeView() != null) {
						plants.setFromDateTime(
								dateFormat.format(schedule.getTimeView()) + " " + schedule.getFromTime());
						plants.setToDateTime(dateFormat.format(schedule.getTimeView()) + " " + schedule.getToTime());
					}

					List<String> deviceIds = new ArrayList<String>();
					for (int j = 0; j < devices.size(); j++) {
						Device device = devices.get(j);
						String deviceId = String.valueOf(device.getDeviceId());

						deviceIds.add(deviceId);
					}
					Map<String, List<String>> conditionCode = new HashMap<String, List<String>>();
					conditionCode.put("deviceIds", deviceIds);
					List<DataInverter1EVN> dataInverter1s = new ArrayList<DataInverter1EVN>();
					Calendar cal = Calendar.getInstance();
					Timestamp timestamp = new Timestamp(cal.getTimeInMillis());
					long dateTimeNow = timestamp.getTime();
					if (deviceIds.size() > 0) {
						List<DataInverter1EVN> dataInverter1List = caCheMapDataInverter1
								.get(String.valueOf(manager.getManagerId()));
						for (int j = 0; j < dataInverter1List.size(); j++) {
							DataInverter1EVN dataInverter1 = dataInverter1List.get(j);
							if (dataInverter1.getW() != null) {
								DataInverter1EVN inverter1 = new DataInverter1EVN();
								long ms = dataInverter1.getSentDate().getTime();
								if ((ms + 5 * 60 * 1000) < dateTimeNow) {
									inverter1.setW(0);
								} else {
									inverter1.setW(dataInverter1.getW());
								}
								dataInverter1s.add(inverter1);
							}

						}

					}
					Double sumW = null;
					if (dataInverter1s.size() > 0) {
						double w = (dataInverter1s.stream().mapToDouble(x -> x.getW() == null ? 0 : x.getW()).sum())
								/ 1000000;
						if (sumW == null) {
							sumW = w;
						} else {
							sumW = sumW + w;
						}

					}

					if (sumW != null) {
						plants.setCongSuatHienTai((double) Math.round(sumW * 1000) / 1000);
					}

					if (plants.getCongSuatLapDat() != null) {
						if (plants.getCongSuatTietGiam() == null) {
							Double congSuatChoPhepPhat = (plants.getCongSuatLapDat());
							plants.setCongSuatChoPhepPhat((double) Math.round(congSuatChoPhepPhat * 1000) / 1000);
						} else {
							Double congSuatChoPhepPhat = (plants.getCongSuatLapDat() - plants.getCongSuatTietGiam());
							plants.setCongSuatChoPhepPhat((double) Math.round(congSuatChoPhepPhat * 1000) / 1000);
						}

					}

					if (plants.getCongSuatChoPhepPhat() != null && plants.getCongSuatHienTai() != null) {
						Integer phanTram = (int) (plants.getCongSuatHienTai() / plants.getCongSuatChoPhepPhat()) * 100
								- 100;
						if (phanTram == 5 || phanTram == -5) {
							plants.setStatus("xanh");
						} else if (phanTram > 5 && phanTram <= 10) {
							plants.setStatus("vàng");
						} else if (phanTram < 10) {
							plants.setStatus("xám");
						} else {
							plants.setStatus("đỏ");
						}
					}

					pList.add(plants);
				}

				List<JSonPlantForm> chartData = chartData(deviceList, schedules, schema);

				Map<String, Object> result = new HashMap<>();

				result.put("chartData", chartData);
				result.put("Plants", pList);

				return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);

			} else if (StringUtils.equals(typeScrop, "2")) {
				Map<String, String> conditionArea = new HashMap<String, String>();
				conditionArea.put("managerId", idSchedule);
				deviceList = new ArrayList<>();
				List<Area> ares = areaService.getAreas(conditionArea);
				List<String> areaIds = new ArrayList<>();

				if (ares.size() > 0) {
					for (Area area : ares) {
						String areaId = String.valueOf(area.getManagerId());
						areaIds.add(areaId);
					}
				}

				Map<String, List<Device>> caCheMapDevices = caCheDevices(areaIds, typeScrop);
				Map<String, List<Schedule>> caCheMapSchedule = caCheSchedules(areaIds, typeScrop, null, null);
				Map<String, List<DataInverter1EVN>> caCheMapDataInverter1 = caCheData(areaIds, typeScrop, schema);

				List<Plant> pList = new ArrayList<Plant>();
				List<Schedule> schedules = new ArrayList<Schedule>();
				for (int i = 0; i < ares.size(); i++) {
					Area area = ares.get(i);
					Plant plants = new Plant();

					plants.setName(area.getAreaName());

					//// condition.put("systemtypeId", "2");

					List<Device> devices = new ArrayList<Device>();

					devices = caCheMapDevices.get(String.valueOf(area.getAreaId()));
					deviceList.addAll(devices);
					Double sumAcPower = null;
					if (devices.size() > 0) {
						sumAcPower = (double) Math.round(
								((devices.stream().mapToDouble(x -> x.getAcPower() == null ? 0 : x.getAcPower()).sum())
										/ 1000000) * 1000)
								/ 1000;
					}

					if (sumAcPower != null) {
						plants.setCongSuatLapDat(sumAcPower);
					}

					List<Schedule> scheduleList = caCheMapSchedule.get(String.valueOf(area.getAreaId()));
					schedules.addAll(scheduleList);
					Schedule schedule = new Schedule();
					SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
					SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
					for (int j = 0; j < scheduleList.size(); j++) {
						Date date1 = sdf.parse(dateFormat.format(scheduleList.get(j).getTimeView()) + " "
								+ scheduleList.get(j).getToTime());
						long millisToTime1 = date1.getTime();
						if (schedule.getToTime() != null) {
							Date date2 = sdf
									.parse(dateFormat.format(schedule.getTimeView()) + " " + schedule.getToTime());
							long millisToTime2 = date2.getTime();
							if (millisToTime1 > millisToTime2) {
								schedule = scheduleList.get(j);
							}
						} else {
							schedule = scheduleList.get(j);
						}
					}

					if (schedule != null && schedule.getCongSuatTietGiam() != null) {
						double congSuatTietGiam = ((double) schedule.getCongSuatTietGiam()) / 1000000;
						plants.setCongSuatTietGiam((double) Math.round(congSuatTietGiam * 1000) / 1000);
					}

					if (schedule.getTimeView() != null) {
						plants.setFromDateTime(
								dateFormat.format(schedule.getTimeView()) + " " + schedule.getFromTime());
						plants.setToDateTime(dateFormat.format(schedule.getTimeView()) + " " + schedule.getToTime());
					}

					List<String> deviceIds = new ArrayList<String>();
					for (int j = 0; j < devices.size(); j++) {
						Device device = devices.get(j);
						String deviceId = String.valueOf(device.getDeviceId());

						deviceIds.add(deviceId);
					}
					Map<String, List<String>> conditionCode = new HashMap<String, List<String>>();
					conditionCode.put("deviceIds", deviceIds);
					List<DataInverter1EVN> dataInverter1s = new ArrayList<DataInverter1EVN>();
					Calendar cal = Calendar.getInstance();
					Timestamp timestamp = new Timestamp(cal.getTimeInMillis());
					long dateTimeNow = timestamp.getTime();
					if (deviceIds.size() > 0) {
						List<DataInverter1EVN> dataInverter1List = caCheMapDataInverter1
								.get(String.valueOf(area.getAreaId()));
						for (int j = 0; j < dataInverter1List.size(); j++) {
							DataInverter1EVN dataInverter1 = dataInverter1List.get(j);
							if (dataInverter1.getW() != null) {
								DataInverter1EVN inverter1 = new DataInverter1EVN();
								long ms = dataInverter1.getSentDate().getTime();
								if ((ms + 5 * 60 * 1000) < dateTimeNow) {
									inverter1.setW(0);
								} else {
									inverter1.setW(dataInverter1.getW());
								}
								dataInverter1s.add(inverter1);
							}

						}

					}
					Double sumW = null;
					if (dataInverter1s.size() > 0) {
						double w = (dataInverter1s.stream().mapToDouble(x -> x.getW() == null ? 0 : x.getW()).sum())
								/ 1000000;
						if (sumW == null) {
							sumW = w;
						} else {
							sumW = sumW + w;
						}

					}

					if (sumW != null) {
						plants.setCongSuatHienTai((double) Math.round(sumW * 1000) / 1000);
					}

					if (plants.getCongSuatLapDat() != null) {
						if (plants.getCongSuatTietGiam() == null) {
							Double congSuatChoPhepPhat = (plants.getCongSuatLapDat());
							plants.setCongSuatChoPhepPhat((double) Math.round(congSuatChoPhepPhat * 1000) / 1000);
						} else {
							Double congSuatChoPhepPhat = (plants.getCongSuatLapDat() - plants.getCongSuatTietGiam());
							plants.setCongSuatChoPhepPhat((double) Math.round(congSuatChoPhepPhat * 1000) / 1000);
						}

					}

					if (plants.getCongSuatChoPhepPhat() != null && plants.getCongSuatHienTai() != null) {
						Integer phanTram = (int) (plants.getCongSuatHienTai() / plants.getCongSuatChoPhepPhat()) * 100
								- 100;
						if (phanTram == 5 || phanTram == -5) {
							plants.setStatus("xanh");
						} else if (phanTram > 5 && phanTram <= 10) {
							plants.setStatus("vàng");
						} else if (phanTram < 10) {
							plants.setStatus("xám");
						} else {
							plants.setStatus("đỏ");
						}
					}

					pList.add(plants);
				}

				List<JSonPlantForm> chartData = chartData(deviceList, schedules, schema);

				Map<String, Object> result = new HashMap<>();

				result.put("chartData", chartData);
				result.put("Plants", pList);

				return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
			} else if (StringUtils.equals(typeScrop, "3")) {
				Map<String, String> conditionProject = new HashMap<String, String>();
				conditionProject.put("areaId", idSchedule);
				deviceList = new ArrayList<>();
				List<Project> projects = projectService.getProjectList(conditionProject);

				List<String> projectIds = new ArrayList<>();

				if (projects.size() > 0) {
					for (Project project : projects) {
						String projectId = String.valueOf(project.getProjectId());
						projectIds.add(projectId);
					}
				}

				Map<String, List<Device>> caCheMapDevices = caCheDevices(projectIds, typeScrop);
				Map<String, List<Schedule>> caCheMapSchedule = caCheSchedules(projectIds, typeScrop, null, null);
				Map<String, List<DataInverter1EVN>> caCheMapDataInverter1 = caCheData(projectIds, typeScrop, schema);

				List<Plant> pList = new ArrayList<Plant>();
				List<Schedule> schedules = new ArrayList<Schedule>();
				for (int i = 0; i < projects.size(); i++) {
					Project project = projects.get(i);
					Plant plants = new Plant();

					plants.setName(project.getProjectName());

					// condition.put("systemtypeId", "2");

					List<Device> devices = new ArrayList<Device>();

					devices = caCheMapDevices.get(String.valueOf(project.getProjectId()));
					deviceList.addAll(devices);
					Double sumAcPower = null;
					if (devices.size() > 0) {
						sumAcPower = (double) Math.round(
								((devices.stream().mapToDouble(x -> x.getAcPower() == null ? 0 : x.getAcPower()).sum())
										/ 1000000) * 1000)
								/ 1000;
					}

					if (sumAcPower != null) {
						plants.setCongSuatLapDat(sumAcPower);
					}

					Map<String, String> conditionSchedule = new HashMap<String, String>();
					List<Schedule> scheduleList = caCheMapSchedule.get(String.valueOf(project.getProjectId()));
					schedules.addAll(scheduleList);
					Schedule schedule = new Schedule();
					SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
					SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
					for (int j = 0; j < scheduleList.size(); j++) {
						Date date1 = sdf.parse(dateFormat.format(scheduleList.get(j).getTimeView()) + " "
								+ scheduleList.get(j).getToTime());
						long millisToTime1 = date1.getTime();
						if (schedule.getToTime() != null) {
							Date date2 = sdf
									.parse(dateFormat.format(schedule.getTimeView()) + " " + schedule.getToTime());
							long millisToTime2 = date2.getTime();
							if (millisToTime1 > millisToTime2) {
								schedule = scheduleList.get(j);
							}
						} else {
							schedule = scheduleList.get(j);
						}
					}

					if (schedule != null && schedule.getCongSuatTietGiam() != null) {
						double congSuatTietGiam = ((double) schedule.getCongSuatTietGiam()) / 1000000;
						plants.setCongSuatTietGiam((double) Math.round(congSuatTietGiam * 1000) / 1000);
					}

					if (schedule.getTimeView() != null) {
						plants.setFromDateTime(
								dateFormat.format(schedule.getTimeView()) + " " + schedule.getFromTime());
						plants.setToDateTime(dateFormat.format(schedule.getTimeView()) + " " + schedule.getToTime());
					}

					List<String> deviceIds = new ArrayList<String>();
					for (int j = 0; j < devices.size(); j++) {
						Device device = devices.get(j);
						String deviceId = String.valueOf(device.getDeviceId());

						deviceIds.add(deviceId);
					}
					List<DataInverter1EVN> dataInverter1s = new ArrayList<DataInverter1EVN>();
					Calendar cal = Calendar.getInstance();
					Timestamp timestamp = new Timestamp(cal.getTimeInMillis());
					long dateTimeNow = timestamp.getTime();
					if (deviceIds.size() > 0) {
						List<DataInverter1EVN> dataInverter1List = caCheMapDataInverter1
								.get(String.valueOf(project.getProjectId()));
						for (int j = 0; j < dataInverter1List.size(); j++) {
							DataInverter1EVN dataInverter1 = dataInverter1List.get(j);
							if (dataInverter1.getW() != null) {
								DataInverter1EVN inverter1 = new DataInverter1EVN();
								long ms = dataInverter1.getSentDate().getTime();
								if ((ms + 5 * 60 * 1000) < dateTimeNow) {
									inverter1.setW(0);
								} else {
									inverter1.setW(dataInverter1.getW());
								}
								dataInverter1s.add(inverter1);
							}

						}

					}
					Double sumW = null;
					if (dataInverter1s.size() > 0) {
						Double w = (dataInverter1s.stream().mapToDouble(x -> x.getW() == null ? 0 : x.getW()).sum())
								/ 1000000;
						if (sumW == null) {
							sumW = w;
						} else {
							sumW = sumW + w;
						}

					}

					if (sumW != null) {
						plants.setCongSuatHienTai((double) Math.round(sumW * 1000) / 1000);
					}

					if (plants.getCongSuatLapDat() != null) {
						if (plants.getCongSuatTietGiam() == null) {
							Double congSuatChoPhepPhat = (plants.getCongSuatLapDat());
							plants.setCongSuatChoPhepPhat((double) Math.round(congSuatChoPhepPhat * 1000) / 1000);
						} else {
							Double congSuatChoPhepPhat = (plants.getCongSuatLapDat() - plants.getCongSuatTietGiam());
							plants.setCongSuatChoPhepPhat((double) Math.round(congSuatChoPhepPhat * 1000) / 1000);
						}

					}

					if (plants.getCongSuatChoPhepPhat() != null && plants.getCongSuatHienTai() != null) {
						Integer phanTram = (int) (plants.getCongSuatHienTai() / plants.getCongSuatChoPhepPhat()) * 100
								- 100;
						if (phanTram == 5 || phanTram == -5) {
							plants.setStatus("xanh");
						} else if (phanTram > 5 && phanTram <= 10) {
							plants.setStatus("vàng");
						} else if (phanTram < 10) {
							plants.setStatus("xám");
						} else {
							plants.setStatus("đỏ");
						}
					}

					pList.add(plants);
				}

				List<JSonPlantForm> chartData = chartData(deviceList, schedules, schema);

				Map<String, Object> result = new HashMap<>();

				result.put("chartData", chartData);
				result.put("Plants", pList);

				return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
			}

		}

		return null;
	}

	public Map<String, List<Device>> caCheDevices(List<String> ids, String typeScrop) {
		Map<String, List<Device>> caCheMapDevices = new HashMap<>();
		List<Device> deviceList = new ArrayList<>();
		Map<String, Object> condition = new HashMap<>();
		if (StringUtils.equals(typeScrop, "0")) {
			condition.put("superManagerIds", ids);
			deviceList = deviceService.getDeviceBySuperManagerIds(condition);
		} else if (StringUtils.equals(typeScrop, "1")) {
			condition.put("managerIds", ids);
			deviceList = deviceService.getDeviceBySuperManagerIds(condition);
		} else if (StringUtils.equals(typeScrop, "2")) {
			condition.put("areaIds", ids);
			deviceList = deviceService.getDeviceByAreaIds(condition);
		} else if (StringUtils.equals(typeScrop, "3")) {
			condition.put("projectIds", ids);
			deviceList = deviceService.getDeviceByProjectIds(condition);
		}

		for (int i = 0; i < ids.size(); i++) {
			List<Device> dvList = new ArrayList<>();
			for (int j = 0; j < deviceList.size(); j++) {
				String spManagerId = String.valueOf(deviceList.get(j).getSuperManagerId());
				if (StringUtils.equals(spManagerId, ids.get(i))) {
					dvList.add(deviceList.get(j));
				}
			}
			caCheMapDevices.put(ids.get(i), dvList);
		}

		return caCheMapDevices;

	}

	public Map<String, List<Schedule>> caCheSchedules(List<String> ids, String typeScrop, String fromDate,
			String toDate) {
		Map<String, List<Schedule>> caCheMapSchedules = new HashMap<>();

		Map<String, Object> conditionSchedule2 = new HashMap<String, Object>();
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String timeViews = formatter.format(calendar.getTime());
		String timeView = timeViews.split(" ")[0];
		String hourMiniView = timeViews.split(" ")[1];
		conditionSchedule2.put("hourMiniView", hourMiniView);
		conditionSchedule2.put("stts", ids);
		conditionSchedule2.put("typeScrop", typeScrop);
		conditionSchedule2.put("deleteFlag", "0");
		conditionSchedule2.put("date", timeView);
		conditionSchedule2.put("fromDate", fromDate);
		conditionSchedule2.put("toDate", toDate);
		List<Schedule> scheduleListBySuperManagerIds = scheduleMapper.getSchedulesPlants(conditionSchedule2);

		for (int i = 0; i < ids.size(); i++) {
			List<Schedule> scheduleList = new ArrayList<>();
			for (int j = 0; j < scheduleListBySuperManagerIds.size(); j++) {
				String spManagerId = String.valueOf(scheduleListBySuperManagerIds.get(j).getStt());
				if (StringUtils.equals(spManagerId, ids.get(i))) {
					scheduleList.add(scheduleListBySuperManagerIds.get(j));
				}

			}
			caCheMapSchedules.put(ids.get(i), scheduleList);
		}

		return caCheMapSchedules;

	}

	public Map<String, List<DataInverter1EVN>> caCheData(List<String> ids, String typeScrop, String schema) {
		Map<String, List<DataInverter1EVN>> caCheMapDataInverter1s = new HashMap<>();
		Map<String, Object> conditionCode = new HashMap<String, Object>();
		if (StringUtils.equals(typeScrop, "0")) {
			conditionCode.put("superManagerIds", ids);
		} else if (StringUtils.equals(typeScrop, "1")) {
			conditionCode.put("managerIds", ids);
		} else if (StringUtils.equals(typeScrop, "2")) {
			conditionCode.put("areaIds", ids);
		} else if (StringUtils.equals(typeScrop, "3")) {
			conditionCode.put("projectIds", ids);
		}

		conditionCode.put("schema", schema);

		List<DataInverter1EVN> dataInverter1List = dataInverter1Service.getDataInverter1ByDeviceIds(conditionCode);

		for (int i = 0; i < ids.size(); i++) {
			List<DataInverter1EVN> data = new ArrayList<>();
			for (int j = 0; j < dataInverter1List.size(); j++) {
				String spManagerId = String.valueOf(dataInverter1List.get(j).getSuperManagerId());
				if (StringUtils.equals(spManagerId, ids.get(i))) {
					data.add(dataInverter1List.get(j));
				}
			}
			caCheMapDataInverter1s.put(ids.get(i), data);
		}

		return caCheMapDataInverter1s;

	}

	@PostMapping(value = "/search")
	public ResponseEntity<List<JSonPlantForm>> search(@RequestBody PlantSend data) {
		List<Schedule> schedules = new ArrayList<Schedule>();
		String userName = data.getUserName();
		String typeScropTree = data.getTypeScropTree();
		String fromDate = data.getFromDate();
		String toDate = data.getToDate();

		String typeScrop = null;

		User user = userService.getUserByUsername(userName);
		Integer customerId = user.getCustomerId();
		String schema = Schema.getSchemas(customerId);
		String idSchedule = null;
		if (StringUtils.equals(String.valueOf(user.getUserType()), "3")) {
			typeScrop = "0";
			globalTupeScope = typeScrop;
			idSchedule = String.valueOf(user.getId());
		} else if (StringUtils.equals(String.valueOf(user.getUserType()), "4")) {
			typeScrop = "1";
			globalTupeScope = typeScrop;
			idSchedule = String.valueOf(user.getTargetId());
		} else if (StringUtils.equals(String.valueOf(user.getUserType()), "5")) {
			typeScrop = "2";
			globalTupeScope = typeScrop;
			idSchedule = String.valueOf(user.getTargetId());
		} else if (StringUtils.equals(String.valueOf(user.getUserType()), "6")) {
			typeScrop = "3";
			globalTupeScope = typeScrop;
			idSchedule = String.valueOf(user.getTargetId());
		}

		if (typeScropTree.length() > 0) {
			String[] typeScropTrees = typeScropTree.split("=");
			idSchedule = typeScropTrees[1];
			Map<String, String> conditionSchedule = new HashMap<String, String>();
			Calendar calendar = Calendar.getInstance();
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			String timeViews = formatter.format(calendar.getTime());
			String timeView = timeViews.split(" ")[0];
			String hourMiniView = timeViews.split(" ")[1];
			if (StringUtils.equals(typeScrop, "0")) {
				conditionSchedule.put("hourMiniView", hourMiniView);
				conditionSchedule.put("stts", idSchedule);
				conditionSchedule.put("typeScrop", typeScrop);
				conditionSchedule.put("deleteFlag", "0");
				conditionSchedule.put("date", timeView);
				conditionSchedule.put("fromDate", fromDate);
				conditionSchedule.put("toDate", toDate);
				schedules = scheduleMapper.getSchedules(conditionSchedule);
			} else if (StringUtils.equals(typeScrop, "1")) {
				conditionSchedule.put("hourMiniView", hourMiniView);
				conditionSchedule.put("stts", idSchedule);
				conditionSchedule.put("typeScrop", typeScrop);
				conditionSchedule.put("deleteFlag", "0");
				conditionSchedule.put("date", timeView);
				conditionSchedule.put("fromDate", fromDate);
				conditionSchedule.put("toDate", toDate);
				schedules = scheduleMapper.getSchedules(conditionSchedule);
			} else if (StringUtils.equals(typeScrop, "2")) {
				conditionSchedule.put("hourMiniView", hourMiniView);
				conditionSchedule.put("stts", idSchedule);
				conditionSchedule.put("typeScrop", typeScrop);
				conditionSchedule.put("deleteFlag", "0");
				conditionSchedule.put("date", timeView);
				conditionSchedule.put("fromDate", fromDate);
				conditionSchedule.put("toDate", toDate);
				schedules = scheduleMapper.getSchedules(conditionSchedule);
			} else if (StringUtils.equals(typeScrop, "3")) {
				conditionSchedule.put("hourMiniView", hourMiniView);
				conditionSchedule.put("stts", idSchedule);
				conditionSchedule.put("typeScrop", typeScrop);
				conditionSchedule.put("deleteFlag", "0");
				conditionSchedule.put("date", timeView);
				conditionSchedule.put("fromDate", fromDate);
				conditionSchedule.put("toDate", toDate);
				schedules = scheduleMapper.getSchedules(conditionSchedule);
			}
		} else {
			if (StringUtils.equals(typeScrop, "0")) {
				Map<String, String> conditionSuper = new HashMap<String, String>();
				List<SuperManager> superManagers = superManagerService.getSuperManagers(conditionSuper);
				List<String> superManagerIds = new ArrayList<>();

				if (superManagers.size() > 0) {
					for (SuperManager superManager : superManagers) {
						String sPMI = String.valueOf(superManager.getSuperManagerId());
						superManagerIds.add(sPMI);
					}
				}

				Map<String, List<Schedule>> caCheMapSchedule = caCheSchedules(superManagerIds, typeScrop, fromDate,
						toDate);

				for (int i = 0; i < superManagers.size(); i++) {
					SuperManager superManager = superManagers.get(i);
					List<Schedule> scheduleList = caCheMapSchedule
							.get(String.valueOf(superManager.getSuperManagerId()));
					schedules.addAll(scheduleList);
				}
			} else if (StringUtils.equals(typeScrop, "1")) {
				Map<String, String> condition = new HashMap<String, String>();
				condition.put("superManagerId", idSchedule);
				List<Manager> managers = managerService.getManagers(condition);
				List<String> managerIds = new ArrayList<>();

				if (managers.size() > 0) {
					for (Manager manager : managers) {
						String sPMI = String.valueOf(manager.getManagerId());
						managerIds.add(sPMI);
					}
				}

				Map<String, List<Schedule>> caCheMapSchedule = caCheSchedules(managerIds, typeScrop, fromDate, toDate);

				for (int i = 0; i < managers.size(); i++) {
					Manager manager = managers.get(i);
					List<Schedule> scheduleList = caCheMapSchedule.get(String.valueOf(manager.getManagerId()));
					schedules.addAll(scheduleList);
				}
			} else if (StringUtils.equals(typeScrop, "2")) {
				Map<String, String> condition = new HashMap<String, String>();
				condition.put("managerId", idSchedule);
				List<Area> areas = areaService.getAreas(condition);
				List<String> areaIds = new ArrayList<>();

				if (areas.size() > 0) {
					for (Area area : areas) {
						String sPMI = String.valueOf(area.getAreaId());
						areaIds.add(sPMI);
					}
				}

				Map<String, List<Schedule>> caCheMapSchedule = caCheSchedules(areaIds, typeScrop, fromDate, toDate);

				for (int i = 0; i < areas.size(); i++) {
					Area area = areas.get(i);
					List<Schedule> scheduleList = caCheMapSchedule.get(String.valueOf(area.getAreaId()));
					schedules.addAll(scheduleList);
				}
			} else if (StringUtils.equals(typeScrop, "3")) {
				Map<String, String> condition = new HashMap<String, String>();
				condition.put("areaId", idSchedule);
				List<Project> projects = projectService.getProjectList(condition);
				List<String> projectIds = new ArrayList<>();

				if (projects.size() > 0) {
					for (Project project : projects) {
						String sPMI = String.valueOf(project.getProjectId());
						projectIds.add(sPMI);
					}
				}

				Map<String, List<Schedule>> caCheMapSchedule = caCheSchedules(projectIds, typeScrop, fromDate, toDate);

				for (int i = 0; i < projects.size(); i++) {
					Project project = projects.get(i);
					List<Schedule> scheduleList = caCheMapSchedule.get(String.valueOf(project.getProjectId()));
					schedules.addAll(scheduleList);
				}
			}
		}

		List<String> deviceIds = new ArrayList<>();
		Map<String, Device> dataDevice = new HashMap<>();
		for (int j = 0; j < deviceList.size(); j++) {
			Device device = deviceList.get(j);
			String deviceId = String.valueOf(device.getDeviceId());
			deviceIds.add(deviceId);
			dataDevice.put(deviceId, device);
		}

		Map<String, Object> condition = new HashMap<>();
		condition.put("fromDate", fromDate);
		condition.put("toDate", toDate);
		condition.put("deviceIds", deviceIds);
		condition.put("schema", schema);
		List<DataInverter1EVN> dataList = dataInverter1Service.getDataInverter1s(condition);

		Map<String, List<DataInverter1EVN>> dataInverter1 = new HashMap<>();
		for (String deviceId : deviceIds) {
			List<DataInverter1EVN> dataInverter1s = new ArrayList<>();
			for (int i = 0; i < dataList.size(); i++) {
				if (StringUtils.equals(deviceId, dataList.get(i).getDeviceId())) {
					dataInverter1s.add(dataList.get(i));
				}
			}
			dataInverter1.put(deviceId, dataInverter1s);
		}

		Map<String, JSonPlantForm> deviceChartData = new LinkedMap<String, JSonPlantForm>();
		for (String key : dataInverter1.keySet()) {
			List<DataInverter1EVN> dataIvt1 = dataInverter1.get(key);
			List<JSonPlantForm> jsonDatas = getChart(fromDate, toDate, dataIvt1, schedules, dataDevice.get(key));
			for (JSonPlantForm jsonData : jsonDatas) {
				JSonPlantForm jSonPlantForm = deviceChartData.get(jsonData.getTime());
				if (jSonPlantForm == null) {
					jSonPlantForm = new JSonPlantForm();
					jSonPlantForm.setW(0.0);
					jSonPlantForm.setWh(0.0);
					jSonPlantForm.setCongSuatChoPhep(0.0);
				}
				jSonPlantForm.setW(jSonPlantForm.getW() + jsonData.getW());
				jSonPlantForm.setWh(jSonPlantForm.getWh() + jsonData.getWh());
				jSonPlantForm.setCongSuatChoPhep(jSonPlantForm.getCongSuatChoPhep() + jsonData.getCongSuatChoPhep());
				deviceChartData.put(jsonData.getTime(), jSonPlantForm);
			}
		}

		List<JSonPlantForm> jsDataList = new ArrayList<>();
		for (String key : deviceChartData.keySet()) {
			JSonPlantForm jSonPlantForm = deviceChartData.get(key);
			JSonPlantForm json = new JSonPlantForm();
			json.setTime(key);
			json.setW(jSonPlantForm.getW());
			json.setWh(jSonPlantForm.getWh());
			json.setCongSuatChoPhep(jSonPlantForm.getCongSuatChoPhep());
			jsDataList.add(json);
		}

		return new ResponseEntity<List<JSonPlantForm>>(jsDataList, HttpStatus.OK);

	}

	public List<JSonPlantForm> chartData(List<Device> deviceList, List<Schedule> schedules, String schema) throws ParseException {
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String fromDate = formatter.format(calendar.getTime());
		String toDate = formatter.format(calendar.getTime());

		List<String> deviceIds = new ArrayList<>();
		Map<String, Device> dataDevice = new HashMap<>();
		for (int j = 0; j < deviceList.size(); j++) {
			Device device = deviceList.get(j);
			String deviceId = String.valueOf(device.getDeviceId());
			deviceIds.add(deviceId);
			dataDevice.put(deviceId, device);
		}

		Map<String, Object> condition = new HashMap<>();
		condition.put("fromDate", fromDate);
		condition.put("toDate", toDate);
		if (deviceIds.size() > 0) {
			condition.put("deviceIds", deviceIds);
		}
		condition.put("schema", schema);
		List<DataInverter1EVN> dataList = dataInverter1Service.getDataInverter1s(condition);

		Map<String, List<DataInverter1EVN>> dataInverter1 = new HashMap<>();
		for (String deviceId : deviceIds) {
			List<DataInverter1EVN> dataInverter1s = new ArrayList<>();
			for (int i = 0; i < dataList.size(); i++) {
				if (StringUtils.equals(deviceId, dataList.get(i).getDeviceId())) {
					dataInverter1s.add(dataList.get(i));
				}
			}
			dataInverter1.put(deviceId, dataInverter1s);
		}

		Map<String, JSonPlantForm> deviceChartData = new LinkedHashMap<>();
		for (String key : dataInverter1.keySet()) {
			List<DataInverter1EVN> data = dataInverter1.get(key);
			List<JSonPlantForm> jsonDatas = getChart(fromDate, toDate, data, schedules, dataDevice.get(key));
			for (JSonPlantForm jsonData : jsonDatas) {
				JSonPlantForm jSonPlantForm = deviceChartData.get(jsonData.getTime());
				if (jSonPlantForm == null) {
					jSonPlantForm = new JSonPlantForm();
					jSonPlantForm.setW(0.0);
					jSonPlantForm.setWh(0.0);
					jSonPlantForm.setCongSuatChoPhep(0.0);
				}
				jSonPlantForm.setW(jSonPlantForm.getW() + jsonData.getW());
				jSonPlantForm.setWh(jSonPlantForm.getWh() + jsonData.getWh());
				jSonPlantForm.setCongSuatChoPhep(jSonPlantForm.getCongSuatChoPhep() + jsonData.getCongSuatChoPhep());
				deviceChartData.put(jsonData.getTime(), jSonPlantForm);
			}
		}

		List<JSonPlantForm> jsDataList = new ArrayList<>();
		long miliTimeNow = Calendar.getInstance().getTimeInMillis();
		for (String key : deviceChartData.keySet()) {
			String myDate = key;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			Date date = sdf.parse(myDate);
			long millis = date.getTime();

			JSonPlantForm jSonPlantForm = deviceChartData.get(key);
			JSonPlantForm json = new JSonPlantForm();
			json.setTime(key);

			if (jSonPlantForm.getW() != 0 || millis < miliTimeNow) {
				json.setW(jSonPlantForm.getW());
			} else {
				json.setW(null);
			}

			json.setWh(jSonPlantForm.getWh());
			json.setCongSuatChoPhep(jSonPlantForm.getCongSuatChoPhep());
			if (jSonPlantForm.getCongSuatChoPhep() != 0) {
				json.setW(jSonPlantForm.getCongSuatChoPhep());
			} else {
				json.setCongSuatChoPhep(null);
			}
			jsDataList.add(json);
		}

		return jsDataList;
	}

	public List<JSonPlantForm> getChart(String fromDate, String toDate, List<DataInverter1EVN> datas,
			List<Schedule> schedules, Device device) {

		LocalDate startDate = LocalDate.parse(fromDate);
		LocalDate endDate = LocalDate.parse(toDate);
		String[] hours = new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12",
				"13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23" };

		String[] minutes = new String[] { "00", "15", "30", "45" };

		List<JSonPlantForm> jsDataList = new ArrayList<JSonPlantForm>();

		double sumAcPower = 0.0;
		if (device.getAcPower() != null) {
			sumAcPower = device.getAcPower() / 1000000;
		}

		for (LocalDate date = startDate; date.isBefore(endDate.plusDays(1)); date = date.plusDays(1)) {
			LocalDate localDate = date;
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			String dateDefalt = localDate.format(formatter);

			for (int i = 0; i < hours.length; i++) {
				DataInverter1EVN inverter1 = new DataInverter1EVN();
				try {
					for (int j = 0; j < minutes.length; j++) {
						String t0 = dateDefalt + " " + hours[i] + ":" + minutes[j] + ":00";

						String t1;
						if (i == hours.length - 1 && j == minutes.length - 1) {
							t1 = dateDefalt + " " + "23:59:00";
						} else if (j == minutes.length - 1 || String.valueOf(minutes[j]) == "45") {
							t1 = dateDefalt + " " + hours[i + 1] + ":00:00";
						} else {
							t1 = dateDefalt + " " + hours[i] + ":" + minutes[j + 1] + ":00";
						}

						System.out.println("T0: " + t0 + " ~ T1: " + t1);

						long msT0 = DateUtils.toDate(t0, Constants.ES.DATETIME_FORMAT_YMDHMS).getTime();
						long msT1 = DateUtils.toDate(t1, Constants.ES.DATETIME_FORMAT_YMDHMS).getTime();
						inverter1.setSentDate(Timestamp.valueOf(t1));
						datas.add(inverter1);
						Collections.sort(datas);

						Double startW = null;
						Double diffW = null;

						Double startWH = null;
						boolean isStartFinish = false;
						Double diffWH = null;

						for (int u = 0; u < datas.size(); u++) {
							DataInverter1EVN dataInverter1 = datas.get(u);
							long ms = dataInverter1.getSentDate().getTime();

							if ((ms - msT0) < -5 * 60 * 1000 || (ms - msT1) > 5 * 60 * 1000) {
								continue;
							}

							if (!isStartFinish && msT0 <= ms && ms < msT1 && dataInverter1.getDeviceId() != null) {
								startWH = (double) (dataInverter1.getW() * 0.25);
								startW = (double) dataInverter1.getW();
								isStartFinish = true;
							}

//                            if (ms == msT1 && dataInverter1.getDeviceId() == null) {
//                                if (j == minutes.length - 1 || String.valueOf(minutes[j]) == "45") {
//                                    endWH = (double) (datas.get(u - 1).getWh() == null ? 0 : datas.get(u - 1).getWh());
//                                } else {
//                                	if (u == 0) {
//                                		endWH = (double) (datas.get(u).getWh() == null ? 0 : datas.get(u).getWh());
//                                	} else if (u < datas.size() - 1) {
//                                    	if ((ms - datas.get(u - 1).getSentDate().getTime()) < (datas.get(u + 1).getSentDate().getTime() - ms)) {
//                                            endWH = (double) (datas.get(u - 1).getWh() == null ? 0 : datas.get(u - 1).getWh());
//                                        } else {
//                                            endWH = (double) (datas.get(u + 1).getWh() == null ? 0 : datas.get(u + 1).getWh());
//                                        }
//                                    } else {
//                                    	endWH = (double) (datas.get(u - 1).getWh() == null ? 0 : datas.get(u - 1).getWh());
//                                    }
//                                    
//                                }
//
//                            }

						}

						if (startW == null) {
							startW = 0.0;
						}

						if (startWH == null) {
							startWH = 0.0;
						}

						if ("4".equals(globalTupeScope)) {
							diffWH = startWH / 1000;
							diffW = startW / 1000;
						} else {
							diffWH = startWH / 1000000;
							diffW = startW / 1000000;
						}

						if (diffW < 0) {
							diffW = 0.0;
						}

						if (diffWH < 0) {
							diffWH = 0.0;
						}

						JSonPlantForm json = new JSonPlantForm();

						json.setTime(dateDefalt + " " + hours[i] + ":" + minutes[j]);

						json.setW(diffW);

						json.setWh(diffWH);

						List<Schedule> shList = new ArrayList<Schedule>();
						if (schedules.size() > 0) {
							for (int u = 0; u < schedules.size(); u++) {
								SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
								SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
								Schedule schedule = schedules.get(u);
								Date date1 = sdf.parse(dateFormat.format(schedule.getTimeView()) + " "
										+ schedule.getFromTime() + ":00");
								Date date2 = sdf.parse(
										dateFormat.format(schedule.getTimeView()) + " " + schedule.getToTime() + ":00");
								long millisFromTime = date1.getTime();
								long millisToTime = date2.getTime();
								if (msT0 >= millisFromTime && msT1 <= millisToTime) {
									System.out.println("T0: " + t0 + " ~ T1: " + t1);
									shList.add(schedule);
								}

							}
						}

						if (shList.size() > 0) {
							double sumCSCP = (shList.stream()
									.mapToDouble(x -> x.getCongSuatChoPhep() == null ? 0 : x.getCongSuatChoPhep())
									.sum()) / 1000000;
							json.setCongSuatChoPhep(sumCSCP);
						} else {
							json.setCongSuatChoPhep(sumAcPower);
						}

						jsDataList.add(json);

					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		return jsDataList;
	}

}
