



import { supplierService } from "../services/supplierService.js";
import { createSupplierCard } from "../features/suppliers/SupplierCard.js";
import { openSupplierModal } from "../features/suppliers/supplierModal.js";
import { renderSupplierCarousel } from "../features/suppliers/SupplierCarousel.js";

//usar lo que vendría del metodo createSupplierCard()) del backend

//funcion segura para crear elementos con contenido (evitar inyecciones html)
const createElement = (tag, className, content) => {
  const el = document.createElement(tag);
  if(className) el.className = className;
  if(content) el.textContent = content;
  return el;
}

//=== Render cards principales ===
function renderSupplierCards(suppliers) {

const container = document.getElementById('supplierGrid');
if(!container || !suppliers) return;

container.innerHTML = '';

  // === Card especial para agregar nuevo supplier ===
const addCard = document.createElement('div');
addCard.className = 'supplier-card add-card';
addCard.innerHTML = `
        <div class="add-card-content">
          <i class="fas fa-user-plus fa-3x"></i>
          <p>Agregar nuevo Supplier</p>
        </div>
`;
addCard.onclick = () => {
  openSupplierModal();//abre modal vacio para crear supplier
};
container.appendChild(addCard);

// === Cards normales de Suppliers ===
  suppliers.forEach((supplier) => {
    const card = createSupplierCard(supplier);
    container.appendChild(card);
  });
}

//=== Cargar Carrusel de recientes =====

async function loadRecentSuppliers() {
  try{
    const recentSuppliers = await supplierService.getRecentSuppliers();
    renderSupplierCarousel(recentSuppliers);
  }catch(error){
    console.error("Error al cargar proveedores recientes: ", error);
  }
}


//========================= INICIO ================================

document.addEventListener("DOMContentLoaded", async () => {
  try{
  const allSuppliers = await supplierService.getAll();
  renderSupplierCards(allSuppliers.content); //porque no es un array directo
  await loadRecentSuppliers();
  }catch(err) {
    console.error("Error cargando suppliers:", err);
  }
});

 // cuando lanzamos el evento supplierUpdate del modal recargamos toda la pagina de nuevo
document.addEventListener("supplierUpdated", async () => {
  try{
    const allSuppliers = await supplierService.getAll();
    renderSupplierCards(allSuppliers.content);

    const recentSuppliers = await supplierService.getRecentSuppliers();
    renderSupplierCarousel(recentSuppliers);


  }catch(err) {
    console.error("Error recargando suppliers: ", err);
  }
});



export { renderSupplierCards };


