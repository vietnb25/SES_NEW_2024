package vn.ses.s3m.plus.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.ses.s3m.plus.dto.DataWeather1;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OperationWeatherResponse {

    private Integer id;

    private Long deviceId;

    private String deviceName;

    private Integer rad;

    private Integer temp;

    private Integer windSp;

    private Integer windDir;

    private Integer h;

    private Integer rain;

    private Integer atmos;

    private String sentDate;

    private Long transactionDate;

    public OperationWeatherResponse(final DataWeather1 weather1) {
        this.id = weather1.getId();
        this.deviceId = weather1.getDeviceId();
        this.rad = weather1.getRad();
        this.temp = weather1.getTemp();
        this.windSp = weather1.getWindSp();
        this.windDir = weather1.getWindDir();
        this.h = weather1.getH();
        this.rain = weather1.getRain();
        this.atmos = weather1.getAtmos();
        this.sentDate = weather1.getSentDate();
        this.transactionDate = weather1.getTransactionDate();
    }
}
