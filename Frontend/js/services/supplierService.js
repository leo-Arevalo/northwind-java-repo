//supplierService.js

import { apiClient } from "../services/apiClient.js";


const API_URL = "http://localhost:8080/api/suppliers";

export const supplierService = {

    //Get the recent suppliers
    async getRecentSuppliers() {
        const res = await apiClient(`${API_URL}/recent`);
        if(!res.ok){
            throw new Error("Error al obtener los proveedores recientes");
        }
        return await res.json();
    },

    //Get all the suppliers
    async getAll() {
        const res = await apiClient(API_URL);
        if(!res.ok){
            throw new Error("No se pudieron obtener los suppliers.");
        }
        return await res.json();
    },

    //Obtener un supplier por ID
    async getById(id){
        if(!id) throw new Error("ID de proveedor no proporcionado.");
        const res = await apiClient(`${API_URL}/${id}`);
        if(!res.ok) throw new Error(`No se pudo obtener el supplier con ID ${id}.`);
        return await res.json();
    },

    //Create a supplier
    async create(supplierData) {
        const res = await apiClient(API_URL, {
            method: "POST", //headers seteado en el apiClient
            body: JSON.stringify(supplierData)
        });
        if(!res.ok){
            const error = await res.text();
            throw new Error(`Error al crear el supplier: ${error}`);
        }
        return await res.json();
    },

    //Update an existing supplier
    async update(id, supplierData) {
        if(!id) throw new Error("ID de proveedor no porporcionado.");
        const res = await apiClient(`${API_URL}/${id}`, {
            method: "PUT",
            body: JSON.stringify(supplierData)
        });
        if(!res.ok) {
            const error = await res.text();
            throw new Error(`Error al actualizar el supplier: ${error}`);
        }
        return await res.json();
    },

    //Delete an existing supplier
    async delete(id) {
        if(!id) throw new Error("ID de proveedor no porporcionado.");
        const res = await apiClient(`${API_URL}/${id}`, {
            method: "DELETE"
        });
        if(!res.ok) {
            throw new Error("Error al eliminar el supplier.");
        }
        return true;

    }

};



