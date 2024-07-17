package vn.ses.s3m.plus.controllers.evn;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.ses.s3m.plus.dao.AreaMapper;
import vn.ses.s3m.plus.dao.DeviceMapper;
import vn.ses.s3m.plus.dao.ManagerMapper;
import vn.ses.s3m.plus.dao.ProjectMapper;
import vn.ses.s3m.plus.dao.SuperManagerMapper;
import vn.ses.s3m.plus.dao.evn.DataInverterMapperEVN;
import vn.ses.s3m.plus.dao.evn.HistoryMapperEVN;
import vn.ses.s3m.plus.dao.evn.ScheduleMapper;
import vn.ses.s3m.plus.dto.Area;
import vn.ses.s3m.plus.dto.Device;
import vn.ses.s3m.plus.dto.Manager;
import vn.ses.s3m.plus.dto.Project;
import vn.ses.s3m.plus.dto.SuperManager;
import vn.ses.s3m.plus.dto.User;
import vn.ses.s3m.plus.dto.evn.DataInverter1EVN;
import vn.ses.s3m.plus.dto.evn.DataSend;
import vn.ses.s3m.plus.dto.evn.HistoryEVN;
import vn.ses.s3m.plus.dto.evn.HistorySend;
import vn.ses.s3m.plus.dto.evn.Schedule;
import vn.ses.s3m.plus.service.UserService;

@RestController
@RequestMapping("/common/schedule")
@Validated
public class ScheduleController {

	@Autowired
	private DeviceMapper deviceMapper;

	@Autowired
	private ScheduleMapper scheduleMapper;

	@Autowired
	private DataInverterMapperEVN dataInverterMapper;

	@Autowired
	private SuperManagerMapper superManagerMapper;

	@Autowired
	private ManagerMapper managerMapper;

	@Autowired
	private AreaMapper areaMapper;

	@Autowired
	private ProjectMapper projectMapper;

	@Autowired
	private HistoryMapperEVN historyMapper;

	@Autowired
	private UserService userService;

	private String typeStatus;
	
	private static String contantDeviceType = "4";

	@GetMapping(value = "/data")
	public ResponseEntity<?> getSchedule() {

		Double tongCSDM = 0.0;
		Double tongCSTG = 0.0;

		List<SuperManager> superManagers = new ArrayList<SuperManager>();

		List<Schedule> schedules = new ArrayList<Schedule>();

		Map<String, String> conditionSuper = new HashMap<String, String>();
		superManagers = superManagerMapper.getSuperManagers(conditionSuper);

		for (int i = 0; i < superManagers.size(); i++) {
			SuperManager superManager = superManagers.get(i);

			Schedule schedule = new Schedule();
			schedule.setStt(superManager.getSuperManagerId());
			schedule.setAddRess(superManager.getSuperManagerName());

			Map<String, String> condition = new HashMap<String, String>();
			condition.put("superManagerId", String.valueOf(superManager.getSuperManagerId()));
			condition.put("deviceType", contantDeviceType);
			List<Device> devices = deviceMapper.getDeviceBySuperManagerId(condition);

			if (devices.size() > 0) {
				double sumAcPower = (devices.stream().mapToDouble(x -> x.getAcPower() == null ? 0 : x.getAcPower())
						.sum()) / 1000000;
				tongCSDM = (double) Math.round((tongCSDM + sumAcPower) * 1000) / 1000;
				schedule.setAcPower(sumAcPower);
			}

			schedules.add(schedule);

		}
		return new ResponseEntity<Object>(schedules, HttpStatus.OK);
	}

	@PostMapping(value = "/data/lower-level")
	public ResponseEntity<?> getScheduleLowerLevel(@RequestBody HistorySend data) {
		Map<String, Object> response = new HashMap<>();

		String userName = data.getUserName();

		User user = userService.getUserByUsername(userName);
		
		Calendar cal = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String toDate = formatter.format(cal.getTime());
        List<HistoryEVN> historyList = new ArrayList<HistoryEVN>();
        
		if (StringUtils.equals(String.valueOf(data.getType()), "4")) {
			SuperManager superManager = superManagerMapper.getSuperManagerById(Long.valueOf(user.getTargetId()));
			Map<String, String> conditionHistory = new HashMap<>();
			conditionHistory.put("stt", String.valueOf(user.getTargetId()));
			conditionHistory.put("typeScrop", "0");
			conditionHistory.put("deleteFlag", "0");
			conditionHistory.put("timeView", toDate);

            List<HistoryEVN> historys = historyMapper.getHistorysSetting(conditionHistory);
            for (int u = 0; u < historys.size(); u++) {
                HistoryEVN history = new HistoryEVN();
                history.setHistoryId(historys.get(u).getHistoryId());
                history.setFromDate(historys.get(u).getFromDate());
                history.setToDate(historys.get(u).getToDate());
                history.setTimeFrame(historys.get(u).getTimeFrame());
                history.setTimeInsert(historys.get(u).getTimeInsert());
                history.setCongSuatDinhMuc(historys.get(u).getCongSuatDinhMuc());
                history.setCongSuatTietGiam(historys.get(u).getCongSuatTietGiam());
                history.setStatus(historys.get(u).getStatus());
                history.setDeleteFlag(historys.get(u).getDeleteFlag());
                history.setUpdateFlag(historys.get(u).getUpdateFlag());
                history.setDeleteDate(historys.get(u).getDeleteDate());
                history.setViTri(superManager.getSuperManagerName());
                historyList.add(history);
            }
            
		} else if (StringUtils.equals(String.valueOf(data.getType()), "5")) {

			Manager manager = managerMapper.getManagerById(user.getTargetId());
			Map<String, String> conditionHistory = new HashMap<>();
			conditionHistory.put("stt", String.valueOf(user.getTargetId()));
			conditionHistory.put("typeScrop", "1");
			conditionHistory.put("deleteFlag", "0");
			conditionHistory.put("timeView", toDate);

            List<HistoryEVN> historys = historyMapper.getHistorysSetting(conditionHistory);
            for (int u = 0; u < historys.size(); u++) {
                HistoryEVN history = new HistoryEVN();
                history.setHistoryId(historys.get(u).getHistoryId());
                history.setFromDate(historys.get(u).getFromDate());
                history.setToDate(historys.get(u).getToDate());
                history.setTimeFrame(historys.get(u).getTimeFrame());
                history.setTimeInsert(historys.get(u).getTimeInsert());
                history.setCongSuatDinhMuc(historys.get(u).getCongSuatDinhMuc());
                history.setCongSuatTietGiam(historys.get(u).getCongSuatTietGiam());
                history.setStatus(historys.get(u).getStatus());
                history.setDeleteFlag(historys.get(u).getDeleteFlag());
                history.setUpdateFlag(historys.get(u).getUpdateFlag());
                history.setDeleteDate(historys.get(u).getDeleteDate());
                history.setViTri(manager.getManagerName());
                historyList.add(history);
            }
            
		} else if (StringUtils.equals(String.valueOf(data.getType()), "6")) {
			Map<String, String> conditionArea = new HashMap<>();
			conditionArea.put("areaId", String.valueOf(user.getTargetId()));
			Area area = areaMapper.getArea(conditionArea);
			Map<String, String> conditionHistory = new HashMap<>();
			conditionHistory.put("stt", String.valueOf(user.getTargetId()));
			conditionHistory.put("typeScrop", "2");
			conditionHistory.put("deleteFlag", "0");
			conditionHistory.put("timeView", toDate);

            List<HistoryEVN> historys = historyMapper.getHistorysSetting(conditionHistory);
            for (int u = 0; u < historys.size(); u++) {
                HistoryEVN history = new HistoryEVN();
                history.setHistoryId(historys.get(u).getHistoryId());
                history.setFromDate(historys.get(u).getFromDate());
                history.setToDate(historys.get(u).getToDate());
                history.setTimeFrame(historys.get(u).getTimeFrame());
                history.setTimeInsert(historys.get(u).getTimeInsert());
                history.setCongSuatDinhMuc(historys.get(u).getCongSuatDinhMuc());
                history.setCongSuatTietGiam(historys.get(u).getCongSuatTietGiam());
                history.setStatus(historys.get(u).getStatus());
                history.setDeleteFlag(historys.get(u).getDeleteFlag());
                history.setUpdateFlag(historys.get(u).getUpdateFlag());
                history.setDeleteDate(historys.get(u).getDeleteDate());
                history.setViTri(area.getAreaName());
                historyList.add(history);
            }
            
		}
		response.put("historySetting", historyList);

		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}

	@SuppressWarnings("unused")
	@GetMapping(value = "/system/{superMangerId}")
	public ResponseEntity<?> getPopUp(@PathVariable("superMangerId") final int id) {

		Schedule schedule = new Schedule();
		List<Device> devices = new ArrayList<Device>();
		Map<String, String> superSchedule = new HashMap<String, String>();
		Map<String, String> condition = new HashMap<String, String>();
		Map<String, Double> respone = new HashMap<String, Double>();

		superSchedule.put("stt", String.valueOf(id));
		schedule = scheduleMapper.getSchedule(superSchedule);

		condition.put("superManagerId", String.valueOf(id));
		condition.put("deviceType", contantDeviceType);
		devices = deviceMapper.getDeviceBySuperManagerId(condition);

		if (schedule != null) {
			double congSuatDat = schedule.getCongSuatTietGiam();
			respone.put("congSuatDat", congSuatDat);
		} else {
			if (devices.size() > 0) {
				int sumAcPower = devices.stream().mapToInt(x -> x.getAcPower() == null ? 0 : x.getAcPower()).sum();
				respone.put("congSuatDat", (double) sumAcPower);
			}
		}

		List<String> deviceIds = new ArrayList<String>();
		List<String> sentDate = new ArrayList<String>();
		for (int i = 0; i < devices.size(); i++) {
			Device device = devices.get(i);
			String deviceId = String.valueOf(device.getDeviceId());

			deviceIds.add(deviceId);
		}
		Map<String, Object> conditionCode = new HashMap<String, Object>();
		String timeStamp = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
		sentDate.add(timeStamp);
		conditionCode.put("deviceIds", deviceIds);
		conditionCode.put("sentDate", sentDate);
		List<DataInverter1EVN> dataInverter1s = new ArrayList<>();

		if (deviceIds.size() > 0) {
			dataInverter1s = dataInverterMapper.getDataInverter1ByDeviceIds(conditionCode);
		}

		Double sumW = null;
		if (dataInverter1s.size() > 0) {
			double w = dataInverter1s.stream().mapToDouble(x -> x.getW() == null ? 0 : x.getW()).sum();
			if (sumW == null) {
				sumW = w;
			} else {
				sumW = sumW + w;
			}

		}

		respone.put("sumW", sumW);
		return new ResponseEntity<Object>(respone, HttpStatus.OK);

	}

	@SuppressWarnings("unused")
	@GetMapping(value = "/history/detail")
	public ResponseEntity<List<Schedule>> getDetailHistory(@RequestBody HistorySend data) {
		String historyId = String.valueOf(data.getId());
		String fromDate = data.getFromDate();
		Map<String, String> condition = new HashMap<String, String>();
		condition.put("historyId", historyId);
		condition.put("timeViewArchive", fromDate);
		List<Schedule> scheduleList = scheduleMapper.getSchedules(condition);
		return new ResponseEntity<List<Schedule>>(scheduleList, HttpStatus.OK);
	}

