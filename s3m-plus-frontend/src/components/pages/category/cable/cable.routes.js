import AddCable from "./add";
import EditCable from "./edit";
import ListCable from "./list";

const CableRouters = [
    {
        component: AddCable,
        path: '/category/cables/add',
        link: {
            title: "Thêm mới cáp",
            icon: "fas fa-user-tie"
        },
        action: "CREATE"
    },
    {
        component: EditCable,
        path: '/category/cables/edit/:id',
        link: {
            title: "Cập nhật cáp",
            icon: "fas fa-user-tie"
        },
        action: "UPDATE"
    },
    {
        component: ListCable,
        path: '/category/cables',
        link: {
            title: "Danh sách cáp",
            icon: "fas fa-user-tie"
        },
        action: "READ"
    }
];

export default CableRouters;