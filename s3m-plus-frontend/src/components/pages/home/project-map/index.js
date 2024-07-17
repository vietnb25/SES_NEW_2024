import React from "react";
import { useEffect } from "react";
import { useLocation, useHistory } from 'react-router-dom';

const ProjectMap = ({ initMap }) => {
    const location = useLocation();
    const history = useHistory();

    const handleRedirect = (type) => {
        let projectId = new URLSearchParams(location.search).get("projectId");
        let customerId = new URLSearchParams(location.search).get("customerId");
        if (type === 1) {
            return
        }
        if (projectId && type === 2) {
            history.push(`/chart/${customerId}/${projectId}`);
        }
    }

    useEffect(() => {
        const init = async () => {
            let { map, markers } = await initMap();
            
            if (location.search) {
                let projectId = new URLSearchParams(location.search).get("projectId");
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
    }, [location.search]);

    return (
        <>
            <div className="select-tab" style={{position:"absolute", top: 0, zIndex: 1}}>
                <button onClick={() => handleRedirect(1)} className={`btn ${location.pathname === "/" && 'selected'}`} style={{backgroundColor: "#fff", color: "black", width: 85, height: 30, borderRadius: 0, padding: 0}}>Bản đồ</button>
                {
                    new URLSearchParams(location.search).get("projectId") &&
                    <button onClick={() => handleRedirect(2)} className={`btn ${location.pathname !== "/" && 'selected'}`} style={{backgroundColor: "#fff", color: "black", width: 85, height: 30, borderRadius: 0, padding: 0}}>Biểu đồ</button>
                }
            </div>
            <div id="project-map" style={{ height: "100%" }}></div>
        </>
    )
}

export default ProjectMap;