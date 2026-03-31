(() => {
    const apiBase = `${window.location.origin}/currency-exchange`;

    let apiToast = null;
    let editModal = null;
    let currentEditPair = null;
    let cachedCurrencies = [];

    function init() {
        console.log('App init');
        console.log('API base:', apiBase);

        const toastElement = document.getElementById('api-toast');
        const modalElement = document.getElementById('edit-exchange-rate-modal');

        if (toastElement && window.bootstrap) {
            apiToast = new bootstrap.Toast(toastElement, {delay: 3500});
        }

        if (modalElement && window.bootstrap) {
            editModal = new bootstrap.Modal(modalElement);
        }

        const apiBaseElement = document.getElementById('api-base');
        if (apiBaseElement) {
            apiBaseElement.textContent = apiBase;
        }

        bindEvents();
        loadInitialData();
    }

    function bindEvents() {
        document.getElementById('add-currency')?.addEventListener('submit', handleAddCurrency);
        document.getElementById('add-exchange-rate')?.addEventListener('submit', handleAddExchangeRate);
        document.getElementById('convert')?.addEventListener('submit', handleConvert);
        document.getElementById('save-exchange-rate-btn')?.addEventListener('click', handleSaveExchangeRate);

        document.querySelector('.exchange-rates-table tbody')?.addEventListener('click', (event) => {
            const button = event.target.closest('[data-edit-pair]');
            if (!button) return;

            currentEditPair = button.dataset.editPair;
            document.getElementById('edit-pair-label').textContent = currentEditPair;
            document.getElementById('exchange-rate-input').value = button.dataset.editRate ?? '';
            document.querySelector('#edit-exchange-rate-modal .modal-title').textContent = `Edit ${currentEditPair}`;

            if (editModal) {
                editModal.show();
            }
        });
    }

    async function loadInitialData() {
        await Promise.all([
            requestCurrencies(),
            requestExchangeRates()
        ]);
    }

    async function requestCurrencies() {
        try {
            const currencies = await requestJson(`${apiBase}/currencies`);
            cachedCurrencies = Array.isArray(currencies) ? currencies : [];

            renderCurrencies(cachedCurrencies);
            populateCurrencySelects(cachedCurrencies);
            updateCurrencyStats(cachedCurrencies.length);
        } catch (error) {
            console.error('Currencies load error:', error);
            showError(error);
            renderCurrencies([]);
            updateCurrencyStats(0);
        }
    }

    async function requestExchangeRates() {
        try {
            const rates = await requestJson(`${apiBase}/exchangeRates`);
            renderExchangeRates(Array.isArray(rates) ? rates : []);
            updateRateStats(Array.isArray(rates) ? rates.length : 0);
        } catch (error) {
            console.error('Exchange rates load error:', error);
            showError(error);
            renderExchangeRates([]);
            updateRateStats(0);
        }
    }

    async function requestJson(url, options = {}) {
        console.log('Fetching:', url, options.method || 'GET');

        const response = await fetch(url, options);
        const contentType = response.headers.get('content-type') || '';
        const isJson = contentType.includes('application/json');
        const payload = isJson ? await response.json() : await response.text();

        if (!response.ok) {
            const message = isJson && payload && payload.message
                ? payload.message
                : `Request failed with status ${response.status}`;
            throw new Error(message);
        }

        return payload;
    }

    async function handleAddCurrency(event) {
        event.preventDefault();

        const form = event.currentTarget;
        const code = form.elements.code?.value?.trim()?.toUpperCase() ?? '';
        const name = form.elements.name?.value?.trim() ?? '';
        const sign = form.elements.sign?.value?.trim() ?? '';

        const body = new URLSearchParams({
            code,
            name,
            sign
        });

        try {
            await requestJson(`${apiBase}/currencies`, {
                method: 'POST',
                headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                body
            });

            form.reset();
            showToast('Currency added successfully', 'Success');
            await requestCurrencies();
        } catch (error) {
            showError(error);
        }
    }

    async function handleAddExchangeRate(event) {
        event.preventDefault();

        const form = event.currentTarget;
        const baseCurrencyCode = form.elements.baseCurrencyCode?.value ?? '';
        const targetCurrencyCode = form.elements.targetCurrencyCode?.value ?? '';
        const rate = form.elements.rate?.value?.trim() ?? '';

        const body = new URLSearchParams({
            baseCurrencyCode,
            targetCurrencyCode,
            rate
        });

        try {
            await requestJson(`${apiBase}/exchangeRates`, {
                method: 'POST',
                headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                body
            });

            form.reset();
            populateCurrencySelects(cachedCurrencies);
            showToast('Exchange rate added successfully', 'Success');
            await requestExchangeRates();
        } catch (error) {
            showError(error);
        }
    }

    async function handleConvert(event) {
        event.preventDefault();

        const from = document.getElementById('convert-base-currency')?.value ?? '';
        const to = document.getElementById('convert-target-currency')?.value ?? '';
        const amount = document.getElementById('convert-amount')?.value?.trim() ?? '';

        try {
            const result = await requestJson(
                `${apiBase}/exchange?from=${encodeURIComponent(from)}&to=${encodeURIComponent(to)}&amount=${encodeURIComponent(amount)}`
            );

            renderConversionResult(result);
        } catch (error) {
            showError(error);
        }
    }

    async function handleSaveExchangeRate() {
        const rate = document.getElementById('exchange-rate-input')?.value?.trim() ?? '';

        if (!currentEditPair) {
            showToast('No exchange rate selected', 'Error');
            return;
        }

        const body = new URLSearchParams({rate});

        try {
            await requestJson(`${apiBase}/exchangeRate/${currentEditPair}`, {
                method: 'PATCH',
                headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                body
            });

            if (editModal) {
                editModal.hide();
            }

            showToast('Exchange rate updated successfully', 'Success');
            await requestExchangeRates();
        } catch (error) {
            showError(error);
        }
    }

    function renderCurrencies(currencies) {
        const tbody = document.querySelector('.currencies-table tbody');
        const empty = document.getElementById('currencies-empty');
        if (!tbody) return;

        if (!currencies.length) {
            tbody.innerHTML = '';
            empty?.classList.remove('d-none');
            return;
        }

        empty?.classList.add('d-none');

        tbody.innerHTML = currencies.map(currency => `
            <tr>
                <td><strong>${escapeHtml(currency.code)}</strong></td>
                <td>${escapeHtml(currency.name)}</td>
                <td>${escapeHtml(currency.sign)}</td>
            </tr>
        `).join('');
    }

    function renderExchangeRates(rates) {
        const tbody = document.querySelector('.exchange-rates-table tbody');
        const empty = document.getElementById('rates-empty');
        if (!tbody) return;

        if (!rates.length) {
            tbody.innerHTML = '';
            empty?.classList.remove('d-none');
            return;
        }

        empty?.classList.add('d-none');

        tbody.innerHTML = rates.map(rate => {
            const pair = `${rate.baseCurrency.code}${rate.targetCurrency.code}`;
            return `
                <tr>
                    <td><strong>${escapeHtml(pair)}</strong></td>
                    <td>${escapeHtml(rate.baseCurrency.code)}</td>
                    <td>${escapeHtml(rate.targetCurrency.code)}</td>
                    <td>${escapeHtml(rate.rate)}</td>
                    <td class="text-end">
                        <button
                            type="button"
                            class="btn btn-sm btn-outline-primary"
                            data-edit-pair="${escapeHtml(pair)}"
                            data-edit-rate="${escapeHtml(rate.rate)}"
                        >
                            Edit
                        </button>
                    </td>
                </tr>
            `;
        }).join('');
    }

    function populateCurrencySelects(currencies) {
        const options = currencies.map(currency => `
        <option
            value="${escapeHtml(currency.code)}"
            title="${escapeHtml(currency.name)}"
        >
            ${escapeHtml(currency.code)}
        </option>
    `).join('');

        const ids = [
            'new-rate-base-currency',
            'new-rate-target-currency',
            'convert-base-currency',
            'convert-target-currency'
        ];

        ids.forEach((id) => {
            const select = document.getElementById(id);
            if (select) {
                const currentValue = select.value;
                select.innerHTML = options;

                const hasCurrent = currencies.some(currency => currency.code === currentValue);
                if (hasCurrent) {
                    select.value = currentValue;
                }
            }
        });
    }

    function renderConversionResult(result) {
        const amountEl = document.getElementById('convert-converted-amount');
        const metaEl = document.getElementById('conversion-meta');

        if (!amountEl || !metaEl) return;

        amountEl.textContent = `${result.convertedAmount} ${result.targetCurrency.code}`;
        metaEl.textContent =
            `${result.amount} ${result.baseCurrency.code} × ${result.rate} = ${result.convertedAmount} ${result.targetCurrency.code}`;
    }

    function updateCurrencyStats(count) {
        const countEl = document.getElementById('currencies-count');
        const pillEl = document.getElementById('currencies-pill');

        if (countEl) countEl.textContent = String(count);
        if (pillEl) pillEl.textContent = `${count} loaded`;
    }

    function updateRateStats(count) {
        const countEl = document.getElementById('rates-count');
        const pillEl = document.getElementById('rates-pill');

        if (countEl) countEl.textContent = String(count);
        if (pillEl) pillEl.textContent = `${count} loaded`;
    }

    function showToast(message, title = 'Notice') {
        const titleEl = document.getElementById('toast-title');
        const messageEl = document.getElementById('toast-message');

        if (titleEl) titleEl.textContent = title;
        if (messageEl) messageEl.textContent = message;

        if (apiToast) {
            apiToast.show();
        } else {
            alert(`${title}: ${message}`);
        }
    }

    function showError(error) {
        showToast(error.message || 'Something went wrong.', 'Error');
    }

    function escapeHtml(value) {
        return String(value)
            .replaceAll('&', '&amp;')
            .replaceAll('<', '&lt;')
            .replaceAll('>', '&gt;')
            .replaceAll('"', '&quot;')
            .replaceAll("'", '&#039;');
    }

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', init);
    } else {
        init();
    }
})();