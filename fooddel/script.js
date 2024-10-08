const restaurants = [
    {
        name: "Italian Bistro",
        description: "Authentic Italian cuisine with a modern twist.",
        image: "https://media.istockphoto.com/id/453203585/photo/outdoor-trattoria-in-a-quiant-village-in-tuscany-italy.jpg?s=612x612&w=0&k=20&c=4Y5_sHnLp0GJFD0WhPlcqbYz7WJxs5khK5EKCw9IN8o=",
        rating: 4.5,
        menu: [
            { name: "Pasta", price: 12, type: "Veg", description: "Creamy Alfredo pasta.", image: "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSo-8-efyGKCbx0LO9V80U4kdjtZdARaC62_Q&s", reviews: [] },
            { name: "Pizza", price: 10, type: "Non-Veg", description: "Pepperoni pizza with fresh mozzarella.", image: "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSFb-QOETAw_2smnhPtCqsh8A1KTeBnpYEBTQ&s", reviews: [] },
            { name: "Tiramisu", price: 5, type: "Veg", description: "Classic Italian dessert.", image: "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRQXGPTb04lktwuFY6CVpvpZsO-GzBO1BsvIg&s", reviews: [] }
        ]
    },
    {
        name: "Sushi Place",
        description: "Fresh sushi and traditional Japanese dishes.",
        image: "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRwy4VQNtoUzqo7ncoQ_oXWGXzed2tHPTQqrQ&s",
        rating: 4.8,
        menu: [
            { name: "Sushi Roll", price: 8, type: "Non-Veg", description: "Delicious sushi rolls.", image: "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTvXMSP-IUeoq8AFQXb-y2va4gQIF2wQO9yPA&s", reviews: [] },
            { name: "Tempura", price: 10, type: "Veg", description: "Crispy fried vegetables.", image: "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRQHDS4k_XMABZYxPrA05U30Q20YXqsf_CqQA&s", reviews: [] },
            { name: "Miso Soup", price: 3, type: "Veg", description: "Traditional Japanese soup.", image: "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTVqXhWgF7Rq72Tz8vLiFcQ2N9-gQNKSrvB0A&s", reviews: [] }
        ]
    },
    {
        name: "Burger Hub",
        description: "Juicy burgers and crispy fries.",
        image: "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQq-k9PJWBGsAis4ikU1vpdJ3BphtgEw-IMlQ&s",
        rating: 4.6,
        menu: [
            { name: "Cheeseburger", price: 8, type: "Non-Veg", description: "Classic cheeseburger with lettuce and tomato.", image: "https://png.pngtree.com/thumb_back/fh260/background/20230615/pngtree-a-photo-big-double-cheddar-cheeseburger-with-chicken-cutlet-image_3108377.jpg", reviews: [] },
            { name: "Veggie Burger", price: 7, type: "Veg", description: "Grilled veggie burger with a side of fries.", image: "https://t4.ftcdn.net/jpg/05/76/67/47/360_F_576674780_gKgOBK76wzU2xMD8OdPxBJt6bnjn1o5D.jpg", reviews: [] },
            { name: "Fries", price: 3, type: "Veg", description: "Crispy golden fries.", image: "https://img.freepik.com/free-photo/crispy-french-fries-with-ketchup-mayonnaise_1150-26588.jpg?size=626&ext=jpg&ga=GA1.1.1819120589.1728259200&semt=ais_hybrid", reviews: [] }
        ]
    },
    {
        name: "Biryani House",
        description: "A haven for biryani lovers with a variety of options.",
        image: "https://i0.wp.com/biryanihouseaf.com/wp-content/uploads/2023/10/baryani-house.png?fit=600%2C385&ssl=1",
        rating: 4.9,
        menu: [
            { name: "Chicken Biryani", price: 15, type: "Non-Veg", description: "Aromatic basmati rice with tender chicken.", image: "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTuWkqR087ENNtJpkaDYBJ5iZx7BeaUJXfjzA&s", reviews: [] },
            { name: "Veg Biryani", price: 12, type: "Veg", description: "Spicy mixed vegetables with basmati rice.", image: "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRPbQMgD79WJwULTkrE5C-MsHcUMyKozMq-Kw&s", reviews: [] },
            { name: "Mutton Biryani", price: 18, type: "Non-Veg", description: "Flavorful biryani made with tender mutton.", image: "https://i.ytimg.com/vi/s4YsKuoYTe8/hq720.jpg?sqp=-oaymwEhCK4FEIIDSFryq4qpAxMIARUAAAAAGAElAADIQj0AgKJD&rs=AOn4CLA8DNcG2opFjrju1AlULhM7SOQLfA", reviews: [] },
            { name: "Paneer Biryani", price: 14, type: "Veg", description: "A delightful mix of paneer and spices with rice.", image: "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRsbo8jnL-eC5RlD4tQsWIzgbv-duqtza3Q3Q&s", reviews: [] }
        ]
    },
    {
        name: "Breakfast Corner",
        description: "Start your day with a delicious breakfast.",
        image: "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSk_DtlqYW4pgJDJYBP6MvpQkdPDB2RWVpbxQ&s",
        rating: 4.5,
        menu: [
            { name: "Pancakes", price: 8, type: "Veg", description: "Fluffy pancakes with syrup.", image: "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcScBg-PMuVbh98LUDEW-fkbT-qZVFceiCNkNQ&s", reviews: [] },
            { name: "Omelette", price: 7, type: "Non-Veg", description: "Three-egg omelette with cheese.", image: "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcR1J_XOcHXsAk7fhXvCrMSPxXieYrcoqQ47jQ&s", reviews: [] },
            { name: "Fruit Salad", price: 6, type: "Veg", description: "Fresh seasonal fruits.", image: "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSxfIPtUpXqx1OaBYhXo4uVld4sts3hZXW9xA&s", reviews: [] },
            { name: "Breakfast Burrito", price: 9, type: "Non-Veg", description: "Egg, sausage, and cheese wrapped in a tortilla.", image: "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTNQgdQglf-KtCV48FLBlf_qelL0LU76ixLCw&s", reviews: [] },
            { name: "Avocado Toast", price: 7, type: "Veg", description: "Toasted bread topped with smashed avocado and seasonings.", image: "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRf2kR-7_slsqwG9cbCi5oS7QxNu9nQbpJ7QA&s", reviews: [] }
        ]
    },
    {
        name: "Dinner Delights",
        description: "Exquisite dinner options for a delightful meal.",
        image: "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ47m2DufHn76ii2EyOplYU0DVwpJFggAKQcg&s",
        rating: 4.8,
        menu: [
            { name: "Steak", price: 20, type: "Non-Veg", description: "Grilled steak with garlic butter.", image: "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcT9o_01EQDnRFjVQSZBwFc3CHG2Vm3Dhw6dhg&s", reviews: [] },
            { name: "Grilled Salmon", price: 18, type: "Non-Veg", description: "Perfectly grilled salmon with herbs.", image: "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRnC2p7osU5rqS-9mXW8IVtilKYpAMKQPWdxw&s", reviews: [] },
            { name: "Vegetable Stir Fry", price: 14, type: "Veg", description: "Mixed vegetables stir-fried in a savory sauce.", image: "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTaDfuKPF05cwQw_BXzhSe91yldkA7-_aZraQ&s", reviews: [] },
            { name: "Pasta Primavera", price: 15, type: "Veg", description: "Pasta with seasonal vegetables and olive oil.", image: "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQovLG7C4mI2W8f2vqMQ9gmyE63cP2JMsmLTQ&s", reviews: [] },
            { name: "Chicken Alfredo", price: 17, type: "Non-Veg", description: "Fettuccine pasta in creamy Alfredo sauce with grilled chicken.", image: "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQdlPuh1PfRnOx8MXA6S_ez-hXI8nk_cbCu_g&s", reviews: [] }
        ]
    }
];

