package vn.ses.s3m.plus.grid.response;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import lombok.Data;
import vn.ses.s3m.plus.common.Constants;
import vn.ses.s3m.plus.dto.DataRmuDrawer1;

@Data
public class DataChartRmuDrawerResponse {

    private Long id;

    private Long deviceId;

    private Integer deviceType;

    private Float sawId1;

    private Float sawId2;

    private Float sawId3;

    private Float sawId4;

    private Float sawId5;

    private Float sawId6;

    private Float gAMean;

    private Integer gAAlarm;

    private Float gBMean;

    private Integer gBAlarm;

    private Integer alarmStatus;

    private Float lfbRatio;

    private Float lfbEppc;

    private Float mfbRatio;

    private Float mlfbEppc;

    private Float hlfbRatio;

    private Float hlfbEppc;

    private Float meanRatio;

    private Float meanEppc;

    private Float rxEHi;

    private Float rxELo;

    private Integer indicator;

    private Float t;

    private Float h;

    private Float uab;

    private Float ubc;

    private Float uca;

    private Float ull;

    private Float uan;

    private Float ubn;

    private Float ucn;

    private Float uln;

    private Float ia;

    private Float ib;

    private Float ic;

    private Float in;

    private Float ig;

    private Float iavg;

    private Float pa;

    private Float pb;

    private Float pc;

    private Float pTotal;

    private Float qa;

    private Float qb;

    private Float qc;

    private Float qTotal;

    private Float sa;

    private Float sb;

    private Float sc;

    private Float sTotal;

    private Float pfa;

    private Float pfb;

    private Float pfc;

    private Float pfavg;

    private Float f;

    private Integer ep;

    private Integer eq;

    private Integer es;

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

    public DataChartRmuDrawerResponse(final DataRmuDrawer1 rmuDrawer1) {
        this.id = rmuDrawer1.getId();
        this.deviceId = rmuDrawer1.getDeviceId();
        this.deviceType = rmuDrawer1.getDeviceType();
        this.sawId1 = rmuDrawer1.getSawId1();
        this.sawId2 = rmuDrawer1.getSawId2();
        this.sawId3 = rmuDrawer1.getSawId3();
        this.sawId4 = rmuDrawer1.getSawId4();
        this.sawId5 = rmuDrawer1.getSawId5();
        this.sawId6 = rmuDrawer1.getSawId6();
        this.gAMean = rmuDrawer1.getGAMean();
        this.gAAlarm = rmuDrawer1.getGAAlarm();
        this.gBMean = rmuDrawer1.getGBMean();
        this.gBAlarm = rmuDrawer1.getGBAlarm();
        this.alarmStatus = rmuDrawer1.getAlarmStatus();
        this.lfbRatio = rmuDrawer1.getLfbRatio();
        this.lfbEppc = rmuDrawer1.getLfbEppc();
        this.mfbRatio = rmuDrawer1.getMfbRatio();
        this.mlfbEppc = rmuDrawer1.getMlfbEppc();
        this.hlfbRatio = rmuDrawer1.getHlfbRatio();
        this.hlfbEppc = rmuDrawer1.getHlfbEppc();
        this.meanRatio = rmuDrawer1.getMeanRatio();
        this.meanEppc = rmuDrawer1.getMeanEppc();
        this.rxEHi = rmuDrawer1.getRxEHi();
        this.rxELo = rmuDrawer1.getRxELo();
        this.indicator = rmuDrawer1.getIndicator();
        this.t = rmuDrawer1.getT();
        this.h = rmuDrawer1.getH();
        this.uab = rmuDrawer1.getUab();
        this.ubc = rmuDrawer1.getUbc();
        this.uca = rmuDrawer1.getUca();
        this.ull = rmuDrawer1.getUll();
        this.uan = rmuDrawer1.getUan();
        this.ubn = rmuDrawer1.getUbn();
        this.ucn = rmuDrawer1.getUcn();
        this.uln = rmuDrawer1.getUln();
        this.ia = rmuDrawer1.getIa();
        this.ib = rmuDrawer1.getIb();
        this.ic = rmuDrawer1.getIc();
        this.in = rmuDrawer1.getIn();
        this.ig = rmuDrawer1.getIg();
        this.iavg = rmuDrawer1.getIavg();
        this.pa = rmuDrawer1.getPa();
        this.pb = rmuDrawer1.getPb();
        this.pc = rmuDrawer1.getPc();
        this.pTotal = rmuDrawer1.getPTotal();
        this.qa = rmuDrawer1.getQa();
        this.qb = rmuDrawer1.getQb();
        this.qc = rmuDrawer1.getQc();
        this.qTotal = rmuDrawer1.getQTotal();
        this.sa = rmuDrawer1.getSa();
        this.sb = rmuDrawer1.getSb();
        this.sc = rmuDrawer1.getSc();
        this.sTotal = rmuDrawer1.getSTotal();
        this.pfa = rmuDrawer1.getPfa();
        this.pfb = rmuDrawer1.getPfb();
        this.pfc = rmuDrawer1.getPfc();
        this.pfavg = rmuDrawer1.getPfavg();
        this.f = rmuDrawer1.getF();
        this.ep = rmuDrawer1.getEp();
        this.eq = rmuDrawer1.getEq();
        this.es = rmuDrawer1.getEs();
        this.thdIa = rmuDrawer1.getThdIa();
        this.thdIb = rmuDrawer1.getThdIb();
        this.thdIc = rmuDrawer1.getThdIc();
        this.thdIn = rmuDrawer1.getThdIn();
        this.thdIg = rmuDrawer1.getThdIg();
        this.thdVab = rmuDrawer1.getThdVab();
        this.thdVbc = rmuDrawer1.getThdVbc();
        this.thdVca = rmuDrawer1.getThdVca();
        this.thdVll = rmuDrawer1.getThdVll();
        this.thdVan = rmuDrawer1.getThdVan();
        this.thdVbn = rmuDrawer1.getThdVbn();
        this.thdVcn = rmuDrawer1.getThdVcn();
        this.thdVln = rmuDrawer1.getThdVln();
        DateFormat parser = new SimpleDateFormat(Constants.ES.DATETIME_FORMAT_YMDHMS);
        Date sentDate1 = null;
        try {
            sentDate1 = parser.parse(rmuDrawer1.getSentDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        DateFormat formatter = new SimpleDateFormat(Constants.ES.DATETIME_FORMAT_YMDHMS);
        this.sentDate = formatter.format(sentDate1);
        this.transactionDate = rmuDrawer1.getTransactionDate();
    }
}
