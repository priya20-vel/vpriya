let products = JSON.parse(localStorage.getItem('products')) || [];
let cart = JSON.parse(localStorage.getItem('cart')) || [];
let orders = JSON.parse(localStorage.getItem('orders')) || [];
let users = JSON.parse(localStorage.getItem('users')) || {};
let loggedInUser = null;

function showHome() {
    hideAll();
    document.getElementById('home').classList.remove('hidden');
    displayProducts();
}

function showBuyProduct() {
    hideAll();
    document.getElementById('buy-product').classList.remove('hidden');
    displayProducts();  // Use the same function to show products
}

function showCart() {
    hideAll();
    document.getElementById('cart').classList.remove('hidden');
    displayCart();
}

function showSellProduct() {
    hideAll();
    document.getElementById('sell-product').classList.remove('hidden');
}

function showLogin() {
    hideAll();
    document.getElementById('login').classList.remove('hidden');
}

function showRegister() {
    hideAll();
    document.getElementById('register').classList.remove('hidden');
}

function showOrders() {
    hideAll();
    document.getElementById('orders').classList.remove('hidden');
    displayOrders();
}

function hideAll() {
    document.getElementById('home').classList.add('hidden');
    document.getElementById('buy-product').classList.add('hidden');
    document.getElementById('cart').classList.add('hidden');
    document.getElementById('sell-product').classList.add('hidden');
    document.getElementById('login').classList.add('hidden');
    document.getElementById('register').classList.add('hidden');
    document.getElementById('orders').classList.add('hidden');
}

function addProduct() {
    const name = document.getElementById('product-name').value;
    const price = document.getElementById('product-price').value;
    const image = document.getElementById('product-image').value;

    const product = { name, price, image };
    products.push(product);
    localStorage.setItem('products', JSON.stringify(products));
    alert('Product added!');

    // Reset fields
    document.getElementById('product-name').value = '';
    document.getElementById('product-price').value = '';
    document.getElementById('product-image').value = '';

    // Show updated products in both sections
    displayProducts();
}

function displayProducts() {
    const productContainerHome = document.getElementById('products');
    const productContainerBuy = document.getElementById('buy-products');

    // Clear both containers
    productContainerHome.innerHTML = '';
    productContainerBuy.innerHTML = '';

    products.forEach((product, index) => {
        const productDiv = document.createElement('div');
        productDiv.className = 'product';
        productDiv.innerHTML = `
            <img src="${product.image}" alt="${product.name}">
            <div>
                <h3>${product.name}</h3>
                <p>Price: ₹${product.price}</p>
                <button onclick="addToCart(${index})">Add to Cart</button>
            </div>
        `;
        productContainerHome.appendChild(productDiv);
        productContainerBuy.appendChild(productDiv.cloneNode(true)); // Clone for the buy section
    });
}

function addToCart(index) {
    const product = products[index];
    cart.push(product);
    localStorage.setItem('cart', JSON.stringify(cart));
    alert('Product added to cart!');
}

// Remaining functions (displayCart, confirmOrder, login, register, etc.) remain unchanged

// Load initial products
displayProducts();
