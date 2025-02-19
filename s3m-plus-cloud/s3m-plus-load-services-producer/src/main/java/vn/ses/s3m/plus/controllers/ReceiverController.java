package vn.ses.s3m.plus.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import vn.ses.s3m.plus.dto.Receiver;
import vn.ses.s3m.plus.service.ReceiverService;

@RestController
@RequestMapping ("/load")
@Slf4j
public class ReceiverController {

    @Autowired
    ReceiverService receiverService;

    static String topicName = "MQTT_UPDATE_RECEIVER";

    @Value ("${mqtt.server}")
    private String topicUrl;

    @Value ("${mqtt.user.name}")
    private String userName;

    @Value ("${mqtt.password}")
    private String password;

    @GetMapping ("/receivers")
    public ResponseEntity<?> getListReceiver(@RequestParam (value = "projectId") final String projectId,
        @RequestParam (value = "systemType") final String systemType) {

        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("projectId", projectId);
        condition.put("systemType", systemType);

        List<Receiver> receivers = receiverService.getListReceiver(condition);

        return new ResponseEntity<List<Receiver>>(receivers, HttpStatus.OK);
    }

    @PostMapping ("/receiver/add/{projectId}/{systemType}")
    public ResponseEntity<?> addNewReceiver(@RequestBody final Receiver receiver,
        @PathVariable (value = "projectId") final String projectId,
        @PathVariable (value = "systemType") final String systemType) {

        Map<String, Object> condition = new HashMap<>();
        condition.put("projectId", projectId);
        condition.put("systemType", systemType);
        condition.put("name", receiver.getName());
        condition.put("phone", receiver.getPhone());
        condition.put("email", receiver.getEmail());
        condition.put("description", receiver.getDescription());
        receiverService.addNewReceiver(condition);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping ("/receiver/update")
    public ResponseEntity<?> updateReceiver(@RequestBody final Receiver receiver) {

        receiverService.updateReceiver(receiver);

        /**
         * mesageReceiver: @ReceiverId*Phone*Email
         */
        String mesageReceiver = "@" + receiver.getReceiverId() + "*" + receiver.getPhone() + "*" + receiver.getEmail();
        callToReceiver(mesageReceiver);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping ("/receiver/delete")
    public ResponseEntity<?> deleteReceiver(@RequestParam (value = "receiverId") final String receiverId) {

        Map<String, Object> condition = new HashMap<>();
        condition.put("receiverId", receiverId);
        receiverService.deleteReceiver(condition);
        receiverService.deleteInfor(condition);

        /**
         * mesageReceiver: @ReceiverId*Phone*Email
         */
        String mesageReceiver = "@" + receiverId;
        callToReceiver(mesageReceiver);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping ("/receiver/save/{systemType}/{customerId}/{projectId}/{deviceId}/{receiverId}")
    ResponseEntity<?> saveWarningToSentGrid(@RequestBody String data, @PathVariable String systemType,
        @PathVariable String customerId, @PathVariable String projectId, @PathVariable String deviceId,
        @PathVariable String receiverId) {

        String warning1 = data.substring(1, data.length() - 1);
        String[] warnings = warning1.split(",");

        Map<String, Object> condition = new HashMap<>();
        condition.put("deviceId", deviceId);
        condition.put("receiverId", receiverId);
        receiverService.deleteInfor(condition);

        if (warning1.length() > 0) {
            for (int i = 0; i < warnings.length; i++) {
                condition.put("warningType", warnings[i]);
                receiverService.insertInforWarning(condition);
            }
        } else {
            warning1 = "#";
        }

        /**
         * mesageReceiver: @systemType*customerId*projectId*deviceId*receiverId*warning
         */
        String mesageReceiver = "@" + systemType + "*" + customerId + "*" + projectId + "*" + deviceId + "*"
            + receiverId + "*" + warning1;
        callToReceiver(mesageReceiver);

        return new ResponseEntity<String>(HttpStatus.OK);
    }

    @GetMapping ("/receiver/getWarning/{receiverId}/{deviceId}")
    public ResponseEntity<?> getWarningInfor(@PathVariable (value = "receiverId") final String receiverId,
        @PathVariable (value = "deviceId") final String deviceId) {

        Map<String, Object> condition = new HashMap<>();
        condition.put("deviceId", deviceId);
        condition.put("receiverId", receiverId);
        List<String> results = receiverService.getWarningsInfor(condition);

        return new ResponseEntity<List<String>>(results, HttpStatus.OK);
    }

    private void callToReceiver(final String setting) {
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
}
