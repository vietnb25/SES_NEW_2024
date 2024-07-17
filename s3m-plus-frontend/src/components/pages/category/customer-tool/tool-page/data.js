import * as d3 from "d3";
import COMMON from "./common";
import ARROW from "./arrow";
import ARROW2 from "./arrow2";
import METER from "./meter";
import MBA from "./mba";
import RMU from "./rmu";
import FEEDER from "./feeder";
import MC from "./mc";
import ULTILITY from "./ultility";
import LABEL from "./label";
import LABEL2 from "./label2";
import MCHOR from "./mchor";
import BUSBAR from "./busbar";
import BUSBARVER from "./busbarver";
import STMV from "./stmv";
import SGMV from "./sgmv";
import INVERTER from "./inverter";
import INVERTERSYMBOL from "./invertersymbol";
import COMBINER from "./combiner";
import COMBINERSYMBOL from "./combinersymbol";
import PANEL from "./panel";
import PANEL2 from "./panel2";
import PANELSYMBOL from "./panelsymbol";
import STRING from "./string";
import STRINGSYMBOL from "./stringsymbol";
import WEATHER from "./weather";
import WEATHERSYMBOL from "./weathersymbol";
import UPS from "./ups";
import KHOANG1 from "./khoang1";
import KHOANG1SYMBOL from "./khoang1symbol";
import KHOANGCAP from "./khoangcap";
import KHOANGCAPSYMBOL from "./khoangcapsymbol";
import KHOANGCHI from "./khoangchi";
import KHOANGDODEM from "./khoangdodem";
import KHOANGDODEMSYMBOL from "./khoangdodemsymbol";
import KHOANGMAYCAT from "./khoangmaycat";
import KHOANGMAYCATSYMBOL from "./khoangmaycatsymbol";
import KHOANGTHANHCAI from "./khoangthanhcai";
import KHOANGTHANHCAISYMBOL from "./khoangthanhcaisymbol";
import PD_HTR02 from "./pd-htr02";
import PD_AMS01 from "./pd-ams01";
import TEXT from "./text";

const $ = window.$;

var g_zoomFactor = 0.1;
var g_zoomK = 1;
var g_zoomX = 0;
var g_zoomY = 0;
var g_zoomPreviousK = 1;
var g_bIsScroll = false;

var G_KEY_DELETE = 46;

var G_MIN_ZOOM = 0.5;
var G_MAX_ZOOM = 2.0;

export var DATA = {
    'action': 0,

    'aCards': [
    ],

    'aArrows': [
    ],

    'selectedID': "",

    'selectedType': 0,

    'deviceType': 0,

    'oldDeviceType': 0,

    'oldX': 0,

    'oldY': 0,

    'x': 0,

    'y': 0
};

export var g_connect = {
    'id': "",
    'connectId': "",
    'cardId': "",
    'attachFlag': false,
    'attachMarker': "",
    'x': 0,
    'y': 0,
    'type': "",
    'circleFrom': "",
    'attachFrom': false,
    'attachTo': false,
};

var action = "";

export const setAction = (value) => {
    action = value
}

export const getAction = () => {
    return action;
}


export const createInstance = () => {
    var oldTransform = {
        k: 1,
        x: 0,
        y: 0,
    }

    d3.select("#grid-canvas").html("");

    var g_svg = d3.select("#grid-canvas").append("svg")
        .attr("width", $("#grid-canvas").width())
        .attr("height", $("#grid-canvas").height())
        .on("click", focusSvg);

    var g_container = g_svg.append('g')
        .attr("id", "svg-container")
        .attr("transform", function (d) { return 'translate(0, 0) scale(1)'; });

    g_container.append("svg:defs").append("svg:marker")
        .attr("id", "arrow")
        .attr("refX", 5)
        .attr("refY", 5)
        .attr("markerWidth", 12)
        .attr("markerHeight", 10)
        .attr("viewBox", "0 0 10 10")
        .attr("stroke-width", 0)
        .attr("orient", "auto")
        .attr("markerUnits", "userSpaceOnUse")
        .append("svg:path")
        .attr("d", "M1,1 L1,9 L10,5 L1,1");

    const handleZoom = (e) => {
        g_container.attr("transform", "translate(" + (e.transform.x - oldTransform.x + Number(COMMON.getTranslation(g_container.attr("transform"))[0])) + "," + (e.transform.y - oldTransform.y + Number(COMMON.getTranslation(g_container.attr("transform"))[1])) + ") scale(" + e.transform.k + ")");
        oldTransform = e.transform;
    }

    const zoom = d3.zoom().on('zoom', handleZoom);

    g_svg.call(zoom);

    g_svg.on("dblclick.zoom", null);

    $("#reset-zoom").click(() => {
        g_svg.call(zoom.transform, d3.zoomIdentity);
    });

    $(window).resize(function () {
        g_svg.attr("width", $("#grid-canvas").width());
        g_svg.attr("height", $("#grid-canvas").height());
    });
}

export const resetForm = () => {
    outFocus();
    $('#mba-description').val("");
    $('#mba-device-name').val("");
    $('#rmu-description').val("");
    $('#rmu-device-name').val("");
    $('#feeder-link-id').val("");
    $('#label-description').val("");
    $('#label-tag-name').val("");
    $('#ultility-description').val("");
    $('#ultility-tag-name').val("");
    $('input[name=feeder_position]').attr('disabled', false);
    $('input[name=mccb_position]').attr('disabled', false);
    $('input[name=mccb_type]').attr('disabled', false);
    $('input[name=label_type]').attr('disabled', false);
    $('input[name=label_type]').attr('disabled', false);
    $('input[name=busbar_position]').attr('disabled', false);
    $('input:radio[name="device_caculator"]').filter('[value="0"]').attr('checked', true);
    $('#stmv-description').val("");
    $('#stmv-tag-name').val("");
    $('input:radio[name="stmv_caculator"]').filter('[value="0"]').attr('checked', true);
    $('#sgmv-description').val("");
    $('#sgmv-tag-name').val("");
    $('input:radio[name="sgmv_caculator"]').filter('[value="0"]').attr('checked', true);
    $('#inverter-description').val("");
    $('#inverter-tag-name').val("");
    $('input:radio[name="inverter_caculator"]').filter('[value="0"]').attr('checked', true);
    $('#inverter-symbol-description').val("");
    $('#inverter-symbol-device-name').val("");
    $('#combiner-description').val("");
    $('#combiner-tag-name').val("");
    $('input:radio[name="combiner_caculator"]').filter('[value="0"]').attr('checked', true);
    $('#combiner-symbol-description').val("");
    $('#combiner-symbol-device-name').val("");
    $('#panel-description').val("");
    $('#panel-tag-name').val("");
    $('input:radio[name="panel_caculator"]').filter('[value="0"]').attr('checked', true);
    $('#panel-symbol-description').val("");
    $('#panel-symbol-device-name').val("");
    $('#string-description').val("");
    $('#string-tag-name').val("");
    $('input:radio[name="string_caculator"]').filter('[value="0"]').attr('checked', true);
    $('#string-symbol-description').val("");
    $('#string-symbol-device-name').val("");
    $('#weather-description').val("");
    $('#weather-tag-name').val("");
    $('input:radio[name="weather_caculator"]').filter('[value="0"]').attr('checked', true);
    $('#weather-symbol-description').val("");
    $('#weather-symbol-device-name').val("");
    $('#ups-description').val("");
    $('#ups-device-name').val("");
    $('#khoang1-description').val("");
    $('#khoang1-tag-name').val("");
    $('input:radio[name="khoang1_caculator"]').filter('[value="0"]').attr('checked', true);
    $('#khoang1-symbol-description').val("");
    $('#khoang1-symbol-device-name').val("");
    $('#khoang-cap-description').val("");
    $('#khoang-cap-tag-name').val("");
    $('input:radio[name="khoang_cap_caculator"]').filter('[value="0"]').attr('checked', true);
    $('#khoang-cap-symbol-description').val("");
    $('#khoang-cap-symbol-device-name').val("");
    $('#khoang-chi-description').val("");
    $('#khoang-chi-device-name').val("");
    $('#khoang-do-dem-description').val("");
    $('#khoang-do-dem-tag-name').val("");
    $('input:radio[name="khoang_do_dem_caculator"]').filter('[value="0"]').attr('checked', true);
    $('#khoang-do-dem-symbol-description').val("");
    $('#khoang-do-dem-symbol-device-name').val("");
    $('#khoang-may-cat-description').val("");
    $('#khoang-may-cat-tag-name').val("");
    $('input:radio[name="khoang_may_cat_caculator"]').filter('[value="0"]').attr('checked', true);
    $('#khoang-may-cat-symbol-description').val("");
    $('#khoang-may-cat-symbol-device-name').val("");
    $('#khoang-thanh-cai-description').val("");
    $('#khoang-thanh-cai-tag-name').val("");
    $('input:radio[name="khoang_thanh_cai_caculator"]').filter('[value="0"]').attr('checked', true);
    $('#khoang-thanh-cai-symbol-description').val("");
    $('#khoang-thanh-cai-symbol-device-name').val("");
    $('#text-name').val("");
    $('#load-device-type-1').val("");
    $('#load-device-type-3').val("");
    $('#load-device-type-4').val("");
    $('#load-device-type-5').val("");
    $('#load-device-type-6').val("");
    $('#load-device-type-7').val("");
    $('#load-device-type-8').val("");
    $('#load-device-type-9').val("");
    $('#load-device-type-10').val("");
    $('#load-device-type-11').val("");
    $('#load-device-type-12').val("");
    $('#load-device-type-13').val("");
    $('#load-device-type-14').val("");
    $('#load-device-type-15').val("");
    $('#load-device-type-19').val("");
    $('#load-device-type-1').val("");
    $('#load-device-type-3').val("");
    $('#load-device-type-4').val("");
    $('#load-device-type-5').val("");
    $('#load-device-type-6').val("");
    $('#load-device-type-7').val("");
    $('#load-device-type-8').val("");
    $('#load-device-type-9').val("");
    $('#load-device-type-10').val("");
    $('#load-device-type-11').val("");
    $('#load-device-type-12').val("");
    $('#load-device-type-13').val("");
    $('#load-device-type-14').val("");
    $('#load-device-type-15').val("");
    $('#load-device-type-19').val("");
    DATA.selectedID = 0;
}

