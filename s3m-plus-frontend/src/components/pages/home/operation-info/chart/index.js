import React, { useEffect, useState } from "react";
import { Link, Route, Switch, useHistory, useParams } from "react-router-dom";
import EffectivePower from "./effective-power";
import PowerCircuit from "./power-circuit";
import Voltage from "./voltage";
import Temperature from "./temperature";
import ElectricalPower from "./electrical-power";
import Harmonic from "./harmonic";
import OperationInformationService from "../../../../../services/OperationInformationService";
import Pickadate from 'pickadate/builds/react-dom';
import $ from "jquery";

const ChartOperation = () => {

    const param = useParams();

    const history = useHistory();
    const [template, setTemplate] = useState(1);

    const getChart = () => {

        let fromDateValue = document.getElementById("from-date").value;
        let toDateValue = document.getElementById("to-date").value;
        let chartTypeValue = document.getElementById("cbo-data-type").value;

        console.log(chartTypeValue);

        if (parseInt(chartTypeValue) === 6) {
            setTemplate(1);
            $('.data-type-electrical').hide();
            $('.to-date').show();
            $('.from-date').show();
            $('.search').show();
            history.push(`/operation/${param.projectId}/${param.deviceId}/chart-operation/harmonic/${fromDateValue}/${toDateValue}/${chartTypeValue}`);
        } else if (parseInt(chartTypeValue) === 5) {
            setTemplate(1);
            $('.data-type-electrical').hide();
            $('.to-date').show();
            $('.from-date').show();
            $('.search').show();
            history.push(`/operation/${param.projectId}/${param.deviceId}/chart-operation/temperature/${fromDateValue}/${toDateValue}/${chartTypeValue}`)
        } else if (parseInt(chartTypeValue) === 4) {
            let pqsViewType = document.getElementById("cbo-data-type-electrical").value;
            if (parseInt(pqsViewType) === 3) {
                setTemplate(3);
            } else if (parseInt(pqsViewType) === 2) {
                setTemplate(2);
            } else {
                setTemplate(1);
            }
            $('.data-type-electrical').show();
            $('.to-date').hide();
            $('.from-date').show();
            $('.search').show();
            history.push(`/operation/${param.projectId}/${param.deviceId}/chart-operation/electrical-power/${fromDateValue}/${pqsViewType}/${chartTypeValue}`);
        } else if (parseInt(chartTypeValue) === 3) {
            setTemplate(1);
            $('.data-type-electrical').hide();
            $('.to-date').show();
            $('.from-date').show();
            $('.search').show();
            history.push(`/operation/${param.projectId}/${param.deviceId}/chart-operation/effective-power/${fromDateValue}/${toDateValue}/${chartTypeValue}`);
        } else if (parseInt(chartTypeValue) === 2) {
            setTemplate(1);
            $('.data-type-electrical').hide();
            $('.to-date').show();
            $('.from-date').show();
            $('.search').show();
            history.push(`/operation/${param.projectId}/${param.deviceId}/chart-operation/voltage/${fromDateValue}/${toDateValue}/${chartTypeValue}`);
        } else {
            setTemplate(1);
            $('.data-type-electrical').hide();
            $('.to-date').show();
            $('.from-date').show();
            $('.search').show();
            history.push(`/operation/${param.projectId}/${param.deviceId}/chart-operation/${fromDateValue}/${toDateValue}/${chartTypeValue}`);
        }
    }

    const downloadDataChart = async () => {

        let fromDateValue = document.getElementById("from-date").value;
        let toDateValue = document.getElementById("to-date").value;
        let chartTypeValue = document.getElementById("cbo-data-type").value;

        if (parseInt(chartTypeValue) === 5) {

            let res = await OperationInformationService.getDataChart(param.deviceId, fromDateValue, toDateValue, 0, chartTypeValue);
        
            if (res.status === 200 && parseInt(res.data.length) > 0) {
                await OperationInformationService.downloadDataChart(param.deviceId, fromDateValue, toDateValue, 0, chartTypeValue);
            } else {
                history.push(`/operation/${param.projectId}/${param.deviceId}/chart-operation/temperature/${fromDateValue}/${toDateValue}/${chartTypeValue}`);
            }

        } else if (parseInt(chartTypeValue) === 4) {

            let pqsViewType = document.getElementById("cbo-data-type-electrical").value;

            let res = await OperationInformationService.getDataChart(param.deviceId, fromDateValue, null, pqsViewType, chartTypeValue);

            if (res.status === 200 && parseInt(res.data.length) > 0) {
                await OperationInformationService.downloadDataChart(param.deviceId, fromDateValue, toDateValue, pqsViewType, chartTypeValue);
            } else {
                history.push(`/operation/${param.projectId}/${param.deviceId}/chart-operation/electrical-power/${fromDateValue}/${pqsViewType}/${chartTypeValue}`);
            }

        } else if (parseInt(chartTypeValue) === 3) {

            let res = await OperationInformationService.getDataChart(param.deviceId, fromDateValue, toDateValue, 0, chartTypeValue);

            if (res.status === 200 && parseInt(res.data.length) > 0) {
                await OperationInformationService.downloadDataChart(param.deviceId, fromDateValue, toDateValue, 0, chartTypeValue);
            } else {
                history.push(`/operation/${param.projectId}/${param.deviceId}/chart-operation/effective-power/${fromDateValue}/${toDateValue}/${chartTypeValue}`);
            }

        } else if (parseInt(chartTypeValue) === 2) {

            let res = await OperationInformationService.getDataChart(param.deviceId, fromDateValue, toDateValue, 0, chartTypeValue);

            if (res.status === 200 && parseInt(res.data.length) > 0) {
                await OperationInformationService.downloadDataChart(param.deviceId, fromDateValue, toDateValue, 0, chartTypeValue);
            } else {
                history.push(`/operation/${param.projectId}/${param.deviceId}/chart-operation/voltage/${fromDateValue}/${toDateValue}/${chartTypeValue}`)
            }

        } else {

            let res = await OperationInformationService.getDataChart(param.deviceId, fromDateValue, toDateValue, 0, chartTypeValue);
            
            if (res.status === 200 && parseInt(res.data.length) > 0) {
                await OperationInformationService.downloadDataChart(param.deviceId, fromDateValue, toDateValue, 0, chartTypeValue);
            } else {
                history.push(`/operation/${param.projectId}/${param.deviceId}/chart-operation/${fromDateValue}/${toDateValue}/${chartTypeValue}`);
            }
        }
    }

    document.title = "Thông tin thiết bị - Biểu đồ";
    
    useEffect(() => {
        let chartTypeValue = document.getElementById("cbo-data-type").value;
        console.log(chartTypeValue);
        if(parseInt(chartTypeValue) !== 4) {
            $('.data-type-electrical').hide();
        }
    }, [template])
    return (
        <>
            <div id="main-search" className="ml-1 mb-3" style={{ height: '32px' }}>
                <div className="form-group mt-2 mb-0 ml-2">
                    <div className="input-group float-left mr-1" style={{ width: '250px' }}>
                        <div className="input-group-prepend">
                            <span className="input-group-text pickericon">
                                <span className="fas fa-circle-info" />
                            </span>
                        </div>
                        <select id="cbo-data-type" name="dataType" defaultValue={1} className="custom-select block custom-select-sm" onChange={() => getChart()}>
                            <option value={1}>Dòng điện</option>
                            <option value={2}>Điện áp</option>
                            <option value={5}>Nhiệt độ</option>
                            <option value={3}>Công suất tác dụng</option>
                            <option value={4}>Điện năng</option>
                            <option value={6}>Harmonic</option>
                        </select>
                    </div>
                    <div className="input-group float-left mr-1 data-type-electrical" style={{ width: '250px' }}>
                        <div className="input-group-prepend">
                            <span className="input-group-text pickericon">
                                <span className="fas fa-calendar" />
                            </span>
                        </div>
                        <select id="cbo-data-type-electrical" name="dataTypeElectrical" defaultValue={0} className="custom-select block custom-select-sm" onChange={() => getChart()}>
                            <option value={1}>Ngày</option>
                            <option value={2}>Tháng</option>
                            <option value={3}>Năm</option>
                        </select>
                    </div>
                    {parseInt(template) === 3 ? <div className="input-group float-left mr-1 from-date" style={{ width: '250px' }}>
                        <div className="input-group-prepend">
                            <span className="input-group-text pickericon">
                                <span className="far fa-calendar" />
                            </span>
                        </div>
                        <Pickadate.InputPicker id="from-date" value={new Date()} className="form-control pickadate form-control-sm" style={{ fontSize: "13px" }} readOnly={true} aria-haspopup="true"
                            aria-readonly="false" aria-owns="from-date_root" autoComplete="off" placeholder="Từ ngày"
                            initialState={{
                                selected: new Date(),
                                template: "YYYY"
                            }}
                        />
                    </div> : ""}
                    {parseInt(template) === 2 ? <div className="input-group float-left mr-1 from-date" style={{ width: '250px' }}>
                        <div className="input-group-prepend">
                            <span className="input-group-text pickericon">
                                <span className="far fa-calendar" />
                            </span>
                        </div>
                        <Pickadate.InputPicker id="from-date" value={new Date()} className="form-control pickadate form-control-sm" style={{ fontSize: "13px" }} readOnly={true} aria-haspopup="true"
                            aria-readonly="false" aria-owns="from-date_root" autoComplete="off" placeholder="Từ ngày"
                            initialState={{
                                selected: new Date(),
                                template: "YYYY-MM"
                            }}
                        />
                    </div> : ""}
                    {parseInt(template) === 1 ? <div className="input-group float-left mr-1 from-date" style={{ width: '250px' }}>
                        <div className="input-group-prepend">
                            <span className="input-group-text pickericon">
                                <span className="far fa-calendar" />
                            </span>
                        </div>
                        <Pickadate.InputPicker id="from-date" value={new Date()} className="form-control pickadate form-control-sm" style={{ fontSize: "13px" }} readOnly={true} aria-haspopup="true"
                            aria-readonly="false" aria-owns="from-date_root" autoComplete="off" placeholder="Từ ngày"
                            initialState={{
                                selected: new Date(),
                                template: "YYYY-MM-DD"
                            }}
                        />
                    </div> : ""}
                    <div className="input-group float-left mr-1 to-date" style={{ width: '250px' }}>
                        <div className="input-group-prepend">
                            <span className="input-group-text pickericon">
                                <span className="far fa-calendar" />
                            </span>
                        </div>
                        <Pickadate.InputPicker id="to-date" value={new Date()} className="form-control pickadate form-control-sm" style={{ fontSize: "13px" }} readOnly={true} aria-haspopup="true"
                            aria-readonly="false" aria-owns="to-date_root" autoComplete="off" placeholder="Từ ngày"
                            initialState={{
                                selected: new Date(),
                                template: "YYYY-MM-DD"
                            }}
                        />
                    </div>
                </div>
                <div className="search-buttons float-left search">
                    <button type="button" className="btn btn-outline-secondary btn-sm mr-1" onClick={() => getChart()}>
                        <i className="fa-solid fa-search" />
                    </button>
                    <button type="button" className="btn btn-outline-secondary btn-sm" onClick={() => downloadDataChart()}>
                        <i className="fa-solid fa-download" />
                    </button>
                </div>
            </div>
            <div id="main-content">
                {
                    <Switch>
                        <Route path={"/operation/:projectId/:deviceId/chart-operation/harmonic/:fromDate/:toDate/:chartType"}><Harmonic></Harmonic></Route>
                        <Route path={"/operation/:projectId/:deviceId/chart-operation/electrical-power/:fromDate/:pqsViewType/:chartType"}><ElectricalPower></ElectricalPower></Route>
                        <Route path={"/operation/:projectId/:deviceId/chart-operation/effective-power/:fromDate/:toDate/:chartType"}><EffectivePower></EffectivePower></Route>
                        <Route path={"/operation/:projectId/:deviceId/chart-operation/temperature/:fromDate/:toDate/:chartType"}><Temperature></Temperature></Route>
                        <Route path={"/operation/:projectId/:deviceId/chart-operation/voltage/:fromDate/:toDate/:chartType"}><Voltage></Voltage></Route>
                        <Route path={"/operation/:projectId/:deviceId/chart-operation/:fromDate/:toDate/:chartType"}><PowerCircuit></PowerCircuit></Route>
                    </Switch>
                }
            </div>
        </>
    )
}

export default ChartOperation;