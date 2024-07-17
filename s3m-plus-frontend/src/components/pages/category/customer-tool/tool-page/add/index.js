import { useEffect, useState } from 'react';
import { useFormik } from "formik";
import SystemMapService from "../../../../../../services/SystemMapService";
import ProjectService from "../../../../../../services/ProjectService";

import $ from "jquery";
import { useHistory, useParams } from "react-router-dom";

const AddSystemMap = () => {
    const { projectId, systemTypeId, customerId } = useParams();
    const history = useHistory();

    const [currentProject, setCurrentProject] = useState([]);
    const [currentModule, setCurrentModule] = useState("");

    const getCurrentProject = async () => {
        let project = await ProjectService.getProject(projectId);
        setCurrentProject(project.data);
        if (systemTypeId == 1) {
            setCurrentModule("LOAD");
        } else if (systemTypeId == 2) {
            setCurrentModule("SOLAR");
        } else if (systemTypeId == 5) {
            setCurrentModule("GRID");
        }
    };

    const initialValues = {
        name: "",
        projectId: projectId,
        systemTypeId: systemTypeId,
        mainFlag: 0,
        layer: 1
    };

    const formik = useFormik({
        initialValues,
        onSubmit: async (data) => {
            let response = await SystemMapService.addSystemMap(data)
            if (response.status === 200) {
                $.alert("Thêm mới window thành công");
                history.push({
                    pathname: "/category/tool-page/egrid-page/" + customerId + "/" + projectId + "/" + systemTypeId,
                    state: {
                        status: 200,
                        message: "INSERT_SUCCESS"
                    }
                });
            }
        }
    });

    useEffect(() => {
        getCurrentProject();
    }, []);

    return (
        <div id="page-body">
            <div id="main-title">
                <h5 className="d-block mb-0 float-left"><i className="fas fa-user-tie"></i> &nbsp;THÊM MỚI SƠ ĐỒ</h5>
            </div>
            <div id="main-content">
                <form onSubmit={formik.handleSubmit}>
                    <table className="table table-input">
                        <tbody>
                            <tr>
                                <th width="150px">Tên window<span className="required">※</span></th>
                                <td>
                                    <input type="text" className="form-control" name="name" maxLength={100} onChange={formik.handleChange} />
                                </td>
                            </tr>
                            <tr>
                                <th width="150px">Layer<span className="required">※</span></th>
                                <td>
                                    <select name="layer" className="custom-select block" onChange={formik.handleChange}>
                                        <option value="1">1</option>
                                        <option value="2">2</option>
                                        <option value="3">3</option>
                                        <option value="4">4</option>
                                        <option value="5">5</option>
                                    </select>
                                </td>
                            </tr>
                            <tr>
                                <th width="150px">Tên khách hàng</th>
                                <td>
                                    <input type="text" className="form-control" defaultValue={currentProject.customerName} disabled/>
                                </td>
                            </tr>
                            <tr>
                                <th width="150px">Tên dự án</th>
                                <td>
                                    <input type="text" className="form-control" defaultValue={currentProject.projectName} disabled/>
                                </td>
                            </tr>
                            <tr>
                                <th width="150px">Module</th>
                                <td>
                                    <input type="text" className="form-control" defaultValue={currentModule} disabled/>
                                </td>
                            </tr>
                        </tbody>
                    </table>

                    <div id="main-button">
                        <button type="submit" className="btn btn-outline-secondary btn-agree mr-1">
                            <i className="fa-solid fa-check"></i>
                        </button>
                        <button type="button" className="btn btn-outline-secondary btn-cancel" onClick={() => history.goBack()}>
                            <i className="fa-solid fa-xmark"></i>
                        </button>
                    </div>
                </form>
            </div>
        </div>
    )
}

export default AddSystemMap;