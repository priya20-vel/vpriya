// index.js
const express = require('express');
const bodyParser = require('body-parser');
const app = express();
const port = 3000;

// Use body-parser middleware
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));

// Serve static files from the 'public' directory
app.use(express.static('public'));

// Store posts and followers in memory
const posts = [];
const followers = {};

// Home route
app.get('/', (req, res) => {
  res.sendFile(__dirname + '/views/index.html');
});

// Get all posts
app.get('/posts', (req, res) => {
  res.json(posts);
});

// Post a new message
app.post('/post', (req, res) => {
  const { username, message } = req.body;
  if (!username || !message) {
    return res.status(400).json({ error: 'Username and message are required' });
  }
  const post = { username, message, timestamp: new Date() };
  posts.push(post);
  res.json(post);
});

// Follow a user
app.post('/follow', (req, res) => {
  const { follower, following } = req.body;
  if (!follower || !following) {
    return res.status(400).json({ error: 'Follower and following usernames are required' });
  }
  if (!followers[following]) {
    followers[following] = [];
  }
  if (!followers[following].includes(follower)) {
    followers[following].push(follower);
  }
  res.json({ follower, following });
});

app.listen(port, () => {
  console.log(`Server running at http://localhost:${port}`);
});
