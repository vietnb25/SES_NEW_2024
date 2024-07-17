package vn.ses.s3m.plus.dto;

import java.sql.Timestamp;

import lombok.Data;

@Data
public class Receiver {

    private Long receiverId;

    private String name;

    private String phone;

    private String email;

    private String description;

    private Timestamp updateDate;
}
