function scanBreakdownHtml(scan) {
    const c = scan.breakdown.charges;
    const cond = scan.breakdown.conditions;
    return `
        <table>
            <tr><td class="mono">Tag ID</td><td class="mono">${escapeHtml(scan.tagId)}</td></tr>
            <tr><td>Vehicle</td><td><b>${escapeHtml(scan.registrationNumber)}</b> (${escapeHtml(scan.vehicleType)})</td></tr>
            <tr><td>Owner</td><td>${escapeHtml(scan.ownerName)}</td></tr>
            <tr><td>Transaction</td><td class="mono">${escapeHtml(scan.transactionId)}</td></tr>
        </table>
        <div class="breakdown">
            <div class="row"><span>Base Toll</span><span class="mono">${inr(c.baseToll)}</span></div>
            <div class="row"><span>Traffic Charge (${escapeHtml(cond.traffic)})</span><span class="mono">+${inr(c.trafficCharge)}</span></div>
            <div class="row"><span>Peak Charge</span><span class="mono">+${inr(c.peakCharge)}</span></div>
            <div class="row"><span>Weather (${escapeHtml(cond.weather)})</span><span class="mono">+${inr(c.weatherCharge)}</span></div>
            <div class="row"><span>Pollution (AQI ${cond.aqi})</span><span class="mono">+${inr(c.pollutionCharge)}</span></div>
            <div class="row total"><span>FINAL TOLL</span><span class="mono">${inr(c.finalAmount)}</span></div>
            <div style="margin-top:10px;text-align:center">Status: <span class="badge pending">PAYMENT REQUIRED</span></div>
            <a href="payments.html" class="btn primary" style="width:100%;justify-content:center;margin-top:10px">GO TO PAYMENT</a>
        </div>`;
}

let scanPoll = null;

async function doScan(tagId) {
    const scan = await api('/rfid/scan', { method: 'POST', body: { tagId } });
    $('#scan-detail').innerHTML = scanBreakdownHtml(scan);
    $('#scan-modal').classList.add('open');
}

document.addEventListener('DOMContentLoaded', async () => {
    try {
        const data = await api('/dashboard/statistics');
        $('#kpi-vehicles').textContent = data.totalVehicles.toLocaleString('en-IN');
        $('#kpi-transactions').textContent = data.todayTransactions.toLocaleString('en-IN');
        $('#kpi-revenue').textContent = inr(data.todayRevenue);
        $('#kpi-success').textContent = data.paymentSuccessRate.toFixed(1) + '%';
        renderLive(data.liveTransactions);

        const tags = await api('/rfid');
        $('#tag-list').innerHTML = tags.map((t) => `<option value="${escapeHtml(t.tagId)}">`).join('');

        const reader = await api('/rfid/status');
        $('#st-reader').textContent = reader.running ? 'ONLINE' : 'OFFLINE';
        $('#st-reader').previousElementSibling.querySelector('.dot').className = 'dot ' + (reader.running ? 'on' : 'off');
    } catch (err) {
        toast(err.message, 'err');
    }

    const modal = $('#scan-modal');
    modal.addEventListener('click', (e) => { if (e.target === modal) modal.classList.remove('open'); });

    $('#scan-btn').addEventListener('click', async () => {
        const tagId = $('#scan-tag').value.trim();
        if (!tagId) return toast('Enter a tag id first', 'err');
        try {
            $('#scan-btn').disabled = true;
            $('#scan-btn').innerHTML = '<span class="spinner"></span> Scanning...';
            await doScan(tagId);
            $('#scan-tag').value = '';
            clearInterval(scanPoll);
            scanPoll = setInterval(refreshLive, 4000);
        } catch (err) {
            toast(err.message, 'err');
        } finally {
            $('#scan-btn').disabled = false;
            $('#scan-btn').textContent = 'SCAN RFID';
        }
    });

    setInterval(refreshLive, 5000);
});

function renderLive(list) {
    const tbody = $('#live-tbody');
    if (!list || list.length === 0) {
        tbody.innerHTML = '<tr><td colspan="5" class="empty">No transactions yet</td></tr>';
        return;
    }
    tbody.innerHTML = list.map((t) => `
        <tr>
            <td class="mono">${escapeHtml(t.rfidTagId || '-')}</td>
            <td>${escapeHtml(t.registrationNumber)} <span style="color:var(--muted)">${escapeHtml(t.vehicleType)}</span></td>
            <td class="mono">${inr(t.finalAmount)}</td>
            <td>${badge(t.status)}</td>
            <td>${fmtTime(t.createdAt)}</td>
        </tr>`).join('');
}

async function refreshLive() {
    try {
        renderLive((await api('/dashboard/live-transactions')).liveTransactions);
    } catch (e) { /* polling errors are silent */ }
}