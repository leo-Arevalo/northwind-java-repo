
import { authService } from "./services/authService";
import { apiClient } from "./services/apiClient";

//esperamos que cargue la pagina por completo
document.addEventListener("DOMContentLoaded", () => {
    initPage();
});

async function initPage(){
    //proteger acceso
    const token = authService.getAccessToken();
    if(!token) {
        window.location.href = "login.html";
        return;
    }
    //configurar eventos
    document.getElementById("logoutBtn").addEventListener("click", async () => {
        await authService.logoutFromServer();
        window.location.href = "login.html";
    });

    document.querySelector("#getCustomersBtn").addEventListener("click",loadCustomers);
   
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
        const [customerRes, ordersRes, productsRes] = await Promise.all([
            apiClient("https://localhost:8443/customers/count"),
            apiClient("https://localhost:8443/orders/count"),
            apiClient("https://localhost:8443/products/count"),
        ]);
    const customerCount = await customerRes.json();
    const orderCount = await ordersRes.json();
    const productCount = await productsRes.json();

    document.getElementById("customerCount").textContent = `Clientes: ${customerCount}`;
    document.getElementById("orderCount").textContent = `Pedidos: ${orderCount}`;
    document.getElementById("productCount").textContent = `Productos: ${productCount}`;



    }catch(err){
        alert("Error cargando estadisticas: " + err.message);
    }
    
}









