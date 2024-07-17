package vn.ses.s3m.plus.dto;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class MaterialType {
    private Integer id;
    private String materialName;
    private Timestamp createDate;
    private Timestamp updateDate;
    private Integer materialForm;
}
