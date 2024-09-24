

// Load feed from localStorage when the page is loaded
document.addEventListener('DOMContentLoaded', () => {
    loadFeed();
});

function createPost() {
    const content = document.getElementById('post-content').value;
    if (content.trim() === '') {
        alert("Post cannot be empty!");
        return;
    }

    const post = {
        content: content,
        timestamp: new Date().toLocaleString(),
        likes: 0 // Initialize likes
    };

    // Save the post in localStorage
    let posts = JSON.parse(localStorage.getItem('posts')) || [];
    posts.push(post);
    localStorage.setItem('posts', JSON.stringify(posts));

    // Clear the textarea
    document.getElementById('post-content').value = '';

    // Reload the feed
    loadFeed();
}

function loadFeed() {
    const feed = document.getElementById('feed');
    feed.innerHTML = '';

    const posts = JSON.parse(localStorage.getItem('posts')) || [];

    posts.forEach((post, index) => {
        const postDiv = document.createElement('div');
        postDiv.textContent = `${post.content} (Posted on ${post.timestamp})`;

        const likeBtn = document.createElement('button');
        likeBtn.textContent = `Like (${post.likes})`;
        likeBtn.classList.add('like-btn');
        likeBtn.onclick = function() {
            // Increment the likes for the current post
            post.likes++;
            posts[index].likes = post.likes; // Update likes count in the array
            localStorage.setItem('posts', JSON.stringify(posts));
            loadFeed(); // Reload the feed to reflect the updated likes
        };

        postDiv.appendChild(likeBtn);
        feed.appendChild(postDiv);
    });
}

