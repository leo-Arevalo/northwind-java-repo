
import { authService } from "./services/authService.js";

import { apiClient } from "./services/apiClient.js";


const API_URL = 'http://localhost:8080/customers';


if(!authService.getAccessToken()){
    window.location.href = "login.html";
}

const tableBody = document.querySelector("#customersTable tbody");
const form = document.getElementById("form");
const formContainer = document.getElementById("customerForm");
const formTitle = document.getElementById("formTitle");
const newCustomerBtn = document.getElementById("newCustomerBtn");


// CARGAR CLIENTE

const loadCustomers = async () => {
    try {
        const data = await apiClient(API_URL).then(res => res.json());
        const customers = data.content || [];
        tableBody.innerHTML = "";
        customers.forEach(addCustomerRow);
    }catch(err){
        alert("Error al cargar clientes: "+ err.message);
    }
};

// AGREGAR FILA A LA TABLA
const addCustomerRow = (c) => {
    const row = document.createElement("tr");
    row.innerHTML = `
    <td>${c.id}</td>
    <td>${c.company}</td>
    <td>${c.lastName}</td>
    <td>${c.firstName}</td>
    <td>${c.email}</td>
    <td>${c.jobTitle || ''}</td>
    <td>${c.businessPhone || ''}</td>
    <td>${c.homePhone || ''}</td>
    <td>${c.faxNumber || ''}</td>
    <td>${c.address || ''}</td>
    <td>${c.city || ''}</td>
    <td>${c.stateProvince || ''}</td>
    <td>${c.postalCode || ''}</td>
    <td>${c.countryRegion || ''}</td>
    <td><a href="${c.webPage || '#'}" target="_blank">${c.webPage || ''}</a></td>
    <td>
        <button onclick = "editCustomer(${c.id})">Editar </button>
        <button onclick = "deleteCustomer(${c.id})">Eliminar</button>
    </td>
    `;
    tableBody.appendChild(row);
}
// ABRIR FORMULARIO
newCustomerBtn.addEventListener("click", () => {
    form.reset();
    document.getElementById("id").value = "";
    formTitle.textContent = "Nuevo Cliente";
    formContainer.style.display = "block";
});

//VALIDAR Y ENVIAR FORM
form.addEventListener("submit", async (e) => {
    e.preventDefault();
    const customer = {
        id: document.getElementById("id").value,
        company: document.getElementById("company").value.trim(),
        lastName: document.getElementById("lastName").value.trim(),
        firstName: document.getElementById("firstName").value.trim(),
        email: document.getElementById("email").value.trim(),
        jobTitle: document.getElementById("jobTitle").value.trim(),
        businessPhone: document.getElementById("businessPhone").value.trim(),
        homePhone: document.getElementById("homePhone").value.trim(),
        mobilePhone: document.getElementById("mobilePhone").value.trim(),
        faxNumber: document.getElementById("faxNumber").value.trim(),
        address: document.getElementById("address").value.trim(),
        city: document.getElementById("city").value.trim(),
        stateProvince: document.getElementById("stateProvince").value.trim(),
        postalCode: document.getElementById("postalCode").value.trim(),
        countryRegion: document.getElementById("countryRegion").value.trim(),
        webPage: document.getElementById("webPage").value.trim()
    }
    //validación basica
    if(!customer.company || !customer.firstName || !customer.lastName || !customer.email){
        alert("Los campos obligatorios no pueden estar vacios.");
        return;
    }
    try{
        const method = customer.id ? "PUT" : "POST";
        const url = customer.id ? `${API_URL}/${customer.id}` : API_URL;
        await apiClient(url, {
            method,
            body: JSON.stringify(customer)
        });
        formContainer.style.display = "none";
        await loadCustomers();
    }catch(err){
        alert("Error al guardar cliente: " + err.message);
    }
});

//FUNCIONES GLOBALES PARA EDITAR Y ELIMINAR
const editCustomer = async (id) => {
    try{
        const res = await apiClient(`${API_URL}/${id}`);
        const c = await res.json();
        formTitle.textContent = "Editar Cliente";
        formContainer.style.display = "block";
        Object.keys(c).forEach( key => {
            const input = document.getElementById(key);
            if(input) input.value = c[key] ?? "";
        });
    }catch(err){
        alert("Error al cargar cliente: "+ err.message);
    }
};

const deleteCustomer = async (id) => {
    if(!confirm("¿Seguro que deseas eliminar este cliente?")) return;
    try{
        await apiClient(`${API_URL}/${id}`, { method: "DELETE" });
        await loadCustomers();
    }catch(err){
        alert("Error al eliminar cliente: " +err.message);
    }
}
//EVENT DELEGATION para los botones de tabla

tableBody.addEventListener("click", (e) => {
    if(e.target.classList.contains("edit-btn")) {
        const id = e.target.dataset.id;
        editCustomer(id);
    }
    if(e.target.classList.contains("delete-btn")){
        const id = e.target.dataset.id;
        deleteCustomer(id);
    }
});


document.getElementById("logoutBtn").addEventListener("click", async () => {
    await authService.logoutFromServer();
    window.location.href = "login.html";
});

document.addEventListener("DOMContentLoaded", loadCustomers);






