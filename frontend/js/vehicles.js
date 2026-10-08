async function loadVehicles(query) {
    const q = query ? '?q=' + encodeURIComponent(query) : '';
    const vehicles = await api('/vehicles' + q);
    const tbody = $('#vehicles-tbody');
    if (vehicles.length === 0) {
        tbody.innerHTML = '<tr><td colspan="7" class="empty">No vehicles found</td></tr>';
        return;
    }
    tbody.innerHTML = vehicles.map((v) => `
        <tr>
            <td class="mono">${escapeHtml(v.registrationNumber)}</td>
            <td>${escapeHtml(v.ownerName)}</td>
            <td><span class="badge pending">${escapeHtml(v.vehicleType)}</span></td>
            <td class="mono">${v.rfidTag ? escapeHtml(v.rfidTag.tagId) : '<span style="color:var(--muted)">no tag</span>'}</td>
            <td>${v.rfidTag ? badge(v.rfidTag.active ? 'ACTIVE' : 'INACTIVE') : '-'}</td>
            <td>${badge(v.active ? 'ACTIVE' : 'INACTIVE')}</td>
            <td style="text-align:right;white-space:nowrap">
                <button class="btn small ghost" onclick="viewVehicle(${v.id})">View</button>
                <button class="btn small ghost" onclick="location.href='register-vehicle.html?id=${v.id}'">Edit</button>
                <button class="btn small danger" onclick="deleteVehicle(${v.id})">Delete</button>
            </td>
        </tr>`).join('');
}

async function viewVehicle(id) {
    const v = await api('/vehicles/' + id);
    const rows = [
        ['Registration', v.registrationNumber],
        ['Owner', v.ownerName],
        ['Type', v.vehicleType],
        ['RFID Tag', v.rfidTag ? v.rfidTag.tagId + (v.rfidTag.active ? ' (active)' : ' (inactive)') : 'not assigned'],
        ['Registered', fmtTime(v.createdAt)],
    ];
    if (v.numberOfSeats != null) rows.push(['Seats', v.numberOfSeats]);
    if (v.loadCapacity != null) rows.push(['Load Capacity', v.loadCapacity + ' tons']);
    if (v.passengerCapacity != null) rows.push(['Passenger Capacity', v.passengerCapacity]);
    if (v.emergencyService != null) rows.push(['Emergency Service', v.emergencyService]);
    $('#detail-body').innerHTML = `
        <table>
            ${rows.map(([k, val]) => `<tr><td style="color:var(--muted)">${k}</td><td><b>${escapeHtml(val)}</b></td></tr>`).join('')}
        </table>`;
    $('#detail-modal').classList.add('open');
}

async function deleteVehicle(id) {
    if (!confirm('Delete this vehicle? The RFID tag will be deactivated.')) return;
    try {
        await api('/vehicles/' + id, { method: 'DELETE' });
        toast('Vehicle deleted');
        loadVehicles($('#search-q').value.trim());
    } catch (err) {
        toast(err.message, 'err');
    }
}

document.addEventListener('DOMContentLoaded', () => {
    loadVehicles('');
    $('#search-btn').addEventListener('click', () => loadVehicles($('#search-q').value.trim()));
    $('#search-q').addEventListener('keydown', (e) => {
        if (e.key === 'Enter') loadVehicles($('#search-q').value.trim());
    });
    $('#detail-modal').addEventListener('click', (e) => {
        if (e.target === $('#detail-modal')) $('#detail-modal').classList.remove('open');
    });
});