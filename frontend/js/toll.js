function conditionsHtml(cond) {
    const rows = [
        ['Traffic', cond.trafficLabel],
        ['Weather', cond.weatherLabel],
        ['Pollution', `AQI ${cond.aqi} (${cond.pollutionLabel})`],
        ['Time', fmtTime(cond.updatedAt)],
        ['Peak Hour', cond.peak ? 'YES' : 'NO'],
    ];
    return `
        <table>
            ${rows.map(([k, val]) => `
                <tr><td style="color:var(--muted)">${k}</td>
                <td><b>${escapeHtml(val)}</b> ${k === 'Peak Hour' && val === 'YES' ? '<span class="badge pending">+extra</span>' : ''}</td>
                </tr>`).join('')}
        </table>
        <p style="color:var(--muted);font-size:12px;margin-top:12px">Conditions are simulated live. Adjust them from Settings.</p>`;
}

function breakdownHtml(b) {
    const c = b.charges;
    return `
        <div class="breakdown">
            <div class="row"><span>Base Toll (${escapeHtml(b.vehicleType)})</span><span class="mono">${inr(c.baseToll)}</span></div>
            <div class="row"><span>Traffic Charge</span><span class="mono">+${inr(c.trafficCharge)}</span></div>
            <div class="row"><span>Peak Hour Charge</span><span class="mono">+${inr(c.peakCharge)}</span></div>
            <div class="row"><span>Weather Adjustment</span><span class="mono">+${inr(c.weatherCharge)}</span></div>
            <div class="row"><span>Pollution Adjustment</span><span class="mono">+${inr(c.pollutionCharge)}</span></div>
            <div class="row total"><span>FINAL TOLL</span><span class="mono">${inr(c.finalAmount)}</span></div>
        </div>`;
}

document.addEventListener('DOMContentLoaded', async () => {
    try {
        $('#conditions-body').innerHTML = conditionsHtml(await api('/toll/current-rates'));
        calculate();
    } catch (err) {
        toast(err.message, 'err');
    }

    async function calculate() {
        try {
            const type = $('#calc-type').value;
            const b = await api('/toll/calculate', { method: 'POST', body: { vehicleType: type } });
            $('#calc-result').innerHTML = breakdownHtml(b);
        } catch (err) {
            toast(err.message, 'err');
        }
    }

    $('#calc-btn').addEventListener('click', calculate);
    $('#calc-type').addEventListener('change', calculate);
});