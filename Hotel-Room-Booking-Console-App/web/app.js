/**
 * ==========================================================================
 * STAYEASE PMS - APP CONTROLLER & STATE ENGINE
 * ==========================================================================
 */

// 1. DATA MASTER RECORDS (Expanded Room Inventory - 20 Rooms)
const ROOMS_INVENTORY = [
    // Floor 1: Single Rooms (Base: ₹1,500.00 / night, Capacity: 1)
    { roomNumber: "101", type: "SINGLE", displayName: "Single", price: 1500.00, maxCapacity: 1, amenities: "Wi-Fi, Smart TV, Workspace, AC, Hot Water" },
    { roomNumber: "102", type: "SINGLE", displayName: "Single", price: 1500.00, maxCapacity: 1, amenities: "Wi-Fi, Smart TV, Workspace, AC, Hot Water" },
    { roomNumber: "103", type: "SINGLE", displayName: "Single", price: 1500.00, maxCapacity: 1, amenities: "Wi-Fi, Smart TV, Workspace, AC, Hot Water" },
    { roomNumber: "104", type: "SINGLE", displayName: "Single", price: 1500.00, maxCapacity: 1, amenities: "Wi-Fi, Smart TV, Workspace, AC, Hot Water" },
    { roomNumber: "105", type: "SINGLE", displayName: "Single", price: 1500.00, maxCapacity: 1, amenities: "Wi-Fi, Smart TV, Workspace, AC, Hot Water" },
    
    // Floor 2: Double Rooms (Base: ₹2,800.00 / night, Capacity: 2)
    { roomNumber: "201", type: "DOUBLE", displayName: "Double", price: 2800.00, maxCapacity: 2, amenities: "Wi-Fi, Smart TV, Mini-Fridge, AC, Queen Bed" },
    { roomNumber: "202", type: "DOUBLE", displayName: "Double", price: 2800.00, maxCapacity: 2, amenities: "Wi-Fi, Smart TV, Mini-Fridge, AC, Queen Bed" },
    { roomNumber: "203", type: "DOUBLE", displayName: "Double", price: 2800.00, maxCapacity: 2, amenities: "Wi-Fi, Smart TV, Mini-Fridge, AC, Queen Bed" },
    { roomNumber: "204", type: "DOUBLE", displayName: "Double", price: 2800.00, maxCapacity: 2, amenities: "Wi-Fi, Smart TV, Mini-Fridge, AC, Queen Bed" },
    { roomNumber: "205", type: "DOUBLE", displayName: "Double", price: 2800.00, maxCapacity: 2, amenities: "Wi-Fi, Smart TV, Mini-Fridge, AC, Queen Bed" },
    
    // Floor 3: Deluxe Rooms (Base: ₹4,500.00 / night, Capacity: 3)
    { roomNumber: "301", type: "DELUXE", displayName: "Deluxe", price: 4500.00, maxCapacity: 3, amenities: "Balcony, Wi-Fi, King Bed, Mini-Bar, AC, Sofa" },
    { roomNumber: "302", type: "DELUXE", displayName: "Deluxe", price: 4500.00, maxCapacity: 3, amenities: "Balcony, Wi-Fi, King Bed, Mini-Bar, AC, Sofa" },
    { roomNumber: "303", type: "DELUXE", displayName: "Deluxe", price: 4500.00, maxCapacity: 3, amenities: "Balcony, Wi-Fi, King Bed, Mini-Bar, AC, Sofa" },
    { roomNumber: "304", type: "DELUXE", displayName: "Deluxe", price: 4500.00, maxCapacity: 3, amenities: "Balcony, Wi-Fi, King Bed, Mini-Bar, AC, Sofa" },
    { roomNumber: "305", type: "DELUXE", displayName: "Deluxe", price: 4500.00, maxCapacity: 3, amenities: "Balcony, Wi-Fi, King Bed, Mini-Bar, AC, Sofa" },
    
    // Floor 4: Suite Rooms (Base: ₹8,000.00 / night, Capacity: 4)
    { roomNumber: "401", type: "SUITE", displayName: "Suite", price: 8000.00, maxCapacity: 4, amenities: "Living Area, Kitchenette, Jacuzzi tub, Luxury Bedding, AC" },
    { roomNumber: "402", type: "SUITE", displayName: "Suite", price: 8000.00, maxCapacity: 4, amenities: "Living Area, Kitchenette, Jacuzzi tub, Luxury Bedding, AC" },
    { roomNumber: "403", type: "SUITE", displayName: "Suite", price: 8000.00, maxCapacity: 4, amenities: "Living Area, Kitchenette, Jacuzzi tub, Luxury Bedding, AC" },
    { roomNumber: "404", type: "SUITE", displayName: "Suite", price: 8000.00, maxCapacity: 4, amenities: "Living Area, Kitchenette, Jacuzzi tub, Luxury Bedding, AC" },
    { roomNumber: "405", type: "SUITE", displayName: "Suite", price: 8000.00, maxCapacity: 4, amenities: "Living Area, Kitchenette, Jacuzzi tub, Luxury Bedding, AC" }
];

