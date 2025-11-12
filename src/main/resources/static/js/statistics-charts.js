// Configuration de base pour tous les graphiques
const chartConfig = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
        legend: {
            position: 'top',
        }
    },
    scales: {
        y: {
            beginAtZero: true,
            ticks: {
                stepSize: 1
            }
        }
    }
};

// Fonction pour créer un graphique simple
function createBarChart(canvasId, labels, data, colors) {
    const ctx = document.getElementById(canvasId).getContext('2d');
    
    return new Chart(ctx, {
        type: 'bar',
        data: {
            labels: labels,
            datasets: [{
                label: 'Effectif',
                data: data,
                backgroundColor: colors,
                borderColor: colors.map(color => color.replace('0.8', '1')),
                borderWidth: 2,
                borderRadius: 8,
            }]
        },
        options: chartConfig
    });
}

// Fonction pour extraire les labels et valeurs des données Thymeleaf
function extractData(thymeleafData) {
    if (!thymeleafData) return { labels: [], values: [] };
    
    const labels = thymeleafData.map(item => item.label);
    const values = thymeleafData.map(item => item.value);
    
    return { labels, values };
}

// Fonction pour mettre à jour les compteurs KPI
function updateKPIcounters() {
    // Compteur masculin
    const masculinData = statsData.genre?.find(item => item.label === 'M');
    if (masculinData) {
        document.getElementById('masculinCount').textContent = masculinData.value;
    }
    
    // Compteur féminin
    const femininData = statsData.genre?.find(item => item.label === 'F');
    if (femininData) {
        document.getElementById('femininCount').textContent = femininData.value;
    }
}

// Initialisation de tous les graphiques
function initializeCharts() {
    // Mettre à jour les KPI
    updateKPIcounters();
    
    // Graphique Genre
    const genreData = extractData(statsData.genre);
    if (genreData.labels.length > 0) {
        createBarChart('genderChart', genreData.labels, genreData.values, ['#667eea', '#764ba2']);
    }
    
    // Graphique Contrat
    const contratData = extractData(statsData.contrat);
    if (contratData.labels.length > 0) {
        const contractColors = ['#667eea', '#764ba2', '#f093fb', '#f5576c', '#4facfe'];
        createBarChart('contractChart', contratData.labels, contratData.values, contractColors);
    }
    
    // Graphique Service
    const serviceData = extractData(statsData.service);
    if (serviceData.labels.length > 0) {
        createBarChart('serviceChart', serviceData.labels, serviceData.values, ['#43e97b']);
    }
    
    // Graphique Tranche d'âge (si disponible)
    const ageData = extractData(statsData.age);
    if (ageData.labels.length > 0) {
        createBarChart('ageChart', ageData.labels, ageData.values, ['#ff6b6b']);
    }
}

// Démarrer quand la page est chargée
document.addEventListener('DOMContentLoaded', function() {
    initializeCharts();
});