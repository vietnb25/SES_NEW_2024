import COMMON from "./common";
import CONSTANTS from "./constant";
import ARROW from "./arrow";
import METER from "./meter";
import MBA from "./mba";
import RMU from "./rmu";
import FEEDER from "./feeder";
import MC from "./mc";
import MCHOR from "./mchor";
import ULTILITY from "./ultility";
import BUSBAR from "./busbar";
import BUSBARVER from "./busbarver";
import { DATA, g_connect, setAction, outFocus, showProperties } from "./data";
import * as d3 from "d3";
import STMV from "./stmv";
import SGMV from "./sgmv";
import INVERTER from "./inverter";
import INVERTERSYMBOL from "./invertersymbol";
import COMBINER from "./combiner";
import COMBINERSYMBOL from "./combinersymbol";
import PANEL from "./panel";
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
import { data } from "jquery";

const $ = window.$;

let IMAGE = {
    ID: "sysimg-",
    ID_CIRCLE_CONNECT: "pc",
    ID_CONNECT_TOP_MARKER: "pct-",
    ID_CONNECT_RIGHT_MARKER: "pcr-",
    ID_CONNECT_BOTTOM_MARKER: "pcb-",
    ID_CONNECT_LEFT_MARKER: "pcl-",

    ID_TOP_LEFT_RESIZE: "rstl-",
    ID_BOTTOM_LEFT_RESIZE: "rsbl-",
    ID_TOP_RIGHT_RESIZE: "rstr-",
    ID_BOTTOM_RIGHT_RESIZE: "rsbr-",
    ID_ROTATE: "lir-",
    ID_REMOVE: "lrm-",

    ID_IMG: "img-",

    DEFAULT_X: 20,
    DEFAULT_Y: 20,
    DEFAULT_HEIGHT: 50,
    DEFAULT_WIDTH: 100,
    DEFAULT_BORDER_RADIUS: 3,
    directArrow: {},

    collect: function (src, width, height) {

        var image = {
            "id": Date.now(),
            "width": width,
            "height": height,
            "x": IMAGE.DEFAULT_X,
            "y": IMAGE.DEFAULT_Y,
            "ox": IMAGE.DEFAULT_X,
            "oy": IMAGE.DEFAULT_Y,
            "type": "sys-image",
            "src": src
        };
        return image;
    },

    create: function () {
        var _oCard = IMAGE.collect();
        DATA.aCards.push(_oCard);
        IMAGE.draw(_oCard);
    },

    drawLoad: function (card) {
        var g_svg = d3.select("#svg-container");

        var _gCard = g_svg.datum(card).append('g')
            .attr("id", function (d) { return (IMAGE.ID + d.id); })
            .attr("transform", function (d) { return 'translate(' + d.x + ',' + d.y + ')'; })
            .datum({
                x: function (d) { return d.x },
                y: function (d) { return d.y }
            });
        var _rect = _gCard.datum(card).append("image")
            .attr("id", function (d) { return IMAGE.ID_IMG + d.id; })
            .attr("href", function (d) { return d.src; })
            .attr("width", function (d) { return d.width })
            .attr("height", function (d) { return d.height })
            .attr("preserveAspectRatio", "none")

    },

    draw: function (card) {
        var g_svg = d3.select("#svg-container");

        var _gCard = g_svg.datum(card).append('g')
            .attr("id", function (d) { return (IMAGE.ID + d.id); })
            .attr("transform", function (d) { return 'translate(' + d.x + ',' + d.y + ')'; })
            .datum({
                x: function (d) { return d.x },
                y: function (d) { return d.y },
                ox: function (d) { return d.x },
                oy: function (d) { return d.y }
            })
            .on("click", IMAGE.select)
            .call(IMAGE.drag);

        var _rect = _gCard.datum(card).append("image")
            .attr("id", function (d) { return IMAGE.ID_IMG + d.id; })
            .attr("href", function (d) { return d.src; })
            .attr("width", function (d) { return d.width })
            .attr("height", function (d) { return d.height })
            .attr("preserveAspectRatio", "none")

        var topLeftRezie = _gCard.datum(card).append('path')
            .attr("id", function (d) { return IMAGE.ID_TOP_LEFT_RESIZE + d.id; })
            .attr("d", "M0 0 L10 0 L10 5 L5 5 L5 10 L0 10 Z")
            .attr("cursor", "pointer")
            .attr("transform", "translate(-10 , -10)")
            .attr("fill", "#0a1a5c")
            .call(IMAGE.resize);

        var _gX = _gCard.datum(card).append("g")
            .attr("id", function (d) { return (IMAGE.ID_REMOVE + d.id); })
            .attr("transform", "translate(-40 , -50)")
            .attr("opacity", "0")
            .attr("cursor", "pointer")
            .on("click", IMAGE.delete);

        var _lineX1 = _gX.datum(card).append("line")
            .attr("x1", "5")
            .attr("y1", "25")
            .attr("x2", "15")
            .attr("y2", "35")
            .attr("stroke", "#276cb8")
            .attr("style", "stroke-width:4");

        var _lineX2 = _gX.datum(card).append("line")
            .attr("x1", "15")
            .attr("y1", "25")
            .attr("x2", "5")
            .attr("y2", "35")
            .attr("stroke", "#276cb8")
            .attr("style", "stroke-width:4");
    },

    drag: d3.drag()
        .on("start", function (i, d) {
            //console.log("Drag start" + "-" + d.x + "-" + d.y + "-" + d.ox + "-" + d.oy + "-" + d.width + "-" + d.height);
            outFocus();
            setAction("");
            DATA.selectedID = d.id;
            DATA.selectedType = 1;

            IMAGE.focus(DATA.selectedID);

            var _oCard = IMAGE.get(d.id);

            var isMarker = false;

            for (var i = 0; i < DATA.aArrows.length; i++) {
                if (d.id == DATA.aArrows[i].from || d.id == DATA.aArrows[i].to) {
                    isMarker = true;
                    break;
                } else {
                    isMarker = false;
                }
            }

            d3.select(this).classed("active", true);
        })
        .on("drag", function (i, d) {
            var _gCard = d3.select("g[id=" + IMAGE.ID + d.id + "]");

            d3.select(this).attr("transform", "translate(" + (d.x) + "," + (d.y) + ")");
            d.x = i.x;
            d.y = i.y;
            d.ox = i.x;
            d.oy = i.y;
        })
        .on("end", function (i, d) {
            setAction("dragend");
            if (g_connect.attachFlag && g_connect.id != "") {
                var _circleConnect = d3.select("circle[id=" + g_connect.id + "]");
                var _circleFrom = d3.select("circle[id=" + g_connect.circleFrom + d.id + "]");
                var _cardTo = {};

                var xTo = Number(COMMON.getTranslation(_cardTo.attr("transform"))[0]) + Number(_circleConnect.attr("cx")) + Number(COMMON.getTranslation(_circleConnect.attr("transform"))[0]);
                var xFrom = xTo - Number(_circleFrom.attr("cx")) - Number(COMMON.getTranslation(_circleFrom.attr("transform"))[0]);

                var yTo = Number(COMMON.getTranslation(_cardTo.attr("transform"))[1]) + Number(_circleConnect.attr("cy")) + Number(COMMON.getTranslation(_circleConnect.attr("transform"))[1]);
                var yFrom = yTo - Number(_circleFrom.attr("cy")) - Number(COMMON.getTranslation(_circleFrom.attr("transform"))[1]);

                d3.select(this).attr("transform", "translate(" + (d.x = xFrom) + "," + (d.y = yFrom) + ")");

            };
            g_connect.id = "";
            g_connect.connectId = "";
            g_connect.cardId = "";
            g_connect.attachFlag = false;
        }),

    select: function (e, d) {
        e.preventDefault();
        e.stopPropagation();
        outFocus();
        DATA.selectedID = d.id;
        DATA.selectedType = 1;
        if (data.typeId == 2) {
            showProperties(46);
        } else if (data.typeId == 3) {
            showProperties(47);
        } else if (data.typeId == 4) {
            showProperties(48);
        } else if (data.typeId == 5) {
            showProperties(49);
        } else if (data.typeId == 6) {
            showProperties(50);
        } else if (data.typeId == 7) {
            showProperties(51);
        } else if (data.typeId == 8) {
            showProperties(52);
        }
        IMAGE.focus(DATA.selectedID);

        var _oCard = IMAGE.get(d.id);
        var isMarker = false;

        for (var i = 0; i < DATA.aArrows.length; i++) {
            if (d.id == DATA.aArrows[i].from || d.id == DATA.aArrows[i].to) {
                isMarker = true;
                break;
            } else {
                isMarker = false;
            }
        }

        if (isMarker) {
            $('input[name=label_type]').attr("disabled", true);
        } else {

            $('input[name=label_type]').attr('disabled', false);
        }

        $("#btn-update").show();
        $("#btn-remove").show();
    },

    get: function (id) {
        var _oCard;
        $.each(DATA.aCards, function (i, card) {
            if (card.id == id) {
                _oCard = card;
                return false;
            }
        });

        return _oCard;
    },

    remove: function (id) {

        var isMarker = false;

        for (var i = 0; i < DATA.aArrows.length; i++) {
            if (id == DATA.aArrows[i].from || id == DATA.aArrows[i].to) {
                isMarker = true;
                break;
            } else {
                isMarker = false;
            }
        }
        if (!isMarker) {
            d3.select("g[id=" + IMAGE.ID + id + "]").remove();

            DATA.aCards = $.grep(DATA.aCards, function (card) {
                return card.id != id;
            });
        }
    },

    focus: function (id) {
        if (id != null) {
            $('circle[id^=' + IMAGE.ID_CONNECT_TOP_MARKER + id + ']').attr("opacity", 1);
            $('circle[id^=' + IMAGE.ID_CONNECT_BOTTOM_MARKER + id + ']').attr("opacity", 1);
            $('circle[id^=' + IMAGE.ID_CONNECT_LEFT_MARKER + id + ']').attr("opacity", 1);
            $('circle[id^=' + IMAGE.ID_CONNECT_RIGHT_MARKER + id + ']').attr("opacity", 1);
            $('g[id^=' + IMAGE.ID_ROTATE + id + ']').attr("opacity", 1);
            $('g[id^=' + IMAGE.ID_REMOVE + id + ']').attr("opacity", 1);
            $('path[id^=' + IMAGE.ID_TOP_LEFT_RESIZE + id + ']').attr("opacity", 1);
        }
    },

    outFocus: function (id) {
        if (id != null) {
            $('circle[id^=' + IMAGE.ID_CONNECT_TOP_MARKER + id + ']').attr("opacity", 0);
            $('circle[id^=' + IMAGE.ID_CONNECT_BOTTOM_MARKER + id + ']').attr("opacity", 0);
            $('circle[id^=' + IMAGE.ID_CONNECT_LEFT_MARKER + id + ']').attr("opacity", 0);
            $('circle[id^=' + IMAGE.ID_CONNECT_RIGHT_MARKER + id + ']').attr("opacity", 0);
            $('g[id^=' + IMAGE.ID_ROTATE + id + ']').attr("opacity", 0);
            $('g[id^=' + IMAGE.ID_REMOVE + id + ']').attr("opacity", 0);
            $('path[id^=' + IMAGE.ID_TOP_LEFT_RESIZE + id + ']').attr("opacity", 0);
        } else {
            $('circle[id^=' + IMAGE.ID_CONNECT_TOP_MARKER + ']').each(function () {
                $(this).attr("opacity", 0);
            });
            $('circle[id^=' + IMAGE.ID_CONNECT_BOTTOM_MARKER + ']').each(function () {
                $(this).attr("opacity", 0);
            });
            $('circle[id^=' + IMAGE.ID_CONNECT_LEFT_MARKER + ']').each(function () {
                $(this).attr("opacity", 0);
            });
            $('circle[id^=' + IMAGE.ID_CONNECT_RIGHT_MARKER + ']').each(function () {
                $(this).attr("opacity", 0);
            });
            $('path[id^=' + IMAGE.ID_TOP_LEFT_RESIZE + ']').each(function () {
                $(this).attr("opacity", 0);
            });
            $('g[id^=' + IMAGE.ID_REMOVE + ']').each(function () {
                $(this).attr("opacity", 0);
            });
        }
    },

    update: function (id) {

        var _oCurrent = IMAGE.get(id);
        if (typeof _oCurrent !== "undefined" && _oCurrent.type == "image") {
            DATA.aCards.forEach(e => {
                if (e.id == _oCurrent.id) {
                    e.width = _oCurrent.width;
                    e.height = _oCurrent.height;
                    e.x = _oCurrent.x;
                    e.y = _oCurrent.y;
                    e.ox = _oCurrent.x;
                    e.oy = _oCurrent.y;
                    e.type = _oCurrent.type
                }
            });

        }
    },

    delete: function (e) {
        e.stopPropagation();
        IMAGE.remove(DATA.selectedID);
    },

    resize: d3.drag()
        .on("start", function (i, d) {
            outFocus();
            setAction("");
            DATA.selectedID = d.id;
            DATA.selectedType = 1;

            IMAGE.focus(DATA.selectedID);

            var _oCard = IMAGE.get(d.id);
            var isMarker = false;

            for (var i = 0; i < DATA.aArrows.length; i++) {
                if (d.id == DATA.aArrows[i].from || d.id == DATA.aArrows[i].to) {
                    isMarker = true;
                    break;
                } else {
                    isMarker = false;
                }
            }

            if (isMarker) {
                $('input[name=label_type]').attr("disabled", true);
            } else {

                $('input[name=label_type]').attr('disabled', false);
            }

            $("#btn-update").show();
            $("#btn-remove").show();

            d3.select(this).classed("active", true);
        })
        .on("drag", function (i, d) {
            var _g_svg = d3.select("#svg-container");
            var scale = Number(COMMON.getTranslation(_g_svg.attr("transform"))[2]);
            var _x = (i.sourceEvent.clientX - 270 - Number(COMMON.getTranslation(_g_svg.attr("transform"))[0])) / scale;
            var _y = (i.sourceEvent.clientY - 105 - Number(COMMON.getTranslation(_g_svg.attr("transform"))[1])) / scale;

            var _gCard = d3.select("g[id=" + IMAGE.ID + d.id + "]");
            var _img = d3.select("image[id=" + IMAGE.ID_IMG + d.id + "]");

            let height = d.height;
            let width = d.width;
            if ((d.ox <= _x && (d.ox + d.width >= _x))) {
                width = Math.abs(d.width - Math.abs(d.ox - _x));
                _img.attr("width", Math.abs(d.width - Math.abs(d.ox - _x)));
            } else {
                width = Math.abs(d.width + Math.abs(d.ox - _x));
                _img.attr("width", Math.abs(d.width + Math.abs(d.ox - _x)));
            }

            if ((d.oy <= _y && (d.oy + d.height >= _y))) {
                height = Math.abs(d.height - Math.abs(d.oy - _y));
                _img.attr("height", Math.abs(d.height - Math.abs(d.oy - _y)));
            } else {
                height = Math.abs(d.height + Math.abs(d.oy - _y));
                _img.attr("height", Math.abs(d.height + Math.abs(d.oy - _y)));
            }

            d.ox = d.x;
            d.oy = d.y;

            d.x = _x;
            d.y = _y;

            d.height = height;
            d.width = width;

            _gCard.attr("transform", "translate(" + (d.x) + "," + (d.y) + ")");
            var _oCard = {};
            _oCard = {
                "id": d.id,
                "width": width,
                "height": height,
                "x": _x,
                "y": _y,
                "ox": d.ox,
                "oy": d.oy,
                "type": d.type
            }

            // DATA.aCards = $.grep(DATA.aCards, function (card) {
            //     return card.id != d.id;
            // });

            DATA.aCards.forEach(e => {
                if (e.id == d.id) {
                    e.width = width;
                    e.height = height;
                    e.x = _x;
                    e.y = _y;
                    e.ox = d.ox;
                    e.oy = d.oy;
                    e.type = d.type
                }
            });

        })
        .on("end", function (i, d) {
            DATA.aCards.forEach(e => {
                if (e.id == d.id) {
                    e.width = d.width;
                    e.height = d.height;
                    e.x = d.x;
                    e.y = d.y;
                    e.ox = d.x;
                    e.oy = d.y;
                    e.type = d.type
                }
            });
        }),
}

export default IMAGE;