const TAX_RATE = 0.10; // 10% tax rate

// 2. STATE STORAGE LEDGER
let bookings = [];

// Initialize App
document.addEventListener("DOMContentLoaded", () => {
    loadStateFromStorage();
    setupHeaderWidgets();
    setupTabNavigation();
    setupFormsAndControls();
    
    // Core Refresh
    refreshAllViews();
});

// Load state from local storage
function loadStateFromStorage() {
    const saved = localStorage.getItem("grand_horizon_bookings");
    if (saved) {
        try {
            bookings = JSON.parse(saved);
        } catch (e) {
            console.error("Corrupted LocalStorage, loading empty bookings ledger.");
            bookings = [];
        }
    } else {
        bookings = [];
    }
}

// Save state to local storage
function saveStateToStorage() {
    localStorage.setItem("grand_horizon_bookings", JSON.stringify(bookings));
}

// Format number into Indian Rupees currency string
function formatINR(number) {
    return "₹" + number.toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
}

// 3. PAGE FRAME & WIDGET CONTROL
function setupHeaderWidgets() {
    // Current System Date
    const dateEl = document.getElementById("current-system-date");
    const today = new Date();
    const options = { weekday: 'long', year: 'numeric', month: 'short', day: 'numeric' };
    dateEl.textContent = today.toLocaleDateString('en-US', options);

    // Default Date Inputs in Search Form
    const checkInInput = document.getElementById("search-check-in");
    const checkOutInput = document.getElementById("search-check-out");
    if (checkInInput && checkOutInput) {
        const tomorrow = new Date();
        tomorrow.setDate(today.getDate() + 1);

        checkInInput.value = today.toISOString().split('T')[0];
        checkInInput.min = today.toISOString().split('T')[0];
        checkOutInput.value = tomorrow.toISOString().split('T')[0];
        checkOutInput.min = tomorrow.toISOString().split('T')[0];
    }
}

function setupTabNavigation() {
    const menuItems = document.querySelectorAll(".menu-item");
    const panels = document.querySelectorAll(".tab-panel");
    const pageTitle = document.getElementById("page-title");

    menuItems.forEach(item => {
        item.addEventListener("click", (e) => {
            e.preventDefault();
            
            // Remove active status
            menuItems.forEach(mi => mi.classList.remove("active"));
            panels.forEach(p => p.classList.remove("active-panel"));

            // Set active
            item.classList.add("active");
            const targetId = item.getAttribute("data-tab");
            const targetPanel = document.getElementById(targetId);
            if (targetPanel) {
                targetPanel.classList.add("active-panel");
            }

            // Title changes
            pageTitle.textContent = item.querySelector("span").textContent + " Overview";
            
            // Refresh on switch
            refreshAllViews();
        });
    });
}

// ==========================================
// CORE REFRESH FUNCTIONS
// ==========================================
function refreshAllViews() {
    // 1. Metric Counts
    updateDashboardMetrics();
    // 2. SVG Line Graph & Bar charts
    renderOccupancyChart();
    renderCategoryAllocationBars();
    // 3. Transactions List
    populateRecentBookingsTable();
    // 4. Ledger Table
    populateLedgerTable();
    // 5. Room Inventory Specification Table
    populateInventoryTable();
}

function updateDashboardMetrics() {
    const todayStr = new Date().toISOString().split('T')[0];
    
    // Dynamic Total Rooms count
    document.getElementById("dash-total-rooms").textContent = ROOMS_INVENTORY.length + " Rooms";

    // Occupied rooms today: active booking status covers today
    const occupiedNumbers = bookings.filter(b => 
        (b.status === "CONFIRMED" || b.status === "IN_HOUSE") &&
        todayStr >= b.checkIn && todayStr < b.checkOut
    ).map(b => b.roomNumber);

    const vacantCount = ROOMS_INVENTORY.length - occupiedNumbers.length;
    document.getElementById("dash-vacant").textContent = vacantCount + " Rooms";

    // Active Bookings count
    const activeCount = bookings.filter(b => b.status === "CONFIRMED" || b.status === "IN_HOUSE").length;
    document.getElementById("dash-active").textContent = activeCount + " Bookings";

    // Estimated Revenue summation (using numerical floats)
    const revenue = bookings.reduce((sum, b) => {
        if (b.status !== "CANCELLED") {
            return sum + b.totalAmount;
        }
        return sum;
    }, 0);
    document.getElementById("dash-revenue").textContent = formatINR(revenue);
}

