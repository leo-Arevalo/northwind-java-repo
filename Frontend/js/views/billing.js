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
const step4Card = document.getElementById("step4Card");

/**
 * Bloquea/desbloquea los pasos 2, 3 y 4 segun el avance real:
 * paso 2 (agregar producto) requiere cliente elegido; los pasos 3
 * (envio/impuestos) y 4 (pago) requieren al menos un producto.
 */
function updateStepGating() {
    const hasCustomer = !!customerSelect.value;
    const hasLines = lines.length > 0;

    setCardEnabled(step2Card, hasCustomer);
    setCardEnabled(step3Card, hasCustomer && hasLines);
    setCardEnabled(step4Card, hasCustomer && hasLines);
}

function setCardEnabled(card, enabled) {
    card.classList.toggle("step-disabled", !enabled);
    card.querySelectorAll("input, select, button").forEach(el => {
        el.disabled = !enabled;
    });
}

customerSelect.addEventListener("change", updateStepGating);

// ---- Datos del pago segun el metodo elegido ----
const paymentTypeSelect = document.getElementById("paymentType");
const cardFields = document.getElementById("cardFields");
const transferFields = document.getElementById("transferFields");
const installmentsWrap = document.getElementById("installmentsWrap");
const cardTypeSelect = document.getElementById("cardType");

function updatePaymentFields() {
    const method = paymentTypeSelect.value;
    cardFields.hidden = method !== "Tarjeta";
    transferFields.hidden = method !== "Transferencia";
    // las cuotas solo existen en credito
    installmentsWrap.hidden = !(method === "Tarjeta" && cardTypeSelect.value === "CREDIT");
}
paymentTypeSelect.addEventListener("change", updatePaymentFields);
cardTypeSelect.addEventListener("change", updatePaymentFields);

const val = (id) => document.getElementById(id).value.trim();

/**
 * Arma el detalle del pago segun el metodo y valida lo obligatorio
 * (mismas reglas que el backend, asi el error aparece antes de enviar).
 * Efectivo no lleva detalle.
 */
function buildPaymentPayload(method) {
    if (method === "Tarjeta") {
        const cardType = val("cardType");
        if (!cardType) throw new Error("Indicá si la tarjeta es de débito o de crédito.");
        if (!val("cardBrand")) throw new Error("Falta la marca de la tarjeta.");
        if (!val("posVoucherNumber")) throw new Error("Falta el número de cupón del posnet.");
        if (!val("authorizationCode")) throw new Error("Falta el código de autorización.");
        const last4 = val("cardLast4");
        if (last4 && !/^\d{4}$/.test(last4)) throw new Error("Los últimos 4 dígitos deben ser 4 números.");

        const payment = {
            cardType,
            cardBrand: val("cardBrand"),
            cardLast4: last4 || null,
            posVoucherNumber: val("posVoucherNumber"),
            authorizationCode: val("authorizationCode"),
        };
        if (cardType === "CREDIT") {
            const installments = parseInt(val("installments"));
            if (!installments || installments < 1 || installments > 24) {
                throw new Error("Las cuotas deben estar entre 1 y 24.");
            }
            payment.installments = installments;
        }
        return payment;
    }
    if (method === "Transferencia") {
        if (!val("transferReference")) throw new Error("Falta el número de operación de la transferencia.");
        return {
            transferReference: val("transferReference"),
            transferBank: val("transferBank") || null,
        };
    }
    return null; // efectivo
}

// Son datos de un cobro puntual: despues de facturar no deben quedar cargados
function resetPaymentFields() {
    ["cardType", "cardBrand", "cardLast4", "posVoucherNumber", "authorizationCode",
     "transferReference", "transferBank"].forEach(id => { document.getElementById(id).value = ""; });
    document.getElementById("installments").value = 1;
    updatePaymentFields();
}

// ---- Pagos: una factura puede tener varios (ej. efectivo + tarjeta) ----
const MAX_PAYMENTS = 5;
let payments = [];   // { paymentMethod, amount, ...detalle del metodo }
let totalCents = 0;  // total de la factura en centavos (lo actualiza recalcTotals)

const paymentsBody = document.getElementById("paymentsBody");
const paymentAmountInput = document.getElementById("paymentAmount");

// Se trabaja en centavos enteros para que sumar pagos no arrastre errores de coma flotante
const toCents = (n) => Math.round((n + Number.EPSILON) * 100);
const fromCents = (c) => (c / 100).toFixed(2);
const sumPaidCents = () => payments.reduce((sum, p) => sum + toCents(p.amount), 0);
const remainingCents = () => totalCents - sumPaidCents();

