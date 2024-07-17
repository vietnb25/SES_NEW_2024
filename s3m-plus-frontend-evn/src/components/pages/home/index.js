import React, { useEffect, useState } from "react";
import "./index.css";

import ProjectTree from "./project-tree/index";
import ProjectMap from "./project-map";
import { Link, NavLink, Route, Switch, useHistory, useLocation  } from 'react-router-dom';

import HomeService from "../../../services/HomeService";
import { MarkerClusterer } from "@googlemaps/markerclusterer";
import ReactDOMServer from 'react-dom/server';
import Warning from './warning/index';
import Schedule from './schedule/index';
import MonitoringPower from './monitoring-power/index';
import Store from './store/index';
import Chart from './chart/index';
import Download from './download/index';
import $ from "jquery";

const Home = () => {
    const history = useHistory();
    const location = useLocation();

    const [map, setMap] = useState();
    const [activeMenu, setActiveMenu] = useState();

    const [type, setType] = useState(null);

    const [idType, setIdType] = useState(null);

    const getLocations = async () => {
        let res = await HomeService.getProjectLocations();
        return res.data;
    }

    const getPopUp = async (markerId) => {
        let res = await HomeService.getPopUp(markerId);
        return res.data;
    }

    $(document).on("click", "#load-system", (e) => {
        e.preventDefault();
        let url = $("#load-system").attr("href");
        history.push(url)
    });



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

        let locations = await getLocations();

        let clusters = locations.map(location => {
            let id = "";
            let name = ""
            if (location.type === "superManager") {
                id = location.superManagerId;
                name = location.superManagerName;
            } else if (location.type === "manager") {
                id = location.manager;
                name = location.managerName;
            } else if (location.type === "area") {
                id = location.area;
                name = location.areaName;
            }
            let marker = new window.google.maps.Marker({
                id: id,
                map: map,
                animation: window.google.maps.Animation.DROP,
                position: {
                    lat: location.latitude,
                    lng: location.longitude
                },
                icon: {
                    labelOrigin: new window.google.maps.Point(10, 35),
                    url: "/resources/image/map-marker-off.png",
                    origin: new window.google.maps.Point(0, 0),
                },
                label: {
                    text: name,
                    anchor: new window.google.maps.Point(0, 0),
                },
                type: location.type
            });

            // click event
            marker.addListener("click", async () => {
                let content = "";
                let dataResponse;
                console.log(marker);
                if (marker.type === "superManager") {
                    // call api nếu type là superManager.
                    dataResponse = await getPopUp(marker.id)
                    console.log(dataResponse);
                    content = ReactDOMServer.renderToString(
                        <table className="res-table">
                            <thead>
                                <tr>
                                    <th scope="col" style={{ textAlign: "center" }}>Dự án</th>
                                    <th scope="col" style={{ width: "130px", textAlign: "center" }}>Công Suất Đặt</th>
                                    <th scope="col" style={{ width: "150px", textAlign: "center" }}>Công Suất Hiện Tại</th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr>
                                    <td>{marker.label.text}</td>
                                    <td>{dataResponse.congSuatDat}</td>
                                    <td>{dataResponse.sumW ?? "-"}</td>
                                </tr>
                            </tbody>
                        </table>
                    )
                } else if (marker.type === "manager") {
                    // call api nếu type là manager.
                } else if (marker.type === "area") {
                    // call api nếu type là area.
                }
                // set content theo response data trả về.
                //     let content = ReactDOMServer.renderToString(
                //         <div id="project-info-window" className="">
                //             <div>
                //                 <h4>Project ID : {project.projectId}</h4>
                //             </div>
                //         </div>
                //     );
                infoWindow.setContent(content);
                infoWindow.open(map, marker);
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
    const [params, setParams] = useState({
        type: 0,
        superManagerId: '',
        managerId: '',
        areaId: ''
    });

    const handleRedirect = (url) => {
        let urlSearchParam;
        if (new URLSearchParams(location.search).get("superManagerId")) {
            let superManagerId = new URLSearchParams(location.search).get("superManagerId")
            setType("superManagerId");
            setIdType(superManagerId)
            history.push({
                pathname: url,
                search: "?superManagerId=" + superManagerId
            });
        } else if (new URLSearchParams(location.search).get("managerId")) {
            let managerId = new URLSearchParams(location.search).get("managerId")
            setType("managerId");
            setIdType(urlSearchParam)
            history.push({
                pathname: url,
                search: "?managerId=" + managerId
            });
        } else if (new URLSearchParams(location.search).get("areaId")) {
            let areaId = new URLSearchParams(location.search).get("areaId")
            setType("areaId");
            setIdType(urlSearchParam)
            history.push({
                pathname: url,
                search: "?areaId=" + areaId
            });
        } else {
            setType(null)
        }
    }
   

    useEffect(() => {
        document.title = "Home";
        //
        if (location.pathname.includes("/schedule")) {
            setActiveMenu("schedule")
        } else if (location.pathname.includes("/monitoring-power")) {
            setActiveMenu("monitoring-power")
        } else if (location.pathname.includes("/store")) {
            setActiveMenu("store")
        } else {
            setActiveMenu("map")
        }
    }, [location]);

    // const getLinkStore = () => {
    //     const type = params.type;
    //     console.log("typeParam: " ,type);
    //     if (type === 0) {
    //         console.log("parammmm:");
    //         return 'superManagerId=' + params.superManagerId;
    //     }
    //     if (type === '1') {
    //         return 'managerId=' + params.superManagerId;
    //     }
    //     if (type === '2') {
    //         return 'areaId=' + params.areaId;
    //     }
    // }
    // const linkStore = getLinkStore();



    return (
        <div id="page-body">

            <div id="main-content">
                {/* Tree Menu */}
                <ProjectTree location={location}/>

                <div id="project-info">
                    <div className="tab-container">
                        <ul className="menu">
                            <li className={`text-center ${activeMenu === "map" ? "active" : ""}`} onClick={() => setActiveMenu("map")}>
                                <NavLink to={`/`}>&nbsp; <span>Bản đồ</span></NavLink>
                            </li>
                            <li className={`text-center ${activeMenu === "schedule" ? "active" : ""}`}
                                onClick={() => {
                                    setActiveMenu("schedule");
                                    handleRedirect("/schedule");
                                }}>
                                <NavLink to={type ? `/schedule/?${type}=${idType}` : '/schedule'}>&nbsp; <span>Lịch tiết giảm</span></NavLink>
                            </li>
                            <li className={`text-center ${activeMenu === "monitoring-power" ? "active" : ""}`} onClick={() => setActiveMenu("monitoring-power")}>
                                <NavLink to={`/monitoring-power`}>&nbsp; <span>Giám sát công suất</span></NavLink>
                            </li>
                            <li className={`text-center ${activeMenu === "store" ? "active" : ""}`} onClick={() => setActiveMenu("store")}>
                                <NavLink to={`/store`} >&nbsp; <span>Lưu trữ</span></NavLink>
                            </li>
                            <li className={`text-center ${activeMenu === "chart" ? "active" : ""}`} onClick={() => setActiveMenu("chart")}>
                                <NavLink to="/chart">&nbsp; <span>Biểu đồ</span></NavLink>
                            </li>
                            <li className={`text-center ${activeMenu === "warning" ? "active" : ""}`} onClick={() => setActiveMenu("warning")}>
                                <NavLink to="/warning">&nbsp; <span>Cảnh báo</span></NavLink>
                            </li>
                            <li className={`text-center ${activeMenu === "download" ? "active" : ""}`} onClick={() => setActiveMenu("download")} >
                                <NavLink to="/download">&nbsp; <span>Download</span></NavLink>
                            </li>
                        </ul>
                    </div>
                    <Switch>
                        <Route path={"/warning"} component={Warning}></Route>
                        <Route path={"/schedule"} component={Schedule}></Route>
                        <Route path={"/monitoring-power"} component={MonitoringPower}></Route>
                        <Route path={"/store"} component={Store}></Route>
                        <Route path={"/chart"} component={Chart}></Route>
                        <Route path={"/download"} component={Download}></Route>
                        <Route path={"/"} ><ProjectMap initMap={initMap}></ProjectMap></Route>
                    </Switch>
                </div>
            </div>
        </div>
    )
}

export default Home;