export const focusSvg = () => {
    ARROW.update(DATA.selectedID, 2);
    ARROW2.update(DATA.selectedID, 2);
    METER.update(DATA.selectedID);
    MBA.update(DATA.selectedID);
    RMU.update(DATA.selectedID);
    FEEDER.update(DATA.selectedID);
    ULTILITY.update(DATA.selectedID);
    LABEL.update(DATA.selectedID);
    LABEL2.update(DATA.selectedID);

    var position = $('input[name=mccb_position]:checked').val();
    if (position == "1") {
        MCHOR.update(DATA.selectedID);
    } else if (position == "2") {
        MC.update(DATA.selectedID);
    };

    var position = $('input[name=busbar_position]:checked').val();
    var qty = $("#jointer-qty").val();
    if (qty > 1) {
        if (position == "1") {
            BUSBAR.update(DATA.selectedID);
        } else if (position == "2") {
            BUSBARVER.update(DATA.selectedID);
        };
    }

    STMV.update(DATA.selectedID);
    SGMV.update(DATA.selectedID);
    INVERTER.update(DATA.selectedID);
    INVERTERSYMBOL.update(DATA.selectedID);
    COMBINER.update(DATA.selectedID);
    COMBINERSYMBOL.update(DATA.selectedID);
    PANEL.update(DATA.selectedID);
    PANELSYMBOL.update(DATA.selectedID);
    STRING.update(DATA.selectedID);
    STRINGSYMBOL.update(DATA.selectedID);
    WEATHER.update(DATA.selectedID);
    WEATHERSYMBOL.update(DATA.selectedID);
    UPS.update(DATA.selectedID);
    KHOANG1.update(DATA.selectedID);
    KHOANG1SYMBOL.update(DATA.selectedID);
    KHOANGCAP.update(DATA.selectedID);
    KHOANGCAPSYMBOL.update(DATA.selectedID);
    KHOANGCHI.update(DATA.selectedID);
    KHOANGDODEM.update(DATA.selectedID);
    KHOANGDODEMSYMBOL.update(DATA.selectedID);
    KHOANGMAYCAT.update(DATA.selectedID);
    KHOANGMAYCATSYMBOL.update(DATA.selectedID);
    KHOANGTHANHCAI.update(DATA.selectedID);
    KHOANGTHANHCAISYMBOL.update(DATA.selectedID);
    TEXT.update(DATA.selectedID);
    PANEL2.update(DATA.selectedID);
    PD_HTR02.update(DATA.selectedID);
    PD_AMS01.update(DATA.selectedID);
    outFocus();
    showProperties(1);
}

export const outFocus = () => {
    ARROW.outFocus();
    ARROW2.outFocus();
    METER.outFocus();
    MBA.outFocus();
    RMU.outFocus();
    FEEDER.outFocus();
    MC.outFocus();
    MCHOR.outFocus();
    ULTILITY.outFocus();
    LABEL.outFocus();
    LABEL2.outFocus();
    BUSBAR.outFocus();
    BUSBARVER.outFocus();
    STMV.outFocus();
    SGMV.outFocus();
    INVERTER.outFocus();
    INVERTERSYMBOL.outFocus();
    COMBINER.outFocus();
    COMBINERSYMBOL.outFocus();
    PANEL.outFocus();
    PANELSYMBOL.outFocus();
    STRING.outFocus();
    STRINGSYMBOL.outFocus();
    WEATHER.outFocus();
    WEATHERSYMBOL.outFocus();
    UPS.outFocus();
    KHOANG1.outFocus();
    KHOANG1SYMBOL.outFocus();
    KHOANGCAP.outFocus();
    KHOANGCAPSYMBOL.outFocus();
    KHOANGCHI.outFocus();
    KHOANGDODEM.outFocus();
    KHOANGDODEMSYMBOL.outFocus();
    KHOANGMAYCAT.outFocus();
    KHOANGMAYCATSYMBOL.outFocus();
    KHOANGTHANHCAI.outFocus();
    KHOANGTHANHCAISYMBOL.outFocus();
    TEXT.outFocus();
    PANEL2.outFocus();
    PD_HTR02.outFocus();
    PD_AMS01.outFocus();
    DATA.selectedID = 0;
}

export const onChangeUpdate = () => {
    if (DATA.selectedID != 0) {
        ARROW.update(DATA.selectedID, 2);
        ARROW2.update(DATA.selectedID, 2);
        METER.update(DATA.selectedID);
        MBA.update(DATA.selectedID);
        RMU.update(DATA.selectedID);
        FEEDER.update(DATA.selectedID);
        ULTILITY.update(DATA.selectedID);
        LABEL.update(DATA.selectedID);
        LABEL2.update(DATA.selectedID);

        var position = $('input[name=mccb_position]:checked').val();
        if (position == "1") {
            MCHOR.update(DATA.selectedID);
        } else if (position == "2") {
            MC.update(DATA.selectedID);
        };

        var position = $('input[name=busbar_position]:checked').val();
        var qty = $("#jointer-qty").val();
        if (qty > 1) {
            if (position == "1") {
                BUSBAR.update(DATA.selectedID);
            } else if (position == "2") {
                BUSBARVER.update(DATA.selectedID);
            };
        }

        STMV.update(DATA.selectedID);
        SGMV.update(DATA.selectedID);
        INVERTER.update(DATA.selectedID);
        INVERTERSYMBOL.update(DATA.selectedID);
        COMBINER.update(DATA.selectedID);
        COMBINERSYMBOL.update(DATA.selectedID);
        PANEL.update(DATA.selectedID);
        PANELSYMBOL.update(DATA.selectedID);
        STRING.update(DATA.selectedID);
        STRINGSYMBOL.update(DATA.selectedID);
        WEATHER.update(DATA.selectedID);
        WEATHERSYMBOL.update(DATA.selectedID);
        UPS.update(DATA.selectedID);
        KHOANG1.update(DATA.selectedID);
        KHOANG1SYMBOL.update(DATA.selectedID);
        KHOANGCAP.update(DATA.selectedID);
        KHOANGCAPSYMBOL.update(DATA.selectedID);
        KHOANGCHI.update(DATA.selectedID);
        KHOANGDODEM.update(DATA.selectedID);
        KHOANGDODEMSYMBOL.update(DATA.selectedID);
        KHOANGMAYCAT.update(DATA.selectedID);
        KHOANGMAYCATSYMBOL.update(DATA.selectedID);
        KHOANGTHANHCAI.update(DATA.selectedID);
        KHOANGTHANHCAISYMBOL.update(DATA.selectedID);
        TEXT.update(DATA.selectedID);
        PANEL2.update(DATA.selectedID);
        PD_HTR02.update(DATA.selectedID);
        PD_AMS01.update(DATA.selectedID);
        ARROW.focus(DATA.selectedID);
        ARROW2.focus(DATA.selectedID);
        METER.focus(DATA.selectedID);
        MBA.focus(DATA.selectedID);
        RMU.focus(DATA.selectedID);
        FEEDER.focus(DATA.selectedID);
        ULTILITY.focus(DATA.selectedID);
        LABEL.focus(DATA.selectedID);
        LABEL2.focus(DATA.selectedID);
        MCHOR.focus(DATA.selectedID);
        MC.focus(DATA.selectedID);
        BUSBAR.focus(DATA.selectedID);
        BUSBARVER.focus(DATA.selectedID);
        STMV.focus(DATA.selectedID);
        SGMV.focus(DATA.selectedID);
        INVERTER.focus(DATA.selectedID);
        INVERTERSYMBOL.focus(DATA.selectedID);
        COMBINER.focus(DATA.selectedID);
        COMBINERSYMBOL.focus(DATA.selectedID);
        PANEL.focus(DATA.selectedID);
        PANELSYMBOL.focus(DATA.selectedID);
        STRING.focus(DATA.selectedID);
        STRINGSYMBOL.focus(DATA.selectedID);
        WEATHER.focus(DATA.selectedID);
        WEATHERSYMBOL.focus(DATA.selectedID);
        UPS.focus(DATA.selectedID);
        KHOANG1.focus(DATA.selectedID);
        KHOANG1SYMBOL.focus(DATA.selectedID);
        KHOANGCAP.focus(DATA.selectedID);
        KHOANGCAPSYMBOL.focus(DATA.selectedID);
        KHOANGCHI.focus(DATA.selectedID);
        KHOANGDODEM.focus(DATA.selectedID);
        KHOANGDODEMSYMBOL.focus(DATA.selectedID);
        KHOANGMAYCAT.focus(DATA.selectedID);
        KHOANGMAYCATSYMBOL.focus(DATA.selectedID);
        KHOANGTHANHCAI.focus(DATA.selectedID);
        KHOANGTHANHCAISYMBOL.focus(DATA.selectedID);
        TEXT.focus(DATA.selectedID);
        PANEL2.focus(DATA.selectedID);
        PD_HTR02.focus(DATA.selectedID);
        PD_AMS01.focus(DATA.selectedID);
    }
}