	@PostMapping(value = "/history")
	public ResponseEntity<List<HistoryEVN>> getHistory(@RequestBody HistorySend data) {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuuu-MM-dd");
		LocalDate localDate = LocalDate.now();
		String toDate = dtf.format(localDate);
		String scrop = "";
		Map<String, String> condition = new HashMap<String, String>();
		if (data.getId() == null) {
			if (data.getType() == 3) {
				condition.put("toDate", toDate);
				condition.put("typeScrop", "5");
			} else if (data.getType() == 4) {
				condition.put("toDate", toDate);
				condition.put("typeScrop", "0");
				condition.put("stt", String.valueOf(data.getSuperManagerId()));
				scrop = "1";
			} else if (data.getType() == 5) {
				condition.put("toDate", toDate);
				condition.put("typeScrop", "1");
				condition.put("stt", String.valueOf(data.getManagerId()));
				scrop = "2";
			} else if (data.getType() == 6) {
				condition.put("toDate", toDate);
				condition.put("typeScrop", "2");
				condition.put("stt", String.valueOf(data.getAreaId()));
				scrop = "3";
			}
		} else {
			if (data.getType() == 3) {
				condition.put("toDate", toDate);
				condition.put("typeScrop", "0");
				condition.put("stt", String.valueOf(data.getId()));
				scrop = "0";
			} else if (data.getType() == 4) {
				condition.put("toDate", toDate);
				condition.put("typeScrop", "1");
				condition.put("stt", String.valueOf(data.getId()));
				scrop = "2";
			} else if (data.getType() == 5) {
				condition.put("toDate", toDate);
				condition.put("typeScrop", "2");
				condition.put("stt", String.valueOf(data.getId()));
				scrop = "3";
			} else if (data.getType() == 6) {
				condition.put("toDate", toDate);
				condition.put("typeScrop", "3");
				condition.put("stt", String.valueOf(data.getId()));
				scrop = "4";
			}

		}

		List<HistoryEVN> historys = historyMapper.getHistoryRealTime(condition);

		Map<String, String> conditionSchedule = new HashMap<String, String>();
		Map<String, String> conditionISchedule = new HashMap<String, String>();
		for (int i = 0; i < historys.size(); i++) {
			if (historys.get(i).getParentId() != null) {
				Schedule iSchedule = new Schedule();
				conditionSchedule.put("historyId", String.valueOf(historys.get(i).getParentId()));
				conditionSchedule.put("stt", String.valueOf(historys.get(i).getStt()));
				iSchedule = scheduleMapper.getSchedule(conditionSchedule);
				if (historys.get(i).getStatus() == 0) {
					historys.get(i).setViTri(iSchedule.getAddRess());
				} else if (historys.get(i).getStatus() == 1) {

					if (data.getType() == 3) {
						Schedule schedule = new Schedule();
						conditionISchedule.put("historyId", String.valueOf(historys.get(i).getParentId()));
						conditionISchedule.put("stt", String.valueOf(data.getId()));
						conditionISchedule.put("typeScrop", scrop);
						schedule = scheduleMapper.getSchedule(conditionISchedule);
						historys.get(i).setCreateDate(schedule.getCreateDate());
						historys.get(i).setViTri(iSchedule.getAddRess());
					} else if (data.getType() != 3 && data.getId() == null) {
						Schedule schedule = new Schedule();
						conditionISchedule.put("historyId", String.valueOf(historys.get(i).getHistoryId()));
						conditionISchedule.put("typeScrop", scrop);
						schedule = scheduleMapper.getSchedule(conditionISchedule);
						historys.get(i).setCreateDate(schedule.getCreateDate());
						historys.get(i).setViTri(iSchedule.getAddRess());
					} else {
						Schedule schedule = new Schedule();
						conditionISchedule.put("historyId", String.valueOf(historys.get(i).getHistoryId()));
						conditionISchedule.put("typeScrop", scrop);
						schedule = scheduleMapper.getSchedule(conditionISchedule);
						historys.get(i).setCreateDate(schedule.getCreateDate());
						historys.get(i).setViTri(iSchedule.getAddRess());
					}
				}
			}
		}
		return new ResponseEntity<List<HistoryEVN>>(historys, HttpStatus.OK);
	}

