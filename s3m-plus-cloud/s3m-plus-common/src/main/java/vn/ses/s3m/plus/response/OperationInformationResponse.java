package vn.ses.s3m.plus.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.ses.s3m.plus.dto.DataLoadFrame1;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OperationInformationResponse {

    private Integer id;

    private Long deviceId;

    private String deviceName;

    private Integer voltage;

    private Integer power;

    private String address;

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

    private Integer epR;

    private Integer epDr;

    private Integer epDrr;

    private Integer eq;

    private Integer eqR;

    private Integer eqDr;

    private Integer eqDrr;

    private Integer es;

    private Integer esR;

    private Integer esDr;

    private Integer esDrr;

    private Float t1;

    private Float t2;

    private Float t3;

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

    // % điện áp không cần bằng
    private Float vu;

    // % Dòng điện không cân bằng
    private Float iu;

    private String sentDate;

    private Long transactionDate;

    public OperationInformationResponse(final DataLoadFrame1 frame1) {
        this.id = frame1.getId();
        this.deviceId = frame1.getDeviceId();
        this.deviceName = frame1.getDeviceName();
        this.power = frame1.getPower();
        this.voltage = frame1.getVoltage();
        this.address = frame1.getAddress();
        this.uab = frame1.getUab();
        this.ubc = frame1.getUbc();
        this.uca = frame1.getUca();
        this.ull = frame1.getUll();
        this.uan = frame1.getUan();
        this.ubn = frame1.getUbn();
        this.ucn = frame1.getUcn();
        this.uln = frame1.getUln();
        this.ia = frame1.getIa();
        this.ib = frame1.getIb();
        this.ic = frame1.getIc();
        this.in = frame1.getIn();
        this.ig = frame1.getIg();
        this.iavg = frame1.getIavg();
        this.pa = frame1.getPa();
        this.pb = frame1.getPb();
        this.pc = frame1.getPc();
        this.pTotal = frame1.getPTotal();
        this.qa = frame1.getQa();
        this.qb = frame1.getQb();
        this.qc = frame1.getQc();
        this.qTotal = frame1.getQTotal();
        this.sa = frame1.getSa();
        this.sb = frame1.getSb();
        this.sc = frame1.getSc();
        this.sTotal = frame1.getSTotal();
        this.pfa = frame1.getPfa();
        this.pfb = frame1.getPfb();
        this.pfc = frame1.getPfc();
        this.pfavg = frame1.getPfavg();
        this.f = frame1.getF();
        this.ep = frame1.getEp();
        this.epR = frame1.getEpR();
        this.epDr = frame1.getEpDr();
        this.epDrr = frame1.getEpDrr();
        this.eq = frame1.getEq();
        this.eqR = frame1.getEqR();
        this.eqDr = frame1.getEqDr();
        this.eqDrr = frame1.getEqDrr();
        this.es = frame1.getEs();
        this.esR = frame1.getEsR();
        this.esDr = frame1.getEsDr();
        this.esDrr = frame1.getEsDrr();
        this.t1 = frame1.getT1();
        this.t2 = frame1.getT2();
        this.t3 = frame1.getT3();
        this.thdIa = frame1.getThdIa();
        this.thdIb = frame1.getThdIb();
        this.thdIc = frame1.getThdIc();
        this.thdIn = frame1.getThdIn();
        this.thdIg = frame1.getThdIg();
        this.thdVab = frame1.getThdVab();
        this.thdVbc = frame1.getThdVbc();
        this.thdVca = frame1.getThdVca();
        this.thdVll = frame1.getThdVll();
        this.thdVan = frame1.getThdVan();
        this.thdVbn = frame1.getThdVbn();
        this.thdVcn = frame1.getThdVcn();
        this.thdVln = frame1.getThdVln();
        this.sentDate = frame1.getSentDate();
        this.transactionDate = frame1.getTransactionDate();
    }

}
