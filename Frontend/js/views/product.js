import { authService } from "../services/authService.js";
import { apiClient } from "../services/apiClient.js";

const API_URL = 'http://localhost:8080/products';

if(!authService.getAccessToken()) {
    window.location.href = "login.html";
}

const tableBody = document.querySelector("#productsTable tbody");
const form = document.getElementById("form");
const formContainer = document.getElementById("productForm");
const formTitle = document.getElementById("formTitle");
const newProductBtn = document.getElementById("newProductBtn");

//CARGAR PRODUCTOS

const loadProducts = async () => {
    try{
        const data = await apiClient(API_URL).then(res => res.json());
        const products = data.content || [];
        tableBody.innerHTML = "";
        products.forEach(addProductRow);
    }catch(err){
        alert("Error al cargar productos: "+ err.message);
    }
}

//AGREGAR FILA A LA TABLA

const addProductRow = (p) => {
    const row = document.createElement("tr");
    row.innerHTML=`
    <td>${p.productID}</td>
    <td>${p.productCode || ''}</td>
    <td>${p.productName}</td>
    <td>${p.category || ''}</td>
    <td>${p.standardCost ?? ''}</td>
    
    <td>${p.listPrice ?? ''}</td>
    <td>${p.reorderLevel ?? ''}</td>
    <td>${p.discontinued ? 'Si' : 'No'}</td>
    <td>
        <button class="edit-btn" data-id="${p.productID}">Editar</button>
        <button class="delete-btn" data-id="${p.productID}">Eliminar</button>
    </td>
    `;
    tableBody.appendChild(row);
};

//ABRIR FORMULARIO
newProductBtn.addEventListener("click", () => {
    form.reset();
    document.getElementById("productID").value="";
    formTitle.textContent = "Nuevo Producto";
    formContainer.style.display = "block";
});
 //VALIDAR Y ENVIAR FORMULARIO
 form.addEventListener("submit", async (e) => {
    e.preventDefault();
        const id = document.getElementById("productID").value;
        const product = {
            productCode: document.getElementById("productCode").value.trim(),
            productName: document.getElementById("productName").value.trim(),
            category: document.getElementById("category").value.trim(),
            quantityPerUnit: document.getElementById("quantityPerUnit").value.trim(),
            standardCost:parseFloat(document.getElementById("standardCost").value),
            listPrice: parseFloat(document.getElementById("listPrice").value),
            reorderLevel: document.getElementById("reorderLevel").value ? parseInt(document.getElementById("reorderLevel").value) : null,
            targetLevel: document.getElementById("targetLevel").value ? parseInt (document.getElementById("targetLevel").value) : null,
            minimumReorderQuantity: document.getElementById("minimumReorderQuantity").value ? parseInt(document.getElementById("minimumReorderQuantity").value) : null,
            description: document.getElementById("description").value.trim(),
            discontinued: document.getElementById("discontinued").checked
        };
    if(!product.productName || Number.isNaN(product.standardCost) || Number.isNaN(product.listPrice)) {
        alert("Nombre, costo y precio son obligatorios.");
        return;
    }    

    try{
        const method = id ? "PUT" : "POST";
        const url = id ? `${API_URL}/${id}` : API_URL;
        if(id) product.productID = parseInt(id);
        await apiClient(url, {
            method,
            body: JSON.stringify(product)
        });
        formContainer.style.display = "none";
        await loadProducts();
    }catch(err){
        alert("Error al guardar producto: "+ err.message);
    }
 });
//FUNCIONES GLOBALES PARA EDITAR Y ELIMINAR
const editProduct = async (id) => {
    try{
        const res = await apiClient(`${API_URL}/${id}`);
        const p = await res.json();
        formTitle.textContent = "Editar Producto";
        formContainer.style.display = "block";
        document.getElementById("productID").value=p.productID;
        document.getElementById("productCode").value = p.productCode ?? "";
        document.getElementById("productName").value = p.productName ?? "";
        document.getElementById("category").value = p.category ?? "";
        document.getElementById("quantityPerUnit").value = p.quantityPerUnit ?? "";
        document.getElementById("standardCost").value = p.standardCost ?? "";
        document.getElementById("listPrice").value = p.listPrice ?? "";
        document.getElementById("reorderLevel").value = p.reorderLevel ?? "";
        document.getElementById("targetLevel").value = p.targetLevel ?? "";
        document.getElementById("minimumReorderQuantity").value = p.minimumReorderQuantity ?? "";
        document.getElementById("description").value = p.description ?? "";
        document.getElementById("discontinued").checked = !!p.discontinued;
    }catch(err) {
        alert("Error al cargar producto: "+err.message);
    }
};
const deleteProduct = async (id) => {
    if(!confirm("¿Desea eliminar este producto?")) return;
    try{
        await apiClient(`${API_URL}/${id}`, { method: "DELETE" });
        await loadProducts();
    }catch(err){
        alert("Error al eliminar producto: "+err.message);
    }
};

//DELEGACIÓN DE EVENTOS PARA LOS BOTONES DE LA TABLA
tableBody.addEventListener("click", (e) => {
    const id = e.target.dataset.id;
    if(!id) return;
    if(e.target.classList.contains("edit-btn")){
        editProduct(id);
    }
    if(e.target.classList.contains("delete-btn")) {
        deleteProduct(id);
    }
});
document.getElementById("logoutBtn").addEventListener("click", async () => {
    await authService.logoutFromServer();
    window.location.href = "login.html";
});
document.addEventListener("DOMContentLoaded", loadProducts);



