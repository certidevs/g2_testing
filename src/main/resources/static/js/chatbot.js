(function () {
    // Guarda el hilo activo en el navegador para continuar la misma conversación.
    const STORAGE_KEY = 'ecommerceChatbotConversationId';

    // Añade una burbuja visual al chat sin recargar la página.
    function appendMessage(container, text, type, demoMode) {
        const wrapper = document.createElement('div');
        wrapper.className = `chat-message ${type}`;

        const bubble = document.createElement('div');
        bubble.className = 'chat-bubble';
        bubble.textContent = text;

        if (demoMode) {
            const badge = document.createElement('span');
            badge.className = 'badge text-bg-warning mt-2';
            badge.textContent = 'Modo demo';
            bubble.appendChild(document.createElement('br'));
            bubble.appendChild(badge);
        }

        wrapper.appendChild(bubble);
        container.appendChild(wrapper);
        container.scrollTop = container.scrollHeight;
    }

    // Bloquea el input mientras esperamos al backend y muestra un spinner.
    function setLoading(input, submit, isLoading) {
        input.disabled = isLoading;
        submit.disabled = isLoading;
        submit.innerHTML = isLoading
            ? '<span class="spinner-border spinner-border-sm" aria-hidden="true"></span><span class="visually-hidden">Cargando</span>'
            : '<i class="fa-solid fa-paper-plane"></i><span class="visually-hidden">Enviar</span>';
    }

    // Conecta cualquier formulario marcado con data-chatbot-form al endpoint /api/chatbot.
    function initChatForm(form) {
        const input = form.querySelector('[data-chatbot-input]');
        const messages = document.querySelector(form.dataset.chatbotMessagesTarget || '[data-chatbot-messages]');
        const submit = form.querySelector('[data-chatbot-submit]');

        if (!input || !messages || !submit || form.dataset.chatbotReady === 'true') {
            return;
        }

        form.dataset.chatbotReady = 'true';
        form.addEventListener('submit', async (event) => {
            event.preventDefault();

            const message = input.value.trim();
            if (!message) {
                return;
            }

            appendMessage(messages, message, 'user', false);
            input.value = '';
            setLoading(input, submit, true);

            try {
                // Si ya existe conversación, enviamos su id para guardar el nuevo turno en el mismo hilo.
                const conversationId = localStorage.getItem(STORAGE_KEY);
                const response = await fetch('/api/chatbot', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({ message, conversationId })
                });

                if (!response.ok) {
                    throw new Error('Respuesta no válida');
                }

                const data = await response.json();
                if (data.conversationId) {
                    // El primer mensaje crea la conversación; desde ahí reutilizamos el id devuelto.
                    localStorage.setItem(STORAGE_KEY, data.conversationId);
                }
                appendMessage(messages, data.answer, 'bot', data.demoMode);
            } catch (error) {
                appendMessage(messages, 'No he podido enviar el mensaje. Revisa la conexión o inténtalo de nuevo.', 'bot', true);
            } finally {
                setLoading(input, submit, false);
                input.focus();
            }
        });
    }

    // Abre y cierra la ventana flotante inferior derecha.
    function initWidget(widget) {
        const windowElement = widget.querySelector('[data-chatbot-window]');
        const toggles = widget.querySelectorAll('[data-chatbot-toggle]');
        const input = widget.querySelector('[data-chatbot-input]');

        function setOpen(isOpen) {
            widget.classList.toggle('is-open', isOpen);
            windowElement.setAttribute('aria-hidden', String(!isOpen));
            toggles.forEach((toggle) => toggle.setAttribute('aria-expanded', String(isOpen)));

            if (isOpen) {
                setTimeout(() => input && input.focus(), 120);
            }
        }

        toggles.forEach((toggle) => {
            toggle.addEventListener('click', () => {
                setOpen(!widget.classList.contains('is-open'));
            });
        });
    }

    // Inicializa tanto la página completa /chatbot como el widget global del footer.
    document.addEventListener('DOMContentLoaded', () => {
        document.querySelectorAll('[data-chatbot-form]').forEach(initChatForm);
        document.querySelectorAll('[data-chatbot-widget]').forEach(initWidget);
    });
})();
