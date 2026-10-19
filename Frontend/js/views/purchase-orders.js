import { authService } from "../services/authService.js";
import { apiClient, parseJsonOrThrow } from "../services/apiClient.js";
import { purchaseOrderService } from "../services/purchaseOrderService.js";
import { supplierService } from "../services/supplierService.js"

const API_BASE = "http://localhost:8080";

if(!authService.getAccessToken()) {
    window.location.href = "login.html";
}

const supplierSelect = document.getElementById("supplierSelect");
const productSelect = document.getElementById("productSelect");
const quantityInput = document.getElementById("quantityInput");
const unitCostInput = document.getElementById("unitCostInput");
const linesBody = document.getElementById("linesBody");
const totalCell = document.getElementById("totalCell");
const poResult = document.getElementById("poResult");
const pendingOrdersBody = document.getElementById("pendingOrdersBody");

let lines = [];
let productsById = {};

async function loadSuppliers() {
    try{
        const data = await supplierService.getAll();
        const suppliers = data.content || data;
        supplierSelect.innerHTML = '<option value="">-- Seleccionar proveedor --</option>';
        suppliers.forEach(s => {
            const opt = document.createElement("option");
            opt.value = s.id;
            opt.textContent = s.company;
            supplierSelect.appendChild(opt);
        });
    }catch(err){
        poResult.textContent = "Error al cargar proveedores: "+ err.message;
        poResult.className = "po-error";
    }
}
async function loadProducts(supplierId) {
    try{
        const url = supplierId
            ? `${API_BASE}/products/by-supplier/${supplierId}`
            : `${API_BASE}/products?page=0&size=200`;
        const data = await apiClient(url).then(parseJsonOrThrow);
        const products = data.content || data; //by-supplier devuelve un array plano, no paginado
        productsById = {};
        productSelect.innerHTML = supplierId
            ? '<option value="">-- Seleccionar producto --</option>'
            : '<option value="">-- Elegí primero un proveedor --</option>';
        products.forEach(p => {
            productsById[p.productID]=p;
            const opt = document.createElement("option");
            opt.value = p.productID;
            opt.textContent = `${p.productName} (costo ref. $${p.standardCost})`;
            productSelect.appendChild(opt);

        });
        if(supplierId && products.length === 0){
            productSelect.innerHTML = '<option value="">--Este proveedor no tiene productos cargados --</option>';
        }
    }catch(err){
        poResult.textContent = "Error al cargar productos: "+err.message;
        poResult.className = "po-error";
    }
}
supplierSelect.addEventListener("change", () => {
    lines = [];
    renderLines();
    loadProducts(supplierSelect.value || null);
})

productSelect.addEventListener("change", () => {
    const p = productsById[productSelect.value];
    if(p && !unitCostInput.value) {
        unitCostInput.value = p.standardCost;
    }
});

document.getElementById("addLineBtn").addEventListener("click", () => {
    const productId = productSelect.value;
    const quantity = parseFloat(quantityInput.value);
    const unitCost = parseFloat(unitCostInput.value);
    
    if(!productId) { alert("Elegí un producto."); return; }
    if(!quantity || quantity <= 0) { alert("La cantidad debe ser mayor a 0."); return; }
    if(!unitCost || unitCost <= 0) { alert("El costo unitario debe ser mayor a 0."); return; }

    const product = productsById[productId];
    lines.push({ productId, productName: product.productName, quantity, unitCost });
    renderLines();

    productSelect.value = "";
    quantityInput.value = 1;
    unitCostInput.value = "";
});

function renderLines() {
    linesBody.innerHTML = "";
    let total = 0;
    lines.forEach((line, idx) => {
        const subtotal = line.quantity * line.unitCost;

        total += subtotal;

        const row = document.createElement("tr");
        row.innerHTML = `
            <td>${line.productName}</td>
            <td>${line.quantity}</td>
            <td>$${line.unitCost.toFixed(2)}</td>
            <td>$${subtotal.toFixed(2)}</td>
            <td><button data-idx="${idx}" class="remove-line-btn">Quitar</button></td>
        `;
        linesBody.appendChild(row);
    });
    totalCell.textContent = `$${total.toFixed(2)}`;
}

linesBody.addEventListener("click", (e) => {
    if(e.target.classList.contains("remove-line-btn")) {
        lines.splice(parseInt(e.target.dataset.idx), 1);
        renderLines();
    }
});

document.getElementById("submitPOBtn").addEventListener("click", async () =>{
    poResult.textContent = "";
    poResult.className = "";

    const supplierId = supplierSelect.value;
    if(!supplierId) {alert("Elegí un proveedor."); return; }
    if(lines.length === 0) {alert("Agregá al menos un producto."); return; }

    const payload = {
        supplierId: parseInt(supplierId),
        expectedDate: document.getElementById("expectedDate").value || null,
        orderDetails: lines.map(l => ({
            productId: parseInt(l.productId),
            quantity: l.quantity,
            unitCost: l.unitCost,
        })),
    };

    try{
        const created = await purchaseOrderService.create(payload);
        poResult.textContent = `Orden de Compra #${created.id} creada correctamente. `;
        poResult.className = "po-success";
        lines = [];
        renderLines();
        await loadPendingOrders();
    }catch(err){
        poResult.textContent = "Error: "+err.message;
        poResult.className = "po-error";
    }
});

// ---- Ordenes pendientes de recibir -----

async function loadPendingOrders() {
    try{
        const data = await purchaseOrderService.getAll();
        const orders = (data.content || data).filter(o => o.statusId !== 3); //3 = Closed
        pendingOrdersBody.innerHTML = "";
        orders.forEach( o => {
            const row = document.createElement("tr");
            row.innerHTML = `
                <td>${o.id}</td>
                <td>Proveedor #${o.supplierId}</td>
                <td>${o.creationDate ?? ''}</td>
                <td>${statusLabel(o.statusId)}</td>
                <td><button class="btn secondary receive-btn" data-id="${o.id}">Recibir</button></td>
            `;
            pendingOrdersBody.appendChild(row);
        });
    }catch(err){
        pendingOrdersBody.innerHTML = `<tr><td colspan="5">Error al cargar órdenes: ${err.message}</td></tr>`;
    }
}

function statusLabel(statusId) {
    // seed purchase_order_status: 0 New, 1 Submitted, 2 Approved, 3 Closed
    return { 0: "Nueva", 1: "Enviada", 2: "Aprobada", 3: "Cerrada" }[statusId] ?? statusId;
}

pendingOrdersBody.addEventListener("click", async (e) => {
    if (!e.target.classList.contains("receive-btn")) return;
    const id = e.target.dataset.id;
    if (!confirm(`¿Confirmás la recepción de la orden #${id}? Esto repone el stock de sus productos.`)) return;
    try {
        await purchaseOrderService.update(id, { statusId: 3 });
        alert("Orden recibida, stock repuesto.");
        await loadPendingOrders();
    } catch (err) {
        alert("Error al recibir la orden: " + err.message);
    }
});

document.getElementById("logoutBtn").addEventListener("click", async () => {
    await authService.logoutFromServer();
    window.location.href = "login.html";
});

document.addEventListener("DOMContentLoaded", () => {
    loadSuppliers();
    loadProducts();
    loadPendingOrders();
});







