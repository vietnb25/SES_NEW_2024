import moment from 'moment';
import { useState } from 'react';
//import './index.css';
import { useEffect } from 'react';
import { Link, useHistory, useParams } from 'react-router-dom';
import ProjectService from '../../../../../services/ProjectService';
import CONS from '../../../../../constants/constant';
import { t } from 'i18next';

const ProjectTool = () => {
    const [projects, setProjects] = useState([]);
    const [projectNameSearch, setProjectName] = useState("");
    const history = useHistory();

    const { customerId } = useParams();

    const getProjectList = async () => {
        var projectsResponse = await ProjectService.getProjectByCustomerId(customerId);
        setProjects(projectsResponse.data);
    }

    const handleSearch = async () => {
        var response = await ProjectService.getListProjectByCustomerIdAndCustomerName(customerId, projectNameSearch);
        setProjects(response.data);
    }

    function handleBack() {
        history.push({
            pathname: "/category/tool-page/"
        });
    }

    useEffect(() => {
        getProjectList();
    }, [])

    return (
        <div id="page-body">
            <div id="main-title">
                <h5 className="d-block mb-0 float-left"><i className="fas fa-diagram-project"></i> &nbsp;{t('content.category.tool_page.list_project_diagram')}</h5>
            </div>

            <div id="main-search">
                <div className="input-group search-item mb-3 float-left w-minus-300px">
                    <div className="input-group-prepend">
                        <span className="input-group-text" id="inputGroup-sizing-default">Tên Dự Án</span>
                    </div>
                    <input type="text" className="form-control" onChange={e => setProjectName(e.target.value)}></input>
                </div>
                <div className="search-buttons float-left">
                    <button type="button" className="btn btn-outline-secondary" onClick={() => handleSearch}><i className="fa-solid fa-magnifying-glass"></i></button>
                </div>
            </div>

            <div id="main-content">
                <table className="table">
                    <thead>
                        <tr>
                            <th width="40px">{t('content.no')}</th>
                            <th>{t('content.category.project.project_name')}</th>
                            <th width="150px">{t('content.super_manager')}</th>
                            <th width="150px">{t('content.manager')}</th>
                            <th width="200px">{t('content.area')}</th>
                            <th width="100px">Mặt bằng</th>
                            <th width="250px">{t('content.system')}</th>
                            <th width="145px">{t('content.update_date')}</th>
                        </tr>
                    </thead>
                    <tbody>
                        {
                            projects?.map(
                                (project, index) => <tr key={project.projectId}>
                                    <td className="text-center">{index + 1}</td>
                                    <td style={{ wordWrap: "break-word" }}>{project.projectName}</td>
                                    <td style={{ wordWrap: "break-word" }}>{project.superManagerName}</td>
                                    <td style={{ wordWrap: "break-word" }}>{project.managerName}</td>
                                    <td style={{ wordWrap: "break-word" }}>{project.areaName}</td>
                                    <td className="text-center" style={{ wordWrap: "break-word" }}>
                                        <img src={project.solarNum > 0 ? '/resources/image/system/icon-floor-active.png' : '/resources/image/system/icon-floor-inactive.png'}
                                            style={{ height: 40, marginRight: 3 }}></img>
                                    </td>
                                    <td className="text-center" style={{ wordWrap: "break-word" }}>
                                        <Link to={`/category/tool-page/egrid-page/${customerId}/${project.projectId}/2`}>
                                            <img src={project.solarNum > 0 ? '/resources/image/system/icon-solar-active.png' : '/resources/image/system/icon-solar-inactive.png'}
                                                style={{ height: 40, marginRight: 3 }}></img>
                                        </Link>
                                        <Link to={`/category/tool-page/egrid-page/${customerId}/${project.projectId}/3`}>
                                            <img src={project.windNum > 0 ? '/resources/image/system/icon-wind-active.png' : '/resources/image/system/icon-wind-inactive.png'}
                                                style={{ height: 40, marginRight: 3 }}></img>
                                        </Link>
                                        <Link to={`/category/tool-page/egrid-page/${customerId}/${project.projectId}/4`}>
                                            <img src={project.evNum > 0 ? '/resources/image/system/icon-battery-active.png' : '/resources/image/system/icon-battery-inactive.png'}
                                                style={{ height: 40, marginRight: 3 }}></img>
                                        </Link>
                                        <Link to={`/category/tool-page/egrid-page/${customerId}/${project.projectId}/5`}>
                                            <img src={project.utilityNum > 0 ? '/resources/image/system/icon-grid-active.png' : '/resources/image/system/icon-grid-inactive.png'}
                                                style={{ height: 40, marginRight: 3 }}></img>
                                        </Link>
                                        <Link to={`/category/tool-page/egrid-page/${customerId}/${project.projectId}/1`}>
                                            <img src={project.loadNum > 0 ? '/resources/image/system/icon-load-active.png' : '/resources/image/system/icon-load-inactive.png'}
                                                style={{ height: 40 }}></img>
                                        </Link>
                                    </td>
                                    <td className="text-center">{moment(project.updateDate).format(CONS.DATE_FORMAT)}</td>
                                </tr>
                            )
                        }
                        <tr style={{ display: projects.length === 0 ? "contents" : "none" }}>
                            <td className="text-center" colSpan={7}>{t('content.no_data')}</td>
                        </tr>

                    </tbody>

                </table>
                <div id="main-button" className="text-left">
                    <button type="button" className="btn btn-outline-secondary btn-s3m w-120px" onClick={() => handleBack()}>
                        <i className="fa-solid fa-house"></i> &nbsp;{t('content.back')}
                    </button>
                </div>
            </div>
        </div>
    )
}

export default ProjectTool;