/**
 * Displays an alert message.
 * @param {string} message - The message to display.
 * @param {string} type - The alert type (e.g., 'success', 'danger').
 */
function showAlert(message, type) {
    const alertDiv = document.createElement('div');
    alertDiv.className = `alert alert-${type} alert-dismissible fade show`;
    alertDiv.innerHTML = `
        ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    `;
    const container = document.querySelector('.container');
    if (container) {
        container.prepend(alertDiv);
        setTimeout(() => alertDiv.remove(), 5000);
    }
}

/**
 * Loads and displays the list of trains, categorized by type.
 * @returns {Promise<void>}
 */
async function loadTrains() {
    try {
        const response = await fetch('/api/reservation/trains');
        if (!response.ok) throw new Error(`Failed to fetch trains: ${response.status}`);
        /** @type {Array<{trainNumber: number, trainName: string, source: string, destination: string, availableSeats: number, baseFare: number, departureTime: string, daysOfOperation: string, type: string}>} */
        const trains = await response.json();

        const loading = document.getElementById('loading');
        const featuredTrains = document.querySelector('#featured-trains .row');
        const passengerTrains = document.querySelector('#passenger-trains .row');
        const expressTrains = document.querySelector('#express-trains .row');

        // Clear existing content
        featuredTrains.innerHTML = '';
        passengerTrains.innerHTML = '';
        expressTrains.innerHTML = '';

        // Categorize trains
        trains.forEach(train => {
            const card = document.createElement('div');
            card.className = `col-12 col-md-6 col-lg-4 ${train.type.toLowerCase().replace(' ', '-')}`;
            card.innerHTML = `
                <div class="card">
                    <div class="card-body">
                        <h5 class="card-title">${train.trainName} (${train.trainNumber})</h5>
                        <p class="card-text">
                            <strong>Route:</strong> ${train.source} to ${train.destination}<br>
                            <strong>Seats:</strong> ${train.availableSeats}<br>
                            <strong>Fare:</strong> ₹${train.baseFare}<br>
                            <strong>Departure:</strong> ${train.departureTime} (${train.daysOfOperation})
                        </p>
                        <a href="/book?trainNumber=${train.trainNumber}" class="btn btn-primary">Book Ticket</a>
                    </div>
                </div>
            `;

            // Assign to the appropriate category
            if (train.type === 'Vande Bharat') {
                featuredTrains.appendChild(card);
            } else if (train.type === 'Passenger') {
                passengerTrains.appendChild(card);
            } else if (train.type === 'Express') {
                expressTrains.appendChild(card);
            }
        });

        // Show/hide sections based on content
        document.getElementById('featured-trains').style.display = featuredTrains.children.length > 0 ? 'block' : 'none';
        document.getElementById('passenger-trains').style.display = passengerTrains.children.length > 0 ? 'block' : 'none';
        document.getElementById('express-trains').style.display = expressTrains.children.length > 0 ? 'block' : 'none';

        loading.style.display = 'none';
    } catch (error) {
        console.warn('Error loading trains:', error);
        showAlert('Failed to load trains.', 'danger');
        document.getElementById('loading').style.display = 'none';
    }
}

/**
 * Loads and displays the list of tickets.
 * @returns {Promise<void>}
 */
