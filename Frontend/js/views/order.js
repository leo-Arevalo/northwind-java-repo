import { authService } from "../services/authService.js";
import { apiClient, parseJsonOrThrow } from "../services/apiClient.js";


// Mas nuevos primero: por defecto el backend ordena por fecha ascendente
// y devuelve solo 10, asi que una factura recien creada nunca se veía.

const API_URL = 'http://localhost:8080/orders?shortBy=orderDate&sortDirection=desc&size=20';


if(!authService.getAccessToken()){
    window.location.href = "login.html";
}

const tableBody = document.querySelector("#ordersTable tbody");

//Los datos del pago (cupon, banco...) los escribe una persona: se escapan
//antes de meterlos en el innerHTML para que no se interpreten como HTML.

const escapeHtml = (text) => String(text ?? '')
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;")
    .replace(/"/g, "&quot;")
    .replace(/'/g, "&#39;");

//Una línea por pago. Una factura puede tener varios (pagos mixtos)
const paymentLine = (p) => {
    const monto = `$${Number(p.amount).toFixed(2)}`;
    if(p.paymentMethod === "Tarjeta") {
        const tipo = p.cardType === "CREDIT" ? "crédito" : "débito";
        const cuotas = p.installments > 1 ? `·${p.installments} cuotas` : '';
        return escapeHtml(`Tarjeta ${tipo} ${p.cardBrand}${cuotas} · cupón ${p.posVoucherNumber} -- ${monto}`);
    }
    if(p.paymentMethod === "Transferencia"){
        const banco = p.transferBank ? `·${p.transferBank}` : '';
        return escapeHtml(`Transferencia · op. ${p.transferReference}${banco} -- ${monto}`);
    }
    return escapeHtml(`${p.paymentMethod} -- ${monto}`);
};

const updatePaymentSummary = (o) => {
    if(!o.payments || o.payments.length === 0) return escapeHtml (o.paymentType ?? '');
    return o.payments.map(paymentLine).join('<br>');
}


const loadOrders = async () => {
    try {
        const data = await apiClient(API_URL).then(parseJsonOrThrow);
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
    <td>${escapeHTML(clienteNombre)}</td>
    <td>${o.orderDate ?? ''}</td>
    <td>${o.shippedDate ?? ''}</td>
    <td>${o.status ? escapeHtml(o.status.statusName) : ''}</td>
    <td>${o.shippingFee ?? ''}</td>
    <td>${o.taxes ?? ''}</td>
    <td>${paymentSummary(o)}</td>
    `;
    tableBody.appendChild(row);
};

document.getElementById("logoutBtn").addEventListener("click", async () => {
    await authService.logoutFromServer();
    window.location.href = "login.html";
});

document.addEventListener("DOMContentLoaded", loadOrders);






