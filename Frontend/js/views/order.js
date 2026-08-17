import { authService } from "./services/authService.js";
import { apiClient } from "./services/apiClient.js";

const API_URL = 'http://localhost:8080/orders';

if(!authService.getAccessToken()){
    window.location.href = "login.html";
}

const tableBody = document.querySelector("#ordersTable tbody");

const loadOrders = async () => {
    try {
        const data = await apiClient(API_URL).then(res => res.json());
        const orders = data.content || [];
        tableBody.innerHTML = "";
        orders.forEach(addOrderRow);
    }catch(err){
        alert("Error al cargar pedidos: "+ err.message);
    }
};

const addOrderRow = (o) => {
    const row = document.createElement("tr");
    const clienteNombre = o.customer ? `${o.customer.firstName ?? ''} ${o.customer.lastName ?? ''}`.trim() : '';
    row.innerHTML = `
    <td>${o.orderID}</td>
    <td>${clienteNombre}</td>
    <td>${o.orderDate ?? ''}</td>
    <td>${o.shipperDate ?? ''}</td>
    <td>${o.status ? o.status.statusName : ''}</td>
    <td>${o.shippingFee ?? ''}</td>
    <td>${o.taxes ?? ''}</td>
    `;
    tableBody.appendChild(row);
};

document.getElementById("logoutBtn").addEventListener("click", async () => {
    await authService.logoutFromServer();
    window.location.href = "login.html";
});

document.addEventListener("DOMContentLoaded", loadOrders);






