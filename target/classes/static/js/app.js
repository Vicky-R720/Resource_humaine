const API_URL = 'http://localhost:8080/api/posts';

// Charger les posts au démarrage
document.addEventListener('DOMContentLoaded', function () {
    loadPosts();

    // Gestion du formulaire
    document.getElementById('postForm').addEventListener('submit', function (e) {
        e.preventDefault();
        savePost();
    });
});

// Charger tous les posts
async function loadPosts() {
    try {
        const response = await fetch(API_URL);
        const posts = await response.json();
        displayPosts(posts);
    } catch (error) {
        console.error('Erreur lors du chargement des posts:', error);
        alert('Erreur lors du chargement des posts');
    }
}

// Afficher les posts dans le tableau
function displayPosts(posts) {
    const tableBody = document.getElementById('postsTable');
    tableBody.innerHTML = '';

    if (posts.length === 0) {
        tableBody.innerHTML = '<tr><td colspan="4" class="text-center">Aucun post trouvé</td></tr>';
        return;
    }

    posts.forEach(post => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${post.name}</td>
            <td class="post-description" title="${post.description}">${post.description || '-'}</td>
            <td class="post-missions" title="${post.missions}">${post.missions || '-'}</td>
            <td>
                <button class="btn btn-sm btn-warning" onclick="editPost(${post.id})">Modifier</button>
                <button class="btn btn-sm btn-danger" onclick="deletePost(${post.id})">Supprimer</button>
            </td>
        `;
        tableBody.appendChild(row);
    });
}

// Sauvegarder un post (création ou modification)
async function savePost() {
    const postId = document.getElementById('postId').value;
    const post = {
        name: document.getElementById('name').value,
        description: document.getElementById('description').value,
        missions: document.getElementById('missions').value
    };

    const url = postId ? `${API_URL}/${postId}` : API_URL;
    const method = postId ? 'PUT' : 'POST';

    try {
        const response = await fetch(url, {
            method: method,
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(post)
        });

        if (response.ok) {
            resetForm();
            loadPosts();
            alert(postId ? 'Post modifié avec succès!' : 'Post créé avec succès!');
        } else {
            alert('Erreur lors de la sauvegarde');
        }
    } catch (error) {
        console.error('Erreur:', error);
        alert('Erreur lors de la sauvegarde');
    }
}

// Modifier un post
async function editPost(id) {
    try {
        const response = await fetch(`${API_URL}/${id}`);
        const post = await response.json();

        document.getElementById('postId').value = post.id;
        document.getElementById('name').value = post.name;
        document.getElementById('description').value = post.description || '';
        document.getElementById('missions').value = post.missions || '';
        document.getElementById('formTitle').textContent = 'Modifier le poste';

        window.scrollTo(0, 0);
    } catch (error) {
        console.error('Erreur:', error);
        alert('Erreur lors du chargement du post');
    }
}

// Supprimer un post
async function deletePost(id) {
    if (!confirm('Êtes-vous sûr de vouloir supprimer ce post?')) {
        return;
    }

    try {
        const response = await fetch(`${API_URL}/${id}`, {
            method: 'DELETE'
        });

        if (response.ok) {
            loadPosts();
            alert('Post supprimé avec succès!');
        } else {
            alert('Erreur lors de la suppression');
        }
    } catch (error) {
        console.error('Erreur:', error);
        alert('Erreur lors de la suppression');
    }
}

// Rechercher par nom
async function searchByName() {
    const name = document.getElementById('searchName').value;
    if (!name) {
        alert('Veuillez entrer un nom à rechercher');
        return;
    }

    try {
        const response = await fetch(`${API_URL}/search/name?name=${encodeURIComponent(name)}`);
        const posts = await response.json();
        displayPosts(posts);
    } catch (error) {
        console.error('Erreur:', error);
        alert('Erreur lors de la recherche');
    }
}

// Rechercher par description
async function searchByDescription() {
    const keyword = document.getElementById('searchDescription').value;
    if (!keyword) {
        alert('Veuillez entrer un mot-clé à rechercher');
        return;
    }

    try {
        const response = await fetch(`${API_URL}/search/description?keyword=${encodeURIComponent(keyword)}`);
        const posts = await response.json();
        displayPosts(posts);
    } catch (error) {
        console.error('Erreur:', error);
        alert('Erreur lors de la recherche');
    }
}

// Réinitialiser le formulaire
function resetForm() {
    document.getElementById('postForm').reset();
    document.getElementById('postId').value = '';
    document.getElementById('formTitle').textContent = 'Ajouter un nouveau poste';
}