package vn.ses.s3m.plus.dto;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class TypeTime {
    private Integer id;
    private String timeName;
    private Timestamp createDate;
    private Timestamp updateDate;
}
