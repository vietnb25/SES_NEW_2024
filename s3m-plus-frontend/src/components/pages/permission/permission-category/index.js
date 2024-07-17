import { Checkbox } from "primereact/checkbox";
import { Dialog } from "primereact/dialog";
import { Fieldset } from "primereact/fieldset";
import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import CategoryRouters from "../../category/category.routes";
import PermissionService from "../../../../services/PermissionService";
import AuthService from "../../../../services/AuthService";
import useAppStore from "../../../../applications/store/AppStore";

import { ProgressSpinner } from 'primereact/progressspinner';

const actions = [
    "CREATE", "READ", "UPDATE", "DELETE"
]

const PermissionCategory = ({userId}) => {
    const [visible, setVisible] = useState(false);
    const [visibleLoad, setVisibleLoad] = useState(false);
    const [successDialog, setSuccessDialog] = useState(false);
    const [routesSelected, setRoutesSelected] = useState([]);
    const [role] = useState(AuthService.getRoleName());
    const [categoryPath] = useState(useAppStore(state => state.categoryPath));

    const handleSave = async () => {
        setVisibleLoad(true);
        let paths = [];
        routesSelected.forEach(route => {
            paths.push(route)
        });

        let data = {
            userId,
            categoriesPath: JSON.stringify(paths)
        }

        let res = await PermissionService.addCategoryPermission(data);

        setVisibleLoad(false);

        if (res.status === 200) {
            setSuccessDialog(true);
        }else{
            setVisible(true);
        }
    }

    const handleSelectRoute = (e, route) => {
        let routes = [...routesSelected];
        if(e.checked){
            let currentRoute = routesSelected.find(r => r.path === route.path);
            if(currentRoute){
                let actions = [...currentRoute.actions];
                actions.push(e.value);
                currentRoute.actions = actions;
            }else{
                route.actions.push(e.value);
                routes.push(route);
            }
        }else{
            let categoryRouter = CategoryRouters.find(r => r.path === route.path);
            let actions = categoryRouter.actions.filter(f => f !== e.value);
            categoryRouter.actions = actions;
        }
        setRoutesSelected(routes);
    }

    useEffect(() => {
        const loadPermission = async () => {
            setVisibleLoad(true);
            let permissions = await PermissionService.getCategoryPermission(userId);
            if(permissions?.status === 200){
                let data = permissions?.data ? JSON.parse(permissions?.data && permissions?.data?.content) : [];
                CategoryRouters.forEach(cRoute => {
                    data.forEach(d => {
                        if(cRoute.path === d.path){
                            cRoute.actions = d.actions
                        }
                    })
                });
            }
            setVisibleLoad(false);
            
        }
        loadPermission();
    }, []);

    return (
        <div className="tab-container">
            <ul className="menu">
                <li>
                    <Link to={`/`} onClick={e => e.preventDefault()}><i className="fas fa-user-tie"></i>&nbsp; <span>Phân quyền danh mục</span></Link>
                </li>
                <li style={{float: "right", textAlign: "right"}}>
                    <button type="button" className="btn btn-warning" onClick={handleSave}><i className="fa fa-save"></i> Lưu</button>
                </li>
            </ul>
            <div style={{paddingTop: 10, height: 815, overflow: "scroll"}}>
                <div className="card" style={{border: "none"}}>
                    {
                        role === "ROLE_ADMIN" &&
                        CategoryRouters.map((route, i) => {
                            return (
                                <Fieldset key={i} legend={route.link.title} toggleable>
                                    <div className="m-0 d-flex pb-3" style={{flexWrap: "wrap"}}>
                                        {
                                            route.path === "/category/setting" ? (
                                                ['READ', 'UPDATE'].map((action, i) => {
                                                    return (
                                                        <div key={i} className="mt-2" style={{width: 200}}>
                                                            <Checkbox checked={route.actions.some(act => act === action)} value={action} onChange={e => handleSelectRoute(e, route)}/>
                                                            <label className="ml-2 mr-3 mb-0">{action}</label>
                                                        </div>
                                                    )
                                                })
                                            ) : 
                                            actions.map((action, i) => { 
                                                return  (
                                                    <div key={i} className="mt-2" style={{width: 200}}>
                                                        <Checkbox checked={route.actions.some(act => act === action)} value={action} onChange={e => handleSelectRoute(e, route)}/>
                                                        <label className="ml-2 mr-3 mb-0">{action}</label>
                                                    </div>
                                                )
                                            })
                                        }
                                    </div>
                                </Fieldset>
                            )
                        })
                    }
                    {
                        role !== "ROLE_ADMIN" &&
                        CategoryRouters.map((route, i) => {
                            return categoryPath.map((path, j) => {
                                return (route.path === path.path) && (
                                    <Fieldset key={j} legend={route.link.title} toggleable 
                                        hidden={(role !== "ROLE_ADMIN" && 
                                        (["/category/users", "/category/customer", "/category/super-manager", "/category/manager", "/category/area", "/category/device"].some(s => s === route.path)))  
                                    }>
                                        <div className="m-0 d-flex pb-3" style={{flexWrap: "wrap"}}>
                                            {
                                                route.path === "/category/setting" ? (
                                                    ['READ', 'UPDATE'].map((action, i) => {
                                                        return (
                                                            <div key={i} className="mt-2" style={{width: 200}}>
                                                                <Checkbox checked={route.actions.some(act => act === action)} value={action} onChange={e => handleSelectRoute(e, route)}/>
                                                                <label className="ml-2 mr-3 mb-0">{action}</label>
                                                            </div>
                                                        )
                                                    })
                                                ) : 
                                                path.actions.map(a => {
                                                    return actions.map((action, i) => {
                                                        return (a === action) && (
                                                            <div key={i} className="mt-2" style={{width: 200}}>
                                                                <Checkbox checked={route.actions.some(act => act === action)} value={action} onChange={e => handleSelectRoute(e, route)}/>
                                                                <label className="ml-2 mr-3 mb-0">{action}</label>
                                                            </div>
                                                        )
                                                    })
                                                })
                                            }
                                        </div>
                                    </Fieldset>
                                )
                            });
                        })
                    }
                </div>
            </div>
            <Dialog header="Cảnh báo" visible={visible} modal={false} style={{ width: '30vw' }} onHide={() => setVisible(false)}>
                <p className="m-0">
                    Có lỗi. Vui lòng thử lại.
                </p>
            </Dialog>

            <Dialog header="" className="text-center" visible={successDialog} modal={false} style={{ width: '15vw', height: '10vw' }} onHide={() => setSuccessDialog(false)}>
                <p className="m-0 text-success">
                    Cập nhật thành công.
                </p>
            </Dialog>

            <Dialog className="text-center" visible={visibleLoad} modal={false} style={{ width: '6vw', height: '8vw' }} closable={false} >
                <ProgressSpinner style={{width: '30px', height: '30px'}} strokeWidth="8" animationDuration="1s" />
            </Dialog>
        </div>
    )
}

export default PermissionCategory;