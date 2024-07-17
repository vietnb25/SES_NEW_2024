import AddSuperManager from "./add";
import EditSuperManager from "./edit";
import ListSuperManagers from "./list";

const SuperManagerRouters = [
    {
        component: AddSuperManager,
        path: '/category/super-manager/add',
        link: {
            title: "Thêm mới khu vực/ miền",
            icon: "fas fa-user-tie"
        },
        action: "CREATE"
    },
    {
        component: EditSuperManager,
        path: '/category/super-manager/edit/:id',
        link: {
            title: "Cập nhật khu vực/ miền",
            icon: "fas fa-user-tie"
        },
        action: "UPDATE"
    },
    {
        component: ListSuperManagers,
        path: '/category/super-manager',
        link: {
            title: "Danh sách khu vực/ miền",
            icon: "fas fa-user-tie"
        },
        action: "READ"
    }
];

export default SuperManagerRouters;