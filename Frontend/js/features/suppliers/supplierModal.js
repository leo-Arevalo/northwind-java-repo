
//modal para crear/editar

import { supplierService } from "../../services/supplierService.js";
import { renderSupplierCarousel } from "./SupplierCarousel.js";
import { renderSupplierCards } from "../../views/suppliers.js";

let modalInjected = false;

export function injectSupplierModal() {

    //verificamos si ya existe el link a modal.css
    if(!document.querySelector("link[href='../css/modal.css']")){
        const link = document.createElement("link");
        link.rel = "stylesheet";
        link.href = "../css/modal.css";
        document.head.appendChild(link);
    }



    if(modalInjected) return;
    modalInjected = true;

    const modalHTML = `
    <div class="modal" id="supplierFormModal">
    <div class="modal-content">
    <span class="close" id="closeSupplierModal">&times;</span>
    <h2 id="modalTitle">Supplier</h2>
    <form id="supplierForm">
        <input type="hidden" id="id" name="id" />

        <label>Empresa *</label>
        <input type="text" id="company" name="company" maxlength="50" required />

        <label>Nombre *</label>
        <input type="text" id="firstName" name="firstName" maxlength="50"required />

        <label>Apellido *</label>
            <input type="text" id="lastName" name="lastName" maxlength="50" required />

            <label>Email *</label>
            <input type="email" id="emailAddress" name="emailAddress" maxlength="50" required />

            <label>Cargo</label>
            <input type="text" id="jobTitle" name="jobTitle" maxlength="50" />

            <label>Teléfono Trabajo</label>
            <input type="text" id="businessPhone" name="businessPhone" maxlength="20" placeholder="Ej: +54 11 4567-8901" />

            <label>Teléfono Casa</label>
            <input type="text" id="homePhone" name="homePhone" maxlength="20" placeholder="Ej: +54 11 4567-8901" />

            <label>Teléfono Móvil</label>
            <input type="text" id="mobilePhone" name="mobilePhone" maxlength="20" placeholder="Ej: +54 11 4567-8901" />

            <label>Fax</label>
            <input type="text" id="faxNumber" name="faxNumber" maxlength="20"  placeholder="Ej: +54 11 4567-8901" />

            <label>Dirección</label>
            <textarea id="address" name="address" maxlength="255"></textarea>

            <label>Ciudad</label>
            <input type="text" id="city" name="city" maxlength="50" />

            <label>Provincia/Estado</label>
            <input type="text" id="stateProvince" name="stateProvince" maxlength="50" />

            <label>Código Postal</label>
            <input type="text" id="zipPostalCode" name="zipPostalCode" maxlength="15" pattern="^\\d{4,15}$" />

            <label>País/Región</label>
            <input type="text" id="countryRegion" name="countryRegion" maxlength="50" />

            <label>Web</label>
            <input type="url" id="webPage" name="webPage" />

            <label>Notas</label>
            <textarea id="notes" name="notes" maxlength="255"></textarea>

            <div class="error-message" id="formError"></div>
            <button type="submit" class="btn">Guardar</button>
        </form>
      </div>
    </div>
    `;

    document.body.insertAdjacentHTML("beforeend", modalHTML);

    //cerrar modal con la x
    document.getElementById("closeSupplierModal").onclick =closeSupplierModal;

    //Manejo del submit
    document.getElementById("supplierForm").onsubmit = handleSubmit;

}

export function openSupplierModal(supplier = null) {
    injectSupplierModal();
    const modal = document.getElementById("supplierFormModal");
    const form = document.getElementById("supplierForm");
    const errorDiv = document.getElementById("formError");
    errorDiv.textContent = "";
    form.reset();

    if(supplier){
        document.getElementById("id").value = supplier.id;
        document.getElementById("company").value = supplier.company;
        document.getElementById("firstName").value = supplier.firstName;
        document.getElementById("lastName").value = supplier.lastName;
         document.getElementById("emailAddress").value = supplier.emailAddress;
        document.getElementById("jobTitle").value = supplier.jobTitle || "";
        document.getElementById("businessPhone").value = supplier.businessPhone || "";
        document.getElementById("homePhone").value = supplier.homePhone || "";
        document.getElementById("mobilePhone").value = supplier.mobilePhone || "";
        document.getElementById("faxNumber").value = supplier.faxNumber || "";
        document.getElementById("address").value = supplier.address || "";
        document.getElementById("city").value = supplier.city || "";
        document.getElementById("stateProvince").value = supplier.stateProvince || "";
        document.getElementById("zipPostalCode").value = supplier.zipPostalCode || "";
        document.getElementById("countryRegion").value = supplier.countryRegion || "";
        document.getElementById("webPage").value = supplier.webPage || "";
        document.getElementById("notes").value = supplier.notes || "";
        document.getElementById("modalTitle").textContent = "Editar Supplier";
    } else {
        document.getElementById("modalTitle").textContent = "Nuevo Supplier";
    }
    modal.style.display = "flex";
}

export function closeSupplierModal() {
    const modal = document.getElementById("supplierFormModal");
    if(modal) modal.style.display = "none";
}

async function handleSubmit(e) {
    e.preventDefault();
   
    const errorDiv = document.getElementById("formError");
    errorDiv.textContent = "";
    const form = e.target;

    // 1. Validación HTML5
    if(!form.checkValidity()){
        errorDiv.textContent = "⚠️ Faltan completar algunos campos obligatorios o hay datos inválidos.";
        return;
    }
    // 2. Validaciones custom
    const supplierData = Object.fromEntries(new FormData(form).entries());
    if(supplierData.company.trim().length < 2){
        errorDiv.textContent = "⚠️ La empresa debe tener al menos 2 caracteres.";
        return;
    }

    if(!supplierData.emailAddress.includes("@")){
        errorDiv.textContent = "⚠️ El email ingresado no es válido.";
        return;
    }
    const id = supplierData.id;
    

    try{
        if(id) { // === EDITAR ===
            await supplierService.update(id, supplierData);
            alert("Proveedor actualizado con éxito ✅");
        } else {
            // === CREAR ===
            await supplierService.create(supplierData);
            alert("Proveedor credo con exito ✅");
        }
        closeSupplierModal();
        //    const allSuppliers = await supplierService.getAll();
        //    const recentSuppliers = await supplierService.getRecentSuppliers();
        //    renderSupplierCards(allSuppliers.content);
        //    renderSupplierCarousel(recentSuppliers.content);
        
        // Disparamos evento global
        document.dispatchEvent(new CustomEvent("supplierUpdated", {
            detail: { id }
        }));

    }catch(err){
        console.error("error guardando supplier:", err);
        errorDiv.textContent = "❌ Error al guardar el proveedor. Verifique los datos.";
    }

}




