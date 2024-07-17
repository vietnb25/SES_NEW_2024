package vn.ses.s3m.plus.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import vn.ses.s3m.plus.dto.Customer;
import vn.ses.s3m.plus.dto.OtpData;
import vn.ses.s3m.plus.dto.Project;

@Mapper
public interface CustomerMapper {

    List<Customer> getCustomers();

    Customer getCustomer(Map<String, String> condition);

    Customer getCustomerMax(Map<String, Object> condition);

    void addCustomer(Map<String, String> condition);

    void addSchema(Map<String, Object> condition);

    void updateCustomer(Customer condition);

    void deleteCustomer(Map<String, Object> condition);

    List<Customer> searchCustomer(Map<String, String> condtion);

    List<Customer> getListCustomer(Map<String, String> condtion);

    List<Customer> getCustomerByCode(Map<String, String> condition);

    List<Customer> checkCustomerIdInUserTable(Map<String, Object> condition);

    List<Customer> checkCustomerIdInProjectTable(Map<String, Object> condition);

    List<Project> getProjectsByCustomerId(Map<String, Object> condition);

    Long getPowerByCustomerId(Integer customerId);

    void insertOtp(OtpData data);

    OtpData getOtpInfor(OtpData data);

    void updateStatusOtp(OtpData data);

    Customer getCustomerByProjectId(Map<String, String> condtion);

    Customer getCustomerIdFirstTime();

    String getListCustomerIds(String userName);

    List<Customer> getListCus(String ids);

}
