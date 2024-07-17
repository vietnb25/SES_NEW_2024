package vn.ses.s3m.plus.response;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.ses.s3m.plus.common.Constants;
import vn.ses.s3m.plus.dto.DataLoadFrame1;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataChartResponse {

    private String date;

    private Float ia;

    private Float ib;

    private Float ic;

    private Float uan;

    private Float ubn;

    private Float ucn;

    private Float pa;

    private Float pb;

    private Float pc;

    private Float t1;

    private Float t2;

    private Float t3;

    private Float pfa;

    private Float pfb;

    private Float pfc;

    public DataChartResponse(final DataLoadFrame1 frame1) throws ParseException {
        this.uan = frame1.getUan();
        this.ubn = frame1.getUbn();
        this.ucn = frame1.getUcn();
        this.ia = frame1.getIa();
        this.ib = frame1.getIb();
        this.ic = frame1.getIc();
        this.pa = frame1.getPa();
        this.pb = frame1.getPc();
        this.pc = frame1.getPc();
        this.t1 = frame1.getT1();
        this.t2 = frame1.getT2();
        this.t3 = frame1.getT3();
        this.pfa = frame1.getPfa();
        this.pfb = frame1.getPfb();
        this.pfc = frame1.getPfc();

        DateFormat parser = new SimpleDateFormat(Constants.ES.DATETIME_FORMAT_YMDHMS);
        Date sentDate = parser.parse(frame1.getSentDate());
        DateFormat formatter = new SimpleDateFormat(Constants.ES.DATETIME_FORMAT_YMDHMS);
        this.date = formatter.format(sentDate);
    }
}