// ==========================================
// INTERACTIVE DATA REPRESENTATION (CHARTS)
// ==========================================
function renderOccupancyChart() {
    // Create mock line data based on bookings list to make chart dynamic
    const days = ["Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"];
    const values = [5, 8, 7, 11, 14, 18, 12]; // Scaled up occupancy numbers for 20 rooms
    
    // Adjust sat/sun values dynamically based on live confirmed bookings
    const activeReservations = bookings.filter(b => b.status === "CONFIRMED" || b.status === "IN_HOUSE");
    if (activeReservations.length > 0) {
        values[4] = Math.min(ROOMS_INVENTORY.length, values[4] + activeReservations.length);
        values[5] = Math.min(ROOMS_INVENTORY.length, values[5] + activeReservations.length);
    }

    const svg = document.getElementById("occupancy-svg");
    const gridLines = document.getElementById("chart-grid-lines");
    const points = document.getElementById("chart-points");
    const labels = document.getElementById("chart-labels");
    const linePath = document.getElementById("chart-line-path");
    const areaPath = document.getElementById("chart-area-path");

    if (!svg || !gridLines || !points || !labels || !linePath || !areaPath) return;

    // Reset SVGs
    gridLines.innerHTML = "";
    points.innerHTML = "";
    labels.innerHTML = "";

    const chartWidth = 440;
    const chartHeight = 150;
    const xStart = 40;
    const yStart = 20;
    const maxVal = ROOMS_INVENTORY.length; // Max inventory count (20)

    // Draw horizontal grid lines
    for (let i = 0; i <= 4; i++) {
        const y = yStart + (chartHeight / 4) * i;
        const lineVal = Math.round(maxVal - (maxVal / 4) * i);

        // Grid lines
        const line = document.createElementNS("http://www.w3.org/2000/svg", "line");
        line.setAttribute("x1", xStart);
        line.setAttribute("y1", y);
        line.setAttribute("x2", xStart + chartWidth);
        line.setAttribute("y2", y);
        line.setAttribute("class", "chart-grid-line");
        gridLines.appendChild(line);

        // Grid value label
        const txt = document.createElementNS("http://www.w3.org/2000/svg", "text");
        txt.setAttribute("x", xStart - 15);
        txt.setAttribute("y", y + 4);
        txt.setAttribute("class", "chart-label-text");
        txt.textContent = lineVal;
        labels.appendChild(txt);
    }

    // Calculate chart coordinate points
    const step = chartWidth / (days.length - 1);
    let pathD = "";
    let areaD = `M ${xStart} ${yStart + chartHeight} `;

    for (let i = 0; i < days.length; i++) {
        const x = xStart + step * i;
        const ratio = values[i] / maxVal;
        const y = yStart + chartHeight - (chartHeight * ratio);

        // Path definitions
        if (i === 0) {
            pathD += `M ${x} ${y} `;
        } else {
            pathD += `L ${x} ${y} `;
        }
        areaD += `L ${x} ${y} `;

        // Point Circles
        const circ = document.createElementNS("http://www.w3.org/2000/svg", "circle");
        circ.setAttribute("cx", x);
        circ.setAttribute("cy", y);
        circ.setAttribute("r", 4);
        circ.setAttribute("class", "chart-pt");
        
        // Tooltip hover actions
        const titleText = `${days[i]}: ${values[i]} of ${maxVal} Rooms occupied`;
        const title = document.createElementNS("http://www.w3.org/2000/svg", "title");
        title.textContent = titleText;
        circ.appendChild(title);

        points.appendChild(circ);

        // Day label text
        const lbl = document.createElementNS("http://www.w3.org/2000/svg", "text");
        lbl.setAttribute("x", x - 10);
        lbl.setAttribute("y", yStart + chartHeight + 15);
        lbl.setAttribute("class", "chart-label-text");
        lbl.textContent = days[i];
        labels.appendChild(lbl);
    }

    areaD += `L ${xStart + chartWidth} ${yStart + chartHeight} Z`;

    // Animate path string values
    linePath.setAttribute("d", pathD);
    areaPath.setAttribute("d", areaD);
}

