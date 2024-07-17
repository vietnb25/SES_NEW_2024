import AddObjectTypeMst from "./add";
import EditObjectTypeMst from "./edit";
import ListObjectTypeMst from "./list";


const ObjectTypeMstRouters = [
    {
        component: AddObjectTypeMst,
        path: '/category/object-type-mst/add',
        link: {
            title: "Thêm đối tượng",
            icon: "fas fa-user-tie"
        },
        action: "ADD"
    },
    {
        component: EditObjectTypeMst,
        path: '/category/object-type-mst/edit/:id',
        link: {
            title: "Cập nhật thiết bị",
            icon: "fas fa-user-tie"
        },
        action: "UPDATE"
    },
    {
        component: ListObjectTypeMst,
        path: '/category/object-type-mst',
        link: {
            title: "Danh sách Đối tượng giám sát",
            icon: "fas fa-user-tie"
        },
        action: "READ"
    },
];

export default ObjectTypeMstRouters;