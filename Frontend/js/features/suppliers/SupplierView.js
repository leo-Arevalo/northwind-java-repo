
// features/suppliers/SupplierDetailView.js

//logica principal de l a pantalla (supplier solo)

import { supplierService } from "../../services/supplierService.js";
import { purchaseOrderService } from "../../services/purchaseOrderService.js";
import { openSupplierModal } from "../../features/suppliers/supplierModal.js";

/**
 * Renderiza la vista completa del detalle de un supplier
 */

document.addEventListener("DOMContentLoaded", async () => {
    const params = new URLSearchParams(window.location.search);
    const supplierId = params.get("id");

    if(!supplierId){
        alert(" No se proporcionó un ID de proveedor.");
    }
    try{
        const supplier = await supplierService.getById(supplierId);
        renderSupplierDetails(supplier);

        const orders = await purchaseOrderService.getBySupplierId(supplierId);
        renderPurchaseOrders(orders);

        renderSupplierStats(orders);
    }catch(err){
        console.error("Error cargando el detalle del proveedor:", err);
        document.getElementById("supplierDetail").innerHTML = `<p>Error al cargar el proveedor</p>`;
    }

});

function renderSupplierDetails(supplier) {
    const container = document.getElementById("supplierDetail");
    container.innerHTML = `
    <h2>${supplier.firstName} ${supplier.lastName}</h2>
    <p><strong>Empresa:</strong> ${supplier.company}</p>
    <p><storng>Email:</strong> ${supplier.emailAddress}</p>
    <p><strong>Teléfono:</strong> ${supplier.businessPhone || "-"}</p>
    <p><strong>Direccion:</strong> ${supplier.address || "-"}, ${supplier.city || "-"}, ${supplier.countryRegion}</p>
    <p><strong>Cargo:</strong> ${supplier.jobTitle} || "-"}</p>
    <button id="editSupplierBtn" class="btn">Editar</button>
    `;

    document.getElementById("editSupplierBtn").onclick = () => {
       openSupplierModal(supplier);
    };
}
function renderPurchaseOrders(orders){
    const container = document.getElementById("purchaseOrders");
    if(!orders.length){
        container.innerHTML = "<p>No hay órdenes asociadas a este proveedor.</p>";
    return
    }

    const table = document.createElement("table");
    table.className = "orders-table";
    table.innerHTML = `
        <thead>
            <tr>
                <th>ID</th>
                <th>Fecha</th>
                <th>Estado</th>
                <th>Total</th>
                <th>Método de pago</th>
            </tr>
        </thead>
        <tbody>

        ${orders.map(order => `
            <tr>
                <td>${order.id}</id>
                <td>${order.creationDate?.substring(0, 10)}</td>
                <td>${order.status?.name || "-"}</td>
                <td>${order.paymentAmount?.toFixed(2) || "0.00"}</td>
                <td>${order.paymentMethod || "-"}</td>
            </tr>`).join("")}
        </tbody>
    `;
    container.appendChild(table);

}

function renderSupplierStats(orders) {
    const container = document.getElementById("supplierStats");

    const totalOrders = orders.length;
    const totalPaid = orders.reduce((sum, o) => sum + (o.paymentAmount || 0), 0);
    const average = totalOrders ? totalPaid / totalOrders : 0;

    container.innerHTML = `
        <h3>Estadísticas</h3>
        <ul>
            <li><strong>Total de órdenes:</strong> ${totalOrders}</li>
            <li><strong>Total pagado:</strong> $${totalPaid.toFixed(2)}</li>
            <li><strong>Promedio por orden:</strong> $${average.toFixed(2)}</li>
        </ul>
    `;
}


document.addEventListener("supplierUpdated", async (e) => {
    try{
        const supplierId = e.detail.id;
        if(!supplierId) return;

        // volvemos a pedir los datos actualizados al backend
        const updateSupplier = await supplierService.getById(supplierId);
        renderSupplierDetails(updateSupplier);

         //Recargar ordenes y estadísticas (en caso de que cambie algun dato)

         const orders = await purchaseOrderService.getBySupplierId(supplierId);
         renderPurchaseOrders(orders);
         renderSupplierStats(orders);

    }catch(err){
        console.error("Error recargando detalle del proveedor:", err);
    }
});




