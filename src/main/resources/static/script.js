const BASE_URL = "http://localhost:8080";

function showSignup() {
    document.getElementById("loginCard").style.display = "none";
    document.getElementById("signupCard").style.display = "block";
}

function showLogin() {
    document.getElementById("signupCard").style.display = "none";
    document.getElementById("loginCard").style.display = "block";
}

async function signupUser() {
    const email = document.getElementById("signupEmail").value;
    const password = document.getElementById("signupPassword").value;
    const role = document.getElementById("signupRole").value;

    try {
        const response = await fetch(BASE_URL + "/auth/signup", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ email, password, role })
        });

        const data = await response.text();

        if (response.ok) {
            document.getElementById("signupMsg").style.color = "green";
            document.getElementById("signupMsg").innerText = data + " Please login now.";
        } else {
            document.getElementById("signupMsg").style.color = "red";
            document.getElementById("signupMsg").innerText = data;
        }
    } catch (err) {
        document.getElementById("signupMsg").innerText = "Something went wrong. Is backend running?";
    }
}

async function loginUser() {
    const email = document.getElementById("loginEmail").value;
    const password = document.getElementById("loginPassword").value;

    try {
        const response = await fetch(BASE_URL + "/auth/login", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ email, password })
        });

        // Read as text first — never crashes unlike response.json()
        const text = await response.text();

        // Try to parse as JSON, fall back to plain string
        let data;
        try {
            data = JSON.parse(text);
        } catch {
            data = text;
        }

        if (response.ok) {
            const json = typeof data === "string" ? JSON.parse(data) : data;
            localStorage.setItem("token", json.token);
            localStorage.setItem("email", email);

            const payload = JSON.parse(atob(json.token.split(".")[1]));
            localStorage.setItem("role", payload.role);

            if (payload.role === "ADMIN") {
                window.location.href = "admin.html";
            } else {
                window.location.href = "index.html";
            }
        } else {
            // Works whether backend sends JSON {"message":"..."} or plain text
            const msg = (typeof data === "object" ? data.message : data) || "Invalid email or password.";
            document.getElementById("loginMsg").innerText = msg;
        }
    } catch (err) {
        document.getElementById("loginMsg").innerText = "Something went wrong. Is backend running?";
    }
}

function logout() {
    localStorage.clear();
    window.location.href = "index.html";
}

function renderNavbar() {
    const navLinks = document.getElementById("navLinks");
    if (!navLinks) return;

    const token = localStorage.getItem("token");
    const role = localStorage.getItem("role");

    if (token && role === "USER") {
        navLinks.innerHTML = `
            <a href="index.html">Rooms</a>
            <a href="my-bookings.html">My Bookings</a>
            <a href="#" onclick="logout()">Logout</a>
        `;
    } else if (token && role === "ADMIN") {
        navLinks.innerHTML = `
            <a href="admin.html">Admin Dashboard</a>
            <a href="#" onclick="logout()">Logout</a>
        `;
    } else {
        navLinks.innerHTML = `
            <a href="login.html">Login</a>
        `;
    }
}


// ===========================================
// ROOM IMAGE MAPPING
// maps room type to a representative image — falls back to a generic hotel room photo
// ===========================================

function getRoomImage(roomType) {
    const type = roomType.toLowerCase();

    if (type.includes("deluxe")) {
        return "https://images.unsplash.com/photo-1591088398332-8a7791972843?q=80&w=1200&auto=format&fit=crop";
    } else if (type.includes("suite")) {
        return "https://images.unsplash.com/photo-1631049307264-da0ec9d70304?q=80&w=1200&auto=format&fit=crop";
    } else if (type.includes("standard")) {
        return "https://images.unsplash.com/photo-1505693416388-ac5ce068fe85?q=80&w=1200&auto=format&fit=crop";
    } else {
        return "https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?q=80&w=1200&auto=format&fit=crop";
    }
}


