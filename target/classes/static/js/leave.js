// leave.js

// ---------------- Calendar ----------------
function initCalendar() {
    const serviceSelect = document.getElementById('serviceSelect');
    const calendarEl = document.getElementById('calendar');

    let calendar = new FullCalendar.Calendar(calendarEl, {
        initialView: 'dayGridMonth',
        events: []
    });

    function loadEvents() {
        const serviceId = serviceSelect.value;
        fetch(`/api/leave-calendar/service/${serviceId}`)
            .then(res => res.json())
            .then(events => {
                calendar.removeAllEvents();
                calendar.addEventSource(events);
            });
    }

    serviceSelect.addEventListener('change', loadEvents);

    calendar.render();
    loadEvents();
}

// ---------------- Leave Form ----------------


// ---------------- Dashboard ----------------
function initDashboard() {
    const btn = document.getElementById('loadBalances');
    const personnelIdInput = document.getElementById('personnelId');
    const balancesDiv = document.getElementById('balances');
    const totalDiv = document.getElementById('totalRemaining');

    btn.addEventListener('click', async () => {
        const personnelId = personnelIdInput.value;
        const res = await fetch(`/api/leave-dashboard/balances/${personnelId}`);
        const data = await res.json();

        balancesDiv.innerHTML = '<ul>' +
            data.balances.map(b => `<li>${b.leaveTypeName}: ${b.soldeRestant}</li>`).join('') +
            '</ul>';
        totalDiv.innerText = 'Total restant: ' + data.totalRemaining;
    });
}

// ---------------- History ----------------
function initHistory() {
    const btn = document.getElementById('loadHistory');
    const personnelIdInput = document.getElementById('personnelIdHistory');
    const tbody = document.getElementById('historyTable');

    btn.addEventListener('click', async () => {
        const personnelId = personnelIdInput.value;
        const res = await fetch(`/api/leave-dashboard/history/${personnelId}`);
        const data = await res.json();

        tbody.innerHTML = data.map(h => `
            <tr>
                <td>${h.dateDebut}</td>
                <td>${h.dateFin}</td>
                <td>${h.motif}</td>
                <td>${h.leaveTypeName}</td>
                <td>${h.statut}</td>
            </tr>
        `).join('');
    });
}
