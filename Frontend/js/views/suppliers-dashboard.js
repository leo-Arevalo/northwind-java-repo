
import { supplierService } from "../services/supplierService.js";
import { purchaseOrderService } from "../services/purchaseOrderService.js";

function renderKPIs(totalSuppliers, totalOrders, totalPaid) {
    document.getElementById("supplierKPIs").innerHTML =`
    <div class="kpi-card"><h3>${totalSuppliers}</h3><p>Proveedores</p></div>
    <div class="kpi-card"><h3>${totalOrders}</h3><p>Órdenes</p></div>
    <div class="kpi-card"><h3>${totalPaid.toFixed(2)}</h3><p>Total pagado</p></div>
    `;
}
 // === Render Chart ===
function renderChart(canvasId, label, labels, data, type="bar") {
    const ctx = document.getElementById(canvasId).getContext("2d");
    new Chart(ctx, {
        type, data: {labels, datasets: [{label, data, backgroundColor: ["#1a73e8", "#34a853", "#fbbc05","#ea4335", "#9c27b0"],
            borderRadius: 6,
        },],},
        options: {
            responsive: true,
            plugins: {
                legend: {display: type !== "bar" ? true : false },
            },
            scales: {
                y: {beginAtZero: true },
            },
        },
    });
}
// === Cargar Datos ===
document.addEventListener("DOMContentLoaded", async () => {
    try{
    const suppliers = (await supplierService.getAll()).content;
    const orders = (await purchaseOrderService.getAll()).content;
    //KPIs
    const totalPaid = orders.reduce((sum, o) => sum + (o.paymentAmount || 0), 0);
    renderKPIs(suppliers.length, orders.length, totalPaid);

    //Chart: pagado por vendedor
    renderChart("chartBySupplier",
                suppliers.map(s=>s.company),
                suppliers.map(s=>orders.filter(o=>o.supplierId === s.id)
                                .reduce((sum,o)=>sum+(o.paymentAmount||0),0)
    ),
    "Total pagado por proveedor."
);
    // Charts: pagado por pais
    const byCountry = suppliers.reduce((acc, s) => {
        const paid = orders
        .filter(o=>o.supplierId === s.id)
        .reduce((sum,o) => sum + (o.paymentAmount || 0), 0);
        acc[s.countryRegion] = (acc[s.countryRegion] || 0) + paid;
        return acc;
    },{});
    renderChart("chartByCountry", Object.keys(byCountry), Object.values(byCountry), "Total por país", "doughnut" );

     // Chart: Evolución mensual
    const monthly = orders.reduce((acc, o) => {
      const month = o.creationDate?.substring(0, 7) || "Sin fecha";
      acc[month] = (acc[month] || 0) + (o.paymentAmount || 0);
      return acc;
    }, {});
    renderChart(
      "chartMonthly",
      Object.keys(monthly),
      Object.values(monthly),
      "Total mensual",
      "line"
    );
  } catch (err) {
    console.error("Error cargando dashboard:", err);
  }


});






