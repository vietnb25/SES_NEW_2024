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

const $ = window.$;

var deviceIdCurrent = 0;
let viewTypeModal;
export const getOperationInformation = async (customerId, deviceId) => {
    let res = await OperationInformationService.getInstantOperationInformation(customerId, deviceId);
    if (res.status === 200) {
        var instantOperationInfo = res.data;
        let objValueCompare = {
            ep: instantOperationInfo.ep * 1000,
            eq: instantOperationInfo.eq * 1000,
            pa: instantOperationInfo.pa,
            pb: instantOperationInfo.pb,
            pc: instantOperationInfo.pc,
            ptotal: instantOperationInfo.ptotal,
            qa: instantOperationInfo.qa,
            qb: instantOperationInfo.qb,
            qc: instantOperationInfo.qc,
            qtotal: instantOperationInfo.qtotal,
            sa: instantOperationInfo.sa,
            sb: instantOperationInfo.sb,
            sc: instantOperationInfo.sc,
            stotal: instantOperationInfo.stotal
        }

        let values = Object.values(objValueCompare).filter(value => value !== null && value > 0);

        let min = Math.min(...values);

        viewTypeModal = converter.setViewType(min);
        let label = {
            p: converter.convertLabelElectricPower(viewTypeModal, "W"),
            q: converter.convertLabelElectricPower(viewTypeModal, "VAr"),
            s: converter.convertLabelElectricPower(viewTypeModal, "VA"),
            ep: converter.convertLabelElectricPower(viewTypeModal, "Wh"),
            eq: converter.convertLabelElectricPower(viewTypeModal, "VArh"),
        }

        $("#label-p").text(`P ${label.p}`);
        $("#label-q").text(`Q ${label.q}`);
        $("#label-s").text(`S ${label.s}`);
        $("#label-ep").html(`E<sub>p</sub> ${label.ep}`);
        $("#label-eq").html(`E<sub>q</sub> ${label.eq}`);

        $('#value-modal-name').text(instantOperationInfo.deviceName);
        $('#value-th-date').html(`<i class="fa-regular fa-clock mr-1" style="color: revert"></i>${instantOperationInfo.sentDate === null || instantOperationInfo.sentDate === undefined ? "" : moment(instantOperationInfo.sentDate).format(CONS.DATE_FORMAT)}`);
        $('#value-td-uab').text(instantOperationInfo.uab !== null && instantOperationInfo.uab >=-0 && instantOperationInfo.uab <= 45000 ? instantOperationInfo.uab :"-");
        $('#value-td-ubc').text(instantOperationInfo.ubc !== null && instantOperationInfo.ubc >=-0 && instantOperationInfo.ubc <= 45000 ? instantOperationInfo.ubc :"-");
        $('#value-td-uca').text(instantOperationInfo.uca !== null && instantOperationInfo.uca >=-0 && instantOperationInfo.uca <= 45000 ? instantOperationInfo.uca :"-");
        $('#value-td-uan').text(instantOperationInfo.uan !== null && instantOperationInfo.uan >=-0 && instantOperationInfo.uan <= 45000 ? instantOperationInfo.uan :"-");
        $('#value-td-ubn').text(instantOperationInfo.ubn !== null && instantOperationInfo.ubn >=-0 && instantOperationInfo.ubn <= 45000 ? instantOperationInfo.ubn :"-");
        $('#value-td-ucn').text(instantOperationInfo.ucn !== null && instantOperationInfo.ucn >=-0 && instantOperationInfo.ucn <= 45000 ? instantOperationInfo.ucn :"-");
        $('#value-td-ia').text(instantOperationInfo.ia !== null && instantOperationInfo.ia >=-0 && instantOperationInfo.ia <= 10000 ? instantOperationInfo.ia :"-");
        $('#value-td-ib').text(instantOperationInfo.ib !== null && instantOperationInfo.ib >=-0 && instantOperationInfo.ib <= 10000 ? instantOperationInfo.ib :"-");
        $('#value-td-ic').text(instantOperationInfo.ic !== null && instantOperationInfo.ic >=-0 && instantOperationInfo.ic <= 10000 ? instantOperationInfo.ic :"-");
        $('#value-td-in').text(instantOperationInfo.in !== null && instantOperationInfo.in >=-0 && instantOperationInfo.in <= 10000 ? instantOperationInfo.in :"-");
        $('#value-td-pa').text(instantOperationInfo.pa !== null && instantOperationInfo.pa >=-2000000 && instantOperationInfo.pa <= 2000000 ? instantOperationInfo.pa :"-");
        $('#value-td-pb').text(instantOperationInfo.pb !== null && instantOperationInfo.pb >=-2000000 && instantOperationInfo.pb <= 2000000 ? instantOperationInfo.pb :"-");
        $('#value-td-pc').text(instantOperationInfo.pc !== null && instantOperationInfo.pc >=-2000000 && instantOperationInfo.pc <= 2000000 ? instantOperationInfo.pc :"-");
        $('#value-td-ptotal').text(instantOperationInfo.ptotal !== null && instantOperationInfo.ptotal >=-6000000 && instantOperationInfo.ptotal <= 6000000 ? instantOperationInfo.ptotal :"-");
        $('#value-td-qa').text(instantOperationInfo.qa !== null && instantOperationInfo.qa >=-2000000 && instantOperationInfo.qa <= 2000000 ? instantOperationInfo.qa :"-");
        $('#value-td-qb').text(instantOperationInfo.qb !== null && instantOperationInfo.qb >=-2000000 && instantOperationInfo.qb <= 2000000 ? instantOperationInfo.qb :"-");
        $('#value-td-qc').text(instantOperationInfo.qc !== null && instantOperationInfo.qc >=-2000000 && instantOperationInfo.qc <= 2000000 ? instantOperationInfo.qc :"-");
        $('#value-td-qtotal').text(instantOperationInfo.qtotal !== null && instantOperationInfo.qtotal >=-2000000 && instantOperationInfo.qtotal <= 2000000 ? instantOperationInfo.qtotal :"-");
        $('#value-td-sa').text(instantOperationInfo.sa !== null && instantOperationInfo.sa >=0 && instantOperationInfo.sa <= 2000000 ? instantOperationInfo.sa :"-");
        $('#value-td-sb').text(instantOperationInfo.sb !== null && instantOperationInfo.sb >=0 && instantOperationInfo.sb <= 2000000 ? instantOperationInfo.sb :"-");
        $('#value-td-sc').text(instantOperationInfo.sc !== null && instantOperationInfo.sc >=0 && instantOperationInfo.sc <= 2000000 ? instantOperationInfo.sc :"-");
        $('#value-td-stotal').text(instantOperationInfo.stotal !== null && instantOperationInfo.stotal >=0 && instantOperationInfo.stotal <= 6000000 ? instantOperationInfo.stotal :"-" );
        $('#value-td-pfa').text(instantOperationInfo.pfa !== null && instantOperationInfo.pfa > -1.0 && instantOperationInfo.pfa < 1.0 ? instantOperationInfo.pfa :"-");
        $('#value-td-pfb').text(instantOperationInfo.pfb !== null && instantOperationInfo.pfb > -1.0 && instantOperationInfo.pfb < 1.0 ? instantOperationInfo.pfb :"-");
        $('#value-td-pfc').text(instantOperationInfo.pfc !== null && instantOperationInfo.pfc > -1.0 && instantOperationInfo.pfc < 1.0 ? instantOperationInfo.pfc :"-");
        $('#value-td-thdVab').text(instantOperationInfo.thdVab !== null && instantOperationInfo.thdVab >=0 && instantOperationInfo.thdVab <= 100 ? instantOperationInfo.thdVab :"-" );
        $('#value-td-thdVbc').text(instantOperationInfo.thdVbc !== null && instantOperationInfo.thdVbc >=0 && instantOperationInfo.thdVbc <= 100 ? instantOperationInfo.thdVbc :"-" );
        $('#value-td-thdVca').text(instantOperationInfo.thdVca !== null && instantOperationInfo.thdVca >=0 && instantOperationInfo.thdVca <= 100 ? instantOperationInfo.thdVca :"-" );
        $('#value-td-thdVan').text(instantOperationInfo.thdVan !== null && instantOperationInfo.thdVan >=0 && instantOperationInfo.thdVan <= 100 ? instantOperationInfo.thdVan :"-" );
        $('#value-td-thdIa').text(instantOperationInfo.thdIa !== null && instantOperationInfo.thdIa >=0 && instantOperationInfo.thdIa <= 100 ? instantOperationInfo.thdIa :"-" );
        $('#value-td-thdIb').text(instantOperationInfo.thdIb !== null && instantOperationInfo.thdIb >=0 && instantOperationInfo.thdIb <= 100 ? instantOperationInfo.thdIb :"-" );
        $('#value-td-thdIc').text(instantOperationInfo.thdIc !== null && instantOperationInfo.thdIc >=0 && instantOperationInfo.thdIc <= 100 ? instantOperationInfo.thdIc :"-" );
        $('#value-td-thdIn').text(instantOperationInfo.thdIn !== null && instantOperationInfo.thdIn >=0 && instantOperationInfo.thdIn <= 100 ? instantOperationInfo.thdIn :"-" );
        
        if(instantOperationInfo.t1 == -1){
            $('#value-td-t1').text("-");
        }else {
            $('#value-td-t1').text(instantOperationInfo.t1 !== null && instantOperationInfo.t1 >=-100 && instantOperationInfo.t1 <= 200.00 ? instantOperationInfo.t1 :"-" );
        }
        if(instantOperationInfo.t2 == -1){
            $('#value-td-t2').text("-");
        }else {
            $('#value-td-t2').text(instantOperationInfo.t2 !== null && instantOperationInfo.t2 >=-100 && instantOperationInfo.t2 <= 200.00 ? instantOperationInfo.t2 :"-" );
        }
        if(instantOperationInfo.t3 == -1){
            $('#value-td-t3').text("-");
        }else {
            $('#value-td-t3').text(instantOperationInfo.t3 !== null && instantOperationInfo.t3 >=-100 && instantOperationInfo.t3 <= 200.00 ? instantOperationInfo.t3 :"-" );
        }      
        $('#value-td-ep').text(instantOperationInfo.ep !== null && instantOperationInfo.ep >=0 && instantOperationInfo.ep < 4000000000 ? instantOperationInfo.ep :"-" );
        $('#value-td-eq').text(instantOperationInfo.eq !== null && instantOperationInfo.eq >=0 && instantOperationInfo.eq < 4000000000 ? instantOperationInfo.eq :"-" );

        deviceIdCurrent = instantOperationInfo.deviceId;
        $('#model-oper-info-tool').show();
    }
};