	@PostMapping(value = "/check")
	public ResponseEntity<?> checkSchedule(@RequestBody DataSend data) throws Exception {

		String timeTo = data.getTimeTo();
		String timeFrom = data.getTimeFrom();
		String fromDate = data.getFromDate();
		String toDate = data.getToDate();
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

		LocalDate startDate = LocalDate.parse(fromDate);
		LocalDate endDate = LocalDate.parse(toDate);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

		Map<String, String> condition = new HashMap<String, String>();
		condition.put("deleteFlag", "0");
		condition.put("fromDate", fromDate);
		condition.put("toDate", toDate);
		condition.put("typeScrop", "5");
		List<HistoryEVN> historyes = historyMapper.getHistoryByToDate(condition);
		List<HistoryEVN> historyListAll = new ArrayList<HistoryEVN>();
		for (int i = 0; i < historyes.size(); i++) {
			HistoryEVN history = historyes.get(i);
			String fd = history.getFromDate();
			String td = history.getToDate();
			LocalDate startFD = LocalDate.parse(fd);
			LocalDate endTD = LocalDate.parse(td);
			String[] timeFrame = String.valueOf(history.getTimeFrame()).split("~");
			String ft = timeFrame[0];
			String tt = timeFrame[1];

			List<HistoryEVN> historyList = new ArrayList<HistoryEVN>();
			for (LocalDate dateBefor = startFD; dateBefor
					.isBefore(endTD.plusDays(1)); dateBefor = dateBefor.plusDays(1)) {
				String fromDateBefor = dateBefor + " " + ft;
				String toDateBefor = dateBefor + " " + tt;

				Date date3 = sdf.parse(fromDateBefor);
				Date date4 = sdf.parse(toDateBefor);
				long millisFromDateBefor = date3.getTime();
				long millisToDateBefor = date4.getTime();

				for (LocalDate date = startDate; date.isBefore(endDate.plusDays(1)); date = date.plusDays(1)) {
					String fromDateAfter = date.toString() + " " + timeFrom;
					String toDateAfter = date.toString() + " " + timeTo;
					Date date1 = sdf.parse(fromDateAfter);
					Date date2 = sdf.parse(toDateAfter);
					long millisfromDateAfter = date1.getTime();
					long millistoDateAfter = date2.getTime();

					if ((millisfromDateAfter >= millisFromDateBefor && millisfromDateAfter <= millisToDateBefor)
							|| (millistoDateAfter >= millisFromDateBefor && millistoDateAfter <= millisToDateBefor)) {
						if (historyList.size() <= 0) {
							historyList.add(history);
						} else if (history.getHistoryId() != historyList.get(0).getHistoryId()) {
							historyList.add(history);
						}

					}
				}
			}
			historyListAll.addAll(historyList);
		}

		if (historyListAll.size() > 0) {
			typeStatus = "success";
			return new ResponseEntity<List<HistoryEVN>>(historyListAll, HttpStatus.OK);
		}

		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@PostMapping(value = "add")
	public ResponseEntity<?> addSchedule(@RequestBody DataSend data) throws Exception {

		Integer typeScrop = 0;
		Integer type = data.getType();
		if (type == 4) {
			typeScrop = 1;
		} else if (type == 5) {
			typeScrop = 2;
		} else if (type == 6) {
			typeScrop = 3;
		}
		String timeTo = data.getTimeTo();
		String timeFrom = data.getTimeFrom();
		String fromDate = data.getFromDate();
		String toDate = data.getToDate();
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
		Double sumCSCP = data.getSumCSCP();
		Double sumCSTG = data.getSumCSTG();
		Double sumCSDM = data.getSumCSDM();
		LocalDate startDate = LocalDate.parse(fromDate);
		LocalDate endDate = LocalDate.parse(toDate);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		List<Schedule> listSchedule = data.getData();
		List<Integer> deleteIdList = new ArrayList<Integer>();

		if (data.getTextStatus() != null) {
			List<HistoryEVN> historyes = data.getDataCheck();
			for (int u = 0; u < historyes.size(); u++) {
				deleteIdList.add(historyes.get(u).getHistoryId());
				Map<String, String> condition = new HashMap<String, String>();
				condition.put("typeScrop", "0");
				condition.put("historyId", String.valueOf(historyes.get(u).getHistoryId()));
				condition.put("parentId", String.valueOf(historyes.get(u).getHistoryId()));
				historyMapper.delete(condition);
				List<HistoryEVN> historyId1 = historyMapper.getHistorysParent(condition);

				if (historyId1.size() > 0) {
					for (int y = 0; y < historyId1.size(); y++) {
						deleteIdList.add(historyId1.get(y).getHistoryId());

						condition = new HashMap<String, String>();
						condition.put("typeScrop", "1");
						condition.put("historyId", String.valueOf(historyId1.get(y).getHistoryId()));
						condition.put("parentId", String.valueOf(historyId1.get(y).getHistoryId()));
						historyMapper.delete(condition);
						List<HistoryEVN> historyId2 = historyMapper.getHistorysParent(condition);

						if (historyId2.size() > 0) {
							for (int t = 0; t < historyId2.size(); t++) {
								deleteIdList.add(historyId2.get(t).getHistoryId());
								condition = new HashMap<String, String>();
								condition.put("typeScrop", "2");
								condition.put("historyId", String.valueOf(historyId2.get(t).getHistoryId()));
								List<HistoryEVN> historyId3 = historyMapper.getHistorysParent(condition);
								historyMapper.delete(condition);
								if (historyId3.size() > 0) {
									for (int r = 0; r < historyId3.size(); r++) {
										deleteIdList.add(historyId3.get(r).getHistoryId());
										condition = new HashMap<String, String>();
										condition.put("typeScrop", "3");
										condition.put("historyId", String.valueOf(historyId3.get(r).getHistoryId()));
										condition.put("parentId", String.valueOf(historyId3.get(r).getHistoryId()));
										List<HistoryEVN> historyId4 = historyMapper.getHistorysParent(condition);
										historyMapper.delete(condition);
										if (historyId4.size() > 0) {
											for (int q = 0; q < historyId4.size(); q++) {
												condition.put("historyId",
														String.valueOf(historyId4.get(q).getHistoryId()));
												historyMapper.delete(condition);
											}

										}

									}
								}

							}
						}

					}
				}

			}

			scheduleMapper.deleteHistoryIds(deleteIdList);
			if (historyes.size() > 0) {
				for (int i = 0; i < historyes.size(); i++) {
					HistoryEVN history = historyes.get(i);
					String fd = history.getFromDate();
					String td = history.getToDate();
					String[] timeFrame = String.valueOf(history.getTimeFrame()).split("~");
					String ft = timeFrame[0].trim();
					String tt = timeFrame[1].trim();

					String fromDateBefor = fd;
					String toDateBefor = td;

					Date date3 = sdf.parse(fromDateBefor);
					Date date4 = sdf.parse(toDateBefor);
					long millisFromDateBefor = date3.getTime();
					long millisToDateBefor = date4.getTime();

					String fromDateAfter = fromDate;
					String toDateAfter = toDate;
					Date date1 = sdf.parse(fromDateAfter);
					Date date2 = sdf.parse(toDateAfter);
					long millisFromDateAfter = date1.getTime();
					long millisToDateAfter = date2.getTime();

					if (StringUtils.equals(fromDate, fd) && StringUtils.equals(toDate, td)) {
						Map<String, Object> conditionHistory2 = new HashMap<String, Object>();
						conditionHistory2.put("fromDate", fromDate);
						conditionHistory2.put("toDate", toDate);
						conditionHistory2.put("timeFrame", timeFrom + " ~ " + timeTo);
						conditionHistory2.put("congSuatChoPhep", String.valueOf(sumCSCP * 1000000));
						conditionHistory2.put("congSuatTietGiam", String.valueOf(sumCSTG * 1000000));
						conditionHistory2.put("congSuatDinhMuc", String.valueOf(sumCSDM * 1000000));
						conditionHistory2.put("deleteFlag", "0");
						conditionHistory2.put("typeScrop", "5");
						if (sumCSDM != null && sumCSTG != null) {
							historyMapper.add(conditionHistory2);
						}

						Map<String, String> condition3 = new HashMap<String, String>();
						condition3.put("deleteFlag", "0");
						condition3.put("fromDate", fromDate);
						condition3.put("toDate", toDate);
						HistoryEVN htr3 = historyMapper.getHistory(condition3);

						for (int j = 0; j < listSchedule.size(); j++) {
							Schedule schedule = listSchedule.get(j);
							if (schedule.getStt() != null && schedule.getCongSuatDinhMuc() != null
									&& schedule.getCongSuatTietGiam() != null) {
								Map<String, Object> condition = new HashMap<String, Object>();
								condition.put("fromDate", fromDate);
								condition.put("toDate", toDate);
								condition.put("timeFrame", timeFrom + " ~ " + timeTo);
								if (schedule.getCongSuatChoPhep() != null) {
									condition.put("congSuatChoPhep",
											String.valueOf(schedule.getCongSuatChoPhep() * 1000000));
								}
								if (schedule.getCongSuatTietGiam() != null) {
									condition.put("congSuatTietGiam",
											String.valueOf(schedule.getCongSuatTietGiam() * 1000000));
								}
								if (schedule.getCongSuatDinhMuc() != null) {
									condition.put("congSuatDinhMuc",
											String.valueOf(schedule.getCongSuatDinhMuc() * 1000000));
								}
								condition.put("deleteFlag", "0");
								condition.put("typeScrop", String.valueOf(schedule.getTypeScrop()));
								condition.put("stt", String.valueOf(schedule.getStt()));
								condition.put("status", "0");
								condition.put("parentId", String.valueOf(htr3.getHistoryId()));
								historyMapper.add(condition);
							}
						}

						for (LocalDate date = startDate; date.isBefore(endDate.plusDays(1)); date = date.plusDays(1)) {
							for (int j = 0; j < listSchedule.size(); j++) {
								Schedule schedule = listSchedule.get(j);
								if (schedule.getStt() != null && schedule.getCongSuatDinhMuc() != null
										&& schedule.getCongSuatTietGiam() != null) {
									Map<String, String> condition = new HashMap<String, String>();
									condition.put("addRess", schedule.getAddRess());
									condition.put("scrop", String.valueOf(schedule.getTypeScrop()));

									condition.put("stt", String.valueOf(schedule.getStt()));
									if (schedule.getCongSuatTietGiam() != null) {
										condition.put("congSuatTietGiam",
												String.valueOf(schedule.getCongSuatTietGiam() * 1000000));
									}
									if (schedule.getCongSuatChoPhep() != null) {
										condition.put("congSuatChoPhep",
												String.valueOf(schedule.getCongSuatChoPhep() * 1000000));
									}
									condition.put("fromTime", timeFrom);
									condition.put("toTime", timeTo);
									condition.put("timeView", String.valueOf(date));
									condition.put("deleteFlag", "0");
									condition.put("createDate", String.valueOf(htr3.getTimeInsert()));
									condition.put("historyId", String.valueOf(htr3.getHistoryId()));
									scheduleMapper.add(condition);
								}
							}
						}
					} else {
						if (millisFromDateBefor == millisFromDateAfter) {
							if (millisToDateAfter < millisToDateBefor) {
								Map<String, Object> conditionHistory1 = new HashMap<String, Object>();
								conditionHistory1.put("fromDate", endDate.plusDays(1).toString());
								conditionHistory1.put("toDate", td);
								conditionHistory1.put("timeFrame", ft + " ~ " + tt);
								conditionHistory1.put("congSuatChoPhep", String.valueOf(history.getCongSuatChoPhep()));
								conditionHistory1.put("congSuatTietGiam",
										String.valueOf(history.getCongSuatTietGiam()));
								conditionHistory1.put("congSuatDinhMuc", String.valueOf(history.getCongSuatDinhMuc()));
								conditionHistory1.put("deleteFlag", "0");
								conditionHistory1.put("typeScrop", "5");
								historyMapper.add(conditionHistory1);

								Map<String, String> condition1 = new HashMap<String, String>();
								condition1.put("deleteFlag", "0");
								condition1.put("fromDate", endDate.plusDays(1).toString());
								condition1.put("toDate", td);
								condition1.put("typeScrop", "5");
								HistoryEVN htr1 = historyMapper.getHistory(condition1);

								List<Schedule> shList = new ArrayList<Schedule>();

								for (LocalDate date = endDate.plusDays(1); date
										.isBefore(LocalDate.parse(td).plusDays(1)); date = date.plusDays(1)) {
									Map<String, String> conditionSchedule = new HashMap<String, String>();
									conditionSchedule.put("date", date.toString());
									conditionSchedule.put("typeScrop", "0");
									conditionSchedule.put("historyId", String.valueOf(history.getHistoryId()));
									List<Schedule> sList = scheduleMapper.getSchedules(conditionSchedule);
									shList.addAll(sList);
									for (int o = 0; o < sList.size(); o++) {
										Schedule schedule = sList.get(o);
										Map<String, String> condition = new HashMap<String, String>();
										condition.put("stt", String.valueOf(schedule.getStt()));
										condition.put("addRess", schedule.getAddRess());
										condition.put("scrop", String.valueOf(schedule.getTypeScrop()));
										if (schedule.getCongSuatTietGiam() != null) {
											condition.put("congSuatTietGiam",
													String.valueOf(schedule.getCongSuatTietGiam() * 1000000));
										}
										if (schedule.getCongSuatChoPhep() != null) {
											condition.put("congSuatChoPhep",
													String.valueOf(schedule.getCongSuatChoPhep() * 1000000));
										}
										condition.put("fromTime", schedule.getFromTime());
										condition.put("toTime", schedule.getToTime());
										condition.put("timeView", String.valueOf(date));
										condition.put("historyId", String.valueOf(htr1.getHistoryId()));
										condition.put("deleteFlag", "0");
										condition.put("createDate", String.valueOf(htr1.getTimeInsert()));
										scheduleMapper.add(condition);

									}
								}

								for (int j = 0; j < shList.size(); j++) {
									Schedule s = shList.get(j);
									Map<String, Object> condition = new HashMap<String, Object>();
									condition.put("fromDate", endDate.plusDays(1).toString());
									condition.put("toDate", td);
									condition.put("timeFrame", ft + " ~ " + tt);
									if (s.getCongSuatChoPhep() != null) {
										condition.put("congSuatChoPhep",
												String.valueOf(s.getCongSuatChoPhep() * 1000000));
									}
									if (s.getCongSuatTietGiam() != null) {
										condition.put("congSuatTietGiam",
												String.valueOf(s.getCongSuatTietGiam() * 1000000));
									}
									condition.put("congSuatDinhMuc", String
											.valueOf((s.getCongSuatTietGiam() + s.getCongSuatChoPhep()) * 1000000));
									condition.put("deleteFlag", "0");
									condition.put("typeScrop", String.valueOf(s.getTypeScrop()));
									condition.put("stt", String.valueOf(s.getStt()));
									condition.put("status", "0");
									condition.put("parentId", String.valueOf(htr1.getHistoryId()));
									historyMapper.add(condition);
								}

								Map<String, Object> conditionHistory2 = new HashMap<String, Object>();
								conditionHistory2.put("fromDate", fromDate);
								conditionHistory2.put("toDate", toDate);
								conditionHistory2.put("timeFrame", timeFrom + " ~ " + timeTo);
								conditionHistory2.put("congSuatChoPhep", String.valueOf(sumCSCP * 1000000));
								conditionHistory2.put("congSuatTietGiam", String.valueOf(sumCSTG * 1000000));
								conditionHistory2.put("congSuatDinhMuc", String.valueOf(sumCSDM * 1000000));
								conditionHistory2.put("deleteFlag", "0");
								conditionHistory2.put("typeScrop", "5");
								if (sumCSDM != null && sumCSTG != null) {
									historyMapper.add(conditionHistory2);
								}

								Map<String, String> condition3 = new HashMap<String, String>();
								condition3.put("deleteFlag", "0");
								condition3.put("fromDate", fromDate);
								condition3.put("toDate", toDate);
								HistoryEVN htr3 = historyMapper.getHistory(condition3);

								for (int j = 0; j < listSchedule.size(); j++) {
									Schedule schedule = listSchedule.get(j);
									if (schedule.getStt() != null && schedule.getCongSuatDinhMuc() != null
											&& schedule.getCongSuatTietGiam() != null) {
										Map<String, Object> condition = new HashMap<String, Object>();
										condition.put("fromDate", fromDate);
										condition.put("toDate", toDate);
										condition.put("timeFrame", timeFrom + " ~ " + timeTo);
										if (schedule.getCongSuatChoPhep() != null) {
											condition.put("congSuatChoPhep",
													String.valueOf(schedule.getCongSuatChoPhep() * 1000000));
										}
										if (schedule.getCongSuatTietGiam() != null) {
											condition.put("congSuatTietGiam",
													String.valueOf(schedule.getCongSuatTietGiam() * 1000000));
										}
										if (schedule.getCongSuatDinhMuc() != null) {
											condition.put("congSuatDinhMuc",
													String.valueOf(schedule.getCongSuatDinhMuc() * 1000000));
										}
										condition.put("deleteFlag", "0");
										condition.put("typeScrop", String.valueOf(schedule.getTypeScrop()));
										condition.put("stt", String.valueOf(schedule.getStt()));
										condition.put("status", "0");
										condition.put("parentId", String.valueOf(htr3.getHistoryId()));
										historyMapper.add(condition);
									}
								}

								for (LocalDate date = startDate; date
										.isBefore(endDate.plusDays(1)); date = date.plusDays(1)) {
									for (int j = 0; j < listSchedule.size(); j++) {
										Schedule schedule = listSchedule.get(j);
										if (schedule.getStt() != null && schedule.getCongSuatDinhMuc() != null
												&& schedule.getCongSuatTietGiam() != null) {
											Map<String, String> condition = new HashMap<String, String>();
											condition.put("addRess", schedule.getAddRess());
											condition.put("scrop", String.valueOf(schedule.getTypeScrop()));

											condition.put("stt", String.valueOf(schedule.getStt()));
											if (schedule.getCongSuatTietGiam() != null) {
												condition.put("congSuatTietGiam",
														String.valueOf(schedule.getCongSuatTietGiam() * 1000000));
											}
											if (schedule.getCongSuatChoPhep() != null) {
												condition.put("congSuatChoPhep",
														String.valueOf(schedule.getCongSuatChoPhep() * 1000000));
											}
											condition.put("fromTime", timeFrom);
											condition.put("toTime", timeTo);
											condition.put("timeView", String.valueOf(date));
											condition.put("deleteFlag", "0");
											condition.put("createDate", String.valueOf(htr3.getTimeInsert()));
											condition.put("historyId", String.valueOf(htr3.getHistoryId()));
											scheduleMapper.add(condition);
										}
									}
								}
							} else {
								Map<String, Object> conditionHistory2 = new HashMap<String, Object>();
								conditionHistory2.put("fromDate", fromDate);
								conditionHistory2.put("toDate", toDate);
								conditionHistory2.put("timeFrame", timeFrom + " ~ " + timeTo);
								conditionHistory2.put("congSuatChoPhep", String.valueOf(sumCSCP * 1000000));
								conditionHistory2.put("congSuatTietGiam", String.valueOf(sumCSTG * 1000000));
								conditionHistory2.put("congSuatDinhMuc", String.valueOf(sumCSDM * 1000000));
								conditionHistory2.put("deleteFlag", "0");
								conditionHistory2.put("typeScrop", "5");
								if (sumCSDM != null && sumCSTG != null) {
									historyMapper.add(conditionHistory2);
								}
								Map<String, String> condition3 = new HashMap<String, String>();
								condition3.put("deleteFlag", "0");
								condition3.put("fromDate", fromDate);
								condition3.put("toDate", toDate);
								HistoryEVN htr3 = historyMapper.getHistory(condition3);

								for (int j = 0; j < listSchedule.size(); j++) {
									Schedule schedule = listSchedule.get(j);
									if (schedule.getStt() != null && schedule.getCongSuatDinhMuc() != null
											&& schedule.getCongSuatTietGiam() != null) {
										Map<String, Object> condition = new HashMap<String, Object>();
										condition.put("fromDate", fromDate);
										condition.put("toDate", toDate);
										condition.put("timeFrame", timeFrom + " ~ " + timeTo);
										if (schedule.getCongSuatChoPhep() != null) {
											condition.put("congSuatChoPhep",
													String.valueOf(schedule.getCongSuatChoPhep() * 1000000));
										}
										if (schedule.getCongSuatTietGiam() != null) {
											condition.put("congSuatTietGiam",
													String.valueOf(schedule.getCongSuatTietGiam() * 1000000));
										}
										if (schedule.getCongSuatDinhMuc() != null) {
											condition.put("congSuatDinhMuc",
													String.valueOf(schedule.getCongSuatDinhMuc() * 1000000));
										}
										condition.put("deleteFlag", "0");
										condition.put("typeScrop", String.valueOf(schedule.getTypeScrop()));
										condition.put("stt", String.valueOf(schedule.getStt()));
										condition.put("status", "0");
										condition.put("parentId", String.valueOf(htr3.getHistoryId()));
										historyMapper.add(condition);
									}
								}

								for (LocalDate date = startDate; date
										.isBefore(endDate.plusDays(1)); date = date.plusDays(1)) {
									for (int j = 0; j < listSchedule.size(); j++) {
										Schedule schedule = listSchedule.get(j);
										if (schedule.getStt() != null && schedule.getCongSuatDinhMuc() != null
												&& schedule.getCongSuatTietGiam() != null) {
											Map<String, String> condition = new HashMap<String, String>();
											condition.put("addRess", schedule.getAddRess());
											condition.put("scrop", String.valueOf(schedule.getTypeScrop()));

											condition.put("stt", String.valueOf(schedule.getStt()));
											if (schedule.getCongSuatTietGiam() != null) {
												condition.put("congSuatTietGiam",
														String.valueOf(schedule.getCongSuatTietGiam() * 1000000));
											}
											if (schedule.getCongSuatChoPhep() != null) {
												condition.put("congSuatChoPhep",
														String.valueOf(schedule.getCongSuatChoPhep() * 1000000));
											}
											condition.put("fromTime", timeFrom);
											condition.put("toTime", timeTo);
											condition.put("timeView", String.valueOf(date));
											condition.put("deleteFlag", "0");
											condition.put("createDate", String.valueOf(htr3.getTimeInsert()));
											condition.put("historyId", String.valueOf(htr3.getHistoryId()));
											scheduleMapper.add(condition);
										}
									}
								}
							}
						} else if (millisToDateAfter == millisToDateBefor) {
							if (millisFromDateBefor < millisFromDateAfter) {
								Map<String, Object> conditionHistory1 = new HashMap<String, Object>();
								conditionHistory1.put("fromDate", fd);
								conditionHistory1.put("toDate", startDate.plusDays(-1).toString());
								conditionHistory1.put("timeFrame", ft + " ~ " + tt);
								conditionHistory1.put("congSuatChoPhep", String.valueOf(history.getCongSuatChoPhep()));
								conditionHistory1.put("congSuatTietGiam",
										String.valueOf(history.getCongSuatTietGiam()));
								conditionHistory1.put("congSuatDinhMuc", String.valueOf(history.getCongSuatDinhMuc()));
								conditionHistory1.put("deleteFlag", "0");
								conditionHistory1.put("typeScrop", "5");
								historyMapper.add(conditionHistory1);

								Map<String, String> condition1 = new HashMap<String, String>();
								condition1.put("deleteFlag", "0");
								condition1.put("fromDate", fd);
								condition1.put("toDate", startDate.plusDays(-1).toString());
								condition1.put("typeScrop", "5");
								HistoryEVN htr1 = historyMapper.getHistory(condition1);

								List<Schedule> shList = new ArrayList<Schedule>();

								for (LocalDate date = LocalDate.parse(fd); date
										.isBefore(startDate); date = date.plusDays(1)) {
									Map<String, String> conditionSchedule = new HashMap<String, String>();
									conditionSchedule.put("date", date.toString());
									conditionSchedule.put("typeScrop", "0");
									conditionSchedule.put("historyId", String.valueOf(history.getHistoryId()));
									List<Schedule> sList = scheduleMapper.getSchedules(conditionSchedule);
									shList.addAll(sList);
									for (int o = 0; o < sList.size(); o++) {
										Schedule schedule = sList.get(o);
										Map<String, String> condition = new HashMap<String, String>();
										condition.put("stt", String.valueOf(schedule.getStt()));
										condition.put("addRess", schedule.getAddRess());
										condition.put("scrop", String.valueOf(schedule.getTypeScrop()));
										if (schedule.getCongSuatTietGiam() != null) {
											condition.put("congSuatTietGiam",
													String.valueOf(schedule.getCongSuatTietGiam() * 1000000));
										}
										if (schedule.getCongSuatChoPhep() != null) {
											condition.put("congSuatChoPhep",
													String.valueOf(schedule.getCongSuatChoPhep() * 1000000));
										}
										condition.put("fromTime", schedule.getFromTime());
										condition.put("toTime", schedule.getToTime());
										condition.put("timeView", String.valueOf(date));
										condition.put("historyId", String.valueOf(htr1.getHistoryId()));
										condition.put("deleteFlag", "0");
										condition.put("createDate", String.valueOf(htr1.getTimeInsert()));
										scheduleMapper.add(condition);

									}
								}

								for (int j = 0; j < shList.size(); j++) {
									Schedule s = shList.get(j);
									Map<String, Object> condition = new HashMap<String, Object>();
									condition.put("fromDate", fd);
									condition.put("toDate", startDate.plusDays(-1).toString());
									condition.put("timeFrame", ft + " ~ " + tt);
									if (s.getCongSuatChoPhep() != null) {
										condition.put("congSuatChoPhep",
												String.valueOf(s.getCongSuatChoPhep() * 1000000));
									}
									if (s.getCongSuatTietGiam() != null) {
										condition.put("congSuatTietGiam",
												String.valueOf(s.getCongSuatTietGiam() * 1000000));
									}
									condition.put("congSuatDinhMuc", String
											.valueOf((s.getCongSuatTietGiam() + s.getCongSuatChoPhep()) * 1000000));
									condition.put("deleteFlag", "0");
									condition.put("typeScrop", String.valueOf(s.getTypeScrop()));
									condition.put("stt", String.valueOf(s.getStt()));
									condition.put("status", "0");
									condition.put("parentId", String.valueOf(htr1.getHistoryId()));
									historyMapper.add(condition);
								}

								Map<String, Object> conditionHistory2 = new HashMap<String, Object>();
								conditionHistory2.put("fromDate", fromDate);
								conditionHistory2.put("toDate", toDate);
								conditionHistory2.put("timeFrame", timeFrom + " ~ " + timeTo);
								conditionHistory2.put("congSuatChoPhep", String.valueOf(sumCSCP * 1000000));
								conditionHistory2.put("congSuatTietGiam", String.valueOf(sumCSTG * 1000000));
								conditionHistory2.put("congSuatDinhMuc", String.valueOf(sumCSDM * 1000000));
								conditionHistory2.put("deleteFlag", "0");
								conditionHistory2.put("typeScrop", "5");
								if (sumCSDM != null && sumCSTG != null) {
									historyMapper.add(conditionHistory2);
								}

								Map<String, String> condition3 = new HashMap<String, String>();
								condition3.put("deleteFlag", "0");
								condition3.put("fromDate", fromDate);
								condition3.put("toDate", toDate);
								HistoryEVN htr3 = historyMapper.getHistory(condition3);

								for (int j = 0; j < listSchedule.size(); j++) {
									Schedule schedule = listSchedule.get(j);
									if (schedule.getStt() != null && schedule.getCongSuatDinhMuc() != null
											&& schedule.getCongSuatTietGiam() != null) {
										Map<String, Object> condition = new HashMap<String, Object>();
										condition.put("fromDate", fromDate);
										condition.put("toDate", toDate);
										condition.put("timeFrame", timeFrom + " ~ " + timeTo);
										if (schedule.getCongSuatChoPhep() != null) {
											condition.put("congSuatChoPhep",
													String.valueOf(schedule.getCongSuatChoPhep() * 1000000));
										}
										if (schedule.getCongSuatTietGiam() != null) {
											condition.put("congSuatTietGiam",
													String.valueOf(schedule.getCongSuatTietGiam() * 1000000));
										}
										if (schedule.getCongSuatDinhMuc() != null) {
											condition.put("congSuatDinhMuc",
													String.valueOf(schedule.getCongSuatDinhMuc() * 1000000));
										}
										condition.put("deleteFlag", "0");
										condition.put("typeScrop", String.valueOf(schedule.getTypeScrop()));
										condition.put("stt", String.valueOf(schedule.getStt()));
										condition.put("status", "0");
										condition.put("parentId", String.valueOf(htr3.getHistoryId()));
										historyMapper.add(condition);
									}
								}

								for (LocalDate date = startDate; date
										.isBefore(endDate.plusDays(1)); date = date.plusDays(1)) {
									for (int j = 0; j < listSchedule.size(); j++) {
										Schedule schedule = listSchedule.get(j);
										if (schedule.getStt() != null && schedule.getCongSuatDinhMuc() != null
												&& schedule.getCongSuatTietGiam() != null) {
											Map<String, String> condition = new HashMap<String, String>();
											condition.put("addRess", schedule.getAddRess());
											condition.put("scrop", String.valueOf(schedule.getTypeScrop()));

											condition.put("stt", String.valueOf(schedule.getStt()));
											if (schedule.getCongSuatTietGiam() != null) {
												condition.put("congSuatTietGiam",
														String.valueOf(schedule.getCongSuatTietGiam() * 1000000));
											}
											if (schedule.getCongSuatChoPhep() != null) {
												condition.put("congSuatChoPhep",
														String.valueOf(schedule.getCongSuatChoPhep() * 1000000));
											}
											condition.put("fromTime", timeFrom);
											condition.put("toTime", timeTo);
											condition.put("timeView", String.valueOf(date));
											condition.put("deleteFlag", "0");
											condition.put("createDate", String.valueOf(htr3.getTimeInsert()));
											condition.put("historyId", String.valueOf(htr3.getHistoryId()));
											scheduleMapper.add(condition);
										}
									}
								}
							} else {
								Map<String, Object> conditionHistory2 = new HashMap<String, Object>();
								conditionHistory2.put("fromDate", fromDate);
								conditionHistory2.put("toDate", toDate);
								conditionHistory2.put("timeFrame", timeFrom + " ~ " + timeTo);
								conditionHistory2.put("congSuatChoPhep", sumCSCP);
								conditionHistory2.put("congSuatTietGiam", sumCSTG);
								conditionHistory2.put("congSuatDinhMuc", sumCSDM);
								conditionHistory2.put("deleteFlag", "0");
								conditionHistory2.put("typeScrop", "5");
								if (sumCSDM != null && sumCSTG != null) {
									historyMapper.add(conditionHistory2);
								}
								Map<String, String> condition3 = new HashMap<String, String>();
								condition3.put("deleteFlag", "0");
								condition3.put("fromDate", fromDate);
								condition3.put("toDate", toDate);
								HistoryEVN htr3 = historyMapper.getHistory(condition3);

								for (int j = 0; j < listSchedule.size(); j++) {
									Schedule schedule = listSchedule.get(j);
									if (schedule.getStt() != null) {
										Map<String, Object> condition = new HashMap<String, Object>();
										condition.put("fromDate", fromDate);
										condition.put("toDate", toDate);
										condition.put("timeFrame", timeFrom + " ~ " + timeTo);
										if (schedule.getCongSuatChoPhep() != null) {
											condition.put("congSuatChoPhep",
													String.valueOf(schedule.getCongSuatChoPhep() * 1000000));
										}
										if (schedule.getCongSuatTietGiam() != null) {
											condition.put("congSuatTietGiam",
													String.valueOf(schedule.getCongSuatTietGiam() * 1000000));
										}
										if (schedule.getCongSuatDinhMuc() != null) {
											condition.put("congSuatDinhMuc",
													String.valueOf(schedule.getCongSuatDinhMuc() * 1000000));
										}
										condition.put("deleteFlag", "0");
										condition.put("typeScrop", String.valueOf(schedule.getTypeScrop()));
										condition.put("stt", String.valueOf(schedule.getStt()));
										condition.put("status", "0");
										condition.put("parentId", String.valueOf(htr3.getHistoryId()));
										historyMapper.add(condition);
									}
								}

								for (LocalDate date = startDate; date
										.isBefore(endDate.plusDays(1)); date = date.plusDays(1)) {
									for (int j = 0; j < listSchedule.size(); j++) {
										Schedule schedule = listSchedule.get(j);
										if (schedule.getStt() != null && schedule.getCongSuatDinhMuc() != null
												&& schedule.getCongSuatTietGiam() != null) {
											Map<String, String> condition = new HashMap<String, String>();
											condition.put("addRess", schedule.getAddRess());
											condition.put("scrop", String.valueOf(schedule.getTypeScrop()));

											condition.put("stt", String.valueOf(schedule.getStt()));
											if (schedule.getCongSuatTietGiam() != null) {
												condition.put("congSuatTietGiam",
														String.valueOf(schedule.getCongSuatTietGiam() * 1000000));
											}
											if (schedule.getCongSuatChoPhep() != null) {
												condition.put("congSuatChoPhep",
														String.valueOf(schedule.getCongSuatChoPhep() * 1000000));
											}
											condition.put("fromTime", timeFrom);
											condition.put("toTime", timeTo);
											condition.put("timeView", String.valueOf(date));
											condition.put("deleteFlag", "0");
											condition.put("createDate", String.valueOf(htr3.getTimeInsert()));
											condition.put("historyId", String.valueOf(htr3.getHistoryId()));
											scheduleMapper.add(condition);
										}
									}
								}

							}
						} else if (millisFromDateBefor < millisFromDateAfter) {
							if (millisToDateAfter < millisToDateBefor) {
								Map<String, Object> conditionHistory1 = new HashMap<String, Object>();
								conditionHistory1.put("fromDate", fd);
								conditionHistory1.put("toDate", startDate.plusDays(-1).toString());
								conditionHistory1.put("timeFrame", ft + " ~ " + tt);
								conditionHistory1.put("congSuatChoPhep", String.valueOf(history.getCongSuatChoPhep()));
								conditionHistory1.put("congSuatTietGiam",
										String.valueOf(history.getCongSuatTietGiam()));
								conditionHistory1.put("congSuatDinhMuc", String.valueOf(history.getCongSuatDinhMuc()));
								conditionHistory1.put("deleteFlag", "0");
								conditionHistory1.put("typeScrop", "5");
								historyMapper.add(conditionHistory1);

								Map<String, String> condition1 = new HashMap<String, String>();
								condition1.put("deleteFlag", "0");
								condition1.put("fromDate", fd);
								condition1.put("toDate", startDate.plusDays(-1).toString());
								condition1.put("typeScrop", "5");
								HistoryEVN htr1 = historyMapper.getHistory(condition1);

								List<Schedule> shList = new ArrayList<Schedule>();

								for (LocalDate date = LocalDate.parse(fd); date
										.isBefore(startDate); date = date.plusDays(1)) {
									Map<String, String> conditionSchedule = new HashMap<String, String>();
									conditionSchedule.put("date", date.toString());
									conditionSchedule.put("typeScrop", "0");
									conditionSchedule.put("historyId", String.valueOf(history.getHistoryId()));
									List<Schedule> sList = scheduleMapper.getSchedules(conditionSchedule);
									shList.addAll(sList);
									for (int o = 0; o < sList.size(); o++) {
										Schedule schedule = sList.get(o);
										Map<String, String> condition = new HashMap<String, String>();
										condition.put("stt", String.valueOf(schedule.getStt()));
										condition.put("addRess", schedule.getAddRess());
										condition.put("scrop", String.valueOf(schedule.getTypeScrop()));
										if (schedule.getCongSuatTietGiam() != null) {
											condition.put("congSuatTietGiam",
													String.valueOf(schedule.getCongSuatTietGiam() * 1000000));
										}
										if (schedule.getCongSuatChoPhep() != null) {
											condition.put("congSuatChoPhep",
													String.valueOf(schedule.getCongSuatChoPhep() * 1000000));
										}
										condition.put("fromTime", schedule.getFromTime());
										condition.put("toTime", schedule.getToTime());
										condition.put("timeView", String.valueOf(date));
										condition.put("historyId", String.valueOf(htr1.getHistoryId()));
										condition.put("deleteFlag", "0");
										condition.put("createDate", String.valueOf(htr1.getTimeInsert()));
										scheduleMapper.add(condition);

									}
								}

								for (int j = 0; j < shList.size(); j++) {
									Schedule s = shList.get(j);
									Map<String, Object> condition = new HashMap<String, Object>();
									condition.put("fromDate", fd);
									condition.put("toDate", startDate.plusDays(-1).toString());
									condition.put("timeFrame", ft + " ~ " + tt);
									if (s.getCongSuatChoPhep() != null) {
										condition.put("congSuatChoPhep",
												String.valueOf(s.getCongSuatChoPhep() * 1000000));
									}
									if (s.getCongSuatTietGiam() != null) {
										condition.put("congSuatTietGiam",
												String.valueOf(s.getCongSuatTietGiam() * 1000000));
									}
									condition.put("congSuatDinhMuc", String
											.valueOf((s.getCongSuatTietGiam() + s.getCongSuatChoPhep()) * 1000000));
									condition.put("deleteFlag", "0");
									condition.put("typeScrop", String.valueOf(s.getTypeScrop()));
									condition.put("stt", String.valueOf(s.getStt()));
									condition.put("status", "0");
									condition.put("parentId", String.valueOf(htr1.getHistoryId()));
									historyMapper.add(condition);
								}

								Map<String, Object> conditionHistory3 = new HashMap<String, Object>();
								conditionHistory3.put("fromDate", endDate.plusDays(1).toString());
								conditionHistory3.put("toDate", td);
								conditionHistory3.put("timeFrame", ft + " ~ " + tt);
								conditionHistory3.put("congSuatChoPhep", String.valueOf(history.getCongSuatChoPhep()));
								conditionHistory3.put("congSuatTietGiam",
										String.valueOf(history.getCongSuatTietGiam()));
								conditionHistory3.put("congSuatDinhMuc", String.valueOf(history.getCongSuatDinhMuc()));
								conditionHistory3.put("deleteFlag", "0");
								conditionHistory3.put("typeScrop", "5");
								historyMapper.add(conditionHistory3);
								Map<String, String> condition2 = new HashMap<String, String>();
								condition2.put("deleteFlag", "0");
								condition2.put("fromDate", endDate.plusDays(1).toString());
								condition2.put("toDate", td);
								condition2.put("typeScrop", "5");
								HistoryEVN htr2 = historyMapper.getHistory(condition2);

								for (int j = 0; j < shList.size(); j++) {
									Schedule s = shList.get(j);
									Map<String, Object> condition = new HashMap<String, Object>();
									condition.put("fromDate", endDate.plusDays(1).toString());
									condition.put("toDate", td);
									condition.put("timeFrame", ft + " ~ " + tt);
									if (s.getCongSuatChoPhep() != null) {
										condition.put("congSuatChoPhep",
												String.valueOf(s.getCongSuatChoPhep() * 1000000));
									}
									if (s.getCongSuatTietGiam() != null) {
										condition.put("congSuatTietGiam",
												String.valueOf(s.getCongSuatTietGiam() * 1000000));
									}
									condition.put("congSuatDinhMuc", String
											.valueOf((s.getCongSuatTietGiam() + s.getCongSuatChoPhep()) * 1000000));
									condition.put("deleteFlag", "0");
									condition.put("typeScrop", typeScrop);
									condition.put("stt", String.valueOf(s.getStt()));
									condition.put("status", "0");
									condition.put("parentId", String.valueOf(htr2.getHistoryId()));
									historyMapper.add(condition);
								}

								for (LocalDate date = endDate.plusDays(1); date
										.isBefore(LocalDate.parse(td).plusDays(1)); date = date.plusDays(1)) {
									for (int o = 0; o < shList.size(); o++) {
										Schedule schedule = shList.get(o);
										Map<String, String> condition = new HashMap<String, String>();
										condition.put("stt", String.valueOf(schedule.getStt()));
										condition.put("addRess", schedule.getAddRess());
										condition.put("scrop", String.valueOf(schedule.getTypeScrop()));
										if (schedule.getCongSuatTietGiam() != null) {
											condition.put("congSuatTietGiam",
													String.valueOf(schedule.getCongSuatTietGiam() * 1000000));
										}
										if (schedule.getCongSuatChoPhep() != null) {
											condition.put("congSuatChoPhep",
													String.valueOf(schedule.getCongSuatChoPhep() * 1000000));
										}
										condition.put("fromTime", schedule.getFromTime());
										condition.put("toTime", schedule.getToTime());
										condition.put("timeView", String.valueOf(date));
										condition.put("historyId", String.valueOf(htr2.getHistoryId()));
										condition.put("deleteFlag", "0");
										condition.put("createDate", String.valueOf(htr2.getTimeInsert()));
										scheduleMapper.add(condition);

									}
								}

								Map<String, Object> conditionHistory2 = new HashMap<String, Object>();
								conditionHistory2.put("fromDate", fromDate);
								conditionHistory2.put("toDate", toDate);
								conditionHistory2.put("timeFrame", timeFrom + " ~ " + timeTo);
								conditionHistory2.put("congSuatChoPhep", String.valueOf(sumCSCP * 1000000));
								conditionHistory2.put("congSuatTietGiam", String.valueOf(sumCSTG * 1000000));
								conditionHistory2.put("congSuatDinhMuc", String.valueOf(sumCSDM * 1000000));
								conditionHistory2.put("deleteFlag", "0");
								conditionHistory2.put("typeScrop", "5");
								if (sumCSDM != null && sumCSTG != null) {
									historyMapper.add(conditionHistory2);
								}
								Map<String, String> condition3 = new HashMap<String, String>();
								condition3.put("deleteFlag", "0");
								condition3.put("fromDate", fromDate);
								condition3.put("toDate", toDate);
								HistoryEVN htr3 = historyMapper.getHistory(condition3);

								for (int j = 0; j < listSchedule.size(); j++) {
									Schedule schedule = listSchedule.get(j);
									if (schedule.getStt() != null && schedule.getCongSuatDinhMuc() != null
											&& schedule.getCongSuatTietGiam() != null) {
										Map<String, Object> condition = new HashMap<String, Object>();
										condition.put("fromDate", fromDate);
										condition.put("toDate", toDate);
										condition.put("timeFrame", timeFrom + " ~ " + timeTo);
										if (schedule.getCongSuatChoPhep() != null) {
											condition.put("congSuatChoPhep",
													String.valueOf(schedule.getCongSuatChoPhep() * 1000000));
										}
										if (schedule.getCongSuatTietGiam() != null) {
											condition.put("congSuatTietGiam",
													String.valueOf(schedule.getCongSuatTietGiam() * 1000000));
										}
										if (schedule.getCongSuatDinhMuc() != null) {
											condition.put("congSuatDinhMuc",
													String.valueOf(schedule.getCongSuatDinhMuc() * 1000000));
										}
										condition.put("deleteFlag", "0");
										condition.put("typeScrop", String.valueOf(schedule.getTypeScrop()));
										condition.put("stt", String.valueOf(schedule.getStt()));
										condition.put("status", "0");
										condition.put("parentId", String.valueOf(htr3.getHistoryId()));
										historyMapper.add(condition);
									}
								}

								for (LocalDate date = startDate; date
										.isBefore(endDate.plusDays(1)); date = date.plusDays(1)) {
									for (int j = 0; j < listSchedule.size(); j++) {
										Schedule schedule = listSchedule.get(j);
										if (schedule.getStt() != null && schedule.getCongSuatDinhMuc() != null
												&& schedule.getCongSuatTietGiam() != null) {
											Map<String, String> condition = new HashMap<String, String>();
											condition.put("addRess", schedule.getAddRess());
											condition.put("scrop", String.valueOf(schedule.getTypeScrop()));

											condition.put("stt", String.valueOf(schedule.getStt()));
											if (schedule.getCongSuatTietGiam() != null) {
												condition.put("congSuatTietGiam",
														String.valueOf(schedule.getCongSuatTietGiam() * 1000000));
											}
											if (schedule.getCongSuatChoPhep() != null) {
												condition.put("congSuatChoPhep",
														String.valueOf(schedule.getCongSuatChoPhep() * 1000000));
											}
											condition.put("fromTime", timeFrom);
											condition.put("toTime", timeTo);
											condition.put("timeView", String.valueOf(date));
											condition.put("deleteFlag", "0");
											condition.put("createDate", String.valueOf(htr3.getTimeInsert()));
											condition.put("historyId", String.valueOf(htr3.getHistoryId()));
											scheduleMapper.add(condition);
										}
									}
								}
							} else {
								Map<String, Object> conditionHistory1 = new HashMap<String, Object>();
								conditionHistory1.put("fromDate", fd);
								conditionHistory1.put("toDate", startDate.plusDays(-1).toString());
								conditionHistory1.put("timeFrame", ft + " ~ " + tt);
								conditionHistory1.put("congSuatChoPhep", String.valueOf(history.getCongSuatChoPhep()));
								conditionHistory1.put("congSuatTietGiam",
										String.valueOf(history.getCongSuatTietGiam()));
								conditionHistory1.put("congSuatDinhMuc", String.valueOf(history.getCongSuatDinhMuc()));
								conditionHistory1.put("deleteFlag", "0");
								conditionHistory1.put("typeScrop", "5");
								historyMapper.add(conditionHistory1);

								Map<String, String> condition1 = new HashMap<String, String>();
								condition1.put("deleteFlag", "0");
								condition1.put("fromDate", fd);
								condition1.put("toDate", startDate.plusDays(-1).toString());
								condition1.put("typeScrop", "5");
								HistoryEVN htr1 = historyMapper.getHistory(condition1);

								List<Schedule> shList = new ArrayList<Schedule>();

								for (LocalDate date = LocalDate.parse(fd); date
										.isBefore(startDate); date = date.plusDays(1)) {
									Map<String, String> conditionSchedule = new HashMap<String, String>();
									conditionSchedule.put("date", date.toString());
									conditionSchedule.put("typeScrop", "0");
									conditionSchedule.put("historyId", String.valueOf(history.getHistoryId()));
									List<Schedule> sList = scheduleMapper.getSchedules(conditionSchedule);
									shList.addAll(sList);
									for (int o = 0; o < sList.size(); o++) {
										Schedule schedule = sList.get(o);
										Map<String, String> condition = new HashMap<String, String>();
										condition.put("stt", String.valueOf(schedule.getStt()));
										condition.put("addRess", schedule.getAddRess());
										condition.put("scrop", String.valueOf(schedule.getTypeScrop()));
										if (schedule.getCongSuatTietGiam() != null) {
											condition.put("congSuatTietGiam",
													String.valueOf(schedule.getCongSuatTietGiam() * 1000000));
										}
										if (schedule.getCongSuatChoPhep() != null) {
											condition.put("congSuatChoPhep",
													String.valueOf(schedule.getCongSuatChoPhep() * 1000000));
										}
										condition.put("fromTime", schedule.getFromTime());
										condition.put("toTime", schedule.getToTime());
										condition.put("timeView", String.valueOf(date));
										condition.put("historyId", String.valueOf(htr1.getHistoryId()));
										condition.put("deleteFlag", "0");
										condition.put("createDate", String.valueOf(htr1.getTimeInsert()));
										scheduleMapper.add(condition);

									}
								}

								for (int j = 0; j < shList.size(); j++) {
									Schedule s = shList.get(j);
									Map<String, Object> condition = new HashMap<String, Object>();
									condition.put("fromDate", fd);
									condition.put("toDate", startDate.plusDays(-1).toString());
									condition.put("timeFrame", ft + " ~ " + tt);
									if (s.getCongSuatChoPhep() != null) {
										condition.put("congSuatChoPhep",
												String.valueOf(s.getCongSuatChoPhep() * 1000000));
									}
									if (s.getCongSuatTietGiam() != null) {
										condition.put("congSuatTietGiam",
												String.valueOf(s.getCongSuatTietGiam() * 1000000));
									}
									condition.put("congSuatDinhMuc", String
											.valueOf((s.getCongSuatTietGiam() + s.getCongSuatChoPhep()) * 1000000));
									condition.put("deleteFlag", "0");
									condition.put("typeScrop", String.valueOf(s.getTypeScrop()));
									condition.put("stt", String.valueOf(s.getStt()));
									condition.put("status", "0");
									condition.put("parentId", String.valueOf(htr1.getHistoryId()));
									historyMapper.add(condition);
								}

								Map<String, Object> conditionHistory2 = new HashMap<String, Object>();
								conditionHistory2.put("fromDate", fromDate);
								conditionHistory2.put("toDate", toDate);
								conditionHistory2.put("timeFrame", timeFrom + " ~ " + timeTo);
								conditionHistory2.put("congSuatChoPhep", String.valueOf(sumCSCP * 1000000));
								conditionHistory2.put("congSuatTietGiam", String.valueOf(sumCSTG * 1000000));
								conditionHistory2.put("congSuatDinhMuc", String.valueOf(sumCSDM * 1000000));
								conditionHistory2.put("deleteFlag", "0");
								conditionHistory2.put("typeScrop", "5");
								if (sumCSDM != null && sumCSTG != null) {
									historyMapper.add(conditionHistory2);
								}
								Map<String, String> condition3 = new HashMap<String, String>();
								condition3.put("deleteFlag", "0");
								condition3.put("fromDate", fromDate);
								condition3.put("toDate", toDate);
								HistoryEVN htr3 = historyMapper.getHistory(condition3);

								for (int j = 0; j < listSchedule.size(); j++) {
									Schedule schedule = listSchedule.get(j);
									if (schedule.getStt() != null && schedule.getCongSuatDinhMuc() != null
											&& schedule.getCongSuatTietGiam() != null) {
										Map<String, Object> condition = new HashMap<String, Object>();
										condition.put("fromDate", fromDate);
										condition.put("toDate", toDate);
										condition.put("timeFrame", timeFrom + " ~ " + timeTo);
										if (schedule.getCongSuatChoPhep() != null) {
											condition.put("congSuatChoPhep",
													String.valueOf(schedule.getCongSuatChoPhep() * 1000000));
										}
										if (schedule.getCongSuatTietGiam() != null) {
											condition.put("congSuatTietGiam",
													String.valueOf(schedule.getCongSuatTietGiam() * 1000000));
										}
										if (schedule.getCongSuatDinhMuc() != null) {
											condition.put("congSuatDinhMuc",
													String.valueOf(schedule.getCongSuatDinhMuc() * 1000000));
										}
										condition.put("deleteFlag", "0");
										condition.put("typeScrop", String.valueOf(schedule.getTypeScrop()));
										condition.put("stt", String.valueOf(schedule.getStt()));
										condition.put("status", "0");
										condition.put("parentId", String.valueOf(htr3.getHistoryId()));
										historyMapper.add(condition);
									}
								}

								for (LocalDate date = startDate; date
										.isBefore(endDate.plusDays(1)); date = date.plusDays(1)) {
									for (int j = 0; j < listSchedule.size(); j++) {
										Schedule schedule = listSchedule.get(j);
										if (schedule.getStt() != null && schedule.getCongSuatDinhMuc() != null
												&& schedule.getCongSuatTietGiam() != null) {
											Map<String, String> condition = new HashMap<String, String>();
											condition.put("addRess", schedule.getAddRess());
											condition.put("scrop", String.valueOf(schedule.getTypeScrop()));

											condition.put("stt", String.valueOf(schedule.getStt()));
											if (schedule.getCongSuatTietGiam() != null) {
												condition.put("congSuatTietGiam",
														String.valueOf(schedule.getCongSuatTietGiam() * 1000000));
											}
											if (schedule.getCongSuatChoPhep() != null) {
												condition.put("congSuatChoPhep",
														String.valueOf(schedule.getCongSuatChoPhep() * 1000000));
											}
											condition.put("fromTime", timeFrom);
											condition.put("toTime", timeTo);
											condition.put("timeView", String.valueOf(date));
											condition.put("deleteFlag", "0");
											condition.put("createDate", String.valueOf(htr3.getTimeInsert()));
											condition.put("historyId", String.valueOf(htr3.getHistoryId()));
											scheduleMapper.add(condition);
										}
									}
								}
							}
						} else {
							if (millisToDateBefor > millisToDateAfter) {
								Map<String, Object> conditionHistory2 = new HashMap<String, Object>();
								conditionHistory2.put("fromDate", endDate.plusDays(1).toString());
								conditionHistory2.put("toDate", td);
								conditionHistory2.put("timeFrame", ft + " ~ " + tt);
								conditionHistory2.put("congSuatChoPhep", String.valueOf(history.getCongSuatChoPhep()));
								conditionHistory2.put("congSuatTietGiam",
										String.valueOf(history.getCongSuatTietGiam()));
								conditionHistory2.put("congSuatDinhMuc", String.valueOf(history.getCongSuatDinhMuc()));
								conditionHistory2.put("deleteFlag", "0");
								conditionHistory2.put("typeScrop", "5");
								historyMapper.add(conditionHistory2);

								Map<String, String> condition1 = new HashMap<String, String>();
								condition1.put("deleteFlag", "0");
								condition1.put("fromDate", endDate.plusDays(1).toString());
								condition1.put("toDate", td);
								condition1.put("typeScrop", "5");
								HistoryEVN htr1 = historyMapper.getHistory(condition1);

								List<Schedule> shList = new ArrayList<Schedule>();

								for (LocalDate date = endDate.plusDays(1); date
										.isBefore(LocalDate.parse(td).plusDays(1)); date = date.plusDays(1)) {
									Map<String, String> conditionSchedule = new HashMap<String, String>();
									conditionSchedule.put("date", date.toString());
									conditionSchedule.put("typeScrop", "0");
									conditionSchedule.put("historyId", String.valueOf(history.getHistoryId()));
									List<Schedule> sList = scheduleMapper.getSchedules(conditionSchedule);
									shList.addAll(sList);
									for (int o = 0; o < sList.size(); o++) {
										Schedule schedule = sList.get(o);
										Map<String, String> condition = new HashMap<String, String>();
										condition.put("stt", String.valueOf(schedule.getStt()));
										condition.put("addRess", schedule.getAddRess());
										condition.put("scrop", String.valueOf(schedule.getTypeScrop()));
										if (schedule.getCongSuatTietGiam() != null) {
											condition.put("congSuatTietGiam",
													String.valueOf(schedule.getCongSuatTietGiam() * 1000000));
										}
										if (schedule.getCongSuatChoPhep() != null) {
											condition.put("congSuatChoPhep",
													String.valueOf(schedule.getCongSuatChoPhep() * 1000000));
										}
										condition.put("fromTime", schedule.getFromTime());
										condition.put("toTime", schedule.getToTime());
										condition.put("timeView", String.valueOf(date));
										condition.put("historyId", String.valueOf(htr1.getHistoryId()));
										condition.put("deleteFlag", "0");
										condition.put("createDate", String.valueOf(htr1.getTimeInsert()));
										scheduleMapper.add(condition);

									}
								}

								for (int j = 0; j < shList.size(); j++) {
									Schedule s = shList.get(j);
									Map<String, Object> condition = new HashMap<String, Object>();
									condition.put("fromDate", endDate.plusDays(1).toString());
									condition.put("toDate", td);
									condition.put("timeFrame", ft + " ~ " + tt);
									if (s.getCongSuatChoPhep() != null) {
										condition.put("congSuatChoPhep",
												String.valueOf(s.getCongSuatChoPhep() * 1000000));
									}
									if (s.getCongSuatTietGiam() != null) {
										condition.put("congSuatTietGiam",
												String.valueOf(s.getCongSuatTietGiam() * 1000000));
									}
									condition.put("congSuatDinhMuc", String
											.valueOf((s.getCongSuatTietGiam() + s.getCongSuatChoPhep()) * 1000000));
									condition.put("deleteFlag", "0");
									condition.put("typeScrop", String.valueOf(s.getTypeScrop()));
									condition.put("stt", String.valueOf(s.getStt()));
									condition.put("status", "0");
									condition.put("parentId", String.valueOf(htr1.getHistoryId()));
									historyMapper.add(condition);
								}

								Map<String, Object> conditionHistory1 = new HashMap<String, Object>();
								conditionHistory1.put("fromDate", fromDate);
								conditionHistory1.put("toDate", toDate);
								conditionHistory1.put("timeFrame", timeFrom + " ~ " + timeTo);
								conditionHistory1.put("congSuatChoPhep", String.valueOf(sumCSCP * 1000000));
								conditionHistory1.put("congSuatTietGiam", String.valueOf(sumCSTG * 1000000));
								conditionHistory1.put("congSuatDinhMuc", String.valueOf(sumCSDM * 1000000));
								conditionHistory1.put("deleteFlag", "0");
								conditionHistory1.put("typeScrop", "5");
								if (sumCSDM != null && sumCSTG != null) {
									historyMapper.add(conditionHistory1);
								}
								Map<String, String> condition3 = new HashMap<String, String>();
								condition3.put("deleteFlag", "0");
								condition3.put("fromDate", fromDate);
								condition3.put("toDate", toDate);
								HistoryEVN htr3 = historyMapper.getHistory(condition3);

								for (int j = 0; j < listSchedule.size(); j++) {
									Schedule schedule = listSchedule.get(j);
									if (schedule.getStt() != null && schedule.getCongSuatDinhMuc() != null
											&& schedule.getCongSuatTietGiam() != null) {
										Map<String, Object> condition = new HashMap<String, Object>();
										condition.put("fromDate", fromDate);
										condition.put("toDate", toDate);
										condition.put("timeFrame", timeFrom + " ~ " + timeTo);
										if (schedule.getCongSuatChoPhep() != null) {
											condition.put("congSuatChoPhep",
													String.valueOf(schedule.getCongSuatChoPhep() * 1000000));
										}
										if (schedule.getCongSuatTietGiam() != null) {
											condition.put("congSuatTietGiam",
													String.valueOf(schedule.getCongSuatTietGiam() * 1000000));
										}
										if (schedule.getCongSuatDinhMuc() != null) {
											condition.put("congSuatDinhMuc",
													String.valueOf(schedule.getCongSuatDinhMuc() * 1000000));
										}
										condition.put("deleteFlag", "0");
										condition.put("typeScrop", typeScrop);
										condition.put("stt", String.valueOf(schedule.getStt()));
										condition.put("status", "0");
										condition.put("parentId", String.valueOf(htr3.getHistoryId()));
										historyMapper.add(condition);
									}
								}

								for (LocalDate date = startDate; date
										.isBefore(endDate.plusDays(1)); date = date.plusDays(1)) {
									for (int j = 0; j < listSchedule.size(); j++) {
										Schedule schedule = listSchedule.get(j);
										if (schedule.getStt() != null && schedule.getCongSuatDinhMuc() != null
												&& schedule.getCongSuatTietGiam() != null) {
											Map<String, String> condition = new HashMap<String, String>();
											condition.put("addRess", schedule.getAddRess());
											condition.put("scrop", String.valueOf(schedule.getTypeScrop()));

											condition.put("stt", String.valueOf(schedule.getStt()));
											if (schedule.getCongSuatTietGiam() != null) {
												condition.put("congSuatTietGiam",
														String.valueOf(schedule.getCongSuatTietGiam() * 1000000));
											}
											if (schedule.getCongSuatChoPhep() != null) {
												condition.put("congSuatChoPhep",
														String.valueOf(schedule.getCongSuatChoPhep() * 1000000));
											}
											condition.put("fromTime", timeFrom);
											condition.put("toTime", timeTo);
											condition.put("timeView", String.valueOf(date));
											condition.put("deleteFlag", "0");
											condition.put("createDate", String.valueOf(htr3.getTimeInsert()));
											condition.put("historyId", String.valueOf(htr3.getHistoryId()));
											scheduleMapper.add(condition);
										}
									}
								}
							} else {
								Map<String, Object> conditionHistory2 = new HashMap<String, Object>();
								conditionHistory2.put("fromDate", fromDate);
								conditionHistory2.put("toDate", toDate);
								conditionHistory2.put("timeFrame", timeFrom + " ~ " + timeTo);
								conditionHistory2.put("congSuatChoPhep", String.valueOf(sumCSCP * 1000000));
								conditionHistory2.put("congSuatTietGiam", String.valueOf(sumCSTG * 1000000));
								conditionHistory2.put("congSuatDinhMuc", String.valueOf(sumCSDM * 1000000));
								conditionHistory2.put("deleteFlag", "0");
								conditionHistory2.put("typeScrop", "5");
								if (sumCSDM != null && sumCSTG != null) {
									historyMapper.add(conditionHistory2);
								}
								Map<String, String> condition3 = new HashMap<String, String>();
								condition3.put("deleteFlag", "0");
								condition3.put("fromDate", fromDate);
								condition3.put("toDate", toDate);
								HistoryEVN htr3 = historyMapper.getHistory(condition3);

								for (int j = 0; j < listSchedule.size(); j++) {
									Schedule schedule = listSchedule.get(j);
									if (schedule.getStt() != null && schedule.getCongSuatDinhMuc() != null
											&& schedule.getCongSuatTietGiam() != null) {
										Map<String, Object> condition = new HashMap<String, Object>();
										condition.put("fromDate", fromDate);
										condition.put("toDate", toDate);
										condition.put("timeFrame", timeFrom + " ~ " + timeTo);
										if (schedule.getCongSuatChoPhep() != null) {
											condition.put("congSuatChoPhep",
													String.valueOf(schedule.getCongSuatChoPhep() * 1000000));
										}
										if (schedule.getCongSuatTietGiam() != null) {
											condition.put("congSuatTietGiam",
													String.valueOf(schedule.getCongSuatTietGiam() * 1000000));
										}
										if (schedule.getCongSuatDinhMuc() != null) {
											condition.put("congSuatDinhMuc",
													String.valueOf(schedule.getCongSuatDinhMuc() * 1000000));
										}
										condition.put("deleteFlag", "0");
										condition.put("typeScrop", typeScrop);
										condition.put("stt", String.valueOf(schedule.getStt()));
										condition.put("status", "0");
										condition.put("parentId", String.valueOf(htr3.getHistoryId()));
										historyMapper.add(condition);
									}
								}

								for (LocalDate date = startDate; date
										.isBefore(endDate.plusDays(1)); date = date.plusDays(1)) {
									for (int j = 0; j < listSchedule.size(); j++) {
										Schedule schedule = listSchedule.get(j);
										if (schedule.getStt() != null && schedule.getCongSuatDinhMuc() != null
												&& schedule.getCongSuatTietGiam() != null) {
											Map<String, String> condition = new HashMap<String, String>();
											condition.put("addRess", schedule.getAddRess());
											condition.put("scrop", String.valueOf(schedule.getTypeScrop()));

											condition.put("stt", String.valueOf(schedule.getStt()));
											if (schedule.getCongSuatTietGiam() != null) {
												condition.put("congSuatTietGiam",
														String.valueOf(schedule.getCongSuatTietGiam() * 1000000));
											}
											if (schedule.getCongSuatChoPhep() != null) {
												condition.put("congSuatChoPhep",
														String.valueOf(schedule.getCongSuatChoPhep() * 1000000));
											}
											condition.put("fromTime", timeFrom);
											condition.put("toTime", timeTo);
											condition.put("timeView", String.valueOf(date));
											condition.put("deleteFlag", "0");
											condition.put("createDate", String.valueOf(htr3.getTimeInsert()));
											condition.put("historyId", String.valueOf(htr3.getHistoryId()));
											scheduleMapper.add(condition);
										}
									}
								}

							}
						}
					}

				}
			}
		} else {
			if (data.getIdSchedule() == null) {

				HistoryEVN history = new HistoryEVN();

				Map<String, Object> conditionHistory = new HashMap<String, Object>();
				conditionHistory.put("fromDate", fromDate);
				conditionHistory.put("toDate", toDate);
				conditionHistory.put("timeFrame", timeFrom + " ~ " + timeTo);
				conditionHistory.put("congSuatChoPhep", String.valueOf(sumCSCP * 1000000));
				conditionHistory.put("congSuatTietGiam", String.valueOf(sumCSTG * 1000000));
				conditionHistory.put("congSuatDinhMuc", String.valueOf(sumCSDM * 1000000));
				conditionHistory.put("typeScrop", "5");
				conditionHistory.put("deleteFlag", "0");
				historyMapper.add(conditionHistory);

				Map<String, String> condition2 = new HashMap<String, String>();
				condition2.put("deleteFlag", "0");
				condition2.put("fromDate", fromDate);
				condition2.put("toDate", toDate);
				condition2.put("typeScrop", "5");
				history = historyMapper.getHistory(condition2);

				for (int i = 0; i < listSchedule.size(); i++) {
					Schedule schedule = listSchedule.get(i);
					if (schedule.getStt() != null && schedule.getCongSuatDinhMuc() != null
							&& schedule.getCongSuatTietGiam() != null) {
						Map<String, Object> condition = new HashMap<String, Object>();
						condition.put("fromDate", fromDate);
						condition.put("toDate", toDate);
						condition.put("timeFrame", timeFrom + " ~ " + timeTo);
						if (schedule.getCongSuatChoPhep() != null) {
							condition.put("congSuatChoPhep", String.valueOf(schedule.getCongSuatChoPhep() * 1000000));
						}
						if (schedule.getCongSuatTietGiam() != null) {
							condition.put("congSuatTietGiam", String.valueOf(schedule.getCongSuatTietGiam() * 1000000));
						}
						condition.put("congSuatDinhMuc", String.valueOf(schedule.getCongSuatDinhMuc() * 1000000));
						condition.put("deleteFlag", "0");
						condition.put("typeScrop", "0");
						condition.put("stt", String.valueOf(schedule.getStt()));
						condition.put("status", "0");
						condition.put("parentId", String.valueOf(history.getHistoryId()));
						historyMapper.add(condition);
					}

				}

				if (listSchedule.size() > 0) {
					for (LocalDate date = startDate; date.isBefore(endDate.plusDays(1)); date = date.plusDays(1)) {
						for (int i = 0; i < listSchedule.size(); i++) {
							Schedule schedule = listSchedule.get(i);
							if (schedule.getStt() != null && schedule.getCongSuatDinhMuc() != null
									&& schedule.getCongSuatTietGiam() != null) {
								Map<String, String> condition = new HashMap<String, String>();
								condition.put("stt", String.valueOf(schedule.getStt()));
								condition.put("addRess", schedule.getAddRess());
								condition.put("scrop", String.valueOf(schedule.getTypeScrop()));
								if (schedule.getCongSuatTietGiam() != null) {
									condition.put("congSuatTietGiam",
											String.valueOf(schedule.getCongSuatTietGiam() * 1000000));
								}
								if (schedule.getCongSuatChoPhep() != null) {
									condition.put("congSuatChoPhep",
											String.valueOf(schedule.getCongSuatChoPhep() * 1000000));
								}
								condition.put("fromTime", timeFrom);
								condition.put("toTime", timeTo);
								condition.put("timeView", String.valueOf(date));
								condition.put("historyId", String.valueOf(history.getHistoryId()));
								condition.put("deleteFlag", "0");
								condition.put("status", "0");
								condition.put("createDate", String.valueOf(history.getTimeInsert()));
								scheduleMapper.add(condition);
							}

						}
					}
				}
			} else {
				String idScheduleSetting = String.valueOf(data.getIdSchedule());
				Map<String, String> conditionCheckHistory = new HashMap<String, String>();
				conditionCheckHistory.put("historyId", idScheduleSetting);
				conditionCheckHistory.put("deleteFlag", "0");
				HistoryEVN history = historyMapper.getHistory(conditionCheckHistory);
				if (history != null && history.getStatus() != 0) {
					Map<String, String> updateStatus = new HashMap<String, String>();
					updateStatus.put("updateFlag", "2");
					updateStatus.put("id", idScheduleSetting);
					historyMapper.updateUpdateFlag(updateStatus);
					for (int i = 0; i < listSchedule.size(); i++) {
						Schedule schedule = listSchedule.get(i);
						if (schedule.getStt() != null) {
							Map<String, String> condition = new HashMap<String, String>();
							if (schedule.getCongSuatChoPhep() != null) {
								condition.put("congSuatChoPhep",
										String.valueOf(schedule.getCongSuatChoPhep() * 1000000));
							}
							if (schedule.getCongSuatTietGiam() != null) {
								condition.put("congSuatTietGiam",
										String.valueOf(schedule.getCongSuatTietGiam() * 1000000));
							}
							if (schedule.getCongSuatDinhMuc() != null) {
								condition.put("congSuatDinhMuc",
										String.valueOf(schedule.getCongSuatDinhMuc() * 1000000));
							}
							condition.put("parentId", idScheduleSetting);
							condition.put("updateFlag", "1");
							condition.put("stt", String.valueOf(schedule.getStt()));
							historyMapper.updateParent(condition);

							for (LocalDate date = startDate; date
									.isBefore(endDate.plusDays(1)); date = date.plusDays(1)) {
								Map<String, String> conditionAddSchedule = new HashMap<String, String>();
								if (schedule.getCongSuatTietGiam() != null) {
									conditionAddSchedule.put("congSuatTietGiam",
											String.valueOf(schedule.getCongSuatTietGiam()));
								}
								if (schedule.getCongSuatChoPhep() != null) {
									conditionAddSchedule.put("congSuatChoPhep",
											String.valueOf(schedule.getCongSuatChoPhep()));
								}
								conditionAddSchedule.put("historyIdAfter", idScheduleSetting);
								conditionAddSchedule.put("stt", String.valueOf(schedule.getStt()));
								scheduleMapper.update(conditionAddSchedule);
							}
						}
					}
				} else {
					if (listSchedule.size() > 0) {
						Map<String, String> updateUpdateFlag = new HashMap<String, String>();
						updateUpdateFlag.put("updateFlag", "0");
						updateUpdateFlag.put("id", idScheduleSetting);
						updateUpdateFlag.put("historyId", idScheduleSetting);
						historyMapper.updateUpdateFlag(updateUpdateFlag);
						HistoryEVN historyParent = historyMapper.getHistory(updateUpdateFlag);
						Map<String, String> updateFlagByParentId = new HashMap<String, String>();
						updateFlagByParentId.put("updateFlag", "0");
						updateFlagByParentId.put("parentId", String.valueOf(historyParent.getHistoryId()));
						historyMapper.updateUpdateFlag(updateFlagByParentId);
						for (int i = 0; i < listSchedule.size(); i++) {
							Schedule schedule = listSchedule.get(i);
							if (schedule.getStt() != null && schedule.getCongSuatTietGiam() != null) {
								Map<String, Object> condition = new HashMap<String, Object>();
								condition.put("fromDate", fromDate);
								condition.put("toDate", toDate);
								condition.put("timeFrame", timeFrom + " ~ " + timeTo);
								if (schedule.getCongSuatChoPhep() != null) {
									condition.put("congSuatChoPhep",
											String.valueOf(schedule.getCongSuatChoPhep() * 1000000));
								}
								if (schedule.getCongSuatTietGiam() != null) {
									condition.put("congSuatTietGiam",
											String.valueOf(schedule.getCongSuatTietGiam() * 1000000));
								}
								if (schedule.getCongSuatDinhMuc() != null) {
									condition.put("congSuatDinhMuc",
											String.valueOf(schedule.getCongSuatDinhMuc() * 1000000));
								}
								condition.put("deleteFlag", "0");
								condition.put("typeScrop", String.valueOf(typeScrop));
								condition.put("stt", String.valueOf(schedule.getStt()));
								condition.put("status", "0");
								condition.put("parentId", idScheduleSetting);
								historyMapper.add(condition);

								for (LocalDate date = startDate; date
										.isBefore(endDate.plusDays(1)); date = date.plusDays(1)) {
									Map<String, String> conditionAddSchedule = new HashMap<String, String>();
									conditionAddSchedule.put("stt", String.valueOf(schedule.getStt()));
									conditionAddSchedule.put("addRess", schedule.getAddRess());
									conditionAddSchedule.put("scrop", String.valueOf(typeScrop));
									if (schedule.getCongSuatTietGiam() != null) {
										conditionAddSchedule.put("congSuatTietGiam",
												String.valueOf(schedule.getCongSuatTietGiam() * 1000000));
									}
									if (schedule.getCongSuatChoPhep() != null) {
										conditionAddSchedule.put("congSuatChoPhep",
												String.valueOf(schedule.getCongSuatChoPhep() * 1000000));
									}
									conditionAddSchedule.put("fromTime", timeFrom);
									conditionAddSchedule.put("toTime", timeTo);
									conditionAddSchedule.put("timeView", String.valueOf(date));
									conditionAddSchedule.put("deleteFlag", "0");
									conditionAddSchedule.put("status", "0");
									conditionAddSchedule.put("historyId", idScheduleSetting);
									scheduleMapper.addSchedule(conditionAddSchedule);
								}

								Map<String, String> updateStatus = new HashMap<String, String>();
								updateStatus.put("status", "1");
								updateStatus.put("id", idScheduleSetting);
								historyMapper.updateStatus(updateStatus);
							}
						}
					}

					Map<String, String> condition = new HashMap<String, String>();
					condition.put("id", history.getParentId().toString());
					condition.put("status", "1");
					historyMapper.updateSendStatusById(condition);

				}
			}
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping(value = "history/show")
	public ResponseEntity<DataSend> getHistoryByParentId(@RequestBody HistorySend data) {
		String idSchedule = String.valueOf(data.getTargetId());
		String typeScrop = "";

		if (data.getType() == 4) {
			typeScrop = "1";
		} else if (data.getType() == 5) {
			typeScrop = "2";
		} else if (data.getType() == 6) {
			typeScrop = "3";
		}

		List<Manager> managers = new ArrayList<Manager>();
		List<Area> areas = new ArrayList<Area>();
		List<Project> projects = new ArrayList<Project>();

		if (StringUtils.equals(typeScrop, "1")) {
			Map<String, String> conditionManager = new HashMap<String, String>();
			conditionManager.put("superManagerId", idSchedule);
			managers = managerMapper.getManagers(conditionManager);
		} else if (StringUtils.equals(typeScrop, "2")) {
			Map<String, String> conditionArea = new HashMap<String, String>();
			conditionArea.put("managerId", idSchedule);
			areas = areaMapper.getAreas(conditionArea);
		} else if (StringUtils.equals(typeScrop, "3")) {
			Map<String, String> conditionProject = new HashMap<String, String>();
			conditionProject.put("areaId", idSchedule);
			projects = projectMapper.getProjectList(conditionProject);
		}

		Double tongCSDM = 0.0;
		Double tongCSTG = 0.0;
		DataSend respone = new DataSend();
		List<Schedule> schedules = new ArrayList<Schedule>();
		if (StringUtils.equals(typeScrop, "1") && managers.size() > 0) {
			for (int i = 0; i < managers.size(); i++) {
				Manager manager = managers.get(i);

				Schedule schedule = new Schedule();
				schedule.setStt(manager.getManagerId());
				schedule.setAddRess(manager.getManagerName());

				Map<String, String> condition = new HashMap<String, String>();
				condition.put("managerId", String.valueOf(manager.getManagerId()));
				condition.put("deviceType", contantDeviceType);
				List<Device> devices = deviceMapper.getDeviceByManagerId(condition);

				if (devices.size() > 0) {
					double sumAcPower = (double) Math
							.round((devices.stream().mapToDouble(x -> x.getAcPower() == null ? 0 : x.getAcPower()).sum()
									/ 1000000) * 1000)
							/ 1000;
					tongCSDM = (double) Math.round(sumAcPower * 1000) / 1000;
					schedule.setAcPower(sumAcPower);
				} else {
					schedule.setAcPower(0.0);
				}

				schedules.add(schedule);
			}

			Double tongCSCP = tongCSDM - (double) Math.round(tongCSTG * 1000) / 1000;

			respone.setSumCSCP(tongCSCP);
			respone.setSumCSDM(tongCSDM);
			respone.setSumCSTG(tongCSTG);

			respone.setData(schedules);

		} else if (StringUtils.equals(typeScrop, "2") && areas.size() > 0) {

			for (int i = 0; i < areas.size(); i++) {
				Area area = areas.get(i);

				Schedule schedule = new Schedule();
				schedule.setStt(area.getAreaId());
				schedule.setAddRess(area.getAreaName());

				Map<String, String> condition = new HashMap<String, String>();
				condition.put("areaId", String.valueOf(area.getAreaId()));
				condition.put("deviceType", contantDeviceType);
				List<Device> devices = deviceMapper.getDeviceByAreaId(condition);

				if (devices.size() > 0) {
					double sumAcPower = (double) Math
							.round((devices.stream().mapToDouble(x -> x.getAcPower() == null ? 0 : x.getAcPower()).sum()
									/ 1000000) * 1000)
							/ 1000;
					tongCSDM = (double) Math.round(sumAcPower * 1000) / 1000;
					schedule.setAcPower(sumAcPower);
				}

				schedules.add(schedule);
			}

			Double tongCSCP = tongCSDM - (double) Math.round(tongCSTG * 1000) / 1000;

			respone.setSumCSCP(tongCSCP);
			respone.setSumCSDM(tongCSDM);
			respone.setSumCSTG(tongCSTG);

			respone.setData(schedules);
		} else if (StringUtils.equals(typeScrop, "3")) {

			for (int i = 0; i < projects.size(); i++) {
				Project project = projects.get(i);

				Schedule schedule = new Schedule();
				schedule.setStt(project.getProjectId());
				schedule.setAddRess(project.getProjectName());

				Map<String, String> condition = new HashMap<String, String>();
				condition.put("projectId", String.valueOf(project.getProjectId()));
				condition.put("deviceType", contantDeviceType);
				List<Device> devices = deviceMapper.getDeviceByProjectId(condition);

				if (devices.size() > 0) {
					double sumAcPower = (double) Math
							.round((devices.stream().mapToDouble(x -> x.getAcPower() == null ? 0 : x.getAcPower()).sum()
									/ 1000000) * 1000)
							/ 1000;
					tongCSDM = (double) Math.round(sumAcPower * 1000) / 1000;
					schedule.setAcPower(sumAcPower);
				}

				schedules.add(schedule);

			}

			Double tongCSCP = tongCSDM - (double) Math.round(tongCSTG * 1000) / 1000;

			respone.setSumCSCP(tongCSCP);
			respone.setSumCSDM(tongCSDM);
			respone.setSumCSTG(tongCSTG);

			respone.setData(schedules);
		}

		return new ResponseEntity<DataSend>(respone, HttpStatus.OK);
	}

	@PostMapping(value = "/history/delete/{historyId}")
	public ResponseEntity<?> delete(@PathVariable("historyId") final int historyId) {
		List<Integer> deleteIdList = new ArrayList<Integer>();
		deleteIdList.add(historyId);
		Map<String, String> condition = new HashMap<String, String>();
		condition.put("historyId", String.valueOf(historyId));
		condition.put("parentId", String.valueOf(historyId));
		condition.put("typeScrop", "0");
		List<HistoryEVN> historyId1 = historyMapper.getHistorysParent(condition);
		historyMapper.delete(condition);

		if (historyId1.size() > 0) {
			for (int y = 0; y < historyId1.size(); y++) {
				deleteIdList.add(historyId1.get(y).getHistoryId());

				condition = new HashMap<String, String>();
				condition.put("typeScrop", "1");
				condition.put("parentId", String.valueOf(historyId1.get(y).getHistoryId()));
				condition.put("historyId", String.valueOf(historyId1.get(y).getHistoryId()));
				historyMapper.delete(condition);
				List<HistoryEVN> historyId2 = historyMapper.getHistorysParent(condition);

				if (historyId2.size() > 0) {
					for (int t = 0; t < historyId2.size(); t++) {
						deleteIdList.add(historyId2.get(t).getHistoryId());
						condition = new HashMap<String, String>();
						condition.put("typeScrop", "2");
						condition.put("parentId", String.valueOf(historyId2.get(t).getHistoryId()));
						condition.put("historyId", String.valueOf(historyId2.get(t).getHistoryId()));
						List<HistoryEVN> historyId3 = historyMapper.getHistorysParent(condition);
						historyMapper.delete(condition);
						if (historyId3.size() > 0) {
							for (int r = 0; r < historyId3.size(); r++) {
								deleteIdList.add(historyId3.get(r).getHistoryId());
								condition = new HashMap<String, String>();
								condition.put("typeScrop", "3");
								condition.put("parentId", String.valueOf(historyId3.get(r).getHistoryId()));
								condition.put("historyId", String.valueOf(historyId3.get(r).getHistoryId()));
								List<HistoryEVN> historyId4 = historyMapper.getHistorysParent(condition);
								historyMapper.delete(condition);
								if (historyId4.size() > 0) {
									for (int q = 0; q < historyId4.size(); q++) {
										deleteIdList.add(historyId4.get(q).getHistoryId());
										condition = new HashMap<String, String>();
										condition.put("typeScrop", "4");
										condition.put("parentId", String.valueOf(historyId4.get(q).getHistoryId()));
										condition.put("historyId", String.valueOf(historyId4.get(q).getHistoryId()));
										List<HistoryEVN> historyId5 = historyMapper.getHistorysParent(condition);
										historyMapper.delete(condition);
										if (historyId5.size() > 0) {
											for (int h = 0; h < historyId5.size(); h++) {
												deleteIdList.add(historyId5.get(h).getHistoryId());
												condition.put("historyId",
														String.valueOf(historyId5.get(h).getHistoryId()));
												historyMapper.delete(condition);
											}

										}
									}

								}

							}
						}

					}
				}

			}

			scheduleMapper.deleteHistoryIds(deleteIdList);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
