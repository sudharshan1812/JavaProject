const CHART_FONT = { color: '#94a3b8' };
const PALETTE = ['#10b981', '#3b82f6', '#f59e0b', '#8b5cf6', '#ef4444', '#22d3ee'];
const chartDefaults = {
    legend: { labels: { color: '#e2e8f0', font: { size: 11 } }, position: 'bottom' },
    tooltips: { enabled: true },
};

async function loadRevenue() {
    const revenue = await api('/reports/revenue');
    $('#rev-today').textContent = inr(revenue.today);
    $('#rev-week').textContent = inr(revenue.week);
    $('#rev-month').textContent = inr(revenue.month);
    $('#rev-total').textContent = inr(revenue.total);
}

async function loadCharts() {
    const [daily, vehicles, methods] = await Promise.all([
        api('/reports/daily-revenue?days=7'),
        api('/reports/vehicle-distribution'),
        api('/reports/payment-methods'),
    ]);

    new Chart($('#chart-daily'), {
        type: 'bar',
        data: {
            labels: Object.keys(daily),
            datasets: [{
                label: 'Revenue',
                data: Object.values(daily),
                backgroundColor: '#10b981',
                borderRadius: 6,
            }],
        },
        options: {
            plugins: chartDefaults,
            scales: { x: { ticks: CHART_FONT, grid: { color: '#22304d' } }, y: { ticks: CHART_FONT, grid: { color: '#22304d' } } },
        },
    });

    new Chart($('#chart-vehicles'), {
        type: 'doughnut',
        data: {
            labels: Object.keys(vehicles),
            datasets: [{ data: Object.values(vehicles), backgroundColor: PALETTE, borderColor: '#131c31', borderWidth: 3 }],
        },
        options: { plugins: chartDefaults },
    });

    new Chart($('#chart-payments'), {
        type: 'doughnut',
        data: {
            labels: Object.keys(methods),
            datasets: [{ data: Object.values(methods), backgroundColor: ['#3b82f6', '#8b5cf6', '#22d3ee'], borderColor: '#131c31', borderWidth: 3 }],
        },
        options: { plugins: chartDefaults },
    });

    const total = Object.values(methods).reduce((a, b) => a + b, 0);
    const pct = (key) => (total ? ((methods[key] || 0) * 100 / total).toFixed(1) : 0);
    $('#summary-body').innerHTML = `
        <table>
            <tr><td>UPI share</td><td class="mono"><b>${pct('UPI')}%</b></td></tr>
            <tr><td>Card share</td><td class="mono"><b>${pct('CARD')}%</b></td></tr>
            <tr><td>Wallet share</td><td class="mono"><b>${pct('WALLET')}%</b></td></tr>
            <tr><td>Total charged</td><td class="mono"><b>${inr(total)}</b></td></tr>
        </table>
        <p style="font-size:12px;margin-top:10px">Dynamic congestion pricing applies traffic, weather, pollution and peak-hour surcharges on every crossing.</p>`;
}

document.addEventListener('DOMContentLoaded', async () => {
    const [rev, charts] = await Promise.allSettled([loadRevenue(), loadCharts()]);
    if (rev.reason) toast('Revenue: ' + rev.reason.message, 'err');
    if (charts.reason) toast('Charts: ' + charts.reason.message, 'err');
});