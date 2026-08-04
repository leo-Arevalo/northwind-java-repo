

//componente visual reutilizable





export function createSupplierCard(supplier){
    const card = document.createElement("div");
    card.className = "supplier-card";

    const header = document.createElement("div");
    header.className = "card-header";
    header.innerHTML = `
        <h3>${supplier.firstName} ${supplier.lastName}</h3>
        <span>${supplier.jobTitle || ""}</span>
    `;

    const body = document.createElement("div");
    body.className = "card-body";
    body.innerHTML = `
    <div>Empresa: ${supplier.company}</div>
    <div>Teléfono: ${supplier.businessPhone}</div>
    <div>Email: ${supplier.emailAddress}</div>
    <div>País: ${supplier.countryRegion}</div>
    `;

    const footer = document.createElement("div");
    footer.className = "card-footer";


    const viewBtn = document.createElement("button");
    viewBtn.textContent = "Ver más";
    viewBtn.onclick = () => {
        window.location.href = `supplierView.html?id=${supplier.id}`;
    }

    footer.appendChild(viewBtn);
    
    card.appendChild(header);
    card.appendChild(body);
    card.appendChild(footer);

    return card;

}















