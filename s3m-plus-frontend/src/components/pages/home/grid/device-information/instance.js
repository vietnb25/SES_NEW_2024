import moment from "moment";
import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import CONS from "../../../../../constants/constant";
import OperationInformationService from "../../../../../services/OperationInformationService";
import converter from "../../../../../common/converter";

const Instance = () => {
    const param = useParams();

    const [state, setState] = useState(0);

    const [instantRmuDrawer, setInstantRmuDrawer] = useState({
        id: "",
        deviceId: "",
        sawId1: "",
        sawId2: "",
        sawId3: "",
        sawId4: "",
        sawId5: "",
        sawId6: "",
        gAMean: "",
        gAAlarm: "",
        gBMean: "",
        gBAlarm: "",
        alarmStatus: "",
        lfbRatio: "",
        lfbEppc: "",
        mfbRatio: "",
        mlfbEppc: "",
        hlfbRatio: "",
        hlfbEppc: "",
        meanRatio: "",
        meanEppc: "",
        indicator: "",
        t: "",
        h: "",
        uab: "",
        ubc: "",
        uca: "",
        uan: "",
        ubn: "",
        ucn: "",
        ia: "",
        ib: "",
        ic: "",
        pa: "",
        pb: "",
        pc: "",
        qa: "",
        qb: "",
        qc: "",
        sa: "",
        sb: "",
        sc: "",
        pfa: "",
        pfb: "",
        pfc: "",
        f: "",
        ep: "",
        eq: "",
        es: "",
        thdIa: "",
        thdIb: "",
        thdIc: "",
        thdVab: "",
        thdVbc: "",
        thdVca: "",
        thdVan: "",
        thdVbn: "",
        thdVcn: "",
        sentDate: "",
        transactionDate: "",
    });

    const [viewTypeTable, setViewTypeTable] = useState(null);

    const getInstantOperationRmuDrawer = async () => {
        if (parseInt(param.deviceId) === 0) {
            return
        }

        let res = await OperationInformationService.getInstantOperationRmuDrawerGrid(param.customerId, param.deviceId);

        if (res.status === 200) {
            let data = res.data;
            setInstantRmuDrawer(res.data);
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
        } else {
            setInstantRmuDrawer([]);
        }
    }

    function Interval() {
        setTimeout(() => {
            setState(state + 1);
        }, 15000);
    }

    useEffect(() => {
        Interval();
        let deviceType = parseInt(param.deviceType);
        if (deviceType === CONS.DEVICE_TYPE_GRID.RMU_DRAWER) {
            getInstantOperationRmuDrawer();
        }
        document.title = "Thông tin thiết bị - Tức thời";
    }, [state, param.deviceId, param.projectId, param.customerId])

    return (
        <>
            <div className="tab-title">
                <div className="latest-time mt-3">
                    <i className="fa-regular fa-clock"></i>&nbsp;{instantRmuDrawer?.sentDate != null ? moment(instantRmuDrawer?.sentDate).format(CONS.DATE_FORMAT_OPERATE) : ""}
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
                        <td className="text-center">{instantRmuDrawer?.uan != null && instantRmuDrawer?.uan >= 0 ? instantRmuDrawer?.uan : "-"}</td>
                        <td className="text-center">{instantRmuDrawer?.ia != null && instantRmuDrawer?.ia >= 0 ? instantRmuDrawer?.ia : "-"}</td>
                        <td className="text-center">-</td>
                        <td className="text-center">{instantRmuDrawer?.pa !== null && instantRmuDrawer?.pa >= -2000000 ? instantRmuDrawer?.pa : "-"}</td>
                        <td className="text-center">{instantRmuDrawer?.qa !== null && instantRmuDrawer?.qa >= -2000000 ? instantRmuDrawer?.qa : "-"}</td>
                        <td className="text-center">{instantRmuDrawer?.sa !== null && instantRmuDrawer?.sa >= -2000000 ? instantRmuDrawer?.sa : "-"}</td>
                        <td className="text-center">{instantRmuDrawer?.pfa != null && instantRmuDrawer?.pfa >= -1 && instantRmuDrawer?.pfa <= 1 ? instantRmuDrawer?.pfa : "-"}</td>
                        <td className="text-center">{instantRmuDrawer?.thdVab != null && instantRmuDrawer?.thdVab >= 0 ? instantRmuDrawer?.thdVab : "-"}</td>
                        <td className="text-center">{instantRmuDrawer?.thdIa != null && instantRmuDrawer?.thdIa >= 0 ? instantRmuDrawer?.thdIa : "-"}</td>
                        <td className="text-center">-</td>
                        <td className="text-center" rowSpan={3}>{instantRmuDrawer?.f != null && instantRmuDrawer?.thdIa >= 45 && instantRmuDrawer?.thdIa <= 65 ? instantRmuDrawer?.f : "-"}</td>
                        <td className="text-center" rowSpan={3}>-</td>
                        <td className="text-center" rowSpan={3}>-</td>
                        <td rowSpan={3} className="text-center">{instantRmuDrawer?.ep === null && instantRmuDrawer?.ep >= 0 ? "-" : instantRmuDrawer?.ep}</td>
                    </tr>
                    <tr>
                        <td className="text-center">B</td>
                        <td className="text-center">{instantRmuDrawer?.ubn != null && instantRmuDrawer?.ubn >= 0 ? instantRmuDrawer?.ubn : "-"}</td>
                        <td className="text-center">{instantRmuDrawer?.ib != null && instantRmuDrawer?.ia >= 0 ? instantRmuDrawer?.ib : "-"}</td>
                        <td className="text-center">-</td>
                        <td className="text-center">{instantRmuDrawer?.pb !== null && instantRmuDrawer?.pb >= -2000000 ? instantRmuDrawer?.pb : "-"}</td>
                        <td className="text-center">{instantRmuDrawer?.qb !== null && instantRmuDrawer?.qb >= -2000000 ? instantRmuDrawer?.qb : "-"}</td>
                        <td className="text-center">{instantRmuDrawer?.sb !== null && instantRmuDrawer?.sb >= -2000000 ? instantRmuDrawer?.sb : "-"}</td>
                        <td className="text-center">{instantRmuDrawer?.pfb != null && instantRmuDrawer?.pfb >= -1 && instantRmuDrawer?.pfb <= 1 ? instantRmuDrawer?.pfb : "-"}</td>
                        <td className="text-center">{instantRmuDrawer?.thdVbc != null && instantRmuDrawer?.thdVbc >= 0 ? instantRmuDrawer?.thdVbc : "-"}</td>
                        <td className="text-center">{instantRmuDrawer?.thdIb != null && instantRmuDrawer?.thdIb >= 0 ? instantRmuDrawer?.thdIb : "-"}</td>
                        <td className="text-center">-</td>
                    </tr>
                    <tr>
                        <td className="text-center">C</td>
                        <td className="text-center">{instantRmuDrawer?.ucn != null && instantRmuDrawer?.ucn >= 0 ? instantRmuDrawer?.ucn : "-"}</td>
                        <td className="text-center">{instantRmuDrawer?.ic != null && instantRmuDrawer?.ic >= 0 ? instantRmuDrawer?.ic : "-"}</td>
                        <td className="text-center">-</td>
                        <td className="text-center">{instantRmuDrawer?.pc !== null && instantRmuDrawer?.pc >= -2000000 ? instantRmuDrawer?.pc : "-"}</td>
                        <td className="text-center">{instantRmuDrawer?.qc !== null && instantRmuDrawer?.qc >= -2000000 ? instantRmuDrawer?.qc : "-"}</td>
                        <td className="text-center">{instantRmuDrawer?.sc !== null && instantRmuDrawer?.sc >= -2000000 ? instantRmuDrawer?.sc : "-"}</td>
                        <td className="text-center">{instantRmuDrawer?.pfc != null && instantRmuDrawer?.pfc >= -1 && instantRmuDrawer?.pfc <= 1 ? instantRmuDrawer?.pfc : "-"}</td>
                        <td className="text-center">{instantRmuDrawer?.thdVca != null && instantRmuDrawer?.thdVca >= 0 ? instantRmuDrawer?.thdVca : "-"}</td>
                        <td className="text-center">{instantRmuDrawer?.thdIc != null && instantRmuDrawer?.thdIc >= 0 ? instantRmuDrawer?.thdIc : "-"}</td>
                        <td className="text-center">-</td>
                    </tr>
                </tbody>
            </table>

            <table className="table tbl-overview tbl-tsnd mt-3">
                <thead>
                    <tr>
                        <th colSpan="4" className="tbl-title">Thông số nhiệt độ</th>
                    </tr>
                </thead>
            </table>
            <table className="table tbl-overview tbl-tsnd">
                <thead style={{ display: "table-header-group", width: "100%", tableLayout: "fixed" }}>
                    <tr>
                        <th width="40px">Pha</th>
                        <th>Nhiệt độ cực trên [°C]</th>
                        <th>Nhiệt độ cực dưới [°C]</th>
                        <th>Nhiệt độ khoang [°C]</th>
                        <th>Độ ẩm [%]</th>
                    </tr>
                </thead>
                <tbody>
                    <tr>
                        <td className="text-center">A</td>
                        <td className="text-center">{instantRmuDrawer?.sawId1 && parseInt(instantRmuDrawer?.sawId1) >= -50 && parseInt(instantRmuDrawer?.sawId1) <= 180 != null ? instantRmuDrawer?.sawId1 : "-"}</td>
                        <td className="text-center">{instantRmuDrawer?.sawId4 && parseInt(instantRmuDrawer?.sawId4) >= -50 && parseInt(instantRmuDrawer?.sawId4) <= 180 != null ? instantRmuDrawer?.sawId4 : "-"}</td>
                        <td rowSpan={3} className="text-center">{instantRmuDrawer?.t && parseInt(instantRmuDrawer?.t) >= -50 && instantRmuDrawer?.t <= 180 != null ? instantRmuDrawer?.t : "-"}</td>
                        <td rowSpan={3} className="text-center">{instantRmuDrawer?.h && parseInt(instantRmuDrawer?.h) >= 0 && instantRmuDrawer?.h <= 100 != null ? instantRmuDrawer?.h : "-"}</td>
                    </tr>
                    <tr>
                        <td className="text-center">B</td>
                        <td className="text-center">{instantRmuDrawer?.sawId2 && parseInt(instantRmuDrawer?.sawId2) >= -50 && parseInt(instantRmuDrawer?.sawId2) <= 180 != null ? instantRmuDrawer?.sawId2 : "-"}</td>
                        <td className="text-center">{instantRmuDrawer?.sawId5 && parseInt(instantRmuDrawer?.sawId5) >= -50 && parseInt(instantRmuDrawer?.sawId5) <= 180 != null ? instantRmuDrawer?.sawId5 : "-"}</td>
                    </tr>
                    <tr>
                        <td className="text-center">C</td>
                        <td className="text-center">{instantRmuDrawer?.sawId3 && parseInt(instantRmuDrawer?.sawId3) >= -50 && parseInt(instantRmuDrawer?.sawId3) <= 180 != null ? instantRmuDrawer?.sawId3 : "-"}</td>
                        <td className="text-center">{instantRmuDrawer?.sawId6 && parseInt(instantRmuDrawer?.sawId6) >= -50 && parseInt(instantRmuDrawer?.sawId6) <= 180 != null ? instantRmuDrawer?.sawId6 : "-"}</td>
                    </tr>
                </tbody>
            </table>
            <table className="table tbl-overview tbl-tsd mt-3">
                <thead>
                    <tr>
                        <th colSpan="32" className="tbl-title">Thông số phóng điện </th>
                    </tr>
                </thead>
            </table>
            <table className="table tbl-overview tbl-tsd">
                <thead style={{ display: "table-header-group", width: "100%", tableLayout: "fixed" }}>
                    <tr>
                        <th></th>
                        <th>LFB ratio [dB]</th>
                        <th>LFB EPPC</th>
                        <th>MFB Ratio [dB]</th>
                        <th>MFB EPPC</th>
                        <th>HFB ratio [dB]</th>
                        <th>HFB EPPC</th>
                    </tr>
                </thead>
                <tbody>
                    <tr>
                        <td>Giá trị</td>
                        <td className="text-center">{instantRmuDrawer?.lfbRatio != null && instantRmuDrawer?.lfbRatio >= 0 ? instantRmuDrawer?.lfbRatio : "-"}</td>
                        <td className="text-center">{instantRmuDrawer?.lfbEppc != null && instantRmuDrawer?.lfbRatio >= 0 ? instantRmuDrawer?.lfbEppc : "-"}</td>
                        <td className="text-center">{instantRmuDrawer?.mfbRatio != null && instantRmuDrawer?.lfbRatio >= 0 ? instantRmuDrawer?.mfbRatio : "-"}</td>
                        <td className="text-center">{instantRmuDrawer?.mlfbEppc != null && instantRmuDrawer?.lfbRatio >= 0 ? instantRmuDrawer?.mlfbEppc : "-"}</td>
                        <td className="text-center">{instantRmuDrawer?.hlfbRatio != null && instantRmuDrawer?.lfbRatio >= 0 ? instantRmuDrawer?.hlfbRatio : "-"}</td>
                        <td className="text-center">{instantRmuDrawer?.hlfbEppc != null && instantRmuDrawer?.lfbRatio >= 0 ? instantRmuDrawer?.hlfbEppc : "-"}</td>
                    </tr>
                    <tr>
                        <td>Trung bình Ratio</td>
                        <td className="text-center" colSpan={6}>{instantRmuDrawer?.meanRatio != null && instantRmuDrawer?.lfbRatio >= 0 ? instantRmuDrawer?.meanRatio : "-"}</td>
                    </tr>
                    <tr>
                        <td>Trung bình EPPC</td>
                        <td className="text-center" colSpan={6}>{instantRmuDrawer?.meanEppc != null && instantRmuDrawer?.lfbRatio >= 0 ? instantRmuDrawer?.meanEppc : "-"}</td>
                    </tr>
                    <tr>
                        <td>Mức chỉ thị</td>
                        <td className="text-center" colSpan={6}>{instantRmuDrawer?.indicator != null && instantRmuDrawer?.lfbRatio >= 0 && instantRmuDrawer?.lfbRatio <= 3 ? instantRmuDrawer?.indicator : "-"}</td>
                    </tr>
                </tbody>
            </table>
        </>
    )
}

export default Instance;