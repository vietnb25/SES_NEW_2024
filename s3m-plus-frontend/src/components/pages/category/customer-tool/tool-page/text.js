import COMMON from "./common";
import { DATA, g_connect, setAction, outFocus, showProperties } from "./data";
import * as d3 from "d3";

const $ = window.$;

let TEXT = {
    ID: "idtext-",
    ID_REMOVE: "textrm-",

    collect: function () {
        var _tagName = document.getElementById("text-name").value;
        if (_tagName.length < 1) {
            _tagName = "TEXT";
        }

        var _text = {
            "id": Date.now(),
            "tagName": _tagName,
            "type": "text",
        }
        return _text;
    },

    create: function () {
        var _oCard = TEXT.collect();
        DATA.aCards.push(_oCard);
        TEXT.draw(_oCard);
    },

    drawLoad: function (card) {
        var g_svg = d3.select("#svg-container");

        var _gCard = g_svg.datum(card).append('g')
            .attr("id", function (d) { return (TEXT.ID + d.id); })
            .attr("transform", function (d) { return 'translate(' + d.x + ',' + d.y + ')'; })
            .datum({
                x: function (d) { return d.x },
                y: function (d) { return d.y }
            });

        var _tagName = _gCard.datum(card).append("text")
            .attr("x", "0")
            .attr("y", "0")
            .attr("fill", "black")
            .attr("font-size", "15")
            .attr("cursor", "default")
            .text(function (d) { return d.tagName });
    },

    draw: function (card) {
        var g_svg = d3.select("#svg-container");

        var _gCard = g_svg.datum(card).append('g')
            .attr("id", function (d) { return (TEXT.ID + d.id); })
            .attr("transform", function (d) { return 'translate(' + d.x + ',' + d.y + ')'; })
            .datum({
                x: function (d) { return d.x },
                y: function (d) { return d.y }
            })
            .on("click", TEXT.select)
            .call(TEXT.drag);

        var _tagName = _gCard.datum(card).append("text")
            .attr("x", "0")
            .attr("y", "0")
            .attr("fill", "black")
            .attr("font-size", "15")
            .attr("cursor", "default")
            .text(function (d) { return d.tagName });

        var _gX = _gCard.datum(card).append("g")
            .attr("id", function (d) { return (TEXT.ID_REMOVE + d.id); })
            .attr("transform", "translate(-25,-40)")
            .attr("opacity", "0")
            .attr("cursor", "pointer")
            .on("click", TEXT.delete);

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
            outFocus();
            setAction("");
            DATA.selectedID = d.id;
            DATA.selectedType = 1;

            showProperties(35);
            TEXT.focus(DATA.selectedID);

            var _oCard = TEXT.get(d.id);
            document.getElementById("text-name").value = _oCard.tagName;

            $("#btn-update").show();
            $("#btn-remove").show();

            d3.select(this).classed("active", true);
        })
        .on("drag", function (i, d) {
            var _gCard = d3.select("g[id=" + TEXT.ID + d.id + "]");
            d3.select(this).attr("transform", "translate(" + (d.x) + "," + (d.y) + ")");
            d.x = i.x;
            d.y = i.y;

        })
        .on("end", function (i, d) {
            setAction("dragend");
        }),

    select: function (e, d) {
        e.preventDefault();
        e.stopPropagation();
        outFocus();
        DATA.selectedID = d.id;
        DATA.selectedType = 1;
        showProperties(35);
        TEXT.focus(DATA.selectedID);

        var _oCard = TEXT.get(d.id);
        document.getElementById("text-name").value = _oCard.tagName;

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
        d3.select("g[id=" + TEXT.ID + id + "]").remove();

        DATA.aCards = $.grep(DATA.aCards, function (card) {
            return card.id != id;
        });

        DATA.selectedType = 1;
    },

    focus: function (id) {
        if (id != null) {
            $('g[id^=' + TEXT.ID_REMOVE + id + ']').attr("opacity", 1);
        }
    },

    outFocus: function (id) {
        if (id != null) {
            $('g[id^=' + TEXT.ID_REMOVE + id + ']').attr("opacity", 0);
        } else {
            $('g[id^=' + TEXT.ID_REMOVE + ']').each(function () {
                $(this).attr("opacity", 0);
            });
        }
    },

    update: function (id) {

        var _oCurrent = TEXT.get(id);
        if (typeof _oCurrent !== "undefined" && _oCurrent.type == "text") {
            var _tagName = document.getElementById("text-name").value;
            if (_tagName == "") {
                _tagName = "TEXT";
            }
            var _oCard = {
                "id": _oCurrent.id,
                "tagName": _tagName,
                "x": _oCurrent.x,
                "y": _oCurrent.y,
                "type": "text",
            }

            d3.select("g[id=" + TEXT.ID + id + "]").remove();

            DATA.aCards = $.grep(DATA.aCards, function (card) {
                return card.id != id;
            });

            DATA.aCards.push(_oCard);
            TEXT.draw(_oCard);
        }

    },

    delete: function (e) {
        e.stopPropagation();
        TEXT.remove(DATA.selectedID);
    },
};

export default TEXT;