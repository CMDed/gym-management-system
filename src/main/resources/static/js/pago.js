document.addEventListener('DOMContentLoaded', async () => {
    const pagoForm = document.getElementById('pagoForm');
    const miembroId = document.getElementById('miembroId').value;
    const inscripcionMembresiaId = document.getElementById('inscripcionMembresiaId').value; // <-- ¡AHORA ES EL ID DE INSCRIPCIÓN!

    const pagoSuccessMessage = document.getElementById('pagoSuccessMessage');
    const pagoErrorMessage = document.getElementById('pagoErrorMessage');
    const montoPagarDisplay = document.getElementById('montoPagarDisplay'); // Nuevo elemento para mostrar el monto

    let montoMembresia = 0; // Variable para almacenar el monto real

    // --- Paso 1: Obtener los detalles de la inscripción de membresía para obtener el monto ---
    if (inscripcionMembresiaId) {
        try {
            const inscripcionResponse = await fetch(`/api/inscripciones/${inscripcionMembresiaId}`);
            if (!inscripcionResponse.ok) {
                throw new Error('No se pudo obtener los detalles de la inscripción de membresía.');
            }
            const inscripcionData = await inscripcionResponse.json();

            // Asegúrate de que tu entidad Membresia en el backend tiene 'precio'
            if (inscripcionData.membresia && inscripcionData.membresia.precio) {
                montoMembresia = inscripcionData.membresia.precio;
                montoPagarDisplay.textContent = `$${parseFloat(montoMembresia).toFixed(2)}`; // Muestra el monto
            } else {
                throw new Error('No se pudo obtener el precio de la membresía desde la inscripción.');
            }

        } catch (error) {
            console.error('Error al cargar datos de la membresía:', error);
            pagoErrorMessage.textContent = 'Error al cargar los detalles de la membresía. Por favor, intenta más tarde.';
            pagoErrorMessage.style.display = 'block';
            pagoForm.querySelector('button[type="submit"]').disabled = true; // Deshabilitar el botón de pago
            return; // Detener la ejecución si no se puede obtener el monto
        }
    } else {
        pagoErrorMessage.textContent = 'ID de inscripción de membresía no proporcionado. No se puede procesar el pago.';
        pagoErrorMessage.style.display = 'block';
        pagoForm.querySelector('button[type="submit"]').disabled = true;
        return;
    }


    pagoForm.addEventListener('submit', async (event) => {
        event.preventDefault();

        pagoSuccessMessage.style.display = 'none';
        pagoErrorMessage.style.display = 'none';

        const numeroTarjeta = document.getElementById('numeroTarjeta').value;
        const fechaVencimiento = document.getElementById('fechaVencimiento').value;
        const cvv = document.getElementById('cvv').value;

        const pagoData = {
            miembroId: parseInt(miembroId),
            inscripcionMembresiaId: parseInt(inscripcionMembresiaId), // <-- ¡YA ES EL REAL!
            monto: montoMembresia, // <-- ¡USAMOS EL MONTO REAL OBTENIDO!
            metodoPago: "Tarjeta de Crédito/Débito",
            estado: "COMPLETADO" // Establecer un estado para el pago
        };

        try {
            const response = await fetch('/api/pagos/procesar', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(pagoData)
            });

            if (response.ok) {
                const pagoConfirmado = await response.json();
                pagoSuccessMessage.textContent = `¡Pago #${pagoConfirmado.id} procesado exitosamente! Redirigiendo al inicio de sesión...`;
                pagoSuccessMessage.style.display = 'block';

                setTimeout(() => {
                    window.location.href = `/login`;
                }, 2000);
            } else {
                const errorBody = await response.json();
                let errorMessage = `Error ${response.status}: ${errorBody.message || 'Error desconocido'}`;
                if (errorBody.errors) {
                    errorMessage += " Detalles: " + Object.values(errorBody.errors).join(", ");
                }
                pagoErrorMessage.textContent = `Error al procesar pago: ${errorMessage}`;
                pagoErrorMessage.style.display = 'block';
            }
        } catch (error) {
            console.error('Error de red o del servidor:', error);
            pagoErrorMessage.textContent = 'Ocurrió un error inesperado al procesar el pago. Por favor, intenta de nuevo.';
            pagoErrorMessage.style.display = 'block';
        }
    });
});