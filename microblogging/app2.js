// Fetch and display posts
function fetchPosts() {
  fetch('/posts')
    .then(response => response.json())
    .then(posts => {
      const postList = document.getElementById('post-list');
      postList.innerHTML = '';
      posts.forEach(post => {
        const listItem = document.createElement('li');
        listItem.textContent = `${post.timestamp}: ${post.username} - ${post.message}`;
        postList.appendChild(listItem);
      });
    });
}

// Post a message
function postMessage() {
  const username = document.getElementById('username').value;
  const message = document.getElementById('message').value;
  fetch('/post', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({ username, message })
  })
  .then(response => response.json())
  .then(() => {
    fetchPosts();
    document.getElementById('message').value = '';
  });
}

// Follow a user
function followUser() {
  const follower = document.getElementById('follower').value;
  const following = document.getElementById('following').value;
  fetch('/follow', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({ follower, following })
  })
  .then(response => response.json())
  .then(() => {
    alert(`Started following ${following}`);
  });
}

// Initial fetch
fetchPosts();
