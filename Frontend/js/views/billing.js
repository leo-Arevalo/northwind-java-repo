import { authService } from "../services/authService.js";
import { apiClient, parseJsonOrThrow } from "../services/apiClient.js";
import { initCustomerForm } from "../components/customerForm.js";

const API_BASE = "http://localhost:8080";

if(!authService.getAccessToken()){
    window.location.href = "../views/login.html";
}

const customerSelect = document.getElementById("customerSelect");
const step2Card = document.getElementById("step2Card");
const step3Card = document.getElementById("step3Card");

/**
 * Bloquea / desbloquea los pasos 2 y 3 segun el avance real:
 * paso 2 (agregar producto) requiere cliente elegido, paso 3
 * (envio / impuesto) requiere al menos un producto en la factura.
 */

function updateStepGating() {
    const hasCustomer = !!customerSelect.value;
    const hasLines = lines.length > 0;

    setCardEnabled(step2Card, hasCustomer);
    setCardEnabled(step3Card, hasCustomer && hasLines);
}
function setCardEnabled(card, enabled){
    card.classList.toggle("step-disabled", !enabled)
    card.querySelectorAll("input, select, button").forEach(el => {
        el.disabled = !enabled;
    });
}

customerSelect.addEventListener("change", updateStepGating);
const productSelect = document.getElementById("productSelect");
const quantityInput = document.getElementById("quantityInput");
const stockInfo = document.getElementById("stockInfo");
const linesBody = document.querySelector("#linesTable tbody");
const orderResult = document.getElementById("orderResult");

//Lineas de la factura en memoria: { productId, productName, quantity, unitPrice }
let lines = [];
let productsById = {};

//---- Cliente: mismo componente reutilizable que customer.html ----
//Al guardar (crear cliente nuevo desde la factura), en vez de recargar
//una tabla, seleccionamos automaticamente al cliente recien creado.

const customerForm = initCustomerForm({
    formContainer: document.getElementById("customerForm"),
    onSaved: (savedCustomer) => {
        if(savedCustomer){
            loadCustomers(savedCustomer.id).then(updateStepGating);
        }
    }
});
document.getElementById("newCustomerBtn").addEventListener("click", () => customerForm.openNew());

// --- Cargar clientes y productos para los selects ---

async function loadCustomers(selectId) {
    try{
        const data = await apiClient(`${API_BASE}/customers?page=0&size=200`).then(parseJsonOrThrow);
        customerSelect.innerHTML = '<option value="">-- seleccionar cliente --</option>';
        (data.content || []).forEach(c => {
            const opt = document.createElement("option");
            opt.value = c.id;
            opt.textContent = `${c.company} - ${c.firstName} ${c.lastName}`;
            customerSelect.appendChild(opt);
        });
        if(selectId) customerSelect.value = selectId;
    }catch(err){
        alert("Error al cargar clientes: "+err.message);
    }
}

async function loadProducts() {
    try{
        const data = await apiClient(`${API_BASE}/products?page=0&size=200`).then(parseJsonOrThrow);
        productSelect.innerHTML='<option value="">-- Seleccionar producto --</option>';
        (data.content || []).forEach(p => {
            productsById[p.productID] =p;
            if(p.discontinued) return; //no ofrecer productos descontinuados
            const opt = document.createElement("option");
            opt.value = p.productID;
            opt.textContent = `${p.productName} ($${p.listPrice})`;
            productSelect.appendChild(opt); 
        });
    }catch(err){
        alert("Error al cargar productos: "+err.message);
    }
}

// ---- Mostrar stock disponible al elegir producto ----
// (el back valida igual al confirmar; esto es solo para no dejar
// cargar a ciegas una cantidad que sabemos que va a rebotar)

productSelect.addEventListener("change", async () => {
    stockInfo.textContent = "";
    const id = productSelect.value;
    if(!id) return;
    try{
        const p = await apiClient(`${API_BASE}/products/${id}`).then(parseJsonOrThrow);
        stockInfo.textContent = `Costo: $${p.standardCost} | Precio lista: $${p.listPrice}`;
    }catch(err){
        //no bloqueante, solo informativo.
    }
});

