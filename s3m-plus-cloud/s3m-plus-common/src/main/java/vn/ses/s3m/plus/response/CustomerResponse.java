package vn.ses.s3m.plus.response;

import java.sql.Timestamp;

import lombok.Data;
import vn.ses.s3m.plus.dto.Customer;

@Data
public class CustomerResponse {

    private Integer customerId;
    private String customerName;
    private String customerCode;
    private String description;
    private Timestamp updateDate;

    public CustomerResponse(Customer customer) {
        this.customerId = customer.getCustomerId();
        this.customerName = customer.getCustomerName();
        this.customerCode = customer.getCustomerCode();
        this.description = customer.getDescription();
        this.updateDate = customer.getUpdateDate();
    }
}
