package vn.ses.s3m.plus.response;

import lombok.Data;

@Data
public class JsonElectricPower {
    private Double load;
    private Double load1;
    private Double load2;
    private Double load3;
    private Double load4;
    private Double pv;
    private Double pv1;
    private Double pv2;
    private Double pv3;
    private Double pv4;
    private Double grid;
    private Double wind;
    private Double ev;
    private String time;

}
