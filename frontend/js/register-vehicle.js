function showTypeFields() {
    const type = $('#vehicleType').value;
    $$('#type-fields .field').forEach((f) => { f.style.display = f.dataset.type === type ? '' : 'none'; });
}

async function loadTags() {
    const tags = await api('/rfid');
    const available = tags.filter((t) => !t.registrationNumber);
    $('#rfidTagId').innerHTML = '<option value="">-- No tag --</option>' +
        available.map((t) => `<option value="${escapeHtml(t.tagId)}">${escapeHtml(t.tagId)}</option>`).join('') +
        (available.length ? '' : '<option disabled>no free tags - create one in RFID Control</option>');
}

async function loadVehicle(id) {
    const v = await api('/vehicles/' + id);
    $('#vehicle-id').value = v.id;
    $('#form-title').textContent = 'Edit Vehicle - ' + v.registrationNumber;
    $('#vehicleType').value = v.vehicleType;
    $('#registrationNumber').value = v.registrationNumber;
    $('#ownerName').value = v.ownerName;
    if (v.numberOfSeats != null) $('#numberOfSeats').value = v.numberOfSeats;
    if (v.loadCapacity != null) $('#loadCapacity').value = v.loadCapacity;
    if (v.passengerCapacity != null) $('#passengerCapacity').value = v.passengerCapacity;
    if (v.emergencyService != null) $('#emergencyService').value = v.emergencyService;
    if (v.rfidTag) {
        $('#rfidTagId').innerHTML = `<option value="${escapeHtml(v.rfidTag.tagId)}">${escapeHtml(v.rfidTag.tagId)} (current)</option>`;
    }
    $('#submit-btn').textContent = 'Save Changes';
    showTypeFields();
}

function collectForm() {
    const body = {
        vehicleType: $('#vehicleType').value,
        registrationNumber: $('#registrationNumber').value,
        ownerName: $('#ownerName').value,
        rfidTagId: $('#rfidTagId').value || null,
    };
    switch ($('#vehicleType').value) {
        case 'CAR': body.numberOfSeats = Number($('#numberOfSeats').value); break;
        case 'TRUCK': body.loadCapacity = Number($('#loadCapacity').value); break;
        case 'BUS': body.passengerCapacity = Number($('#passengerCapacity').value); break;
        case 'EMERGENCY': body.emergencyService = $('#emergencyService').value; break;
    }
    return body;
}

document.addEventListener('DOMContentLoaded', () => {
    $('#vehicleType').addEventListener('change', showTypeFields);

    const params = new URLSearchParams(location.search);
    const editId = params.get('id');

    Promise.resolve()
        .then(loadTags)
        .then(() => editId ? loadVehicle(editId) : null)
        .catch((err) => toast(err.message, 'err'));

    const form = $('#vehicle-form');
    if (!editId) {
        form.addEventListener('submit', async (e) => {
            e.preventDefault();
            try {
                await api('/vehicles', { method: 'POST', body: collectForm() });
                toast('Vehicle registered');
                setTimeout(() => location.href = 'vehicles.html', 600);
            } catch (err) {
                toast(err.message, 'err');
            }
        });
    } else {
        form.addEventListener('submit', async (e) => {
            e.preventDefault();
            try {
                await api('/vehicles/' + editId, { method: 'PUT', body: collectForm() });
                toast('Vehicle updated');
                setTimeout(() => location.href = 'vehicles.html', 600);
            } catch (err) {
                toast(err.message, 'err');
            }
        });
    }
});