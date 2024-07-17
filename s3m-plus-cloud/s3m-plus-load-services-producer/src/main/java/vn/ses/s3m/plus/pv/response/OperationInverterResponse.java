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
import vn.ses.s3m.plus.dto.DataInverter1;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OperationInverterResponse {

    private Long id;

    private Long deviceId;

    private String deviceName;

    private Float Ia;

    private Float Ib;

    private Float Ic;

    private Float Vab;

    private Float Vbc;

    private Float Vca;

    private Float Va;

    private Float Vb;

    private Float Vc;

    private Float F;

    private Float PF;

    private Float Idc;

    private Float Udc;

    private Float Pdc;

    private Float TmpCab;

    private Float TmpSnk;

    private Float TmpTrns;

    private Float TmpOt;

    private Float St;

    private Float StVnd;

    private Float Evt1;

    private Float Evt2;

    private Float EvtVnd1;

    private Float EvtVnd2;

    private Float EvtVnd3;

    private Float EvtVnd4;

    private Float TmSrc;

    private Float Tms;

    private Float CRC16;

    private Float PVConn;

    private Float ECPConn;

    private Float Conn;

    private Float Wmax;

    private Float Vref;

    private Float VRefofs;

    private Float VAMax;

    private Float VAMaxQ1;

    private Float VAMaxQ2;

    private Float VAMaxQ3;

    private Float VAMaxQ4;

    private Float F_normal;

    private Float WMaxLimPct;

    private Float WMaxLimPct_WinTms;

    private Float WMaxLimPct_RvrtTms;

    private Float WMaxLimPct_RmpTms;

    private Float WMaxLim_Ena;

    private Float OutPFSet;

    private Float OutPFSet_WinTms;

    private Float OutPFSet_RvrtTms;

    private Float OutPFSet_RmpTms;

    private Float OutPFSet_Ena;

    private Float VArWMaxPct;

    private Float VArMaxPct;

    private Float VArAvalPct;

    private Float VArPct_WinTms;

    private Float VArPct_RvrtTms;

    private Float VArPct_RmpTms;

    private Float VArPct_Mod;

    private Float VArPct_Ena;

    private Float Ptotal;

    private Float Pa;

    private Float Pb;

    private Float Pc;

    private Float Stotal;

    private Float Sa;

    private Float Sb;

    private Float Sc;

    private Float Qtotal;

    private Float Qa;

    private Float Qb;

    private Float Qc;

    private Float PFa;

    private Float PFb;

    private Float PFc;

    private Float Ep;

    private Float EpDC;

    private String sentDate;

    private Long transactionDate;

    public OperationInverterResponse(final DataInverter1 inverter1) {
        this.id = inverter1.getId();
        this.deviceId = inverter1.getDeviceId();
        this.deviceName = inverter1.getDeviceName();
        this.Ia = inverter1.getIa();
        this.Ib = inverter1.getIb();
        this.Ic = inverter1.getIc();
        this.Va = inverter1.getVa();
        this.Vb = inverter1.getVb();
        this.Vc = inverter1.getVc();
        this.PFa = inverter1.getPFa();
        this.PFb = inverter1.getPFb();
        this.PFc = inverter1.getPFc();
        this.Pa = inverter1.getPa();
        this.Pb = inverter1.getPb();
        this.Pc = inverter1.getPc();
        this.Qa = inverter1.getQa();
        this.Qb = inverter1.getQb();
        this.Qc = inverter1.getQc();
        this.Ptotal = inverter1.getPtotal();
        this.Qtotal = inverter1.getQtotal();
        this.Udc = inverter1.getUdc();
        this.Idc = inverter1.getIdc();
        this.Pdc = inverter1.getPdc();
        this.F = inverter1.getF();
        this.Ep = inverter1.getEp();
        this.TmpCab = inverter1.getTmpCab();
        this.TmpSnk = inverter1.getTmpSnk();

        DateFormat parser = new SimpleDateFormat(Constants.ES.DATETIME_FORMAT_YMDHMS);
        Date sentDate1 = null;
        try {
            sentDate1 = parser.parse(inverter1.getSentDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        DateFormat formatter = new SimpleDateFormat(Constants.ES.DATETIME_FORMAT_YMDHMS);
        this.sentDate = formatter.format(sentDate1);
        this.transactionDate = inverter1.getTransactionDate();
    }
}