function renderCategoryAllocationBars() {
    const statsContainer = document.getElementById("allocation-stats");
    if (!statsContainer) return;

    // Count how many rooms of each category have active bookings today
    const todayStr = new Date().toISOString().split('T')[0];
    const activeRooms = bookings.filter(b => 
        (b.status === "CONFIRMED" || b.status === "IN_HOUSE") &&
        todayStr >= b.checkIn && todayStr < b.checkOut
    ).map(b => b.roomNumber);

    const categories = {
        SINGLE: { displayName: "Single", max: ROOMS_INVENTORY.filter(r => r.type === "SINGLE").length, count: 0, fillClass: "fill-single" },
        DOUBLE: { displayName: "Double", max: ROOMS_INVENTORY.filter(r => r.type === "DOUBLE").length, count: 0, fillClass: "fill-double" },
        DELUXE: { displayName: "Deluxe", max: ROOMS_INVENTORY.filter(r => r.type === "DELUXE").length, count: 0, fillClass: "fill-deluxe" },
        SUITE: { displayName: "Suite", max: ROOMS_INVENTORY.filter(r => r.type === "SUITE").length, count: 0, fillClass: "fill-suite" }
    };

    activeRooms.forEach(num => {
        const room = ROOMS_INVENTORY.find(r => r.roomNumber === num);
        if (room && categories[room.type]) {
            categories[room.type].count++;
        }
    });

    statsContainer.innerHTML = "";
    Object.keys(categories).forEach(key => {
        const cat = categories[key];
        const percent = (cat.count / cat.max) * 100;

        const barItem = document.createElement("div");
        barItem.className = "allocation-item";
        barItem.innerHTML = `
            <div class="allocation-info">
                <span class="allocation-lbl">${cat.displayName} Rooms</span>
                <span class="allocation-count">${cat.count} / ${cat.max} Occupied</span>
            </div>
            <div class="allocation-bar-bg">
                <div class="allocation-bar-fill ${cat.fillClass}" style="width: ${percent}%"></div>
            </div>
        `;
        statsContainer.appendChild(barItem);
    });
}

function populateRecentBookingsTable() {
    const tbody = document.querySelector("#tbl-recent-bookings tbody");
    if (!tbody) return;

    tbody.innerHTML = "";
    if (bookings.length === 0) {
        tbody.innerHTML = `<tr><td colspan="7" class="empty-state">No transaction records found.</td></tr>`;
        return;
    }

    // Sort showing the latest transaction first, displaying max 4
    const list = [...bookings].reverse().slice(0, 4);

    list.forEach(b => {
        const tr = document.createElement("tr");
        tr.innerHTML = `
            <td><strong>${b.bookingId}</strong></td>
            <td>${b.guestName}</td>
            <td>Room ${b.roomNumber}</td>
            <td>${b.checkIn}</td>
            <td>${b.checkOut}</td>
            <td><strong>${formatINR(b.totalAmount)}</strong></td>
            <td><span class="badge ${getStatusBadgeClass(b.status)}">${b.status}</span></td>
        `;
        tbody.appendChild(tr);
    });
}

// ==========================================
// TAB 2: ROOM FINDER & SCHEDULING
// ==========================================
function setupFormsAndControls() {
    // 1. Search Vacant Rooms form
    const frmAvailability = document.getElementById("frm-availability");
    if (frmAvailability) {
        frmAvailability.addEventListener("submit", (e) => {
            e.preventDefault();
            searchAvailableRooms();
        });
    }

    // 2. Booking Drawer controls
    const btnCloseDrawer = document.getElementById("btn-close-booking-drawer");
    const btnCancelDrawer = document.getElementById("btn-cancel-booking-drawer");
    if (btnCloseDrawer) btnCloseDrawer.addEventListener("click", closeBookingDrawer);
    if (btnCancelDrawer) btnCancelDrawer.addEventListener("click", closeBookingDrawer);

    const frmCreateBooking = document.getElementById("frm-create-booking");
    if (frmCreateBooking) {
        frmCreateBooking.addEventListener("submit", (e) => {
            e.preventDefault();
            submitNewReservation();
        });
    }

    // Guest Count Selector in Drawer -> Sync nightly rates on change
    const selectGuestCount = document.getElementById("guest-count");
    if (selectGuestCount) {
        selectGuestCount.addEventListener("change", () => {
            const roomNumber = document.getElementById("book-room-number").value;
            const checkIn = document.getElementById("book-check-in").value;
            const checkOut = document.getElementById("book-check-out").value;
            updateDrawerBillingPanel(roomNumber, checkIn, checkOut);
        });
    }

    // 3. Modal Receipt Panel
    const btnCloseInv = document.getElementById("btn-close-invoice");
    const btnCloseInvOk = document.getElementById("btn-close-invoice-ok");
    const btnPrintInv = document.getElementById("btn-print-invoice");

    if (btnCloseInv) btnCloseInv.addEventListener("click", closeInvoiceModal);
    if (btnCloseInvOk) btnCloseInvOk.addEventListener("click", closeInvoiceModal);
    if (btnPrintInv) {
        btnPrintInv.addEventListener("click", () => {
            window.print();
        });
    }

    // 4. Ledger Filters
    const btnFilter = document.getElementById("btn-ledger-filter");
    const btnClear = document.getElementById("btn-ledger-clear");
    const txtSearch = document.getElementById("ledger-search-input");

    if (btnFilter) btnFilter.addEventListener("click", populateLedgerTable);
    if (btnClear) {
        btnClear.addEventListener("click", () => {
            txtSearch.value = "";
            populateLedgerTable();
        });
    }
}

