import React, { useEffect } from "react";
import { useHistory } from "react-router-dom";
import homeService from "../../../../services/HomeService";

const $ = window.$;

const ProjectTree = ({location}) => {
    const history = useHistory();

    const initProjectTree = async () => {
        let res = await homeService.getProjectTree();
        $('#project-tree').jstree({
            'core': {
                'data': res.data
            },
            "plugins": [
                "search", "sort", "types", "state", "wholerow"
            ],
            "types": {
                "superManager": {
                    "icon": "/resources/image/icon-super-manager.png"
                },
                "manager": {
                    "icon": "/resources/image/icon-manager.png"
                },
                "customer": {
                    "icon": "/resources/image/icon-customer.png"
                },
                "area": {
                    "icon": "/resources/image/icon-area.png"
                },
                "project": {
                    "icon": "/resources/image/icon-project.png"
                },
                "load": {
                    "icon": "/resources/image/icon-load.png"
                },
                "solar": {
                    "icon": "/resources/image/icon-solar.png"
                }
            },
            "search": {
                "show_only_matches": true
            },
            "sort": function (a, b) {
                let a1 = this.get_node(a);
                let b1 = this.get_node(b);
                return (a1.data.position > b1.data.position) ? 1 : -1;
            }
        }).on("changed.jstree", (e, data) => {
            let type = data?.node?.original?.type;
            let nameKV = data?.node?.text;
            let hrefHeader = window.location.pathname;
            if((type && type === "superManager") && nameKV !== "All"){
                const superManagerIds = data.node.id.split("-");
                let superManagerId = superManagerIds[1];
                history.push({
                    pathname: hrefHeader,
                    search: `?superManagerId=${superManagerId}`
                })
            }else if((type && type === "manager") && nameKV !== "All"){
                const managerIds = data.node.id.split("-");
                let managerId = managerIds[1];
                history.push({
                    pathname: hrefHeader,
                    search: `?managerId=${managerId}`
                })
            }else if((type && type === "area")){
                const areaIds = data.node.id.split("-");
                let areaId = areaIds[1];
                history.push({
                    pathname: hrefHeader,
                    search: `?areaId=${areaId}`
                })
            }
            else {
                history.push({
                    pathname: hrefHeader
                });
            }
        });

        $('#project-tree').bind("loaded.jstree", function (event, data) {
            $('#project-tree').jstree("open_all");
        });

        $('#btn-search-project').keyup(function(){
            $('#project-tree').jstree(true).search($(this).val());
        });

        $('#project-tree').on('select_node.jstree', function (e, data) {
            $('#project-tree').jstree(true).deselect_node(data.node);
        });
    }

    useEffect(() => {
        initProjectTree();
    }, []);

    return <>
        <div id="project-list">
            <input type="text" id="btn-search-project" className="input" placeholder="   Tìm kiếm Dự án" autoComplete="off" />
            <div id="project-tree"></div>
        </div>
    </>
}

export default ProjectTree;