package vn.ses.s3m.plus.dto;

import lombok.Data;

@Data
public class LoadType {

    private Integer loadTypeId;

    private String loadTypeName;

    private String description;

    private String createDate;

    private String updateDate;

    private String createId;

    private String updateId;
}