export const showProperties = (type) => {

    $('.grid-component').each(function (i, obj) {
        $(this).removeClass("active ");
    });

    $("#component-" + type).addClass("active");

    $('.component-properties').each(function (i, obj) {
        $(this).hide();
    });

    switch (type) {
        case 1:
            $('#window-properties').show();
            if ($('#grid-action').is(':visible')) {
                $('#grid-action').hide();
            }
            $('#window-properties').addClass("active");
            break;
        case 2:
            $('#arrow-properties').show();
            if ($('#grid-action').is(':hidden')) {
                $('#grid-action').show();
            }
            break;
        case 3:
            $('#meter-properties').show();
            if ($('#grid-action').is(':hidden')) {
                $('#grid-action').show();
            }
            break;
        case 4:
            $('#mba-properties').show();
            if ($('#grid-action').is(':hidden')) {
                $('#grid-action').show();
            }
            break;
        case 5:
            $('#rmu-properties').show();
            if ($('#grid-action').is(':hidden')) {
                $('#grid-action').show();
            }
            break;
        case 6:
            $('#feeder-properties').show();
            if ($('#grid-action').is(':hidden')) {
                $('#grid-action').show();
            }
            break;
        case 7:
            $('#mc-properties').show();
            if ($('#grid-action').is(':hidden')) {
                $('#grid-action').show();
            }
            break;
        case 8:
            $('#label-properties').show();
            if ($('#grid-action').is(':hidden')) {
                $('#grid-action').show();
            }
            break;
        case 9:
            $('#ultility-properties').show();
            if ($('#grid-action').is(':hidden')) {
                $('#grid-action').show();
            }
            break;
        case 10:
            $('#busbar-properties').show();
            if ($('#grid-action').is(':hidden')) {
                $('#grid-action').show();
            }
            break;
        case 11:
            $('#stmv-properties').show();
            if ($('#grid-action').is(':hidden')) {
                $('#grid-action').show();
            }
            break;
        case 12:
            $('#sgmv-properties').show();
            if ($('#grid-action').is(':hidden')) {
                $('#grid-action').show();
            }
            break;
        case 13:
            $('#inverter-properties').show();
            if ($('#grid-action').is(':hidden')) {
                $('#grid-action').show();
            }
            break;
        case 14:
            $('#inverter-symbol-properties').show();
            if ($('#grid-action').is(':hidden')) {
                $('#grid-action').show();
            }
            break;
        case 15:
            $('#combiner-properties').show();
            if ($('#grid-action').is(':hidden')) {
                $('#grid-action').show();
            }
            break;
        case 16:
            $('#combiner-symbol-properties').show();
            if ($('#grid-action').is(':hidden')) {
                $('#grid-action').show();
            }
            break;
        case 17:
            $('#panel-properties').show();
            if ($('#grid-action').is(':hidden')) {
                $('#grid-action').show();
            }
            break;
        case 18:
            $('#panel-symbol-properties').show();
            if ($('#grid-action').is(':hidden')) {
                $('#grid-action').show();
            }
            break;
        case 19:
            $('#string-properties').show();
            if ($('#grid-action').is(':hidden')) {
                $('#grid-action').show();
            }
            break;
        case 20:
            $('#string-symbol-properties').show();
            if ($('#grid-action').is(':hidden')) {
                $('#grid-action').show();
            }
            break;
        case 21:
            $('#weather-properties').show();
            if ($('#grid-action').is(':hidden')) {
                $('#grid-action').show();
            }
            break;
        case 22:
            $('#weather-symbol-properties').show();
            if ($('#grid-action').is(':hidden')) {
                $('#grid-action').show();
            }
            break;
        case 23:
            $('#ups-properties').show();
            if ($('#grid-action').is(':hidden')) {
                $('#grid-action').show();
            }
            break;
        case 24:
            $('#khoang1-properties').show();
            if ($('#grid-action').is(':hidden')) {
                $('#grid-action').show();
            }
            break;
        case 25:
            $('#khoang1-symbol-properties').show();
            if ($('#grid-action').is(':hidden')) {
                $('#grid-action').show();
            }
            break;
        case 26:
            $('#khoang-cap-properties').show();
            if ($('#grid-action').is(':hidden')) {
                $('#grid-action').show();
            }
            break;
        case 27:
            $('#khoang-cap-symbol-properties').show();
            if ($('#grid-action').is(':hidden')) {
                $('#grid-action').show();
            }
            break;
        case 28:
            $('#khoang-chi-properties').show();
            if ($('#grid-action').is(':hidden')) {
                $('#grid-action').show();
            }
            break;
        case 29:
            $('#khoang-do-dem-properties').show();
            if ($('#grid-action').is(':hidden')) {
                $('#grid-action').show();
            }
            break;
        case 30:
            $('#khoang-do-dem-symbol-properties').show();
            if ($('#grid-action').is(':hidden')) {
                $('#grid-action').show();
            }
            break;
        case 31:
            $('#khoang-may-cat-properties').show();
            if ($('#grid-action').is(':hidden')) {
                $('#grid-action').show();
            }
            break;
        case 32:
            $('#khoang-may-cat-symbol-properties').show();
            if ($('#grid-action').is(':hidden')) {
                $('#grid-action').show();
            }
            break;
        case 33:
            $('#khoang-thanh-cai-properties').show();
            if ($('#grid-action').is(':hidden')) {
                $('#grid-action').show();
            }
            break;
        case 34:
            $('#khoang-thanh-cai-symbol-properties').show();
            if ($('#grid-action').is(':hidden')) {
                $('#grid-action').show();
            }
            break;
        case 35:
            $('#text-properties').show();
            if ($('#grid-action').is(':hidden')) {
                $('#grid-action').show();
            }
            break;
        case 46:
            $('#label2-properties').show();
            if ($('#grid-action').is(':hidden')) {
                $('#grid-action').show();
            }
            break;
        case 47:
            $('#label3-properties').show();
            if ($('#grid-action').is(':hidden')) {
                $('#grid-action').show();
            }
            break;
        case 48:
            $('#label4-properties').show();
            if ($('#grid-action').is(':hidden')) {
                $('#grid-action').show();
            }
            break;
        case 49:
            $('#label5-properties').show();
            if ($('#grid-action').is(':hidden')) {
                $('#grid-action').show();
            }
            break;
        case 50:
            $('#label6-properties').show();
            if ($('#grid-action').is(':hidden')) {
                $('#grid-action').show();
            }
            break;
        case 51:
            $('#label7-properties').show();
            if ($('#grid-action').is(':hidden')) {
                $('#grid-action').show();
            }
            break;
        case 52:
            $('#label8-properties').show();
            if ($('#grid-action').is(':hidden')) {
                $('#grid-action').show();
            }
            break;
        case 53:
            // không xử lý
            break;
        case 54:
            $('#htr02-properties').show();
            if ($('#grid-action').is(':hidden')) {
                $('#grid-action').show();
            }
            break;
        case 55:
            $('#ams01-properties').show();
            if ($('#grid-action').is(':hidden')) {
                $('#grid-action').show();
            }
            break;
    };
};

