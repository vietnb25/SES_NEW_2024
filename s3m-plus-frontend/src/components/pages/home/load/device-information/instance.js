import moment from "moment";
import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import CONS from "../../../../../constants/constant";
import OperationInformationService from "../../../../../services/OperationInformationService";
import converter from "../../../../../common/converter";

const Instance = () => {
    const param = useParams();

    const [state, setState] = useState(0);

    const [instant, setInstantOperationInfo] = useState({
        id: "",
        deviceId: "",
        deviceName: "",
        votage: "",
        power: "",
        address: "",
        uab: "",
        ubc: "",
        uca: "",
        ull: "",
        uan: "",
        ubn: "",
        ucn: "",
        uln: "",
        ia: "",
        ib: "",
        ic: "",
        in: "",
        iavg: "",
        pa: "",
        pb: "",
        pc: "",
        ptotal: "",
        qa: "",
        qb: "",
        qc: "",
        qtotal: "",
        sa: "",
        sb: "",
        sc: "",
        stotal: "",
        pfa: "",
        pfb: "",
        pfc: "",
        pfavg: "",
        thdVab: "",
        thdVbc: "",
        thdVca: "",
        thdVan: "",
        thdIa: "",
        thdIb: "",
        thdIc: "",
        thdIn: "",
        t1: "",
        t2: "",
        t3: "",
        ep: "",
        epR: "",
        epDr: "",
        epDrr: "",
        eq: "",
        eqR: "",
        eqDr: "",
        eqDrr: "",
        sentDate: "",
        iu: "",
        vu: "",
        transactionDate: ""
    });
    const [instantPowerQuality, setInstantPowerQuality] = useState({
        id: "",
        deviceId: "",
        deviceNa: "",
        vanH1: "",
        vanH2: "",
        vanH3: "",
        vanH4: "",
        vanH5: "",
        vanH6: "",
        vanH7: "",
        vanH8: "",
        vanH9: "",
        vanH10: "",
        vanH11: "",
        vanH12: "",
        vanH13: "",
        vanH14: "",
        vanH15: "",
        vanH16: "",
        vanH17: "",
        vanH18: "",
        vanH19: "",
        vanH20: "",
        vanH21: "",
        vanH22: "",
        vanH23: "",
        vanH24: "",
        vanH25: "",
        vanH26: "",
        vanH27: "",
        vanH28: "",
        vanH29: "",
        vanH30: "",
        vanH31: "",
        vbnH1: "",
        vbnH2: "",
        vbnH3: "",
        vbnH4: "",
        vbnH5: "",
        vbnH6: "",
        vbnH7: "",
        vbnH8: "",
        vbnH9: "",
        vbnH10: "",
        vbnH11: "",
        vbnH12: "",
        vbnH13: "",
        vbnH14: "",
        vbnH15: "",
        vbnH16: "",
        vbnH17: "",
        vbnH18: "",
        vbnH19: "",
        vbnH20: "",
        vbnH21: "",
        vbnH22: "",
        vbnH23: "",
        vbnH24: "",
        vbnH25: "",
        vbnH26: "",
        vbnH27: "",
        vbnH28: "",
        vbnH29: "",
        vbnH30: "",
        vbnH31: "",
        vcnH1: "",
        vcnH2: "",
        vcnH3: "",
        vcnH4: "",
        vcnH5: "",
        vcnH6: "",
        vcnH7: "",
        vcnH8: "",
        vcnH9: "",
        vcnH10: "",
        vcnH11: "",
        vcnH12: "",
        vcnH13: "",
        vcnH14: "",
        vcnH15: "",
        vcnH16: "",
        vcnH17: "",
        vcnH18: "",
        vcnH19: "",
        vcnH20: "",
        vcnH21: "",
        vcnH22: "",
        vcnH23: "",
        vcnH24: "",
        vcnH25: "",
        vcnH26: "",
        vcnH27: "",
        vcnH28: "",
        vcnH29: "",
        vcnH30: "",
        vcnH31: "",
        iaH1: "",
        iaH2: "",
        iaH3: "",
        iaH4: "",
        iaH5: "",
        iaH6: "",
        iaH7: "",
        iaH8: "",
        iaH9: "",
        iaH10: "",
        iaH11: "",
        iaH12: "",
        iaH13: "",
        iaH14: "",
        iaH15: "",
        iaH16: "",
        iaH17: "",
        iaH18: "",
        iaH19: "",
        iaH20: "",
        iaH21: "",
        iaH22: "",
        iaH23: "",
        iaH24: "",
        iaH25: "",
        iaH26: "",
        iaH27: "",
        iaH28: "",
        iaH29: "",
        iaH30: "",
        iaH31: "",
        ibH1: "",
        ibH2: "",
        ibH3: "",
        ibH4: "",
        ibH5: "",
        ibH6: "",
        ibH7: "",
        ibH8: "",
        ibH9: "",
        ibH10: "",
        ibH11: "",
        ibH12: "",
        ibH13: "",
        ibH14: "",
        ibH15: "",
        ibH16: "",
        ibH17: "",
        ibH18: "",
        ibH19: "",
        ibH20: "",
        ibH21: "",
        ibH22: "",
        ibH23: "",
        ibH24: "",
        ibH25: "",
        ibH26: "",
        ibH27: "",
        ibH28: "",
        ibH29: "",
        ibH30: "",
        ibH31: "",
        icH1: "",
        icH2: "",
        icH3: "",
        icH4: "",
        icH5: "",
        icH6: "",
        icH7: "",
        icH8: "",
        icH9: "",
        icH10: "",
        icH11: "",
        icH12: "",
        icH13: "",
        icH14: "",
        icH15: "",
        icH16: "",
        icH17: "",
        icH18: "",
        icH19: "",
        icH20: "",
        icH21: "",
        icH22: "",
        icH23: "",
        icH24: "",
        icH25: "",
        icH26: "",
        icH27: "",
        icH28: "",
        icH29: "",
        icH30: "",
        icH31: "",
        sentDate: "",
        transactionDate: "",
    });

    const [viewTypeTable, setViewTypeTable] = useState(null);

    const getInstantOperationInformation = async () => {
        if (parseInt(param.deviceId) === 0) {
            return
        }

        let res = await OperationInformationService.getInstantOperationInformation(param.customerId, param.deviceId);
        if (res.status === 200) {
            let data = res.data;

            let objValueCompare = {
                ep: data.ep,
                eq: data.eq,
                pa: data.pa,
                pb: data.pb,
                pc: data.pc,
                ptotal: data.ptotal,
                qa: data.qa,
                qb: data.qb,
                qc: data.qc,
                qtotal: data.qtotal,
                sa: data.sa,
                sb: data.sb,
                sc: data.sc,
                stotal: data.stotal
            }

            let values = Object.values(objValueCompare).filter(value => value !== null && value > 0);

            let min = Math.min(...values);

            let viewTypeModal = converter.setViewType(min);

            setViewTypeTable(viewTypeModal);

            setInstantOperationInfo(res.data);
        } else {
            setInstantOperationInfo([]);
        }
    }

    const getInstantPowerQuality = async () => {
        if (parseInt(param.deviceId) === 0) {
            return
        }
        let res = await OperationInformationService.getInstantPowerQuality(param.customerId, param.deviceId);
        if (res.status === 200) {
            setInstantPowerQuality(res.data);
        } else {
            setInstantPowerQuality([]);
        }
    }

    function Interval() {
        setTimeout(() => {
            setState(state + 1);
        }, 15000)
    }

    useEffect(() => {
        Interval();
        getInstantOperationInformation();
        getInstantPowerQuality();
        document.title = "Thông tin thiết bị - Tức thời";
    }, [state, param.deviceId, param.customerId])

    return (
        <>
            <div className="tab-title">
                <div className="latest-time mt-3">
                    <i className="fa-regular fa-clock"></i>&nbsp;{instant?.sentDate != null ? moment(instant?.sentDate).format(CONS.DATE_FORMAT_OPERATE) : ""}
                </div>
                <div className="latest-warning">
                    <button type="button" className="btn btn-outline-info" style={{ width: "34.5px", height: "29px", paddingLeft: "10px", fontSize: "13px" }} onClick={() => Interval()}>
                        <i className="fa-solid fa-rotate-right"></i>
                    </button>
                </div>
            </div>
            <table className="table tbl-overview tbl-tsd">
                <thead>
                    <tr>
                        <th colSpan="15" className="tbl-title">Thông số điện</th>
                    </tr>
                </thead>
            </table>
            <table className="table tbl-overview tbl-tsd">
                <thead style={{ display: "table-header-group", width: "100%", tableLayout: "fixed" }}>

                    <tr>
                        <th width="40px">Pha</th>
                        <th width="100px">Điện áp [V]</th>
                        <th width="100px">Dòng điện [A]</th>
                        <th width="50px">%</th>
                        <th width="80px">P [kW]</th>
                        <th width="80px">Q [kVAr]</th>
                        <th width="80px">S [kVA]</th>
                        <th width="80px">PF</th>
                        <th width="80px">THD U [%]</th>
                        <th width="80px">THD I [%]</th>
                        <th width="80px">Phase U</th>
                        <th width="80px">F [Hz]</th>
                        <th width="80px">Vu [%]</th>
                        <th width="80px">Iu [%]</th>
                        <th>Điện năng [kWh]</th>
                    </tr>
                </thead>
                <tbody>
                    <tr>
                        <td className="text-center">A</td>
                        <td className="text-center">{instant?.uan != null ? instant?.uan : "-"}</td>
                        <td className="text-center">{instant?.ia != null ? instant?.ia : "-"}</td>
                        <td className="text-center">-</td>
                        <td className="text-center">{instant?.pa != null ? instant?.pa : "-"}</td>
                        <td className="text-center">{instant?.qa != null ? instant?.qa : "-"}</td>
                        <td className="text-center">{instant?.sa != null ? instant?.sa : "-"}</td>
                        <td className="text-center">{instant?.pfa != null ? instant?.pfa : "-"}</td>
                        <td className="text-center">{instant?.thdVab != null ? instant?.thdVab : "-"}</td>
                        <td className="text-center">{instant?.thdIa != null ? instant?.thdIa : "-"}</td>
                        <td className="text-center">-</td>
                        <td className="text-center" rowSpan={3}>{instant?.f != null ? instant?.f : "-"}</td>
                        <td className="text-center" rowSpan={3}>-</td>
                        <td className="text-center" rowSpan={3}>-</td>
                        <td className="text-center" rowSpan={3}>{instant?.ep != null && instant?.ep >= 0 ? instant?.ep : "-"}</td>
                    </tr>
                    <tr>
                        <td className="text-center">B</td>
                        <td className="text-center">{instant?.ubn != null ? instant?.ubn : "-"}</td>
                        <td className="text-center">{instant?.ib != null ? instant?.ib : "-"}</td>
                        <td className="text-center">-</td>
                        <td className="text-center">{instant?.pb != null ? instant?.pb : "-"}</td>
                        <td className="text-center">{instant?.qb != null ? instant?.qb : "-"}</td>
                        <td className="text-center">{instant?.sb != null ? instant?.sb : "-"}</td>
                        <td className="text-center">{instant?.pfb != null ? instant?.pfb : "-"}</td>
                        <td className="text-center">{instant?.thdVbc != null ? instant?.thdVbc : "-"}</td>
                        <td className="text-center">{instant?.thdIb != null ? instant?.thdIb : "-"}</td>
                        <td className="text-center">-</td>
                    </tr>
                    <tr>
                        <td className="text-center">C</td>
                        <td className="text-center">{instant?.ucn != null ? instant?.ucn : "-"}</td>
                        <td className="text-center">{instant?.ic != null ? instant?.ic : "-"}</td>
                        <td className="text-center">-</td>
                        <td className="text-center">{instant?.pc != null ? instant?.pc : "-"}</td>
                        <td className="text-center">{instant?.qc != null ? instant?.qc : "-"}</td>
                        <td className="text-center">{instant?.sc != null ? instant?.sc : "-"}</td>
                        <td className="text-center">{instant?.pfc != null ? instant?.pfc : "-"}</td>
                        <td className="text-center">{instant?.thdVca != null ? instant?.thdVca : "-"}</td>
                        <td className="text-center">{instant?.thdIc != null ? instant?.thdIc : "-"}</td>
                        <td className="text-center">-</td>
                    </tr>
                </tbody>
            </table>

            <table className="table tbl-overview tbl-tsnd mt-3">
                <thead>
                    <tr>
                        <th colSpan="4" className="tbl-title">Thông số nhiệt độ [°C]</th>
                    </tr>
                </thead>
            </table>
            <table className="table tbl-overview tbl-tsnd">
                <thead style={{ display: "table-header-group", width: "100%", tableLayout: "fixed" }}>
                    <tr>
                        <th width="40px">Pha</th>
                        <th>Vị trí 1</th>
                        <th>Vị trí 2</th>
                        <th>Vị trí 3</th>
                    </tr>
                </thead>
                <tbody>
                    <tr>
                        <td className="text-center">A</td>
                        <td className="text-center">{instant?.t1 != null ? instant?.t1 : "-"}</td>
                        <td className="text-center">-</td>
                        <td className="text-center">-</td>
                    </tr>
                    <tr>
                        <td className="text-center">B</td>
                        <td className="text-center">{instant?.t2 != null ? instant?.t2 : "-"}</td>
                        <td className="text-center">-</td>
                        <td className="text-center">-</td>
                    </tr>
                    <tr>
                        <td className="text-center">C</td>
                        <td className="text-center">{instant?.t3 != null ? instant?.t3 : "-"}</td>
                        <td className="text-center">-</td>
                        <td className="text-center">-</td>
                    </tr>
                </tbody>
            </table>

            <div className="latest-time mt-2" style={{ paddingLeft: 10, paddingTop: 10 }}>
                <i className="fa-regular fa-clock"></i>&nbsp;{instantPowerQuality?.sentDate != null ? moment(instantPowerQuality?.sentDate).format(CONS.DATE_FORMAT_OPERATE) : ""}
            </div>

            <table className="table tbl-overview tbl-tsd mt-1">
                <thead>
                    <tr>
                        <th colSpan="32" className="tbl-title">Thông số sóng hài [%]</th>
                    </tr>
                </thead>
            </table>
            <table className="table tbl-overview tbl-tsd">
                <thead style={{ display: "table-header-group", width: "100%", tableLayout: "fixed" }}>
                    <tr>
                        <th width="40px">Pha</th>
                        <th>H1</th>
                        <th>H2</th>
                        <th>H3</th>
                        <th>H4</th>
                        <th>H5</th>
                        <th>H6</th>
                        <th>H7</th>
                        <th>H8</th>
                        <th>H9</th>
                        <th>H10</th>
                        <th>H11</th>
                        <th>H12</th>
                        <th>H13</th>
                        <th>H14</th>
                        <th>H15</th>
                        <th>H16</th>
                        <th>H17</th>
                        <th>H18</th>
                        <th>H19</th>
                        <th>H20</th>
                        <th>H21</th>
                        <th>H22</th>
                        <th>H23</th>
                        <th>H24</th>
                        <th>H25</th>
                        <th>H26</th>
                        <th>H27</th>
                        <th>H28</th>
                        <th>H29</th>
                        <th>H30</th>
                        <th>H31</th>
                    </tr>
                </thead>
                <tbody>
                    <tr>
                        <td className="text-center">UA</td>
                        <td className="text-center">{instantPowerQuality?.vanH1 != null ? instantPowerQuality?.vanH1 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.vanH2 != null ? instantPowerQuality?.vanH2 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.vanH3 != null ? instantPowerQuality?.vanH3 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.vanH4 != null ? instantPowerQuality?.vanH4 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.vanH5 != null ? instantPowerQuality?.vanH5 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.vanH6 != null ? instantPowerQuality?.vanH6 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.vanH7 != null ? instantPowerQuality?.vanH7 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.vanH8 != null ? instantPowerQuality?.vanH8 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.vanH9 != null ? instantPowerQuality?.vanH9 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.vanH10 != null ? instantPowerQuality?.vanH10 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.vanH11 != null ? instantPowerQuality?.vanH11 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.vanH12 != null ? instantPowerQuality?.vanH12 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.vanH13 != null ? instantPowerQuality?.vanH13 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.vanH14 != null ? instantPowerQuality?.vanH14 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.vanH15 != null ? instantPowerQuality?.vanH15 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.vanH16 != null ? instantPowerQuality?.vanH16 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.vanH17 != null ? instantPowerQuality?.vanH17 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.vanH18 != null ? instantPowerQuality?.vanH18 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.vanH19 != null ? instantPowerQuality?.vanH19 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.vanH20 != null ? instantPowerQuality?.vanH20 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.vanH21 != null ? instantPowerQuality?.vanH21 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.vanH22 != null ? instantPowerQuality?.vanH22 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.vanH23 != null ? instantPowerQuality?.vanH23 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.vanH24 != null ? instantPowerQuality?.vanH24 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.vanH25 != null ? instantPowerQuality?.vanH25 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.vanH26 != null ? instantPowerQuality?.vanH26 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.vanH27 != null ? instantPowerQuality?.vanH27 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.vanH28 != null ? instantPowerQuality?.vanH28 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.vanH29 != null ? instantPowerQuality?.vanH29 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.vanH30 != null ? instantPowerQuality?.vanH30 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.vanH31 != null ? instantPowerQuality?.vanH31 : "-"}</td>
                    </tr>
                    <tr>
                        <td className="text-center">UB</td>
                        <td className="text-center">{instantPowerQuality?.vbnH1 != null ? instantPowerQuality?.vanH1 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.vbnH2 != null ? instantPowerQuality?.vanH2 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.vbnH3 != null ? instantPowerQuality?.vanH3 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.vbnH4 != null ? instantPowerQuality?.vanH4 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.vbnH5 != null ? instantPowerQuality?.vanH5 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.vbnH6 != null ? instantPowerQuality?.vanH6 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.vbnH7 != null ? instantPowerQuality?.vanH7 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.vbnH8 != null ? instantPowerQuality?.vanH8 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.vbnH9 != null ? instantPowerQuality?.vanH9 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.vbnH10 != null ? instantPowerQuality?.vanH10 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.vbnH11 != null ? instantPowerQuality?.vanH11 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.vbnH12 != null ? instantPowerQuality?.vanH12 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.vbnH13 != null ? instantPowerQuality?.vanH13 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.vbnH14 != null ? instantPowerQuality?.vanH14 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.vbnH15 != null ? instantPowerQuality?.vanH15 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.vbnH16 != null ? instantPowerQuality?.vanH16 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.vbnH17 != null ? instantPowerQuality?.vanH17 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.vbnH18 != null ? instantPowerQuality?.vanH18 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.vbnH19 != null ? instantPowerQuality?.vanH19 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.vbnH20 != null ? instantPowerQuality?.vanH20 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.vbnH21 != null ? instantPowerQuality?.vanH21 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.vbnH22 != null ? instantPowerQuality?.vanH22 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.vbnH23 != null ? instantPowerQuality?.vanH23 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.vbnH24 != null ? instantPowerQuality?.vanH24 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.vbnH25 != null ? instantPowerQuality?.vanH25 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.vbnH26 != null ? instantPowerQuality?.vanH26 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.vbnH27 != null ? instantPowerQuality?.vanH27 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.vbnH28 != null ? instantPowerQuality?.vanH28 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.vbnH29 != null ? instantPowerQuality?.vanH29 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.vbnH30 != null ? instantPowerQuality?.vanH30 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.vbnH31 != null ? instantPowerQuality?.vanH31 : "-"}</td>
                    </tr>
                    <tr>
                        <td className="text-center">UC</td>
                        <td className="text-center">{instantPowerQuality?.vcnH1 != null ? instantPowerQuality?.vcnH1 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.vcnH2 != null ? instantPowerQuality?.vcnH2 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.vcnH3 != null ? instantPowerQuality?.vcnH3 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.vcnH4 != null ? instantPowerQuality?.vcnH4 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.vcnH5 != null ? instantPowerQuality?.vcnH5 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.vcnH6 != null ? instantPowerQuality?.vcnH6 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.vcnH7 != null ? instantPowerQuality?.vcnH7 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.vcnH8 != null ? instantPowerQuality?.vcnH8 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.vcnH9 != null ? instantPowerQuality?.vcnH9 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.vcnH10 != null ? instantPowerQuality?.vcnH10 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.vcnH11 != null ? instantPowerQuality?.vcnH11 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.vcnH12 != null ? instantPowerQuality?.vcnH12 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.vcnH13 != null ? instantPowerQuality?.vcnH13 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.vcnH14 != null ? instantPowerQuality?.vcnH14 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.vcnH15 != null ? instantPowerQuality?.vcnH15 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.vcnH16 != null ? instantPowerQuality?.vcnH16 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.vcnH17 != null ? instantPowerQuality?.vcnH17 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.vcnH18 != null ? instantPowerQuality?.vcnH18 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.vcnH19 != null ? instantPowerQuality?.vcnH19 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.vcnH20 != null ? instantPowerQuality?.vcnH20 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.vcnH21 != null ? instantPowerQuality?.vcnH21 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.vcnH22 != null ? instantPowerQuality?.vcnH22 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.vcnH23 != null ? instantPowerQuality?.vcnH23 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.vcnH24 != null ? instantPowerQuality?.vcnH24 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.vcnH25 != null ? instantPowerQuality?.vcnH25 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.vcnH26 != null ? instantPowerQuality?.vcnH26 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.vcnH27 != null ? instantPowerQuality?.vcnH27 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.vcnH28 != null ? instantPowerQuality?.vcnH28 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.vcnH29 != null ? instantPowerQuality?.vcnH29 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.vcnH30 != null ? instantPowerQuality?.vcnH30 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.vcnH31 != null ? instantPowerQuality?.vcnH31 : "-"}</td>
                    </tr>
                    <tr>
                        <td className="text-center">IA</td>
                        <td className="text-center">{instantPowerQuality?.iaH1 != null ? instantPowerQuality?.iaH1 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.iaH2 != null ? instantPowerQuality?.iaH2 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.iaH3 != null ? instantPowerQuality?.iaH3 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.iaH4 != null ? instantPowerQuality?.iaH4 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.iaH5 != null ? instantPowerQuality?.iaH5 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.iaH6 != null ? instantPowerQuality?.iaH6 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.iaH7 != null ? instantPowerQuality?.iaH7 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.iaH8 != null ? instantPowerQuality?.iaH8 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.iaH9 != null ? instantPowerQuality?.iaH9 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.iaH10 != null ? instantPowerQuality?.iaH10 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.iaH11 != null ? instantPowerQuality?.iaH11 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.iaH12 != null ? instantPowerQuality?.iaH12 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.iaH13 != null ? instantPowerQuality?.iaH13 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.iaH14 != null ? instantPowerQuality?.iaH14 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.iaH15 != null ? instantPowerQuality?.iaH15 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.iaH16 != null ? instantPowerQuality?.iaH16 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.iaH17 != null ? instantPowerQuality?.iaH17 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.iaH18 != null ? instantPowerQuality?.iaH18 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.iaH19 != null ? instantPowerQuality?.iaH19 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.iaH20 != null ? instantPowerQuality?.iaH20 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.iaH21 != null ? instantPowerQuality?.iaH21 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.iaH22 != null ? instantPowerQuality?.iaH22 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.iaH23 != null ? instantPowerQuality?.iaH23 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.iaH24 != null ? instantPowerQuality?.iaH24 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.iaH25 != null ? instantPowerQuality?.iaH25 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.iaH26 != null ? instantPowerQuality?.iaH26 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.iaH27 != null ? instantPowerQuality?.iaH27 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.iaH28 != null ? instantPowerQuality?.iaH28 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.iaH29 != null ? instantPowerQuality?.iaH29 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.iaH30 != null ? instantPowerQuality?.iaH30 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.iaH31 != null ? instantPowerQuality?.iaH31 : "-"}</td>
                    </tr>
                    <tr>
                        <td className="text-center">IB</td>
                        <td className="text-center">{instantPowerQuality?.ibH1 != null ? instantPowerQuality?.ibH1 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.ibH2 != null ? instantPowerQuality?.ibH2 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.ibH3 != null ? instantPowerQuality?.ibH3 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.ibH4 != null ? instantPowerQuality?.ibH4 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.ibH5 != null ? instantPowerQuality?.ibH5 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.ibH6 != null ? instantPowerQuality?.ibH6 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.ibH7 != null ? instantPowerQuality?.ibH7 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.ibH8 != null ? instantPowerQuality?.ibH8 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.ibH9 != null ? instantPowerQuality?.ibH9 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.ibH10 != null ? instantPowerQuality?.ibH10 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.ibH11 != null ? instantPowerQuality?.ibH11 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.ibH12 != null ? instantPowerQuality?.ibH12 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.ibH13 != null ? instantPowerQuality?.ibH13 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.ibH14 != null ? instantPowerQuality?.ibH14 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.ibH15 != null ? instantPowerQuality?.ibH15 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.ibH16 != null ? instantPowerQuality?.ibH16 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.ibH17 != null ? instantPowerQuality?.ibH17 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.ibH18 != null ? instantPowerQuality?.ibH18 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.ibH19 != null ? instantPowerQuality?.ibH19 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.ibH20 != null ? instantPowerQuality?.ibH20 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.ibH21 != null ? instantPowerQuality?.ibH21 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.ibH22 != null ? instantPowerQuality?.ibH22 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.ibH23 != null ? instantPowerQuality?.ibH23 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.ibH24 != null ? instantPowerQuality?.ibH24 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.ibH25 != null ? instantPowerQuality?.ibH25 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.ibH26 != null ? instantPowerQuality?.ibH26 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.ibH27 != null ? instantPowerQuality?.ibH27 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.ibH28 != null ? instantPowerQuality?.ibH28 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.ibH29 != null ? instantPowerQuality?.ibH29 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.ibH30 != null ? instantPowerQuality?.ibH30 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.ibH31 != null ? instantPowerQuality?.ibH31 : "-"}</td>
                    </tr>
                    <tr>
                        <td className="text-center">IC</td>
                        <td className="text-center">{instantPowerQuality?.icH1 != null ? instantPowerQuality?.icH1 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.icH2 != null ? instantPowerQuality?.icH2 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.icH3 != null ? instantPowerQuality?.icH3 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.icH4 != null ? instantPowerQuality?.icH4 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.icH5 != null ? instantPowerQuality?.icH5 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.icH6 != null ? instantPowerQuality?.icH6 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.icH7 != null ? instantPowerQuality?.icH7 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.icH8 != null ? instantPowerQuality?.icH8 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.icH9 != null ? instantPowerQuality?.icH9 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.icH10 != null ? instantPowerQuality?.icH10 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.icH11 != null ? instantPowerQuality?.icH11 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.icH12 != null ? instantPowerQuality?.icH12 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.icH13 != null ? instantPowerQuality?.icH13 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.icH14 != null ? instantPowerQuality?.icH14 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.icH15 != null ? instantPowerQuality?.icH15 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.icH16 != null ? instantPowerQuality?.icH16 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.icH17 != null ? instantPowerQuality?.icH17 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.icH18 != null ? instantPowerQuality?.icH18 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.icH19 != null ? instantPowerQuality?.icH19 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.icH20 != null ? instantPowerQuality?.icH20 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.icH21 != null ? instantPowerQuality?.icH21 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.icH22 != null ? instantPowerQuality?.icH22 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.icH23 != null ? instantPowerQuality?.icH23 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.icH24 != null ? instantPowerQuality?.icH24 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.icH25 != null ? instantPowerQuality?.icH25 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.icH26 != null ? instantPowerQuality?.icH26 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.icH27 != null ? instantPowerQuality?.icH27 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.icH28 != null ? instantPowerQuality?.icH28 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.icH29 != null ? instantPowerQuality?.icH29 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.icH30 != null ? instantPowerQuality?.icH30 : "-"}</td>
                        <td className="text-center">{instantPowerQuality?.icH31 != null ? instantPowerQuality?.icH31 : "-"}</td>
                    </tr>
                </tbody>
            </table>
        </>
    )
}

export default Instance;