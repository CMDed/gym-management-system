document.addEventListener('DOMContentLoaded', () => {
    const membershipsGrid = document.getElementById('memberships-grid');
    const loadingMessage = document.getElementById('loading-message');
    const errorMessage = document.getElementById('error-message');
    const noMembershipsMessage = document.getElementById('no-memberships-message');

    const formatPrice = (price) => {
        return `S/ ${parseFloat(price).toFixed(2)}`;
    };

    const loadMembresias = async () => {
        loadingMessage.style.display = 'block';
        errorMessage.style.display = 'none';
        noMembershipsMessage.style.display = 'none';
        membershipsGrid.innerHTML = '';

        try {
            const response = await fetch('/api/membresias/activas');
            if (!response.ok) {
                throw new Error(`Error HTTP: ${response.status} ${response.statusText}`);
            }
            const membresias = await response.json();

            if (membresias.length === 0) {
                noMembershipsMessage.style.display = 'block';
                return;
            }

            membresias.forEach((membresia, index) => {
                const card = document.createElement('div');
                card.className = 'membership-card';

                card.style.animationDelay = `${0.2 * index + 0.8}s`;
                card.style.opacity = '0';
                card.style.transform = 'translateY(30px)';


                card.innerHTML = `
                    <h3>${membresia.nombrePlan}</h3>
                    <div class="price">${formatPrice(membresia.precio)} / ${membresia.duracionDias} días</div>
                    <ul>
                        <li>${membresia.descripcion || 'Sin descripción disponible.'}</li>
                        <li>Acceso ilimitado a instalaciones.</li>
                        <li>Clases grupales incluidas.</li>
                    </ul>
                    <a href="/registro-miembro?membershipId=${membresia.id}&membershipType=${encodeURIComponent(membresia.nombrePlan)}" class="button-secondary">
                        Inscribirse ahora
                    </a>
                `;
                membershipsGrid.appendChild(card);
            });

        } catch (error) {
            console.error('Error al obtener las membresías:', error);
            errorMessage.style.display = 'block';
        } finally {
            loadingMessage.style.display = 'none';
        }
    };

    loadMembresias();
});