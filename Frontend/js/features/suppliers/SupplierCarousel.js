

//opcional, si el carrusel es exclusivo de suppliers

export function renderSupplierCarousel(suppliers, onClickVerMas){
    const track = document.getElementById("carouselTrack");
    if(!track || !suppliers) return;

    track.innerHTML = "";

    suppliers.forEach(supplier => {
        const card = document.createElement("div");
        card.className = "carousel-card";

        card.innerHTML =  `
            <h3>${supplier.firstName} ${supplier.lastName}</h3>
            <p>Empresa: ${supplier.company}</p>
            <p>País: ${supplier.countryRegion}</p>    
        `;

        const btn = document.createElement("button");
        btn.textContent = "Ver más";
        btn.onclick = () => {
            window.location.href = `supplierView.html?id=${supplier.id}`;
        }
        card.appendChild(btn);

        track.appendChild(card);

    });




}






