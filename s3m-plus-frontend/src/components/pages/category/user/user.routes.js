import AddUser from "./add";
import EditUser from "./edit";
import ListUsers from "./list";

const UserRouters = [
    {
        component: AddUser,
        path: '/category/users/add',
        link: {
            title: "Thêm mới tài khoản",
            icon: "fas fa-user-tie"
        },
        action: "CREATE"
    },
    {
        component: EditUser,
        path: '/category/users/:id/edit',
        link: {
            title: "Cập nhật tài khoản",
            icon: "fas fa-user-tie"
        },
        action: "UPDATE"
    },
    {
        component: ListUsers,
        path: '/category/users',
        link: {
            title: "Danh sách tài khoản",
            icon: "fas fa-user-tie"
        },
        action: "READ"
    }
];

export default UserRouters;