async function loadRooms() {
    try {
        const response = await fetch(BASE_URL + "/rooms");
        const rooms = await response.json();

        const roomsList = document.getElementById("roomsList");
        roomsList.innerHTML = "";

        const token = localStorage.getItem("token");

        rooms.forEach(room => {
            const stockText = room.totalRooms > 0
                ? `<p class="stock">${room.totalRooms} rooms available</p>`
                : `<p class="out-of-stock">Sold Out</p>`;

            let bookButton;
            if (room.totalRooms <= 0) {
                bookButton = `<button disabled>Not Available</button>`;
            } else if (token) {
                bookButton = `<button onclick="openBookingModal(${room.id}, '${room.roomType}')">Book Now</button>`;
            } else {
                bookButton = `<button onclick="redirectToLogin()">Login to Book</button>`;
            }

            const imageUrl = getRoomImage(room.roomType);

            roomsList.innerHTML += `
                <div class="room-card">
                    <div class="room-image" style="background-image: url('${imageUrl}');"></div>
                    <div class="room-card-body">
                        <h3>${room.roomType}</h3>
                        <p>${room.description}</p>
                        <p class="price">₹${room.pricePerNight} / night</p>
                        ${stockText}
                        ${bookButton}
                    </div>
                </div>
            `;
        });
    } catch (err) {
        console.log("Error loading rooms", err);
    }
}

function redirectToLogin() {
    alert("Please login or signup first to book a room.");
    window.location.href = "login.html";
}

function openBookingModal(roomId, roomType) {
    document.getElementById("modalRoomId").value = roomId;
    document.getElementById("modalRoomType").innerText = "Room: " + roomType;

    // Set minimum date to today so past dates can't be selected
    const today = new Date().toISOString().split("T")[0];
    document.getElementById("checkInDate").min = today;
    document.getElementById("checkOutDate").min = today;

    document.getElementById("bookingModal").style.display = "flex";
}

function closeModal() {
    document.getElementById("bookingModal").style.display = "none";
}

async function confirmBooking() {
    const roomId = document.getElementById("modalRoomId").value;
    const checkInDate = document.getElementById("checkInDate").value;
    const checkOutDate = document.getElementById("checkOutDate").value;
    const token = localStorage.getItem("token");

    if (!checkInDate || !checkOutDate) {
        document.getElementById("bookingMsg").innerText = "Please select both dates.";
        return;
    }

    // Validate check-out is after check-in
    if (checkOutDate <= checkInDate) {
        document.getElementById("bookingMsg").innerText = "Check-out must be after check-in.";
        return;
    }

    try {
        const response = await fetch(BASE_URL + "/booking", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Authorization": "Bearer " + token
            },
            body: JSON.stringify({ roomId: Number(roomId), checkInDate, checkOutDate })
        });

        const data = await response.json();

        if (response.ok) {
            alert("Booking confirmed! Total Amount: ₹" + data.totalAmount);
            closeModal();
            loadRooms();
        } else {
            document.getElementById("bookingMsg").innerText = data.message || data || "Booking failed.";
        }
    } catch (err) {
        document.getElementById("bookingMsg").innerText = "Something went wrong.";
    }
}

async function loadMyBookings() {
    const token = localStorage.getItem("token");

    try {
        const response = await fetch(BASE_URL + "/booking/my-bookings", {
            headers: { "Authorization": "Bearer " + token }
        });

        const bookings = await response.json();
        const list = document.getElementById("bookingsList");
        list.innerHTML = "";

        if (bookings.length === 0) {
            list.innerHTML = "<p>You have no bookings yet.</p>";
            return;
        }

        bookings.forEach(booking => {
            const statusClass = booking.status === "CONFIRMED" ? "status-confirmed" : "status-cancelled";

            list.innerHTML += `
                <div class="booking-card">
                    <h3>${booking.room.roomType}</h3>
                    <p>Check-In: ${booking.checkInDate}</p>
                    <p>Check-Out: ${booking.checkOutDate}</p>
                    <p>Nights: ${booking.numberOfNights}</p>
                    <p>Total Amount: ₹${booking.totalAmount}</p>
                    <p class="${statusClass}">${booking.status}</p>
                    <button class="delete-btn" onclick="deleteBooking(${booking.id})">Cancel Booking</button>
                </div>
            `;
        });
    } catch (err) {
        console.log("Error loading bookings", err);
    }
}

async function deleteBooking(bookingId) {
    const token = localStorage.getItem("token");

    if (!confirm("Are you sure you want to cancel this booking?")) {
        return;
    }

    try {
        const response = await fetch(BASE_URL + "/booking/" + bookingId, {
            method: "DELETE",
            headers: { "Authorization": "Bearer " + token }
        });

        if (response.ok) {
            alert("Booking cancelled successfully.");
            loadMyBookings();
        } else {
            const data = await response.text();
            alert(data);
        }
    } catch (err) {
        alert("Something went wrong.");
    }
}

