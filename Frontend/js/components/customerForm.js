import { apiClient } from "../services/apiClient.js";

const API_URL = 'http://localhost:8080/customers';

/**
 * Componente reutilizable de alta/edición de cliente.
 * 
 * No asume DONDE se usa: no recarga tablas ni redirige por su cuenta.
 * Quien lo monta decide que pasa despues de guardar via el callback onSaved.
 * 
 * Ejemplo en la pantalla de Clientes:
 *      const customerForm = initCustomerForm
 *          formContainer: document.getElementById('customerForm'),
 *          onSaved: () => loadCustomers(),
 *      });
 *      newCustomerBtn.addEventListener('click', () => customerForm.openNew());
 * 
 *  Ejemplo futuro en Facturación (mismo componente, distion callback):
 *      const customerForm = initCustomerForm ({
 *          formContainer: document.getElementById('quickCustomerModal'),
 *          onSaved: (clienteCreado) => seleccionarClienteEnFactura(clienteCreado)
 *      });
 *  
 */

export function initCustomerForm({ formContainer, onSaved }) {
    const form = formContainer.querySelector("form");
    const formTitle = formContainer.querySelector("#formTitle");

    const fieldIds = [
        "id", "company", "lastName", "firstName", "email", "jobTitle",
        "businessPhone", "homePhone", "mobilePhone", "faxNumber",
        "address", "city", "stateProvince", "postalCode", "countryRegion", "webPage"
    ];

    const getField = (id) => formContainer.querySelector(`#${id}`);

    async function openNew() {
        form.reset();
        getField("id").value = "";
        if(formTitle) formTitle.textContent = "Nuevo Cliente";
        formContainer.style.display = "block";
    }

    async function openEdit(id) {    
        try{
            const res = await apiClient('${API_URL}/${id}');
            const c = await res.json();
            if(formTitle) formTitle.textContent = "Editar Cliente";
            formContainer.style.display = "block";
            fieldIds.forEach((key) => {
                const input = getField(key);
                if(input) input.value = c[key] ?? "";
            });
        }catch(err){
            alert("Error al cargar cliente: "+err.message);
        }
    }

    function close(){
        formContainer.style.display = "none";
    }

    async function handleSubmit(e){
        e.preventDefault();
        const customer = {};
        fieldIds.forEach((key) => {
            const input = getField(key);
            customer[key] = input ? input.value.trim() : "";
        });

        if(!customer.company || !customer.firstName || !customer.lastName || !customer.email) {
            alert("Los campos obligatorios no pueden estar vacios.");
            return;
        }
        try{
            const method = customer.id ? "PUT" : "POST";
            const url = customer.id ? `${API_URL}/${customer.id}` : API_URL;
            const res = await apiClient(url, {
                method,
                body: JSON.stringify(customer)
            });
            const savedCustomer = await res.json();
            close();
            if(onSaved) onSaved(savedCustomer);
        }catch(err){
            alert("Error al guardar cliente: "+ err.message);
        }
    }

    async function remove(id) {
        if(!confirm("¿Seguro que deseas eliminar este cliente?")) return;
        try{
            await apiClient(`${API_URL}/${id}`, { method: "DELETE" });
            if(onSaved) onSaved(null);
        }catch(err){
            alert("Error al eliminar cliente: "+err.message);
        }
    }
    form.addEventListener("submit", handleSubmit);
    const cancelBtn = formContainer.querySelector("[data-action='cancel']");
    if(cancelBtn) cancelBtn.addEventListener("click",close);

    return { openNew, openEdit, close, remove };
}




















