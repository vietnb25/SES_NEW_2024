import React, { useEffect, useState } from "react";
import "./index.css";

import ProjectMap from "./project-map";
import OperationInformation from "./operation-info";
import { Route, Switch, useHistory, useLocation } from 'react-router-dom';
import Load from './load/index';
import Solar from "./solar";
import Grid from "./grid/index";

import HomeService from "../../../services/HomeService";
import { MarkerClusterer } from "@googlemaps/markerclusterer";
import ReactDOMServer from 'react-dom/server';
import moment from "moment/moment";
import Customer from "./customer/index";
import SuperManager from "./superManager/index";
import Manager from "./manager/index";
import Area from "./area/index";
import AllCustomer from "./all";
import ChartHome from './chart/index';
import converter from "../../../common/converter";
import { Tree } from 'primereact/tree';
import { Button } from 'primereact/button';
import useAppData from "../../../applications/store/AppStore";
import AuthService from "../../../services/AuthService";
import AccessDenied from "../access-denied/AccessDenied";
import CONS from "../../../constants/constant";

const $ = window.$;

const Home = () => {
    const location = useLocation();

    const history = useHistory();

    const [map, setMap] = useState();
    const [viewTypeValue, setViewTypeValue] = useState(null);
    const [tree, setTree] = useState(useAppData(state => state.userTreeData));
    const [markers, setMarkers] = useState(useAppData(state => state.projectMarkers));
    const [isLoading, setIsLoading] = useState(true);
    const [selectedNodeKey, setSelectedNodeKey] = useState('');
    const [nodeTemp, setNodeTemp] = useState([]);
    const [expandedKeys, setExpandedKeys] = useState({});
    const [systemMapProjectCurrent, setSystemMapProjectCurrent] = useState([]);
    const [userTreeData] = useState(useAppData(state => state.userTreeData));
    const [role] = useState(AuthService.getRoleName());
    const [isExpandAll, setIsExpandAll] = useState(false);

    const saveTreeData = useAppData(state => state.saveTreeData);
    const saveMapData = useAppData(state => state.saveMapData);

    const getLocations = async () => {
        let res = await HomeService.getProjectLocations();
        return res.data;

    }

    $(document).on("click", "#load-system", (e) => {
        e.preventDefault();
        let node = $(".p-highlight").closest("li");
        let element = node.find("span:contains('LOAD')").closest("div");
        $(element).click();
    })

    $(document).on("click", "#pv-system", (e) => {
        e.preventDefault();
        let node = $(".p-highlight").closest("li");
        let element = node.find("span:contains('SOLAR')").closest("div");
        $(element).click();
    });

    $(document).on("click", "#grid-system", (e) => {
        e.preventDefault();
        let node = $(".p-highlight").closest("li");
        let element = node.find("span:contains('GRID')").closest("div");
        $(element).click();
    });

    $(document).on("click", "#wind-system", (e) => {
        e.preventDefault();
        let node = $(".p-highlight").closest("li");
        let element = node.find("span:contains('WIND')").closest("div");
        $(element).click();
    })

    $(document).on("click", "#battery-system", (e) => {
        e.preventDefault();
        let node = $(".p-highlight").closest("li");
        let element = node.find("span:contains('BATTERY')").closest("div");
        $(element).click();
    })

    const handleLoadSystemByUser = () => {
        let systemMaps = [];

        handleData(userTreeData, systemMaps);

        setSystemMapProjectCurrent(systemMaps);
    }

    const handleData = (
        data,
        systemMaps
    ) => {
        console.log(systemMaps);
        data.forEach(item => {
            if (item.type === "load" || item.type === "solar" || item.type === "wind" || item.type === "battery" || item.type === "grid") {
                systemMaps.push(item);
            }
            if (item.children && item.children.length > 0) {
                handleData(item.children,
                    systemMaps);
            }
        });

        return systemMaps;
    }

    const handleSetViewTypeChart = (data) => {
        let values = [];

        if (data.loadPower && data.loadPower > 0) {
            values.push(data.loadPower);
        }
        if (data.pvPower && data.pvPower > 0) {
            values.push(data.pvPower);
        }

        let min = Math.min(...values);

        let typeValue = converter.setViewType(values.length > 0 ? min : 0)

        setViewTypeValue(typeValue);

        return typeValue;
    }


    const ajaxGetListData = React.useRef(null);
    const ajaxGetMeterData = React.useRef(null);

    const initMap = async () => {
        const gMarkers = [];

        let styledMapType = new window.google.maps.StyledMapType(
            [{
                "elementType": "geometry",
                "stylers": [{
                    "color": "#ebe3cd"
                }]
            },
            {
                "elementType": "labels.text.fill",
                "stylers": [{
                    "color": "#523735"
                }]
            },
            {
                "elementType": "labels.text.stroke",
                "stylers": [{
                    "color": "#f5f1e6"
                }]
            },
            {
                "featureType": "administrative",
                "elementType": "geometry",
                "stylers": [{
                    "visibility": "off"
                }]
            },
            {
                "featureType": "administrative",
                "elementType": "geometry.stroke",
                "stylers": [{
                    "color": "#c9b2a6"
                }]
            },
            {
                "featureType": "administrative.land_parcel",
                "elementType": "geometry.stroke",
                "stylers": [{
                    "color": "#dcd2be"
                }]
            },
            {
                "featureType": "administrative.land_parcel",
                "elementType": "labels.text.fill",
                "stylers": [{
                    "color": "#ae9e90"
                }]
            },
            {
                "featureType": "landscape.natural",
                "elementType": "geometry",
                "stylers": [{
                    "color": "#dfd2ae"
                }]
            },
            {
                "featureType": "poi",
                "stylers": [{
                    "visibility": "off"
                }]
            },
            {
                "featureType": "poi",
                "elementType": "geometry",
                "stylers": [{
                    "color": "#dfd2ae"
                }]
            },
            {
                "featureType": "poi",
                "elementType": "labels.text.fill",
                "stylers": [{
                    "color": "#93817c"
                }]
            },
            {
                "featureType": "poi.park",
                "elementType": "geometry.fill",
                "stylers": [{
                    "color": "#a5b076"
                }]
            },
            {
                "featureType": "poi.park",
                "elementType": "labels.text.fill",
                "stylers": [{
                    "color": "#447530"
                }]
            },
            {
                "featureType": "road",
                "elementType": "geometry",
                "stylers": [{
                    "color": "#f5f1e6"
                }]
            },
            {
                "featureType": "road",
                "elementType": "labels.icon",
                "stylers": [{
                    "visibility": "off"
                }]
            },
            {
                "featureType": "road.arterial",
                "elementType": "geometry",
                "stylers": [{
                    "color": "#fdfcf8"
                }]
            },
            {
                "featureType": "road.highway",
                "elementType": "geometry",
                "stylers": [{
                    "color": "#f8c967"
                }]
            },
            {
                "featureType": "road.highway",
                "elementType": "geometry.stroke",
                "stylers": [{
                    "color": "#e9bc62"
                }]
            },
            {
                "featureType": "road.highway.controlled_access",
                "elementType": "geometry",
                "stylers": [{
                    "color": "#e98d58"
                }]
            },
            {
                "featureType": "road.highway.controlled_access",
                "elementType": "geometry.stroke",
                "stylers": [{
                    "color": "#db8555"
                }]
            },
            {
                "featureType": "road.local",
                "elementType": "labels.text.fill",
                "stylers": [{
                    "color": "#806b63"
                }]
            },
            {
                "featureType": "transit",
                "stylers": [{
                    "visibility": "off"
                }]
            },
            {
                "featureType": "transit.line",
                "elementType": "geometry",
                "stylers": [{
                    "color": "#dfd2ae"
                }]
            },
            {
                "featureType": "transit.line",
                "elementType": "labels.text.fill",
                "stylers": [{
                    "color": "#8f7d77"
                }]
            },
            {
                "featureType": "transit.line",
                "elementType": "labels.text.stroke",
                "stylers": [{
                    "color": "#ebe3cd"
                }]
            },
            {
                "featureType": "transit.station",
                "elementType": "geometry",
                "stylers": [{
                    "color": "#dfd2ae"
                }]
            },
            {
                "featureType": "water",
                "elementType": "geometry.fill",
                "stylers": [{
                    "color": "#b9d3c2"
                }]
            },
            {
                "featureType": "water",
                "elementType": "labels.text.fill",
                "stylers": [{
                    "color": "#92998d"
                }]
            }
            ], {
            name: 'Styled Map'
        });

        let mapOptions = {
            zoom: 15,
            center: { lat: 21.0307869, lng: 105.7879486 }
        };

        // Create a map object, and include the MapTypeId to add
        // to the map type control.
        var map = new window.google.maps.Map(document.getElementById('project-map'), mapOptions);
        setMap(map);

        // map.mapTypes.set('styled_map', styledMapType);
        // map.setMapTypeId('styled_map');
        //Associate the styled map with the MapTypeId and set it to display.
        map.mapTypes.set('styled_map', styledMapType);
        map.setMapTypeId('styled_map');

        // Display multiple markers on a map
        let infoWindow = new window.google.maps.InfoWindow();

        let bounds = new window.google.maps.LatLngBounds();

        let locations = (role === "ROLE_ADMIN") || (role === "ROLE_MOD") ? await getLocations() : markers;

        let customerId = new URLSearchParams(location.search).get('customerId')
        let projectId = new URLSearchParams(location.search).get('projectId')
        let ress = await HomeService.getProjectById(customerId, projectId);

        let clusters = locations.map(location => {

            let marker = new window.google.maps.Marker({
                customerId: customerId,
                id: location.projectId,
                map: map,
                animation: window.google.maps.Animation.DROP,
                position: {
                    lat: location.latitude,
                    lng: location.longitude
                },
                icon: {
                    labelOrigin: new window.google.maps.Point(10, 35),
                    url: "/resources/image/map-marker-" + location.statusColor + ".png",
                    origin: new window.google.maps.Point(0, 0),
                },
                label: {
                    text: location.projectName,
                    anchor: new window.google.maps.Point(0, 0),
                },
            });

            let systemMapProjectCurrents = [];

            systemMapProjectCurrents = handleData(userTreeData, systemMapProjectCurrents);
            // console.log(systemMapProjectCurrents);

            // click event
            marker.addListener("click", async () => {
                let res = await HomeService.getProjectById(marker.customerId, marker.id);
                if (res.status === 200) {
                    let project = res.data;
                    let hasLoad = systemMapProjectCurrents.some(s => (s.data.projectId === project.projectId && s.data.systemTypeId === 1));
                    let hasPv = systemMapProjectCurrents.some(s => (s.data.projectId === project.projectId && s.data.systemTypeId === 2));
                    let hasWind = systemMapProjectCurrents.some(s => (s.data.projectId === project.projectId && s.data.systemTypeId === 3));
                    let hasBattery = systemMapProjectCurrents.some(s => (s.data.projectId === project.projectId && s.data.systemTypeId === 4));
                    let hasGrid = systemMapProjectCurrents.some(s => (s.data.projectId === project.projectId && s.data.systemTypeId === 5));
                    let typeValue = handleSetViewTypeChart(project);
                    let content = ReactDOMServer.renderToString(
                        <div id="project-info-window" className="">
                            <div style={{ border: '1px solid #B3B3B3', borderRadius: '5px' }}>
                                <div className="system-project-title">
                                    <h4>{project.projectName}</h4>
                                    <span><i className="fa-regular fa-clock"></i>&nbsp; {project.currentTime ? moment(project.currentTime).format(CONS.DATE_FORMAT_OPERATE) : ""}</span>
                                </div>

                                <div id="project-overview">
                                    <div id="system-power">
                                        <div>{project.pvPower !== null && project.pvPower >= -20000000 && project.pvPower <= 20000000 ? project.pvPower.toFixed(2) : "-"} {converter.convertLabelElectricPower(typeValue, "W")}</div>
                                        <div>{project.windPower !== null && project.windPower >= -2000000 && project.windPower <= 2000000 ? project.windPower.toFixed(2) : "-"} {converter.convertLabelElectricPower(typeValue, "W")}</div>
                                        <div>{project.batteryPower !== null && project.batteryPower >= -2000000 && project.batteryPower <= 2000000 ? project.batteryPower.toFixed(2) : "-"} {converter.convertLabelElectricPower(typeValue, "W")}</div>
                                        <div>{project.gridPower !== null && project.gridPower >= -60000000 && project.gridPower <= 60000000 ? project.gridPower.toFixed(2) : "-"} {converter.convertLabelElectricPower(typeValue, "W")}</div>
                                    </div>

                                    <div id="system-icon">
                                        {
                                            role === "ROLE_ADMIN" ?
                                                <div className={`${project.pvStatus}`}>
                                                    {
                                                        project.pvStatus !== "inactive" ?
                                                            <a id="pv-system" href={"/home/solar/" + project.customerId + "/" + project.projectId}><img src="/resources/image/system-icon/system-solar.png" alt="Solar System" /></a>
                                                            :
                                                            <a id="pv-system" href="/home/" style={{ pointerEvents: "none", cursor: "default" }}><img src="/resources/image/system-icon/system-solar.png" alt="Solar System" /></a>
                                                    }
                                                </div> :
                                                <div className={`${project.pvStatus}`}>
                                                    {
                                                        project.pvStatus !== "inactive" ?
                                                            <a id="pv-system"
                                                                style={!hasPv ? { pointerEvents: "none", cursor: "default" } : {}}
                                                                href={hasPv ? "/home/solar/" + project.customerId + "/" + project.projectId : "#"}><img src="/resources/image/system-icon/system-solar.png" alt="Solar System" /></a>
                                                            :
                                                            <a id="pv-system"
                                                                style={{ pointerEvents: "none", cursor: "default" }}
                                                                href="/home/"><img src="/resources/image/system-icon/system-solar.png" alt="Solar System" /></a>
                                                    }
                                                </div>
                                        }

                                        {
                                            role === "ROLE_ADMIN" ?
                                                <div className={`${project.windStatus}`}>
                                                    {
                                                        project.windStatus !== "inactive" ?
                                                            <a id="wind-system" href={"/home/load/" + project.customerId + "/" + project.projectId}><img src="/resources/image/system-icon/system-wind.png" alt="Wind System" /></a> :
                                                            <a id="wind-system" href="/home/" style={{ pointerEvents: "none", cursor: "default" }}><img src="/resources/image/system-icon/system-wind.png" alt="Wind System" /></a>
                                                    }

                                                </div> :
                                                <div className={`${project.windStatus}`}>
                                                    {
                                                        project.windStatus !== "inactive" ?
                                                            <a id="wind-system"
                                                                style={!hasWind ? { pointerEvents: "none", cursor: "default" } : {}}
                                                                href={hasWind ? "/home/load/" + project.customerId + "/" + project.projectId : "#"}><img src="/resources/image/system-icon/system-wind.png" alt="Wind System" /></a> :
                                                            <a id="wind-system"
                                                                style={{ pointerEvents: "none", cursor: "default" }}
                                                                href={"/home/"}><img src="/resources/image/system-icon/system-wind.png" alt="Wind System" /></a>
                                                    }
                                                </div>
                                        }

                                        {
                                            role === "ROLE_ADMIN" ?
                                                <div className={`${project.batteryStatus}`}>
                                                    {
                                                        project.batteryStatus !== "inactive" ?
                                                            <a id="battery-system" href={"/home/load/" + project.customerId + "/" + project.projectId}><img src="/resources/image/system-icon/system-ev.png" alt="EV System" /></a> :
                                                            <a id="battery-system" href="/home/" style={{ pointerEvents: "none", cursor: "default" }}><img src="/resources/image/system-icon/system-ev.png" alt="EV System" /></a>
                                                    }

                                                </div> :
                                                <div className={`${project.batteryStatus}`}>
                                                    {
                                                        project.batteryStatus !== "inactive" ?
                                                            <a id="battery-system"
                                                                style={!hasBattery ? { pointerEvents: "none", cursor: "default" } : {}}
                                                                href={hasBattery ? "/home/load/" + project.customerId + "/" + project.projectId : "#"}><img src="/resources/image/system-icon/system-ev.png" alt="EV System" /></a> :
                                                            <a id="battery-system"
                                                                style={{ pointerEvents: "none", cursor: "default" }}
                                                                href={"/home/"}><img src="/resources/image/system-icon/system-ev.png" alt="EV System" /></a>
                                                    }
                                                </div>
                                        }

                                        {
                                            role === "ROLE_ADMIN" ?
                                                <div className={`${project.gridStatus}`}>
                                                    {
                                                        project.gridStatus !== "inactive" ?
                                                            <a id="grid-system" href={"/home/grid/" + project.customerId + "/" + project.projectId}><img src="/resources/image/system-icon/system-utility.png" alt="Utility System" /></a>
                                                            :
                                                            <a id="grid-system" href="/home/" style={{ pointerEvents: "none", cursor: "default" }}><img src="/resources/image/system-icon/system-utility.png" alt="Utility System" /></a>
                                                    }
                                                </div> :
                                                <div className={`${project.gridStatus}`}>
                                                    {
                                                        project.gridStatus !== "inactive" ?
                                                            <a id="grid-system"
                                                                style={!hasGrid ? { pointerEvents: "none", cursor: "default" } : {}}
                                                                href={hasGrid ? "/home/grid/" + project.customerId + "/" + project.projectId : "#"}><img src="/resources/image/system-icon/system-utility.png" alt="Utility System" /></a>
                                                            :
                                                            <a id="grid-system"
                                                                style={{ pointerEvents: "none", cursor: "default" }}
                                                                href="/home/"><img src="/resources/image/system-icon/system-solar.png" alt="Grid System" /></a>
                                                    }
                                                    {/* <a id="grid-system" 
                                                    style={!hasPv ? {pointerEvents: "none", cursor: "default"} : {}}
                                                    href={hasPv ? "/solar/" + project.customerId + "/" + project.projectId : "#"}><img src="/resources/image/system-icon/system-utility.png" alt="Utility System" /></a> */}
                                                </div>
                                        }
                                    </div>

                                    <div id="system-arrow">
                                        <div>
                                            {
                                                (project.pvPower === 0 || !project.pvPower) && <img src="/resources/image/system-icon/system-line.png" alt="system-load-line" />
                                            }
                                            {
                                                project.pvPower < 0 && <img src="/resources/image/system-icon/system-line-up.png" alt="system-load-line" />
                                            }
                                            {
                                                project.pvPower > 0 && <img src="/resources/image/system-icon/system-line-down.png" alt="system-load-line" />
                                            }
                                        </div>
                                        <div>
                                            {
                                                (project.windPower === 0 || !project.windPower) && <img src="/resources/image/system-icon/system-line.png" alt="system-load-line" />
                                            }
                                            {
                                                project.windPower < 0 && <img src="/resources/image/system-icon/system-line-up.png" alt="system-load-line" />
                                            }
                                            {
                                                project.windPower > 0 && <img src="/resources/image/system-icon/system-line-down.png" alt="system-load-line" />
                                            }
                                        </div>
                                        <div>
                                            {
                                                (project.batteryPower === 0 || !project.batteryPower) && <img src="/resources/image/system-icon/system-line.png" alt="system-load-line" />
                                            }
                                            {
                                                project.batteryPower < 0 && <img src="/resources/image/system-icon/system-line-up.png" alt="system-load-line" />
                                            }
                                            {
                                                project.batteryPower > 0 && <img src="/resources/image/system-icon/system-line-down.png" alt="system-load-line" />
                                            }
                                        </div>
                                        <div>
                                            {
                                                (project.gridPower === 0 || !project.gridPower) && <img src="/resources/image/system-icon/system-line.png" alt="system-load-line" />
                                            }
                                            {
                                                project.gridPower < 0 && <img src="/resources/image/system-icon/system-line-up.png" alt="system-load-line" />
                                            }
                                            {
                                                project.gridPower > 0 && <img src="/resources/image/system-icon/system-line-down.png" alt="system-load-line" />
                                            }
                                        </div>
                                    </div>

                                    <div id="system-arrow-load">
                                        {
                                            (project.loadPower === 0 || !project.loadPower) && <img src="/resources/image/system-icon/system-line-load.png" alt="system-load-line" />
                                        }
                                        {
                                            project.loadPower < 0 && <img src="/resources/image/system-icon/system-line-load-up.png" alt="system-load-line" />
                                        }
                                        {
                                            project.loadPower > 0 && <img src="/resources/image/system-icon/system-line-load-down.png" alt="system-load-line" />
                                        }
                                    </div>

                                    {
                                        role === "ROLE_ADMIN" &&
                                        <div id="system-load">
                                            <div className={`${project.loadStatus}`}>
                                                {
                                                    project.loadStatus !== "inactive" ?
                                                        <a id="load-system" href={"/home/load/" + project.customerId + "/" + project.projectId}><img src="/resources/image/system-icon/system-load.png" alt="Load System" /></a> :
                                                        <a id="load-system" href="/home/" style={{ pointerEvents: "none", cursor: "default" }}><img src="/resources/image/system-icon/system-load.png" alt="Load System" /></a>
                                                }

                                            </div>
                                        </div>
                                    }

                                    {
                                        role !== "ROLE_ADMIN" &&
                                        <div id="system-load">
                                            <div className={`${project.loadStatus}`}>
                                                {
                                                    project.loadStatus !== "inactive" ?
                                                        <a id="load-system"
                                                            style={!hasLoad ? { pointerEvents: "none", cursor: "default" } : {}}
                                                            href={hasLoad ? "/home/load/" + project.customerId + "/" + project.projectId : "#"}><img src="/resources/image/system-icon/system-load.png" alt="Load System" /></a> :
                                                        <a id="load-system"
                                                            style={{ pointerEvents: "none", cursor: "default" }}
                                                            href={"/home/"}><img src="/resources/image/system-icon/system-load.png" alt="Load System" /></a>
                                                }
                                            </div>
                                        </div>
                                    }

                                    <div id="system-power-load">
                                        <div>{project.loadPower !== null && project.loadPower >= -2000000 && project.loadPower <= 2000000 ? project.loadPower.toFixed(2) : "-"} {converter.convertLabelElectricPower(typeValue, "W")}</div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    );
                    infoWindow.setContent(content);
                    infoWindow.open(map, marker);
                }
            });

            bounds.extend(marker.position);

            gMarkers.push(marker);

            return marker;
        });


        // Add a marker clusterer to manage the markers.
        let markerCluster = new MarkerClusterer(map, gMarkers);

        // Automatically center the map fitting all markers on the screen
        map.fitBounds(bounds);

        map.setZoom(8);
        map.panTo(new window.google.maps.LatLng(21.0307869, 105.7879486));

        return {
            map: map,
            markers: gMarkers
        }
    }

    const initHomeMonitorData = async () => {
        let role = await AuthService.getRoleName();
        if ((role === "ROLE_ADMIN" || role === "ROLE_MOD") && (tree.length === 0 || markers.length === 0)) {
            let res = await HomeService.initHomeMonitorData();
            console.log(res);
            if (res.status === 200) {
                let treeData = res.data.tree;
                sortSystemMaps(treeData);
                setTree(res.data.tree);
                setNodeTemp(res.data.tree);
                setMarkers(res.data.markers);
                if (res.data.tree.length > 0) {
                    expandAll(res.data.tree);
                }
                saveTreeData(res.data.tree);
                saveMapData(res.data.markers);
            }
        } else {
            expandAll(tree);
            sortSystemMaps(tree);
        }
        setIsLoading(false)
    }

    const sortSystemMaps = (treeData) => {
        treeData.forEach(item => {
            if (item.type === "project") {
                item.children.sort(function (a, b) {
                    return a.data.piority - b.data.piority
                });
                return;
            }

            if (item.children && item.children.length > 0) {
                sortSystemMaps(item.children);
            }
        })
    }

    const onSelectNode = (event) => {
        let node = event.node;
        let typeNode = node.type;
        if (typeNode && typeNode === "load") {
            let customerId = node.data.customerId;
            let projectId = node.data.projectId;
            let pathname = window.location.pathname;
            let arrPathName = pathname.split("/");

            let url = "/";

            arrPathName[3] = customerId;
            arrPathName[4] = projectId;

            for (let index = 0; index < arrPathName.length; index++) {
                if (arrPathName[index] !== "") {
                    if (index === 6) {
                        arrPathName[index] = "0";
                    }
                    url += arrPathName[index] + "/";
                }
                else {
                    arrPathName[2] = "load";
                    url += arrPathName[index];
                }
            }

            history.push(url);
        } else if (typeNode && typeNode === "project") {
            let projectId = node.data.projectId;
            let customerId = node.data.customerId;
            history.push({
                pathname: "/home/",
                search: `?customerId=${customerId}&projectId=${projectId}`
            });
        } else if (typeNode && typeNode === "customer" && node.key === "All-C") {
            history.push("/home/all");
        } else if (typeNode && typeNode === "customer") {
            let customerId = node.data.customerId;
            history.push(`/home/customer/${customerId}`);
        } else if (typeNode && typeNode === "superManager") {
            let customerId = node.data.customerId;
            let superManagerId = node.data.superManagerId;
            history.push(`/home/superManager/${customerId}/${superManagerId}`);
        } else if (typeNode && typeNode === "manager") {
            let customerId = node.data.customerId;
            let superManagerId = node.data.superManagerId;
            let managerId = node.data.managerId;
            history.push(`/home/manager/${customerId}/${superManagerId}/${managerId}`);
        } else if (typeNode && typeNode === "area") {
            let customerId = node.data.customerId;
            let superManagerId = node.data.superManagerId;
            let managerId = node.data.managerId;
            let areaId = node.data.areaId;
            history.push(`/home/area/${customerId}/${superManagerId}/${managerId}/${areaId}`);
        } else if (typeNode && typeNode === "solar") {
            let customerId = node.data.customerId;
            let projectId = node.data.projectId;
            let pathname = window.location.pathname;
            let arrPathName = pathname.split("/");
            let url = "/";

            arrPathName[3] = customerId;
            arrPathName[4] = projectId;

            for (let index = 0; index < arrPathName.length; index++) {
                if (arrPathName[index] !== "") {
                    if (index === 6) {
                        arrPathName[index] = "0";
                    }
                    url += arrPathName[index] + "/";
                }
                else {
                    arrPathName[2] = "solar";
                    url += arrPathName[index];
                }
            }

            history.push(url);
        } else if (typeNode && typeNode === "grid") {
            let customerId = node.data.customerId;
            let projectId = node.data.projectId;
            let pathname = window.location.pathname;
            let arrPathName = pathname.split("/");
            let url = "/";

            arrPathName[3] = customerId;
            arrPathName[4] = projectId;

            for (let index = 0; index < arrPathName.length; index++) {
                if (arrPathName[index] !== "") {
                    if (index === 6) {
                        arrPathName[index] = "0";
                    }
                    url += arrPathName[index] + "/";
                }
                else {
                    arrPathName[2] = "grid";
                    url += arrPathName[index];
                }
            }

            history.push(url);
        } else if (typeNode && typeNode === "wind") {
            let customerId = node.data.customerId;
            let projectId = node.data.projectId;
            let pathname = window.location.pathname;
            let arrPathName = pathname.split("/");

            let url = "/";

            arrPathName[3] = customerId;
            arrPathName[4] = projectId;

            for (let index = 0; index < arrPathName.length; index++) {
                if (arrPathName[index] !== "") {
                    if (index === 6) {
                        arrPathName[index] = "0";
                    }
                    url += arrPathName[index] + "/";
                }
                else {
                    arrPathName[2] = "load";
                    url += arrPathName[index];
                }
            }

            history.push(url);
        } else if (typeNode && typeNode === "battery") {
            let customerId = node.data.customerId;
            let projectId = node.data.projectId;
            let pathname = window.location.pathname;
            let arrPathName = pathname.split("/");

            let url = "/";

            arrPathName[3] = customerId;
            arrPathName[4] = projectId;

            for (let index = 0; index < arrPathName.length; index++) {
                if (arrPathName[index] !== "") {
                    if (index === 6) {
                        arrPathName[index] = "0";
                    }
                    url += arrPathName[index] + "/";
                }
                else {
                    arrPathName[2] = "load";
                    url += arrPathName[index];
                }
            }

            history.push(url);
        }


    }

    const expandNode = (node, _expandedKeys) => {
        if (node.children && node.children.length) {
            _expandedKeys[node.key] = true;

            for (let child of node.children) {
                expandNode(child, _expandedKeys);
            }
        }
    };

    const expandAll = (data) => {
        let _expandedKeys = {};

        for (let node of data) {
            expandNode(node, _expandedKeys);
        }

        setExpandedKeys(_expandedKeys);

        setIsExpandAll(true);
    };

    const handleExpand = () => {
        if (isExpandAll) {
            setExpandedKeys({});
            setIsExpandAll(false);

            $(".p-tree-container").css("overflow", "hidden");
        } else {
            setIsExpandAll(true);
            expandAll(tree);
            $(".p-tree-container").css("overflow", "scroll");
        }
    }

    const nodeTemplate = (node, options) => {
        if (node.type === "customer") {
            let element = $(".tree-customer").closest("div");
            $(element).css("border", "1px solid #dbdad5");
            $(element).css("margin-bottom", "3px");
        }

        return <span className={options.className}>{node.label}</span>
    }

    useEffect(() => {
        initHomeMonitorData();
        handleLoadSystemByUser();
        document.title = "Home";
    }, []);

    return (
        <div id="page-body">

            <div id="main-content">
                <div id="project-list">
                    <div id="project-tree" style={{ height: "100%" }}>
                        {
                            isLoading ?
                                <div className="d-flex justify-content-center">
                                    <div className="spinner-border spinner-border-sm" role="status">
                                        <span className="sr-only">Loading...</span>
                                    </div>
                                </div> :
                                <>
                                    <Tree value={tree}
                                        style={tree?.length >= 25 ? { overflow: 'auto' } : { fontSize: 13, border: 'none', height: "calc(100% - 26px)", paddingBottom: "20px" }}
                                        selectionMode="single"
                                        selectionKeys={selectedNodeKey}
                                        onSelectionChange={(e) => setSelectedNodeKey(e.value)}
                                        onNodeClick={e => {
                                            onSelectNode(e);
                                        }}
                                        filter
                                        filterMode="lenient"
                                        filterPlaceholder="Tìm kiếm .........."
                                        expandedKeys={expandedKeys}
                                        onToggle={(e) => setExpandedKeys(e.value)}
                                        nodeTemplate={nodeTemplate}
                                    />
                                    <div className="w-100 mb-2">
                                        <Button label={isExpandAll ? "-" : "+"} onClick={handleExpand} className="w-100 btn-expand" style={{ backgroundColor: "#FFA87D", borderColor: "#FFA87D", fontSize: 20 }} />
                                    </div>
                                </>
                        }
                    </div>
                </div>

                <div id="project-info" className="tab-project-info">
                    <Switch>
                        <Route path={"/home/load/:customerId/:projectId"}><Load ajaxGetListData={ajaxGetListData} ajaxGetMeterData={ajaxGetMeterData}></Load></Route>
                        <Route path={"/home/solar/:customerId/:projectId"}><Solar ajaxGetListData={ajaxGetListData} ajaxGetMeterData={ajaxGetMeterData}></Solar></Route>
                        <Route path={"/home/grid/:customerId/:projectId"}><Grid ajaxGetListData={ajaxGetListData} ajaxGetMeterData={ajaxGetMeterData}></Grid></Route>
                        <Route path={"/home/load/:projectId"}><Load></Load></Route>
                        <Route path={"/home/pv/:projectId"}><Solar></Solar></Route>
                        <Route path={"/home/all"}><AllCustomer></AllCustomer></Route>
                        <Route path={"/home/customer/:customerId"}><Customer></Customer></Route>
                        <Route path={"/home/superManager/:customerId/:superManagerId"}><SuperManager></SuperManager></Route>
                        <Route path={"/home/manager/:customerId/:superManagerId/:managerId"}><Manager></Manager></Route>
                        <Route path={"/home/area/:customerId/:superManagerId/:managerId/:areaId"}><Area></Area></Route>
                        <Route path={"/home/operation/:projectId/:deviceId"} ><OperationInformation /></Route>
                        <Route path={"/home/chart/:customerId/:projectId"}>
                            <ChartHome />
                        </Route>
                        <Route path={"/home/"} ><ProjectMap initMap={initMap}></ProjectMap></Route>
                        <Route path={"*"} ><AccessDenied /></Route>
                    </Switch>
                </div>
            </div>

        </div>
    )
}

export default Home;