async function loadTickets() {
    try {
        const response = await fetch('/api/reservation/tickets');
        if (!response.ok) throw new Error(`Failed to fetch tickets: ${response.status}`);
        /** @type {Array<{ticketId: number, train: {trainName: string, trainNumber: number}, passenger: {name: string}, seatNumber: number, status: string, payment: {amount: number}}>} */
        const tickets = await response.json();

        const loading = document.getElementById('loading');
        const ticketList = document.getElementById('ticket-list');
        const noTickets = document.getElementById('no-tickets');

        if (ticketList && noTickets) {
            ticketList.innerHTML = '';
            if (tickets.length === 0) {
                loading.style.display = 'none';
                ticketList.style.display = 'none';
                noTickets.style.display = 'block';
                return;
            }

            tickets.forEach(ticket => {
                ticket.seats = undefined;
                ticket.ticket = undefined;
                const ticketId = ticket.ticketId ?? ticket.ticket?.ticketId ?? 'N/A';
                const trainName = ticket.train?.trainName ?? 'Unknown';
                const trainNumber = ticket.train?.trainNumber ?? 'N/A';
                const passengerName = ticket.passenger?.name ?? 'Unknown';
                const seatNumber = ticket.seatNumber ?? (ticket.seats?.join(', ') || 'N/A');
                const status = ticket.status ?? 'Unknown';
                const amount = ticket.payment?.amount ?? '0';

                const card = document.createElement('div');
                card.className = 'col-12 col-md-6 col-lg-4';
                card.innerHTML = `
                    <div class="card">
                        <div class="card-body">
                            <h5 class="card-title">Ticket ID: ${ticketId}</h5>
                            <p class="card-text">
                                <strong>Train:</strong> ${trainName} (${trainNumber})<br>
                                <strong>Passenger:</strong> ${passengerName}<br>
                                <strong>Seat:</strong> ${seatNumber}<br>
                                <strong>Status:</strong> ${status}<br>
                                <strong>Amount:</strong> ₹${amount}
                            </p>
                        </div>
                    </div>
                `;
                ticketList.appendChild(card);
            });

            loading.style.display = 'none';
            ticketList.style.display = 'block';
            noTickets.style.display = 'none';
        }
    } catch (error) {
        console.warn('Error loading tickets:', error);
        showAlert(`Failed to load tickets: ${error.message}`, 'danger');
        const loading = document.getElementById('loading');
        if (loading) loading.style.display = 'none';
    }
}

/**
 * Books a ticket using form data.
 * @param {Event} event - The form submit event.
 * @returns {Promise<void>}
 */
async function bookTicket(event) {
    event.preventDefault();
    const trainNumber = new URLSearchParams(window.location.search).get('trainNumber');
    const form = document.getElementById('book-ticket-form');
    const data = {
        passengerDTO: {
            name: form.name.value,
            age: parseInt(form.age.value),
            contact: form.contact.value,
            aadhaar: form.aadhaar.value
        },
        paymentDTO: {
            paymentMethod: form.paymentMethod.value,
            paymentDetails: form.paymentDetails.value
        }
    };
    try {
        const response = await fetch(`/api/reservation/book?trainNumber=${trainNumber}`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)}
        );
        if (!response.ok) {
            const error = await response.text();
            throw new Error(error);
        }
        /** @type {{ticketId: number, seatNumber: string, train: {trainName: string, trainNumber: number}, passenger: {name: string}, status: string, amount: number}} */
        const ticket = await response.json();
        showAlert(`Ticket booked! Ticket ID: ${ticket.ticketId}, Seat: ${ticket.seatNumber}`, 'success');
        form.reset();
    } catch (error) {
        console.warn('Error booking ticket:', error);
        showAlert(`Failed to book ticket: ${error.message}`, 'danger');
    }
}

/**
 * Cancels a ticket by ID.
 * @param {Event} event - The form submit event.
 * @returns {Promise<void>}
 */
async function cancelTicket(event) {
    event.preventDefault();
    const ticketId = document.getElementById('ticketId').value;
    try {
        const response = await fetch(`/api/reservation/cancel/${ticketId}`, {
            method: 'DELETE'
        });
        if (!response.ok) {
            const error = await response.text();
            throw new Error(error);
        }
        showAlert(`Ticket ${ticketId} canceled successfully.`, 'success');
        document.getElementById('cancel-ticket-form').reset();
        if (document.getElementById('ticket-list')) {
            await loadTickets();
        }
    } catch (error) {
        console.warn('Error canceling ticket:', error);
        showAlert(`Failed to cancel ticket: ${error.message}`, 'danger');
    }
}

// Initialize page
document.addEventListener('DOMContentLoaded', () => {
    const trainList = document.getElementById('featured-trains') || document.getElementById('passenger-trains') || document.getElementById('express-trains');
    const ticketList = document.getElementById('ticket-list');

    if (trainList) {
        loadTrains().catch(error => console.warn('Failed to load trains:', error));
    }
    if (ticketList) {
        loadTickets().catch(error => console.warn('Failed to load tickets:', error));
    }
});