// ---- Agregar linea a la factura ----
document.getElementById("addLineBtn").addEventListener("click", async () => {
    const productId = productSelect.value;
    const quantity = parseInt(quantityInput.value);

    if(!productId) {
        alert("Elegí un producto.");
        return;
    }
    if(!quantity || quantity <= 0){
        alert("La cantidad debe ser mayo a 0.");
        return;
    }

    // Validar stock disponible ANTES de agregar la línea, para no
    //dejar cargar una cantidad que el backend va a rechazar igual
    //al confirmar la factura.

    const existingQty = lines
        .filter(l => l.productId === productId)
        .reduce((sum, l) => sum + l.quantity, 0);
    try{
        const stock = await apiClient(`${API_BASE}/products/${productId}/stock`).then(parseJsonOrThrow);
        if (existingQty + quantity > stock.availableStock) {
            alert(`Stock insuficiente. Disponible: ${stock.availableStock}, ya tenés ${existingQty} en la factura, estás pidiendo ${quantity} más. `);
            return;
        }
    }catch (err) {
        alert("No se pudo validar el stock: " +  err.message);
        return;
    }



    const product = productsById[productId];
    const existing = lines.find(l => l.productId === productId);
    if(existing){
        existing.quantity += quantity;
    }else{
        lines.push({
            productId,
            productName: product.productName,
            quantity,
            unitPrice: product.listPrice
        });
    }
    renderLines();
    //Lipiamos el formulario para la proxima línea - antes quedaba el
    //producto anterior seleccionado, invitando a error.
    productSelect.value = "";
    quantityInput.value = 1;
    stockInfo.textContent = "";

});

function renderLines() {
    linesBody.innerHTML = "";
    lines.forEach((line, idx) => {
        const subtotal = (line.unitPrice * line.quantity).toFixed(2);
        const row = document.createElement("tr");
        row.innerHTML = `
        <td>${line.productName}</td>
        <td>${line.quantity}</td>
        <td>${line.unitPrice}</td>
        <td>${subtotal}</td>
        <td><button data-idx="${idx}" class="remove-line-btn">Quitar</button></td>
        `;
        linesBody.appendChild(row);
    });
    recalcTotals();
    updateStepGating();
}
linesBody.addEventListener("click", (e) => {
    if(e.target.classList.contains("remove-line-btn")) {
        const idx = parseInt(e.target.dataset.idx);
        lines.splice(idx,1);
        renderLines();
    }
});
function recalcTotals() {
    const subtotal = lines.reduce((sum, l) => sum + (l.unitPrice * l.quantity), 0);
    const taxRate = parseFloat(document.getElementById("taxRate").value) || 0;
    const shippingFee = parseFloat(document.getElementById("shippingFee").value) || 0;
    const taxes = subtotal * (taxRate /100);
    const total = subtotal + taxes + shippingFee;

    document.getElementById("subtotalCell").textContent = subtotal.toFixed(2);
    document.getElementById("taxesCell").textContent = taxes.toFixed(2);
    document.getElementById("shippingCell").textContent = shippingFee.toFixed(2);
    document.getElementById("totalCell").textContent = total.toFixed(2);
}
document.getElementById("taxRate").addEventListener("input", recalcTotals);
document.getElementById("shippingFee").addEventListener("input", recalcTotals);

// ---- Confirmar factura ----
document.getElementById("submitOrderBtn").addEventListener("click", async () => {
    orderResult.textContent = "";
    orderResult.className = "";

    const customerId = customerSelect.value;
    if(!customerId){
        alert("Elegí un cliente ( o creá uno nuevo).");
        return;
    }
    if(lines.length === 0){
        alert("Agregá al menos un producto.");
        return;
    }

    const shipName = document.getElementById("shipName").value.trim()
        || customerSelect.options[customerSelect.selectedIndex].textContent;

    const payload = {
        customerId: parseInt(customerId),
        shipName,
        shipAddress: document.getElementById("shipAddress").value.trim(),
        shippingFee: parseFloat (document.getElementById("shippingFee").value) || 0,
        taxRate: parseFloat(document.getElementById("taxRate").value) || 0,
        paymentType: document.getElementById("paymentType").value,
        orderDetails: lines.map(l => ({
            productId: parseInt(l.productId),
            quantity: l.quantity,
            unitPrice: l.unitPrice,
        }))
    };

    try{
        const res = await apiClient(`${API_BASE}/orders`,{
            method: "POST",
            body: JSON.stringify(payload),
        });
        if(!res.ok){
            const err = await res.json().catch( () => ({}));
            throw new Error(err.message || "No se pudo crear la factura.");
        }
        const created = await res.json();
        orderResult.textContent = `Factura #${created.orderID} creada correctamente.`;
        orderResult.className = "";
        lines = [];
        renderLines();
        await loadProducts(); //refresca stock disponible mostrado
    }catch(err) {
        orderResult.textContent = "Error: "+ err.message;
        orderResult.className = "stock-warning";
    }
});

document.getElementById("logoutBtn").addEventListener("click", async () => {
    await authService.logoutFromServer();
    window.location.href = "../views/login.html";
});

document.addEventListener("DOMContentLoaded", () => {
    loadCustomers();
    loadProducts();
    updateStepGating();
});











