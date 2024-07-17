package vn.ses.s3m.plus.dto;

import java.sql.Timestamp;

import lombok.Data;

@Data
public class Customer {

    private Integer customerId;

    private String customerName;

    private String customerCode;

    private Integer managerId;

    private String managerName;

    private String description;

    private Timestamp updateDate;

    private Integer projectNo;

    private Integer projectNumber;

    private Integer pvNumber;

    private Long pvTotal;

    private Integer loadNumber;

    private Long loadEnnergy;

    private Long sgmv;

    private Long cspTotal;

    private Integer idSchema;
}
