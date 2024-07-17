// Tạo Global State cho ứng dụng dựa trên người dùng đang đăng nhập.

import { create } from "zustand";
import { persist, createJSONStorage } from 'zustand/middleware'

const useAppStore = create(persist(
    (set, get) => ({
        userTreeData: [],
        googleMapData: {},
        projectMarkers: [],
        appUserData: {},
        categoryPath: [],
        imageProfile: "",
        language: "",
        saveUserData : (userData) => {
            set(() => ({ appUserData: userData }))
        },
        saveTreeData: (treeData) => {
            set(() => ({userTreeData: treeData}))
        },
        saveMapData: (mapData) => {
            set(() => ({projectMarkers: mapData}))
        },
        saveGoogleMapData : (map) => {
            set(() => ({googleMapData: map }));
        },
        saveCategoryPath : (paths) => {
            set(() => ({categoryPath: paths }));
        },
        saveImage : (image) => {
            set(() => ({imageProfile: image }));
        },
        saveLanguage : (language) => {
            set(() => ({language: language }));
        }
    }),
    {
        name: 'user-storage', // name of the item in the storage (must be unique)
        storage: createJSONStorage(() => sessionStorage), // (optional) by default, 'localStorage' is used
    }
));

export default useAppStore;