async function refreshReader() {
    const status = await api('/rfid/status');
    $('#reader-id').textContent = status.readerId;
    $('#reader-status').innerHTML = status.running
        ? '<span class="dot on"></span>ONLINE'
        : '<span class="dot off"></span>OFFLINE';
    $('#start-reader').disabled = status.running;
    $('#stop-reader').disabled = !status.running;
    return status.running;
}

async function loadTags() {
    const tags = await api('/rfid');
    $('#registered-tags').innerHTML = tags.map((t) => `<option value="${escapeHtml(t.tagId)}">`).join('');
    const tbody = $('#tags-tbody');
    if (tags.length === 0) {
        tbody.innerHTML = '<tr><td colspan="4" class="empty">No tags registered</td></tr>';
        return;
    }
    tbody.innerHTML = tags.map((t) => `
        <tr>
            <td class="mono">${escapeHtml(t.tagId)}</td>
            <td>${t.registrationNumber ? escapeHtml(t.registrationNumber) : '<span style="color:var(--muted)">not assigned</span>'}</td>
            <td>${badge(t.active ? 'ACTIVE' : 'INACTIVE')}</td>
            <td style="text-align:right;white-space:nowrap">
                ${t.active
                    ? `<button class="btn small ghost" onclick="toggleTag('${t.tagId}', false)">Deactivate</button>`
                    : `<button class="btn small primary" onclick="toggleTag('${t.tagId}', true)">Activate</button>`}
            </td>
        </tr>`).join('');
}

async function toggleTag(tagId, active) {
    try {
        await api(`/rfid/${encodeURIComponent(tagId)}/${active ? 'activate' : 'deactivate'}`, { method: 'POST' });
        toast('Tag ' + tagId + (active ? ' activated' : ' deactivated'));
        loadTags();
    } catch (err) {
        toast(err.message, 'err');
    }
}

function scanHtml(scan, payUrl) {
    const c = scan.breakdown.charges;
    const cond = scan.breakdown.conditions;
    return `
        <table>
            <tr><td class="mono">${escapeHtml(scan.tagId)}</td><td><b>${escapeHtml(scan.registrationNumber)}</b> ${escapeHtml(scan.vehicleType)}</td></tr>
            <tr><td colspan="2">Owner: ${escapeHtml(scan.ownerName)} | Tx: <span class="mono">${escapeHtml(scan.transactionId)}</span></td></tr>
        </table>
        <div class="breakdown">
            <div class="row"><span>Base Toll</span><span class="mono">${inr(c.baseToll)}</span></div>
            <div class="row"><span>Traffic (${escapeHtml(cond.traffic)})</span><span class="mono">+${inr(c.trafficCharge)}</span></div>
            <div class="row"><span>Peak</span><span class="mono">+${inr(c.peakCharge)}</span></div>
            <div class="row"><span>Weather (${escapeHtml(cond.weather)})</span><span class="mono">+${inr(c.weatherCharge)}</span></div>
            <div class="row"><span>Pollution (AQI ${cond.aqi})</span><span class="mono">+${inr(c.pollutionCharge)}</span></div>
            <div class="row total"><span>FINAL TOLL</span><span class="mono">${inr(c.finalAmount)}</span></div>
            <div style="margin-top:10px;text-align:center">${badge('PAYMENT REQUIRED')}</div>
            ${payUrl ? `<a href="${payUrl}" class="btn primary" style="width:100%;justify-content:center;margin-top:10px">PAY NOW</a>` : ''}
        </div>`;
}

document.addEventListener('DOMContentLoaded', () => {
    refreshReader().catch(() => {});
    loadTags().catch(() => {});

    $('#start-reader').addEventListener('click', async () => {
        try { await api('/rfid/reader/start', { method: 'POST' }); refreshReader(); } catch (e) { toast(e.message, 'err'); }
    });
    $('#stop-reader').addEventListener('click', async () => {
        try { await api('/rfid/reader/stop', { method: 'POST' }); refreshReader(); } catch (e) { toast(e.message, 'err'); }
    });
    $('#tag-form').addEventListener('submit', async (e) => {
        e.preventDefault();
        try {
            await api('/rfid', { method: 'POST', body: { tagId: $('#new-tag').value } });
            $('#new-tag').value = '';
            toast('Tag created');
            loadTags();
        } catch (err) { toast(err.message, 'err'); }
    });
    $('#scan-btn').addEventListener('click', async () => {
        const tagId = $('#scan-tag').value.trim();
        if (!tagId) return toast('Enter a tag id first', 'err');
        try {
            $('#scan-btn').disabled = true;
            $('#scan-btn').innerHTML = '<span class="spinner"></span> Scanning...';
            const scan = await api('/rfid/scan', { method: 'POST', body: { tagId } });
            $('#scan-result').innerHTML = scanHtml(scan, 'payments.html');
            $('#scan-tag').value = '';
        } catch (err) {
            toast(err.message, 'err');
            $('#scan-result').innerHTML = '';
        } finally {
            $('#scan-btn').disabled = false;
            $('#scan-btn').textContent = 'SCAN RFID';
        }
    });
});