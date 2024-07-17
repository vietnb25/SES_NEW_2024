import CONSTANTS from "../../../category/customer-tool/tool-page/constant";
import * as d3 from "d3";

const $ = window.$;

let FEEDER_LOAD = {
    ID: "gf",
    CIRCLE_X: 20,
    CIRCLE_Y: 30,
    CIRCLE_RADIUS: 20,
    DEFAULT_X: 20,
    DEFAULT_Y: 40,

    drawLoad: function (card) {
        var _dArrow = "";
        var _gPosition = "";
        var _x1;
        var _y1;
        var _x2;
        var _y2;
        var _cx;
        var _cy;
        var _rotate = "";

        switch (Number(card.position)) {
            case 1:
                _dArrow = "M -30 -69 l 20 0 l 0 40 l 15 0 l -24.8 20 l -24.8 -20 l 15 0 l 0 -41";
                _x1 = 20;
                _y1 = 50;
                _x2 = 20;
                _y2 = 70;
                _cx = 20;
                _cy = 70;
                _rotate = "rotate(180)";
                _gPosition = "translate(0,0)";
                break;
            case 2:
                _dArrow = "M 10 -9 l 20 0 l 0 40 l 15 0 l -24.8 20 l -24.8 -20 l 15 0 l 0 -41";
                _x1 = 20;
                _y1 = 90;
                _x2 = 20;
                _y2 = 70;
                _cx = 20;
                _cy = 70;
                _gPosition = "translate(0,80)";
                break;
            case 3:
                _dArrow = "M 20 -59 l 20 0 l 0 40 l 15 0 l -24.8 20 l -24.8 -20 l 15 0 l 0 -41";
                _x1 = 0;
                _y1 = 70;
                _x2 = 20;
                _y2 = 70;
                _cx = 20;
                _cy = 70;
                _rotate = "rotate(90)";
                _gPosition = "translate(-40,40)";
                break;
            case 4:
                _dArrow = "M -40 -19 l 20 0 l 0 40 l 15 0 l -24.8 20 l -24.8 -20 l 15 0 l 0 -41";
                _x1 = 40;
                _y1 = 70;
                _x2 = 20;
                _y2 = 70;
                _cx = 20;
                _cy = 70;
                _rotate = "rotate(270)";
                _gPosition = "translate(40,40)";
                break;
        };

        var g_svg = d3.select("#svg-container");

        var _gCard = g_svg.datum(card).append('g')
            .attr("id", function (d) { return (FEEDER_LOAD.ID + d.id); })
            .attr("transform", function (d) { return 'translate(' + d.x + ',' + d.y + ')'; })
            .datum({
                x: function (d) { return d.x },
                y: function (d) { return d.y }
            })
            .on("click", FEEDER_LOAD.next);

        var _arrow = _gCard.datum(card).append("path")
            .attr("d", _dArrow)
            .attr("fill", "#276cb8")
            .attr("stroke-width", "2")
            .attr("transform", _gPosition + "" + _rotate)
            .attr("stroke", "#276cb8");
    },

    next: function (e, feeder) {
        e.preventDefault();
        e.stopPropagation();
        var _feederConnect = feeder.linkId;

        if (_feederConnect != "" && _feederConnect != null) {
            window.location.href = window.location.href.substring(0, window.location.href.lastIndexOf("/") + 1) + _feederConnect;
        }

    },
}

export default FEEDER_LOAD;