let currentUser = null;

// Load posts from localStorage
function loadPosts() {
    const posts = JSON.parse(localStorage.getItem('posts')) || [];
    posts.forEach(post => {
        createPost(post.content, post.likes, post.dislikes, post.comments);
    });
    document.getElementById('count').innerText = posts.length;
}

// Login functionality
document.getElementById('login-btn').addEventListener('click', () => {
    const usernameInput = document.getElementById('username');
    currentUser = usernameInput.value.trim();

    if (currentUser) {
        document.getElementById('user-display').innerText = currentUser;
        document.getElementById('login-section').style.display = 'none';
        document.getElementById('post-section').style.display = 'block';
        loadPosts(); // Load posts on login
    } else {
        alert("Please enter a username");
    }
});

// Post functionality
document.getElementById('post-btn').addEventListener('click', () => {
    const postInput = document.getElementById('post-input');
    const postContent = postInput.value.trim();

    if (postContent) {
        createPost(postContent);
        postInput.value = '';
        updatePostCount();
    } else {
        alert("Please write something to post");
    }
});

// Create a post
function createPost(content, likes = 0, dislikes = 0, comments = []) {
    const postContainer = document.getElementById('posts-container');
   
    const postDiv = document.createElement('div');
    postDiv.className = 'post';

    const postText = document.createElement('p');
    postText.innerText = content;
    postDiv.appendChild(postText);

    const reactionsDiv = document.createElement('div');
    reactionsDiv.className = 'reactions';

    const likeButton = document.createElement('button');
    likeButton.innerHTML = `👍 <span class="like-count">${likes}</span>`;
    likeButton.addEventListener('click', () => {
        likes++;
        likeButton.querySelector('.like-count').innerText = likes;
        updateLocalStorage();
    });

    const dislikeButton = document.createElement('button');
    dislikeButton.innerHTML = `👎 <span class="dislike-count">${dislikes}</span>`;
    dislikeButton.addEventListener('click', () => {
        dislikes++;
        dislikeButton.querySelector('.dislike-count').innerText = dislikes;
        updateLocalStorage();
    });

    reactionsDiv.appendChild(likeButton);
    reactionsDiv.appendChild(dislikeButton);
   
    const commentSection = document.createElement('div');
    commentSection.className = 'comments';
    const commentCount = document.createElement('p');
    commentCount.className = 'comment-count';
    commentCount.innerText = `Comments: ${comments.length}`;
    commentSection.appendChild(commentCount);

    const commentInput = document.createElement('input');
    commentInput.type = 'text';
    commentInput.placeholder = 'Add a comment...';
    commentInput.className = 'comment-input';

    const commentButton = document.createElement('button');
    commentButton.innerText = 'Comment';
    commentButton.addEventListener('click', () => {
        const commentText = commentInput.value.trim();
        if (commentText) {
            const comment = document.createElement('p');
            comment.className = 'comment';
            comment.innerHTML = `👤 ${currentUser}: ${commentText}`;
            commentSection.appendChild(comment);
            comments.push(commentText);
            commentCount.innerText = `Comments: ${comments.length}`;
            commentInput.value = '';
            updateLocalStorage();
        }
    });

    commentSection.appendChild(commentInput);
    commentSection.appendChild(commentButton);
   
    postDiv.appendChild(reactionsDiv);
    postDiv.appendChild(commentSection);
    postContainer.prepend(postDiv);

    updateLocalStorage();
}

// Update local storage
function updateLocalStorage() {
    const posts = [];
    const postElements = document.querySelectorAll('.post');

    postElements.forEach(postElement => {
        const content = postElement.querySelector('p').innerText;
        const likes = postElement.querySelector('.like-count').innerText;
        const dislikes = postElement.querySelector('.dislike-count').innerText;
        const comments = Array.from(postElement.querySelectorAll('.comments .comment')).map(comment => comment.innerText);

        posts.push({ content, likes: parseInt(likes), dislikes: parseInt(dislikes), comments });
    });

    localStorage.setItem('posts', JSON.stringify(posts));
}

// Update post count
function updatePostCount() {
    const postCount = document.querySelectorAll('.post').length;
    document.getElementById('count').innerText = postCount;
}


