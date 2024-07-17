import { useEffect, useState } from 'react';
import * as d3 from "d3";
import { DATA } from "../../../category/customer-tool/tool-page/data";
import { Link, useHistory, useLocation, useParams } from 'react-router-dom';
import SystemMapService from '../../../../../services/SystemMapService';
import ARROW from '../../../category/customer-tool/tool-page/arrow';
import MBA from '../../../category/customer-tool/tool-page/mba';
import RMU from '../../../category/customer-tool/tool-page/rmu';
import MC from '../../../category/customer-tool/tool-page/mc';
import MCHOR from '../../../category/customer-tool/tool-page/mchor';
import LABEL from '../../../category/customer-tool/tool-page/label';
import ULTILITY from '../../../category/customer-tool/tool-page/ultility';
import METER_LOAD from './meter';
import FEEDER from '../../../category/customer-tool/tool-page/feeder';
import BUSBAR from '../../../category/customer-tool/tool-page/busbar';
import BUSBARVER from '../../../category/customer-tool/tool-page/busbarver';
import STMV from '../../../category/customer-tool/tool-page/stmv';
import SGMV from '../../../category/customer-tool/tool-page/sgmv';
import { createInstance } from '../../../category/customer-tool/tool-page/data';
import CONSTANTS from '../../../category/customer-tool/tool-page/constant';
import moment from 'moment';
import CONS from '../../../../../constants/constant';
import OperationInformationService from '../../../../../services/OperationInformationService';
import converter from '../../../../../common/converter';
import INVERTER from '../../../category/customer-tool/tool-page/inverter';
import COMBINER from '../../../category/customer-tool/tool-page/combiner';
import PANEL from '../../../category/customer-tool/tool-page/panel';
import STRING from '../../../category/customer-tool/tool-page/string';
import WEATHER from '../../../category/customer-tool/tool-page/weather';
import INVERTERSYMBOL from '../../../category/customer-tool/tool-page/invertersymbol';
import COMBINERSYMBOL from '../../../category/customer-tool/tool-page/combinersymbol';
import PANELSYMBOL from '../../../category/customer-tool/tool-page/panelsymbol';
import STRINGSYMBOL from '../../../category/customer-tool/tool-page/stringsymbol';
import WEATHERSYMBOL from '../../../category/customer-tool/tool-page/weathersymbol';
import UPS from '../../../category/customer-tool/tool-page/ups';
import DeviceService from '../../../../../services/DeviceService';
import TEXT from '../../../category/customer-tool/tool-page/text';

const $ = window.$;

var deviceIdCurrent = 0;
export const getOperationInformation = async (customerId, deviceId) => {
    let deviceRes = await DeviceService.detailsDevice(deviceId);
    let device = deviceRes.data;

    if (device.deviceType === 1) {
        let res = await OperationInformationService.getInstantOperationInverterPV(customerId, deviceId);
        if (res.status === 200) {
            let inverterInstance = res.data;
            $('#inverter-name').text(inverterInstance.deviceName);
            $('#inverter-time').html(`<i class="fa-regular fa-clock mr-1" style= "color: revert"></i>${inverterInstance.sentDate === null || inverterInstance.sentDate === undefined ? "" : moment(inverterInstance.sentDate).format(CONS.DATE_FORMAT_OPERATE)}`);
            $('#value-td-ppvphA').text(inverterInstance.va === null || inverterInstance.va === undefined ? "-" : inverterInstance.va);
            $('#value-td-ppvphB').text(inverterInstance.vb === null || inverterInstance.vb === undefined ? "-" : inverterInstance.vb);
            $('#value-td-ppvphC').text(inverterInstance.vc === null || inverterInstance.vc === undefined ? "-" : inverterInstance.vc);
            $('#value-td-aphaA').text(inverterInstance.ia === null || inverterInstance.ia === undefined ? "-" : inverterInstance.ia);
            $('#value-td-aphaB').text(inverterInstance.ib === null || inverterInstance.ib === undefined ? "-" : inverterInstance.ib);
            $('#value-td-aphaC').text(inverterInstance.ic === null || inverterInstance.ic === undefined ? "-" : inverterInstance.ic);

            $('.value-td-pa').text(inverterInstance?.pa != null ? inverterInstance?.pa : "-");
            $('.value-td-pb').text(inverterInstance?.pb != null ? inverterInstance?.pb : "-");
            $('.value-td-pc').text(inverterInstance?.pc != null ? inverterInstance?.pc : "-");
            $('.value-td-ptotal').text(inverterInstance.ptotal === null || inverterInstance.ptotal === undefined ? "-" : inverterInstance.ptotal);
            $('.value-td-idc').text(inverterInstance?.idc != null ? inverterInstance?.idc : "-");
            $('.value-td-udc').text(inverterInstance?.udc != null ? inverterInstance?.udc : "-");
            $('.value-td-pdc').text(inverterInstance?.pdc != null ? inverterInstance?.pdc : "-");

            $('.value-td-pfa').text(inverterInstance?.pfa != null ? inverterInstance?.pfa : "-");
            $('.value-td-pfb').text(inverterInstance?.pfb != null ? inverterInstance?.pfb : "-");
            $('.value-td-pfc').text(inverterInstance?.pfc != null ? inverterInstance?.pfc : "-");

            $('.value-td-ep').text(inverterInstance.ep === null || inverterInstance.ep === undefined ? "-" : inverterInstance.ep);

            $('.value-td-pf').text(inverterInstance?.pf != null ? inverterInstance?.pf : "-");

            $('.value-td-f').text(inverterInstance?.f != null ? inverterInstance?.f : "-");
            $('.value-td-hs').text(inverterInstance?.pdc != null && inverterInstance?.pdc != 0 && inverterInstance?.ptotal != null ?(inverterInstance?.ptotal / inverterInstance?.pdc ).toFixed(1)  : "-")
            deviceIdCurrent = inverterInstance.deviceId;
            $('#model-oper-info-tool').hide();
            $('#string-table').hide();
            $('#weather-table').hide();
            $('#panel-table').hide();
            $('#combiner-table').hide();
            $('#inverter-table').show();

        }
    } if (device.deviceType == 2) {
        let res = await OperationInformationService.getInstantOperationWeatherPV(customerId, deviceId);
        if (res.status === 200) {
            let weatherInstance = res.data;
            $('#weather-name').text(weatherInstance.deviceName);
            $('#weather-time').html(`<i class="fa-regular fa-clock mr-1" style= "color: revert"></i>${weatherInstance.sentDate === null || weatherInstance.sentDate === undefined ? "" : moment(weatherInstance.sentDate).format(CONS.DATE_FORMAT_OPERATE)}`);
            $('#weather-temp').text(weatherInstance.temp === null || weatherInstance.temp === undefined ? "-" : weatherInstance.temp);
            $('#weather-rad').text(weatherInstance.rad === null || weatherInstance.rad === undefined ? "-" : weatherInstance.rad);
            $('#weather-h').text(weatherInstance.h === null || weatherInstance.h === undefined ? "-" : weatherInstance.h);
            $('#weather-windSp').text(weatherInstance.wind_sp === null || weatherInstance.wind_sp === undefined ? "-" : weatherInstance.wind_sp);
            $('#weather-atmos').text(weatherInstance.atmos === null || weatherInstance.atmos === undefined ? "-" : weatherInstance.atmos);

            deviceIdCurrent = weatherInstance.deviceId;
            $('#model-oper-info-tool').hide();
            $('#string-table').hide();
            $('#weather-table').show();
            $('#inverter-table').hide();
            $('#panel-table').hide();
            $('#combiner-table').hide();
        }
    } if (device.deviceType == 3) {
        let res = await OperationInformationService.getInstantOperationCombinerPV(customerId, deviceId);
        if (res.status === 200) {
            let combiner = res.data;
            $('#combiner-name').text(combiner.deviceName);
            $('#combiner-time').html(`<i class="fa-regular fa-clock mr-1" style= "color: revert"></i>${combiner.sentDate === null || combiner.sentDate === undefined ? "" : moment(combiner.sentDate).format(CONS.DATE_FORMAT_OPERATE)}`);
            $('#combiner-dcv').text(combiner.vdcCombiner === null || combiner.vdcCombiner === undefined ? "-" : combiner.vdcCombiner);
            $('#combiner-dca').text(combiner.idcCombiner === null || combiner.idcCombiner === undefined ? "-" : combiner.idcCombiner);
            $('#combiner-power').text(combiner.pdcCombiner === null || combiner.pdcCombiner === undefined ? "-" : combiner.pdcCombiner);
            $('#combiner-wattHours').text(combiner.epCombiner === null || combiner.epCombiner === undefined ? "-" : combiner.epCombiner);
            $('#combiner-temp').text(combiner.t === null || combiner.t === undefined ? "-" : combiner.t);

            deviceIdCurrent = combiner.deviceId;
            $('#model-oper-info-tool').hide();
            $('#string-table').hide();
            $('#weather-table').hide();
            $('#inverter-table').hide();
            $('#panel-table').hide();
            $('#combiner-table').show();
        }
    } if (device.deviceType == 4) {
        let res = await OperationInformationService.getInstantOperationStringPV(customerId, deviceId);
        if (res.status === 200) {
            let stringInstance = res.data;
            $('#string-name').text(stringInstance.deviceName);
            $('#th-string-time').html(`<i class="fa-regular fa-clock mr-1" style= "color: revert" ></i>${stringInstance.sentDate === null || stringInstance.sentDate === undefined ? "" : moment(stringInstance.sentDate).format(CONS.DATE_FORMAT)}`);
            $('#inDca').text(stringInstance.idcStr === null || stringInstance.idcStr === undefined ? "-" : stringInstance.idcStr);
            $('#inDCV').text(stringInstance.vdcStr === null || stringInstance.vdcStr === undefined ? "-" : stringInstance.vdcStr);
            $('#inDCW').text(stringInstance.pdcStr === null || stringInstance.pdcStr === undefined ? "-" : stringInstance.pdcStr);
            $('#inDCWh').text(stringInstance.epStr === null || stringInstance.epStr === undefined ? "-" : stringInstance.epStr);
            $('#inDCPR').text("-");
            $('#temp').text(stringInstance.tstr === null || stringInstance.tstr === undefined ? "-" : stringInstance.tstr);

            deviceIdCurrent = stringInstance.deviceId;
            $('#model-oper-info-tool').hide();
            $('#inverter-table').hide();
            $('#weather-table').hide();
            $('#combiner-table').hide();
            $('#panel-table').hide();
            $('#string-table').show();
        }
    } if (device.deviceType == 5) {
        let res = await OperationInformationService.getInstantOperationPanelPV(customerId, deviceId);
        if (res.status === 200) {
            let panel = res.data;

            $('#panel-time').html(`<i class="fa-regular fa-clock mr-1" style= "color: revert" ></i>${panel.sentDate === null || panel.sentDate === undefined ? "" : moment(panel.sentDate).format(CONS.DATE_FORMAT)}`);
            $('#panel-tempPanel').text(panel.tempPanel === null || panel.tempPanel === undefined ? "-" : panel.tempPanel);
            $('#panel-p').text(panel.p === null || panel.p === undefined ? "-" : panel.p);

            deviceIdCurrent = panel.deviceId;
            $('#model-oper-info-tool').hide();
            $('#inverter-table').hide();
            $('#weather-table').hide();
            $('#combiner-table').hide();
            $('#string-table').hide();
            $('#panel-table').show();
        }
    }

};

