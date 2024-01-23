document.addEventListener('DOMContentLoaded', function() {
    fetch('/pdf-files')  // Assuming the endpoint is the same. Adjust if necessary.
        .then(response => response.json())
        .then(files => {
            const listElement = document.getElementById('campaign-list');
            files.forEach(file => {
                const listItem = document.createElement('li');
                listItem.textContent = "<a href='/pdf/"+file+"'>"+file+"</a>";
                listElement.appendChild(listItem);
            });
        })
        .catch(error => console.error('Error fetching campaign files:', error));
});
