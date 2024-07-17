import ListLoadType from "./list";
import AddLoadType from "./add";
import EditLoadType from "./edit";

const LoadTypeRouters = [
    {
        component: AddLoadType,
        path: '/category/load-type/add',
        link: {
            title: "Thêm phụ tải",
            icon: "fas fa-user-tie"
        },
        action: "CREATE"
    },
    {
        component: EditLoadType,
        path: '/category/load-type/edit/:id',
        link: {
            title: "Cập nhật phụ tải",
            icon: "fas fa-user-tie"
        },
        action: "UPDATE"
    },
    {
        component: ListLoadType,
        path: '/category/load-type',
        link: {
            title: "Danh sách phụ tải",
            icon: ""
        },
        action: "READ"
    }
];

export default LoadTypeRouters;