const SystemMapSolar = ({ ajaxGetListData, ajaxGetMeterData, projectInfo }) => {
    const location = useLocation();
    const history = useHistory();

    const { projectId, systemMapId, customerId } = useParams();
    const [meterAlready, setMeterAlready] = useState([]);
    const [systemTypeId, setSystemTypeId] = useState(2);
    const [layerTree, setlayerTree] = useState([]);
    const [nextId, setNextWindowId] = useState(0);
    const [beforeId, setBeforeWindowId] = useState(0);
    const [systemMapCurrent, setSystemMapCurrent] = useState(0);
    let [deviceCurrent, setDeviceCurrent] = useState(0);
    const [breadscrum, setBreadscrum] = useState("");
    const [deviceTime, setDeviceTime] = useState("");
    var listSystemMap = [];

    var listId = "";
    var count = 0;
    var ajaxDataMeter = [];
    var systemMapTree = [];

    const getSystemMapList = async () => {
        $("#svg-container").hide();
        setNextWindowId(0);
        setBeforeWindowId(0);

        let systemMapsResponse = await SystemMapService.getSystemMapByProjectIdAndSystemTypeId(projectId, systemTypeId);
        listSystemMap = systemMapsResponse.data;

        if (systemMapsResponse.data.length > 0) {
            for (var i = 0; i < systemMapsResponse.data.length; i++) {
                var tempLayer = {
                    id: systemMapsResponse.data[i].layer,
                    systemMap: [systemMapsResponse.data[i]],
                }

                var layerIndex = systemMapTree.findIndex(x => x.id === tempLayer.id);
                if (layerIndex >= 0) {
                    systemMapTree[layerIndex].systemMap.push(systemMapsResponse.data[i]);
                } else {
                    systemMapTree.push(tempLayer);
                }
            }
        }

        setlayerTree(systemMapTree);

        if (systemMapId) {
            let systemMapResponse = await SystemMapService.getSystemMap(systemMapId);
            loadDataFromJson(JSON.parse(systemMapResponse.data.jsonData));

            if (systemMapTree.length > 0 && systemMapTree.findIndex(x => x.id == systemMapsResponse.data[systemMapsResponse.data.findIndex(x => x.id === Number(systemMapId))].layer) < systemMapTree.length - 1) {
                setNextWindowId(systemMapTree[systemMapTree.findIndex(x => x.id == systemMapsResponse.data[systemMapsResponse.data.findIndex(x => x.id === Number(systemMapId))].layer) + 1].systemMap[0].id);
            }

            if (systemMapTree.length > 0 && systemMapTree.findIndex(x => x.id == systemMapsResponse.data[systemMapsResponse.data.findIndex(x => x.id === Number(systemMapId))].layer) > 0) {
                setBeforeWindowId(systemMapTree[systemMapTree.findIndex(x => x.id == systemMapsResponse.data[systemMapsResponse.data.findIndex(x => x.id === Number(systemMapId))].layer) - 1].systemMap[0].id);
            }

            setSystemMapCurrent(systemMapId);

            let breadscrumCurrent = "Layer " + systemMapResponse.data.layer + "/ " + systemMapResponse.data.name;

            if (listSystemMap.length > 0) {
                for (let i = 0; i < listSystemMap.length; i++) {
                    if (listSystemMap[i].jsonData != null && listSystemMap[i].jsonData != "") {
                        let systemMapData = JSON.parse(listSystemMap[i].jsonData);
                        for (let j = 0; j < systemMapData.aCards.length; j++) {
                            let obj = systemMapData.aCards[j];
                            if (obj.type == "feeder" && obj.linkId == systemMapResponse.data.id) {
                                breadscrumCurrent = "Layer " + listSystemMap[i].layer + "/ " + listSystemMap[i].name + " -> Layer " + systemMapResponse.data.layer + "/ " + systemMapResponse.data.name;
                                break;
                            }
                        }
                    }
                }
            }

            setBreadscrum(breadscrumCurrent);

            let systemMapJsonData = JSON.parse(systemMapResponse.data.jsonData);
            let deviceJsonList = "";

            if (systemMapJsonData != null && systemMapJsonData != "") {
                for (let i = 0; i < systemMapJsonData.aCards.length; i++) {
                    let obj = systemMapJsonData.aCards[i];

                    if (obj.type == "meter" || obj.type == "stmv" || obj.type == "sgmv" || obj.type == "inverter" || obj.type == "combiner" || obj.type == "panel" || obj.type == "string" || obj.type == "weather") {
                        deviceJsonList = deviceJsonList + obj.deviceId + ",";
                    }
                }
            }

            if (deviceJsonList !== "") {
                deviceJsonList = deviceJsonList.slice(0, -1);
                let systemInfoTime = await SystemMapService.getSystemInfoTime(deviceJsonList, customerId);
                if (systemInfoTime.data.length > 0) {
                    setDeviceTime(systemInfoTime.data);
                } else {
                    setDeviceTime("Không có dữ liệu");
                }
            } else {
                setDeviceTime("-");
            }

        } else {
            if (systemMapsResponse.data.length > 0) {

                let systemMapResponse = await SystemMapService.getSystemMap(systemMapsResponse.data[0].id);
                loadDataFromJson(JSON.parse(systemMapResponse.data.jsonData));

                if (systemMapTree.length > 0 && systemMapTree.findIndex(x => x.id == systemMapsResponse.data[systemMapsResponse.data.findIndex(x => x.id === Number(systemMapsResponse.data[0].id))].layer) < systemMapTree.length - 1) {
                    setNextWindowId(systemMapTree[systemMapTree.findIndex(x => x.id == systemMapsResponse.data[systemMapsResponse.data.findIndex(x => x.id === Number(systemMapsResponse.data[0].id))].layer) + 1].systemMap[0].id);
                }

                if (systemMapTree.length > 0 && systemMapTree.findIndex(x => x.id == systemMapsResponse.data[systemMapsResponse.data.findIndex(x => x.id === Number(systemMapsResponse.data[0].id))].layer) > 0) {
                    setBeforeWindowId(systemMapTree[systemMapTree.findIndex(x => x.id == systemMapsResponse.data[systemMapsResponse.data.findIndex(x => x.id === Number(systemMapsResponse.data[0].id))].layer) - 1].systemMap[0].id);
                }

                setSystemMapCurrent(systemMapsResponse.data[0].id);

                let breadscrumCurrent = "Layer " + systemMapResponse.data.layer + "/ " + systemMapResponse.data.name;

                if (listSystemMap.length > 0) {
                    for (let i = 0; i < listSystemMap.length; i++) {
                        if (listSystemMap[i].jsonData != null && listSystemMap[i].jsonData != "") {
                            let systemMapData = JSON.parse(listSystemMap[i].jsonData);
                            for (let j = 0; j < systemMapData.aCards.length; j++) {
                                let obj = systemMapData.aCards[j];
                                if (obj.type == "feeder" && obj.linkId == systemMapsResponse.data[0].id) {
                                    breadscrumCurrent = "Layer " + listSystemMap[i].layer + "/ " + listSystemMap[i].name + " -> Layer " + systemMapsResponse.data[0].layer + "/ " + systemMapsResponse.data[0].name;
                                    break;
                                }
                            }
                        }
                    }
                }

                setBreadscrum(breadscrumCurrent);

                let systemMapJsonData = JSON.parse(systemMapResponse.data.jsonData);
                let deviceJsonList = "";

                if (systemMapJsonData != null && systemMapJsonData != "") {
                    for (let i = 0; i < systemMapJsonData.aCards.length; i++) {
                        let obj = systemMapJsonData.aCards[i];
                        if (obj.type == "meter" || obj.type == "stmv" || obj.type == "sgmv" || obj.type == "inverter" || obj.type == "combiner" || obj.type == "panel" || obj.type == "string" || obj.type == "weather") {
                            deviceJsonList = deviceJsonList + obj.deviceId + ",";
                        }
                    }
                }

                if (deviceJsonList !== "") {
                    deviceJsonList = deviceJsonList.slice(0, -1);
                    let systemInfoTime = await SystemMapService.getSystemInfoTime(deviceJsonList, customerId);
                    if (systemInfoTime.data.length > 0) {
                        setDeviceTime(systemInfoTime.data);
                    } else {
                        setDeviceTime("Không có dữ liệu");
                    }
                } else {
                    setDeviceTime("-");
                }
            }
        }

        let devicesAlreadyResponse = await SystemMapService.getDevicesAlready(projectId, systemTypeId);

        if (devicesAlreadyResponse.data.length > 0) {
            setMeterAlready(devicesAlreadyResponse.data)
        };

        setDeviceCurrent(0);

        if (new URLSearchParams(location.search).get("deviceId")) {
            focusDevice(new URLSearchParams(location.search).get("deviceId"));
        };

        $("#svg-container").show();
    };

    function focusDevice(deviceId) {
        var deviceIndex = DATA.aCards.findIndex(x => x.deviceId == deviceId);
        if (deviceIndex >= 0) {
            setDeviceCurrent(deviceId);
            var deviceX = DATA.aCards[deviceIndex].x;
            var deviceY = DATA.aCards[deviceIndex].y;
            var canvasWidth = $("#grid-canvas").width();
            var canvasHeight = $("#grid-canvas").height();
            var deviceWidth = null;
            var deviceHeight = null;
            var deviceRect = null
            if (DATA.aCards[deviceIndex].type == "meter") {
                deviceWidth = $("#" + METER_LOAD.ID + DATA.aCards[deviceIndex].id)[0].getBoundingClientRect().width;
                deviceHeight = $("#" + METER_LOAD.ID + DATA.aCards[deviceIndex].id)[0].getBoundingClientRect().height;
                deviceRect = d3.select("rect[id=" + METER_LOAD.ID_FOCUS + DATA.aCards[deviceIndex].id + "]");
            } else if (DATA.aCards[deviceIndex].type == "stmv") {
                deviceWidth = $("#" + STMV.ID + DATA.aCards[deviceIndex].id)[0].getBoundingClientRect().width;
                deviceHeight = $("#" + STMV.ID + DATA.aCards[deviceIndex].id)[0].getBoundingClientRect().height;
                deviceRect = d3.select("rect[id=" + STMV.ID_FOCUS + DATA.aCards[deviceIndex].id + "]");
            } else if (DATA.aCards[deviceIndex].type == "sgmv") {
                deviceWidth = $("#" + SGMV.ID + DATA.aCards[deviceIndex].id)[0].getBoundingClientRect().width;
                deviceHeight = $("#" + SGMV.ID + DATA.aCards[deviceIndex].id)[0].getBoundingClientRect().height;
                deviceRect = d3.select("rect[id=" + SGMV.ID_FOCUS + DATA.aCards[deviceIndex].id + "]");
            } else if (DATA.aCards[deviceIndex].type == "inverter") {
                deviceWidth = $("#" + INVERTER.ID + DATA.aCards[deviceIndex].id)[0].getBoundingClientRect().width;
                deviceHeight = $("#" + INVERTER.ID + DATA.aCards[deviceIndex].id)[0].getBoundingClientRect().height;
                deviceRect = d3.select("rect[id=" + INVERTER.ID_FOCUS + DATA.aCards[deviceIndex].id + "]");
            } else if (DATA.aCards[deviceIndex].type == "combiner") {
                deviceWidth = $("#" + COMBINER.ID + DATA.aCards[deviceIndex].id)[0].getBoundingClientRect().width;
                deviceHeight = $("#" + COMBINER.ID + DATA.aCards[deviceIndex].id)[0].getBoundingClientRect().height;
                deviceRect = d3.select("rect[id=" + COMBINER.ID_FOCUS + DATA.aCards[deviceIndex].id + "]");
            } else if (DATA.aCards[deviceIndex].type == "panel") {
                deviceWidth = $("#" + PANEL.ID + DATA.aCards[deviceIndex].id)[0].getBoundingClientRect().width;
                deviceHeight = $("#" + PANEL.ID + DATA.aCards[deviceIndex].id)[0].getBoundingClientRect().height;
                deviceRect = d3.select("rect[id=" + PANEL.ID_FOCUS + DATA.aCards[deviceIndex].id + "]");
            } else if (DATA.aCards[deviceIndex].type == "string") {
                deviceWidth = $("#" + STRING.ID + DATA.aCards[deviceIndex].id)[0].getBoundingClientRect().width;
                deviceHeight = $("#" + STRING.ID + DATA.aCards[deviceIndex].id)[0].getBoundingClientRect().height;
                deviceRect = d3.select("rect[id=" + STRING.ID_FOCUS + DATA.aCards[deviceIndex].id + "]");
            } else if (DATA.aCards[deviceIndex].type == "weather") {
                deviceWidth = $("#" + WEATHER.ID + DATA.aCards[deviceIndex].id)[0].getBoundingClientRect().width;
                deviceHeight = $("#" + WEATHER.ID + DATA.aCards[deviceIndex].id)[0].getBoundingClientRect().height;
                deviceRect = d3.select("rect[id=" + WEATHER.ID_FOCUS + DATA.aCards[deviceIndex].id + "]");
            }

            var xCenter = (canvasWidth / 2) - deviceX - deviceWidth / 2;
            var yCenter = (canvasHeight / 2) - deviceY - deviceHeight / 2;

            var g_container = d3.select("#svg-container");
            g_container.attr('transform', "translate(" + xCenter + "," + yCenter + ")");
            deviceRect.attr('style', "stroke-width:1;stroke:rgb(167, 201, 66)");
        }

    }

    function loadDataFromJson(dataJson) {
        DATA.aCards = [];
        DATA.aArrows = [];

        clearInterval(ajaxGetListData.current);
        clearInterval(ajaxGetMeterData.current);

        if (dataJson != null && dataJson != "") {
            for (var i = 0; i < dataJson.aArrows.length; i++) {
                var arrow = dataJson.aArrows[i];
                DATA.aArrows.push(arrow);
                ARROW.drawLoad(arrow);
            };

            for (var i = 0; i < dataJson.aCards.length; i++) {
                let obj = dataJson.aCards[i];
                DATA.aCards.push(obj);

                switch (obj.type) {
                    case "meter":
                        if (obj.deviceId != "") {
                            listId = listId + obj.deviceId + ",";
                        }
                        METER_LOAD.drawLoad(obj);
                        break;
                    case "mba":
                        MBA.drawLoad(obj);
                        break;
                    case "rmu":
                        RMU.drawLoad(obj);
                        break;
                    case "feeder":
                        FEEDER.drawLoad(obj);
                        break;
                    case "mc":
                        MC.drawLoad(obj);
                        break;
                    case "mchor":
                        MCHOR.drawLoad(obj);
                        break;
                    case "label":
                        LABEL.drawLoad(obj);
                        break;
                    case "ultility":
                        ULTILITY.drawLoad(obj);
                        break;
                    case "busbar":
                        BUSBAR.drawLoad(obj);
                        break;
                    case "busbarver":
                        BUSBARVER.drawLoad(obj);
                        break;
                    case "stmv":
                        STMV.drawLoad(obj);
                        break;
                    case "sgmv":
                        SGMV.drawLoad(obj);
                        break;
                    case "inverter":
                        if (obj.deviceId != "") {
                            listId = listId + obj.deviceId + ",";
                        }
                        INVERTER.drawLoad(obj);
                        break;
                    case "invertersymbol":
                        INVERTERSYMBOL.drawLoad(obj);
                        break;
                    case "combiner":
                        if (obj.deviceId != "") {
                            listId = listId + obj.deviceId + ",";
                        }
                        COMBINER.drawLoad(obj);
                        break;
                    case "combinersymbol":
                        COMBINERSYMBOL.drawLoad(obj);
                        break;
                    case "panel":
                        if (obj.deviceId != "") {
                            listId = listId + obj.deviceId + ",";
                        }
                        PANEL.drawLoad(obj);
                        break;
                    case "panelsymbol":
                        PANELSYMBOL.drawLoad(obj);
                        break;
                    case "string":
                        if (obj.deviceId != "") {
                            listId = listId + obj.deviceId + ",";
                        }
                        STRING.drawLoad(obj);
                        break;
                    case "stringsymbol":
                        STRINGSYMBOL.drawLoad(obj);
                        break;
                    case "weather":
                        if (obj.deviceId != "") {
                            listId = listId + obj.deviceId + ",";
                        }
                        WEATHER.drawLoad(obj);
                        break;
                    case "weathersymbol":
                        WEATHERSYMBOL.drawLoad(obj);
                        break;
                    case "ups":
                        UPS.drawLoad(obj);
                        break;
                    case "text":
                        TEXT.drawLoad(obj);
                        break;
                    default:
                        break;
                };
            };

            listId = listId.slice(0, -1);

            if (listId.length > 0) {
                ajaxDataJson();
                getDataMeter();
                dataTimer();
                getMeterTimer();
            };
        }
    };

    let ajaxDataJson = async () => {
        if (systemTypeId === 2) {
            if (listId !== "") {
                let ajaxDataTimerResponse = await SystemMapService.getDataJson(listId, customerId);
                ajaxDataMeter = ajaxDataTimerResponse.data;
            } else {
                clearInterval(ajaxGetListData.current);
            }
        }
    };

    function dataTimer() {
        ajaxGetListData.current = setInterval(ajaxDataJson, 5000);
    };

    function getMeterTimer() {
        ajaxGetMeterData.current = setInterval(getDataMeter, 1000);
    };

    function getDataMeter() {
        if (systemTypeId == 2) {
            if (ajaxDataMeter != null && ajaxDataMeter.length > 0) {
                var listData = ajaxDataMeter[count].data.split(" ");
                if (listData != "") {
                    for (let i = 0; i < parseInt(listData.length); i++) {
                        var deviceId = listData[i].split(":")[0];
                        var uData = listData[i].split(":")[1].split("*")[0];
                        var iData = listData[i].split(":")[1].split("*")[1];
                        var pfData = listData[i].split(":")[1].split("*")[2];
                        var tData = listData[i].split(":")[1].split("*")[4];
                        var direction = listData[i].split(":")[1].split("*")[5];
                        var status = listData[i].split(":")[1].split("*")[6];
                        var deviceType = listData[i].split(":")[1].split("*")[7];
                        var uDataInverter = listData[i].split(":")[1].split("*")[14];
                        var iDataInverter = listData[i].split(":")[1].split("*")[1];
                        var pDataInverter = listData[i].split(":")[1].split("*")[2];
                        var vdcCombiner = listData[i].split(":")[1].split("*")[16];
                        var idcCombiner = listData[i].split(":")[1].split("*")[17];
                        var vdcStr= listData[i].split(":")[1].split("*")[18];
                        var idcStr = listData[i].split(":")[1].split("*")[19];
                        var uPanel = listData[i].split(":")[1].split("*")[20];
                        var iPanel = listData[i].split(":")[1].split("*")[21];
                        var temp = listData[i].split(":")[1].split("*")[22];
                        var h = listData[i].split(":")[1].split("*")[23];
                        var rad = listData[i].split(":")[1].split("*")[24];
                        var pdcCombiner = listData[i].split(":")[1].split("*")[25];
                        var inDCPR = listData[i].split(":")[1].split("*")[26];

                        if (deviceType == "1") {
                            let dataU = d3.select("text[id=" + INVERTER.ID_INVERTER_VALUE_U + deviceId + "]");
                            let dataI = d3.select("text[id=" + INVERTER.ID_INVERTER_VALUE_I + deviceId + "]");
                            let dataP = d3.select("text[id=" + INVERTER.ID_INVERTER_VALUE_P + deviceId + "]");
                            let dataDirection = d3.select("path[id=" + INVERTER.ID_PATH_INVERTER + deviceId + "]");
                            let dataStatus = d3.select("path[id=" + INVERTER.ID_STATUS_INVERTER + deviceId + "]");

                            dataU.text(uDataInverter);
                            dataI.text(iDataInverter);
                            dataP.text(pDataInverter);

                            if (Number(pDataInverter) > 0) {
                                dataDirection.attr("d", CONSTANTS.INVERTER_DOWN.path1);
                            } else if (Number(pDataInverter) < 0) {
                                dataDirection.attr("d", CONSTANTS.INVERTER_UP.path1);
                            } else {
                                dataDirection.attr("d", CONSTANTS.INVERTER.path1);
                            }

                            if (Number(status) == 0) {
                                dataStatus.attr("d", CONSTANTS.INVERTER_STATUS.path1)
                                    .attr("style", " stroke:none;fill-rule:evenodd;fill:rgb(1.568627%,98.431373%,1.568627%);fill-opacity:1;");
                            } else if (Number(status) == 1) {
                                dataStatus.attr("d", CONSTANTS.INVERTER_STATUS.path1)
                                    .attr("style", " stroke:none;fill-rule:evenodd;fill:rgb(98.431373%,51.372549%,1.568627%);fill-opacity:1;");
                            } else {
                                dataStatus.attr("d", CONSTANTS.INVERTER_STATUS.path1)
                                    .attr("style", " stroke:none;fill-rule:evenodd;fill:rgb(98.431373%,1.568627%,1.568627%);fill-opacity:1;");
                            }

                            if (count == 0) {
                                dataU.attr("fill", "#FF0000");
                                dataI.attr("fill", "#FF0000");
                                dataP.attr("fill", "#FF0000");
                            } else if (count == 1) {
                                dataU.attr("fill", "#FFCA00");
                                dataI.attr("fill", "#FFCA00");
                                dataP.attr("fill", "#FFCA00");
                            } else if (count == 2) {
                                dataU.attr("fill", "#53A1EA");
                                dataI.attr("fill", "#53A1EA");
                                dataP.attr("fill", "#53A1EA");
                            }
                        }else if (deviceType == "2"){
                            var dataTemp = d3.select("text[id=" + WEATHER.ID_WEATHER_VALUE_T + deviceId + "]");
                            var dataH = d3.select("text[id=" + WEATHER.ID_WEATHER_VALUE_H + deviceId + "]");
                            var dataRad = d3.select("text[id=" + WEATHER.ID_WEATHER_VALUE_BX + deviceId + "]");
                            var dataDirection = d3.select("path[id=" + WEATHER.ID_PATH_WEATHER + deviceId + "]");
                            var dataStatus = d3.select("path[id=" + WEATHER.ID_STATUS_WEATHER + deviceId + "]");

                            dataTemp.text(temp);
                            dataH.text(h);
                            dataRad.text(rad);


                            if (Number(status) == 0) {
                                dataStatus.attr("d", CONSTANTS.WEATHER_STATUS.path1)
                                    .attr("style", " stroke:none;fill-rule:evenodd;fill:rgb(1.568627%,98.431373%,1.568627%);fill-opacity:1;");
                            } else if (Number(status) == 1) {
                                dataStatus.attr("d", CONSTANTS.WEATHER_STATUS.path1)
                                    .attr("style", " stroke:none;fill-rule:evenodd;fill:rgb(98.431373%,51.372549%,1.568627%);fill-opacity:1;");
                            } else {
                                dataStatus.attr("d", CONSTANTS.WEATHER_STATUS.path1)
                                    .attr("style", " stroke:none;fill-rule:evenodd;fill:rgb(98.431373%,1.568627%,1.568627%);fill-opacity:1;");
                            }

                            if (count == 0) {
                                dataTemp.attr("fill", "#FF0000");
                                dataH.attr("fill", "#FF0000");
                                dataRad.attr("fill", "#FF0000");
                            } else if (count == 1) {
                                dataTemp.attr("fill", "#FFCA00");
                                dataH.attr("fill", "#FFCA00");
                                dataRad.attr("fill", "#FFCA00");
                            } else if (count == 2) {
                                dataTemp.attr("fill", "#53A1EA");
                                dataH.attr("fill", "#53A1EA");
                                dataRad.attr("fill", "#53A1EA");
                            }
                        }else if(deviceType == "3"){
                            var dataVdcCb = d3.select("text[id=" + COMBINER.ID_COMBINER_VALUE_U + deviceId + "]");
                            var dataIdcCb = d3.select("text[id=" + COMBINER.ID_COMBINER_VALUE_I + deviceId + "]");
                            var dataPdcCb = d3.select("text[id=" + COMBINER.ID_COMBINER_VALUE_P + deviceId + "]");
                            var dataDirection = d3.select("path[id=" + COMBINER.ID_PATH_COMBINER + deviceId + "]");
                            var dataStatus = d3.select("path[id=" + COMBINER.ID_STATUS_COMBINER + deviceId + "]");

                            dataVdcCb.text(vdcCombiner);
                            dataIdcCb.text(idcCombiner);
                            dataPdcCb.text(pdcCombiner);


                            if (Number(pdcCombiner) > 0) {
                                dataDirection.attr("d", CONSTANTS.COMBINER_DOWN.path1);
                            } else if (Number(pdcCombiner) < 0) {
                                dataDirection.attr("d", CONSTANTS.COMBINER_UP.path1);
                            } else {
                                dataDirection.attr("d", CONSTANTS.COMBINER.path1);
                            }

                            if (Number(status) == 0) {
                                dataStatus.attr("d", CONSTANTS.COMBINER_STATUS.path1)
                                    .attr("style", " stroke:none;fill-rule:evenodd;fill:rgb(1.568627%,98.431373%,1.568627%);fill-opacity:1;");
                            } else if (Number(status) == 1) {
                                dataStatus.attr("d", CONSTANTS.COMBINER_STATUS.path1)
                                    .attr("style", " stroke:none;fill-rule:evenodd;fill:rgb(98.431373%,51.372549%,1.568627%);fill-opacity:1;");
                            } else {
                                dataStatus.attr("d", CONSTANTS.COMBINER_STATUS.path1)
                                    .attr("style", " stroke:none;fill-rule:evenodd;fill:rgb(98.431373%,1.568627%,1.568627%);fill-opacity:1;");
                            }

                            if (count == 0) {
                                dataVdcCb.attr("fill", "#FF0000");
                                dataIdcCb.attr("fill", "#FF0000");
                                dataPdcCb.attr("fill", "#FF0000");
                            } else if (count == 1) {
                                dataVdcCb.attr("fill", "#FFCA00");
                                dataIdcCb.attr("fill", "#FFCA00");
                                dataPdcCb.attr("fill", "#FFCA00");
                            } else if (count == 2) {
                                dataVdcCb.attr("fill", "#53A1EA");
                                dataIdcCb.attr("fill", "#53A1EA");
                                dataPdcCb.attr("fill", "#53A1EA");
                            }
                        }else if(deviceType == "4"){
                            var dataVdcStr = d3.select("text[id=" + STRING.ID_STRING_VALUE_U + deviceId + "]");
                            var dataIdcStr = d3.select("text[id=" + STRING.ID_STRING_VALUE_I + deviceId + "]");
                            var dataPdcStr = d3.select("text[id=" + STRING.ID_STRING_VALUE_T + deviceId + "]");
                            var dataDirection = d3.select("path[id=" + STRING.ID_PATH_STRING + deviceId + "]");
                            var dataStatus = d3.select("path[id=" + STRING.ID_STATUS_STRING + deviceId + "]");

                            dataVdcStr.text(vdcStr);
                            dataIdcStr.text(idcStr);
                            dataPdcStr.text("-");

                            // set direction
                            if (Number(dataPdcStr) > 0) {
                                dataDirection.attr("d", CONSTANTS.STRING_DOWN.path1);
                            } else if (Number(dataPdcStr) < 0) {
                                dataDirection.attr("d", CONSTANTS.STRING_UP.path1);
                            } else {
                                dataDirection.attr("d", CONSTANTS.STRING.path1);
                            }

                            if (Number(status) == 0) {
                                dataStatus.attr("d", CONSTANTS.STRING_STATUS.path1)
                                    .attr("style", " stroke:none;fill-rule:evenodd;fill:rgb(1.568627%,98.431373%,1.568627%);fill-opacity:1;");
                            } else if (Number(status) == 1) {
                                dataStatus.attr("d", CONSTANTS.STRING_STATUS.path1)
                                    .attr("style", " stroke:none;fill-rule:evenodd;fill:rgb(98.431373%,51.372549%,1.568627%);fill-opacity:1;");
                            } else {
                                dataStatus.attr("d", CONSTANTS.STRING_STATUS.path1)
                                    .attr("style", " stroke:none;fill-rule:evenodd;fill:rgb(98.431373%,1.568627%,1.568627%);fill-opacity:1;");
                            }
                          

                            if (count == 0) {
                                dataVdcStr.attr("fill", "#FF0000");
                                dataIdcStr.attr("fill", "#FF0000");
                                dataPdcStr.attr("fill", "#FF0000");
                            } else if (count == 1) {
                                dataVdcStr.attr("fill", "#FFCA00");
                                dataIdcStr.attr("fill", "#FFCA00");
                                dataPdcStr.attr("fill", "#FFCA00");
                            } else if (count == 2) {
                                dataVdcStr.attr("fill", "#53A1EA");
                                dataIdcStr.attr("fill", "#53A1EA");
                                dataPdcStr.attr("fill", "#53A1EA");
                            }
                        }else {
                            let dataUpanel = d3.select("text[id=" + PANEL.ID_PANEL_VALUE_U + deviceId + "]");
                            let dataIpanel = d3.select("text[id=" + PANEL.ID_PANEL_VALUE_I + deviceId + "]");
                            let dataPpanel = d3.select("text[id=" + PANEL.ID_PANEL_VALUE_T + deviceId + "]");
                            let dataDirection = d3.select("path[id=" + PANEL.ID_PATH_PANEL + deviceId + "]");
                            let dataStatus = d3.select("path[id=" + PANEL.ID_STATUS_PANEL + deviceId + "]");

                            dataUpanel.text(uPanel);
                            dataIpanel.text(iPanel);
                            dataPpanel.text("-");

                            if (Number(status) == 0) {
                                dataStatus.attr("d", CONSTANTS.PANEL_STATUS.path1)
                                    .attr("style", " stroke:none;fill-rule:evenodd;fill:rgb(1.568627%,98.431373%,1.568627%);fill-opacity:1;");
                            } else if (Number(status) == 1) {
                                dataStatus.attr("d", CONSTANTS.PANEL_STATUS.path1)
                                    .attr("style", " stroke:none;fill-rule:evenodd;fill:rgb(98.431373%,51.372549%,1.568627%);fill-opacity:1;");
                            } else {
                                dataStatus.attr("d", CONSTANTS.PANEL_STATUS.path1)
                                    .attr("style", " stroke:none;fill-rule:evenodd;fill:rgb(98.431373%,1.568627%,1.568627%);fill-opacity:1;");
                            }

                            //set direction
                            // if (Number(pDataInverter) > 0) {
                            //     dataDirection.attr("d", CONSTANTS.PANEL_DOWN.path1);
                            // } else if (Number(pDataInverter) < 0) {
                            //     dataDirection.attr("d", CONSTANTS.PANEL_UP.path1);
                            // } else {
                            //     dataDirection.attr("d", CONSTANTS.PANEL.path1);
                            // }

                            
                            if (count == 0) {
                                dataUpanel.attr("fill", "#FF0000");
                                dataIpanel.attr("fill", "#FF0000");
                                dataPpanel.attr("fill", "#FF0000");
                            } else if (count == 1) {
                                dataUpanel.attr("fill", "#FFCA00");
                                dataIpanel.attr("fill", "#FFCA00");
                                dataPpanel.attr("fill", "#FFCA00");
                            } else if (count == 2) {
                                dataUpanel.attr("fill", "#53A1EA");
                                dataIpanel.attr("fill", "#53A1EA");
                                dataPpanel.attr("fill", "#53A1EA");
                            }
                        }
                    }

                    count = count + 1;

                    if (count > 2) {
                        count = 0;
                    }
                }
            }
        } else {
            clearInterval(ajaxGetMeterData.current);
        }
    };

    useEffect(() => {
        createInstance();
        setSystemTypeId(2);
        getSystemMapList();

        return () => {
            clearInterval(ajaxGetListData.current);
            clearInterval(ajaxGetMeterData.current);
        };
    }, [location]);

    return (
        <>
            <div>
                <div className="project-infor" style={{ padding: "8px 10px", display: "block", marginTop: "0px" }}>
                    <span className="project-tree">{projectInfo}</span>
                </div>
                <div className="grid-utility-home">
                    <div className="tab-content pt-1">
                        <div className="tab-pane active" id="tab-window" role="tabpanel"
                            aria-labelledby="baseIcon-tab-window">
                            {
                                layerTree?.map(
                                    (layer, index) =>
                                        <div key={index}>
                                            <span className={`tool-layer layer-0${layer.id}`}> <i className="fa-solid fa-tag" style={{color: "white"}}></i>
                                                &nbsp;LAYER {layer.id}</span>
                                            {
                                                layer.systemMap?.map(
                                                    (window, i) =>
                                                        <div key={i}>
                                                            {
                                                                systemMapCurrent == window.id ?
                                                                    <Link to={`/home/solar/${customerId}/${projectId}/systemMap/${window.id}`} style={{ display: 'block', color: '#495057', background: '#ffa87d' }} className="grid-component"><span>
                                                                        <i className="fa-regular fa-map"></i>&nbsp;
                                                                        {window.name.length > 13 ? window.name.substring(0, 10) + "..." : window.name}
                                                                    </span></Link>
                                                                    :
                                                                    <Link to={`/home/solar/${customerId}/${projectId}/systemMap/${window.id}`} style={{ display: 'block', color: '#495057' }} className="grid-component"><span>
                                                                        <i className="fa-regular fa-map"></i>&nbsp;
                                                                        {window.name.length > 13 ? window.name.substring(0, 10) + "..." : window.name}
                                                                    </span></Link>
                                                            }
                                                            {
                                                                meterAlready?.map(
                                                                    (meterItem, j) =>
                                                                        meterItem.systemMapId == window.id &&
                                                                        <Link to={`/home/solar/${customerId}/${projectId}/systemMap/${window.id}?deviceId=${meterItem.deviceId}`} style={{ display: 'block', color: '#495057', backgroundColor: `${deviceCurrent == meterItem.deviceId ? "#A7C942" : ""}` }} className="grid-device" key={j}>
                                                                            <span className={`${deviceCurrent == meterItem.deviceId && "font-weight-bold"}`}>
                                                                                <i className="fas fa-server"></i>
                                                                                &nbsp;{meterItem.deviceName.length > 13 ? meterItem.deviceName.substring(0, 10) + "..." : meterItem.deviceName}
                                                                            </span>
                                                                        </Link>
                                                                )
                                                            }
                                                        </div>
                                                )
                                            }
                                        </div>
                                )
                            }
                        </div>
                    </div>
                </div>

                <div className="window-info-home mt-4">
                    <div style={{ color: `${deviceTime == "Không có dữ liệu" ? "#dc3545" : "#45484D"}` }}>
                        {deviceTime}
                    </div>
                    <div className='mt-1'>
                        {breadscrum}
                    </div>
                </div>

                {
                    beforeId > 0 ?
                        <Link className="system-map system-up mt-4" to={`/home/solar/${customerId}/${projectId}/systemMap/${beforeId}`}><img className="system-map-to-window" src="/resources/image/icon-arrow-up.png" alt="system-map-up" /></Link>
                        :
                        <Link className="system-map system-up mt-4" to={`/home/`} onClick={(event) => event.preventDefault()}><img className="system-map-to-window" src="/resources/image/icon-arrow-up-disable.png" alt="system-map-up" /></Link>
                }

                {
                    nextId > 0 ?
                        <Link className="system-map system-down mt-4" to={`/home/solar/${customerId}/${projectId}/systemMap/${nextId}`}><img className="system-map-to-window" src="/resources/image/icon-arrow-down.png" alt="system-map-down" /></Link>
                        :
                        <Link className="system-map system-down mt-4" to={`/home/`} onClick={(event) => event.preventDefault()}><img className="system-map-to-window" src="/resources/image/icon-arrow-down-disable.png" alt="system-map-down" /></Link>
                }

                <Link className="system-map system-reset mt-4" to={`/home/`} onClick={(event) => event.preventDefault()}><img id="reset-zoom" className="system-map-to-window" src="/resources/image/icon-reset-zoom.png" alt="reset-zoom" /></Link>

                <div id="grid-canvas" className="grid-canvas-home"></div>

                <div className="modal tab-content" id="model-oper-info-tool">
                    <div className="modal-dialog modal-lg" role="document">
                        <div className="modal-content">
                            <div className="modal-header" style={{ backgroundColor: "rgb(5, 34, 116)", height: "44px" }}>
                                <h5 className="modal-title" id="value-modal-name" style={{ fontSize: "15px", color: "white", fontWeight: "bold" }}></h5>
                            </div>
                            <div className="modal-body">
                                <table className="table text-center tbl-overview table-oper-info-tool">
                                    <thead>
                                        <tr>
                                            <th id="value-th-date" style={{ backgroundColor: "white" }}></th>
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
                                            <td id="value-td-uab"></td>
                                            <td id="value-td-ubc"></td>
                                            <td id="value-td-uca"></td>
                                            <td>-</td>
                                            <td>-</td>
                                        </tr>
                                        <tr>
                                            <th scope="row">U<sub>LN</sub> [V]</th>
                                            <td id="value-td-uan"></td>
                                            <td id="value-td-ubn"></td>
                                            <td id="value-td-ucn"></td>
                                            <td>-</td>
                                            <td>-</td>
                                        </tr>
                                        <tr>
                                            <th scope="row">I [A]</th>
                                            <td id="value-td-ia"></td>
                                            <td id="value-td-ib"></td>
                                            <td id="value-td-ic"></td>
                                            <td id="value-td-in"></td>
                                            <td>-</td>
                                        </tr>
                                        <tr>
                                            <th scope="row" id="label-p">P</th>
                                            <td id="value-td-pa"></td>
                                            <td id="value-td-pb"></td>
                                            <td id="value-td-pc"></td>
                                            <td>-</td>
                                            <td id="value-td-ptotal"></td>
                                        </tr>
                                        <tr>
                                            <th scope="row" id="label-q">Q [kVAr]</th>
                                            <td id="value-td-qa"></td>
                                            <td id="value-td-qb"></td>
                                            <td id="value-td-qc"></td>
                                            <td>-</td>
                                            <td id="value-td-qtotal"></td>
                                        </tr>
                                        <tr>
                                            <th scope="row" id="label-s">S [kVA]</th>
                                            <td id="value-td-sa"></td>
                                            <td id="value-td-sb"></td>
                                            <td id="value-td-sc"></td>
                                            <td>-</td>
                                            <td id="value-td-stotal"></td>
                                        </tr>
                                        <tr>
                                            <th scope="row">PF</th>
                                            <td id="value-td-pfa"></td>
                                            <td id="value-td-pfb"></td>
                                            <td id="value-td-pfc"></td>
                                            <td>-</td>
                                            <td>-</td>
                                        </tr>
                                        <tr>
                                            <th scope="row">THD U [%]</th>
                                            <td id="value-td-thdVab"></td>
                                            <td id="value-td-thdVbc"></td>
                                            <td id="value-td-thdVca"></td>
                                            <td id="value-td-thdVan"></td>
                                            <td>-</td>
                                        </tr>
                                        <tr>
                                            <th scope="row">THD I [%]</th>
                                            <td id="value-td-thdIa"></td>
                                            <td id="value-td-thdIb"></td>
                                            <td id="value-td-thdIc"></td>
                                            <td id="value-td-thdIn"></td>
                                            <td>-</td>
                                        </tr>
                                        <tr>
                                            <th scope="row">Temp [°C]</th>
                                            <td id="value-td-t1"></td>
                                            <td id="value-td-t2"></td>
                                            <td id="value-td-t3"></td>
                                            <td>-</td>
                                            <td>-</td>
                                        </tr>
                                        <tr>
                                            <th scope="row" id="label-ep">Active Energy [kWh]</th>
                                            <td id="value-td-ep" colSpan={5}></td>
                                        </tr>
                                        <tr>
                                            <th scope="row" id="label-eq">Reactive Energy [kVArh]</th>
                                            <td id="value-td-eq" colSpan={5}></td>
                                        </tr>
                                        <tr>
                                            <th scope="row">Trạng Thái MCCB</th>
                                            <td colSpan={5}></td>
                                        </tr>
                                    </tbody>
                                </table>
                            </div>
                            <div className="modal-footer">
                                <button type="button" className="btn btn-outline-primary" data-dismiss="modal" onClick={() => {
                                    $('#model-oper-info-tool').hide()
                                }}>Đóng</button>
                                <button className="btn btn-primary" onClick={() => {
                                    history.push(`/home/solar/${customerId}/${projectId}/device-information/${deviceIdCurrent}`)
                                }}>Chi tiết</button>
                            </div>
                        </div>
                    </div>
                </div>

                <div className="modal tab-content" id="string-table">
                    <div className="modal-dialog modal-lg" role="document">
                        <div className="modal-content">
                            <div className="modal-header" style={{ backgroundColor: "rgb(5, 34, 116)", height: "44px" }}>
                                <h5 className="modal-title" id="string-name" style={{ fontSize: "15px", color: "white", fontWeight: "bold" }}></h5>
                            </div>
                            <div className="modal-body">
                                <table className="table text-center tbl-overview table-oper-info-tool">
                                    <thead>
                                        <tr>
                                            <th style={{ backgroundColor: "white" }} id='th-string-time'><i className="fa-regular fa-clock mr-1" style={{ color: "revert" }}></i></th>
                                            <th width="350px">Giá trị</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <tr>
                                            <th scope="row">U<sub>DC</sub> [V]</th>
                                            <td id='inDCV'></td>
                                        </tr>
                                        <tr>
                                            <th scope="row">I<sub>DC</sub> [A]</th>
                                            <td id='inDca'></td>
                                        </tr>
                                        <tr>
                                            <th scope="row">P [kW]</th>
                                            <td id='inDCW'></td>
                                        </tr>
                                        <tr>
                                            <th scope="row">E [kWh]</th>
                                            <td id='inDCWh'></td>
                                        </tr>
                                        <tr>
                                            <th scope="row">Temp [°C]</th>
                                            <td id='temp'></td>
                                        </tr>
                                        <tr>
                                            <th scope="row">Hiệu suất [%]</th>
                                            <td id='inDCPR'></td>
                                        </tr>
                                    </tbody>
                                </table>
                            </div>
                            <div className="modal-footer">
                                <button type="button" className="btn btn-outline-primary" data-dismiss="modal" onClick={() => {
                                    $('#string-table').hide()
                                }}>Đóng</button>
                                <button className="btn btn-primary" onClick={() => {
                                    history.push(`/home/solar/${customerId}/${projectId}/device-information/${deviceIdCurrent}`)
                                }}>Chi tiết</button>
                            </div>
                        </div>
                    </div>
                </div>

                <div className="modal tab-content" id="inverter-table">
                    <div className="modal-dialog modal-lg" role="document">
                        <div className="modal-content">
                            <div className="modal-header" style={{ backgroundColor: "rgb(5, 34, 116)", height: "44px" }}>
                                <h5 className="modal-title" id="inverter-name" style={{ fontSize: "15px", color: "white", fontWeight: "bold" }}></h5>
                            </div>
                            <div className="modal-body">
                                <table className="table text-center tbl-overview table-oper-info-tool">
                                    <thead>
                                        <tr>
                                            <th style={{ backgroundColor: "white" }} id="inverter-time"></th>
                                            <th width="100px">Phase A</th>
                                            <th width="100px">Phase B</th>
                                            <th width="100px">Phase C</th>
                                            <th width="100px">Total</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <tr>
                                            <th scope="row">U<sub>AC</sub> [V]</th>
                                            <td id="value-td-ppvphA"></td>
                                            <td id="value-td-ppvphB"></td>
                                            <td id="value-td-ppvphC"></td>
                                            <td>-</td>
                                        </tr>
                                        <tr>
                                            <th scope="row">I<sub>AC</sub> [A]</th>
                                            <td id="value-td-aphaA"></td>
                                            <td id="value-td-aphaB"></td>
                                            <td id="value-td-aphaC"></td>
                                            <td>-</td>
                                        </tr>
                                        <tr>
                                            <th scope="row">P<sub>AC</sub> [kW]</th>
                                            <td className="value-td-pa"></td>
                                            <td className="value-td-pb"></td>
                                            <td className="value-td-pc"></td>
                                            <td className="value-td-ptotal"></td>
                                        </tr>
                                        <tr>
                                            <th scope="row">U<sub>DC</sub> [V]</th>
                                            <td>-</td>
                                            <td>-</td>
                                            <td>-</td>
                                            <td className="value-td-udc"></td>
                                        </tr>
                                        <tr>
                                            <th scope="row">I<sub>DC</sub> [A]</th>
                                            <td>-</td>
                                            <td>-</td>
                                            <td>-</td>
                                            <td className="value-td-idc"></td>
                                        </tr>
                                        <tr>
                                            <th scope="row">P<sub>DC</sub> [kW]</th>
                                            <td>-</td>
                                            <td>-</td>
                                            <td>-</td>
                                            <td className="value-td-pdc"></td>
                                        </tr>
                                        <tr>
                                            <th scope="row">PF</th>
                                            <td className="value-td-pfa"></td>
                                            <td className="value-td-pfb"></td>
                                            <td className="value-td-pfc"></td>
                                            <td className="value-td-pf"></td>
                                        </tr>
                                        <tr>
                                            <th scope="row">Hiệu suất [%]</th>
                                            <td>-</td>
                                            <td>-</td>
                                            <td>-</td>
                                            <td className="value-td-hs"></td>
                                        </tr>
                                        <tr>
                                            <th scope="row">Yield [kWh]</th>
                                            <td>-</td>
                                            <td>-</td>
                                            <td>-</td>
                                            <td className="value-td-ep"></td>
                                        </tr>
                                        <tr>
                                            <th scope="row">Frequency [Hz]</th>
                                            <td>-</td>
                                            <td>-</td>
                                            <td>-</td>
                                            <td className="value-td-f"></td>
                                        </tr>
                                    </tbody>
                                </table>
                            </div>
                            <div className="modal-footer">
                                <button type="button" className="btn btn-outline-primary" data-dismiss="modal" onClick={() => {
                                    $('#inverter-table').hide()
                                }}>Đóng</button>
                                <button className="btn btn-primary" onClick={() => {
                                    history.push(`/home/solar/${customerId}/${projectId}/device-information/${deviceIdCurrent}`)
                                }}>Chi tiết</button>
                            </div>
                        </div>
                    </div>
                </div>

                <div className="modal tab-content" id="weather-table">
                    <div className="modal-dialog modal-lg" role="document">
                        <div className="modal-content">
                            <div className="modal-header" style={{ backgroundColor: "rgb(5, 34, 116)", height: "44px" }}>
                                <h5 className="modal-title" id="weather-name" style={{ fontSize: "15px", color: "white", fontWeight: "bold" }}></h5>
                            </div>
                            <div className="modal-body">
                                <table className="table text-center tbl-overview table-oper-info-tool">
                                    <thead>
                                        <tr>
                                            <th style={{ backgroundColor: "white" }} id="weather-time"></th>
                                            <th width="350px">Giá trị</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <tr>
                                            <th scope="row">Nhiệt độ môi trường [°C]</th>
                                            <td id='weather-temp'></td>
                                        </tr>
                                        <tr>
                                            <th scope="row">Bức xạ [W/m²]</th>
                                            <td id='weather-rad'></td>
                                        </tr>
                                        <tr>
                                            <th scope="row">Độ ẩm [%]</th>
                                            <td id='weather-h'></td>
                                        </tr>
                                        <tr>
                                            <th scope="row">Tốc độ gió [m/s]</th>
                                            <td id='weather-windSp'></td>
                                        </tr>
                                        <tr>
                                            <th scope="row">Áp suất [atm]</th>
                                            <td id='weather-atmos'></td>
                                        </tr>
                                    </tbody>
                                </table>
                            </div>
                            <div className="modal-footer">
                                <button type="button" className="btn btn-outline-primary" data-dismiss="modal" onClick={() => {
                                    $('#weather-table').hide()
                                }}>Đóng</button>
                                <button className="btn btn-primary" onClick={() => {
                                    history.push(`/home/solar/${customerId}/${projectId}/device-information/${deviceIdCurrent}`)
                                }}>Chi tiết</button>
                            </div>
                        </div>
                    </div>
                </div>

                <div className="modal tab-content" id="combiner-table">
                    <div className="modal-dialog modal-lg" role="document">
                        <div className="modal-content">
                            <div className="modal-header" style={{ backgroundColor: "rgb(5, 34, 116)", height: "44px" }}>
                                <h5 className="modal-title" id="combiner-name" style={{ fontSize: "15px", color: "white", fontWeight: "bold" }}></h5>
                            </div>
                            <div className="modal-body">
                                <table className="table text-center tbl-overview table-oper-info-tool">
                                    <thead>
                                        <tr>
                                            <th style={{ backgroundColor: "white" }} id="combiner-time"></th>
                                            <th width="350px">Giá trị</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <tr>
                                            <th scope="row">U<sub>DC</sub> [V]</th>
                                            <td id='combiner-dcv'></td>
                                        </tr>
                                        <tr>
                                            <th scope="row">A<sub>DC</sub> [A]</th>
                                            <td id='combiner-dca'></td>
                                        </tr>
                                        <tr>
                                            <th scope="row">P [kW]</th>
                                            <td id='combiner-power'></td>
                                        </tr>
                                        <tr>
                                            <th scope="row">E [kWh]</th>
                                            <td id='combiner-wattHours'></td>
                                        </tr>
                                        <tr>
                                            <th scope="row">Temp [°C]</th>
                                            <td id='combiner-temp'></td>
                                        </tr>
                                    </tbody>
                                </table>
                            </div>
                            <div className="modal-footer">
                                <button type="button" className="btn btn-outline-primary" data-dismiss="modal" onClick={() => {
                                    $('#combiner-table').hide()
                                }}>Đóng</button>
                                <button className="btn btn-primary" onClick={() => {
                                    history.push(`/home/solar/${customerId}/${projectId}/device-information/${deviceIdCurrent}`)
                                }}>Chi tiết</button>
                            </div>
                        </div>
                    </div>
                </div>

                <div className="modal tab-content" id="panel-table">
                    <div className="modal-dialog modal-lg" role="document">
                        <div className="modal-content">
                            <div className="modal-header" style={{ backgroundColor: "rgb(5, 34, 116)", height: "44px" }}>
                                <h5 className="modal-title" id="panel-name" style={{ fontSize: "15px", color: "white", fontWeight: "bold" }}></h5>
                            </div>
                            <div className="modal-body">
                                <table className="table text-center tbl-overview table-oper-info-tool">
                                    <thead>
                                        <tr>
                                            <th style={{ backgroundColor: "white" }} id="panel-time"></th>
                                            <th width="350px">Giá trị</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <tr>
                                            <th scope="row">U<sub>DC</sub> [V]</th>
                                            <td>-</td>
                                        </tr>
                                        <tr>
                                            <th scope="row">A<sub>DC</sub> [A]</th>
                                            <td>-</td>
                                        </tr>
                                        <tr>
                                            <th scope="row">P [kW]</th>
                                            <td id='panel-p'></td>
                                        </tr>
                                        <tr>
                                            <th scope="row">E [kWh]</th>
                                            <td>-</td>
                                        </tr>
                                        <tr>
                                            <th scope="row">Temp [°C]</th>
                                            <td id='panel-tempPanel'></td>
                                        </tr>
                                    </tbody>
                                </table>
                            </div>
                            <div className="modal-footer">
                                <button type="button" className="btn btn-outline-primary" data-dismiss="modal" onClick={() => {
                                    $('#panel-table').hide()
                                }}>Đóng</button>
                                <button className="btn btn-primary" onClick={() => {
                                    history.push(`/home/solar/${customerId}/${projectId}/device-information/${deviceIdCurrent}`)
                                }}>Chi tiết</button>
                            </div>
                        </div>
                    </div>
                </div>

            </div>
        </>
    );
}

export default SystemMapSolar