function searchAvailableRooms() {
    const checkIn = document.getElementById("search-check-in").value;
    const checkOut = document.getElementById("search-check-out").value;
    const roomType = document.getElementById("search-room-type").value;

    const resultsGrid = document.getElementById("search-results-grid");
    const resultsHeading = document.getElementById("results-heading");
    const resultsCounter = document.getElementById("results-counter");

    if (!checkIn || !checkOut) {
        showToast("Please select valid dates.", "error");
        return;
    }

    const start = new Date(checkIn);
    const end = new Date(checkOut);
    const today = new Date();
    today.setHours(0,0,0,0);

    if (start < today) {
        showToast("Check-in date cannot be in the past.", "error");
        return;
    }

    if (end <= start) {
        showToast("Check-out date must be after check-in.", "error");
        return;
    }

    // Filter matching rooms
    const matchedRooms = ROOMS_INVENTORY.filter(room => room.type === roomType);

    // Filter out rooms that overlap with existing confirmed/in-house bookings
    const vacantRooms = matchedRooms.filter(room => {
        const overlap = bookings.some(b => 
            b.roomNumber === room.roomNumber &&
            (b.status === "CONFIRMED" || b.status === "IN_HOUSE") &&
            (checkIn < b.checkOut && checkOut > b.checkIn)
        );
        return !overlap;
    });

    resultsGrid.innerHTML = "";
    resultsHeading.style.display = "flex";
    resultsCounter.textContent = `${vacantRooms.length} Available`;

    if (vacantRooms.length === 0) {
        resultsGrid.innerHTML = `
            <div class="empty-state-card">
                <i class="fa-solid fa-triangle-exclamation icon-large" style="color: var(--color-red);"></i>
                <p>No rooms of type ${roomType} are vacant for the chosen date range.</p>
            </div>
        `;
        return;
    }

    vacantRooms.forEach(room => {
        const card = document.createElement("div");
        card.className = "room-card";
        card.innerHTML = `
            <div class="room-card-header">
                <span class="room-lbl">Room ${room.roomNumber}</span>
                <span class="badge badge-teal">${room.displayName}</span>
            </div>
            <div class="room-card-body">
                <div class="room-price-row">
                    <span class="room-price-val">₹${room.price.toLocaleString('en-IN')}</span>
                    <span class="room-price-unit">/ night</span>
                </div>
                <div class="room-capacity-row">
                    <i class="fa-solid fa-users"></i>
                    <span>Capacity: Max ${room.maxCapacity} Guest${room.maxCapacity > 1 ? 's' : ''}</span>
                </div>
                <div class="room-capacity-row" style="margin-top:-4px;">
                    <i class="fa-solid fa-circle-check" style="color: var(--color-primary);"></i>
                    <span style="font-size:10px;">${room.amenities}</span>
                </div>
            </div>
            <div class="room-card-footer">
                <button type="button" class="btn btn-teal w-full" onclick="openBookingDrawer('${room.roomNumber}', '${checkIn}', '${checkOut}')">
                    <i class="fa-solid fa-receipt"></i> Book Room
                </button>
            </div>
        `;
        resultsGrid.appendChild(card);
    });
}

