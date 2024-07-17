import moment from "moment";
import React, { useEffect } from "react";
import { useState } from "react";
import { useParams } from 'react-router-dom';
import converter from "../../../../../../common/converter";
import CONS from "../../../../../../constants/constant";
import OperationInformationService from "../../../../../../services/OperationInformationService";

const OperationSetting = () => {
    const param = useParams();
    const [viewTypeTable, setViewTypeTable] = useState(null);
    const [state, setState] = useState(0);

    const [setting, setSetting] = useState({
        id: "",
        deviceId: "",
        wmax: "",
        vref: "",
        vamax: "",
        vamaxQ1: "",
        vamaxQ2: "",
        vamaxQ3: "",
        vamaxQ4: "",
        fnormal: "",
        outPFSet: "",
        sentDate: "",
        transactionDate: ""
    });


    const getOperationSettingInverter = async () => {
        let res = await OperationInformationService.getOperationSettingInverter(param.customerId, param.deviceId);
        if (res.status === 200) {
            setSetting(res.data);
        } else {
            setSetting([]);
        }
    }

    function Interval() {
        setTimeout(() => {
            setState(state + 1);
        }, 15000);
    }

    useEffect(() => {
        document.title = "Thông tin thiết bị - Thông số cài đặt";
        getOperationSettingInverter();
        Interval();
    }, [param.deviceId, param.deviceType, param.customerId])

    return (
        <>
            <div className="tab-content">
                <div className="tab-title">
                    <div className="latest-time mt-2"><i className="fa-regular fa-clock"></i>&nbsp;{setting?.sentDate != null ? moment(setting?.sentDate).format(CONS.DATE_FORMAT_OPERATE) : ""}</div>
                    <div className="latest-warning">
                    <button type="button" className="btn btn-outline-info" style={{ width: "34.5px", height: "29px", paddingLeft: "10px", fontSize: "13px" }} onClick={() => Interval()}>
                        <i className="fa-solid fa-rotate-right"></i>
                    </button>
                </div>
                </div>
                <table className="table tbl-overview tbl-tsnd mt-3">
                    <thead>
                        <tr>
                            <th colSpan={9} className="tbl-title">Thông số cài đặt thiết bị</th>
                        </tr>
                    </thead>
                </table>
                <table className="table tbl-overview table-bordered text-center">
                    <tbody>
                        <tr>
                            <th>WMax [W]</th>
                            <th>Vref [V]</th>
                            <th>VAMax [VA]</th>
                            <th>VAMaxQ1 [VAr]</th>
                            <th>VAMaxQ2 [VAr]</th>
                            <th>VAMaxQ3 [VAr]</th>
                            <th>VAMaxQ4 [VAr]</th>
                            <th>F_normal [Hz]</th>
                            <th>OutPFSet</th>
                        </tr>
                        <tr>
                            <td>{setting?.wmax != null ? setting?.wmax : "-"}</td>
                            <td>{setting?.vref != null ? setting?.vref : "-"}</td>
                            <td>{setting?.vamax != null ? setting?.vamax : "-"}</td>
                            <td>{setting?.vamaxQ1 != null ? setting?.vamaxQ1 : "-"}</td>
                            <td>{setting?.vamaxQ2 != null ? setting?.vamaxQ2 : "-"}</td>
                            <td>{setting?.vamaxQ3 != null ? setting?.vamaxQ3 : "-"}</td>
                            <td>{setting?.vamaxQ4 != null ? setting?.vamaxQ4 : "-"}</td>
                            <td>{setting?.fnormal != null ? setting?.fnormal : "-"}</td>
                            <td>{setting?.outPFSet != null ? setting?.outPFSet : "-"}</td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </>
    )
}

export default OperationSetting;