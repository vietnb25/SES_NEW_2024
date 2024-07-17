package vn.ses.s3m.plus.controllers.load;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vn.ses.s3m.plus.client.LoadClient;
import vn.ses.s3m.plus.dto.Receiver;

@RestController
@RequestMapping ("/load")
public class ReceiverLoadController {

    @Autowired
    private LoadClient loadClient;

    /**
     * @author HoangLH
     * @since Apr 27, 2023
     * @param projectId
     * @param systemType
     * @return
     */
    @GetMapping ("/receivers")
    public ResponseEntity<?> getListReceiverLoad(@RequestParam (value = "projectId") final String projectId,
        @RequestParam (value = "systemType") final String systemType) {

        return loadClient.getListReceiverLoad(projectId, systemType);
    }

    /**
     *
     */
    @PostMapping ("/receiver/save/{systemType}/{customerId}/{projectId}/{deviceId}/{receiverId}")
    public ResponseEntity<?> saveWarningToSentLoad(@RequestBody String data, @PathVariable String systemType,
        @PathVariable String customerId, @PathVariable String projectId, @PathVariable String deviceId,
        @PathVariable String receiverId) {
        return loadClient.saveWarningToSentLoad(data, systemType, customerId, projectId, deviceId, receiverId);
    }

    /**
     *
     */
    @GetMapping ("/receiver/getWarning/{receiverId}/{deviceId}")
    public ResponseEntity<?> getWarningInforLoad(@PathVariable (value = "receiverId") final String receiverId,
        @PathVariable (value = "deviceId") final String deviceId) {
        return loadClient.getWarningInforLoad(receiverId, deviceId);
    }

    /**
     *
     */
    @PostMapping ("/receiver/add/{projectId}/{systemType}")
    public ResponseEntity<?> addNewReceiverLoad(@RequestBody final Receiver receiver,
        @PathVariable (value = "projectId") final String projectId,
        @PathVariable (value = "systemType") final String systemType) {

        return loadClient.addNewReceiverLoad(receiver, projectId, systemType);
    }

    /**
     *
     */
    @PostMapping ("/receiver/update")
    public ResponseEntity<?> updateReceiverLoad(@RequestBody final Receiver receiver) {

        return loadClient.updateReceiverLoad(receiver);
    }

    /**
     *
     */
    @GetMapping ("/receiver/delete")
    public ResponseEntity<?> deleteReceiverLoad(@RequestParam (value = "receiverId") final String receiverId) {

        return loadClient.deleteReceiverLoad(receiverId);
    }

}