// Global functions for grid event mapping
window.openBookingDrawer = function(roomNumber, checkIn, checkOut) {
    const drawer = document.getElementById("booking-drawer");
    const room = ROOMS_INVENTORY.find(r => r.roomNumber === roomNumber);
    if (!room || !drawer) return;

    // Fields
    document.getElementById("book-room-number").value = roomNumber;
    document.getElementById("book-check-in").value = checkIn;
    document.getElementById("book-check-out").value = checkOut;

    // Summary labels
    document.getElementById("summary-room-num").textContent = `Room ${roomNumber}`;
    document.getElementById("summary-room-type").textContent = `${room.displayName} Category (₹${room.price.toLocaleString('en-IN')}/night)`;
    document.getElementById("summary-dates").textContent = `${checkIn} to ${checkOut}`;

    // Fill capacity spinner options as per specific room capacity
    const selectGuest = document.getElementById("guest-count");
    selectGuest.innerHTML = "";
    for (let i = 1; i <= room.maxCapacity; i++) {
        const opt = document.createElement("option");
        opt.value = i;
        opt.textContent = `${i} Guest${i > 1 ? 's' : ''}`;
        selectGuest.appendChild(opt);
    }

    // Billing Panel calculation
    updateDrawerBillingPanel(roomNumber, checkIn, checkOut);

    drawer.classList.add("active");
};

function closeBookingDrawer() {
    const drawer = document.getElementById("booking-drawer");
    if (drawer) {
        drawer.classList.remove("active");
        document.getElementById("frm-create-booking").reset();
    }
}

function updateDrawerBillingPanel(roomNumber, checkIn, checkOut) {
    const room = ROOMS_INVENTORY.find(r => r.roomNumber === roomNumber);
    if (!room) return;

    const start = new Date(checkIn);
    const end = new Date(checkOut);
    let nights = Math.ceil((end - start) / (1000 * 60 * 60 * 24));
    if (nights <= 0) nights = 1;

    const subtotal = room.price * nights;
    const taxes = subtotal * TAX_RATE;
    const total = subtotal + taxes;

    document.getElementById("calc-subtotal").textContent = formatINR(subtotal);
    document.getElementById("calc-taxes").textContent = formatINR(taxes);
    document.getElementById("calc-total").textContent = formatINR(total);
}

function submitNewReservation() {
    const roomNumber = document.getElementById("book-room-number").value;
    const checkIn = document.getElementById("book-check-in").value;
    const checkOut = document.getElementById("book-check-out").value;
    
    const name = document.getElementById("guest-name").value.trim();
    const phone = document.getElementById("guest-phone").value.trim();
    const email = document.getElementById("guest-email").value.trim();
    const guestCount = parseInt(document.getElementById("guest-count").value);

    const room = ROOMS_INVENTORY.find(r => r.roomNumber === roomNumber);
    if (!room) return;

    // Check duplicate overlays at submittal time
    const doubleBooked = bookings.some(b => 
        b.roomNumber === roomNumber &&
        (b.status === "CONFIRMED" || b.status === "IN_HOUSE") &&
        (checkIn < b.checkOut && checkOut > b.checkIn)
    );

    if (doubleBooked) {
        showToast("Double-booking clash detected. This room is no longer vacant for these dates.", "error");
        return;
    }

    const start = new Date(checkIn);
    const end = new Date(checkOut);
    let nights = Math.ceil((end - start) / (1000 * 60 * 60 * 24));
    if (nights <= 0) nights = 1;

    const subtotal = room.price * nights;
    const taxes = subtotal * TAX_RATE;
    const total = subtotal + taxes;

    // Generate Booking ID (BK-10001...)
    let maxId = 10000;
    bookings.forEach(b => {
        const idPart = b.bookingId;
        if (idPart.startsWith("BK-")) {
            const num = parseInt(idPart.substring(3));
            if (!isNaN(num) && num > maxId) {
                maxId = num;
            }
        }
    });
    const bookingId = "BK-" + (maxId + 1);

    const newBooking = {
        bookingId,
        guestName: name,
        guestPhone: phone,
        guestEmail: email,
        roomNumber,
        roomType: room.type,
        roomDisplayName: room.displayName,
        checkIn,
        checkOut,
        numberOfNights: nights,
        guestCount,
        roomCharge: subtotal,
        tax: taxes,
        totalAmount: total,
        status: "CONFIRMED"
    };

    bookings.push(newBooking);
    saveStateToStorage();
    closeBookingDrawer();

    showToast(`Booking ${bookingId} successfully confirmed!`, "success");
    refreshAllViews();
    searchAvailableRooms(); // Refresh grid results
}