// Los datos del pago los escribe una persona: se escapan antes de usar innerHTML
const escapeHtml = (text) => String(text ?? "")
    .replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;")
    .replace(/"/g, "&quot;").replace(/'/g, "&#39;");

function describePayment(p) {
    if (p.paymentMethod === "Tarjeta") {
        const tipo = p.cardType === "CREDIT" ? "crédito" : "débito";
        const cuotas = p.installments > 1 ? ` · ${p.installments} cuotas` : "";
        return `${tipo} ${p.cardBrand}${cuotas} · cupón ${p.posVoucherNumber}`;
    }
    if (p.paymentMethod === "Transferencia") {
        return `op. ${p.transferReference}${p.transferBank ? " · " + p.transferBank : ""}`;
    }
    return "—";
}

function renderPayments() {
    paymentsBody.innerHTML = "";
    payments.forEach((p, idx) => {
        const row = document.createElement("tr");
        row.innerHTML = `
        <td>${escapeHtml(p.paymentMethod)}</td>
        <td>${escapeHtml(describePayment(p))}</td>
        <td>$${fromCents(toCents(p.amount))}</td>
        <td><button type="button" data-idx="${idx}" class="remove-payment-btn">Quitar</button></td>
        `;
        paymentsBody.appendChild(row);
    });
    updatePaymentSummary();
}

function updatePaymentSummary() {
    const remaining = remainingCents();
    document.getElementById("payTotalCell").textContent = `$${fromCents(totalCents)}`;
    document.getElementById("paidTotalCell").textContent = `$${fromCents(sumPaidCents())}`;

    const remainingCell = document.getElementById("remainingCell");
    remainingCell.textContent = remaining >= 0 ? `$${fromCents(remaining)}` : `Sobran $${fromCents(-remaining)}`;
    remainingCell.classList.toggle("pay-ok", remaining === 0 && payments.length > 0);
    remainingCell.classList.toggle("pay-over", remaining < 0);

    // Se sugiere el monto que falta, para el caso comun de pagar todo de una vez
    paymentAmountInput.value = remaining > 0 ? fromCents(remaining) : "";
}

document.getElementById("addPaymentBtn").addEventListener("click", () => {
    if (payments.length >= MAX_PAYMENTS) {
        alert(`Una factura admite hasta ${MAX_PAYMENTS} pagos.`);
        return;
    }
    const remaining = remainingCents();
    if (remaining <= 0) {
        alert("Los pagos ya cubren el total de la factura.");
        return;
    }
    const amount = parseFloat(paymentAmountInput.value);
    if (!amount || amount <= 0) {
        alert("Ingresá el monto de este pago.");
        return;
    }
    if (Math.abs(amount * 100 - Math.round(amount * 100)) > 1e-6) {
        alert("El monto puede tener como máximo 2 decimales.");
        return;
    }
    const amountCents = toCents(amount);
    if (amountCents > remaining) {
        alert(`El monto supera lo que falta pagar ($${fromCents(remaining)}).`);
        return;
    }

    let details;
    try {
        details = buildPaymentPayload(paymentTypeSelect.value);
    } catch (err) {
        alert(err.message);
        return;
    }

    payments.push({ paymentMethod: paymentTypeSelect.value, amount: amountCents / 100, ...details });
    resetPaymentFields();
    renderPayments();
});

paymentsBody.addEventListener("click", (e) => {
    if (e.target.classList.contains("remove-payment-btn")) {
        payments.splice(parseInt(e.target.dataset.idx), 1);
        renderPayments();
    }
});
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

    // Validar stock disponible ANTES de agregar la linea, para no
    // dejar cargar una cantidad que el backend va a rechazar igual
    // al confirmar la factura.
    const existingQty = lines
        .filter(l => l.productId === productId)
        .reduce((sum, l) => sum + l.quantity, 0);
    try {
        const stock = await apiClient(`${API_BASE}/products/${productId}/stock`).then(parseJsonOrThrow);
        if (existingQty + quantity > stock.availableStock) {
            alert(`Stock insuficiente. Disponible: ${stock.availableStock}, ya tenés ${existingQty} en la factura, estás pidiendo ${quantity} más.`);
            return;
        }
    } catch (err) {
        alert("No se pudo validar el stock: " + err.message);
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

    // Limpiar el formulario para la proxima linea - antes quedaba el
    // producto anterior seleccionado, invitando a error.
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
// Redondeo a 2 decimales igual que el backend (HALF_UP): los impuestos se
// redondean solos y despues el total, asi el total que ve el usuario es
// el mismo contra el que el servidor valida la suma de los pagos.
const round2 = (n) => Math.round((n + Number.EPSILON) * 100) / 100;

function recalcTotals() {
    const subtotal = lines.reduce((sum, l) => sum + (l.unitPrice * l.quantity), 0);
    const taxRate = parseFloat(document.getElementById("taxRate").value) || 0;
    const shippingFee = parseFloat(document.getElementById("shippingFee").value) || 0;
    const taxes = round2(subtotal * (taxRate / 100));
    const total = round2(subtotal + taxes + shippingFee);

    document.getElementById("subtotalCell").textContent = round2(subtotal).toFixed(2);
    document.getElementById("taxesCell").textContent = taxes.toFixed(2);
    document.getElementById("shippingCell").textContent = shippingFee.toFixed(2);
    document.getElementById("totalCell").textContent = total.toFixed(2);

    totalCents = toCents(total);
    updatePaymentSummary();
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

    if (payments.length === 0) {
        alert("Agregá al menos un pago.");
        return;
    }
    const remaining = remainingCents();
    if (remaining !== 0) {
        alert(remaining > 0
            ? `Todavía faltan $${fromCents(remaining)} para cubrir el total.`
            : `Los pagos superan el total por $${fromCents(-remaining)}.`);
        return;
    }

    const payload = {
        customerId: parseInt(customerId),
        shipName,
        shipAddress: document.getElementById("shipAddress").value.trim(),
        shippingFee: parseFloat (document.getElementById("shippingFee").value) || 0,
        taxRate: parseFloat(document.getElementById("taxRate").value) || 0,
        payments,
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
        orderResult.className = "po-success";
        lines = [];
        payments = [];
        renderLines();
        renderPayments();
        resetPaymentFields();
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
    updatePaymentFields();
    renderPayments();
});











