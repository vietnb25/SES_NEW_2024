package vn.ses.s3m.plus.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import vn.ses.s3m.plus.common.Constants;
import vn.ses.s3m.plus.common.Schema;
import vn.ses.s3m.plus.dto.Plan;
import vn.ses.s3m.plus.dto.Setting;
import vn.ses.s3m.plus.dto.SettingWarning;
import vn.ses.s3m.plus.service.SettingService;
import vn.ses.s3m.plus.service.SettingWarningService;

/**
 * Controller xử lý cài đặt
 *
 * @author Wasiq Bhamla
 * @since 13 thg 1, 2023
 */
@RestController
@RequestMapping("/common/setting")
@Slf4j
public class SettingController {

	@Autowired
	private SettingService settingService;

	@Autowired
	private SettingWarningService service;

	static String topicName = "MQTT_UPDATE_SETTING";

	@Value("${mqtt.server}")
	private String topicUrl;

	@Value("${mqtt.user.name}")
	private String userName;

	@Value("${mqtt.password}")
	private String password;

	/**
	 * Lấy ra danh sách cài đặt.
	 *
	 * @param projectId  Id dự án.
	 * @param customerId Id khách hàng.
	 * @return Danh sách cài đặt.
	 */
	@GetMapping("/")
	public ResponseEntity<List<Setting>> getListSetting(@RequestParam final String projectId,
			@RequestParam final String customerId, @RequestParam final Integer type) {
		log.info("getListSetting START");

		Map<String, String> condition = new HashMap<String, String>();
		if (projectId != null && customerId != null) {
			condition.put("projectId", projectId);
			condition.put("customerId", customerId);
			condition.put("type", String.valueOf(type));

			List<Setting> listSettings = settingService.getSettings(condition);

			log.info("getListSetting END");

			return new ResponseEntity<List<Setting>>(listSettings, HttpStatus.OK);
		} else {

			log.info("getListSetting END");

			return new ResponseEntity<List<Setting>>(HttpStatus.OK);
		}

	}

	/**
	 * Lấy ra thông tin cài đặt theo Id
	 *
	 * @param settingId Id cài đặt.
	 * @return Trả về thông tin cài đặt được lấy theo id.
	 */
	@GetMapping("/{settingId}")
	public ResponseEntity<Setting> getSettingById(@PathVariable final Integer settingId) {

		log.info("SettingController.getSettingById START");

		Setting setting = settingService.getSetting(settingId);

		String[] settingValue = setting.getSettingValue().split(Constants.ES.COMMA_CHARACTER);

		if (settingValue.length > 1) {
			setting.setSettingValue(settingValue[0]);
			String settingValue2 = settingValue[1];

			setting.setSettingValue2(settingValue2);
		}

		log.info("END");

		return new ResponseEntity<Setting>(setting, HttpStatus.OK);
	}

