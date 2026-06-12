(function () {
    if (localStorage.getItem('aryen-dark') === '1') {
        document.documentElement.classList.add('dark-pending');
    }

    document.addEventListener('DOMContentLoaded', function () {
        if (localStorage.getItem('aryen-dark') === '1') {
            document.body.classList.add('dark');
        }
        document.documentElement.classList.remove('dark-pending');

        function actualizarIcono() {
            var btns = document.querySelectorAll('.btn-dark-toggle');
            var isDark = document.body.classList.contains('dark');
            btns.forEach(function(btn) {
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