// ==========================================
// TAB 3: LEDGER MANAGEMENT (ACTIONS)
// ==========================================
function populateLedgerTable() {
    const tbody = document.querySelector("#tbl-ledger tbody");
    if (!tbody) return;

    const query = document.getElementById("ledger-search-input").value.trim().toLowerCase();
    tbody.innerHTML = "";

    const filtered = bookings.filter(b => 
        query === "" || 
        b.bookingId.toLowerCase().includes(query) ||
        b.guestName.toLowerCase().includes(query)
    );

    if (filtered.length === 0) {
        tbody.innerHTML = `<tr><td colspan="10" class="empty-state">No bookings match the search criteria.</td></tr>`;
        return;
    }

    filtered.forEach(b => {
        const tr = document.createElement("tr");
        tr.innerHTML = `
            <td><strong>${b.bookingId}</strong></td>
            <td>${b.guestName}</td>
            <td>${b.guestPhone}</td>
            <td>Room ${b.roomNumber}</td>
            <td>${b.checkIn}</td>
            <td>${b.checkOut}</td>
            <td>${b.numberOfNights}</td>
            <td><strong>${formatINR(b.totalAmount)}</strong></td>
            <td><span class="badge ${getStatusBadgeClass(b.status)}">${b.status}</span></td>
            <td>
                <div class="table-actions-cell">
                    ${b.status === 'CONFIRMED' ? `
                        <button class="btn-icon btn-icon-teal" onclick="ledgerCheckIn('${b.bookingId}')" title="Check-In Guest">
                            <i class="fa-solid fa-person-walking-luggage"></i>
                        </button>
                    ` : ''}
                    ${b.status === 'IN_HOUSE' ? `
                        <button class="btn-icon btn-icon-teal" onclick="ledgerCheckOut('${b.bookingId}')" title="Check-Out & Invoicing">
                            <i class="fa-solid fa-file-invoice-dollar"></i>
                        </button>
                    ` : ''}
                    ${b.status === 'CONFIRMED' ? `
                        <button class="btn-icon btn-icon-red" onclick="ledgerCancel('${b.bookingId}')" title="Cancel Booking">
                            <i class="fa-solid fa-trash-can"></i>
                        </button>
                    ` : ''}
                </div>
            </td>
        `;
        tbody.appendChild(tr);
    });
}

window.ledgerCheckIn = function(bookingId) {
    const booking = bookings.find(b => b.bookingId === bookingId);
    if (!booking) return;

    booking.status = "IN_HOUSE";
    saveStateToStorage();
    showToast(`Guest checked-in successfully for booking ${bookingId}.`, "success");
    refreshAllViews();
};

window.ledgerCheckOut = function(bookingId) {
    const booking = bookings.find(b => b.bookingId === bookingId);
    if (!booking) return;

    booking.status = "COMPLETED";
    saveStateToStorage();
    showToast(`Checkout successful for booking ${bookingId}.`, "success");

    // Print invoice pop-up
    openInvoiceModal(booking);
    refreshAllViews();
};

window.ledgerCancel = function(bookingId) {
    if (confirm(`Are you sure you want to cancel reservation ${bookingId}?`)) {
        const booking = bookings.find(b => b.bookingId === bookingId);
        if (!booking) return;

        booking.status = "CANCELLED";
        saveStateToStorage();
        showToast(`Reservation ${bookingId} has been cancelled.`, "success");
        refreshAllViews();
    }
};

