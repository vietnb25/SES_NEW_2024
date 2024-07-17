package vn.ses.s3m.plus.response;

import lombok.Data;

@Data
public class DataPowerIResponse {

    private String harmonicsNo;
    private Integer iaH;
    private Integer ibH;
    private Integer icH;
}
