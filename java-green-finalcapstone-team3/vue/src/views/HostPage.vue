<template>
  <div class="background-container">
    <!-- Background Image -->
    <div class="background-image"></div>

    <div class="host-page">
      <!-- HEADER with Back Button -->
      <header class="host-header">
        <h1 class="host-title"></h1>
        <button class="back-btn" @click="goToLanding">Back to Landing</button>
      </header>A

      <!-- Event List Section with Tape Effect -->
      <section class="event-list-section">
        <div class="tape">
          <h2 class="events-title">My Assigned Events</h2>
        </div>

        <!-- Show events if available -->
        <ul v-if="events.length > 0" class="event-list">
          <li v-for="event in events" :key="event.eventID" class="event-item">
            <span class="event-name">{{ event.name }}</span>
            <span class="event-date">{{ formatDate(event.eventDate) }}</span>
            <span class="event-time">{{ formatTime(event.startTime) }} - {{ formatTime(event.endTime) }}</span>
          </li>
        </ul>

        <!-- Show message if no events found -->
        <p v-else class="no-events">No events assigned to you.</p>
      </section>
    </div>
  </div>
</template>

<script>
export default {
  name: 'HostPage',
  data() {
    return {
      events: []
    };
  },
  methods: {
    // Navigates back to the landing page
    goToLanding() {
      this.$router.push({ name: 'landing' });
    },

    // Fetch all events and filter by host ID
    async fetchMyEvents() {
      try {
        const hostId = this.$store.state.user.id;
        const response = await this.$axios.get(`${import.meta.env.VITE_REMOTE_API}/events`);

        // ✅ Filter events where `event.hosts` includes the logged-in host's ID
        this.events = response.data.filter(event => event.hosts && event.hosts.includes(hostId));

        // Debugging: Log the filtered events
        console.log("Filtered Events for Host:", this.events);
      } catch (error) {
        console.error('Error fetching events for host:', error);
      }
    },

    // Format date (e.g., "2025-06-15" → "Jun 15, 2025")
    formatDate(dateStr) {
      const dateObj = new Date(dateStr);
      const monthNames = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];
      return `${monthNames[dateObj.getMonth()]} ${dateObj.getDate()}, ${dateObj.getFullYear()}`;
    },

    // Format time (e.g., "18:00:00" → "6:00 PM")
    formatTime(timeStr) {
      if (!timeStr) return "";
      const [hour, minute] = timeStr.split(':').map(Number);
      const suffix = hour >= 12 ? 'PM' : 'AM';
      const formattedHour = hour % 12 || 12;
      return `${formattedHour}:${minute.toString().padStart(2, '0')} ${suffix}`;
    }
  },

  // Fetch host events when the component loads
  mounted() {
    this.fetchMyEvents();
  }
};
</script>

<style scoped>
@font-face {
  font-family: 'DANGSIKINKTRAP';
  src: url('/fonts/DANGSIKINKTRAP.otf') format('opentype'),
       url('/fonts/DANGSIKINKTRAP.ttf') format('truetype');
}

@font-face {
  font-family: 'DANGSIK';
  src: url('/fonts/DANGSIK.otf') format('opentype'),
       url('/fonts/DANGSIK.ttf') format('truetype');
}

/* 🌟 Background Container */
.background-container {
  position: relative;
  min-height: 100vh;
  background-color: rgba(0, 0, 0, 0.5); /* To overlay content on top of the image */
}

/* 🌟 Background Image */
.background-image {
  background-image: url('/hostbackground.png'); /* Correct reference for files in the public folder */
  background-size: cover;
  background-position: center;
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: -1; /* Ensure background stays behind content */
}

/* 🌟 Host Page Styling */
.host-page {
  color: #fff;
  font-family: 'DANGSIK', sans-serif; /* Apply the font family */
  padding: 2rem;
  text-align: left; /* Left-align the content */
  position: relative;
}

/* 🌟 Header */
.host-header {
  display: flex;
  justify-content: flex-start; /* Left-align the header content */
  align-items: center;
  margin-bottom: 2rem;
}
.host-header h1 {
  font-size: 3rem;
  text-shadow: 2px 2px 4px #000;
}
.back-btn {
  background: transparent;
  border: 2px solid #fff;
  color: #fff;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  cursor: pointer;
  transition: background 0.3s ease;
}
.back-btn:hover {
  background: #fff;
  color: #3a47d5;
}

/* 🌟 Event List Section with Tape Effect */
.event-list-section {
  background-image: url('/transparenttape.png'); /* The tape is replaced by transparenttape.png */
  background-size: contain;
  background-position: center;
  background-repeat: no-repeat;
  padding: 4rem 2rem;
  border-radius: 8px;
  max-width: 900px;
  margin: 0 auto;
  margin-top: 20px;
  color: black;
  text-align: center;
  position: relative;
}

/* 🌟 "My Assigned Events" text on tape */
.tape {
  position: relative;
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  font-family: 'DANGSIKINKTRAP', sans-serif; /* Using DANGSIKINKTRAP font */
}

.events-title {
  font-family: 'DANGSIKINKTRAP', sans-serif;
  font-weight: bold;
  font-size: 3rem;
  color: black; /* Text color for the tape */
  text-shadow: 2px 2px 4px #fff; /* Optional: Add a shadow for better visibility */
  position: absolute;
  top: 20px;
  left: 50%;
  transform: translateX(-50%); /* Centering the text */
}

/* 🌟 Event List */
.event-list {
  list-style: none;
  padding: 0;
  text-align: left;
  margin: 0 auto;
  max-width: 600px;
}
.event-item {
  background: rgba(255, 255, 255, 0.2);
  padding: 1rem;
  border-radius: 6px;
  margin-bottom: 1rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 1.1rem;
}
.event-name {
  font-weight: bold;
}
.event-date,
.event-time {
  font-style: italic;
}

/* 🌟 No Events Message */
.no-events {
  font-size: 1.2rem;
  font-style: italic;
  color: #e0e0e0;
  margin-top: 1rem;
}

/* 🌟 Host Page Greeting */
.host-title {
  font-family: 'DANGSIK', sans-serif;
  font-weight: bold;
  color: #fff;
  text-align: left; /* Left-align the greeting text */
  font-size: 2.5rem;
}
</style>
