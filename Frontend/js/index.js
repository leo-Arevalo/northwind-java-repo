
import { authService } from "./services/authService.js";
import { apiClient } from "./services/apiClient";

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

    document.querySelector("#getCustomersBtn").addEventListener("click",async () => {
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
        const [customerRes, ordersRes, productsRes] = await Promise.all([
            apiClient(`${API_BASE}/customers?page=0&size=1`),
            apiClient(`${API_BASE}/orders?page=0&size=1`),
            apiClient(`${API_BASE}/products?page=0&size=1`),
        ]);
    const customerPage = await customerRes.json();
    const ordersPage = await ordersRes.json();
    const productCount = await productsRes.json();

    document.getElementById("customerCount").textContent = `Clientes: ${customerPage.totalElements}`;
    document.getElementById("orderCount").textContent = `Pedidos: ${ordersPage.totalElements}`;
    document.getElementById("productCount").textContent = `Productos: ${productsPage.totalElements}`;



    }catch(err){
        console.warn("Error cargando estadisticas:", err);
        document.getElementById("customerCount").textContent = "Clientes: --";
        document.getElementById("orderCount").textContent = "Pedidos: --";
        document.getElementById("productCount").textContent = "Productos: --";

    }
    
}









