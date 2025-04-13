document.getElementById("search-form").addEventListener("submit", function (e) {
    e.preventDefault();
    const query = document.getElementById("search-input").value;
    
    fetch(`/bin/formsearch?q=${encodeURIComponent(query)}`)
        .then(res => res.json())
        .then(data => {
            const container = document.getElementById("search-results");
            container.innerHTML = "";

            if (data.results.length === 0) {
                container.innerHTML = "<p>No results found.</p>";
            } else {
                data.results.forEach(item => {
                    container.innerHTML += `
                        <div class="search-item">
                            <h3>${item.title}</h3>
                            <p>${item.description}</p>
                            <img src="${item.image}" alt="${item.title}" />
                            <small>Last Modified: ${item.lastModified}</small>
                        </div>
                    `;
                });
            }
        });
});