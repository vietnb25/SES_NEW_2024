package vn.ses.s3m.plus.controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import vn.ses.s3m.plus.common.Constants;
import vn.ses.s3m.plus.dto.Cable;
import vn.ses.s3m.plus.form.CableForm;
import vn.ses.s3m.plus.response.CableResponse;
import vn.ses.s3m.plus.service.CableService;

/**
 * Controller xử lí danh mục cáp.
 *
 * @author Arius Vietnam JSC
 * @since 2022-01-01
 */
@RestController
@RequestMapping ("/common/cable")
@Validated
@Slf4j
public class CableController {

    @Autowired
    private CableService cableService;

    /**
     * Lấy ra sách dây cáp.
     *
     * @return Danh sách dây cáp.
     */
    @GetMapping ("/list")
    public ResponseEntity<List<CableResponse>> getListCable() {

        log.info("vn.ses.s3m.plus.controllers.CableController.getListCable START");

        List<Cable> listCables = cableService.getCables();
        List<CableResponse> cableResponses = new ArrayList<>();
        for (Cable cable : listCables) {
            CableResponse cables = new CableResponse(cable);
            cableResponses.add(cables);
        }

        log.info("END");

        return new ResponseEntity<List<CableResponse>>(cableResponses, HttpStatus.OK);
    }

    /**
     * Thêm mới thông tin dây cáp.
     *
     * @param cableForm Đối tượng cáp nhập vào.
     * @return Đối tượng cáp được thêm vào.
     */
    @PostMapping ("/add")
    public ResponseEntity<?> createCable(@Valid @RequestBody final CableForm cableForm) {

        log.info("vn.ses.s3m.plus.controllers.CableController.createCable START");

        List<String> error = new ArrayList<>();
        Cable cableByCableName = cableService.getCableByCableName(cableForm.getCableName());
        if (cableByCableName != null) {
            error.add(Constants.CableValidation.CABLE_NAME_EXIST);
        }

        if (error.size() > 0) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", org.apache.http.HttpStatus.SC_BAD_REQUEST);
            response.put("errors", error);
            response.put("timestamp", new Date());

            return new ResponseEntity<Object>(response, HttpStatus.BAD_REQUEST);
        }

        Cable cable = new Cable();
        BeanUtils.copyProperties(cableForm, cable);
        cableService.insertCable(cable);

        log.info("END");

        return new ResponseEntity<Cable>(cable, HttpStatus.OK);
    }

    /**
     * Lấy thông tin cáp theo id cáp.
     *
     * @param cableId Mã cáp.
     * @return Đối tượng cáp được lấy ra theo id.
     */
    @GetMapping ("/{cable_id}")
    public ResponseEntity<Cable> getCableById(@PathVariable ("cable_id") final int cableId) {

        log.info("vn.ses.s3m.plus.controllers.CableController.getCableById START");

        Cable cabl = cableService.getCableById(cableId);

        log.info("END");

        return new ResponseEntity<Cable>(cabl, HttpStatus.OK);
    }

    /**
     * Chỉnh sửa thông tin cáp.
     *
     * @param cableId Mã cáp.
     * @param cableForm Đối tượng cáp truyền vào.
     * @return Đối tượng cáp được chỉnh sửa.
     */
    @PutMapping ("update/{cable_id}")
    public ResponseEntity<?> editCable(@Valid @PathVariable ("cable_id") final int cableId,
        @RequestBody final CableForm cableForm) {

        log.info("vn.ses.s3m.plus.controllers.CableController.editCable START");
        Cable cable = new Cable();
        BeanUtils.copyProperties(cableForm, cable);

        // cab là đối tượng cáp lấy theo id
        Cable cab = cableService.getCableById(cableId);

        cab.setCableId(cable.getCableId());
        cab.setCableName(cable.getCableName());
        cab.setCurrent(cable.getCurrent());
        cab.setDescription(cable.getDescription());

        cableService.updateCable(cab);

        log.info("END");

        return new ResponseEntity<Cable>(cable, HttpStatus.OK);
    }

    /**
     * Xóa thông tin cáp.
     *
     * @param cableId Mã cáp.
     * @return Trả về mã trạng thái (200: thành công).
     */
    @DeleteMapping ("/delete/{cable_id}")
    public ResponseEntity<Void> delete(@PathVariable ("cable_id") final int cableId) {

        log.info("vn.ses.s3m.plus.controllers.CableController.delete START");

        cableService.deleteCable(cableId);

        log.info("END");

        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    /**
     * Tìm kiếm thông tin cáp.
     *
     * @param keyword Từ khóa tìm kiếm.
     * @return Danh sách cáp được tìm kiếm.
     */
    @GetMapping ("/search")
    public ResponseEntity<List<CableResponse>> search(@RequestParam ("keyword") final String keyword) {

        log.info("vn.ses.s3m.plus.controllers.CableController.search START");

        List<Cable> listCables = cableService.searchCables(keyword);
        List<CableResponse> cableResponses = new ArrayList<>();
        for (Cable cable : listCables) {
            CableResponse cables = new CableResponse(cable);
            cableResponses.add(cables);
        }

        log.info("END");

        return new ResponseEntity<List<CableResponse>>(cableResponses, HttpStatus.OK);
    }
}