export const dragAll = (d, _gCard) => {
    d3.selectAll("g[id^=" + METER.ID + "]").each(function (c) {
        if (d.id != c.id) {
            var _isOverlap = COMMON.intersectRect(_gCard.node(), this);
            if (_isOverlap) {
                METER.focus(this.id.replace(METER.ID, ''));
            } else {
                METER.outFocus(this.id.replace(METER.ID, ''));
            }
        }
    });

    d3.selectAll("g[id^=" + MBA.ID + "]").each(function (c) {
        var _isOverlap = COMMON.intersectRect(_gCard.node(), this);
        if (_isOverlap) {
            MBA.focus(this.id.replace(MBA.ID, ''));
        } else {
            MBA.outFocus(this.id.replace(MBA.ID, ''));
        }
    });

    d3.selectAll("g[id^=" + RMU.ID + "]").each(function (c) {
        var _isOverlap = COMMON.intersectRect(_gCard.node(), this);
        if (_isOverlap) {
            RMU.focus(this.id.replace(RMU.ID, ''));
        } else {
            RMU.outFocus(this.id.replace(RMU.ID, ''));
        }
    });

    d3.selectAll("g[id^=" + FEEDER.ID + "]").each(function (c) {
        var _isOverlap = COMMON.intersectRect(_gCard.node(), this);
        if (_isOverlap) {
            FEEDER.focus(this.id.replace(FEEDER.ID, ''));
        } else {
            FEEDER.outFocus(this.id.replace(FEEDER.ID, ''));
        }
    });

    d3.selectAll("g[id^=" + MC.ID + "]").each(function (c) {
        var _isOverlap = COMMON.intersectRect(_gCard.node(), this);
        if (_isOverlap) {
            MC.focus(this.id.replace(MC.ID, ''));
        } else {
            MC.outFocus(this.id.replace(MC.ID, ''));
        }
    });

    d3.selectAll("g[id^=" + MCHOR.ID + "]").each(function (c) {
        var _isOverlap = COMMON.intersectRect(_gCard.node(), this);
        if (_isOverlap) {
            MCHOR.focus(this.id.replace(MCHOR.ID, ''));
        } else {
            MCHOR.outFocus(this.id.replace(MCHOR.ID, ''));
        }
    });

    d3.selectAll("g[id^=" + LABEL.ID + "]").each(function (c) {
        var _isOverlap = COMMON.intersectRect(_gCard.node(), this);
        if (_isOverlap) {
            LABEL.focus(this.id.replace(LABEL.ID, ''));
        } else {
            LABEL.outFocus(this.id.replace(LABEL.ID, ''));
        }
    });

    d3.selectAll("g[id^=" + ULTILITY.ID + "]").each(function (c) {
        var _isOverlap = COMMON.intersectRect(_gCard.node(), this);
        if (_isOverlap) {
            ULTILITY.focus(this.id.replace(ULTILITY.ID, ''));
        } else {
            ULTILITY.outFocus(this.id.replace(ULTILITY.ID, ''));
        }
    });

    d3.selectAll("g[id^=" + BUSBAR.ID + "]").each(function (c) {
        var _isOverlap = COMMON.intersectRect(_gCard.node(), this);
        if (_isOverlap) {
            BUSBAR.focus(this.id.replace(BUSBAR.ID, ''));
        } else {
            BUSBAR.outFocus(this.id.replace(BUSBAR.ID, ''));
        }
    });

    d3.selectAll("g[id^=" + BUSBARVER.ID + "]").each(function (c) {
        var _isOverlap = COMMON.intersectRect(_gCard.node(), this);
        if (_isOverlap) {
            BUSBARVER.focus(this.id.replace(BUSBARVER.ID, ''));
        } else {
            BUSBARVER.outFocus(this.id.replace(BUSBARVER.ID, ''));
        }
    });

    d3.selectAll("g[id^=" + STMV.ID + "]").each(function (c) {
        var _isOverlap = COMMON.intersectRect(_gCard.node(), this);
        if (_isOverlap) {
            STMV.focus(this.id.replace(STMV.ID, ''));
        } else {
            STMV.outFocus(this.id.replace(STMV.ID, ''));
        }
    });

    d3.selectAll("g[id^=" + SGMV.ID + "]").each(function (c) {
        var _isOverlap = COMMON.intersectRect(_gCard.node(), this);
        if (_isOverlap) {
            SGMV.focus(this.id.replace(SGMV.ID, ''));
        } else {
            SGMV.outFocus(this.id.replace(SGMV.ID, ''));
        }
    });

    d3.selectAll("g[id^=" + INVERTER.ID + "]").each(function (c) {
        var _isOverlap = COMMON.intersectRect(_gCard.node(), this);
        if (_isOverlap) {
            INVERTER.focus(this.id.replace(INVERTER.ID, ''));
        } else {
            INVERTER.outFocus(this.id.replace(INVERTER.ID, ''));
        }
    });

    d3.selectAll("g[id^=" + INVERTERSYMBOL.ID + "]").each(function (c) {
        var _isOverlap = COMMON.intersectRect(_gCard.node(), this);
        if (_isOverlap) {
            INVERTERSYMBOL.focus(this.id.replace(INVERTERSYMBOL.ID, ''));
        } else {
            INVERTERSYMBOL.outFocus(this.id.replace(INVERTERSYMBOL.ID, ''));
        }
    });

    d3.selectAll("g[id^=" + COMBINER.ID + "]").each(function (c) {
        var _isOverlap = COMMON.intersectRect(_gCard.node(), this);
        if (_isOverlap) {
            COMBINER.focus(this.id.replace(COMBINER.ID, ''));
        } else {
            COMBINER.outFocus(this.id.replace(COMBINER.ID, ''));
        }
    });

    d3.selectAll("g[id^=" + COMBINERSYMBOL.ID + "]").each(function (c) {
        var _isOverlap = COMMON.intersectRect(_gCard.node(), this);
        if (_isOverlap) {
            COMBINERSYMBOL.focus(this.id.replace(COMBINERSYMBOL.ID, ''));
        } else {
            COMBINERSYMBOL.outFocus(this.id.replace(COMBINERSYMBOL.ID, ''));
        }
    });

    d3.selectAll("g[id^=" + PANEL.ID + "]").each(function (c) {
        var _isOverlap = COMMON.intersectRect(_gCard.node(), this);
        if (_isOverlap) {
            PANEL.focus(this.id.replace(PANEL.ID, ''));
        } else {
            PANEL.outFocus(this.id.replace(PANEL.ID, ''));
        }
    });

    d3.selectAll("g[id^=" + PANELSYMBOL.ID + "]").each(function (c) {
        var _isOverlap = COMMON.intersectRect(_gCard.node(), this);
        if (_isOverlap) {
            PANELSYMBOL.focus(this.id.replace(PANELSYMBOL.ID, ''));
        } else {
            PANELSYMBOL.outFocus(this.id.replace(PANELSYMBOL.ID, ''));
        }
    });

    d3.selectAll("g[id^=" + STRING.ID + "]").each(function (c) {
        var _isOverlap = COMMON.intersectRect(_gCard.node(), this);
        if (_isOverlap) {
            STRING.focus(this.id.replace(STRING.ID, ''));
        } else {
            STRING.outFocus(this.id.replace(STRING.ID, ''));
        }
    });

    d3.selectAll("g[id^=" + STRINGSYMBOL.ID + "]").each(function (c) {
        var _isOverlap = COMMON.intersectRect(_gCard.node(), this);
        if (_isOverlap) {
            STRINGSYMBOL.focus(this.id.replace(STRINGSYMBOL.ID, ''));
        } else {
            STRINGSYMBOL.outFocus(this.id.replace(STRINGSYMBOL.ID, ''));
        }
    });

    d3.selectAll("g[id^=" + WEATHER.ID + "]").each(function (c) {
        var _isOverlap = COMMON.intersectRect(_gCard.node(), this);
        if (_isOverlap) {
            WEATHER.focus(this.id.replace(WEATHER.ID, ''));
        } else {
            WEATHER.outFocus(this.id.replace(WEATHER.ID, ''));
        }
    });

    d3.selectAll("g[id^=" + WEATHERSYMBOL.ID + "]").each(function (c) {
        var _isOverlap = COMMON.intersectRect(_gCard.node(), this);
        if (_isOverlap) {
            WEATHERSYMBOL.focus(this.id.replace(WEATHERSYMBOL.ID, ''));
        } else {
            WEATHERSYMBOL.outFocus(this.id.replace(WEATHERSYMBOL.ID, ''));
        }
    });

    d3.selectAll("g[id^=" + UPS.ID + "]").each(function (c) {
        var _isOverlap = COMMON.intersectRect(_gCard.node(), this);
        if (_isOverlap) {
            UPS.focus(this.id.replace(UPS.ID, ''));
        } else {
            UPS.outFocus(this.id.replace(UPS.ID, ''));
        }
    });

    d3.selectAll("g[id^=" + KHOANG1.ID + "]").each(function (c) {
        var _isOverlap = COMMON.intersectRect(_gCard.node(), this);
        if (_isOverlap) {
            KHOANG1.focus(this.id.replace(KHOANG1.ID, ''));
        } else {
            KHOANG1.outFocus(this.id.replace(KHOANG1.ID, ''));
        }
    });

    d3.selectAll("g[id^=" + KHOANG1SYMBOL.ID + "]").each(function (c) {
        var _isOverlap = COMMON.intersectRect(_gCard.node(), this);
        if (_isOverlap) {
            KHOANG1SYMBOL.focus(this.id.replace(KHOANG1SYMBOL.ID, ''));
        } else {
            KHOANG1SYMBOL.outFocus(this.id.replace(KHOANG1SYMBOL.ID, ''));
        }
    });

    d3.selectAll("g[id^=" + KHOANGCAP.ID + "]").each(function (c) {
        var _isOverlap = COMMON.intersectRect(_gCard.node(), this);
        if (_isOverlap) {
            KHOANGCAP.focus(this.id.replace(KHOANGCAP.ID, ''));
        } else {
            KHOANGCAP.outFocus(this.id.replace(KHOANGCAP.ID, ''));
        }
    });

    d3.selectAll("g[id^=" + KHOANGCAPSYMBOL.ID + "]").each(function (c) {
        var _isOverlap = COMMON.intersectRect(_gCard.node(), this);
        if (_isOverlap) {
            KHOANGCAPSYMBOL.focus(this.id.replace(KHOANGCAPSYMBOL.ID, ''));
        } else {
            KHOANGCAPSYMBOL.outFocus(this.id.replace(KHOANGCAPSYMBOL.ID, ''));
        }
    });

    d3.selectAll("g[id^=" + KHOANGCHI.ID + "]").each(function (c) {
        var _isOverlap = COMMON.intersectRect(_gCard.node(), this);
        if (_isOverlap) {
            KHOANGCHI.focus(this.id.replace(KHOANGCHI.ID, ''));
        } else {
            KHOANGCHI.outFocus(this.id.replace(KHOANGCHI.ID, ''));
        }
    });

    d3.selectAll("g[id^=" + KHOANGDODEM.ID + "]").each(function (c) {
        var _isOverlap = COMMON.intersectRect(_gCard.node(), this);
        if (_isOverlap) {
            KHOANGDODEM.focus(this.id.replace(KHOANGDODEM.ID, ''));
        } else {
            KHOANGDODEM.outFocus(this.id.replace(KHOANGDODEM.ID, ''));
        }
    });

    d3.selectAll("g[id^=" + KHOANGDODEMSYMBOL.ID + "]").each(function (c) {
        var _isOverlap = COMMON.intersectRect(_gCard.node(), this);
        if (_isOverlap) {
            KHOANGDODEMSYMBOL.focus(this.id.replace(KHOANGDODEMSYMBOL.ID, ''));
        } else {
            KHOANGDODEMSYMBOL.outFocus(this.id.replace(KHOANGDODEMSYMBOL.ID, ''));
        }
    });

    d3.selectAll("g[id^=" + KHOANGMAYCAT.ID + "]").each(function (c) {
        var _isOverlap = COMMON.intersectRect(_gCard.node(), this);
        if (_isOverlap) {
            KHOANGMAYCAT.focus(this.id.replace(KHOANGMAYCAT.ID, ''));
        } else {
            KHOANGMAYCAT.outFocus(this.id.replace(KHOANGMAYCAT.ID, ''));
        }
    });

    d3.selectAll("g[id^=" + KHOANGMAYCATSYMBOL.ID + "]").each(function (c) {
        var _isOverlap = COMMON.intersectRect(_gCard.node(), this);
        if (_isOverlap) {
            KHOANGMAYCATSYMBOL.focus(this.id.replace(KHOANGMAYCATSYMBOL.ID, ''));
        } else {
            KHOANGMAYCATSYMBOL.outFocus(this.id.replace(KHOANGMAYCATSYMBOL.ID, ''));
        }
    });

    d3.selectAll("g[id^=" + KHOANGTHANHCAI.ID + "]").each(function (c) {
        var _isOverlap = COMMON.intersectRect(_gCard.node(), this);
        if (_isOverlap) {
            KHOANGTHANHCAI.focus(this.id.replace(KHOANGTHANHCAI.ID, ''));
        } else {
            KHOANGTHANHCAI.outFocus(this.id.replace(KHOANGTHANHCAI.ID, ''));
        }
    });

    d3.selectAll("g[id^=" + KHOANGTHANHCAISYMBOL.ID + "]").each(function (c) {
        var _isOverlap = COMMON.intersectRect(_gCard.node(), this);
        if (_isOverlap) {
            KHOANGTHANHCAISYMBOL.focus(this.id.replace(KHOANGTHANHCAISYMBOL.ID, ''));
        } else {
            KHOANGTHANHCAISYMBOL.outFocus(this.id.replace(KHOANGTHANHCAISYMBOL.ID, ''));
        }
    });

    d3.selectAll("g[id^=" + PD_HTR02.ID + "]").each(function (c) {
        var _isOverlap = COMMON.intersectRect(_gCard.node(), this);
        if (_isOverlap) {
            PD_HTR02.focus(this.id.replace(PD_HTR02.ID, ''));
        } else {
            PD_HTR02.outFocus(this.id.replace(PD_HTR02.ID, ''));
        }
    });

    d3.selectAll("g[id^=" + PD_AMS01.ID + "]").each(function (c) {
        var _isOverlap = COMMON.intersectRect(_gCard.node(), this);
        if (_isOverlap) {
            PD_AMS01.focus(this.id.replace(PD_AMS01.ID, ''));
        } else {
            PD_AMS01.outFocus(this.id.replace(PD_AMS01.ID, ''));
        }
    });
}

