import React from "react";
import { useEffect } from "react";
import { useLocation, useHistory } from 'react-router-dom';

const ProjectMap = ({ initMap }) => {
    const location = useLocation();


    useEffect(() => {
        const init = async () => {
            let { map, markers } = await initMap();
            let pathname = window.location.pathname.split("/")
            if (pathname[2] != undefined) {
                let projectId = pathname[2]
                markers.forEach(marker => {
                    if (marker.id === parseInt(projectId)) {
                        map.setZoom(13);
                        map.panTo(marker.getPosition());
                        new window.google.maps.event.trigger(marker, 'click');
                    }
                });
            }
        }
        init();
    }, [location]);

    return (
        <>
            <div id="project-map" style={{ height: "98%", borderRadius: "20px", marginTop:"1%" }}></div>
        </>
    )
}

export default ProjectMap;