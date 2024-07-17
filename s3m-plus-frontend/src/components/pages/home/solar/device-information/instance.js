import moment from "moment";
import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import CONS from "../../../../../constants/constant";
import OperationInformationService from "../../../../../services/OperationInformationService";
import converter from "../../../../../common/converter";

const Instance = () => {
    const param = useParams();

    const [state, setState] = useState(0);

    const [instantInverted, setInstantInverted] = useState({});

    const [instantWeather, setInstantWeather] = useState({});

    const [instantCombiner, setInstantCombiner] = useState({});

    const [instantString, setInstantString] = useState({});

    const [instantPanel, setInstantPanel] = useState({});

    const [viewTypeTable, setViewTypeTable] = useState(null);

    const getInstantInverterPV = async () => {
        if (parseInt(param.deviceId) === 0) {
            return
        }
        let res = await OperationInformationService.getInstantOperationInverterPV(param.customerId, param.deviceId);
        if (res.status === 200) {
            let data = res.data;

            let objValueCompare = {
                w: data.w,
                wh: data.wh,
                dcw: data.dcw
            }

            let values = Object.values(objValueCompare).filter(value => value !== null && value > 0);

            let min = Math.min(...values);

            let viewTypeModal = converter.setViewType(min);

            setViewTypeTable(viewTypeModal);

            setInstantInverted(res.data);
        } else {
            setInstantInverted([]);
        }
    }

    const getInstantWeather = async () => {
        if (parseInt(param.deviceId) === 0) {
            return
        }
        let res = await OperationInformationService.getInstantOperationWeatherPV(param.customerId, param.deviceId);
        if (res.status === 200) {
            setInstantWeather(res.data);
        } else {
            setInstantWeather([]);
        }
    }

    const getInstantCombiner = async () => {
        if (parseInt(param.deviceId) === 0) {
            return
        }
        let res = await OperationInformationService.getInstantOperationCombinerPV(param.customerId, param.deviceId);
        if (res.status === 200) {
            setInstantCombiner(res.data);
        } else {
            setInstantCombiner([]);
        }
    }

    const getInstantString = async () => {
        if (parseInt(param.deviceId) === 0) {
            return
        }
        let res = await OperationInformationService.getInstantOperationStringPV(param.customerId, param.deviceId);
        if (res.status === 200) {
            setInstantString(res.data);
        } else {
            setInstantString([]);
        }
    }

    const getInstantPanel = async () => {
        if (parseInt(param.deviceId) === 0) {
            return
        }
        let res = await OperationInformationService.getInstantOperationPanelPV(param.customerId, param.deviceId);
        console.log(res.data);
        if (res.status === 200) {
            setInstantPanel(res.data);
        } else {
            setInstantPanel([]);
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
        if (deviceType === CONS.DEVICE_TYPE_PV.INVERTER) {
            getInstantInverterPV();
        }
        if (deviceType === CONS.DEVICE_TYPE_PV.WEARTHER) {
            getInstantWeather();
        }
        if (deviceType === CONS.DEVICE_TYPE_PV.COMBINER) {
            getInstantCombiner();
        }
        if (deviceType === CONS.DEVICE_TYPE_PV.STRING) {
            getInstantString();
        }
        if (deviceType === CONS.DEVICE_TYPE_PV.PANEL) {
            getInstantPanel();
        }

        document.title = "Thông tin thiết bị - Tức thời";
    }, [state, param.deviceId, param.projectId, param.customerId])

    return (
        <>
            {parseInt(param.deviceType) === CONS.DEVICE_TYPE_PV.INVERTER &&
                <>
                    <div className="tab-title">
                        <div className="latest-time mt-3">
                            <i className="fa-regular fa-clock"></i>&nbsp;{instantInverted?.sentDate != null ? moment(instantInverted?.sentDate).format(CONS.DATE_FORMAT_OPERATE) : ""}
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
                                <th colSpan="10" className="tbl-title">Thông số Inverter</th>
                            </tr>
                        </thead>
                    </table>
                    <table className="table tbl-overview tbl-tsd">
                        <thead style={{ display: "table-header-group", width: "100%", tableLayout: "fixed" }}>
                            <tr>
                                <th width="40px">Pha</th>
                                <th width="60px">U<sub>AC</sub> [V]</th>
                                <th width="60px">I<sub>AC</sub> [A]</th>
                                <th width="50px">PF</th>
                                <th width="80px">P<sub>AC</sub> [KW]</th>
                                <th width="80px">Q<sub>AC</sub> [KW]</th>
                                <th width="120px">P<sub>AC_Total</sub> [KW]</th>
                                <th width="120px">Q<sub>AC_Total</sub> [KW]</th>
                                <th width="60px">U<sub>DC</sub> [V]</th>
                                <th width="60px">I<sub>DC</sub> [A]</th>
                                <th width="80px">P<sub>DC</sub> [KW]</th>
                                <th width="120px">Hiệu suất [%]</th>
                                <th width="50px">F [Hz]</th>
                                <th>Yield [kWh]</th>
                                <th width="120px">T<sub>Cabnet</sub> [°C]</th>
                                <th width="120px">T<sub>Heatsink</sub> [°C]</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr>
                                <td className="text-center">A</td>
                                <td className="text-center">{instantInverted?.va != null ? instantInverted?.va : "-"}</td>
                                <td className="text-center">{instantInverted?.ia != null ? instantInverted?.ia : "-"}</td>
                                <td className="text-center">{instantInverted?.pfa != null ? instantInverted?.pfa : "-"}</td>
                                <td className="text-center">{instantInverted?.pa != null ? instantInverted?.pa : "-"}</td>
                                <td className="text-center">{instantInverted?.qa != null ? instantInverted?.qa : "-"}</td>
                                <td className="text-center" rowSpan={3}>{instantInverted?.ptotal != null ? instantInverted?.ptotal : "-"}</td>
                                <td className="text-center" rowSpan={3}>{instantInverted?.qtotal != null ? instantInverted?.qtotal : "-"}</td>
                                <td className="text-center" rowSpan={3}>{instantInverted?.udc != null ? instantInverted?.udc : "-"}</td>
                                <td className="text-center" rowSpan={3}>{instantInverted?.idc != null ? instantInverted?.idc : "-"}</td>
                                <td className="text-center" rowSpan={3}>{instantInverted?.pdc != null ? instantInverted?.pdc : "-"}</td>
                                <td className="text-center" rowSpan={3}>-</td>
                                <td className="text-center" rowSpan={3}>{instantInverted?.f != null ? instantInverted?.f : "-"}</td>
                                <td className="text-center" rowSpan={3}>{instantInverted?.ep != null ? instantInverted?.ep : "-"}</td>
                                <td className="text-center" rowSpan={3}>{instantInverted?.tmpCab != null ? instantInverted?.tmpCab : "-"}</td>
                                <td className="text-center" rowSpan={3}>{instantInverted?.tmpSnk != null ? instantInverted?.tmpSnk : "-"}</td>
                            </tr>
                            <tr>
                                <td className="text-center">B</td>
                                <td className="text-center">{instantInverted?.vb != null ? instantInverted?.vb : "-"}</td>
                                <td className="text-center">{instantInverted?.ib != null ? instantInverted?.ib : "-"}</td>
                                <td className="text-center">{instantInverted?.pfb != null ? instantInverted?.pfb : "-"}</td>
                                <td className="text-center">{instantInverted?.pb != null ? instantInverted?.pb : "-"}</td>
                                <td className="text-center">{instantInverted?.qb != null ? instantInverted?.qb : "-"}</td>
                            </tr>
                            <tr>
                                <td className="text-center">C</td>
                                <td className="text-center">{instantInverted?.vc != null ? instantInverted?.vc : "-"}</td>
                                <td className="text-center">{instantInverted?.ic != null ? instantInverted?.ic : "-"}</td>
                                <td className="text-center">{instantInverted?.pfc != null ? instantInverted?.pfc : "-"}</td>
                                <td className="text-center">{instantInverted?.pc != null ? instantInverted?.pc : "-"}</td>
                                <td className="text-center">{instantInverted?.qc != null ? instantInverted?.qc : "-"}</td>
                            </tr>
                        </tbody>
                    </table>
                </>
            }
            {parseInt(param.deviceType) === CONS.DEVICE_TYPE_PV.WEARTHER &&
                <>
                    <div className="tab-title">
                        <div className="latest-time mt-3">
                            <i className="fa-regular fa-clock"></i>&nbsp;{instantWeather?.sentDate != null ? moment(instantWeather?.sentDate).format(CONS.DATE_FORMAT_OPERATE) : ""}
                        </div>
                        <div className="latest-warning">
                            <button type="button" className="btn btn-outline-info" style={{ width: "34.5px", height: "29px", paddingLeft: "10px", fontSize: "13px" }} onClick={() => Interval()}>
                                <i className="fa-solid fa-rotate-right"></i>
                            </button>
                        </div>
                    </div>

                    <table className="table tbl-overview tbl-tsnd mt-1">
                        <thead>
                            <tr>
                                <th colSpan="4" className="tbl-title">Thông số thời tiết</th>
                            </tr>
                        </thead>
                    </table>
                    <table className="table tbl-overview tbl-tsnd">
                        <thead style={{ display: "table-header-group", width: "100%", tableLayout: "fixed" }}>
                            <tr>
                                <th colSpan={2}>Nhiệt độ [°C]</th>
                                <th colSpan={2}>Bức xạ [W/m2]</th>
                                <th colSpan={2}>Độ ẩm [%]</th>
                                <th colSpan={2}>Tốc độ gió [m/s]</th>
                                <th colSpan={2}>Áp suất [atm]</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr>
                                <td className="text-center" colSpan={2} height="70px">{instantWeather?.temp != null ? instantWeather?.temp : "-"}</td>
                                <td className="text-center" colSpan={2} height="70px">{instantWeather?.rad != null ? instantWeather?.rad : "-"}</td>
                                <td className="text-center" colSpan={2} height="70px">{instantWeather?.h != null ? instantWeather?.h : "-"}</td>
                                <td className="text-center" colSpan={2} height="70px">{instantWeather?.wind_sp != null ? instantWeather?.wind_sp : "-"}</td>
                                <td className="text-center" colSpan={2} height="70px">{instantWeather?.atmos != null ? instantWeather?.atmos : "-"}</td>
                            </tr>
                        </tbody>
                    </table>
                </>
            }
            {parseInt(param.deviceType) === CONS.DEVICE_TYPE_PV.COMBINER &&
                <>
                    <div className="tab-title">
                        <div className="latest-time mt-3">
                            <i className="fa-regular fa-clock"></i>&nbsp;{instantCombiner?.sentDate != null ? moment(instantCombiner?.sentDate).format(CONS.DATE_FORMAT_OPERATE) : ""}
                        </div>
                        <div className="latest-warning">
                            <button type="button" className="btn btn-outline-info" style={{ width: "34.5px", height: "29px", paddingLeft: "10px", fontSize: "13px" }} onClick={() => Interval()}>
                                <i className="fa-solid fa-rotate-right"></i>
                            </button>
                        </div>
                    </div>
                    <table className="table tbl-overview tbl-tsnd mt-1">
                        <thead>
                            <tr>
                                <th colSpan="4" className="tbl-title">Thông số Combiner BOX</th>
                            </tr>
                        </thead>
                    </table>
                    <table className="table tbl-overview tbl-tsnd">
                        <thead style={{ display: "table-header-group", width: "100%", tableLayout: "fixed" }}>
                            <tr>
                                <th className="text-center">U<sub>DC</sub> [V]</th>
                                <th className="text-center">I<sub>DC</sub> [A]</th>
                                <th className="text-center">P [kW]</th>
                                <th className="text-center">E [kWh]</th>
                                <th className="text-center">Nhiệt độ [°C]</th>
                                <th className="text-center">Hiệu suất [%]</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr>
                                <td className="text-center" height="70px">{instantCombiner?.vdcCombiner != null ? instantCombiner?.vdcCombiner : "-"}</td>
                                <td className="text-center" height="70px">{instantCombiner?.idcCombiner != null ? instantCombiner?.idcCombiner : "-"}</td>
                                <td className="text-center" height="70px">{instantCombiner?.pdcCombiner != null ? instantCombiner?.pdcCombiner : "-"}</td>
                                <td className="text-center" height="70px">{instantCombiner?.epCombiner != null ? instantCombiner?.epCombiner : "-"}</td>
                                <td className="text-center" height="70px">{instantCombiner?.t != null ? instantCombiner?.t : "-"}</td>
                                <td className="text-center" height="70px">-</td>
                            </tr>
                        </tbody>
                    </table>
                </>
            }
            {parseInt(param.deviceType) === CONS.DEVICE_TYPE_PV.STRING &&
                <>
                    <div className="tab-title">
                        <div className="latest-time mt-3">
                            <i className="fa-regular fa-clock"></i>&nbsp;{instantString?.sentDate != null ? moment(instantString?.sentDate).format(CONS.DATE_FORMAT_OPERATE) : ""}
                        </div>
                        <div className="latest-warning">
                            <button type="button" className="btn btn-outline-info" style={{ width: "34.5px", height: "29px", paddingLeft: "10px", fontSize: "13px" }} onClick={() => Interval()}>
                                <i className="fa-solid fa-rotate-right"></i>
                            </button>
                        </div>
                    </div>
                    <table className="table tbl-overview tbl-tsnd mt-1">
                        <thead>
                            <tr>
                                <th colSpan="6" className="tbl-title">Thông số String</th>
                            </tr>
                        </thead>
                    </table>
                    <table className="table tbl-overview tbl-tsnd">
                        <thead style={{ display: "table-header-group", width: "100%", tableLayout: "fixed" }}>
                            <tr>
                                <th className="text-center">U<sub>DC</sub> [V]</th>
                                <th className="text-center">I<sub>DC</sub> [A]</th>
                                <th className="text-center">P [kW]</th>
                                <th className="text-center">E [kWh]</th>
                                <th className="text-center">Nhiệt độ [°C]</th>
                                <th className="text-center">Hiệu suất [%]</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr>
                                <td className="text-center" height="70px">{instantString?.vdcStr != null ? instantString?.vdcStr : "-"}</td>
                                <td className="text-center" height="70px">{instantString?.idcStr != null ? instantString?.idcStr : "-"}</td>
                                <td className="text-center" height="70px">{instantString?.pdcStr != null ? instantString?.pdcStr : "-"}</td>
                                <td className="text-center" height="70px">{instantString?.epStr != null ? instantString?.epStr : "-"}</td>
                                <td className="text-center" height="70px">{instantString?.tstr != null ? instantString?.tstr : "-"}</td>
                                <td className="text-center" height="70px">-</td>
                            </tr>
                        </tbody>
                    </table>
                </>
            }

            {parseInt(param.deviceType) === CONS.DEVICE_TYPE_PV.PANEL &&
                <>
                    <div className="tab-title">
                        <div className="latest-time mt-3">
                            <i className="fa-regular fa-clock"></i>&nbsp;{instantPanel?.sentDate != null ? moment(instantPanel?.sentDate).format(CONS.DATE_FORMAT_OPERATE) : ""}
                        </div>
                        <div className="latest-warning">
                            <button type="button" className="btn btn-outline-info" style={{ width: "34.5px", height: "29px", paddingLeft: "10px", fontSize: "13px" }} onClick={() => Interval()}>
                                <i className="fa-solid fa-rotate-right"></i>
                            </button>
                        </div>
                    </div>
                    <table className="table tbl-overview tbl-tsnd mt-1">
                        <thead>
                            <tr>
                                <th colSpan="4" className="tbl-title">Thông số Panel</th>
                            </tr>
                        </thead>
                    </table>
                    <table className="table tbl-overview tbl-tsnd">
                        <thead style={{ display: "table-header-group", width: "100%", tableLayout: "fixed" }}>
                            <tr>
                                <th className="text-center">U<sub>DC</sub> [V]</th>
                                <th className="text-center">I<sub>DC</sub> [A]</th>
                                <th className="text-center">P [kW]</th>
                                <th className="text-center">E [kWh]</th>
                                <th className="text-center">Nhiệt độ [°C]</th>
                                <th className="text-center">Hiệu suất [%]</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr>
                                <td className="text-center" height="70px">{instantPanel?.u != null ? instantPanel?.u : "-"}</td>
                                <td className="text-center" height="70px">{instantPanel?.i != null ? instantPanel?.i : "-"}</td>
                                <td className="text-center" height="70px">{instantPanel?.p != null ? instantPanel?.p : "-"}</td>
                                <td className="text-center" height="70px">-</td>
                                <td className="text-center" height="70px">{instantPanel?.t != null ? instantPanel?.t : "-"}</td>
                                <td className="text-center" height="70px">-</td>
                            </tr>
                        </tbody>
                    </table>
                </>
            }
        </>
    )
}

export default Instance;