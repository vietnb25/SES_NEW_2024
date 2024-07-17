
import AddDevice from "./add";
import EditDevice from "./edit";
import ListDevice from "./list";

const DeviceRouters = [
    {
        component: AddDevice,
        path: '/category/device/add',
        link: {
            title: "Thêm mới thiết bị",
            icon: "fas fa-user-tie"
        },
        action: "CREATE"
    },
    {
        component: EditDevice,
        path: '/category/device/edit/:id',
        link: {
            title: "Cập nhật thiết bị",
            icon: "fas fa-user-tie"
        },
        action: "UPDATE"
    },
    {
        component: ListDevice,
        path: '/category/device',
        link: {
            title: "Danh sách thiết bị",
            icon: "fas fa-user-tie"
        },
        action: "READ"
    }
];

export default DeviceRouters;