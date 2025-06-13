document.addEventListener('DOMContentLoaded', () => {
    console.log("Admin Dashboard cargado.");
    // Ejemplo de cómo cargar miembros desde tu API REST
    fetch('/api/miembros') // Llama a tu endpoint REST
        .then(response => response.json())
        .then(data => {
            const miembrosListDiv = document.getElementById('miembros-list');
            if (data && data.length > 0) {
                const ul = document.createElement('ul');
                data.forEach(miembro => {
                    const li = document.createElement('li');
                    li.textContent = `DNI: ${miembro.dni}, Nombre: ${miembro.nombre} ${miembro.apellido}, Email: ${miembro.email}`;
                    ul.appendChild(li);
                });
                miembrosListDiv.appendChild(ul);
            } else {
                miembrosListDiv.textContent = "No hay miembros registrados.";
            }
        })
        .catch(error => {
            console.error('Error al cargar miembros:', error);
            document.getElementById('miembros-list').textContent = "Error al cargar los miembros.";
        });
});