export const dragConnect = (OBJECT, d, _circleTopMaker, _circleBottomMaker, _circleLeftMaker, _circleRightMaker) => {
    d3.selectAll("circle[id^=" + METER.ID_CIRCLE_CONNECT + "]").each(function (c) {
        if (d.id != c.id) {
            var _isOverlapT = COMMON.intersectRect(_circleTopMaker.node(), this);
            var _isOverlapB = COMMON.intersectRect(_circleBottomMaker.node(), this);
            var _isOverlapL = COMMON.intersectRect(_circleLeftMaker.node(), this);
            var _isOverlapR = COMMON.intersectRect(_circleRightMaker.node(), this);
            if (_isOverlapT || _isOverlapB || _isOverlapL || _isOverlapR) {
                g_connect.id = this.id;
                g_connect.cardId = this.id.split("-").pop();
                g_connect.type = "meter";
                if (_isOverlapT) {
                    g_connect.circleFrom = OBJECT.ID_CONNECT_TOP_MARKER;
                } else if (_isOverlapB) {
                    g_connect.circleFrom = OBJECT.ID_CONNECT_BOTTOM_MARKER;
                } else if (_isOverlapL) {
                    g_connect.circleFrom = OBJECT.ID_CONNECT_LEFT_MARKER;
                } else if (_isOverlapR) {
                    g_connect.circleFrom = OBJECT.ID_CONNECT_RIGHT_MARKER;
                };
            };
        }
    });

    d3.selectAll("circle[id^=" + MBA.ID_CIRCLE_CONNECT + "]").each(function (c) {
        var _isOverlapT = COMMON.intersectRect(_circleTopMaker.node(), this);
        var _isOverlapB = COMMON.intersectRect(_circleBottomMaker.node(), this);
        var _isOverlapL = COMMON.intersectRect(_circleLeftMaker.node(), this);
        var _isOverlapR = COMMON.intersectRect(_circleRightMaker.node(), this);
        if (_isOverlapT || _isOverlapB || _isOverlapL || _isOverlapR) {
            g_connect.id = this.id;
            g_connect.cardId = this.id.split("-").pop();
            g_connect.type = "mba";
            if (_isOverlapT) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_TOP_MARKER;
            } else if (_isOverlapB) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_BOTTOM_MARKER;
            } else if (_isOverlapL) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_LEFT_MARKER;
            } else if (_isOverlapR) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_RIGHT_MARKER;
            };
        };
    });

    d3.selectAll("circle[id^=" + RMU.ID_CIRCLE_CONNECT + "]").each(function (c) {
        var _isOverlapT = COMMON.intersectRect(_circleTopMaker.node(), this);
        var _isOverlapB = COMMON.intersectRect(_circleBottomMaker.node(), this);
        var _isOverlapL = COMMON.intersectRect(_circleLeftMaker.node(), this);
        var _isOverlapR = COMMON.intersectRect(_circleRightMaker.node(), this);
        if (_isOverlapT || _isOverlapB || _isOverlapL || _isOverlapR) {
            g_connect.id = this.id;
            g_connect.cardId = this.id.split("-").pop();
            g_connect.type = "rmu";
            if (_isOverlapT) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_TOP_MARKER;
            } else if (_isOverlapB) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_BOTTOM_MARKER;
            } else if (_isOverlapL) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_LEFT_MARKER;
            } else if (_isOverlapR) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_RIGHT_MARKER;
            };
        };
    });

    d3.selectAll("circle[id^=" + FEEDER.ID_CIRCLE_CONNECT + "]").each(function (c) {
        var _isOverlapT = COMMON.intersectRect(_circleTopMaker.node(), this);
        var _isOverlapB = COMMON.intersectRect(_circleBottomMaker.node(), this);
        var _isOverlapL = COMMON.intersectRect(_circleLeftMaker.node(), this);
        var _isOverlapR = COMMON.intersectRect(_circleRightMaker.node(), this);
        if (_isOverlapT || _isOverlapB || _isOverlapL || _isOverlapR) {
            g_connect.id = this.id;
            g_connect.cardId = this.id.split("-").pop();
            g_connect.type = "feeder";
            if (_isOverlapT) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_TOP_MARKER;
            } else if (_isOverlapB) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_BOTTOM_MARKER;
            } else if (_isOverlapL) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_LEFT_MARKER;
            } else if (_isOverlapR) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_RIGHT_MARKER;
            };
        };
    });

    d3.selectAll("circle[id^=" + MC.ID_CIRCLE_CONNECT + "]").each(function (c) {
        var _isOverlapT = COMMON.intersectRect(_circleTopMaker.node(), this);
        var _isOverlapB = COMMON.intersectRect(_circleBottomMaker.node(), this);
        var _isOverlapL = COMMON.intersectRect(_circleLeftMaker.node(), this);
        var _isOverlapR = COMMON.intersectRect(_circleRightMaker.node(), this);
        if (_isOverlapT || _isOverlapB || _isOverlapL || _isOverlapR) {
            g_connect.id = this.id;
            g_connect.cardId = this.id.split("-").pop();
            g_connect.type = "mc";
            if (_isOverlapT) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_TOP_MARKER;
            } else if (_isOverlapB) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_BOTTOM_MARKER;
            } else if (_isOverlapL) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_LEFT_MARKER;
            } else if (_isOverlapR) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_RIGHT_MARKER;
            };
        };
    });

    d3.selectAll("circle[id^=" + MCHOR.ID_CIRCLE_CONNECT + "]").each(function (c) {
        var _isOverlapT = COMMON.intersectRect(_circleTopMaker.node(), this);
        var _isOverlapB = COMMON.intersectRect(_circleBottomMaker.node(), this);
        var _isOverlapL = COMMON.intersectRect(_circleLeftMaker.node(), this);
        var _isOverlapR = COMMON.intersectRect(_circleRightMaker.node(), this);
        if (_isOverlapT || _isOverlapB || _isOverlapL || _isOverlapR) {
            g_connect.id = this.id;
            g_connect.cardId = this.id.split("-").pop();
            g_connect.type = "mchor";
            if (_isOverlapT) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_TOP_MARKER;
            } else if (_isOverlapB) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_BOTTOM_MARKER;
            } else if (_isOverlapL) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_LEFT_MARKER;
            } else if (_isOverlapR) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_RIGHT_MARKER;
            };
        };
    });

    d3.selectAll("circle[id^=" + LABEL.ID_CIRCLE_CONNECT + "]").each(function (c) {
        var _isOverlapT = COMMON.intersectRect(_circleTopMaker.node(), this);
        var _isOverlapB = COMMON.intersectRect(_circleBottomMaker.node(), this);
        var _isOverlapL = COMMON.intersectRect(_circleLeftMaker.node(), this);
        var _isOverlapR = COMMON.intersectRect(_circleRightMaker.node(), this);
        if (_isOverlapT || _isOverlapB || _isOverlapL || _isOverlapR) {
            g_connect.id = this.id;
            g_connect.cardId = this.id.split("-").pop();
            g_connect.type = "label";
            if (_isOverlapT) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_TOP_MARKER;
            } else if (_isOverlapB) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_BOTTOM_MARKER;
            } else if (_isOverlapL) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_LEFT_MARKER;
            } else if (_isOverlapR) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_RIGHT_MARKER;
            };
        };
    });

    d3.selectAll("circle[id^=" + ULTILITY.ID_CIRCLE_CONNECT + "]").each(function (c) {
        var _isOverlapT = COMMON.intersectRect(_circleTopMaker.node(), this);
        var _isOverlapB = COMMON.intersectRect(_circleBottomMaker.node(), this);
        var _isOverlapL = COMMON.intersectRect(_circleLeftMaker.node(), this);
        var _isOverlapR = COMMON.intersectRect(_circleRightMaker.node(), this);
        if (_isOverlapT || _isOverlapB || _isOverlapL || _isOverlapR) {
            g_connect.id = this.id;
            g_connect.cardId = this.id.split("-").pop();
            g_connect.type = "ultility";
            if (_isOverlapT) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_TOP_MARKER;
            } else if (_isOverlapB) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_BOTTOM_MARKER;
            } else if (_isOverlapL) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_LEFT_MARKER;
            } else if (_isOverlapR) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_RIGHT_MARKER;
            };
        };
    });

    d3.selectAll("circle[id^=" + BUSBAR.ID_CIRCLE_CONNECT + "]").each(function (c) {
        var _isOverlapT = COMMON.intersectRect(_circleTopMaker.node(), this);
        var _isOverlapB = COMMON.intersectRect(_circleBottomMaker.node(), this);
        var _isOverlapL = COMMON.intersectRect(_circleLeftMaker.node(), this);
        var _isOverlapR = COMMON.intersectRect(_circleRightMaker.node(), this);
        if (_isOverlapT || _isOverlapB || _isOverlapL || _isOverlapR) {
            g_connect.id = this.id;
            g_connect.cardId = this.id.split("-").pop();
            g_connect.type = "busbar";
            if (_isOverlapT) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_TOP_MARKER;
            } else if (_isOverlapB) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_BOTTOM_MARKER;
            } else if (_isOverlapL) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_LEFT_MARKER;
            } else if (_isOverlapR) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_RIGHT_MARKER;
            };
        };
    });

    d3.selectAll("circle[id^=" + BUSBARVER.ID_CIRCLE_CONNECT + "]").each(function (c) {
        var _isOverlapT = COMMON.intersectRect(_circleTopMaker.node(), this);
        var _isOverlapB = COMMON.intersectRect(_circleBottomMaker.node(), this);
        var _isOverlapL = COMMON.intersectRect(_circleLeftMaker.node(), this);
        var _isOverlapR = COMMON.intersectRect(_circleRightMaker.node(), this);
        if (_isOverlapT || _isOverlapB || _isOverlapL || _isOverlapR) {
            g_connect.id = this.id;
            g_connect.cardId = this.id.split("-").pop();
            g_connect.type = "busbarver";
            if (_isOverlapT) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_TOP_MARKER;
            } else if (_isOverlapB) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_BOTTOM_MARKER;
            } else if (_isOverlapL) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_LEFT_MARKER;
            } else if (_isOverlapR) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_RIGHT_MARKER;
            };
        };
    });

    d3.selectAll("circle[id^=" + STMV.ID_CIRCLE_CONNECT + "]").each(function (c) {
        var _isOverlapT = COMMON.intersectRect(_circleTopMaker.node(), this);
        var _isOverlapB = COMMON.intersectRect(_circleBottomMaker.node(), this);
        var _isOverlapL = COMMON.intersectRect(_circleLeftMaker.node(), this);
        var _isOverlapR = COMMON.intersectRect(_circleRightMaker.node(), this);
        if (_isOverlapT || _isOverlapB || _isOverlapL || _isOverlapR) {
            g_connect.id = this.id;
            g_connect.cardId = this.id.split("-").pop();
            g_connect.type = "stmv";
            if (_isOverlapT) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_TOP_MARKER;
            } else if (_isOverlapB) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_BOTTOM_MARKER;
            } else if (_isOverlapL) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_LEFT_MARKER;
            } else if (_isOverlapR) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_RIGHT_MARKER;
            };
        };
    });

    d3.selectAll("circle[id^=" + SGMV.ID_CIRCLE_CONNECT + "]").each(function (c) {
        var _isOverlapT = COMMON.intersectRect(_circleTopMaker.node(), this);
        var _isOverlapB = COMMON.intersectRect(_circleBottomMaker.node(), this);
        var _isOverlapL = COMMON.intersectRect(_circleLeftMaker.node(), this);
        var _isOverlapR = COMMON.intersectRect(_circleRightMaker.node(), this);
        if (_isOverlapT || _isOverlapB || _isOverlapL || _isOverlapR) {
            g_connect.id = this.id;
            g_connect.cardId = this.id.split("-").pop();
            g_connect.type = "sgmv";
            if (_isOverlapT) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_TOP_MARKER;
            } else if (_isOverlapB) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_BOTTOM_MARKER;
            } else if (_isOverlapL) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_LEFT_MARKER;
            } else if (_isOverlapR) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_RIGHT_MARKER;
            };
        };
    });

    d3.selectAll("circle[id^=" + INVERTER.ID_CIRCLE_CONNECT + "]").each(function (c) {
        var _isOverlapT = COMMON.intersectRect(_circleTopMaker.node(), this);
        var _isOverlapB = COMMON.intersectRect(_circleBottomMaker.node(), this);
        var _isOverlapL = COMMON.intersectRect(_circleLeftMaker.node(), this);
        var _isOverlapR = COMMON.intersectRect(_circleRightMaker.node(), this);
        if (_isOverlapT || _isOverlapB || _isOverlapL || _isOverlapR) {
            g_connect.id = this.id;
            g_connect.cardId = this.id.split("-").pop();
            g_connect.type = "inverter";
            if (_isOverlapT) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_TOP_MARKER;
            } else if (_isOverlapB) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_BOTTOM_MARKER;
            } else if (_isOverlapL) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_LEFT_MARKER;
            } else if (_isOverlapR) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_RIGHT_MARKER;
            };
        };
    });

    d3.selectAll("circle[id^=" + INVERTERSYMBOL.ID_CIRCLE_CONNECT + "]").each(function (c) {
        var _isOverlapT = COMMON.intersectRect(_circleTopMaker.node(), this);
        var _isOverlapB = COMMON.intersectRect(_circleBottomMaker.node(), this);
        var _isOverlapL = COMMON.intersectRect(_circleLeftMaker.node(), this);
        var _isOverlapR = COMMON.intersectRect(_circleRightMaker.node(), this);
        if (_isOverlapT || _isOverlapB || _isOverlapL || _isOverlapR) {
            g_connect.id = this.id;
            g_connect.cardId = this.id.split("-").pop();
            g_connect.type = "invertersymbol";
            if (_isOverlapT) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_TOP_MARKER;
            } else if (_isOverlapB) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_BOTTOM_MARKER;
            } else if (_isOverlapL) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_LEFT_MARKER;
            } else if (_isOverlapR) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_RIGHT_MARKER;
            };
        };
    });

    d3.selectAll("circle[id^=" + COMBINER.ID_CIRCLE_CONNECT + "]").each(function (c) {
        var _isOverlapT = COMMON.intersectRect(_circleTopMaker.node(), this);
        var _isOverlapB = COMMON.intersectRect(_circleBottomMaker.node(), this);
        var _isOverlapL = COMMON.intersectRect(_circleLeftMaker.node(), this);
        var _isOverlapR = COMMON.intersectRect(_circleRightMaker.node(), this);
        if (_isOverlapT || _isOverlapB || _isOverlapL || _isOverlapR) {
            g_connect.id = this.id;
            g_connect.cardId = this.id.split("-").pop();
            g_connect.type = "combiner";
            if (_isOverlapT) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_TOP_MARKER;
            } else if (_isOverlapB) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_BOTTOM_MARKER;
            } else if (_isOverlapL) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_LEFT_MARKER;
            } else if (_isOverlapR) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_RIGHT_MARKER;
            };
        };
    });

    d3.selectAll("circle[id^=" + COMBINERSYMBOL.ID_CIRCLE_CONNECT + "]").each(function (c) {
        var _isOverlapT = COMMON.intersectRect(_circleTopMaker.node(), this);
        var _isOverlapB = COMMON.intersectRect(_circleBottomMaker.node(), this);
        var _isOverlapL = COMMON.intersectRect(_circleLeftMaker.node(), this);
        var _isOverlapR = COMMON.intersectRect(_circleRightMaker.node(), this);
        if (_isOverlapT || _isOverlapB || _isOverlapL || _isOverlapR) {
            g_connect.id = this.id;
            g_connect.cardId = this.id.split("-").pop();
            g_connect.type = "combinersymbol";
            if (_isOverlapT) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_TOP_MARKER;
            } else if (_isOverlapB) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_BOTTOM_MARKER;
            } else if (_isOverlapL) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_LEFT_MARKER;
            } else if (_isOverlapR) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_RIGHT_MARKER;
            };
        };
    });

    d3.selectAll("circle[id^=" + PANEL.ID_CIRCLE_CONNECT + "]").each(function (c) {
        var _isOverlapT = COMMON.intersectRect(_circleTopMaker.node(), this);
        var _isOverlapB = COMMON.intersectRect(_circleBottomMaker.node(), this);
        var _isOverlapL = COMMON.intersectRect(_circleLeftMaker.node(), this);
        var _isOverlapR = COMMON.intersectRect(_circleRightMaker.node(), this);
        if (_isOverlapT || _isOverlapB || _isOverlapL || _isOverlapR) {
            g_connect.id = this.id;
            g_connect.cardId = this.id.split("-").pop();
            g_connect.type = "panel";
            if (_isOverlapT) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_TOP_MARKER;
            } else if (_isOverlapB) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_BOTTOM_MARKER;
            } else if (_isOverlapL) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_LEFT_MARKER;
            } else if (_isOverlapR) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_RIGHT_MARKER;
            };
        };
    });

    d3.selectAll("circle[id^=" + PANELSYMBOL.ID_CIRCLE_CONNECT + "]").each(function (c) {
        var _isOverlapT = COMMON.intersectRect(_circleTopMaker.node(), this);
        var _isOverlapB = COMMON.intersectRect(_circleBottomMaker.node(), this);
        var _isOverlapL = COMMON.intersectRect(_circleLeftMaker.node(), this);
        var _isOverlapR = COMMON.intersectRect(_circleRightMaker.node(), this);
        if (_isOverlapT || _isOverlapB || _isOverlapL || _isOverlapR) {
            g_connect.id = this.id;
            g_connect.cardId = this.id.split("-").pop();
            g_connect.type = "panelsymbol";
            if (_isOverlapT) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_TOP_MARKER;
            } else if (_isOverlapB) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_BOTTOM_MARKER;
            } else if (_isOverlapL) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_LEFT_MARKER;
            } else if (_isOverlapR) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_RIGHT_MARKER;
            };
        };
    });

    d3.selectAll("circle[id^=" + STRING.ID_CIRCLE_CONNECT + "]").each(function (c) {
        var _isOverlapT = COMMON.intersectRect(_circleTopMaker.node(), this);
        var _isOverlapB = COMMON.intersectRect(_circleBottomMaker.node(), this);
        var _isOverlapL = COMMON.intersectRect(_circleLeftMaker.node(), this);
        var _isOverlapR = COMMON.intersectRect(_circleRightMaker.node(), this);
        if (_isOverlapT || _isOverlapB || _isOverlapL || _isOverlapR) {
            g_connect.id = this.id;
            g_connect.cardId = this.id.split("-").pop();
            g_connect.type = "string";
            if (_isOverlapT) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_TOP_MARKER;
            } else if (_isOverlapB) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_BOTTOM_MARKER;
            } else if (_isOverlapL) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_LEFT_MARKER;
            } else if (_isOverlapR) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_RIGHT_MARKER;
            };
        };
    });

    d3.selectAll("circle[id^=" + STRINGSYMBOL.ID_CIRCLE_CONNECT + "]").each(function (c) {
        var _isOverlapT = COMMON.intersectRect(_circleTopMaker.node(), this);
        var _isOverlapB = COMMON.intersectRect(_circleBottomMaker.node(), this);
        var _isOverlapL = COMMON.intersectRect(_circleLeftMaker.node(), this);
        var _isOverlapR = COMMON.intersectRect(_circleRightMaker.node(), this);
        if (_isOverlapT || _isOverlapB || _isOverlapL || _isOverlapR) {
            g_connect.id = this.id;
            g_connect.cardId = this.id.split("-").pop();
            g_connect.type = "stringsymbol";
            if (_isOverlapT) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_TOP_MARKER;
            } else if (_isOverlapB) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_BOTTOM_MARKER;
            } else if (_isOverlapL) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_LEFT_MARKER;
            } else if (_isOverlapR) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_RIGHT_MARKER;
            };
        };
    });

    d3.selectAll("circle[id^=" + WEATHER.ID_CIRCLE_CONNECT + "]").each(function (c) {
        var _isOverlapT = COMMON.intersectRect(_circleTopMaker.node(), this);
        var _isOverlapB = COMMON.intersectRect(_circleBottomMaker.node(), this);
        var _isOverlapL = COMMON.intersectRect(_circleLeftMaker.node(), this);
        var _isOverlapR = COMMON.intersectRect(_circleRightMaker.node(), this);
        if (_isOverlapT || _isOverlapB || _isOverlapL || _isOverlapR) {
            g_connect.id = this.id;
            g_connect.cardId = this.id.split("-").pop();
            g_connect.type = "weather";
            if (_isOverlapT) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_TOP_MARKER;
            } else if (_isOverlapB) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_BOTTOM_MARKER;
            } else if (_isOverlapL) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_LEFT_MARKER;
            } else if (_isOverlapR) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_RIGHT_MARKER;
            };
        };
    });

    d3.selectAll("circle[id^=" + WEATHERSYMBOL.ID_CIRCLE_CONNECT + "]").each(function (c) {
        var _isOverlapT = COMMON.intersectRect(_circleTopMaker.node(), this);
        var _isOverlapB = COMMON.intersectRect(_circleBottomMaker.node(), this);
        var _isOverlapL = COMMON.intersectRect(_circleLeftMaker.node(), this);
        var _isOverlapR = COMMON.intersectRect(_circleRightMaker.node(), this);
        if (_isOverlapT || _isOverlapB || _isOverlapL || _isOverlapR) {
            g_connect.id = this.id;
            g_connect.cardId = this.id.split("-").pop();
            g_connect.type = "weathersymbol";
            if (_isOverlapT) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_TOP_MARKER;
            } else if (_isOverlapB) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_BOTTOM_MARKER;
            } else if (_isOverlapL) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_LEFT_MARKER;
            } else if (_isOverlapR) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_RIGHT_MARKER;
            };
        };
    });

    d3.selectAll("circle[id^=" + UPS.ID_CIRCLE_CONNECT + "]").each(function (c) {
        var _isOverlapT = COMMON.intersectRect(_circleTopMaker.node(), this);
        var _isOverlapB = COMMON.intersectRect(_circleBottomMaker.node(), this);
        var _isOverlapL = COMMON.intersectRect(_circleLeftMaker.node(), this);
        var _isOverlapR = COMMON.intersectRect(_circleRightMaker.node(), this);
        if (_isOverlapT || _isOverlapB || _isOverlapL || _isOverlapR) {
            g_connect.id = this.id;
            g_connect.cardId = this.id.split("-").pop();
            g_connect.type = "ups";
            if (_isOverlapT) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_TOP_MARKER;
            } else if (_isOverlapB) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_BOTTOM_MARKER;
            } else if (_isOverlapL) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_LEFT_MARKER;
            } else if (_isOverlapR) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_RIGHT_MARKER;
            };
        };
    });

    d3.selectAll("circle[id^=" + KHOANG1.ID_CIRCLE_CONNECT + "]").each(function (c) {
        var _isOverlapT = COMMON.intersectRect(_circleTopMaker.node(), this);
        var _isOverlapB = COMMON.intersectRect(_circleBottomMaker.node(), this);
        var _isOverlapL = COMMON.intersectRect(_circleLeftMaker.node(), this);
        var _isOverlapR = COMMON.intersectRect(_circleRightMaker.node(), this);
        if (_isOverlapT || _isOverlapB || _isOverlapL || _isOverlapR) {
            g_connect.id = this.id;
            g_connect.cardId = this.id.split("-").pop();
            g_connect.type = "khoang1";
            if (_isOverlapT) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_TOP_MARKER;
            } else if (_isOverlapB) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_BOTTOM_MARKER;
            } else if (_isOverlapL) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_LEFT_MARKER;
            } else if (_isOverlapR) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_RIGHT_MARKER;
            };
        };
    });

    d3.selectAll("circle[id^=" + KHOANG1SYMBOL.ID_CIRCLE_CONNECT + "]").each(function (c) {
        var _isOverlapT = COMMON.intersectRect(_circleTopMaker.node(), this);
        var _isOverlapB = COMMON.intersectRect(_circleBottomMaker.node(), this);
        var _isOverlapL = COMMON.intersectRect(_circleLeftMaker.node(), this);
        var _isOverlapR = COMMON.intersectRect(_circleRightMaker.node(), this);
        if (_isOverlapT || _isOverlapB || _isOverlapL || _isOverlapR) {
            g_connect.id = this.id;
            g_connect.cardId = this.id.split("-").pop();
            g_connect.type = "khoang1symbol";
            if (_isOverlapT) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_TOP_MARKER;
            } else if (_isOverlapB) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_BOTTOM_MARKER;
            } else if (_isOverlapL) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_LEFT_MARKER;
            } else if (_isOverlapR) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_RIGHT_MARKER;
            };
        };
    });

    d3.selectAll("circle[id^=" + KHOANGCAP.ID_CIRCLE_CONNECT + "]").each(function (c) {
        var _isOverlapT = COMMON.intersectRect(_circleTopMaker.node(), this);
        var _isOverlapB = COMMON.intersectRect(_circleBottomMaker.node(), this);
        var _isOverlapL = COMMON.intersectRect(_circleLeftMaker.node(), this);
        var _isOverlapR = COMMON.intersectRect(_circleRightMaker.node(), this);
        if (_isOverlapT || _isOverlapB || _isOverlapL || _isOverlapR) {
            g_connect.id = this.id;
            g_connect.cardId = this.id.split("-").pop();
            g_connect.type = "khoangcap";
            if (_isOverlapT) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_TOP_MARKER;
            } else if (_isOverlapB) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_BOTTOM_MARKER;
            } else if (_isOverlapL) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_LEFT_MARKER;
            } else if (_isOverlapR) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_RIGHT_MARKER;
            };
        };
    });

    d3.selectAll("circle[id^=" + KHOANGCAPSYMBOL.ID_CIRCLE_CONNECT + "]").each(function (c) {
        var _isOverlapT = COMMON.intersectRect(_circleTopMaker.node(), this);
        var _isOverlapB = COMMON.intersectRect(_circleBottomMaker.node(), this);
        var _isOverlapL = COMMON.intersectRect(_circleLeftMaker.node(), this);
        var _isOverlapR = COMMON.intersectRect(_circleRightMaker.node(), this);
        if (_isOverlapT || _isOverlapB || _isOverlapL || _isOverlapR) {
            g_connect.id = this.id;
            g_connect.cardId = this.id.split("-").pop();
            g_connect.type = "khoangcapsymbol";
            if (_isOverlapT) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_TOP_MARKER;
            } else if (_isOverlapB) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_BOTTOM_MARKER;
            } else if (_isOverlapL) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_LEFT_MARKER;
            } else if (_isOverlapR) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_RIGHT_MARKER;
            };
        };
    });

    d3.selectAll("circle[id^=" + KHOANGCHI.ID_CIRCLE_CONNECT + "]").each(function (c) {
        var _isOverlapT = COMMON.intersectRect(_circleTopMaker.node(), this);
        var _isOverlapB = COMMON.intersectRect(_circleBottomMaker.node(), this);
        var _isOverlapL = COMMON.intersectRect(_circleLeftMaker.node(), this);
        var _isOverlapR = COMMON.intersectRect(_circleRightMaker.node(), this);
        if (_isOverlapT || _isOverlapB || _isOverlapL || _isOverlapR) {
            g_connect.id = this.id;
            g_connect.cardId = this.id.split("-").pop();
            g_connect.type = "khoangchi";
            if (_isOverlapT) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_TOP_MARKER;
            } else if (_isOverlapB) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_BOTTOM_MARKER;
            } else if (_isOverlapL) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_LEFT_MARKER;
            } else if (_isOverlapR) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_RIGHT_MARKER;
            };
        };
    });

    d3.selectAll("circle[id^=" + KHOANGDODEM.ID_CIRCLE_CONNECT + "]").each(function (c) {
        var _isOverlapT = COMMON.intersectRect(_circleTopMaker.node(), this);
        var _isOverlapB = COMMON.intersectRect(_circleBottomMaker.node(), this);
        var _isOverlapL = COMMON.intersectRect(_circleLeftMaker.node(), this);
        var _isOverlapR = COMMON.intersectRect(_circleRightMaker.node(), this);
        if (_isOverlapT || _isOverlapB || _isOverlapL || _isOverlapR) {
            g_connect.id = this.id;
            g_connect.cardId = this.id.split("-").pop();
            g_connect.type = "khoangdodem";
            if (_isOverlapT) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_TOP_MARKER;
            } else if (_isOverlapB) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_BOTTOM_MARKER;
            } else if (_isOverlapL) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_LEFT_MARKER;
            } else if (_isOverlapR) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_RIGHT_MARKER;
            };
        };
    });

    d3.selectAll("circle[id^=" + KHOANGDODEMSYMBOL.ID_CIRCLE_CONNECT + "]").each(function (c) {
        var _isOverlapT = COMMON.intersectRect(_circleTopMaker.node(), this);
        var _isOverlapB = COMMON.intersectRect(_circleBottomMaker.node(), this);
        var _isOverlapL = COMMON.intersectRect(_circleLeftMaker.node(), this);
        var _isOverlapR = COMMON.intersectRect(_circleRightMaker.node(), this);
        if (_isOverlapT || _isOverlapB || _isOverlapL || _isOverlapR) {
            g_connect.id = this.id;
            g_connect.cardId = this.id.split("-").pop();
            g_connect.type = "khoangdodemsymbol";
            if (_isOverlapT) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_TOP_MARKER;
            } else if (_isOverlapB) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_BOTTOM_MARKER;
            } else if (_isOverlapL) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_LEFT_MARKER;
            } else if (_isOverlapR) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_RIGHT_MARKER;
            };
        };
    });

    d3.selectAll("circle[id^=" + KHOANGMAYCAT.ID_CIRCLE_CONNECT + "]").each(function (c) {
        var _isOverlapT = COMMON.intersectRect(_circleTopMaker.node(), this);
        var _isOverlapB = COMMON.intersectRect(_circleBottomMaker.node(), this);
        var _isOverlapL = COMMON.intersectRect(_circleLeftMaker.node(), this);
        var _isOverlapR = COMMON.intersectRect(_circleRightMaker.node(), this);
        if (_isOverlapT || _isOverlapB || _isOverlapL || _isOverlapR) {
            g_connect.id = this.id;
            g_connect.cardId = this.id.split("-").pop();
            g_connect.type = "khoangmaycat";
            if (_isOverlapT) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_TOP_MARKER;
            } else if (_isOverlapB) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_BOTTOM_MARKER;
            } else if (_isOverlapL) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_LEFT_MARKER;
            } else if (_isOverlapR) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_RIGHT_MARKER;
            };
        };
    });

    d3.selectAll("circle[id^=" + KHOANGMAYCATSYMBOL.ID_CIRCLE_CONNECT + "]").each(function (c) {
        var _isOverlapT = COMMON.intersectRect(_circleTopMaker.node(), this);
        var _isOverlapB = COMMON.intersectRect(_circleBottomMaker.node(), this);
        var _isOverlapL = COMMON.intersectRect(_circleLeftMaker.node(), this);
        var _isOverlapR = COMMON.intersectRect(_circleRightMaker.node(), this);
        if (_isOverlapT || _isOverlapB || _isOverlapL || _isOverlapR) {
            g_connect.id = this.id;
            g_connect.cardId = this.id.split("-").pop();
            g_connect.type = "khoangmaycatsymbol";
            if (_isOverlapT) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_TOP_MARKER;
            } else if (_isOverlapB) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_BOTTOM_MARKER;
            } else if (_isOverlapL) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_LEFT_MARKER;
            } else if (_isOverlapR) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_RIGHT_MARKER;
            };
        };
    });

    d3.selectAll("circle[id^=" + KHOANGTHANHCAI.ID_CIRCLE_CONNECT + "]").each(function (c) {
        var _isOverlapT = COMMON.intersectRect(_circleTopMaker.node(), this);
        var _isOverlapB = COMMON.intersectRect(_circleBottomMaker.node(), this);
        var _isOverlapL = COMMON.intersectRect(_circleLeftMaker.node(), this);
        var _isOverlapR = COMMON.intersectRect(_circleRightMaker.node(), this);
        if (_isOverlapT || _isOverlapB || _isOverlapL || _isOverlapR) {
            g_connect.id = this.id;
            g_connect.cardId = this.id.split("-").pop();
            g_connect.type = "khoangthanhcai";
            if (_isOverlapT) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_TOP_MARKER;
            } else if (_isOverlapB) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_BOTTOM_MARKER;
            } else if (_isOverlapL) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_LEFT_MARKER;
            } else if (_isOverlapR) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_RIGHT_MARKER;
            };
        };
    });

    d3.selectAll("circle[id^=" + KHOANGTHANHCAISYMBOL.ID_CIRCLE_CONNECT + "]").each(function (c) {
        var _isOverlapT = COMMON.intersectRect(_circleTopMaker.node(), this);
        var _isOverlapB = COMMON.intersectRect(_circleBottomMaker.node(), this);
        var _isOverlapL = COMMON.intersectRect(_circleLeftMaker.node(), this);
        var _isOverlapR = COMMON.intersectRect(_circleRightMaker.node(), this);
        if (_isOverlapT || _isOverlapB || _isOverlapL || _isOverlapR) {
            g_connect.id = this.id;
            g_connect.cardId = this.id.split("-").pop();
            g_connect.type = "khoangthanhcaisymbol";
            if (_isOverlapT) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_TOP_MARKER;
            } else if (_isOverlapB) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_BOTTOM_MARKER;
            } else if (_isOverlapL) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_LEFT_MARKER;
            } else if (_isOverlapR) {
                g_connect.circleFrom = OBJECT.ID_CONNECT_RIGHT_MARKER;
            };
        };
    });

    d3.selectAll("circle[id^=" + PD_HTR02.ID_CIRCLE_CONNECT + "]").each(function (c) {
        if (d.id != c.id) {
            var _isOverlapT = COMMON.intersectRect(_circleTopMaker.node(), this);
            var _isOverlapB = COMMON.intersectRect(_circleBottomMaker.node(), this);
            var _isOverlapL = COMMON.intersectRect(_circleLeftMaker.node(), this);
            var _isOverlapR = COMMON.intersectRect(_circleRightMaker.node(), this);
            if (_isOverlapT || _isOverlapB || _isOverlapL || _isOverlapR) {
                console.log(OBJECT);
                g_connect.id = this.id;
                g_connect.cardId = this.id.split("-").pop();
                g_connect.type = "pd-htr02";
                if (_isOverlapT) {
                    g_connect.circleFrom = OBJECT.ID_CONNECT_TOP_MARKER;
                } else if (_isOverlapB) {
                    g_connect.circleFrom = OBJECT.ID_CONNECT_BOTTOM_MARKER;
                } else if (_isOverlapL) {
                    g_connect.circleFrom = OBJECT.ID_CONNECT_LEFT_MARKER;
                } else if (_isOverlapR) {
                    g_connect.circleFrom = OBJECT.ID_CONNECT_RIGHT_MARKER;
                };
            };
        }
    });



    d3.selectAll("circle[id^=" + PD_AMS01.ID_CIRCLE_CONNECT + "]").each(function (c) {
        if (d.id != c.id) {
            var _isOverlapT = COMMON.intersectRect(_circleTopMaker.node(), this);
            var _isOverlapB = COMMON.intersectRect(_circleBottomMaker.node(), this);
            var _isOverlapL = COMMON.intersectRect(_circleLeftMaker.node(), this);
            var _isOverlapR = COMMON.intersectRect(_circleRightMaker.node(), this);
            if (_isOverlapT || _isOverlapB || _isOverlapL || _isOverlapR) {
                g_connect.id = this.id;
                g_connect.cardId = this.id.split("-").pop();
                g_connect.type = "pd-ams01";
                if (_isOverlapT) {
                    g_connect.circleFrom = OBJECT.ID_CONNECT_TOP_MARKER;
                } else if (_isOverlapB) {
                    g_connect.circleFrom = OBJECT.ID_CONNECT_BOTTOM_MARKER;
                } else if (_isOverlapL) {
                    g_connect.circleFrom = OBJECT.ID_CONNECT_LEFT_MARKER;
                } else if (_isOverlapR) {
                    g_connect.circleFrom = OBJECT.ID_CONNECT_RIGHT_MARKER;
                };
            };
        }
    });


}


