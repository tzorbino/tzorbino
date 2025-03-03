<template>
    <div class="create-playlist">
      <header class="playlist-header">
        <h1>Create Playlist</h1>
        <button class="back-btn" @click="goToDJPage">Back to DJ Dashboard</button>
      </header>
      <form @submit.prevent="submitPlaylist">
        <div class="form-group">
          <label for="playlistName">Playlist Name:</label>
          <input type="text" id="playlistName" v-model="playlist.name" required />
        </div>
        <div class="form-group">
          <label for="event">Select Event:</label>
          <select id="event" v-model="playlist.eventId" required>
            <option disabled value="">Please select an event</option>
            <option v-for="event in events" :key="event.eventID" :value="event.eventID">
              {{ event.name }} ({{ event.eventDate }})
            </option>
          </select>
        </div>
        <div class="form-group">
          <label>Songs (Coming Soon):</label>
          <p class="placeholder">This feature will be implemented soon. Stay tuned!</p>
        </div>
        <button type="submit">Create Playlist</button>
      </form>
      <div v-if="errorMessage" class="error">{{ errorMessage }}</div>
      <div v-if="successMessage" class="success">{{ successMessage }}</div>
    </div>
  </template>
  
  <script>
  import EventService from '@/services/EventService';
  
  export default {
    name: 'CreatePlaylistView',
    data() {
      return {
        playlist: {
          name: '',
          eventId: ''
        },
        events: [],
        errorMessage: '',
        successMessage: ''
      };
    },
    methods: {
      async fetchEvents() {
        try {
          // Fetch events created by the current DJ.
          // You may want to filter based on the current DJ's ID.
          const response = await EventService.getEvents('', '');
          this.events = response.data;
        } catch (error) {
          console.error('Error fetching events:', error);
          this.errorMessage = 'Failed to load events.';
        }
      },
      goToDJPage() {
        this.$router.push({ name: 'djPage' });
      },
      async submitPlaylist() {
        this.errorMessage = '';
        this.successMessage = '';
        if (!this.playlist.name || !this.playlist.eventId) {
          this.errorMessage = 'Please fill in all fields.';
          return;
        }
        try {
          // Simulate playlist creation API call.
          // Replace this with an actual call to your backend when available.
          setTimeout(() => {
            this.successMessage = 'Playlist created successfully!';
            // Optionally, reset the form.
            this.playlist = { name: '', eventId: '' };
          }, 1000);
        } catch (error) {
          console.error('Error creating playlist:', error);
          this.errorMessage = 'Error creating playlist.';
        }
      }
    },
    mounted() {
      this.fetchEvents();
    }
  };
  </script>
  
  <style scoped>
  .create-playlist {
    max-width: 600px;
    margin: 2rem auto;
    padding: 1rem;
    background: linear-gradient(135deg, #ff0099, #493240);
    color: #fff;
    font-family: 'Courier New', Courier, monospace;
    border-radius: 8px;
    box-shadow: 0 4px 8px rgba(0,0,0,0.3);
  }
  .playlist-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 1.5rem;
  }
  .playlist-header h1 {
    font-size: 2.5rem;
    text-shadow: 2px 2px #000;
  }
  .back-btn {
    background: transparent;
    border: 2px solid #fff;
    color: #fff;
    padding: 0.5rem 1rem;
    cursor: pointer;
    border-radius: 4px;
    transition: background 0.3s ease;
  }
  .back-btn:hover {
    background: #fff;
    color: #493240;
  }
  .form-group {
    margin-bottom: 1rem;
    display: flex;
    flex-direction: column;
  }
  label {
    margin-bottom: 0.5rem;
  }
  input, select {
    padding: 0.5rem;
    font-size: 1rem;
    border: none;
    border-radius: 4px;
  }
  button[type="submit"] {
    padding: 0.75rem 1.5rem;
    font-size: 1rem;
    cursor: pointer;
    border: none;
    border-radius: 4px;
    background-color: #fff;
    color: #493240;
    transition: background-color 0.3s ease;
  }
  button[type="submit"]:hover {
    background-color: #493240;
    color: #fff;
  }
  .placeholder {
    font-style: italic;
    color: #ccc;
  }
  .error {
    color: #ff6b6b;
    margin-top: 1rem;
  }
  .success {
    color: #7bed9f;
    margin-top: 1rem;
  }
  </style>
  