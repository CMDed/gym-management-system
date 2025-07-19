document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('miembroForm');
    if (!form) {
        console.error("Error: Formulario con ID 'miembroForm' no encontrado.");
        return;
    }

    const idMiembroInput = document.getElementById('id');
    const modoEdicion = idMiembroInput && idMiembroInput.value !== '';

    const contrasenaInput = document.getElementById('contrasena');

    const successMessageDiv = document.querySelector('.messages.success');
    const errorMessageDiv = document.querySelector('.messages.error');

    const membresiaSelect = document.getElementById('membresiaId');
    const montoPagoSection = document.getElementById('montoPagoSection');
    const montoMembresiaDisplay = document.getElementById('montoMembresiaDisplay');
    const numeroTarjetaInput = document.getElementById('numeroTarjeta');
    const fechaVencimientoInput = document.getElementById('fechaVencimiento');
    const cvvInput = document.getElementById('cvv');

    const showMessage = (element, message, type) => {
        if (element) {
            element.textContent = message;
            element.style.display = 'block';
            element.className = `messages ${type}`;
        }
    };

    const hideMessages = () => {
        if (successMessageDiv) successMessageDiv.style.display = 'none';
        if (errorMessageDiv) errorMessageDiv.style.display = 'none';
    };

    const updateMontoPagoSection = async () => {
        const selectedMembresiaId = membresiaSelect.value;
        if (selectedMembresiaId) {
            try {
                const response = await fetch(`/api/membresias/${selectedMembresiaId}`);
                if (response.ok) {
                    const membresia = await response.json();
                    montoMembresiaDisplay.textContent = `S/ ${parseFloat(membresia.precio).toFixed(2)}`;
                    montoPagoSection.style.display = 'block';

                    numeroTarjetaInput.required = true;
                    fechaVencimientoInput.required = true;
                    cvvInput.required = true;

                } else {
                    console.error('Error al obtener el precio de la membresía:', await response.text());
                    montoMembresiaDisplay.textContent = 'Error al cargar el precio.';
                    montoPagoSection.style.display = 'none';
                    numeroTarjetaInput.required = false;
                    fechaVencimientoInput.required = false;
                    cvvInput.required = false;
                }
            } catch (error) {
                console.error('Error de red al obtener el precio de la membresía:', error);
                montoMembresiaDisplay.textContent = 'Error de conexión.';
                montoPagoSection.style.display = 'none';
                numeroTarjetaInput.required = false;
                fechaVencimientoInput.required = false;
                cvvInput.required = false;
            }
        } else {
            montoPagoSection.style.display = 'none';
            numeroTarjetaInput.required = false;
            fechaVencimientoInput.required = false;
            cvvInput.required = false;
        }
    };

    if (membresiaSelect) {
        membresiaSelect.addEventListener('change', updateMontoPagoSection);
        updateMontoPagoSection();
    }

    form.addEventListener('submit', async (event) => {
        event.preventDefault();

        hideMessages();

        const formData = new FormData(form);
        const miembroData = {};
        formData.forEach((value, key) => {
            miembroData[key] = value;
        });

        miembroData.activo = miembroData.activo === 'true';

        if (!modoEdicion && miembroData.id === '') {
            delete miembroData.id;
        }

        if (modoEdicion && miembroData.contrasena === '') {
            delete miembroData.contrasena;
        }

        const selectedMembresiaId = membresiaSelect ? membresiaSelect.value : null;
        let pagoData = null;

        if (selectedMembresiaId) {
            if (!numeroTarjetaInput.value || !fechaVencimientoInput.value || !cvvInput.value) {
                showMessage(errorMessageDiv, 'Por favor, complete todos los campos de pago para la membresía seleccionada.', 'error');
                return;
            }
            pagoData = {
                numeroTarjeta: numeroTarjetaInput.value,
                fechaVencimiento: fechaVencimientoInput.value,
                cvv: cvvInput.value
            };
            miembroData.membresiaId = parseInt(selectedMembresiaId);
        } else {
            delete miembroData.membresiaId;
            delete miembroData.numeroTarjeta;
            delete miembroData.fechaVencimiento;
            delete miembroData.cvv;
        }

        const requestBody = {
            ...miembroData,
            ...pagoData
        };

        const url = modoEdicion ? `/api/admin/miembros/${miembroData.id}` : '/api/admin/miembros';
        const method = modoEdicion ? 'PUT' : 'POST';

        try {
            const response = await fetch(url, {
                method: method,
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(requestBody)
            });

            if (response.ok) {
                const responseData = await response.json();
                showMessage(successMessageDiv, `¡Miembro ${modoEdicion ? 'actualizado' : 'registrado'} exitosamente!`, 'success');

                if (!modoEdicion && responseData.inscripcionId) {
                    setTimeout(() => {
                        window.location.href = `/admin/miembros/ver`;
                    }, 2000);
                } else {
                     setTimeout(() => {
                        window.location.href = `/admin/miembros/ver`;
                    }, 2000);
                }

            } else {
                const errorBody = await response.json();
                let errorMessage = `Error al ${modoEdicion ? 'actualizar' : 'registrar'} miembro: `;

                if (errorBody && typeof errorBody === 'object') {
                    const validationErrors = Object.values(errorBody).join(', ');
                    errorMessage += ` Detalles: ${validationErrors}`;
                } else if (errorBody && errorBody.message) {
                    errorMessage += errorBody.message;
                } else {
                    errorMessage += response.statusText;
                }
                showMessage(errorMessageDiv, errorMessage, 'error');
            }
        } catch (error) {
            console.error('Error de red o del servidor:', error);
            showMessage(errorMessageDiv, 'Ocurrió un error inesperado. Por favor, intenta de nuevo.', 'error');
        }
    });

    if (modoEdicion && idMiembroInput.value) {
    }
});