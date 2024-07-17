package vn.ses.s3m.plus.controllers;

import java.time.Year;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.validation.Valid;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vn.ses.s3m.plus.common.Constants;
import vn.ses.s3m.plus.common.DateUtils;
import vn.ses.s3m.plus.common.Schema;
import vn.ses.s3m.plus.common.SmsConsumer;
import vn.ses.s3m.plus.dto.Customer;
import vn.ses.s3m.plus.dto.OtpData;
import vn.ses.s3m.plus.dto.User;
import vn.ses.s3m.plus.form.CustomerForm;
import vn.ses.s3m.plus.response.CustomerResponse;
import vn.ses.s3m.plus.service.CustomerService;
import vn.ses.s3m.plus.service.UserService;

/**
 * Controller xử lý thông tin khách hàng.
 *
 * @author Arius Vietnam JSC
 * @since 2022-01-01
 */
@RestController
@RequestMapping ("/common/customer")
@Validated
public class CustomerController {
    private static final int MAX_LENGTH_NAME = 255;

    private static final int MAX_LENGTH_DESC = 1000;

    private static final int MAX_LENGTH_CODE = 100;

    private static final int MAX_RANDOM = 999999;

    private static final int CHECK_FAILE = 400;

    @Value ("${spring.datasource.url}")
    private String url;

    @Value ("${spring.datasource.password}")
    private String password;

    @Value ("${spring.datasource.username}")
    private String username;

    @Value ("${SMS_BASE_URL}")
    private String smsBaseUrl;

    @Value ("${SMS_MESSAGE}")
    private String smsMessage;

    // Logging
    private final Log log = LogFactory.getLog(CustomerController.class);

    @Autowired
    private CustomerService customerService;

    @Autowired
    private UserService userService;

    /**
     * Lấy danh sách khách hàng từ DB.
     *
     * @return Danh sách khách hàng.
     */
    @GetMapping ("/list")
    public ResponseEntity<List<CustomerResponse>> getListCustomer() {

        log.info("getListCustomer START");
        List<Customer> customers = customerService.getListCustomer();
        List<CustomerResponse> customerResponses = new ArrayList<>();
        for (Customer customer : customers) {
            CustomerResponse customerRes = new CustomerResponse(customer);
            customerResponses.add(customerRes);
        }
        log.info("getListCustomer END");
        return new ResponseEntity<List<CustomerResponse>>(customerResponses, HttpStatus.OK);
    }

    /**
     * Kiểm tra tồn tại của mã khách hàng.
     *
     * @param information thông tin khách hàng cần kiểm tra
     * @return true: nếu mã khách hàng đã tồn tại, flase: nếu mã khách hàng chưa tồn tại
     */
    @PostMapping ("/check")
    public ResponseEntity<?> checkExistedCustomerCode(@RequestBody final Customer information) {

        log.info("checkExistedCustomerCode START");
        List<Customer> customers = customerService.getListCustomer();
        for (Customer customer : customers) {
            // CHECKSTYLE:OFF
            if (information.getCustomerId() != null) {
                if (customer.getCustomerId() != Integer.valueOf(information.getCustomerId())
                    && customer.getCustomerCode()
                        .equals(information.getCustomerCode())) {
                    // CHECKSTYLE:ON
                    log.info("checkExistedCustomerCode END");
                    return new ResponseEntity<Boolean>(true, HttpStatus.OK);
                }
            } else if (customer.getCustomerCode()
                .equals(information.getCustomerCode())) {
                log.info("checkExistedCustomerCode END");
                return new ResponseEntity<Boolean>(true, HttpStatus.OK);
            }
        }
        log.info("checkExistedCustomerCode END");
        return new ResponseEntity<Boolean>(false, HttpStatus.OK);
    }

    /**
     * Tìm khách hàng từ DB.
     *
     * @param keyword từ khóa tìm kiếm theo tên hoặc mã KH
     * @return Khách hàng có từ khóa phù hợp.
     */
    @GetMapping ("/search/{keyword}")
    public ResponseEntity<List<CustomerResponse>> searchCustomer(@PathVariable final String keyword) {

        log.info("searchCustomer START");
        Map<String, String> condition = new HashMap<String, String>();
        condition.put("keyword", keyword);
        List<Customer> customers = customerService.searchCustomer(condition);
        List<CustomerResponse> customerResponses = new ArrayList<>();
        for (Customer customer : customers) {
            CustomerResponse customerRes = new CustomerResponse(customer);
            customerResponses.add(customerRes);
        }
        log.info("searchCustomer END");
        return new ResponseEntity<List<CustomerResponse>>(customerResponses, HttpStatus.OK);
    }

