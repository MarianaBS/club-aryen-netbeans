(function () {
    var overlay = document.createElement('div');
    overlay.className = 'modal-overlay';
    overlay.innerHTML = `
        <div class="modal-box">
            <h3 id="modal-title">¿Confirmar acción?</h3>
            <p  id="modal-msg">Esta acción no se puede deshacer.</p>
            <div class="modal-actions">
                <button id="modal-cancel" class="btn btn-secondary">Cancelar</button>
                <button id="modal-confirm" class="btn btn-danger">Confirmar</button>
            </div>
        </div>
    `;
    document.body.appendChild(overlay);

    var pendingHref = null;
    var pendingForm = null;

    function openModal(title, msg, href, form) {
        document.getElementById('modal-title').textContent = title || '¿Confirmar acción?';
        document.getElementById('modal-msg').textContent   = msg   || 'Esta acción no se puede deshacer.';
        pendingHref = href || null;
        pendingForm = form || null;
        overlay.classList.add('active');
    }

    function closeModal() {
        overlay.classList.remove('active');
    }

    document.getElementById('modal-cancel').addEventListener('click', function() {
        pendingHref = null;
        pendingForm = null;
        closeModal();
    });

    overlay.addEventListener('click', function (e) {
        if (e.target === overlay) {
            pendingHref = null;
            pendingForm = null;
            closeModal();
        }
    });

    document.getElementById('modal-confirm').addEventListener('click', function () {
        // Guardar referencias antes de cerrar
        var href = pendingHref;
        var form = pendingForm;

        pendingHref = null;
        pendingForm = null;
        closeModal();

        // Navegar DESPUÉS de cerrar
        if (href) {
            window.location.href = href;
        } else if (form) {
            form.submit();
        }
    });

    document.addEventListener('click', function (e) {
        var el = e.target.closest('.confirm-action');
        if (!el) return;
        e.preventDefault();
        // Usar getAttribute para obtener el href tal como está en el HTML
        // (evita problemas con URLs absolutas vs relativas)
        var href = el.getAttribute('href') || el.href;
        openModal(
            el.dataset.title || '¿Confirmar acción?',
            el.dataset.msg   || 'Esta acción no se puede deshacer.',
            href,
            null
        );
    });

    document.addEventListener('submit', function (e) {
        var form = e.target;
        if (!form.classList.contains('confirm-form')) return;
        e.preventDefault();
        openModal(
            form.dataset.title || '¿Confirmar acción?',
            form.dataset.msg   || 'Esta acción no se puede deshacer.',
            null,
            form
        );
    });
})();
