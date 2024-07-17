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
import METER_LOAD from '../../../home/solar/systemMap/meter';
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
import KHOANG1 from '../../../category/customer-tool/tool-page/khoang1';
import KHOANG1SYMBOL from '../../../category/customer-tool/tool-page/khoang1symbol';
import KHOANGCAP from '../../../category/customer-tool/tool-page/khoangcap';
import KHOANGCAPSYMBOL from '../../../category/customer-tool/tool-page/khoangcapsymbol';
import KHOANGCHI from '../../../category/customer-tool/tool-page/khoangchi';
import KHOANGDODEM from '../../../category/customer-tool/tool-page/khoangdodem';
import KHOANGDODEMSYMBOL from '../../../category/customer-tool/tool-page/khoangdodemsymbol';
import KHOANGMAYCAT from '../../../category/customer-tool/tool-page/khoangmaycat';
import KHOANGMAYCATSYMBOL from '../../../category/customer-tool/tool-page/khoangmaycatsymbol';
import KHOANGTHANHCAI from '../../../category/customer-tool/tool-page/khoangthanhcai';
import KHOANGTHANHCAISYMBOL from '../../../category/customer-tool/tool-page/khoangthanhcaisymbol';
import TEXT from '../../../category/customer-tool/tool-page/text';

const $ = window.$;

var deviceIdCurrent = 0;
export const getOperationInformation = async (customerId, deviceId) => {
  let deviceRes = await DeviceService.detailsDevice(deviceId);
  let device = deviceRes.data;
  if (device.deviceType == 1) {
    let res = await OperationInformationService.getInstantOperationRmuDrawerGrid(customerId, deviceId);
    if (res.status === 200) {
      let rmuInstance = res.data;
      $('#modal-value-uan').text(rmuInstance?.uan != null && (rmuInstance.uan) >= 0 && (rmuInstance.uan) <= 50000 ? rmuInstance.uan : "-");
      $('#modal-value-ubn').text(rmuInstance?.ubn != null && (rmuInstance.ubn) >= 0 && (rmuInstance.ubn) <= 50000 ? rmuInstance.ubn : "-");
      $('#modal-value-ucn').text(rmuInstance?.ucn != null && (rmuInstance.ucn) >= 0 && (rmuInstance.ucn) <= 50000 ? rmuInstance.ucn : "-");
      $('#modal-value-ia').text(rmuInstance?.ia != null && (rmuInstance.ia) >= 0 && (rmuInstance.ia) <= 10000 ? rmuInstance.ia : "-");
      $('#modal-value-ib').text(rmuInstance?.ib != null && (rmuInstance.ib) >= 0 && (rmuInstance.ib) <= 10000 ? rmuInstance.ib : "-");
      $('#modal-value-ic').text(rmuInstance?.ic != null && (rmuInstance.ic) >= 0 && (rmuInstance.ic) <= 10000 ? rmuInstance.ic : "-");
      $('#modal-value-pa').text(rmuInstance?.pa != null && (rmuInstance.pa) >= -500000000 && (rmuInstance.pa) <= 500000000 ? rmuInstance.pa : "-");
      $('#modal-value-pb').text(rmuInstance?.pb != null && (rmuInstance.pb) >= -500000000 && (rmuInstance.pb) <= 500000000 ? rmuInstance.pb : "-");
      $('#modal-value-pc').text(rmuInstance?.pc != null && (rmuInstance.pc) >= -500000000 && (rmuInstance.pc) <= 500000000 ? rmuInstance.pc : "-");
      $('#modal-value-sawId1').text(rmuInstance?.sawId1 != null && (rmuInstance.sawId1) >= -500 && (rmuInstance.sawId1) <= 1800 ? (rmuInstance.sawId1) : "-");
      $('#modal-value-sawId2').text(rmuInstance?.sawId2 != null && (rmuInstance.sawId2) >= -500 && (rmuInstance.sawId2) <= 1800 ? (rmuInstance.sawId2) : "-");
      $('#modal-value-sawId3').text(rmuInstance?.sawId3 != null && (rmuInstance.sawId3) >= -500 && (rmuInstance.sawId3) <= 1800 ? (rmuInstance.sawId3) : "-");
      $('#modal-value-sawId4').text(rmuInstance?.sawId4 != null && (rmuInstance.sawId4) >= -500 && (rmuInstance.sawId4) <= 1800 ? (rmuInstance.sawId4) : "-");
      $('#modal-value-sawId5').text(rmuInstance?.sawId5 != null && (rmuInstance.sawId5) >= -500 && (rmuInstance.sawId5) <= 1800 ? (rmuInstance.sawId5) : "-");
      $('#modal-value-sawId6').text(rmuInstance?.sawId6 != null && (rmuInstance.sawId6) >= -500 && (rmuInstance.sawId6) <= 1800 ? (rmuInstance.sawId6) : "-");
      $('#modal-value-t').text(rmuInstance?.t != null && (rmuInstance.t) >= -50.0 && (rmuInstance.t) <= 180.0 ? (rmuInstance.t) : "-");
      $('#modal-value-h').text(rmuInstance?.h != null && (rmuInstance.h) >= 0 && (rmuInstance.h) <= 100.0 ? (rmuInstance.h) : "-");
      $('#modal-value-lfbRatio').text(rmuInstance?.lfbRatio != null && (rmuInstance.lfbRatio) >= 0 && (rmuInstance.hlfbRatio) <= 65534 ? (rmuInstance.lfbRatio) : "-");
      $('#modal-value-lfbEppc').text(rmuInstance?.lfbEppc != null && (rmuInstance.lfbEppc) >= 0 && (rmuInstance.lfbEppc) <= 65534 ? (rmuInstance.lfbEppc) : "-");
      $('#modal-value-mfbRatio').text(rmuInstance?.mfbRatio != null && (rmuInstance.mfbRatio) >= 0 && (rmuInstance.mfbRatio) <= 65534 ? (rmuInstance.mfbRatio) : "-");
      $('#modal-value-mlfbEppc').text(rmuInstance?.mlfbEppc != null && (rmuInstance.mlfbEppc) >= 0 && (rmuInstance.mlfbEppc) <= 65534 ? (rmuInstance.mlfbEppc) : "-");
      $('#modal-value-hlfbRatio').text(rmuInstance?.hlfbRatio != null && (rmuInstance.hlfbRatio) >= 0 && (rmuInstance.hlfbRatio) <= 65534 ? (rmuInstance.hlfbRatio) : "-");
      $('#modal-value-hlfbEppc').text(rmuInstance?.hlfbEppc != null && (rmuInstance.hlfbEppc) >= 0 && (rmuInstance.hlfbEppc) <= 65534 ? (rmuInstance.hlfbEppc) : "-");
      $('#modal-value-indicator').text(rmuInstance?.indicator != null && (rmuInstance.indicator) >= 0 && (rmuInstance.indicator) <= 3 ? rmuInstance.indicator : "-");
      $('#value-modal-name').text(rmuInstance.deviceName);
      $('#time').html(`<i class="fa-regular fa-clock mr-1" style="color: revert"></i>${rmuInstance?.sentDate != null ? moment(rmuInstance.sentDate).format(CONS.DATE_FORMAT_OPERATE) : ""}`);
      deviceIdCurrent = rmuInstance.deviceId;
      $('#rmu-table').show();
    }
  }

};

