import { t } from "i18next"
import moment from "moment";
import React, { useEffect, useState } from "react";
import ManufactureService from "../../../../services/ManufactureService";
import { useParams } from "react-router-dom/cjs/react-router-dom.min";
import { NumberFormatBase } from "react-number-format";
import { NotficationError, NotficationSuscces } from "../notification/notification";
import { Calendar } from "primereact/calendar";
import { ToastContainer } from "react-toastify";
import { data, get } from "jquery";

const ManufactureTableData = (props) => {
    const [values, setValues] = useState([]);
    const [revenueValues, setRevenueValues] = useState([]);
    const [dates, setDates] = useState([]);
    const [dataEp, setDataEp] = useState([]);
    const [manufactureDetail, setManufactureDetail] = useState([]);
    const param = useParams();
    const [fromDate, setFromDate] = useState(new Date());
    const [toDate, setToDate] = useState(new Date());
    const [checkDate, setCheckDate] = useState();
    const [update, setUpdate] = useState(false);
    const [loadEp, setLoadEp] = useState(false);
    const [loadEp1, setLoadEp1] = useState(false);
    const [loading, setLoading] = useState(false);
    const [loading1, setLoading1] = useState(false);
    useEffect(() => {
        getListDate(fromDate, toDate);
    }, [props.manufature, update])

    const onChangeFromDate = (vlDate) => {
        // getListDate(vlDate, toDate);
        // getListManufactureDetail(param.customerId, props.manufature.id, moment(vlDate).format("YYYY-MM-DD"), moment(toDate).format("YYYY-MM-DD"));
    }

    const onChangeToDate = (vlDate) => {
        // getListDate(fromDate, vlDate);
        // getListManufactureDetail(param.customerId, props.manufature.id, moment(fromDate).format("YYYY-MM-DD"), moment(vlDate).format("YYYY-MM-DD"));
    }

    const convertShiftId = (arr) => {
        let strId = "";
        for (let i = 0; i < arr.length; i++) {
            if (i == 0) {
                strId += arr[i].id;
            } else {
                strId += "," + arr[i].id;
            }
        }
        return strId;
    }

    const funcCheck = (e) => {
        const date1 = new Date(moment(fromDate).format("YYYY-MM-DD"));
        const date2 = new Date(moment(e.target.value).format("YYYY-MM-DD"));
        var date = new Date(date2);
        date.setDate(date.getDate() - 31);
        setCheckDate(date);
        const diffTime = date2 - date1;
        let diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
        if (diffDays < 0) {
            setFromDate(e.value);
        } else if (diffDays > 31) {
            setFromDate(date);
        }

    };
    const fetchDataTab1 = async (startDate, endDate) => {
        await getListDate(startDate, endDate);
       
    }
    const search = async (startDate, endDate) => {
        setLoading1(true);
        fetchDataTab1(startDate, endDate).catch(console.error);
        
    }
    const getListDate = async (startDate, endDate) => {
        let result = [];
        let currentDate = new Date(startDate);
        while (currentDate <= endDate) {
            result.push(new Date(currentDate));
            currentDate.setDate(currentDate.getDate() + 1);
        }
        setDates(result);
        // if (props.manufature.devices != undefined) {
        //     result.map( date => {
        //         getEpByShiftAndViewTime(param.customerId, props.manufature.projectId, moment(date).format("YYYY-MM-DD"), convertIdDevices(props.manufature.devices), convertShiftId(props.settingShift), startDate, endDate);
        //     })
        // }
        if (props.manufature.devices != undefined) {
            await getEpByShiftAndViewTime(param.customerId, props.manufature.projectId, convertIdDevices(props.manufature.devices), convertShiftId(props.settingShift), moment(startDate).format("YYYY-MM-DD"), moment(endDate).format("YYYY-MM-DD"));
        }
        if (props.manufature.id != undefined) {
            await getListManufactureDetail(param.customerId, props.manufature.id, moment(fromDate).format("YYYY-MM-DD"), moment(toDate).format("YYYY-MM-DD"));
        }
setLoading1(false);
    }
    const convertIdDevices = (arr) => {
        let strId = "";
        for (let i = 0; i < arr.length; i++) {
            if (i == 0) {
                strId += arr[i].deviceId;
            } else {
                strId += "," + arr[i].deviceId;
            }
        }
        return strId;
    }
    const checkEpByViewTime = async (ep) => {
        let check = dataEp.filter((d) => d.shiftId == ep.shiftId && moment(d.viewTime).format("YYYY-MM-DD") == moment(ep.viewTime).format("YYYY-MM-DD"))[0];
        if (dataEp.length > 0) {
            if (check == undefined) {
                dataEp.push(ep);
            }
        } else {
            dataEp.push(ep);
        }
        setLoadEp(!loadEp)
    }

    const getListManufactureDetail = async (customer, manufacture, fromDate, toDate) => {
        let res = await ManufactureService.getDataManufactureDetailByManufactureAndViewTime(customer, manufacture, fromDate, toDate);
        if (res.status == 200) {
            setManufactureDetail(res.data);
        }
    }
    const getEpByShiftAndViewTime = async (customer, project, devices, shiftIds, startDate, endDate) => {
        let res = await ManufactureService.getDataEpByShiftAndViewTime(customer, project, devices, shiftIds, startDate, endDate);
        if (res.status == 200) {
            setDataEp(res.data)
        }
    }

    const changeInput = (id, manufactureId, shiftId, viewTime, productionNumber, epTotal, doanhThu) => {
        if (values.length <= 0) {
            setValues([{ id: id, manufactureId: manufactureId, shiftId: shiftId, viewTime: moment(viewTime).format("YYYY-MM-DD"), productionNumber: Number(productionNumber), epTotal: epTotal, totalRevenue: 0 }])
        } else {
            let data = values.filter((val) => val.viewTime == moment(viewTime).format("YYYY-MM-DD") && val.shiftId == shiftId && val.manufactureId == manufactureId)[0];
            if (data != undefined) {
                data.productionNumber = Number(productionNumber);
            } else {
                setValues([...values, { id: id, manufactureId: manufactureId, shiftId: shiftId, viewTime: moment(viewTime).format("YYYY-MM-DD"), productionNumber: Number(productionNumber), epTotal: epTotal, totalRevenue: 0 }])
            }
        }
    }
    const changeDoanhThu = (doanhThu, viewTime, manufactureId) => {
        if (revenueValues <= 0) {
            setRevenueValues([{ id: 0, manufactureId: manufactureId, shiftId: undefined, viewTime: moment(viewTime).format("YYYY-MM-DD"), productionNumber: undefined, epTotal: undefined, totalRevenue: doanhThu != undefined ? Number(doanhThu) : 0 }])
        } else {
            let data = revenueValues.filter((val) => val.viewTime == moment(viewTime).format("YYYY-MM-DD") && val.manufactureId == manufactureId)[0];
            if (data != undefined) {
                data.totalRevenue = Number(doanhThu);
            } else {
                setRevenueValues([...revenueValues, { id: 0, manufactureId: manufactureId, shiftId: undefined, viewTime: moment(viewTime).format("YYYY-MM-DD"), productionNumber: undefined, epTotal: undefined, totalRevenue: doanhThu != undefined ? Number(doanhThu) : 0 }])
            }
        }
    }

    const onSaveManufactureDetail = async () => {
        setLoading(true);
        let responseStatus;
        if (values.length > 0) {
            let res = await ManufactureService.addOrUpdateManufactureDetail(param.customerId, values);
            if (res.status == 200) {
                setValues([]);
                responseStatus = res.status;
            }
        }
        // if (revenueValues.length > 0) {
        //     let resp = await ManufactureService.updateOrUpdateManufactureDetailRevenue(param.customerId, revenueValues);
        //     if (resp.status == 200) {
        //         setRevenueValues([]);
        //         responseStatus = resp.status;
        //     }
        // }

        if (responseStatus == 200) {
            resetInput();
            NotficationSuscces("Lưu thành công!")
            setUpdate(!update)
        }
        setLoading(false);
    }

    const onDowloadData = async () => {
        setLoading(true);
        // prefixs =  customerId@projectId@ProductionId@ProductionStepId@ManufactureId@DeviceId
        // time =  fromDate@toDate
        // production =  productionName@productionStepName@unit
        let prefix = param.customerId + "@" + props.manufature.projectId + "@" + props.manufature.productionId + "@"
            + props.manufature.productionStepId + "@" + props.manufature.id + "@" + convertIdDevices(props.manufature.devices);
        let time = moment(fromDate).format("YYYY-MM-DD") + "@" + moment(toDate).format("YYYY-MM-DD")
        let production = props.manufature.productionName + "@" + props.manufature.productionStepName + "@" + props.manufature.unit
        let res = await ManufactureService.dowloadMaufactureDetail(prefix, time, production);
        setLoading(false);

    }

    const resetInput = () => {
        let a = document.getElementsByName("inputValue");
        a.forEach(val => {
            val.value = ""
        })
        let b = document.getElementsByName("ravenue");
        b.forEach(val => {
            val.value = ""
        })

    }

    const getManufactureDetail = (manus, viewTime, shiftId) => {
        let a = manus.filter((manu) => manu.shiftId == shiftId && moment(manu.viewTime).format("DD-MM-YYYY") == moment(viewTime).format("DD-MM-YYYY"))[0];
        return a;
    }
    const getDataEp = (dataEps, date, shiftId) => {
        if (dataEps.length > 0) {
            let a = dataEps.filter((data) => data.shiftId == shiftId && moment(data.viewTime).format("DD-MM-YYYY") == moment(date).format("DD-MM-YYYY"))[0];
            return a;
        }
    }

    const getTotalEp = (viewTime) => {
        let total = 0;
        let a = dataEp.filter((dep) => moment(viewTime).format("DD-MM-YYYY") == moment(dep.viewTime).format("DD-MM-YYYY"));
        if (a.length > 0) {
            a.map((d) => {
                total = total + d.epTotal;
            })
        }
        return total;
    }
    const getTotalCost = (viewTime) => {
        let total = 0;
        let a = dataEp.filter(dep => moment(viewTime).format("DD-MM-YYYY") == moment(dep.viewTime).format("DD-MM-YYYY"));
        if (a.length > 0) {
            a.map((d) => {
                total = total + (d.lowCost + d.normalCost + d.highCost);
            })
        }
        return total;
    }
    return <>
        <div style={{ position: 'relative' }}>
            {loading == true ?
                <div style={{ position: 'fixed', zIndex: '1', left: '50%', top: '200px' }}>
                    <div className="loading">
                        <img height="60px" src="/resources/image/loading2.gif" alt="loading" />
                    </div>
                </div> : null
            }
            <div className="mt-3 mb-1 ">
                <div className="d-flex justify-content-start mt-1 ">
                    <div className="fixed d-flex justify-content-start">
                        <div style={{ height: '35px', width: '35px', backgroundColor: '#1B3281', paddingTop: '3.6%', paddingLeft: '4%', borderTopLeftRadius: '5px', borderBottomLeftRadius: '5px' }}><i style={{ color: 'white', fontSize: '18px' }} className="fa-regular fa-calendar"></i></div>
                        <Calendar locale="vn"
                            style={{ width: '100%', height: '35px', border: 'solid 2px #1B3281', fontWeight: 'bold', borderTopRightRadius: '5px', borderEndEndRadius: '5px' }}
                            value={fromDate}
                            onChange={(e) => {
                                setFromDate(e.value)
                                onChangeFromDate(e.value);
                            }}
                            view="date"
                            dateFormat="yy-mm-dd"
                            maxDate={toDate}
                        // minDate={checkDate}
                        />
                    </div>
                    <span style={{ color: '#1B3281', fontSize: '16px', marginTop: '0.8%' }}>&#10148;</span>
                    <div className="d-flex justify-content-start">
                        <div style={{ height: '35px', width: '35px', backgroundColor: '#1B3281', paddingTop: '3.6%', paddingLeft: '4%', borderTopLeftRadius: '5px', borderBottomLeftRadius: '5px' }}><i style={{ color: 'white', fontSize: '18px' }} className="fa-regular fa-calendar"></i></div>
                        <Calendar locale="vn"
                            style={{ width: '100%', height: '35px', border: 'solid 2px #1B3281', borderTopRightRadius: '5px', borderEndEndRadius: '5px' }}
                            value={toDate}
                            onChange={(e) => {
                                setToDate(e.value);
                                funcCheck(e)
                                onChangeToDate(e.value);
                            }
                            }
                            view="date"
                            dateFormat="yy-mm-dd"
                            maxDate={new Date}
                            minDate={fromDate}
                        />
                    </div>
                    <button className="manufacture-main-btn-find" onClick={() => search(fromDate, toDate)}><i style={{ color: 'white', fontSize: '18px' }} className="fa-solid fa-magnifying-glass"></i></button>
                    <button className="manufacture-main-btn-find" onClick={() => onSaveManufactureDetail()}><i style={{ color: 'white', fontSize: '18px' }} className="fa-solid fa-floppy-disk"></i></button>
                    <button className="manufacture-main-btn-find" onClick={() => onDowloadData()}><i style={{ color: 'white', fontSize: '18px' }} className="fa-solid fa-download"></i></button>
                </div>
            </div>
            {loading1 == true ?
                    <div style={{ position: 'fixed', zIndex: '1', left: '50%', top: '200px' }}>
                        <div className="loading">
                            <img height="60px" src="/resources/image/loading2.gif" alt="loading" />
                        </div>
                    </div> :
            <table className="hihi" width={"98%"} >
                <thead >
                    <tr height="60px">
                        <th colSpan={3 + props.settingShift.length} style={{ backgroundColor: "var(--ses-blue-100-color)", fontSize: "20px" }}>
                            {t('content.table_manufacture')}</th>
                    </tr>
                    <tr height="50px" style={{ backgroundColor: "red" }}>
                        <th width="200px">{t('content.home_page.manufacture.time')} </th>
                        {props.settingShift.map((shift, index) => {
                            return (
                                <th width="200px" key={index + "ket"}>
                                    {shift.shiftName}
                                    <br />
                                    {moment(shift.startTime, "HH:mm:ss").format("HH:mm") + "-" + moment(shift.endTime, "HH:mm:ss").format("HH:mm")}
                                    <br />
                                    {(shift.status == 0 ? "(Đã khóa)" : '')}
                                </th>
                            )
                        })}
                        {/* <th width="200px">
                            {t('content.home_page.manufacture.total_number_of_units')}
                        </th> */}
                        <th width="200px" className="text-uppercase">
                            {t('content.total')}
                        </th >
                        <th width="200px">
                            {t('content.home_page.manufacture.update_date')}
                        </th>
                    </tr>
                </thead>
                    <tbody className="hihitbody">
                    {
                        dates.map((date, index) => {
                            let totalNumber = 0;
                            return <React.Fragment key={"manu" + index}>
                                <tr height="40px">
                                    <td rowSpan="3" style={{ width: '200px !important' }}><p style={{ width: 'fit-content', fontWeight: 'bold', fontSize: '16px', margin: "0 auto" }}>{moment(date).format("DD-MM-YYYY")}</p></td>
                                    {props.settingShift.map((shift, idx) => {
                                        let o = getManufactureDetail(manufactureDetail, date, shift.id);
                                        totalNumber += (o != undefined ? Number(o.productionNumber) : 0);
                                        return (
                                            <td key={idx + 's'} >
                                                <p style={{ fontWeight: 'bold', fontFamily: 'sans-serif', fontSize: '15px', margin: 0 }} >{o != undefined ? Number(o.productionNumber) + " " : "0 "}{props.manufature.unit != undefined ? props.manufature.unit : ""}</p>
                                                <input
                                                    key={idx + 'i'}
                                                    className="mt-1"
                                                    type="number"
                                                    name="inputValue"
                                                    min={0} step="1" lang="nb"

                                                    style={shift.status == 0 ? { width: "150px", margin: '0 auto', fontWeight: 'bold', color: 'black', backgroundColor: '#afafaf', border: '#c35b1e solid 2px' } : { width: "150px", margin: '0 auto', fontWeight: 'bold', color: 'black' }}
                                                    onChange={(e) => {
                                                        changeInput(
                                                            o != undefined ? o.id : 0,
                                                            props.manufature.id,
                                                            shift.id,
                                                            date,
                                                            e.target.value,
                                                            getDataEp(dataEp, date, shift.id) != undefined ? getDataEp(dataEp, date, shift.id).epTotal : 0
                                                        )
                                                    }
                                                    }
                                                    readOnly={shift.status == 0}
                                                />
                                            </td>)

                                    })}
                                    <td width={'200px'} >
                                        <p style={{ fontWeight: 'bold', fontFamily: 'sans-serif', fontSize: '15px', margin: 0 }} >{
                                            totalNumber + " " + (props.manufature.unit != undefined ? props.manufature.unit : "")
                                        }</p>
                                        {/* <span> VNĐ</span> */}
                                    </td>

                                    <td rowSpan="3" width={'200px'}>
                                        <p style={{ width: 'fit-content', fontWeight: 'bold', fontSize: '16px', margin: "0 auto" }}>
                                            {manufactureDetail.filter((d) => moment(d.viewTime).format("DD-MM-YYYY") == moment(date).format("DD-MM-YYYY"))[0] != undefined ? moment(manufactureDetail.filter((d) => moment(d.viewTime).format("DD-MM-YYYY") == moment(date).format("DD-MM-YYYY"))[0].updateDate).format("DD-MM-YYYY") : moment(new Date).format("DD-MM-YYYY")}
                                        </p>

                                    </td>
                                </tr>
                                <tr height="50px" style={{ fontWeight: 'bold' }}>
                                    {props.settingShift.map((shift, index) => {
                                        return (
                                            <td key={index + 'e'}>
                                                {getDataEp(dataEp, date, shift.id) != undefined ? getDataEp(dataEp, date, shift.id)?.epTotal : 0} kWh
                                            </td>
                                        )
                                    })}
                                    <td >{getTotalEp(date) + " kWh"} </td>
                                </tr>
                                <tr height="50px" style={{ fontWeight: 'bold' }}>
                                    {props.settingShift.map((shift, index) => {
                                        return (
                                            <td key={index + 'e'}>
                                                {getDataEp(dataEp, date, shift.id) != undefined ? (getDataEp(dataEp, date, shift.id).lowCost + getDataEp(dataEp, date, shift.id).normalCost + getDataEp(dataEp, date, shift.id).highCost) : 0} VND
                                            </td>
                                        )
                                    })}
                                    <td >{getTotalCost(date) + " VND"} </td>
                                </tr>
                            </React.Fragment>
                        })
                }
                    </tbody>
                {/* } */}
            </table>}
        </div>

    </>
}
export default ManufactureTableData;