package vn.ses.s3m.plus.pv.response;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.ses.s3m.plus.common.Constants;
import vn.ses.s3m.plus.dto.DataWeather1;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChartOperationWeatherResponse {

    private Long id;

    private Long deviceId;

    private Integer Wind_sp;

    private Integer Wind_dir;

    private Integer Rad;

    private Integer Temp;

    private Integer H;

    private Integer Rain;

    private Integer Atmos;

    private String sentDate;

    public ChartOperationWeatherResponse(final DataWeather1 weather1) {
        this.id = weather1.getId();
        this.deviceId = weather1.getDeviceId();
        this.Rad = weather1.getRad();
        this.Temp = weather1.getTemp();
        this.Wind_sp = weather1.getWind_sp();
        this.Wind_dir = weather1.getWind_dir();
        this.H = weather1.getH();
        this.Rain = weather1.getRain();
        this.Atmos = weather1.getAtmos();
        DateFormat parser = new SimpleDateFormat(Constants.ES.DATETIME_FORMAT_YMDHMS);
        Date sentDate1 = null;
        try {
            sentDate1 = parser.parse(weather1.getSentDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        DateFormat formatter = new SimpleDateFormat(Constants.ES.DATETIME_FORMAT_YMDHMS);
        this.sentDate = formatter.format(sentDate1);
    }
}