// ==========================================
// INVOICE PORTAL DIALOG
// ==========================================
function openInvoiceModal(booking) {
    const modal = document.getElementById("invoice-modal");
    const container = document.getElementById("invoice-receipt-content");
    if (!modal || !container) return;

    container.innerHTML = `
        <div class="invoice-receipt">
            <div class="invoice-headline">
                <span class="invoice-title">STAYEASE PMS INVOICE</span>
                <p style="font-size:10px; color: var(--color-text-muted); margin-top:4px;">Plot 45, StayEase Square, Tech Corridor</p>
            </div>
            
            <div class="invoice-meta">
                <div class="invoice-line">
                    <span class="invoice-label">Invoice ID:</span>
                    <span class="invoice-value">INV-${booking.bookingId.substring(3)}</span>
                </div>
                <div class="invoice-line">
                    <span class="invoice-label">Booking Reference:</span>
                    <span class="invoice-value">${booking.bookingId}</span>
                </div>
                <div class="invoice-line">
                    <span class="invoice-label">Billing Date:</span>
                    <span class="invoice-value">${new Date().toISOString().split('T')[0]}</span>
                </div>
            </div>
            
            <hr class="invoice-divider">
            
            <div class="invoice-meta">
                <div class="invoice-line">
                    <span class="invoice-label">Registered Guest:</span>
                    <span class="invoice-value">${booking.guestName}</span>
                </div>
                <div class="invoice-line">
                    <span class="invoice-label">Guest Phone:</span>
                    <span class="invoice-value">${booking.guestPhone}</span>
                </div>
                <div class="invoice-line">
                    <span class="invoice-label">Guest Email:</span>
                    <span class="invoice-value">${booking.guestEmail}</span>
                </div>
            </div>

            <hr class="invoice-divider">

            <div class="invoice-meta">
                <div class="invoice-line">
                    <span class="invoice-label">Room Allocation:</span>
                    <span class="invoice-value">Room ${booking.roomNumber} (${booking.roomDisplayName})</span>
                </div>
                <div class="invoice-line">
                    <span class="invoice-label">Stay Duration:</span>
                    <span class="invoice-value">${booking.checkIn} to ${booking.checkOut} (${booking.numberOfNights} nights)</span>
                </div>
                <div class="invoice-line">
                    <span class="invoice-label">Occupants:</span>
                    <span class="invoice-value">${booking.guestCount} Guest(s)</span>
                </div>
            </div>

            <hr class="invoice-divider-dashed">

            <div class="invoice-meta">
                <div class="invoice-line">
                    <span class="invoice-label">Room Subtotal:</span>
                    <span class="invoice-value">${formatINR(booking.roomCharge)}</span>
                </div>
                <div class="invoice-line">
                    <span class="invoice-label">Taxes & surcharges (10%):</span>
                    <span class="invoice-value">${formatINR(booking.tax)}</span>
                </div>
            </div>

            <hr class="invoice-divider">

            <div class="invoice-total-section">
                <span>Grand Total:</span>
                <span>${formatINR(booking.totalAmount)}</span>
            </div>

            <div class="invoice-paid-stamp">
                <i class="fa-solid fa-stamp"></i> PAID IN FULL
            </div>
        </div>
    `;

    modal.classList.add("active");
}

function closeInvoiceModal() {
    const modal = document.getElementById("invoice-modal");
    if (modal) {
        modal.classList.remove("active");
    }
}

// ==========================================
// TAB 4: ROOM INVENTORY DETAILS TABLE
// ==========================================
function populateInventoryTable() {
    const tbody = document.querySelector("#tbl-inventory tbody");
    if (!tbody) return;

    tbody.innerHTML = "";
    ROOMS_INVENTORY.forEach(room => {
        const tr = document.createElement("tr");
        tr.innerHTML = `
            <td><strong>Room ${room.roomNumber}</strong></td>
            <td><span class="badge ${getCategoryBadgeClass(room.type)}">${room.displayName}</span></td>
            <td><strong>${formatINR(room.price)}</strong></td>
            <td>Max ${room.maxCapacity} Guest${room.maxCapacity > 1 ? 's' : ''}</td>
            <td><span style="font-size: 11px; color: var(--color-text-muted);">${room.amenities}</span></td>
        `;
        tbody.appendChild(tr);
    });
}

// ==========================================
// HELPER UTILITIES
// ==========================================
function getStatusBadgeClass(status) {
    switch (status) {
        case "CONFIRMED": return "badge-teal";
        case "IN_HOUSE": return "badge-indigo";
        case "COMPLETED": return "badge-slate";
        case "CANCELLED": return "badge-red";
        default: return "badge-slate";
    }
}

function getCategoryBadgeClass(type) {
    switch (type) {
        case "SINGLE": return "badge-indigo";
        case "DOUBLE": return "badge-teal";
        case "DELUXE": return "badge-purple";
        case "SUITE": return "badge-teal";
        default: return "badge-slate";
    }
}

// Custom Toast Alerts
function showToast(message, type = "success") {
    const wrapper = document.getElementById("toast-wrapper");
    if (!wrapper) return;

    const toast = document.createElement("div");
    toast.className = `toast toast-${type}`;
    
    const icon = type === "success" ? "fa-circle-check" : "fa-triangle-exclamation";
    toast.innerHTML = `
        <i class="fa-solid ${icon}"></i>
        <span>${message}</span>
    `;

    wrapper.appendChild(toast);
    
    // Animate display
    setTimeout(() => {
        toast.classList.add("show");
    }, 10);

    // Autoclose
    setTimeout(() => {
        toast.classList.remove("show");
        setTimeout(() => {
            toast.remove();
        }, 400);
    }, 3500);
}
