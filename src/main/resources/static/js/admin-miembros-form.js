document.addEventListener('DOMContentLoaded', () => {
    const membresiaSelect = document.getElementById('membresiaId');
    const montoMembresiaDisplay = document.getElementById('montoMembresiaDisplay');
    const montoPagoSection = document.getElementById('montoPagoSection');
    const numeroTarjetaInput = document.getElementById('numeroTarjeta');
    const fechaVencimientoInput = document.getElementById('fechaVencimiento');
    const cvvInput = document.getElementById('cvv');

    const form = document.querySelector('.membresia-form');

    let selectedMembresiaPrecio = 0;

    async function fetchMembresiaPrecio(membresiaId) {
        if (!membresiaId) {
            montoMembresiaDisplay.textContent = 'Cargando...';
            montoPagoSection.style.display = 'none';
            selectedMembresiaPrecio = 0;
            return;
        }

        try {
            const response = await fetch(`/api/membresias/${membresiaId}`);
            if (!response.ok) {
                throw new Error('No se pudo obtener el precio de la membresía.');
            }
            const membresia = await response.json();

            if (membresia && typeof membresia.precio !== 'undefined') {
                selectedMembresiaPrecio = parseFloat(membresia.precio);
                montoMembresiaDisplay.textContent = `S/.${selectedMembresiaPrecio.toFixed(2)}`;
                montoPagoSection.style.display = 'block';
                numeroTarjetaInput.setAttribute('required', 'true');
                fechaVencimientoInput.setAttribute('required', 'true');
                cvvInput.setAttribute('required', 'true');

            } else {
                throw new Error('La respuesta de la membresía no contiene el precio.');
            }
        } catch (error) {
            console.error('Error al cargar el precio de la membresía:', error);
            montoMembresiaDisplay.textContent = 'Error al cargar precio.';
            montoPagoSection.style.display = 'none';
            selectedMembresiaPrecio = 0;
            numeroTarjetaInput.removeAttribute('required');
            fechaVencimientoInput.removeAttribute('required');
            cvvInput.removeAttribute('required');
        }
    }

    membresiaSelect.addEventListener('change', (event) => {
        const selectedId = event.target.value;
        fetchMembresiaPrecio(selectedId);
    });

    if (membresiaSelect.value) {
        fetchMembresiaPrecio(membresiaSelect.value);
    } else {
        montoPagoSection.style.display = 'none';
        numeroTarjetaInput.removeAttribute('required');
        fechaVencimientoInput.removeAttribute('required');
        cvvInput.removeAttribute('required');
    }

    form.addEventListener('submit', (event) => {
        if (membresiaSelect.value && selectedMembresiaPrecio > 0) {
            let isValid = true;

            if (!numeroTarjetaInput.value.match(/^[0-9]{16}$/)) {
                document.getElementById('numeroTarjetaError').textContent = 'Número de tarjeta inválido (16 dígitos).';
                document.getElementById('numeroTarjetaError').style.display = 'block';
                isValid = false;
            } else {
                document.getElementById('numeroTarjetaError').style.display = 'none';
            }

            if (!fechaVencimientoInput.value.match(/^(0[1-9]|1[0-2])\/[0-9]{2}$/)) {
                document.getElementById('fechaVencimientoError').textContent = 'Formato MM/AA inválido.';
                document.getElementById('fechaVencimientoError').style.display = 'block';
                isValid = false;
            } else {
                document.getElementById('fechaVencimientoError').style.display = 'none';
            }

            if (!cvvInput.value.match(/^[0-9]{3,4}$/)) {
                document.getElementById('cvvError').textContent = 'CVV inválido (3 o 4 dígitos).';
                document.getElementById('cvvError').style.display = 'block';
                isValid = false;
            } else {
                document.getElementById('cvvError').style.display = 'none';
            }

            if (!isValid) {
                event.preventDefault();
            }
        }
    });
});