/**
 * Se ejecuta INMEDIATAMENTE (no espera DOMContentLoaded)
 * para evitar el flash blanco al cargar en modo oscuro.
 */
(function () {
    // Aplicar fondo oscuro en <html> antes de que el browser pinte
    if (localStorage.getItem('aryen-dark') === '1') {
        document.documentElement.classList.add('dark-pending');
    }

    // Una vez cargado el DOM, mover la clase a <body> y activar botones
    document.addEventListener('DOMContentLoaded', function () {
        if (localStorage.getItem('aryen-dark') === '1') {
            document.body.classList.add('dark');
        }
        // Quitar la clase del html (ya no la necesitamos)
        document.documentElement.classList.remove('dark-pending');

        function actualizarIcono() {
            var isDark = document.body.classList.contains('dark');
            document.querySelectorAll('.btn-dark-toggle').forEach(function (btn) {
                btn.textContent = isDark ? '☀️' : '🌙';
                btn.title = isDark ? 'Cambiar a modo claro' : 'Cambiar a modo oscuro';
            });
        }

        document.querySelectorAll('.btn-dark-toggle').forEach(function (btn) {
            btn.addEventListener('click', function () {
                document.body.classList.toggle('dark');
                var isDark = document.body.classList.contains('dark');
                localStorage.setItem('aryen-dark', isDark ? '1' : '0');
                actualizarIcono();
            });
        });

        actualizarIcono();
    });
})();