let cart = [];

// Function to display restaurants and their menus
function displayRestaurants() {
    const restaurantContainer = document.getElementById('restaurants');
    restaurantContainer.innerHTML = '';

    restaurants.forEach((restaurant) => {
        const restaurantDiv = document.createElement('div');
        restaurantDiv.classList.add('restaurant');
        restaurantDiv.innerHTML = `
            <h3>${restaurant.name}</h3>
            <img src="${restaurant.image}" alt="${restaurant.name}" />
            <p>${restaurant.description}</p>
            <div class="star-rating">${'★'.repeat(Math.floor(restaurant.rating))}${'☆'.repeat(5 - Math.floor(restaurant.rating))} (${restaurant.rating})</div>
        `;

        restaurant.menu.forEach((item) => {
            const menuItemDiv = document.createElement('div');
            menuItemDiv.classList.add('menu-item');
            menuItemDiv.innerHTML = `
                <img src="${item.image}" alt="${item.name}" />
                <h4>${item.name} - $${item.price} (${item.type})</h4>
                <p>${item.description}</p>
                <button onclick="addToCart('${item.name}', ${item.price}, '${item.type}')">Add to Cart</button>
                <div class="review-section">
                    <input type="number" min="1" max="5" placeholder="Rating (1-5)" id="rating-${item.name}" />
                    <input type="text" placeholder="Write a review" id="review-${item.name}" />
                    <button onclick="addReview('${item.name}')">Submit Review</button>
                    <div id="reviews-${item.name}"></div>
                </div>
            `;
            restaurantDiv.appendChild(menuItemDiv);
        });

        restaurantContainer.appendChild(restaurantDiv);
    });
}

