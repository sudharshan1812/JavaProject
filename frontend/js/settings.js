document.addEventListener('DOMContentLoaded', () => {
    $('#settings-user').textContent = localStorage.getItem('username') || '-';
    $('#logout-btn2').addEventListener('click', logout);

    api('/toll/current-rates')
        .then((c) => {
            $('#traffic').value = c.traffic;
            $('#weather').value = c.weather;
            $('#pollution').value = c.pollution;
        })
        .catch((e) => toast(e.message, 'err'));

    $('#save-conditions').addEventListener('click', async () => {
        try {
            await api('/toll/conditions', {
                method: 'PUT',
                body: {
                    traffic: $('#traffic').value,
                    weather: $('#weather').value,
                    pollution: $('#pollution').value,
                },
            });
            toast('Pricing conditions updated - next scan will use the new rates');
        } catch (err) {
            toast(err.message, 'err');
        }
    });
});