	/**
	 * Chỉnh sửa thông tin cài đặt
	 *
	 * @param settingId Id cài đặt.
	 * @param setting   Đối tượng cài đặt truyền vào.
	 * @return Trả về giá trị cài đặt sau khi chỉnh sửa.
	 * @return Trả về mã lỗi 400.
	 */
	@PutMapping("/update/{settingId}")
	public ResponseEntity<?> editSetting(@PathVariable final Integer settingId, @RequestBody final Setting setting) {

		log.info("SettingController.editSetting START");

		try {

			if (StringUtils.isNotEmpty(String.valueOf(settingId))) {
				Map<String, String> condition = new HashMap<String, String>();
				condition.put("settingId", String.valueOf(settingId));
				String description = "";

				switch (setting.getSettingMstId()) {
				case 1:
					description = "Ua || Ub || Uc > " + setting.getSettingValue();
					break;
				case 2:
					description = "Ua || Ub || Uc < " + setting.getSettingValue();
					break;
				case 3:
					description = "Nhiệt độ ngoài trời || Nhiệt độ thiết bị > " + setting.getSettingValue() + " °C";
					break;
				case 4:
					description = "((Ia + Ib + Ic)/3) / (CongSuatThietBi*1.44) > " + setting.getSettingValue()
							+ " & cosA || cosB || cosC < " + setting.getSettingValue2();
					break;
				case 5:
					description = "Ia || Ib || Ic >= " + setting.getSettingValue() + "*Imccb";
					break;
				case 6:
					description = "F < " + setting.getSettingValue();
					break;
				case 7:
					description = "F > " + setting.getSettingValue();
					break;
				case 8:
					description = "Ua || Ub || Uc < " + setting.getSettingValue();
					break;
				case 9:
					description = "((Ia + Ib + Ic)/3) / (CongSuatThietBi*1.44) > " + setting.getSettingValue()
							+ " & (Imax – Imin)/Imin > " + setting.getSettingValue2();
					break;
				case 10:
					description = "cosA || cosB || cosC < " + setting.getSettingValue();
					break;
				case 11:
					description = "H_iA, iB, iC, uA-N, uB-N, uC-N > " + setting.getSettingValue() + " (%)";
					break;
				case 12:
					description = "THD_VA-N|| THD_VB-N || THD_VC-N > " + setting.getSettingValue() + " (%)";
					break;
				case 13:
					description = "In > Icap x " + setting.getSettingValue();
					break;
				case 14:
					description = null;
					break;
				case 15:
					description = null;
					break;
				case 16:
					description = null;
					break;
				case 17:
					description = "Umax - Umin > " + setting.getSettingValue() + " (V) & (UA,B,C > "
							+ setting.getSettingValue2() + " (V))";
					break;
				case 18:
					description = setting.getSettingValue();
					break;
				case 19:
					description = setting.getSettingValue();
					break;
				case 20:
					description = setting.getSettingValue();
					break;
				case 21:
					description = setting.getSettingValue();
					break;
				case 22:
					description = setting.getSettingValue();
					break;
				case 23:
					description = setting.getSettingValue();
					break;
				case 24:
					description = setting.getSettingValue();
					break;
				case 25:
					description = setting.getSettingValue();
					break;
				case 26:
					description = setting.getSettingValue();
					break;
				case 27:
					description = setting.getSettingValue();
					break;
				case 28:
					description = setting.getSettingValue();
					break;
				case 29:
					description = setting.getSettingValue();
					break;
				case 30:
					description = setting.getSettingValue();
					break;
				case 31:
					description = setting.getSettingValue();
					break;
				case 32:
					description = setting.getSettingValue();
					break;
				case 33:
					description = setting.getSettingValue();
					break;
				case 34:
					description = "Ua || Ub || Uc > " + setting.getSettingValue();
					break;
				case 35:
					description = "Ua || Ub || Uc < " + setting.getSettingValue();
					break;
				case 36:
					description = "Nhiệt độ ngoài trời || Nhiệt độ thiết bị > " + setting.getSettingValue() + " °C";
					break;
				case 37:
					description = "SAW_ID1 || SAW_ID2...|| SAW_ID6 > " + setting.getSettingValue() + " °C";
					break;
				case 38:
					description = "((Ia + Ib + Ic)/3) / (CongSuatThietBi*1.44) > " + setting.getSettingValue()
							+ " & cosA || cosB || cosC < " + setting.getSettingValue2();
					break;
				case 39:
					description = "Ia || Ib || Ic >= " + setting.getSettingValue() + " * Imccb";
					break;
				case 40:
					description = " F < " + setting.getSettingValue();
					break;
				case 41:
					description = "F > " + setting.getSettingValue();
					break;
				case 42:
					description = "Ua || Ub || Uc < " + setting.getSettingValue();
					break;
				case 43:
					description = "((Ia + Ib + Ic)/3) / (CongSuatThietBi*1.44) > " + setting.getSettingValue()
							+ " & (Imax – Imin)/Imin > " + setting.getSettingValue2();
					break;
				case 44:
					description = "cosA || cosB || cosC < " + setting.getSettingValue();
					break;
				case 45:
					description = "THD_VA-N|| THD_VB-N || THD_VC-N > " + setting.getSettingValue() + " (%)";
					break;
				case 46:
					description = setting.getSettingValue();
					break;
				case 47:
					description = "H > " + setting.getSettingValue() + " %";
					break;
				case 48:
					description = "T > " + setting.getSettingValue() + " °C";
					break;
				case 49:
					description = "Giá điện giờ thấp điểm: " + setting.getSettingValue();
					break;
				case 50:
					description = "Giá điện giờ trung bình: " + setting.getSettingValue();
					break;
				case 51:
					description = "Giá điện giờ cao điểm: " + setting.getSettingValue();
					break;
				case 52:
					description = "Tiền bán điện giờ thấp điểm: " + setting.getSettingValue();
					break;
				case 53:
					description = "Tiền bán điện trung bình: " + setting.getSettingValue();
					break;
				case 54:
					description = "Tiền bán điện cao điểm: " + setting.getSettingValue();
					break;
				default:
					break;
				}
				if (StringUtils.isNotEmpty(setting.getSettingValue2())) {
					setting.setSettingValue(setting.getSettingValue().concat(",").concat(setting.getSettingValue2()));
				}

				condition.put("settingValue", setting.getSettingValue());

				condition.put("description", description);

				condition.put("warningLevel", String.valueOf(setting.getWarningLevel()));

				settingService.updateSetting(condition);

				String value2 = setting.getSettingValue2() != null ? setting.getSettingValue2() : "#";

				/**
				 * settingMessage: @systemType*customerId*projectId*settingId*settingValue1*settingValue2
				 */
				String settingMessage = "@" + setting.getType() + "*" + setting.getCustomerId() + "*"
						+ setting.getProjectId() + "*" + settingId + "*" + setting.getSettingValue() + "*" + value2;
				callToSetingWarning(settingMessage);
			}
			log.info("END");

			return new ResponseEntity<Setting>(setting, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			log.info("END");

			return new ResponseEntity<Object>(HttpStatus.BAD_REQUEST);
		}

	}

	private void callToSetingWarning(final String setting) {
		String content = setting;
		String clientId = "C" + 1;
		MemoryPersistence persistence = new MemoryPersistence();

		try {
			MqttClient client = new MqttClient(topicUrl, clientId, persistence);

			MqttConnectOptions connOpts = new MqttConnectOptions();
			connOpts.setCleanSession(true);
			connOpts.setUserName(userName);
			connOpts.setPassword(password.toCharArray());

			log.info("Connecting to broker: " + topicUrl);

			client.connect(connOpts);

			log.info("Connected");

			log.info("Publishing message: " + content);

			MqttMessage message = new MqttMessage(content.getBytes());
			message.setQos(2);

			client.publish(topicName, message);
			log.info("Message published");

			client.disconnect();
			log.info("Disconnected");

		} catch (MqttException me) {
			log.error("reason " + me.getReasonCode());
			log.error("msg " + me.getMessage());
			log.error("loc " + me.getLocalizedMessage());
			log.error("cause " + me.getCause());
			log.error("excep " + me);
			me.printStackTrace();
		}
	}

	@GetMapping("/settings-by-devices")
	public ResponseEntity<List<Setting>> getSettingByDeviceIds(@RequestParam("devices") String devices) {
		log.info("SettingController.getSettingById START");
		Map<String, String> condition = new HashMap<>();
		if (devices.equals("") || devices == null) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
		condition.put("deviceIds", devices);
		List<Setting> ls = this.settingService.getSettingByDeviceIds(condition);

		log.info("END");

		return new ResponseEntity<List<Setting>>(ls, HttpStatus.OK);
	}

	@GetMapping("/settings-by-device-type")
	public ResponseEntity<List<SettingWarning>> getSettingByDeviceType(@RequestParam("customer") String customer,
			@RequestParam("systemType") String systemType,
			@RequestParam(value = "project", required = false) String project,
			@RequestParam("deviceType") String deviceType, @RequestParam("deviceId") String deviceId) {
		log.info("SettingController.getByDeviceType START");
		Map<String, Object> condition = new HashMap<>();
		if (deviceType.equals("") || deviceType == null) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
		condition.put("customer", customer);
		condition.put("systemType", systemType);
		condition.put("project", project);
		condition.put("deviceType", deviceType);
		condition.put("deviceId", deviceId);
		List<SettingWarning> ls = this.service.getSettingWarningByDeviceType(condition);

		log.info("END");

		return new ResponseEntity<List<SettingWarning>>(ls, HttpStatus.OK);
	}

	@PutMapping("/update-by-devices")
	public ResponseEntity<?> updateSetting(@RequestBody SettingWarning st, @RequestParam("devices") String devices) {
		if (st.getWarningTypeId() == null || devices.equals("") || devices == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		Map<String, String> con = new HashMap<>();
		con.put("stValue", st.getSettingValue());
		con.put("sDescription", st.getDescription());
		con.put("stLevel", String.valueOf(st.getWarningLevel()));
		con.put("warningType", String.valueOf(st.getWarningTypeId()));
		String[] str = devices.split(",");
		for (String device : str) {
			con.put("device", device);
			this.service.updateSettingValue(con);
			SettingWarning setting = this.service.getSettingByDeviceAndWarningType(con);
			String settingMessage = "@" + setting.getType() + "*" + setting.getCustomerId() + "*"
					+ setting.getProjectId() + "*" + setting.getSettingId() + "*" + st.getSettingValue();
			callToSetingWarning(settingMessage);
		}

//

		return new ResponseEntity<>(HttpStatus.OK);
	}

	// Lấy setting value history
	@GetMapping("/getSettingHistory")
	public ResponseEntity<List<Setting>> getSettingHistory(@RequestParam("customerId") final Integer customerId,
			@RequestParam(value = "projectId", required = false) final Integer projectId,
			@RequestParam(value = "fromDate") final String fromDate,
			@RequestParam(value = "toDate") final String toDate,
			@RequestParam(value = "deviceId") final Integer deviceId,
			@RequestParam(value = "warningType", required = false) final String warningType) {
		Map<String, Object> condition = new HashMap<>();
		String schema = Schema.getSchemas(customerId);
		condition.put("schema", schema);
		condition.put("fromDate", fromDate);
		condition.put("toDate", toDate);
		condition.put("deviceId", deviceId);
		if (projectId != null) {
			condition.put("projectId", projectId);
		}
		if (warningType != null && warningType.compareTo("ALL") != 0) {
			condition.put("warningType", warningType);
		}
		List<Setting> settingList = settingService.getSettingHistory(condition);
		System.out.println("condition listsetting history" + condition);
		return new ResponseEntity<List<Setting>>(settingList, HttpStatus.OK);

	}

	@GetMapping("/getSettingHistory1")
	public ResponseEntity<List<Setting>> getSettingHistory(@RequestParam final String projectId,
			@RequestParam final String customerId,
			@RequestParam(value = "warningType", required = false) final String warningType) {
		Map<String, Object> condition = new HashMap<>();
		if (projectId != null && customerId != null) {
			condition.put("projectId", projectId);
			condition.put("customerId", customerId);
			if (warningType != null && warningType.compareTo("ALL") != 0) {
				condition.put("warningType", warningType);
			}
			List<Setting> listSettings = settingService.getSettingHistory(condition);

			log.info("getListSetting END");

			return new ResponseEntity<List<Setting>>(listSettings, HttpStatus.OK);
		} else {

			log.info("getListSetting END");

			return new ResponseEntity<List<Setting>>(HttpStatus.OK);
		}

	}

}
