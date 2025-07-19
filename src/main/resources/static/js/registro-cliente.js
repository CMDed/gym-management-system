document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('registroClienteForm');
    if (!form) {
        console.error("Error: Formulario con ID 'registroClienteForm' no encontrado.");
        return;
    }

    const passwordInput = document.getElementById('contrasena');
    const confirmPasswordInput = document.getElementById('confirmarContrasena');
    const passwordMatchError = document.getElementById('passwordMatchError');
    const registrationSuccessMessage = document.getElementById('registrationSuccessMessage');
    const registrationErrorMessage = document.getElementById('registrationErrorMessage');
    const selectedMembershipIdInput = document.getElementById('selectedMembershipId');

    const showMessage = (element, message, type) => {
        if (element) {
            element.textContent = message;
            element.style.display = 'block';
            element.className = `messages ${type}`;
        }
    };

    const hideMessages = () => {
        passwordMatchError.style.display = 'none';
        registrationSuccessMessage.style.display = 'none';
        registrationErrorMessage.style.display = 'none';
        registrationSuccessMessage.textContent = '';
        registrationErrorMessage.textContent = '';
    };

    form.addEventListener('submit', async function(event) {
        event.preventDefault();

        hideMessages();

        if (passwordInput.value !== confirmPasswordInput.value) {
            showMessage(passwordMatchError, 'Las contraseñas no coinciden.', 'error');
            return;
        }

        const miembroData = {
                tipoIdentificacion: document.getElementById('tipoIdentificacion').value,
                numeroIdentificacion: document.getElementById('numeroIdentificacion').value,
                nombre: document.getElementById('nombre').value,
                apellido: document.getElementById('apellido').value,
                correo: document.getElementById('correo').value,
                telefono: document.getElementById('telefono').value,
                sexo: document.getElementById('sexo').value,
                fechaNacimiento: document.getElementById('fechaNacimiento').value,
                contrasena: passwordInput.value,
                activo: true,
                membresiaId: selectedMembershipIdInput && selectedMembershipIdInput.value ? parseInt(selectedMembershipIdInput.value) : null
            };

        for (const key in miembroData) {
            if (miembroData[key] === null || miembroData[key] === '') {
                delete miembroData[key];
            }
        }

        try {
            const response = await fetch('/api/miembros/registrar', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(miembroData)
            });

            const data = await response.json();

            if (response.ok) {
                showMessage(registrationSuccessMessage, '¡Registro exitoso! Ahora puedes iniciar sesión.', 'success');
                form.reset();
                setTimeout(() => {
                    window.location.href = '/login?registrationSuccess=true';
                }, 2000);
            } else {
                let errorMessage = 'Error al registrar el miembro.';
                if (data && typeof data === 'string') {
                    errorMessage = data;
                } else if (data && data.message) {
                    errorMessage = data.message;
                }
                showMessage(registrationErrorMessage, errorMessage, 'error');
            }
        } catch (error) {
            console.error('Error en la solicitud:', error);
            showMessage(registrationErrorMessage, 'Ocurrió un error inesperado. Por favor, intenta de nuevo.', 'error');
        }
    });
});