
import { apiClient } from "../services/apiClient.js";



const API_URL = "http://localhost:8080/api/purchase-orders";

export const purchaseOrderService = {

    async getAll(){
        const res = await apiClient(API_URL);
        if(!res.ok) throw new Error("No se pudieron obtener las ordenes.");
        return await res.json();
    },
    async getById(id){
        if(!id) throw new Error("ID de orden no proporcionada.");
        const res = await apiClient(`${API_URL}/${id}`);
        if(!res.ok) throw new Error(`No se pudo obtener la orden con id ${id}.`);
        return await res.json();
    },
    
    async getBySupplierId(supplierId){
        if(!supplierId) throw new Error("ID de supplier no proporcionado.");
        const res = await apiClient(`${API_URL}/by-supplier/${supplierId}`);
        if(!res.ok) throw new Error(`No se pudieron obtener las ordenes del proveedor ${supplierId}.`);
        return await res.json();
    },
    async create(orderData) {
        const res = await apiClient(API_URL, {
            method : "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(orderData)
        });
        if(!res.ok) {
            const error = await res.text();
            throw new Error(`Error al crear la orden: ${error}`);
        }
        return await res.json();
    },

    async update(id, orderData){
        if(!id) throw new Error("ID de orden no proporcionado.");
        const res = await apiClient(`${API_URL}/${id}`, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(orderData)
        });
        if(!res.ok){
            const error = await res.text();
            throw new Error(`Error al actualizar la orden: ${error}`);
        }
        return await res.json();
    },
    async delete(id){
        if(!id) throw new Error("ID de orden no proporcionada.");
        const res = await apiClient(`${API_URL}/${id}`,{
            method: "DELETE"
        });
        if(!res.ok) throw new Error("Error al eliminar la orden");
        return true;
    }


}









