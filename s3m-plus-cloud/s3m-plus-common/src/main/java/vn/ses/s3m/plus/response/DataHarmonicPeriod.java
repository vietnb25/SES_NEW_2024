package vn.ses.s3m.plus.response;

import java.text.DateFormat;
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
public class DataHarmonicPeriod {

    private Integer id;

    private Long deviceId;

    private Float thdIa;

    private Float thdIb;

    private Float thdIc;

    private Float thdIn;

    private Float thdIg;

    private Float thdVab;

    private Float thdVbc;

    private Float thdVca;

    private Float thdVll;

    private Float thdVan;

    private Float thdVbn;

    private Float thdVcn;

    private Float thdVln;

    private String sentDate;

    private Long transactionDate;

    public DataHarmonicPeriod(final DataLoadFrame1 frame1) throws Exception {
        this.id = frame1.getId();
        this.deviceId = frame1.getDeviceId();
        this.thdIa = frame1.getThdIa() != null ? frame1.getThdIa() : 0;
        this.thdIb = frame1.getThdIb() != null ? frame1.getThdIb() : 0;
        this.thdIc = frame1.getThdIc() != null ? frame1.getThdIb() : 0;
        this.thdIn = frame1.getThdIn() != null ? frame1.getThdIb() : 0;
        this.thdIg = frame1.getThdIg() != null ? frame1.getThdIb() : 0;
        this.thdVab = frame1.getThdVab() != null ? frame1.getThdVab() : 0;
        this.thdVbc = frame1.getThdVbc() != null ? frame1.getThdVbc() : 0;
        this.thdVca = frame1.getThdVca() != null ? frame1.getThdVca() : 0;
        this.thdVll = frame1.getThdVll() != null ? frame1.getThdVll() : 0;
        this.thdVan = frame1.getThdVan() != null ? frame1.getThdVan() : 0;
        this.thdVbn = frame1.getThdVbn() != null ? frame1.getThdVbn() : 0;
        this.thdVcn = frame1.getThdVcn() != null ? frame1.getThdVcn() : 0;
        this.thdVln = frame1.getThdVln() != null ? frame1.getThdVln() : 0;

        DateFormat parser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = parser.parse(frame1.getSentDate());
        DateFormat formatter = new SimpleDateFormat(Constants.ES.DATETIME_FORMAT_DMYHM);

        this.sentDate = formatter.format(date);
        this.transactionDate = frame1.getTransactionDate();
    }

}
