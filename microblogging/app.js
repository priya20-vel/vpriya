

let currentUser = null;
let selectedPost = null; // Variable to keep track of the selected post

// Load posts from localStorage
function loadPosts() {
    const posts = JSON.parse(localStorage.getItem('posts')) || [];
    posts.forEach(post => {
        createPost(post.content, post.likes, post.dislikes, post.comments, post.author);
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
        createPost(postContent, 0, 0, [], currentUser); // Pass currentUser as author
        postInput.value = '';
        updatePostCount();
    } else {
        alert("Please write something to post");
    }
});

// Create a post
function createPost(content, likes = 0, dislikes = 0, comments = [], author) {
    const postContainer = document.getElementById('posts-container');
    const postDiv = document.createElement('div');
    postDiv.className = 'post';

    postDiv.tabIndex = 0; // Make the post focusable
    postDiv.addEventListener('focus', () => {
        selectedPost = postDiv; // Set the selected post when focused
    });
    postDiv.addEventListener('blur', () => {
        if (selectedPost === postDiv) {
            selectedPost = null; // Clear selection if focus is lost
        }
    });

    const postText = document.createElement('p');
    postText.innerText = content;
    postDiv.appendChild(postText);

    // Display the author of the post
    const authorText = document.createElement('small');
    authorText.innerText = `Posted by: ${author}`;
    postDiv.appendChild(authorText);

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
    commentButton.innerHTML = '💬'; // Comment emoji
    commentButton.addEventListener('click', () => {
        const commentText = commentInput.value.trim();
        if (commentText) {
            const comment = document.createElement('p');
            comment.className = 'comment';
            comment.innerHTML = `👤 ${commentText}`; // Show emoji with the comment text
            commentSection.appendChild(comment);
            comments.push(commentText);
            commentCount.innerText = `Comments: ${comments.length}`;
            commentInput.value = '';
            updateLocalStorage();
        }
    });

    // Load existing comments
    comments.forEach(commentText => {
        const comment = document.createElement('p');
        comment.className = 'comment';
        comment.innerHTML = `👤 ${commentText}`; // Show emoji with the comment text
        commentSection.appendChild(comment);
    });

    commentSection.appendChild(commentInput);
    commentSection.appendChild(commentButton);

    postDiv.appendChild(reactionsDiv);
    postDiv.appendChild(commentSection);
    postContainer.prepend(postDiv);

    updateLocalStorage();
}

// Listen for the delete key
document.addEventListener('keydown', (event) => {
    if (event.key === 'Delete' && selectedPost) {
        selectedPost.remove(); // Remove post from the DOM
        updateLocalStorage(); // Update local storage
        updatePostCount(); // Update post count
        selectedPost = null; // Clear selection after deletion
    }
});

// Update local storage
function updateLocalStorage() {
    const posts = [];
    const postElements = document.querySelectorAll('.post');

    postElements.forEach(postElement => {
        const content = postElement.querySelector('p').innerText;
        const likes = postElement.querySelector('.like-count').innerText;
        const dislikes = postElement.querySelector('.dislike-count').innerText;
        const comments = Array.from(postElement.querySelectorAll('.comments .comment')).map(comment => comment.innerText);
        const author = postElement.querySelector('small').innerText.replace('Posted by: ', ''); // Extract author name

        posts.push({ content, likes: parseInt(likes), dislikes: parseInt(dislikes), comments, author });
    });

    localStorage.setItem('posts', JSON.stringify(posts));
}

// Update post count
function updatePostCount() {
    const postCount = document.querySelectorAll('.post').length;
    document.getElementById('count').innerText = postCount;
}