const SystemMap = ({ ajaxGetListData, ajaxGetMeterData, projectInfo }) => {
  const location = useLocation();
  const history = useHistory();

  const { projectId, systemMapId, customerId } = useParams();
  const [meterAlready, setMeterAlready] = useState([]);
  const [systemTypeId, setSystemTypeId] = useState(5);
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
          if (obj.type == "khoangcap") {
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

            if (obj.type == "khoangcap") {
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
      } else if (DATA.aCards[deviceIndex].type == "khoang1") {
        deviceWidth = $("#" + KHOANG1.ID + DATA.aCards[deviceIndex].id)[0].getBoundingClientRect().width;
        deviceHeight = $("#" + KHOANG1.ID + DATA.aCards[deviceIndex].id)[0].getBoundingClientRect().height;
        deviceRect = d3.select("rect[id=" + KHOANG1.ID_FOCUS + DATA.aCards[deviceIndex].id + "]");
      } else if (DATA.aCards[deviceIndex].type == "khoangcap") {
        deviceWidth = $("#" + KHOANGCAP.ID + DATA.aCards[deviceIndex].id)[0].getBoundingClientRect().width;
        deviceHeight = $("#" + KHOANGCAP.ID + DATA.aCards[deviceIndex].id)[0].getBoundingClientRect().height;
        deviceRect = d3.select("rect[id=" + KHOANGCAP.ID_FOCUS + DATA.aCards[deviceIndex].id + "]");
      } else if (DATA.aCards[deviceIndex].type == "khoangdodem") {
        deviceWidth = $("#" + KHOANGDODEM.ID + DATA.aCards[deviceIndex].id)[0].getBoundingClientRect().width;
        deviceHeight = $("#" + KHOANGDODEM.ID + DATA.aCards[deviceIndex].id)[0].getBoundingClientRect().height;
        deviceRect = d3.select("rect[id=" + KHOANGDODEM.ID_FOCUS + DATA.aCards[deviceIndex].id + "]");
      } else if (DATA.aCards[deviceIndex].type == "khoangmaycat") {
        deviceWidth = $("#" + KHOANGMAYCAT.ID + DATA.aCards[deviceIndex].id)[0].getBoundingClientRect().width;
        deviceHeight = $("#" + KHOANGMAYCAT.ID + DATA.aCards[deviceIndex].id)[0].getBoundingClientRect().height;
        deviceRect = d3.select("rect[id=" + KHOANGMAYCAT.ID_FOCUS + DATA.aCards[deviceIndex].id + "]");
      } else if (DATA.aCards[deviceIndex].type == "khoangthanhcai") {
        deviceWidth = $("#" + KHOANGTHANHCAI.ID + DATA.aCards[deviceIndex].id)[0].getBoundingClientRect().width;
        deviceHeight = $("#" + KHOANGTHANHCAI.ID + DATA.aCards[deviceIndex].id)[0].getBoundingClientRect().height;
        deviceRect = d3.select("rect[id=" + KHOANGTHANHCAI.ID_FOCUS + DATA.aCards[deviceIndex].id + "]");
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
          case "khoang1":
            if (obj.deviceId != "") {
              listId = listId + obj.deviceId + ",";
            }
            KHOANG1.drawLoad(obj);
            break;
          case "khoang1symbol":
            KHOANG1SYMBOL.drawLoad(obj);
            break;
          case "khoangcap":
            if (obj.deviceId != "") {
              listId = listId + obj.deviceId + ",";
            }
            KHOANGCAP.drawLoad(obj);
            break;
          case "khoangcapsymbol":
            KHOANGCAPSYMBOL.drawLoad(obj);
            break;
          case "khoangchi":
            KHOANGCHI.drawLoad(obj);
            break;
          case "khoangdodem":
            if (obj.deviceId != "") {
              listId = listId + obj.deviceId + ",";
            }
            KHOANGDODEM.drawLoad(obj);
            break;
          case "khoangdodemsymbol":
            KHOANGDODEMSYMBOL.drawLoad(obj);
            break;
          case "khoangmaycat":
            if (obj.deviceId != "") {
              listId = listId + obj.deviceId + ",";
            }
            KHOANGMAYCAT.drawLoad(obj);
            break;
          case "khoangmaycatsymbol":
            KHOANGMAYCATSYMBOL.drawLoad(obj);
            break;
          case "khoangthanhcai":
            if (obj.deviceId != "") {
              listId = listId + obj.deviceId + ",";
            }
            KHOANGTHANHCAI.drawLoad(obj);
            break;
          case "khoangthanhcaisymbol":
            KHOANGTHANHCAISYMBOL.drawLoad(obj);
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
    if (systemTypeId === 5) {
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
    if (systemTypeId == 5) {
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
            var deviceType = listData[i].split(":")[1].split("*")[7];
            var uDataInverter = listData[i].split(":")[1].split("*")[8];
            var iDataInverter = listData[i].split(":")[1].split("*")[9];
            var wData = listData[i].split(":")[1].split("*")[10];
            var tDataRmu = listData[i].split(":")[1].split("*")[11];
            var hDataRmu = listData[i].split(":")[1].split("*")[12];
            var indicatorDataRmu = listData[i].split(":")[1].split("*")[13];


            if (deviceType == "1") {
              var dataT = d3.select("text[id=" + KHOANGDODEM.ID_KHOANG_VALUE_T + deviceId + "]");
              var dataH = d3.select("text[id=" + KHOANGDODEM.ID_KHOANG_VALUE_H + deviceId + "]");
              var dataP = d3.select("text[id=" + KHOANGDODEM.ID_KHOANG_VALUE_P + deviceId + "]");
              var dataU = d3.select("text[id=" + KHOANGDODEM.ID_KHOANG_VALUE_U + deviceId + "]");
              var dataI = d3.select("text[id=" + KHOANGDODEM.ID_KHOANG_VALUE_I + deviceId + "]");

              // var dataDirection = d3.select("path[id=" + KHOANGDODEM.ID_PATH_INVERTER + deviceId + "]");
              var dataStatus = d3.select("circle[id=" + KHOANGDODEM.ID_STATUS_KHOANG + deviceId + "]");

              if (dataT != null) {
                if (tDataRmu == "-3276.8") {
                  tDataRmu = "-";
                }
                dataT.text(tDataRmu >= -50.0 && tDataRmu <=180.0 ? tDataRmu : "-");
              }
              if (dataH != null) {
                if (hDataRmu == "-1") {
                  hDataRmu = "-";
                }
                dataH.text(hDataRmu >= 0 && hDataRmu <= 100.0 ? hDataRmu : "-");
              }
              if (dataP != null) {
                if (indicatorDataRmu == "-1") {
                  indicatorDataRmu = "-";
                }
                dataP.text(indicatorDataRmu >= 0 && indicatorDataRmu <= 3 ? indicatorDataRmu : "-");
              }
              if (dataU != null) {
                if (uData == "-1") {
                  uData = "-";
                }
                dataU.text(uData);
              }
              if (dataI != null) {
                if (iData == "-1") {
                  iData = "-";
                }
                dataI.text(iData);
              }

              if (Number(status) == 0) {
                dataStatus.attr("fill", "#33FF33");
              } else if (Number(status) == 1) {
                dataStatus.attr("fill", "#FF8000");
              } else {
                dataStatus.attr("fill", "#FF0000");
              }

              if (count == 0) {
                dataT.attr("fill", "#FF0000");
                dataH.attr("fill", "#FF0000");
                dataP.attr("fill", "#FF0000");
                dataU.attr("fill", "#FF0000");
                dataI.attr("fill", "#FF0000");
              } else if (count == 1) {
                dataT.attr("fill", "#FFCA00");
                dataH.attr("fill", "#FFCA00");
                dataP.attr("fill", "#FFCA00");
                dataU.attr("fill", "#FFCA00");
                dataI.attr("fill", "#FFCA00");
              } else if (count == 2) {
                dataT.attr("fill", "#53A1EA");
                dataH.attr("fill", "#53A1EA");
                dataP.attr("fill", "#53A1EA");
                dataU.attr("fill", "#53A1EA");
                dataI.attr("fill", "#53A1EA");
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
    setSystemTypeId(5);
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
                      <span className={`tool-layer layer-0${layer.id}`}> <i className="fa-solid fa-tag"></i>
                        &nbsp;LAYER {layer.id}</span>
                      {
                        layer.systemMap?.map(
                          (window, i) =>
                            <div key={i}>
                              {
                                systemMapCurrent == window.id ?
                                  <Link to={`/home/grid/${customerId}/${projectId}/systemMap/${window.id}`} style={{ display: 'block', color: '#495057', background: '#ffa87d' }} className="grid-component"><span>
                                    <i className="fa-regular fa-map"></i>&nbsp;
                                    {window.name.length > 13 ? window.name.substring(0, 10) + "..." : window.name}
                                  </span></Link>
                                  :
                                  <Link to={`/home/grid/${customerId}/${projectId}/systemMap/${window.id}`} style={{ display: 'block', color: '#495057' }} className="grid-component"><span>
                                    <i className="fa-regular fa-map"></i>&nbsp;
                                    {window.name.length > 13 ? window.name.substring(0, 10) + "..." : window.name}
                                  </span></Link>
                              }
                              {
                                meterAlready?.map(
                                  (meterItem, j) =>
                                    meterItem.systemMapId == window.id &&
                                    <Link to={`/home/grid/${customerId}/${projectId}/systemMap/${window.id}?deviceId=${meterItem.deviceId}`} style={{ display: 'block', color: '#495057', backgroundColor: `${deviceCurrent == meterItem.deviceId ? "#A7C942" : ""}` }} className="grid-device" key={j}>
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
            <Link className="system-map system-up mt-4" to={`/home/grid/${customerId}/${projectId}/systemMap/${beforeId}`}><img className="system-map-to-window" src="/resources/image/icon-arrow-up.png" alt="system-map-up" /></Link>
            :
            <Link className="system-map system-up mt-4" to={`/home/`} onClick={(event) => event.preventDefault()}><img className="system-map-to-window" src="/resources/image/icon-arrow-up-disable.png" alt="system-map-up" /></Link>
        }

        {
          nextId > 0 ?
            <Link className="system-map system-down mt-4" to={`/home/grid/${customerId}/${projectId}/systemMap/${nextId}`}><img className="system-map-to-window" src="/resources/image/icon-arrow-down.png" alt="system-map-down" /></Link>
            :
            <Link className="system-map system-down mt-4" to={`/home/`} onClick={(event) => event.preventDefault()}><img className="system-map-to-window" src="/resources/image/icon-arrow-down-disable.png" alt="system-map-down" /></Link>
        }

        <Link className="system-map system-reset mt-4" to={`/home/`} onClick={(event) => event.preventDefault()}><img id="reset-zoom" className="system-map-to-window" src="/resources/image/icon-reset-zoom.png" alt="reset-zoom" /></Link>

        <div id="grid-canvas" className="grid-canvas-home"></div>

        <div className="modal tab-content" id="rmu-table">
          <div className="modal-dialog modal-lg" role="document">
            <div className="modal-content">
              <div className="modal-header" style={{ backgroundColor: "rgb(5, 34, 116)", height: "44px" }}>
                <h5 className="modal-title" id="value-modal-name" style={{ fontSize: "15px", color: "white", fontWeight: "bold" }}></h5>
              </div>
              <div className="modal-body">
                <table className="table text-center tbl-overview table-oper-info-tool">
                  <thead>
                    <tr>
                      <th style={{ backgroundColor: "white" }} id="time"></th>
                      <th width="150px">Phase A</th>
                      <th width="150px">Phase B</th>
                      <th width="150px">Phase C</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr>
                      <th scope="row">U<sub>LN</sub> [V]</th>
                      <td id="modal-value-uan"></td>
                      <td id="modal-value-ubn"></td>
                      <td id="modal-value-ucn"></td>
                    </tr>
                    <tr>
                      <th scope="row">I [A]</th>
                      <td id="modal-value-ia"></td>
                      <td id="modal-value-ib"></td>
                      <td id="modal-value-ic"></td>
                    </tr>
                    <tr>
                      <th scope="row">P [kW]</th>
                      <td id="modal-value-pa"></td>
                      <td id="modal-value-pb"></td>
                      <td id="modal-value-pc"></td>
                    </tr>
                    <tr>
                      <th scope="row">Nhiệt độ cực trên [°C]</th>
                      <td id="modal-value-sawId1"></td>
                      <td id="modal-value-sawId2"></td>
                      <td id="modal-value-sawId3"></td>
                    </tr>
                    <tr>
                      <th scope="row">Nhiệt độ cực dưới [°C]</th>
                      <td id="modal-value-sawId4"></td>
                      <td id="modal-value-sawId5"></td>
                      <td id="modal-value-sawId6"></td>
                    </tr>
                    <tr>
                      <th scope="row">Nhiệt độ khoang [°C]</th>
                      <td colSpan={3} id="modal-value-t"></td>
                    </tr>
                    <tr>
                      <th scope="row">Độ ẩm [%]</th>
                      <td colSpan={3} id="modal-value-h"></td>
                    </tr>
                    <tr>
                      <th scope="row">LFB ratio [dB]</th>
                      <td colSpan={3} id="modal-value-lfbRatio"></td>
                    </tr>
                    <tr>
                      <th scope="row">LFB EPPC</th>
                      <td colSpan={3} id="modal-value-lfbEppc"></td>
                    </tr>
                    <tr>
                      <th scope="row">MFB ratio [dB]</th>
                      <td colSpan={3} id="modal-value-mfbRatio"></td>
                    </tr>
                    <tr>
                      <th scope="row">MFB EPPC</th>
                      <td colSpan={3} id="modal-value-mlfbEppc"></td>
                    </tr>
                    <tr>
                      <th scope="row">HFB ratio[dB]</th>
                      <td colSpan={3} id="modal-value-hlfbRatio"></td>
                    </tr>
                    <tr>
                      <th scope="row">HFB EPPC</th>
                      <td colSpan={3} id="modal-value-hlfbEppc"></td>
                    </tr>
                    <tr>
                      <th scope="row">Mức chỉ thị</th>
                      <td colSpan={3} id="modal-value-indicator"></td>
                    </tr>
                  </tbody>
                </table>
              </div>
              <div className="modal-footer">
                <button type="button" className="btn btn-outline-primary" data-dismiss="modal" onClick={() => {
                  $('#rmu-table').hide()
                }}>Đóng</button>
                <button className="btn btn-primary" onClick={() => {
                  history.push(`/home/grid/${customerId}/${projectId}/device-information/${deviceIdCurrent}`)
                }}>Chi tiết</button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </>
  );
}

export default SystemMap