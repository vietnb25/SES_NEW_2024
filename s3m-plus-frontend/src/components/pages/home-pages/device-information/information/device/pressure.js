import React, { useState } from "react";
import { useEffect } from "react";
import { useRef } from "react";
import { Link, Route, Switch, useHistory, useLocation, useParams } from "react-router-dom/cjs/react-router-dom.min";
import DeviceService from "../../../../../../services/DeviceService";
import moment from "moment";
import CONS from "../../../../../../constants/constant";
import WarningService from "../../../../../../services/WarningService";
import "./../../index.css"
import * as am5 from "@amcharts/amcharts5";
import * as am5xy from "@amcharts/amcharts5/xy";
import am5themes_Animated from "@amcharts/amcharts5/themes/Animated";
import { useDownloadExcel } from "react-export-table-to-excel";


const Pressure = () => {

    const [accessDenied, setAccessDenied] = useState(false)
    const param = useParams();
    const [iDevice, setIDevice] = useState({
        deviceId: "",
        deviceName: "",
        createDate: "",
        systemTypeId: "",
        deviceTypeId: "",
        description: "",
        projectName: "",
    });


    const [device, setDevice] = useState({
        systemTypeId: "",
        deviceType: "",
        operatingStatus: "",
        uab: "",
        pfa: "",
        ubc: "",
        pfb: "",
        uca: "",
        pfc: "",
        uan: "",
        sa: "",
        ubn: "",
        sb: "",
        ucn: "",
        sc: "",
        ia: "",
        ep: "",
        ib: "",
        ic: "",
        f: "",
        pa: "",
        pb: "",
        pc: "",
        t: "",
        ptotal: "",
        qa: "",
        qb: "",
        qc: "",
        qtotal: "",
        vdcCombiner: "",
        idcCombiner: "",
        pdcCombiner: "",
        h: "",
        vdcStr: "",
        idcStr: "",
        pdcStr: "",
        sentDate: "",
    })

    useEffect(() => {
        document.title = "Thông tin chi tiết thiết bị";
        getInfoDevice(param.customerId, param.deviceId);
    }, [param.deviceId])


    const getInfoDevice = async (customerId, deviceId) => {
        let res = await DeviceService.getInforDeviceByDeviceId(customerId, deviceId)
        if (res.status === 200) {
            if (res.data.length > 0) {
                setAccessDenied(false)
                let info = res.data[1]
                setIDevice(info)
                await getDataInstance(customerId, res.data[1].systemTypeId, res.data[1].deviceType, deviceId)
            } else {
                setAccessDenied(true)
            }
        }
    }

    const getDataInstance = async (customerId, systemTypeId, deviceType, deviceId) => {
        let res = await DeviceService.getDataInstance(customerId, systemTypeId, deviceType, deviceId)
        if (res.status === 200) {
            setDevice(res.data)
        }
    }

    return (
        <>
            <div className="content-1">
                <div className="content-1">
                    <div className="box">

                        <table className="table-param" style={{ border: "2px solid", width: "100%", margin: "auto", height: "470px" }}>
                            <tbody style={{ lineHeight: 2 }}>
                                <tr>
                                    <th style={{ width: "50%" }}>THÔNG SỐ</th>
                                    <th style={{ width: "50%" }}>GIÁ TRỊ</th>
                                </tr>
                                <tr>
                                    <td className="text-param">Áp suất</td>
                                    <td className="text-param">{device.p != null ? device.p : "-"} bar</td>
                                </tr>
                            </tbody>
                        </table>


                    </div>

                </div>
            </div>
        </>
    )

}

export default Pressure;