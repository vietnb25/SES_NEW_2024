import React, { useEffect, useState } from 'react';
import overviewLoadService from "../../../../../services/OverviewLoadService";
import OperationInformationService from "../../../../../services/OperationInformationService";
import { useParams } from "react-router";
import { Link, useHistory } from "react-router-dom";
import moment from 'moment';
import $ from "jquery";
import CONS from "../../../../../constants/constant";
import Converter from "../../../../../common/converter";
import './index.css';
const ControlLoad = ({ projectInfo }) => {
    const param = useParams();
    const history = useHistory();

    const [powers, setPower] = useState([]);
    const [powersWarning, setPowersWarning] = useState([]);

    const [viewTypeTable, setViewTypeTable] = useState(null);
    const [viewTypeModel, setViewTypeModel] = useState(null);

    const getPowers = async () => {
        let keyword = document.getElementById('keyword').value;
        let res = await overviewLoadService.getPower(param.customerId, param.projectId, keyword);

        if (res.status === 200) {
            console.log(res.data.deviceList);
            setPower(res.data.deviceList);
            setPowersWarning(res.data.deviceList);
            setViewType(res.data.deviceList);
        }
    };

    const [instantOperationInfo, setInstantOperationInfo] = useState({
        id: "",
        deviceId: "",
        deviceName: "",
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
        transactionDate: ""
    });

    const setViewType = async (data) => {
        let values = [];

        data.forEach(item => {
            if (item.ptotal && item.ptotal > 0) {
                values.push(item.ptotal);
            }
            if (item.ep && item.ep > 0) {
                values.push(item.ep);
            }
        });

        let min = Math.min(...values);

        let viewTypeTable = Converter.setViewType(values.length > 0 ? min : 0);

        setViewTypeTable(viewTypeTable);
    }
    const getOperationInformation = async (deviceId) => {
        let res = await OperationInformationService.getInstantOperationInformation(param.customerId,deviceId);
        if (res.status === 200) {
            setInstantOperationInfo(res.data);
            // convert

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

            let viewTypeModal = Converter.setViewType(min);

            setViewTypeModel(viewTypeModal);
        } else {
            setInstantOperationInfo([]);
        }
    };
    useEffect(() => {
        document.title = "Control Load";
        getPowers();
    }, [param.customerId, param.projectId]);
    return (
        <>
            <div className="tab-content">
                <div className="project-infor" style={{ padding: "0px 10px", display: "block", marginTop: "10px" }}>
                    <span className="project-tree">{projectInfo}</span>
                </div>
                {/* <label className="switch">
                    <input type="checkbox" Checked/>
                        <span className="slider round"></span>
                </label> */}
                <div id="main-search">
                    <div className="input-group search-item mb-3 float-left">
                        <div className="input-group-prepend">
                            <span className="input-group-text" id="inputGroup-sizing-default">Tìm kiếm</span>
                        </div>
                        <input type="text" id="keyword" className="form-control" aria-label="Mô tả" aria-describedby="inputGroup-sizing-sm" onKeyDown={e => e.key === "Enter" && getPowers(e)} />
                    </div>

                    <div className="search-buttons float-left">
                        <button type="button" className="btn btn-outline-secondary" onClick={getPowers}>
                            <i className="fa-solid fa-magnifying-glass"></i>
                        </button>
                    </div>
                </div>
                <table className="table tbl-overview tbl-power mt-5">
                    <thead style={{ display: "table", width: "100%", tableLayout: "fixed" }}>
                        <tr>
                            <th width="40px">TT</th>
                            <th>Thành Phần</th>
                            <th width="150px">Trạng Thái</th>
                            <th width="150px">Trạng Thái MCCB</th>
                            <th width="150px">Vị Trí</th>
                            <th width="70px"><i className="fa-regular fa-hand"></i></th>
                        </tr>
                    </thead>
                    <tbody style={{ display: "block", maxHeight: "300px", overflow: "auto" }}>
                        {
                            powers?.map(
                                (power, index) => <tr key={index} style={{ display: "table", width: "100%", tableLayout: "fixed" }}>
                                    <td width="40px" style={{ textAlign: "center" }}>{index + 1}</td>
                                    <td>{power.deviceName}</td>
                                    {
                                        (power.loadStatus == "active") &&
                                        <td className="text-center device-status active" width="150px"><i className="fa-solid fa-circle"></i></td>
                                    }
                                    {
                                        (power.loadStatus == "error") &&
                                        <td className="text-center device-status offline" width="150px"><i className="fa-solid fa-circle"></i></td>
                                    }
                                    {
                                        (power.loadStatus == "warning") &&
                                        <td className="text-center device-status warning" width="150px"><i className="fa-solid fa-circle"></i></td>
                                    }
                                    <td width="150px" style={{ textAlign: "center" }}>ON</td>
                                    {
                                        power.layer ?
                                            <td width="150px">
                                                <Link to={`/load/${param.customerId}/${power.projectId}/systemMap/${power.systemMapId}?deviceId=${power.deviceId}`}>
                                                    Layer {power.layer} {`>`} {power.systemMapName}
                                                </Link>
                                            </td>
                                            : <td width="150px" style={{ textAlign: "center" }} >-</td>
                                    }
                                    <td width="70px" style={{ padding: "3px 15px" }} >
                                        <a className="button-icon text-left" data-toggle="modal" data-target={"#model-" + (index + 1)} onClick={() => getOperationInformation(power.deviceId)}>
                                            <img height="16px" src="/resources/image/icon-info.png" title="View Info" alt="view-info" />
                                        </a>
                                        {
                                            power.systemMapId > 0 &&
                                            <Link to={`/load/${param.customerId}/${power.projectId}/systemMap/${power.systemMapId}?deviceId=${power.deviceId}`} className="button-icon float-right" title="View Grid">
                                                <img height="16px" className="mt-0.5" src="/resources/image/icon-grid.png" alt="system-map-icon" />
                                            </Link>
                                        }
                                        <div className="modal fade" id={"model-" + (index + 1)} tabIndex="-1" role="dialog" aria-labelledby="exampleModalLabel"
                                            aria-hidden="true">
                                            <div className="modal-dialog modal-lg" role="document">
                                                <div className="modal-content">
                                                    <div className="modal-header" style={{ backgroundColor: "#052274", height: "44px" }}>
                                                        <h5 className="modal-title" id="myModalLabel" style={{ color: "white", fontSize: "15px", fontWeight: "bold" }}>{power.deviceName}</h5>
                                                    </div>
                                                    <div className="modal-body">
                                                        <table className="table text-center tbl-overview">
                                                            <thead>
                                                                <tr>
                                                                    <th style={{ backgroundColor: "#6a994e" }}>{instantOperationInfo?.sentDate != null ? moment(instantOperationInfo.sentDate).format(CONS.DATE_FORMAT_OPERATE) : "-"}</th>
                                                                    <th width="100px">Phase A</th>
                                                                    <th width="100px">Phase B</th>
                                                                    <th width="100px">Phase C</th>
                                                                    <th width="100px">Phase N</th>
                                                                    <th width="100px">Total</th>
                                                                </tr>
                                                            </thead>
                                                            <tbody>
                                                                <tr>
                                                                    <th scope="row">U<sub>LL</sub> [V]</th>
                                                                    <td>{instantOperationInfo?.uab != null ? instantOperationInfo.uab : "-"}</td>
                                                                    <td>{instantOperationInfo?.ubc != null ? instantOperationInfo.ubc : "-"}</td>
                                                                    <td>{instantOperationInfo?.uca != null ? instantOperationInfo.uca : "-"}</td>
                                                                    <td>-</td>
                                                                    <td>-</td>
                                                                </tr>
                                                                <tr>
                                                                    <th scope="row">U<sub>LN</sub> [V]</th>
                                                                    <td>{instantOperationInfo?.uan != null ? instantOperationInfo.uan : "-"}</td>
                                                                    <td>{instantOperationInfo?.ubn != null ? instantOperationInfo.ubn : "-"}</td>
                                                                    <td>{instantOperationInfo?.ucn != null ? instantOperationInfo.ucn : "-"}</td>
                                                                    <td>-</td>
                                                                    <td>-</td>
                                                                </tr>
                                                                <tr>
                                                                    <th scope="row">I [A]</th>
                                                                    <td>{instantOperationInfo?.ia != null ? instantOperationInfo.ia : "-"}</td>
                                                                    <td>{instantOperationInfo?.ib != null ? instantOperationInfo.ib : "-"}</td>
                                                                    <td>{instantOperationInfo?.ic != null ? instantOperationInfo.ic : "-"}</td>
                                                                    <td>{instantOperationInfo?.in != null ? instantOperationInfo.in : "-"}</td>
                                                                    <td>-</td>
                                                                </tr>
                                                                <tr>
                                                                    <th scope="row">P {Converter.convertLabelElectricPower(viewTypeModel, "W")}</th>
                                                                    <td>{instantOperationInfo?.pa != null ?  instantOperationInfo.pa : "-"}</td>
                                                                    <td>{instantOperationInfo?.pb != null ? instantOperationInfo.pb : "-"}</td>
                                                                    <td>{instantOperationInfo?.pc != null ? instantOperationInfo.pc : "-"}</td>
                                                                    <td>-</td>
                                                                    <td>{instantOperationInfo?.ptotal != null ?  instantOperationInfo.ptotal : "-"}</td>
                                                                </tr>
                                                                <tr>
                                                                    <th scope="row">Q {Converter.convertLabelElectricPower(viewTypeModel, "VAr")}</th>
                                                                    <td>{instantOperationInfo?.qa != null ?  instantOperationInfo.qa : "-"}</td>
                                                                    <td>{instantOperationInfo?.qb != null ?  instantOperationInfo.qb : "-"}</td>
                                                                    <td>{instantOperationInfo?.qc != null ?  instantOperationInfo.qc : "-"}</td>
                                                                    <td>-</td>
                                                                    <td>{instantOperationInfo?.qtotal != null ?  instantOperationInfo.qtotal : "-"}</td>
                                                                </tr>
                                                                <tr>
                                                                    <th scope="row">S {Converter.convertLabelElectricPower(viewTypeModel, "VA")}</th>
                                                                    <td>{instantOperationInfo?.sa != null ?  instantOperationInfo.sa : "-"}</td>
                                                                    <td>{instantOperationInfo?.sb != null ?  instantOperationInfo.sb : "-"}</td>
                                                                    <td>{instantOperationInfo?.sc != null ?  instantOperationInfo.sc : "-"}</td>
                                                                    <td>-</td>
                                                                    <td>{instantOperationInfo?.stotal != null ?  instantOperationInfo.stotal : "-"}</td>
                                                                </tr>
                                                                <tr>
                                                                    <th scope="row">PF</th>
                                                                    <td>{instantOperationInfo?.pfa != null ? instantOperationInfo.pfa : "-"}</td>
                                                                    <td>{instantOperationInfo?.pfb != null ? instantOperationInfo.pfb : "-"}</td>
                                                                    <td>{instantOperationInfo?.pfc != null ? instantOperationInfo.pfc : "-"}</td>
                                                                    <td>-</td>
                                                                    <td>-</td>
                                                                </tr>
                                                                <tr>
                                                                    <th scope="row">THD U [%]</th>
                                                                    <td>{instantOperationInfo?.thdVab != null ? instantOperationInfo.thdVab : "-"}</td>
                                                                    <td>{instantOperationInfo?.thdVbc != null ? instantOperationInfo.thdVbc : "-"}</td>
                                                                    <td>{instantOperationInfo?.thdVca != null ? instantOperationInfo.thdVca : "-"}</td>
                                                                    <td>{instantOperationInfo?.thdVan != null ? instantOperationInfo.thdVan : "-"}</td>
                                                                    <td>-</td>
                                                                </tr>
                                                                <tr>
                                                                    <th scope="row">THD I [%]</th>
                                                                    <td>{instantOperationInfo?.thdIa != null ? instantOperationInfo.thdIa : "-"}</td>
                                                                    <td>{instantOperationInfo?.thdIb != null ? instantOperationInfo.thdIb : "-"}</td>
                                                                    <td>{instantOperationInfo?.thdIc != null ? instantOperationInfo.thdIc : "-"}</td>
                                                                    <td>{instantOperationInfo?.thdIn != null ? instantOperationInfo.thdIn : "-"}</td>
                                                                    <td>-</td>
                                                                </tr>
                                                                <tr>
                                                                    <th scope="row">Temp [°C]</th>
                                                                    <td>{instantOperationInfo?.t1 != null ? instantOperationInfo.t1 : "-"}</td>
                                                                    <td>{instantOperationInfo?.t2 != null ? instantOperationInfo.t2 : "-"}</td>
                                                                    <td>{instantOperationInfo?.t3 != null ? instantOperationInfo.t3 : "-"}</td>
                                                                    <td>-</td>
                                                                    <td>-</td>
                                                                </tr>
                                                                <tr>
                                                                    <th scope="row">E<sub>P</sub> {Converter.convertLabelElectricPower(viewTypeModel, "Wh")}</th>
                                                                    <td colSpan={5}>{instantOperationInfo?.ep != null ?  instantOperationInfo.ep : "-"}</td>
                                                                </tr>
                                                                <tr>
                                                                    <th scope="row">E<sub>Q</sub> {Converter.convertLabelElectricPower(viewTypeModel, "VArh")}</th>
                                                                    <td colSpan={5}>{instantOperationInfo?.eq != null ?  instantOperationInfo.eq : "-"}</td>
                                                                </tr>
                                                                <tr>
                                                                    <th scope="row">Trạng Thái MCCB</th>
                                                                    <td colSpan={5}></td>
                                                                </tr>
                                                            </tbody>
                                                        </table>
                                                    </div>
                                                    <div className="modal-footer">
                                                        <button type="button" className="btn btn-outline-primary" onClick={() => $('#model-' + (index + 1)).hide()}>Đóng</button>
                                                        <button className="btn btn-primary" onClick={() => {
                                                            history.push("/load/" + param.customerId + "/" + param.projectId + "/device-information/" + power.deviceId)
                                                        }}>Chi tiết</button>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </td>
                                </tr>
                            )
                        }
                    </tbody>
                </table>
            </div>
        </>
    )
}

export default ControlLoad
