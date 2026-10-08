let pendingList = [];
let selected = null;

async function loadPending() {
    pendingList = await api('/transactions/pending');
    const tbody = $('#pending-tbody');
    if (pendingList.length === 0) {
        tbody.innerHTML = '<tr><td colspan="4" class="empty">No pending payments</td></tr>';
        return;
    }
    tbody.innerHTML = pendingList.map((t) => `
        <tr>
            <td class="mono">${escapeHtml(t.transactionId)}</td>
            <td class="mono">${escapeHtml(t.registrationNumber)}</td>
            <td class="mono"><b>${inr(t.finalAmount)}</b></td>
            <td style="text-align:right">
                <button class="btn small primary" onclick="selectPending('${escapeHtml(t.transactionId)}')">PAY NOW</button>
            </td>
        </tr>`).join('');
}

async function selectPending(transactionId) {
    selected = pendingList.find((t) => t.transactionId === transactionId);
    if (!selected) return;
    $('#pay-detail').innerHTML = `
        <table>
            <tr><td style="color:var(--muted)">Transaction</td><td class="mono"><b>${escapeHtml(selected.transactionId)}</b></td></tr>
            <tr><td style="color:var(--muted)">Vehicle</td><td class="mono">${escapeHtml(selected.registrationNumber)}</td></tr>
            <tr><td style="color:var(--muted)">Toll Breakdown</td><td class="mono">${inr(selected.baseToll)} +${inr(selected.trafficCharge + selected.peakCharge + selected.weatherCharge + selected.pollutionCharge)}</td></tr>
            <tr><td style="color:var(--muted)">Amount</td><td class="mono"><b style="color:var(--accent)">${inr(selected.finalAmount)}</b></td></tr>
        </table>
        <div class="field" style="margin-top:12px">
            <label>Payment Method</label>
            <select id="pay-method">
                <option value="UPI">UPI</option>
                <option value="CARD">Card</option>
                <option value="WALLET">Wallet</option>
            </select>
        </div>
        <button class="btn primary" style="width:100%;justify-content:center" id="pay-btn">PAY NOW</button>`;
}

async function pay() {
    const btn = $('#pay-btn');
    btn.disabled = true;
    btn.innerHTML = '<span class="spinner"></span> Processing payment...';
    try {
        const result = await api('/payments', {
            method: 'POST',
            body: { transactionId: selected.transactionId, method: $('#pay-method').value },
        });
        $('#receipt-text').textContent = result.receipt;
        $('#receipt-modal').classList.add('open');
        loadPending();
        $('#pay-detail').innerHTML = '<div class="empty">Select an unpaid transaction</div>';
        toast('Payment of ' + inr(result.amount) + ' successful (paid)');
    } catch (err) {
        toast(err.message, 'err');
    } finally {
        btn.disabled = false;
        btn.textContent = 'PAY NOW';
    }
}

document.addEventListener('DOMContentLoaded', () => {
    loadPending().catch((e) => toast(e.message, 'err'));
    document.addEventListener('click', (e) => {
        if (e.target.id === 'pay-btn') pay();
    });
    $('#receipt-modal').addEventListener('click', (e) => {
        if (e.target === $('#receipt-modal')) $('#receipt-modal').classList.remove('open');
    });
});