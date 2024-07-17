import AddManager from "./add";
import EditManager from "./edit";
import ListManager from "./list";


const ManagerRouters = [
    {
        component: AddManager,
        path: '/category/manager/add',
        link: {
            title: "Thêm mới tỉnh thành",
            icon: "fas fa-user-tie"
        },
        action: "CREATE"
    },
    {
        component: EditManager,
        path: '/category/manager/edit/:id',
        link: {
            title: "Cập nhật tỉnh thành",
            icon: "fas fa-user-tie"
        },
        action: "UPDATE"
    },
    {
        component: ListManager,
        path: '/category/manager',
        link: {
            title: "Danh sách tỉnh thành",
            icon: "fas fa-user-tie"
        },
        action: "READ"
    }
];

export default ManagerRouters;