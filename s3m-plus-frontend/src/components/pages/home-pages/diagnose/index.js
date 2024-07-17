import { useEffect } from "react";

const Diagnose = () => {
    
    useEffect(() => {
        document.title = "Thông tin thiết bị";
    }, []);

    return (
        <div>Thông tin thiết bị</div>
    )
}

export default Diagnose;