import React from "react";
import { Link } from "react-router-dom";

const AccessDenied = () => {
    return (
        <div className="page-danied" style={{ height: "calc(100vh - 88px)", margin: "0 auto", textAlign: "center", paddingTop: "20px" }}>
            <div className="alert alert-warning alert-dismissible fade show" style={{ width: "calc(100vw - 30px)", margin: "0 auto", marginBottom: "20px" }}>
                <strong>Cảnh báo!</strong> Bạn không thể truy cập vào trang này.
            </div>
            {/* <Link className="btn btn-outline-warning" to={{ pathname: "/" }}>HOME</Link> */}
        </div>
    )
}

export default AccessDenied;