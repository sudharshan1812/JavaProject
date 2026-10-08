async function loadTransactions() {
    const from = $('#from').value;
    const to = $('#to').value;
    let url = '/transactions';
    if (from && to) {
        url += '?from=' + encodeURIComponent(new Date(from).toISOString()) +
               '&to=' + encodeURIComponent(new Date(to).toISOString());
    }
    const list = await api(url);
    const tbody = $('#tx-tbody');
    if (list.length === 0) {
        tbody.innerHTML = '<tr><td colspan="9" class="empty">No transactions found</td></tr>';
        return;
    }
    tbody.innerHTML = list.map((t) => {
        const dynamic = t.trafficCharge + t.peakCharge + t.weatherCharge + t.pollutionCharge;
        return `
        <tr>
            <td class="mono">${escapeHtml(t.transactionId)}</td>
            <td class="mono">${escapeHtml(t.registrationNumber)}</td>
            <td>${escapeHtml(t.vehicleType)}</td>
            <td class="mono">${escapeHtml(t.rfidTagId || '-')}</td>
            <td class="mono">${inr(t.baseToll)}</td>
            <td class="mono">+${inr(dynamic)}</td>
            <td class="mono"><b>${inr(t.finalAmount)}</b></td>
            <td>${badge(t.status)}</td>
            <td>${fmtTime(t.createdAt)}</td>
        </tr>`;
    }).join('');
}

document.addEventListener('DOMContentLoaded', () => {
    loadTransactions().catch((e) => toast(e.message, 'err'));
    $('#filter-btn').addEventListener('click', () => loadTransactions().catch((e) => toast(e.message, 'err')));
    $('#clear-btn').addEventListener('click', () => {
        $('#from').value = ''; $('#to').value = '';
        loadTransactions().catch((e) => toast(e.message, 'err'));
    });
    $('#refresh-btn').addEventListener('click', () => loadTransactions().catch((e) => toast(e.message, 'err')));
});