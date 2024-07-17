import React from "react";
import { useState } from "react";
import { useEffect } from "react";
import { useLocation } from 'react-router-dom';

const ProjectMap = ({ initMap }) => {
    const location = useLocation();

    

    useEffect(() => {
        const init = async () => {
            let { map, markers } = await initMap();
            if (location.search) {
                let param = "";
                if (location.search.includes("superManager")) {
                    param = "superManager";
                }else if(location.search.includes("manager")){
                    param = "manager";
                }else if(location.search.includes("area")){
                    param = "area";
                }
                let id = new URLSearchParams(location.search).get(param);
                markers.forEach(marker => {
                    if (marker.id === parseInt(id)) {
                        map.setZoom(13);
                        map.panTo(marker.getPosition());
                        new window.google.maps.event.trigger(marker, 'click');
                    }
                });
            }
        }
        init();
    }, [location.search]);

    return (
        <div id="project-map" style={{ height: 'calc(100% - 36px)' }}></div>
    )
}

export default ProjectMap;