// Function to add items to cart
function addToCart(name, price, type) {
    cart.push({ name, price, type });
    displayCart();
}

// Function to display cart
function displayCart() {
    const cartContainer = document.getElementById('cart');
    cartContainer.innerHTML = '';

    if (cart.length === 0) {
        cartContainer.innerHTML = '<p>Your cart is empty.</p>';
        document.getElementById('checkoutButton').style.display = 'none';
        return;
    }

    let total = 0;
    cart.forEach((item) => {
        total += item.price;
        cartContainer.innerHTML += `<p>${item.name} - $${item.price} (${item.type})</p>`;
    });
    cartContainer.innerHTML += `<strong>Total: $${total}</strong>`;
    document.getElementById('checkoutButton').style.display = 'block';
}

// Function to add reviews
function addReview(itemName) {
    const rating = document.getElementById(`rating-${itemName}`).value;
    const reviewText = document.getElementById(`review-${itemName}`).value;

    if (rating < 1 || rating > 5 || !reviewText) {
        alert('Please provide a valid rating and review.');
        return;
    }

    const item = findMenuItem(itemName);
    if (item) {
        item.reviews.push({ rating, reviewText });
        displayReviews(itemName);
        document.getElementById(`rating-${itemName}`).value = '';
        document.getElementById(`review-${itemName}`).value = '';
    }
}

// Helper function to find a menu item
function findMenuItem(itemName) {
    for (const restaurant of restaurants) {
        const menuItem = restaurant.menu.find(item => item.name === itemName);
        if (menuItem) return menuItem;
    }
    return null;
}

// Function to display reviews
function displayReviews(itemName) {
    const item = findMenuItem(itemName);
    const reviewsContainer = document.getElementById(`reviews-${itemName}`);
    reviewsContainer.innerHTML = '';

    if (item && item.reviews.length > 0) {
        item.reviews.forEach(review => {
            reviewsContainer.innerHTML += `<div class="review">${review.rating} stars: ${review.reviewText}</div>`;
        });
    }
}

// Checkout function
document.getElementById('checkoutButton').addEventListener('click', () => {
    alert('Proceeding to checkout!');
    cart = []; // Reset cart
    displayCart(); // Update cart display
});

// Initial call to display restaurants
displayRestaurants();