async function addRoom() {
    const roomType = document.getElementById("roomType").value;
    const description = document.getElementById("roomDescription").value;
    const pricePerNight = document.getElementById("roomPrice").value;
    const totalRooms = document.getElementById("roomQuantity").value;
    const token = localStorage.getItem("token");

    try {
        const response = await fetch(BASE_URL + "/admin/rooms", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Authorization": "Bearer " + token
            },
            body: JSON.stringify({
                roomType,
                description,
                pricePerNight: Number(pricePerNight),
                totalRooms: Number(totalRooms)
            })
        });

        if (response.ok) {
            document.getElementById("addRoomMsg").style.color = "green";
            document.getElementById("addRoomMsg").innerText = "Room added successfully!";

            document.getElementById("roomType").value = "";
            document.getElementById("roomDescription").value = "";
            document.getElementById("roomPrice").value = "";
            document.getElementById("roomQuantity").value = "";

            loadAdminRooms();
        } else {
            const data = await response.text();
            document.getElementById("addRoomMsg").style.color = "red";
            document.getElementById("addRoomMsg").innerText = data;
        }
    } catch (err) {
        document.getElementById("addRoomMsg").innerText = "Something went wrong.";
    }
}

async function loadAdminRooms() {
    const token = localStorage.getItem("token");

    try {
        const response = await fetch(BASE_URL + "/admin/rooms", {
            headers: { "Authorization": "Bearer " + token }
        });

        const rooms = await response.json();
        const list = document.getElementById("adminRoomsList");
        list.innerHTML = "";

        rooms.forEach(room => {
            const imageUrl = getRoomImage(room.roomType);

            list.innerHTML += `
                <div class="room-card">
                    <div class="room-image" style="background-image: url('${imageUrl}');"></div>
                    <div class="room-card-body">
                        <h3>${room.roomType}</h3>
                        <p>${room.description}</p>
                        <p class="price">₹${room.pricePerNight} / night</p>
                        <p class="stock">${room.totalRooms} rooms in stock</p>
                    </div>
                </div>
            `;
        });
    } catch (err) {
        console.log("Error loading admin rooms", err);
    }
}

async function loadAdminBookings() {
    const token = localStorage.getItem("token");

    try {
        const response = await fetch(BASE_URL + "/admin/bookings", {
            headers: { "Authorization": "Bearer " + token }
        });

        const bookings = await response.json();
        const list = document.getElementById("adminBookingsList");
        list.innerHTML = "";

        if (bookings.length === 0) {
            list.innerHTML = "<p>No bookings yet.</p>";
            return;
        }

        bookings.forEach(booking => {
            const statusClass = booking.status === "CONFIRMED" ? "status-confirmed" : "status-cancelled";

            const cancelButton = booking.status === "CONFIRMED"
                ? `<button class="delete-btn" onclick="adminCancelBooking(${booking.id})">Cancel Booking</button>`
                : "";

            list.innerHTML += `
                <div class="booking-card">
                    <h3>${booking.room.roomType}</h3>
                    <p>Booked by: ${booking.email}</p>
                    <p>Check-In: ${booking.checkInDate}</p>
                    <p>Check-Out: ${booking.checkOutDate}</p>
                    <p>Total Amount: ₹${booking.totalAmount}</p>
                    <p class="${statusClass}">${booking.status}</p>
                    ${cancelButton}
                </div>
            `;
        });
    } catch (err) {
        console.log("Error loading admin bookings", err);
    }
}

async function adminCancelBooking(bookingId) {
    const token = localStorage.getItem("token");

    if (!confirm("Are you sure you want to cancel this booking?")) {
        return;
    }

    try {
        const response = await fetch(BASE_URL + "/admin/bookings/" + bookingId + "/cancel", {
            method: "PUT",
            headers: { "Authorization": "Bearer " + token }
        });

        if (response.ok) {
            alert("Booking cancelled successfully.");
            loadAdminBookings();
        } else {
            const data = await response.text();
            alert(data);
        }
    } catch (err) {
        alert("Something went wrong.");
    }
}

renderNavbar();

if (document.getElementById("roomsList")) {
    loadRooms();
}

if (document.getElementById("bookingsList")) {
    loadMyBookings();
}

if (document.getElementById("adminRoomsList")) {
    loadAdminRooms();
    loadAdminBookings();
}