    /**
     * Thêm khách hàng vào DB.
     *
     * @param newCustomer Khách hàng
     * @return Thông báo kết quả thêm mới KH (200: Thành công, Other: Thất bại).
     */
    @PostMapping ("/add")
    public ResponseEntity<?> saveCustomer(@Valid @RequestBody final CustomerForm newCustomer) {

        log.info("saveCustomer START");
        Map<String, String> conditionCheck = new HashMap<>();
        conditionCheck.put("customerId", String.valueOf(newCustomer.getCustomerId()));
        conditionCheck.put("customerCode", newCustomer.getCustomerCode());
        List<String> errors = new ArrayList<>();
        List<Customer> checkCustomers = customerService.getCustomerByCode(conditionCheck);

        // Kiểm tra mã khách hàng đã tồn tại chưa
        if (checkCustomers.size() > 0) {
            errors.add(Constants.CustomerValidate.CUSTOMER_CODE_EXISTED);
            Map<String, Object> response = new HashMap<>();
            response.put("errors", errors);
            log.info("saveCustomer END");
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
        }

        String customerName = newCustomer.getCustomerName();
        String customerDesc = newCustomer.getDescription();
        String customerCode = newCustomer.getCustomerCode();
        // CHECKSTYLE:OFF
        if (customerName != null && customerCode != null && customerName.length() > 0
            && customerName.length() < MAX_LENGTH_NAME && customerDesc.length() < MAX_LENGTH_DESC
            && customerCode.length() < MAX_LENGTH_CODE) {
            // CHECKSTYLE:ON
            Map<String, String> condition = new HashMap<String, String>();
            condition.put("customerName", customerName);
            condition.put("customerDesc", customerDesc);
            condition.put("customerCode", customerCode);
            customerService.addCustomer(condition);

            // Thêm schema cho khách hàng
            Map<String, Object> conditionSchema = new HashMap<>();
            Customer c = customerService.getCustomerMax(conditionSchema);
            String schema = Schema.getSchemas(c.getIdSchema())
                .replace(".", "");

            Integer year = Year.now()
                .getValue();
            conditionSchema.put("year", year);
            conditionSchema.put("schema", schema);
            customerService.addSchema(conditionSchema);

            log.info("saveCustomer END");
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            log.info("saveCustomer END");
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Lấy thông tin KH theo Id.
     *
     * @param customerId Id khách hàng
     * @return Thông tin khách hàng
     */
    @GetMapping ("/{customerId}")
    public ResponseEntity<Customer> getCustomer(@PathVariable final String customerId) {

        log.info("getCustomer START");
        Map<String, String> condition = new HashMap<String, String>();
        condition.put("customerId", customerId);
        Customer customer = customerService.getCustomer(condition);
        log.info("getCustomer END");
        return new ResponseEntity<Customer>(customer, HttpStatus.OK);
    }

    /**
     * Chỉnh sửa thông tin khách hàng.
     *
     * @param customerId ID khách hàng
     * @param customer Thông tin khách hàng
     * @return Thông báo kết quả khi cập nhật thành công(200: thành công, other: thất bại)
     */
    @PostMapping ("/update/{customerId}")
    public ResponseEntity<?> updateCustomer(@PathVariable final String customerId,
        @Valid @RequestBody final CustomerForm customer) {

        log.info("updateCustomer START");
        Map<String, String> condition = new HashMap<>();
        condition.put("customerId", customerId);
        condition.put("customerCode", customer.getCustomerCode());
        List<String> errors = new ArrayList<>();
        List<Customer> checkCustomers = customerService.getCustomerByCode(condition);

        // Kiểm tra mã khách hàng đã tồn tại chưa
        if (checkCustomers.size() > 0) {
            errors.add(Constants.CustomerValidate.CUSTOMER_CODE_EXISTED);
            Map<String, Object> response = new HashMap<>();
            response.put("errors", errors);
            log.info("updateCustomer END");
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
        }

        if (customer.getCustomerName()
            // CHECKSTYLE:OFF
            .length() <= MAX_LENGTH_NAME
            && customer.getCustomerCode()
                .length() <= MAX_LENGTH_CODE
            && customer.getDescription()
                .length() <= MAX_LENGTH_DESC) {
            // CHECKSTYLE:ON
            try {
                Map<String, String> conditionGetCustomer = new HashMap<String, String>();
                conditionGetCustomer.put("customerId", customerId);
                Customer updateCustomer = customerService.getCustomer(conditionGetCustomer);
                updateCustomer.setCustomerId(customer.getCustomerId());
                updateCustomer.setCustomerName(customer.getCustomerName());
                updateCustomer.setDescription(customer.getDescription());
                updateCustomer.setCustomerCode(customer.getCustomerCode());
                customerService.updateCustomer(updateCustomer);
            } catch (Exception e) {
                e.printStackTrace();
            }
            log.info("updateCustomer END");
            return new ResponseEntity<String>("", HttpStatus.OK);
        } else {
            log.info("updateCustomer END");
            return new ResponseEntity<String>("", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Xóa khách hàng trong danh sách khách hàng.
     *
     * @param customerId ID khách hàng
     * @return Thông báo kết quả khi xóa thành công(200: thành công, other: thất bại)
     */
    @GetMapping ("/delete/{customerId}")
    public ResponseEntity<?> deleteCustomer(@PathVariable final String customerId) {

        log.info("confirmCustomer START");
        String id = customerId;
        if (id != null && id.length() > 0) {
            Map<String, Object> condition = new HashMap<String, Object>();
            condition.put("customerId", id);
            customerService.deleteCustomer(condition);
            return new ResponseEntity<String>(HttpStatus.OK);
        } else {
            log.info("confirmCustomer END");
            return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Lấy thông tin otp
     */
    @GetMapping ("/getOTP/{userName}/{customerId}")
    public ResponseEntity<?> sentOTP(@PathVariable final String userName, @PathVariable final String customerId) {

        User user = userService.getUserByUsername(userName);
        String phoneNumber = "84" + user.getPhone()
            .substring(1);
        OtpData data = new OtpData();
        data.setCustomerId(Integer.parseInt(customerId));
        data.setUserId(user.getId());
        data.setStatus(0);
        Random rnd = new Random();
        int number = rnd.nextInt(MAX_RANDOM);
        data.setOtpCode(number);
        customerService.insertOtp(data);

        String mOTP = String.format("%06d", number);
        String content = "Nhap ma OTP " + mOTP + " de xac nhan thay doi CSDL, het han sau 5 phut.";
        SmsConsumer.getInstance()
            .sendSms(content, smsMessage, phoneNumber, smsBaseUrl);
        return null;
    }

    /**
     * Check otp
     */
    @PostMapping ("/checkOTP")
    // CHECKSTYLE:OFF
    public ResponseEntity<?> checkOtp(@RequestBody final OtpData data) {

        OtpData dataCheck = customerService.getOtpInfor(data);
        if (dataCheck == null) {
            return new ResponseEntity<Boolean>(false, HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            Date now = new Date();
            Date timeOfOtp = DateUtils.toDate(dataCheck.getCreateDate(), Constants.ES.DATETIME_FORMAT_YMDHMS);
            if ( (now.getTime() - timeOfOtp.getTime()) / 1000 > 300) {
                customerService.updateStatusOtp(dataCheck);
                return new ResponseEntity<Boolean>(false, HttpStatus.INTERNAL_SERVER_ERROR);
            } else {
                if (data.getOtpCode()
                    .compareTo(dataCheck.getOtpCode()) != 0) {
                    return new ResponseEntity<Integer>(CHECK_FAILE, HttpStatus.INTERNAL_SERVER_ERROR);
                } else {
                    Map<String, Object> condition = new HashMap<String, Object>();
                    condition.put("customerId", data.getCustomerId());
                    customerService.deleteCustomer(condition);
                    customerService.updateStatusOtp(dataCheck);
                    return new ResponseEntity<Boolean>(true, HttpStatus.OK);
                }
            }
        }
    }
    // CHECKSTYL:ON

    /**
     * expire otp
     */
    @PostMapping ("/expireOTP")
    public ResponseEntity<?> expireOtp(@RequestBody final OtpData data) {

        OtpData dataCheck = customerService.getOtpInfor(data);
        if (dataCheck == null) {
            return new ResponseEntity<Boolean>(false, HttpStatus.OK);
        } else {
            customerService.updateStatusOtp(dataCheck);
            return new ResponseEntity<Boolean>(false, HttpStatus.OK);
        }
    }

    @GetMapping ("/listIds")
    public ResponseEntity<List<CustomerResponse>> getListCustomerIds(
        @RequestParam (value = "userName", required = true) final String userName) {

        log.info("getListCustomerIds START");
        List<CustomerResponse> customerResponses = new ArrayList<>();
        String customerIds = customerService.getListCustomerIds(userName);
        if (customerIds != null) {
            List<Customer> customers = customerService.getListCus(customerIds);
            for (Customer customer : customers) {
                CustomerResponse customerRes = new CustomerResponse(customer);
                customerResponses.add(customerRes);
            }
        }

        log.info("getListCustomerIds END");
        return new ResponseEntity<List<CustomerResponse>>(customerResponses, HttpStatus.OK);
    }

}
