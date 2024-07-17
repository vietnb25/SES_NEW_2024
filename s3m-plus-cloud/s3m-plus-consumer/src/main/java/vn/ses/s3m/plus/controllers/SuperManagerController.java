package vn.ses.s3m.plus.controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

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

import vn.ses.s3m.plus.common.Constants;
import vn.ses.s3m.plus.dto.SuperManager;
import vn.ses.s3m.plus.form.SuperManagerForm;
import vn.ses.s3m.plus.response.SuperManagerResponse;
import vn.ses.s3m.plus.service.SuperManagerService;

/**
 * Controller Xử lý khu vực/miền
 *
 * @author Arius Vietnam JSC
 * @since 2022-11-09
 */
@RestController
@RequestMapping ("/common/super-manager")
@Validated
public class SuperManagerController {

    @Autowired
    private SuperManagerService service;

    /**
     * Lấy danh sách khu vực
     *
     * @return Danh sách khu vực
     */
    @GetMapping ("/list")
    public ResponseEntity<List<SuperManagerResponse>> getListSuperManager() {

        List<SuperManagerResponse> dataRes = new ArrayList<>();

        List<SuperManager> data = service.getListSuperManager();
        for (SuperManager sm : data) {
            SuperManagerResponse smRes = new SuperManagerResponse(sm);
            dataRes.add(smRes);
        }

        return new ResponseEntity<List<SuperManagerResponse>>(dataRes, HttpStatus.OK);
    }

    /**
     * Tìm kiếm khu vực theo từ khóa
     *
     * @param keyword Từ khóa tìm kiếm
     * @return Danh sách khu vực
     */
    @GetMapping ("/search")
    public ResponseEntity<List<SuperManagerResponse>> getListSuperManagerByName(
        @RequestParam ("keyword") final String keyword) {

        List<SuperManagerResponse> dataRes = new ArrayList<>();

        List<SuperManager> data = service.getListSuperManagerByName(keyword);
        for (SuperManager sm : data) {
            SuperManagerResponse smRes = new SuperManagerResponse(sm);
            dataRes.add(smRes);
        }

        return new ResponseEntity<List<SuperManagerResponse>>(dataRes, HttpStatus.OK);
    }

    /**
     * Thêm mới khu vực
     *
     * @param superManager Đối tượng khu vực
     * @return Trạng thái thêm mới(200: Thành công, 400: Các lỗi thêm mới)
     */
    @PostMapping ("/add")
    public ResponseEntity<?> addSuperManager(@Valid @RequestBody final SuperManagerForm superManager) {

        List<String> errors = new ArrayList<>();
        SuperManager sm = service.getSuperManagerByName(superManager.getSuperManagerName());

        if (sm != null) {
            errors.add(Constants.SuperManagerValidation.SUPER_MANAGER_NAME_EXIST);
        }
        if (errors.size() > 0) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", org.apache.http.HttpStatus.SC_BAD_REQUEST);
            response.put("errors", errors);
            response.put("timestamp", new Date());

            return new ResponseEntity<Object>(response, HttpStatus.BAD_REQUEST);
        }

        service.addSuperManager(superManager);

        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    /**
     * Chi tiết khu vực/miền
     *
     * @param superManagerId Mã khu vực
     * @return Đối tượng khu vực
     */
    @GetMapping ("/{superManagerId}")
    public ResponseEntity<SuperManagerResponse> detailsSuperManager(
        @PathVariable ("superManagerId") final Long superManagerId) {

        SuperManager sm = service.getSuperManagerById(superManagerId);
        SuperManagerResponse smRes = new SuperManagerResponse(sm);

        return new ResponseEntity<SuperManagerResponse>(smRes, HttpStatus.OK);
    }

    /**
     * Cập nhật khu vực/miền
     *
     * @param superManager Đối tượng khu vực
     * @param superManagerId Mã khu vực
     * @return Trạng thái thêm mới(200: Thành công, 400: Các lỗi thêm mới)
     */
    @PutMapping ("/update/{superManagerId}")
    public ResponseEntity<?> updateSuperManager(@RequestBody final SuperManagerForm superManager,
        @PathVariable ("superManagerId") final Long superManagerId) {

        List<String> errors = new ArrayList<>();

        // Lấy data chưa chỉnh sửa
        SuperManager sm = service.getSuperManagerById(superManagerId);

        // kiểm tra tên khu vực mới đã tồn tại
        SuperManager smName = service.getSuperManagerByName(superManager.getSuperManagerName());

        // Check người dùng nếu chưa thay đổi tên người dùng
        if (smName != null && !sm.getSuperManagerName()
            .equals(smName.getSuperManagerName())) {
            errors.add(Constants.SuperManagerValidation.SUPER_MANAGER_NAME_EXIST);
        }
        if (errors.size() > 0) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", org.apache.http.HttpStatus.SC_BAD_REQUEST);
            response.put("errors", errors);
            response.put("timestamp", new Date());

            return new ResponseEntity<Object>(response, HttpStatus.BAD_REQUEST);
        }

        sm.setSuperManagerName(superManager.getSuperManagerName());
        sm.setLongitude(superManager.getLongitude());
        sm.setLatitude(superManager.getLatitude());
        sm.setDescription(superManager.getDescription());

        service.updateSuperManager(sm);

        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    /**
     * Xóa khu vực/miền
     *
     * @param superManagerId Mã khu vực
     * @return Trang thái(200: Thành công)
     */
    @DeleteMapping ("/delete/{superManagerId}")
    public ResponseEntity<Void> deleteSuperManager(@PathVariable ("superManagerId") final Long superManagerId) {
        service.deleteSuperManager(superManagerId);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    /**
     * Lấy ra khu vực theo khách hàng
     *
     * @param customerId Mã khách hàng
     * @return Danh sách khu vực được lấy ra theo khách hàng
     */
    @GetMapping ("/list/{customerId}")
    public ResponseEntity<List<SuperManager>> getManagerByCustomerId(
        @PathVariable ("customerId") final int customerId) {

        Map<String, String> condition = new HashMap<>();
        condition.put("customerId", String.valueOf(customerId));
        List<SuperManager> lisSuperManagers = null;

        return new ResponseEntity<List<SuperManager>>(lisSuperManagers, HttpStatus.OK);
    }

}
