import CONSTANTS from "../../../category/customer-tool/tool-page/constant";
import * as d3 from "d3";
import { getOperationInformation } from "./index";

const $ = window.$;

let METER_LOAD = {
    ID: "gmtl",
    ID_STATUS_METER: "sim-",
    ID_PATH_METER: "pim-",
    ID_METER_U_VALUE: "iuv-",
    ID_METER_I_VALUE: "iiv-",
    ID_METER_P_VALUE: "ipv-",
    ID_METER_T_VALUE: "itv-",
    ID_DETAIL: "idtl-",
    ID_DETAIL1: "idtl1-",
    ID_DETAIL2: "idtl2-",
    ID_FOCUS: "mtlfc",

    METER_U_TEXT: "U",
    METER_I_TEXT: "I (A)",
    METER_P_TEXT: "P (kW)",
    METER_T_TEXT: "T (°C)",

    drawLoad: function (card) {

        var g_svg = d3.select("#svg-container");

        var _gCard = g_svg.datum(card).append('g')
            .attr("id", function (d) { return (METER_LOAD.ID + d.id); })
            .attr("transform", function (d) { return 'translate(' + d.x + ',' + d.y + ')'; })
            .datum({
                x: function (d) { return d.x },
                y: function (d) { return d.y }
            });

        var _rect = _gCard.datum(card).append("rect")
            .attr("id", function (d) { return (METER_LOAD.ID_FOCUS + d.id); })
            .attr("x", "-20")
            .attr("y", "-20")
            .attr("width", "280")
            .attr("height", "200")
            .attr("fill", "transparent");

        var _normalPath = _gCard.datum(card).append("path")
            .attr("id", function (d) { return (METER_LOAD.ID_STATUS_METER + d.deviceId); })
            .attr("d", "")
            .attr("style", " stroke:none;fill-rule:evenodd;fill:rgb(98.431373%,1.568627%,1.568627%);fill-opacity:1;");

        var _path1 = _gCard.datum(card).append("path")
            .attr("id", function (d) { return (METER_LOAD.ID_PATH_METER + d.deviceId); })
            .attr("d", CONSTANTS.METER.path1)
            .attr("style", "stroke:none;fill-rule:evenodd;fill:rgb(4.705882%,10.980392%,14.509804%);fill-opacity:1;");

        var _gDeviceName = _gCard.datum(card).append('g')
            .attr("transform", "translate(90,0)");

        var _svgDeviceName = _gDeviceName.append('svg')
            .attr("width", "150");

        var _deviceName = _svgDeviceName.datum(card).append("text")
            .attr("x", "50%")
            .attr("y", "25")
            .attr("fill", "black")
            .attr("dominant-baseline", "middle")
            .attr("text-anchor", "middle")
            .attr("font-size", "15")
            .text(function (d) { return d.deviceName });

        var _textU = _gCard.datum(card).append("text")
            .attr("x", "116")
            .attr("y", "50")
            .attr("fill", "black")
            .attr("dominant-baseline", "middle")
            .attr("text-anchor", "middle")
            .attr("font-size", "15")
            .text(METER_LOAD.METER_U_TEXT);

        var _textLn = _gCard.datum(card).append("text")
            .attr("x", "128")
            .attr("y", "53")
            .attr("fill", "black")
            .attr("dominant-baseline", "middle")
            .attr("text-anchor", "middle")
            .attr("font-size", "10")
            .text("LN");

        var _textV = _gCard.datum(card).append("text")
            .attr("x", "147")
            .attr("y", "50")
            .attr("fill", "black")
            .attr("dominant-baseline", "middle")
            .attr("text-anchor", "middle")
            .attr("font-size", "15")
            .text("(V)");

        var _valueU = _gCard.datum(card).append("text")
            .attr("id", function (d) { return (METER_LOAD.ID_METER_U_VALUE + d.deviceId); })
            .attr("x", "190")
            .attr("y", "50")
            .attr("fill", "black")
            .attr("dominant-baseline", "middle")
            .attr("text-anchor", "middle")
            .attr("font-size", "15")
            .text("");

        var _textI = _gCard.datum(card).append("text")
            .attr("x", "130")
            .attr("y", "80")
            .attr("fill", "black")
            .attr("dominant-baseline", "middle")
            .attr("text-anchor", "middle")
            .attr("font-size", "15")
            .text(METER_LOAD.METER_I_TEXT);

        var _valueI = _gCard.datum(card).append("text")
            .attr("id", function (d) { return (METER_LOAD.ID_METER_I_VALUE + d.deviceId); })
            .attr("x", "190")
            .attr("y", "80")
            .attr("fill", "black")
            .attr("dominant-baseline", "middle")
            .attr("text-anchor", "middle")
            .attr("font-size", "15")
            .text("");

        var _textP = _gCard.datum(card).append("text")
            .attr("x", "130")
            .attr("y", "108")
            .attr("fill", "black")
            .attr("dominant-baseline", "middle")
            .attr("text-anchor", "middle")
            .attr("font-size", "15")
            .text(METER_LOAD.METER_P_TEXT);

        var _valueP = _gCard.datum(card).append("text")
            .attr("id", function (d) { return (METER_LOAD.ID_METER_P_VALUE + d.deviceId); })
            .attr("x", "190")
            .attr("y", "108")
            .attr("fill", "black")
            .attr("dominant-baseline", "middle")
            .attr("text-anchor", "middle")
            .attr("font-size", "15")
            .text("");

        var _textT = _gCard.datum(card).append("text")
            .attr("x", "130")
            .attr("y", "137")
            .attr("fill", "black")
            .attr("dominant-baseline", "middle")
            .attr("text-anchor", "middle")
            .attr("font-size", "15")
            .text(METER_LOAD.METER_T_TEXT);

        var _valueT = _gCard.datum(card).append("text")
            .attr("id", function (d) { return (METER_LOAD.ID_METER_T_VALUE + d.deviceId); })
            .attr("x", "190")
            .attr("y", "137")
            .attr("fill", "black")
            .attr("dominant-baseline", "middle")
            .attr("text-anchor", "middle")
            .attr("font-size", "15")
            .text("");

        var _rect1 = _gCard.datum(card).append("rect")
            .attr("id", function (d) { return (METER_LOAD.ID_DETAIL1 + d.id) })
            .attr("x", "0")
            .attr("y", "0")
            .attr("width", "50")
            .attr("height", "50")
            .attr("fill", "transparent")
            .attr("cursor", "pointer")
            .on("click", METER_LOAD.detail);

        var _rect2 = _gCard.datum(card).append("rect")
            .attr("id", function (d) { return (METER_LOAD.ID_DETAIL2 + d.id) })
            .attr("x", "100")
            .attr("y", "10")
            .attr("width", "130")
            .attr("height", "140")
            .attr("fill", "transparent")
            .attr("cursor", "pointer")
            .on("click", METER_LOAD.detail);

        if (card.caculator == 1) {
            var _gCaculator = _gCard.datum(card).append('g')
                .attr("transform", "translate(210,-40)");

            var _caculatorRect = _gCaculator.datum(card).append("rect")
                .attr("x", "0")
                .attr("y", "25")
                .attr("width", "20")
                .attr("height", "20")
                .attr("fill", "transparent")
                .attr('style', "stroke-width:1;stroke:rgb(4.705882%,10.980392%,14.509804%)");;

            var _caculatorLine1 = _gCaculator.append("line")
                .attr("x1", "5")
                .attr("y1", "35")
                .attr("x2", "10")
                .attr("y2", "40")
                .attr("stroke", "#276cb8")
                .attr("style", "stroke-width:2");

            var _caculatorLine2 = _gCaculator.append("line")
                .attr("x1", "9")
                .attr("y1", "40")
                .attr("x2", "16")
                .attr("y2", "29")
                .attr("stroke", "#276cb8")
                .attr("style", "stroke-width:2");
        }
    },

    detail: function (d, data) {
        window.location.href = window.location.href.substring(0, window.location.href.indexOf("/", 1)) + "/" + data.customerId + "/" + "device-information" + "/" + data.deviceId;
    },
}

export default METER_LOAD;