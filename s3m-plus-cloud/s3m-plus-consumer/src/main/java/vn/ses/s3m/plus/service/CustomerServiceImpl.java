package vn.ses.s3m.plus.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.ses.s3m.plus.dao.CustomerMapper;
import vn.ses.s3m.plus.dto.Customer;
import vn.ses.s3m.plus.dto.OtpData;
import vn.ses.s3m.plus.dto.Project;

/**
 * Service xử lý thông tin khách hàng.
 *
 * @author Arius Vietnam JSC
 * @since 2022-01-01
 */
@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerMapper customerMapper;

    /**
     * Lấy danh sách khách hàng từ DB.
     *
     * @param
     * @return List<Customer>
     */
    @Override
    public List<Customer> getListCustomer() {
        return customerMapper.getCustomers();
    }

    /**
     * Thêm khách hàng vào DB.
     *
     * @param condition Map chứa thông tin khách hàng cần thêm
     */
    @Override
    public void addCustomer(final Map<String, String> condition) {
        customerMapper.addCustomer(condition);
    }

    /**
     * Lấy khách hàng theo Id từ DB.
     *
     * @param condition: Map chứa ID khách hàng
     * @return Customer
     */
    @Override
    public Customer getCustomer(final Map<String, String> condition) {
        return customerMapper.getCustomer(condition);
    }

    /**
     * Cập nhật thông tin khách hàng.
     *
     * @param condition Khách hàng
     * @return
     */
    @Override
    public void updateCustomer(final Customer condition) {
        customerMapper.updateCustomer(condition);
    }

    /**
     * Xóa khách hàng theo Id.
     *
     * @param condition: Map chứa thông tin ID khách hàng
     * @return
     */
    @Override
    public void deleteCustomer(final Map<String, Object> condition) {
        customerMapper.deleteCustomer(condition);
    }

    /**
     * Tìm kiếm khách hàng theo từ khóa.
     *
     * @param condition: Map chứa từ khóa
     * @return List<Customer>
     */
    @Override
    public List<Customer> searchCustomer(final Map<String, String> condition) {
        return customerMapper.searchCustomer(condition);
    }

    /**
     * Lấy Customer theo điều kiện.
     *
     * @param condition Điều kiện lấy Customer
     * @return Danh sách Customer
     */
    @Override
    public List<Customer> getListCustomer(final Map<String, String> condtion) {
        return customerMapper.getListCustomer(condtion);
    }

    /**
     * Lấy Customer theo điều kiện.
     *
     * @param condition Điều kiện lấy Customer
     * @return Danh sách Customer
     */
    @Override
    public List<Customer> getCustomerByCode(final Map<String, String> condition) {
        return customerMapper.getCustomerByCode(condition);
    }

    /**
    *
    */
    @Override
    public boolean checkCustomerToDelete(final Map<String, Object> condition) {
        // CHECKSTYLE:OFF
        if (customerMapper.checkCustomerIdInUserTable(condition)
            .size() > 0
            || customerMapper.checkCustomerIdInProjectTable(condition)
                .size() > 0) {
            // CHECKSTYLE:ON
            return false;
        } else {
            return true;
        }
    }

    /**
     * Lấy tổng công suất theo khách hàng
     *
     * @param customerId Mã khách hàng
     * @return Tổng công suất theo khách hàng
     */
    @Override
    public Long getPowerByCustomerId(final Integer customerId) {
        return customerMapper.getPowerByCustomerId(customerId);
    }

    /**
     * Lấy schema khách hàng mới nhất
     */
    @Override
    public Customer getCustomerMax(final Map<String, Object> condition) {
        return customerMapper.getCustomerMax(condition);
    }

    /**
     *
     */
    @Override
    public List<Customer> getProjectsByCustomerId(final Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return customerMapper.checkCustomerIdInProjectTable(condition);
    }

    /**
     *
     */
    @Override
    public List<Project> getProjects(final Map<String, Object> condition) {
        // TODO Auto-generated method stub
        return customerMapper.getProjectsByCustomerId(condition);
    }

    /**
     *
     */
    @Override
    public void insertOtp(final OtpData data) {
        // TODO Auto-generated method stub
        customerMapper.insertOtp(data);
    }

    /**
     *
     */
    @Override
    public OtpData getOtpInfor(final OtpData data) {
        // TODO Auto-generated method stub
        return customerMapper.getOtpInfor(data);
    }

    /**
     *
     */
    @Override
    public void updateStatusOtp(final OtpData data) {
        // TODO Auto-generated method stub
        customerMapper.updateStatusOtp(data);
    }

    /**
     * Thêm schema cho khách hàng
     */
    @Override
    public void addSchema(Map<String, Object> condition) {
        customerMapper.addSchema(condition);
    }

    @Override
    public Customer getCustomerByProjectId(Map<String, String> condtion) {
        return customerMapper.getCustomerByProjectId(condtion);
    }

    @Override
    public List<Customer> getCustomers() {
        return customerMapper.getCustomers();
    }

    @Override
    public Customer getCustomerIdFirstTime() {
        return customerMapper.getCustomerIdFirstTime();
    }

    @Override
    public String getListCustomerIds(String userName) {
        return customerMapper.getListCustomerIds(userName);
    }

    @Override
    public List<Customer> getListCus(String ids) {
        return customerMapper.getListCus(ids);
    }

}