const SystemMapLoad = ({ ajaxGetListData, ajaxGetMeterData, projectInfo }) => {
    const location = useLocation();
    const history = useHistory();

    const { projectId, systemMapId, customerId } = useParams();
    const [meterAlready, setMeterAlready] = useState([]);
    const [systemTypeId, setSystemTypeId] = useState(1);
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
            setMeterAlready(devicesAlreadyResponse.data.filter(
                (device) => device.deviceType == 1 || device.deviceType == 2 || device.deviceType == 3 || device.deviceType == 4 || device.deviceType == 5 || device.deviceType == 6 || device.deviceType == 7 || device.deviceType == 8
            ));
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
                        METER_LOAD.drawLoad(obj, viewTypeModal);
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
                        Text.drawLoad(obj);
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
        if (systemTypeId === 1) {
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
        if (systemTypeId == 1) {
            if (ajaxDataMeter != null && ajaxDataMeter.length > 0) {
                var listData = ajaxDataMeter[count].data.split(" ");
                if (listData != "") {
                    for (var i = 0; i < listData.length; i++) {
                        var deviceId = listData[i].split(":")[0];
                        var uData = listData[i].split(":")[1].split("*")[0];
                        var iData = listData[i].split(":")[1].split("*")[1];
                        var pfData = listData[i].split(":")[1].split("*")[2];
                        var tData = listData[i].split(":")[1].split("*")[4];
                        var direction = listData[i].split(":")[1].split("*")[5];
                        var status = listData[i].split(":")[1].split("*")[6];

                        var dataU = d3.select("text[id=" + METER_LOAD.ID_METER_U_VALUE + deviceId + "]");
                        var dataI = d3.select("text[id=" + METER_LOAD.ID_METER_I_VALUE + deviceId + "]");
                        var dataPF = d3.select("text[id=" + METER_LOAD.ID_METER_P_VALUE + deviceId + "]");
                        var dataT = d3.select("text[id=" + METER_LOAD.ID_METER_T_VALUE + deviceId + "]");
                        var dataDirection = d3.select("path[id=" + METER_LOAD.ID_PATH_METER + deviceId + "]");
                        var dataStatus = d3.select("path[id=" + METER_LOAD.ID_STATUS_METER + deviceId + "]");

                        dataU.text(uData);
                        dataI.text(iData);

                        var p_Total = pfData;

                        dataPF.text(p_Total);
                        dataT.text(tData);

                        if (Number(pfData) < 0) {
                            dataDirection.attr("d", CONSTANTS.METER_LOAD_DOWN.path2);
                        } else if (Number(pfData) > 0) {
                            dataDirection.attr("d", CONSTANTS.METER_LOAD_DOWN.path1);
                        } else {
                            dataDirection.attr("d", CONSTANTS.METER.path1);
                        }

                        if (Number(status) == 0) {
                            dataStatus.attr("d", CONSTANTS.METER_LOAD_DOWN.normalPath)
                                .attr("style", " stroke:none;fill-rule:evenodd;fill:rgb(1.568627%,98.431373%,1.568627%);fill-opacity:1;");
                        } else if (Number(status) == 1) {
                            dataStatus.attr("d", CONSTANTS.METER_LOAD_DOWN.warningPath)
                                .attr("style", " stroke:none;fill-rule:evenodd;fill:rgb(98.431373%,51.372549%,1.568627%);fill-opacity:1;");
                        } else {
                            dataStatus.attr("d", CONSTANTS.METER_LOAD_DOWN.errorPath)
                                .attr("style", " stroke:none;fill-rule:evenodd;fill:rgb(98.431373%,1.568627%,1.568627%);fill-opacity:1;");
                            dataDirection.attr("d", CONSTANTS.METER.path1)
                                .attr("style", "stroke:none;fill-rule:evenodd;fill:rgb(4.705882%,10.980392%,14.509804%);fill-opacity:1;");
                        }

                        if (count == 0) {
                            dataU.attr("fill", "#FF0000");
                            dataI.attr("fill", "#FF0000");
                            dataPF.attr("fill", "#FF0000");
                            dataT.attr("fill", "#FF0000");
                        } else if (count == 1) {
                            dataU.attr("fill", "#FFCA00");
                            dataI.attr("fill", "#FFCA00");
                            dataPF.attr("fill", "#FFCA00");
                            dataT.attr("fill", "#FFCA00");
                        } else if (count == 2) {
                            dataU.attr("fill", "#53A1EA");
                            dataI.attr("fill", "#53A1EA");
                            dataPF.attr("fill", "#53A1EA");
                            dataT.attr("fill", "#53A1EA");
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
        setSystemTypeId(1);
        getSystemMapList();

        return () => {
            clearInterval(ajaxGetListData.current);
            clearInterval(ajaxGetMeterData.current);
        };
    }, [location]);

    return (
        <>
            <div>
                <div className="project-infor" style={{ padding: "8px 10px", display: "block" }}>
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
                                                                    <Link to={`/home/load/${customerId}/${projectId}/systemMap/${window.id}`} style={{ display: 'block', color: '#495057', background: '#ffa87d' }} className="grid-component"><span>
                                                                        <i className="fa-regular fa-map"></i>&nbsp;
                                                                        {window.name.length > 13 ? window.name.substring(0, 10) + "..." : window.name}
                                                                    </span></Link>
                                                                    :
                                                                    <Link to={`/home/load/${customerId}/${projectId}/systemMap/${window.id}`} style={{ display: 'block', color: '#495057' }} className="grid-component"><span>
                                                                        <i className="fa-regular fa-map"></i>&nbsp;
                                                                        {window.name.length > 13 ? window.name.substring(0, 10) + "..." : window.name}
                                                                    </span></Link>
                                                            }
                                                            {
                                                                meterAlready?.map(
                                                                    (meterItem, j) =>
                                                                        meterItem.systemMapId == window.id &&
                                                                        <Link to={`/home/load/${customerId}/${projectId}/systemMap/${window.id}?deviceId=${meterItem.deviceId}`} style={{ display: 'block', color: '#495057', backgroundColor: `${deviceCurrent == meterItem.deviceId ? "#A7C942" : ""}` }} className="grid-device" key={j}>
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
                        <Link className="system-map system-up mt-4" to={`/home/load/${customerId}/${projectId}/systemMap/${beforeId}`}><img className="system-map-to-window" src="/resources/image/icon-arrow-up.png" alt="system-map-up" /></Link>
                        :
                        <Link className="system-map system-up mt-4" to={`/home/`} onClick={(event) => event.preventDefault()}><img className="system-map-to-window" src="/resources/image/icon-arrow-up-disable.png" alt="system-map-up" /></Link>
                }

                {
                    nextId > 0 ?
                        <Link className="system-map system-down mt-4" to={`/home/load/${customerId}/${projectId}/systemMap/${nextId}`}><img className="system-map-to-window" src="/resources/image/icon-arrow-down.png" alt="system-map-down" /></Link>
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
                                    history.push(`/home/load/${customerId}/${projectId}/device-information/${deviceIdCurrent}`);
                                }}>Chi tiết</button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </>
    );
}

export default SystemMapLoad;