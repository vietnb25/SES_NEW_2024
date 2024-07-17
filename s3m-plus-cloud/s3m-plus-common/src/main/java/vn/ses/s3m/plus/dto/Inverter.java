package vn.ses.s3m.plus.dto;

import java.sql.Timestamp;

import lombok.Data;

@Data
public class Inverter {

    private Long id;

    private Long deviceId;

    private Integer A;

    private Integer AphA;

    private Integer AphB;

    private Integer AphC;

    private Integer PPVphAB;

    private Integer PPVphBC;

    private Integer PPVphCA;

    private Integer PhVphA;

    private Integer PhVphB;

    private Integer PhVphC;

    private Integer W;

    private Integer Hz;

    private Integer VA;

    private Integer VAr;

    private Integer PF;

    private Integer Wh;

    private Float DCA;

    private Float DCV;

    private Float DCW;

    private Integer TmpCab;

    private Integer TmpSnk;

    private Integer TmpTrns;

    private Integer TmpOt;

    private Integer St;

    private Integer StVnd;

    private String Evt1;

    private Integer Evt2;

    private Integer EvtVnd1;

    private Integer EvtVnd2;

    private Integer EvtVnd3;

    private Integer EvtVnd4;

    private Integer TmSrc;

    private Integer Tms;

    private String crc16;

    private Timestamp sentDate;

    private Long transaction;
}
