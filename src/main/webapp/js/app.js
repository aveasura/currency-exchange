(() => {
    const appRootUrl = new URL('..', document.currentScript.src);
    const apiBase = appRootUrl.pathname.replace(/\/$/, '');
    const THEME_STORAGE_KEY = 'currency-exchange-theme';

    let apiToast = null;
    let editModal = null;
    let currentEditPair = null;
    let cachedCurrencies = [];

    function init() {
        initBootstrap();
        restoreTheme();

        const apiBaseElement = document.getElementById('api-base');
        if (apiBaseElement) {
            apiBaseElement.textContent = apiBase;
        }

        bindThemeToggle();
        bindEvents();
        loadInitialData();
    }

    function initBootstrap() {
        const modalElement = document.getElementById('edit-exchange-rate-modal');
        if (modalElement && window.bootstrap) {
            editModal = new bootstrap.Modal(modalElement);
        }
    }

    function bindThemeToggle() {
        const button = document.getElementById('theme-toggle-btn');
        if (!button) return;

        updateThemeButtonLabel();

        button.addEventListener('click', () => {
            document.body.classList.toggle('dark-theme');
            persistTheme();
            updateThemeButtonLabel();
        });
    }

    function restoreTheme() {
        const savedTheme = localStorage.getItem(THEME_STORAGE_KEY);

        if (savedTheme === 'light') {
            document.body.classList.remove('dark-theme');
        } else {
            document.body.classList.add('dark-theme');
        }
    }

    function persistTheme() {
        const isDark = document.body.classList.contains('dark-theme');
        localStorage.setItem(THEME_STORAGE_KEY, isDark ? 'dark' : 'light');
    }

    function updateThemeButtonLabel() {
        const button = document.getElementById('theme-toggle-btn');
        if (!button) return;

        const isDark = document.body.classList.contains('dark-theme');
        button.textContent = isDark ? '☀️ Light mode' : '🌙 Dark mode';
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

            editModal?.show();
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
            showError(error);
            renderExchangeRates([]);
            updateRateStats(0);
        }
    }

    async function requestJson(url, options = {}) {
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
            showToast('Currency added successfully', 'Success', 'success');
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
            showToast('Exchange rate added successfully', 'Success', 'success');
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
            showToast('No exchange rate selected', 'Error', 'error');
            return;
        }

        const body = new URLSearchParams({rate});

        try {
            await requestJson(`${apiBase}/exchangeRate/${currentEditPair}`, {
                method: 'PATCH',
                headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                body
            });

            editModal?.hide();
            showToast('Exchange rate updated successfully', 'Success', 'success');
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
                    <td>${escapeHtml(formatRateForDisplay(rate.rate))}</td>
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
            <option value="${escapeHtml(currency.code)}" title="${escapeHtml(currency.name)}">
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
            if (!select) return;

            const currentValue = select.value;
            select.innerHTML = options;

            const hasCurrent = currencies.some(currency => currency.code === currentValue);
            if (hasCurrent) {
                select.value = currentValue;
            }
        });
    }

    function renderConversionResult(result) {
        const amountEl = document.getElementById('convert-converted-amount');
        const metaEl = document.getElementById('conversion-meta');

        if (!amountEl || !metaEl) return;

        const rate = Number(result.rate);
        const amount = Number(result.amount);
        const preciseConvertedAmount = rate * amount;

        const displayRate = formatRateForDisplay(result.rate);
        const displayAmount = formatAmountForDisplay(result.amount, result.baseCurrency.code);
        const displayConvertedAmount = formatConvertedAmountForDisplay(
            preciseConvertedAmount,
            result.targetCurrency.code
        );

        amountEl.textContent = displayConvertedAmount;
        metaEl.textContent = `${displayAmount} × ${displayRate} = ${displayConvertedAmount}`;
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

    function showToast(message, title = 'Notice', type = 'info') {
        const titleEl = document.getElementById('toast-title');
        const messageEl = document.getElementById('toast-message');
        const toastEl = document.getElementById('api-toast');

        if (titleEl) titleEl.textContent = title;
        if (messageEl) messageEl.textContent = message;
        if (!toastEl) return;

        toastEl.classList.remove('toast-success', 'toast-error', 'toast-info');

        if (type === 'success') {
            toastEl.classList.add('toast-success');
        } else if (type === 'error') {
            toastEl.classList.add('toast-error');
        } else {
            toastEl.classList.add('toast-info');
        }

        if (window.bootstrap) {
            apiToast?.dispose();
            apiToast = new bootstrap.Toast(toastEl, {
                delay: type === 'error' ? 5500 : 2600
            });
            apiToast.show();
        } else {
            alert(`${title}: ${message}`);
        }
    }

    function showError(error) {
        showToast(error.message || 'Something went wrong.', 'Error', 'error');
    }

    function escapeHtml(value) {
        return String(value)
            .replaceAll('&', '&amp;')
            .replaceAll('<', '&lt;')
            .replaceAll('>', '&gt;')
            .replaceAll('"', '&quot;')
            .replaceAll("'", '&#039;');
    }

    function formatRateForDisplay(rate) {
        const numericRate = Number(rate);

        if (Number.isNaN(numericRate)) {
            return String(rate);
        }

        if (numericRate > 0 && numericRate < 0.000001) {
            return '< 0.000001';
        }

        return numericRate.toFixed(6);
    }

    function formatConvertedAmountForDisplay(rawValue, currencyCode) {
        const numericValue = Number(rawValue);

        if (Number.isNaN(numericValue)) {
            return `${rawValue} ${currencyCode}`;
        }

        if (numericValue > 0 && numericValue < 0.01) {
            return `< 0.01 ${currencyCode}`;
        }

        return `${numericValue.toFixed(2)} ${currencyCode}`;
    }

    function formatAmountForDisplay(amount, currencyCode) {
        const numericAmount = Number(amount);

        if (Number.isNaN(numericAmount)) {
            return `${amount} ${currencyCode}`;
        }

        return `${numericAmount} ${currencyCode}`;
    }

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', init);
    } else {
        init();
    }
})();