import React, { useEffect, useState } from "react";
import { Link, useHistory, useLocation } from "react-router-dom";
import CustomerService from "../../../../services/CustomerService";
import SuperManagerService from "../../../../services/SuperManagerService";
import ManagerService from "../../../../services/ManagerService";
import AreaService from "../../../../services/AreaService";
import ProjectService from "../../../../services/ProjectService";
import SystemMapService from "../../../../services/SystemMapService";

import { Fieldset } from 'primereact/fieldset';
import { Checkbox } from 'primereact/checkbox';
import { Dialog } from 'primereact/dialog';
import { ProgressSpinner } from 'primereact/progressspinner';

import PermissionService from "../../../../services/PermissionService";
import AuthService from "../../../../services/AuthService";
import useAppStore from "../../../../applications/store/AppStore";


const PermissionSystemData = ({ userId }) => {
    const location = useLocation();
    // state
    const [customers, setCustomers] = useState([]);
    const [superManagers, setSuperManagers] = useState([]);
    const [managers, setManagers] = useState([]);
    const [areas, setAreas] = useState([]);
    const [projects, setProjects] = useState([]);
    const [systemMaps, setSystemMaps] = useState([]);
    const [visibleCustomer, setVisibleCustomer] = useState(false);
    const [visible, setVisible] = useState(false);
    const [visibleLoad, setVisibleLoad] = useState(false);
    const [successDialog, setSuccessDialog] = useState(false);
    const [role] = useState(AuthService.getRoleName());
    const appUserData = useAppStore(state => state.appUserData);
    const userTreeData = useAppStore(state => state.userTreeData);

    // data permission
    const [customersSelected, setCustomersSelected] = useState([]);
    const [superManagersSelected, setSuperManagersSelected] = useState([]);
    const [managersSelected, setManagersSelected] = useState([]);
    const [areasSelected, setAreasSelected] = useState([]);
    const [projectsSelected, setProjectsSelected] = useState([]);
    const [systemMapsSelected, setSystemMapsSelected] = useState([]);

    const loadData = async () => {
        setVisibleLoad(true);
        let resCustomers = await CustomerService.getListCustomer();
        let resSuperManagers = await SuperManagerService.listSuperManager();
        let resManagers = await ManagerService.listManager();
        let resAreas = await AreaService.listArea();
        let resProjects = await ProjectService.listProject();
        let resSystemMaps = await SystemMapService.getAll();

        setCustomers(resCustomers.status === 200 ? resCustomers.data : []);
        setSuperManagers(resSuperManagers.status === 200 ? resSuperManagers.data : []);
        setManagers(resManagers.status === 200 ? resManagers.data : []);
        setAreas(resAreas.status === 200 ? resAreas.data.areas : []);
        setProjects(resProjects.status === 200 ? resProjects.data : []);
        setSystemMaps(resSystemMaps.status === 200 ? resSystemMaps.data : []);

        if (appUserData.authorized === 1 && role !== "ROLE_ADMIN") {
            let customersData = [];
            let superManagersData = [];
            let managersData = [];
            let areasData = [];
            let projectsData = [];
            let systemMapsData = [];

            handleData(resCustomers.data,
                resSuperManagers.data,
                resManagers.data,
                resAreas.data.areas,
                resProjects.data,
                resSystemMaps.data,
                userTreeData ? userTreeData : [],
                customersData,
                superManagersData,
                managersData,
                areasData,
                projectsData,
                systemMapsData);

            setCustomers(customersData);
            setSuperManagers(superManagersData);
            setManagers(managersData);
            setAreas(areasData);
            setProjects(projectsData);
            setSystemMaps(systemMapsData);
        }

        let currentDataPermission = await PermissionService.getPermissionByUserId(userId);

        let _data = JSON.parse(currentDataPermission.data.treeData && currentDataPermission.data.treeData.content);

        let customerTemp = [];
        let superManagersTemp = [];
        let managersTemp = [];
        let areasTemp = [];
        let projectsTemp = [];
        let systemMapsTemp = [];

        handleData(resCustomers.data,
            resSuperManagers.data,
            resManagers.data,
            resAreas.data.areas,
            resProjects.data,
            resSystemMaps.data,
            _data ? _data : [],
            customerTemp,
            superManagersTemp,
            managersTemp,
            areasTemp,
            projectsTemp,
            systemMapsTemp);

        handleUpdateState(customerTemp,
            superManagersTemp,
            managersTemp,
            areasTemp,
            projectsTemp,
            systemMapsTemp);

        setVisibleLoad(false);
    }

    const handleData = (
        customers,
        superManagers,
        managers,
        areas,
        projects,
        systemMaps,
        data,
        customerTemp,
        superManagersTemp,
        managersTemp,
        areasTemp,
        projectsTemp,
        systemMapsTemp
    ) => {
        data.map((item, i) => {
            
            if (item.type === "customer") {
                let customer = customers.find(c => c.customerId === item.data.customerId);
                (customer && !customerTemp.some(c => c.customerId === customer.customerId)) && customerTemp.push(customer)
            } else if (item.type === "superManager") {
                let sm = superManagers.find(s => s.superManagerId === item.data.superManagerId);
                (sm && !superManagersTemp.some(s => s.superManagerId === sm.superManagerId)) && superManagersTemp.push(sm);
            } else if (item.type === "manager") {
                let manager = managers.find(m => m.managerId === item.data.managerId);
                (manager && !managersTemp.some(m => m.managerId === manager.managerId)) && managersTemp.push(manager);
            } else if (item.type === "area") {
                let area = areas.find(a => a.areaId === item.data.areaId);
                (area && !areasTemp.some(a => a.areaId === area.areaId)) && areasTemp.push(area);
            } else if (item.type === "project") {
                let project = projects.find(p => p.projectId === item.data.projectId);
                (project && !projectsTemp.some(p => p.projectId === project.projectId)) && projectsTemp.push(project);
            } else {
                // console.log(item[i]);
                let systemMap = systemMaps.find(s => s.id === item.data.id);
                (systemMap && !systemMapsTemp.some(s => s.id === systemMap.id)) && systemMapsTemp.push(systemMap);
            }

            if (item.children && item.children.length > 0) {
                handleData(customers, superManagers, managers, areas, projects, systemMaps, item.children,
                    customerTemp,
                    superManagersTemp,
                    managersTemp,
                    areasTemp,
                    projectsTemp,
                    systemMapsTemp);
            }
        });
    }

    const handleUpdateState = (customerTemp, superManagers, managers, areas, projects, systemMaps) => {
        setCustomersSelected(customerTemp);
        setSuperManagersSelected(superManagers);
        setManagersSelected(managers);
        setAreasSelected(areas);
        setProjectsSelected(projects);
        setSystemMapsSelected(systemMaps);
    }

    const handleSelectAllCustomer = (e) => {
        if (e.checked) {
            setCustomersSelected(customers);
        } else {
            setVisibleCustomer(true);
        }
    }

    const handleSelectAllSuperManagers = (e) => {
        if (e.checked) {
            setSuperManagersSelected(superManagers);
        } else {
            setSuperManagersSelected([]);
            setManagersSelected([]);
            setAreasSelected([]);
            setProjectsSelected([]);
            setSystemMapsSelected([]);
        }
    }

    const handleSelectAllManagers = (e) => {
        if (e.checked) {
            setManagersSelected(managers);
        } else {
            setManagersSelected([]);
            setAreasSelected([]);
            setProjectsSelected([]);
            setSystemMapsSelected([]);
        }
    }

    const handleSelectAllArea = (e) => {
        if (e.checked) {
            setAreasSelected(areas);
        } else {
            setAreasSelected([]);
            setProjectsSelected([]);
            setSystemMapsSelected([]);
        }
    }

    const handleSelectAllProject = (e) => {
        if (e.checked) {
            setProjectsSelected(projects);
        } else {
            setProjectsSelected([]);
            setSystemMapsSelected([]);
        }
    }

    const handleSelectAllSystem = (e) => {
        if (e.checked) {
            setSystemMapsSelected(systemMaps);
        } else {
            setSystemMapsSelected([]);
        }
    }

    const handleSelectCustomer = (e) => {
        let customersSelect = [...customersSelected];
        if (e.checked) {
            customersSelect.push(e.value);
        } else {
            customersSelect = customersSelect.filter(c => c.customerId !== e.value.customerId);
        }
        setCustomersSelected(customersSelect);
    }

    const handleSelectSuperManager = (e) => {
        let superManagersSelect = [...superManagersSelected];
        if (e.checked) {
            superManagersSelect.push(e.value);
        } else {
            let smId = e.value.superManagerId;
            superManagersSelect = superManagersSelect.filter(sm => sm.superManagerId !== smId);

            let managerSelected = [...managersSelected];

            managerSelected.forEach(m => {
                let managerId = m.managerId;

                let areasSelect = [...areasSelected];

                areasSelect.forEach(a => {
                    let areaId = a.areaId;
                    let _projectsSelect = [...projectsSelected];

                    _projectsSelect.forEach(p => {
                        let projectId = p.projectId;

                        let systemMapSelect = [...systemMapsSelected];

                        systemMapSelect = systemMapSelect.filter(s => s.projectId !== projectId);

                        setSystemMapsSelected(systemMapSelect);
                    });

                    _projectsSelect = _projectsSelect.filter(p => p.areaId !== areaId);

                    setProjectsSelected(_projectsSelect);
                })

                areasSelect = areasSelect.filter(a => a.managerId !== managerId);

                setAreasSelected(areasSelect);
            });

            managerSelected = managersSelected.filter(m => m.superManagerId !== smId);

            setManagersSelected(managerSelected);
        }
        setSuperManagersSelected(superManagersSelect);
    }

    const handleSelectManager = (e) => {
        let managersSelect = [...managersSelected];
        if (e.checked) {
            managersSelect.push(e.value);
        } else {
            managersSelect.forEach(m => {
                let managerId = m.managerId;

                let areasSelect = [...areasSelected];

                areasSelect.forEach(a => {
                    let areaId = a.areaId;
                    let _projectsSelect = [...projectsSelected];

                    _projectsSelect.forEach(p => {
                        let projectId = p.projectId;

                        let systemMapSelect = [...systemMapsSelected];

                        systemMapSelect = systemMapSelect.filter(s => s.projectId !== projectId);

                        setSystemMapsSelected(systemMapSelect);
                    });

                    _projectsSelect = _projectsSelect.filter(p => p.areaId !== areaId);

                    setProjectsSelected(_projectsSelect);
                })

                areasSelect = areasSelect.filter(a => a.managerId !== managerId);

                setAreasSelected(areasSelect);
            });

            managersSelect = managersSelect.filter(m => m.managerId !== e.value.managerId);
        }
        setManagersSelected(managersSelect);
    }

    const handleSelectArea = (e) => {
        let areasSelect = [...areasSelected];
        if (e.checked) {
            areasSelect.push(e.value);
        } else {
            areasSelect.forEach(a => {
                let areaId = a.areaId;
                let _projectsSelect = [...projectsSelected];

                _projectsSelect.forEach(p => {
                    let projectId = p.projectId;

                    let systemMapSelect = [...systemMapsSelected];

                    systemMapSelect = systemMapSelect.filter(s => s.projectId !== projectId);

                    setSystemMapsSelected(systemMapSelect);
                });

                _projectsSelect = _projectsSelect.filter(p => p.areaId !== areaId);

                setProjectsSelected(_projectsSelect);
            });

            areasSelect = areasSelect.filter(a => a.areaId !== e.value.areaId);
        }
        setAreasSelected(areasSelect);
    }

    const handleSelectProject = (e) => {
        let projectsSelect = [...projectsSelected];
        if (e.checked) {
            projectsSelect.push(e.value);
        } else {
            projectsSelect.forEach(p => {
                let projectId = p.projectId;

                let systemMapSelect = [...systemMapsSelected];

                systemMapSelect = systemMapSelect.filter(s => s.projectId !== projectId);

                setSystemMapsSelected(systemMapSelect);
            });

            projectsSelect = projectsSelect.filter(p => p.projectId !== e.value.projectId);
        }
        setProjectsSelected(projectsSelect);
    }

    const handleSelectSystemMap = (e) => {
        let systemMapSelect = [...systemMapsSelected];
        if (e.checked) {
            systemMapSelect.push(e.value);
        } else {
            systemMapSelect = systemMapSelect.filter(s => s.id !== e.value.id);
        }
        setSystemMapsSelected(systemMapSelect);
    }

    const handleSave = async () => {
        setVisibleLoad(true);
        let dataTree = [];
        let markers = [];

        let treeDataAll = {
            key: "All-C",
            type: "customer",
            icon: "tree-customer",
            label: "All",
            data: {
                position: -1
            }
        }
        dataTree.push(treeDataAll);
        customersSelected.forEach((customer, i) => {
            let treeDataCustomer = {
                key: "C-" + customer.customerId,
                type: "customer",
                icon: "tree-customer",
                label: customer.customerName,
                data: {
                    position: i,
                    customerId: customer.customerId
                },
                children: []
            }

            superManagersSelected.forEach((sm, j) => {
                let treeDataSuperManager = {
                    key: "S-" + sm.superManagerId + "-" + i,
                    type: "superManager",
                    icon: "tree-superManager",
                    label: sm.superManagerName,
                    data: {
                        position: j,
                        customerId: customer.customerId,
                        superManagerId: sm.superManagerId
                    },
                    children: []
                }


                managersSelected.forEach((manager, k) => {
                    if (manager.superManagerId === sm.superManagerId) {
                        let treeManagerData = {
                            key: "M-" + manager.managerId + "-" + i,
                            type: "manager",
                            icon: "tree-manager",
                            label: manager.managerName,
                            data: {
                                position: k,
                                customerId: customer.customerId,
                                superManagerId: sm.superManagerId,
                                managerId: manager.managerId
                            },
                            children: []
                        }

                        areasSelected.forEach((area, m) => {
                            if (area.managerId === manager.managerId) {
                                let treeAreaData = {
                                    key: "A-" + area.areaId + "-" + i,
                                    type: "area",
                                    icon: "tree-area",
                                    label: area.areaName,
                                    data: {
                                        position: m,
                                        customerId: customer.customerId,
                                        superManagerId: sm.superManagerId,
                                        managerId: manager.managerId,
                                        areaId: area.areaId
                                    },
                                    children: []
                                }

                                projectsSelected.forEach((project, n) => {
                                    if (project.customerId === customer.customerId) {
                                        if (project.areaId === area.areaId) {
                                            let projectTreeData = {
                                                key: "P-" + project.projectId + "-" + m + "-" + i,
                                                type: "project",
                                                icon: "tree-project",
                                                label: project.projectName,
                                                data: {
                                                    position: n,
                                                    customerId: customer.customerId,
                                                    superManagerId: sm.superManagerId,
                                                    managerId: manager.managerId,
                                                    areaId: area.areaId,
                                                    projectId: project.projectId
                                                },
                                                children: []
                                            }

                                            systemMapsSelected.forEach((systemMap, g) => {
                                                if (systemMap.projectId === project.projectId) {
                                                    let systemMapData = {
                                                        key: `${project.projectId}-S-${g}-${n}-${m}-${i}`,
                                                        type: "",
                                                        icon: "",
                                                        label: "",
                                                        data: {
                                                            id: systemMap.id,
                                                            position: g,
                                                            customerId: customer.customerId,
                                                            superManagerId: sm.superManagerId,
                                                            managerId: manager.managerId,
                                                            areaId: area.areaId,
                                                            projectId: project.projectId,
                                                            systemTypeId: systemMap.systemTypeId,
                                                            piority: 0
                                                        }
                                                    }
                                                    let type = "";
                                                    let typeName = "";
                                                    let icon = "";
                                                    let piority = 0;
                                                    switch (systemMap.systemTypeId) {
                                                        case 1:
                                                            typeName = "LOAD";
                                                            type = "load";
                                                            icon = "tree-load";
                                                            piority = 1;
                                                            break;
                                                        case 2:
                                                            typeName = "SOLAR";
                                                            type = "solar";
                                                            icon = "tree-solar";
                                                            piority = 2;
                                                            break;
                                                        case 3:
                                                            typeName = "WIND";
                                                            type = "wind";
                                                            icon = "tree-wind";
                                                            piority = 3;
                                                            break;
                                                        case 4:
                                                            typeName = "BATTERY";
                                                            type = "battery";
                                                            icon = "tree-battery";
                                                            piority = 4;
                                                            break;
                                                        case 5:
                                                            typeName = "GRID";
                                                            type = "grid";
                                                            icon = "tree-grid";
                                                            piority = 5;
                                                            break;
                                                        default:
                                                            break;
                                                    }
                                                    systemMapData.icon = icon;
                                                    systemMapData.label = typeName;
                                                    systemMapData.type = type;
                                                    systemMapData.data.piority = piority;

                                                    
                                                    projectTreeData.children.push(systemMapData);                                                                                                 
                                                }
                                            });
                                            const dataProject = Object.values(projectTreeData.children.reduce((acc,cur)=>Object.assign(acc,{[cur.label]:cur}),{}))
                                            const convertData = Object.assign({}, dataProject);
                                            // console.log(convertData);
                                            treeAreaData.children.push(convertData);
                                        }
                                    }
                                });
                                if (treeAreaData.children.length > 0) {
                                    treeManagerData.children.push(treeAreaData);
                                }

                            }
                        });
                        if (treeManagerData.children.length > 0) {
                            treeDataSuperManager.children.push(treeManagerData);
                        }

                    }
                });
                if (treeDataSuperManager.children.length > 0) {
                    treeDataCustomer.children.push(treeDataSuperManager);
                }

            });
            if (treeDataCustomer.children.length > 0) {
                dataTree.push(treeDataCustomer);
            }
        });

        handleGetProjectMarkers(dataTree, markers);

        let customerIds = [];

        customersSelected.forEach(customer => {
            customerIds.push(customer.customerId);
        })

        let data = {
            dataTree: JSON.stringify(dataTree),
            dataMarkers: JSON.stringify(markers),
            userId: userId
        }

        if (role === "ROLE_ADMIN") {
            data.customerIds = customerIds.toString()
        }

        console.log(data.customerIds);
        if (data.customerIds == "") {
            setVisibleLoad(false);
            setVisibleCustomer(true);
        } else {
            let res = await PermissionService.add(data);

            setVisibleLoad(false);

            if (res.status === 200) {
                setSuccessDialog(true);
            } else {
                setVisible(true);
            }
        }

    }


    const handleGetProjectMarkers = (data, markes) => {
        data.forEach(item => {
            if (item.type === "project") {
                let project = projects.find(p => p.projectId === item.data.projectId);
                let { projectId, projectName, latitude, longitude } = { ...project };
                markes.push({
                    projectId,
                    projectName,
                    latitude,
                    longitude
                });
            }
            if (item.children && item.children.length > 0) {
                handleGetProjectMarkers(item.children, markes);
            }
        });
    }

    useEffect(() => {
        loadData();
    }, []);

    return (
        <div className="tab-container">
            <ul className="menu">
                <li>
                    <Link to={`/`} onClick={e => e.preventDefault()}><i className="fas fa-user-tie"></i>&nbsp; <span>Phân quyền</span></Link>
                </li>
                <li style={{ float: "right", textAlign: "right" }}>
                    <button onClick={() => loadData()} type="button" className="btn btn-warning mr-2" style={{ padding: "3px 10px" }}><i className="fa fa-refresh"></i> Reset</button>
                    <button type="button" className="btn btn-warning" onClick={handleSave} style={{ padding: "3px 10px", marginRight: 10 }}><i className="fa fa-save"></i> Lưu</button>
                </li>
            </ul>
            <div className="" style={{ padding: 10, height: 810, overflow: "scroll" }}>
                <div className="card" style={{ border: "none" }}>
                    <Fieldset legend="Khách hàng" toggleable>
                        <div className="m-0 pb-2" >
                            <Checkbox value={"ALL-CUSTOMER"} onChange={handleSelectAllCustomer} checked={customers.length === customersSelected.length} />
                            <label className="ml-2 mr-3 mb-0">Tất cả</label>
                        </div>
                        <div className="m-0 d-flex" style={{ flexWrap: "wrap" }}>
                            {
                                customers.map((customer, i) => {
                                    return (
                                        <div key={i} className="">
                                            <Checkbox checked={customersSelected.some(c => c.customerId === customer.customerId)} inputId={customer.customerId} value={customer} onChange={handleSelectCustomer} />
                                            <label htmlFor={customer.customerId} className="ml-2 mr-3 mb-0">{customer.customerName}</label>
                                        </div>
                                    )
                                })
                            }
                        </div>
                    </Fieldset>
                </div>
                {
                    customersSelected.length > 0 && (
                        <div className="card mt-2" style={{ border: "none" }}>
                            <Fieldset legend="Khu vực/miền" toggleable>
                                <div className="m-0 pb-2" >
                                    <Checkbox value={"ALL-SUPERMANAGER"} onChange={handleSelectAllSuperManagers} checked={superManagersSelected.length === superManagers.length} />
                                    <label className="ml-2 mr-3 mb-0">Tất cả</label>
                                </div>
                                <div className="m-0 d-flex" style={{ flexWrap: "wrap" }}>
                                    {
                                        superManagers.map((superManager, i) => {
                                            return (
                                                <div key={i} className="">
                                                    <Checkbox checked={superManagersSelected.some(sm => sm.superManagerId === superManager.superManagerId)} inputId={superManager.superManagerId} value={superManager} onChange={handleSelectSuperManager} />
                                                    <label htmlFor={superManager.superManagerId} className="ml-2 mr-3 mb-0">{superManager.superManagerName}</label>
                                                </div>
                                            )
                                        })
                                    }
                                </div>
                            </Fieldset>
                        </div>
                    )
                }
                {
                    (customersSelected.length > 0 && superManagersSelected.length > 0) &&
                    <div className="card mt-2" style={{ border: "none" }}>
                        <Fieldset legend="Tỉnh thành" toggleable>
                            <div className="m-0 pb-2" >
                                <Checkbox value={"ALL-MANAGER"} onChange={handleSelectAllManagers} checked={managers.length === managersSelected.length} />
                                <label className="ml-2 mr-3 mb-0">Tất cả</label>
                            </div>
                            <div className="m-0 d-flex" style={{ flexWrap: "wrap" }}>
                                {
                                    managers.map((manager, i) => {
                                        let superManagerId = manager.superManagerId;
                                        return (superManagersSelected.some(sm => sm.superManagerId === superManagerId) &&
                                            <div key={i} className="" style={{ width: 200 }}>
                                                <Checkbox checked={managersSelected.some(m => m.managerId === manager.managerId)} inputId={manager.managerId} value={manager} onChange={handleSelectManager} />
                                                <label htmlFor={manager.managerId} className="ml-2 mr-3 mb-0">{manager.managerName}</label>
                                            </div>
                                        )
                                    })
                                }
                            </div>
                        </Fieldset>
                    </div>
                }
                {
                    (customersSelected.length > 0 && superManagersSelected.length > 0 && managersSelected.length > 0) && (
                        <div className="card mt-2" style={{ border: "none" }}>
                            <Fieldset legend="Quận huyện" toggleable>
                                <div className="m-0 pb-2">
                                    <Checkbox value={"ALL-AREA"} onChange={handleSelectAllArea} checked={areas.length === areasSelected.length} />
                                    <label className="ml-2 mr-3 mb-0">Tất cả</label>
                                </div>
                                <div className="m-0 d-flex" style={{ flexWrap: "wrap" }}>
                                    {
                                        areas.map((area, i) => {
                                            let managerId = area.managerId;
                                            return (managersSelected.some(m => m.managerId === managerId) &&
                                                <div key={i} className="" style={{ width: 200 }}>
                                                    <Checkbox checked={areasSelected.some(a => a.areaId === area.areaId)} inputId={area.areaId} value={area} onChange={handleSelectArea} />
                                                    <label htmlFor={area.areaId} className="ml-2 mr-3 mb-0">{area.areaName}</label>
                                                </div>
                                            )
                                        })
                                    }
                                </div>
                            </Fieldset>
                        </div>
                    )
                }
                {
                    (customersSelected.length > 0 && superManagersSelected.length > 0 && managersSelected.length > 0 && areasSelected.length > 0) && (
                        <div className="card mt-2" style={{ border: "none" }}>
                            <Fieldset legend="Dự án" toggleable>
                                <div className="m-0 pb-2" >
                                    <Checkbox value={"ALL-PROJECT"} onChange={handleSelectAllProject} checked={projects.length === projectsSelected.length} />
                                    <label className="ml-2 mr-3 mb-0">Tất cả</label>
                                </div>
                                <div className="m-0 d-flex" style={{ flexWrap: "wrap" }}>
                                    {
                                        projects.map((project, i) => {
                                            let areaId = project.areaId;
                                            return ((areasSelected.some(a => a.areaId === areaId) && customersSelected.some(c => c.customerId === project.customerId)) &&
                                                <div key={i} className="" style={{ width: 200 }}>
                                                    <Checkbox checked={projectsSelected.some(p => p.projectId === project.projectId)} inputId={project.projectId} value={project} onChange={handleSelectProject} />
                                                    <label htmlFor={project.projectId} className="ml-2 mr-3 mb-0">{project.projectName}</label>
                                                </div>
                                            )
                                        })
                                    }
                                </div>
                            </Fieldset>
                        </div>
                    )
                }
                {
                    (customersSelected.length > 0
                        && superManagersSelected.length > 0
                        && managersSelected.length > 0
                        && areasSelected.length > 0
                        && projectsSelected.length > 0) && (
                        <div className="card mt-2" style={{ border: "none" }}>
                            <Fieldset legend="Hệ thống" toggleable>
                                <div className="m-0 pb-2" >
                                    <Checkbox value={"ALL-SYSTEM"} onChange={handleSelectAllSystem} checked={systemMaps.length === systemMapsSelected.length} />
                                    <label className="ml-2 mr-3 mb-0">Tất cả</label>
                                </div>
                                <div className="m-0 d-flex" style={{ flexWrap: "wrap" }}>
                                    {
                                        systemMaps.map((systemMap, i) => {
                                            let projectId = systemMap.projectId;
                                            return (projectsSelected.some(p => p.projectId === projectId && customersSelected.some(c => c.customerId === p.customerId)) &&
                                                (
                                                    <div key={i} className="" style={{ width: 200 }}>
                                                        <Checkbox checked={systemMapsSelected.some(s => s.id === systemMap.id && projectsSelected.some(p => p.projectId === s.projectId))} inputId={systemMap.id} value={systemMap} onChange={handleSelectSystemMap} />
                                                        <label htmlFor={systemMap.id} className="ml-2 mr-3 mb-0">
                                                            {systemMap.systemTypeId === 1 && projectsSelected.find(p => p.projectId === projectId).projectName + " - LOAD"}
                                                            {systemMap.systemTypeId === 2 && projectsSelected.find(p => p.projectId === projectId).projectName + " - PV"}
                                                            {systemMap.systemTypeId === 3 && projectsSelected.find(p => p.projectId === projectId).projectName + " - WIND"}
                                                            {systemMap.systemTypeId === 4 && projectsSelected.find(p => p.projectId === projectId).projectName + " - BATTERY"}
                                                            {systemMap.systemTypeId === 5 && projectsSelected.find(p => p.projectId === projectId).projectName + " - GRID"}
                                                        </label>
                                                    </div>
                                                )
                                            )
                                        })
                                    }
                                </div>
                            </Fieldset>
                        </div>
                    )
                }
            </div>

            <Dialog header="" className="text-center" visible={successDialog} modal={false} style={{ width: '15vw', height: '10vw' }} onHide={() => setSuccessDialog(false)}>
                <p className="m-0 text-success">
                    Cập nhật thành công.
                </p>
            </Dialog>

            <Dialog header="Cảnh báo" visible={visible} modal={false} style={{ width: '50vw' }} onHide={() => setVisible(false)}>
                <p className="m-0">
                    Có lỗi. Vui lòng thử lại.
                </p>
            </Dialog>

            <Dialog header="Cảnh báo" visible={visibleCustomer} modal={false} style={{ width: '50vw' }} onHide={() => setVisibleCustomer(false)}>
                <p className="m-0">
                    Tài khoản phải có ít nhất 1 khách hàng
                </p>
            </Dialog>

            <Dialog className="text-center" visible={visibleLoad} modal={false} style={{ width: '6vw', height: '8vw' }} closable={false} >
                <ProgressSpinner style={{ width: '30px', height: '30px' }} strokeWidth="8" animationDuration="1s" />
            </Dialog>
        </div>
    )
}

export default PermissionSystemData;