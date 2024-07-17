package vn.ses.s3m.plus.service;

import java.util.List;
import java.util.Map;

import vn.ses.s3m.plus.dto.Customer;
import vn.ses.s3m.plus.dto.OtpData;
import vn.ses.s3m.plus.dto.Project;

public interface CustomerService {

    List<Customer> getListCustomer();

    void addCustomer(Map<String, String> conditon);

    Customer getCustomerMax(Map<String, Object> condition);

    void updateCustomer(Customer condition);

    Customer getCustomer(Map<String, String> condition);

    void deleteCustomer(Map<String, Object> condition);

    List<Customer> searchCustomer(Map<String, String> condition);

    List<Customer> getListCustomer(Map<String, String> condtion);

    List<Customer> getCustomerByCode(Map<String, String> condition);

    boolean checkCustomerToDelete(Map<String, Object> condition);

    List<Customer> getProjectsByCustomerId(Map<String, Object> condition);

    Long getPowerByCustomerId(Integer customerId);

    List<Project> getProjects(Map<String, Object> condition);

    void insertOtp(OtpData data);

    OtpData getOtpInfor(OtpData data);

    void updateStatusOtp(OtpData data);

    void addSchema(Map<String, Object> condition);

    Customer getCustomerByProjectId(Map<String, String> condtion);

    List<Customer> getCustomers();

    Customer getCustomerIdFirstTime();

    String getListCustomerIds(String userName);

    List<Customer> getListCus(String ids);

}
