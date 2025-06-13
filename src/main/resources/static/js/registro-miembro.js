document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('registroMiembroForm');
    const passwordInput = document.getElementById('contrasena');
    const confirmPasswordInput = document.getElementById('confirmarContrasena');
    const passwordMatchError = document.getElementById('passwordMatchError');
    const registrationSuccessMessage = document.getElementById('registrationSuccessMessage');
    const registrationErrorMessage = document.getElementById('registrationErrorMessage');

    const validatePasswords = () => {
        if (passwordInput.value !== confirmPasswordInput.value) {
            passwordMatchError.style.display = 'block';
            confirmPasswordInput.setCustomValidity('Las contraseñas no coinciden.');
            return false;
        } else {
            passwordMatchError.style.display = 'none';
            confirmPasswordInput.setCustomValidity('');
            return true;
        }
    };

    passwordInput.addEventListener('input', validatePasswords);
    confirmPasswordInput.addEventListener('input', validatePasswords);


    form.addEventListener('submit', async (event) => {
        event.preventDefault();

        registrationSuccessMessage.style.display = 'none';
        registrationErrorMessage.style.display = 'none';

        if (!validatePasswords()) {
            return;
        }

        const formData = new FormData(form);
        const data = {};
        formData.forEach((value, key) => {
            data[key] = value;
        });

        const miembroData = {
            tipoIdentificacion: data.tipoIdentificacion,
            numeroIdentificacion: data.numeroIdentificacion,
            nombre: data.nombre,
            apellido: data.apellido,
            correo: data.correo,
            telefono: data.telefono || null,
            sexo: data.sexo,
            fechaNacimiento: data.fechaNacimiento,
            contrasena: data.contrasena,
            activo: true,
            membresiaId: parseInt(document.getElementById('selectedMembershipId').value) // Asegúrate de obtenerlo del hidden input
        };

        // NO NECESITAMOS 'const membresiaId = data.membresiaId;' aquí, ya está en miembroData

        try {
            const response = await fetch('/api/miembros/registrar', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(miembroData)
            });

            if (response.ok) {
                // *** ¡CAMBIO CRÍTICO AQUÍ! ***
                const registroResponse = await response.json(); // Esperamos el { miembroId, inscripcionId, mensaje }

                registrationSuccessMessage.textContent = `¡Registro exitoso! Redirigiendo a la página de pago...`;
                registrationSuccessMessage.style.display = 'block';

                setTimeout(() => {
                    // Redirige usando el inscripcionId devuelto por el backend
                    window.location.href = `/pago?miembroId=${registroResponse.miembroId}&inscripcionMembresiaId=${registroResponse.inscripcionId}`;
                }, 2000);
            } else {
                const errorBody = await response.json();
                let errorMessage = 'Error desconocido al registrar.';
                if (errorBody && errorBody.message) {
                    errorMessage = errorBody.message;
                }
                if (errorBody && errorBody.errors) {
                    const validationErrors = Object.values(errorBody.errors).join(', ');
                    errorMessage += ` Detalles: ${validationErrors}`;
                }
                registrationErrorMessage.textContent = `Error al registrar: ${errorMessage}`;
                registrationErrorMessage.style.display = 'block';
            }
        } catch (error) {
            console.error('Error de red o del servidor:', error);
            registrationErrorMessage.textContent = 'Ocurrió un error inesperado. Por favor, intenta de nuevo.';
            registrationErrorMessage.style.display = 'block';
        }
    });
});