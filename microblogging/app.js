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

    // Display existing comments
    comments.forEach(commentText => {
        const comment = document.createElement('p');
        comment.className = 'comment';
        comment.innerHTML = `👤 ${commentText}`; // Display emoji and comment text only
        commentSection.appendChild(comment);
    });

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
            comment.innerHTML = `👤 ${commentText}`; // Display emoji and comment text only
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
