package vn.ses.s3m.plus.response;

import lombok.Data;

@Data
public class DataPowerUResponse {

    private String harmonicsNo;
    private Integer vabH;
    private Integer vbcH;
    private Integer vcaH;
}
