
import { authService } from "./services/authService.js";
import { apiClient, parseJsonOrThrow } from "./services/apiClient.js";


const API_BASE = "http://localhost:8080";

//esperamos que cargue la pagina por completo
document.addEventListener("DOMContentLoaded", () => {
    initPage();
});

async function initPage(){
    //proteger acceso
    const token = authService.getAccessToken();
    if(!token) {
        window.location.href = "views/login.html";
        return;
    }
    //configurar eventos
    document.getElementById("logoutBtn").addEventListener("click", async () => {
        await authService.logoutFromServer();
        window.location.href = "views/login.html";
    });

    document.querySelector("#getCustomersBtn").addEventListener("click", () => {
        window.location.href = "views/customers.html";
    });
   
    //cargar estadisticas iniciales
    loadStats();
}
async function loadCustomers(){
    try{
        const response = await apiClient("http://localhost:8080/customers");
        const customer = response.json();
    }catch(err){
        alert("Error al obtener cliente: "+err.message);
    }
}


async function loadStats() {
    try{
        //No hay endpoints /count en el backend todavia, asi que usamos
        //la paginación (page = 0 & size = 1) y leemos "totalElements"
        const [customerPag, ordersPag, productsPag] = await Promise.all([
            apiClient(`${API_BASE}/customers?page=0&size=1`).then(parseJsonOrThrow),
            apiClient(`${API_BASE}/orders?page=0&size=1`).then(parseJsonOrThrow),
            apiClient(`${API_BASE}/products?page=0&size=1`).then(parseJsonOrThrow),
        ]);

    document.getElementById("customerCount").textContent = customerPage.totalElements;
    document.getElementById("orderCount").textContent = ordersPage.totalElements;
    document.getElementById("productCount").textContent = productsPage.totalElements;

    }catch(err){
        console.warn("Error cargando estadisticas:", err);
        document.getElementById("customerCount").textContent = "--";
        document.getElementById("orderCount").textContent = "--";
        document.getElementById("productCount").textContent = "--";

    }
    
}









