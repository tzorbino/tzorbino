<template>
  <div class="dj-page">
    <!-- HEADER with Back Button -->
    <header class="header">
      <img src="/landingGif.gif" alt="Header GIF" class="header-gif" />
      <div class="filter-container">
        <h2 class="filter-title">Find Your Event</h2>
        <div class="filter-row">
          <input
            type="text"
            v-model="nameFilter"
            placeholder="Find your event"
            class="filter-input large-input"
          />
          <input
            type="date"
            v-model="dateFilter"
            placeholder="Filter by date"
            class="filter-input large-input"
          />
        </div>
        <div class="filter-row">
          <select v-model="djFilter" class="filter-input large-input">
            <option value="">Find your DJ</option>
            <option v-for="dj in uniqueDJs" :key="dj" :value="dj">{{ dj }}</option>
          </select>
          <label class="checkbox-label inline-checkbox">
            <input type="checkbox" v-model="filterMyEvents" />
            My events only
          </label>
        </div>
      </div>
    </header>


    
    

    <!-- EDIT PLAYLIST FORM TOGGLE -->
    <section class="create-playlist-section">
      
      <div v-if="showCreatePlaylistForm" class="create-playlist-wrapper">
        <!-- Song Search Input -->
        <input
          v-model="searchQuery"
          type="text"
          placeholder="Search for a song..."
          @input="searchSongs"
          class="search-input"
        />

        

        <!-- Button to Switch Between Adding and Deleting Songs -->
        <button @click="isAddingSongs = !isAddingSongs">
          Switch to {{ isAddingSongs ? 'Delete Songs' : 'Add Songs' }}
        </button>

        <!-- Button to Trigger Add/Delete Songs Action -->
        <button @click="isAddingSongs ? addSongsToPlaylist() : deleteSongsFromPlaylist()" class="create-btn">
          {{ isAddingSongs ? 'Add Songs to Playlist' : 'Delete Songs from Playlist' }}
        </button>
      </div>
    </section>

    <!-- EVENTS LIST -->
    <section class="events-section">
      <h2 class="section-title">My Events</h2>
      <div class="large-boxes-container">
        <div v-for="(event, index) in events" :key="index" class="large-box">
          <p>{{ event.name }}</p>
          <p>DATE: {{ formatDate(event.eventDate) }}</p>
          <p>
            TIME: {{ formatTime(event.startTime) }} - {{ formatTime(event.endTime) }}
          </p>
          <p v-if="event.createdBy">DJ: {{ getDJUsername(event.createdBy) }}</p>
        </div>
      </div>
    </section>

    

    <!-- SPOTIFY PLAYLIST SECTION -->
    <div class="playlist-section">

      <div v-if="userRole === 'ROLE_USER'" class="vote-section">
      <button v-on:click="upvote" class="vote-button upvote">▲</button>
      <p class="vote-count">{{ upvotes }}</p>
      <button v-on:click="downvote" class="vote-button downvote">▼</button>
      <p class="vote-count">{{ downvotes }}</p>
    </div>

      <!-- Dropdown to select playlist -->
      <div class="dropdown-container">
      <select v-model="selectedPlaylist" class="playlist-select">
        <option value="" disabled>Select a Playlist</option>
        <option v-for="(playlist, index) in playlists" :key="index" :value="index">
          {{ playlist.name || "Featured Playlist" }}
        </option>
      </select>
      </div>

      <!-- Show selected playlist tracks -->
      <div v-if="selectedPlaylist !== null && playlists[selectedPlaylist]">
        <h2 class="playlist-title">{{ playlists[selectedPlaylist].name || "Featured Playlist" }}</h2>
        <div v-if="playlists[selectedPlaylist].tracks.length" class="playlist-container">
          <div v-for="(track, trackIndex) in playlists[selectedPlaylist].tracks" :key="trackIndex" class="playlist-card">
            <!-- Album Cover -->
            <img :src="track.images[0]?.url" alt="Album Cover" class="album-cover" />

            <!-- Track Info -->
            <div class="track-info">
              <h3 class="track-name">{{ track.name }}</h3>
              <p class="track-artist">
                <strong>Artist:</strong> {{ track.artists }}
              </p>
              <p class="track-genre"><strong>Genre:</strong> {{ track.genre || "Unknown" }}</p>
            </div>

            <!-- Embedded Player -->
            <iframe
              :src="`https://open.spotify.com/embed/track/${track.id}`"
              class="spotify-player"
              frameborder="0"
              allow="encrypted-media"
            ></iframe>
          </div>
        </div>
      </div>
      
      <p v-if="playlists.length === 0" class="loading-text">Loading playlists...</p>
    </div>
  </div>
</template>

<script>

import EventService from '@/services/EventService';
import CreateEventView from '@/views/CreateEventView.vue';
import SpotifyService from '../services/SpotifyService';

export default {
  name: 'DJPage',
  components: { CreateEventView },
  props: ['selectedPlaylist'],
  data() {
    return {
      showCreateForm: false,
      showCreatePlaylistForm: false,
      events: [],
      userId: 4, // For testing, set dynamically in production
      userRole: 'ROLE_USER',
      djUsernames: {
        1: 'DJ Cool',
        4: 'DJ Funky',
        7: 'DJ Smooth'
      },
      playlists: [],
      token: '',
      playlistName: '',
      playlistNameInput: '', // Track the playlist name input
      searchQuery: '',
      searchResults: [],
      selectedTracks: [], // Array of selected track IDs
      availableGenres: [],
      selectedGenre: '',
      accessToken: '',
      selectedPlaylist: '',
      tracks: [],
      upvotes: 0,
      downvotes: 0
    };
  },

  watch: {
    selectedPlaylist(newIndex){
      if(newIndex !== ''){
        this.fetchTracks(this.playlists[newIndex.id]);
      }
    },
    
    selectedPlaylist(newVal, oldVal) {
      if (newVal !== oldVal) {
        this.resetVotes();
      }
    },

    userRole(newVal) {
      console.log("Updated role:", newVal);
   }
  },
    
  
  
  mounted(){
    this.getSpotifyTokenFromURL();
  },

  methods: {
    goToLanding() {
      this.$router.push({ name: 'landing' });
    },
    toggleCreateForm() {
      this.showCreateForm = !this.showCreateForm;
    },
    toggleCreatePlaylistForm() {
      this.showCreatePlaylistForm = !this.showCreatePlaylistForm;
    },
    async fetchMyEvents() {
      try {
        const response = await EventService.getEvents();
        this.events = response.data.filter(event => event.createdBy === this.userId);
      } catch (error) {
        console.error("Error fetching events:", error);
      }
    },

    

    getDJUsername(djId) {
      return this.djUsernames[djId] || `DJ ${djId}`;
    },
    formatDate(dateStr) {
      const dateObj = new Date(dateStr);
      const monthNames = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];
      return `${monthNames[dateObj.getMonth()]} ${dateObj.getDate()}, ${dateObj.getFullYear()}`;
    },
    formatTime(timeStr) {
      if (!timeStr) return "";
      const [hour, minute] = timeStr.split(':').map(Number);
      const suffix = hour >= 12 ? 'PM' : 'AM';
      const formattedHour = hour % 12 || 12;
      return `${formattedHour}:${minute.toString().padStart(2, '0')} ${suffix}`;
    },
    async fetchPlaylists() {
  try {
    const tokenResponse = await SpotifyService.getToken();
    const token = tokenResponse.data.access_token;

    // Playlist IDs for the five playlists you want
    const playlistIds = [
      "3gV9fNBHxYroyN2xGp91tN", // Playlist 1
      "4gBPF7rkxIknJEeaJoRrcx", // Playlist 2
      "4OP09jcySxfZgcVvJ3X4xn", // Playlist 3
      "6DScZ8GRzjDhSXrZdksOI8", // Playlist 4
      "1pnIcIzBzuxzLTyZacmN8d"  // Playlist 5
    ];

    // Loop through the playlistIds and fetch data for each playlist
    const playlistPromises = playlistIds.map(async (playlistId) => {
      const playlistResponse = await SpotifyService.getPlaylist(token, playlistId);
      
      return {
        name: playlistResponse.data.name,
        tracks: playlistResponse.data.tracks.items.map(track => ({
          id: track.track.id,
          name: track.track.name,
          images: track.track.album.images,
          artists: track.track.artists.map(artist => artist.name).join(", "),
          genre: "Unknown"  // Hardcoded genre or you can fetch it if needed
        }))
      };
    });

    // Wait for all playlist data to be fetched
    const playlistsData = await Promise.all(playlistPromises);

    // Store the fetched playlist data in separate variables
    this.playlists = playlistsData;
  } catch (error) {
    console.error("Error fetching playlists:", error);
  }
},
    async searchSongs() {
      if (this.searchQuery.trim() === "") {
        this.searchResults = [];
        return;
    }

      try {
        const tokenResponse = await SpotifyService.getToken();
        const token = tokenResponse.data.access_token;

        // If a genre is selected, add it to the query
        let query = this.searchQuery;
        if (this.selectedGenre) {
      query += ` genre:"${this.selectedGenre}"`;
    }

        const searchResponse = await SpotifyService.searchTracks(query, token);
        const tracks = searchResponse.data.tracks.items;

        this.searchResults = tracks.map(track => ({
          id: track.id,
          name: track.name,
          album: track.album,
          artists: track.artists,
          genre: this.selectedGenre || "Unknown"
        }));
    }     catch (error) {
        console.error("Error searching for songs:", error);
  }
},
    async createNewPlaylist() {
      if (!this.selectedTracks.length) {
        alert("Please select at least one song to create the playlist.");
        return;
      }

      // Use the inputted playlist name or fallback to a default name

      SpotifyService.getToken().then(
        (response) => {
          this.token = response.data.access_token;

          console.log('did I get a token back?')
          console.log(this.token);

          SpotifyService.createPlaylist(
            this.userId,
            playlistName,
            this.token
          ).then(
              (response) => {
                this.playlistName = playlistName;
                alert("Playlist created successfully!");
              }
          )
        }

      );

      const playlistName = this.playlistNameInput.trim() || "My Custom Playlist";

      // try {
      //   // Get token
      //   const tokenResponse = await SpotifyService.getToken();
      //   this.token = tokenResponse.data.access_token;

      //   // Create the playlist
      //   const playlistResponse = await SpotifyService.createPlaylist(
      //     this.userId,
      //     playlistName,
      //     this.token
      //   );

      //   // Add selected tracks to the new playlist
      //   await SpotifyService.addTracksToPlaylist(playlistResponse.data.id, this.selectedTracks, this.token);

      //   this.playlistName = playlistName; // Set playlist name
      //   alert("Playlist created successfully!");
      // } catch (error) {
      //   console.error("Error creating playlist:", error);
      // }
    },

    async fetchGenres() {
    try {
      console.log("Fetching token...");
      const tokenResponse = await SpotifyService.getToken();
      const token = tokenResponse.data.access_token;
      console.log("Token received:", token);

      console.log("Fetching available genres...");
      const genreResponse = await SpotifyService.getAvailableGenres(token);
      console.log("Genre Response:", genreResponse.data);

      this.availableGenres = genreResponse.data.genres || []; // Prevents undefined issues
    } catch (error) {
      console.error("Error fetching genres:", error);
    }
  },

  loginWithSpotify() {
      const authUrl = `https://accounts.spotify.com/authorize?client_id=${CLIENT_ID}&response_type=token&redirect_uri=${encodeURIComponent(
        REDIRECT_URI
      )}&scope=${SCOPES.join("%20")}`;
      window.location.href = authUrl;
    },

    // Function to extract the token from the URL
    getSpotifyTokenFromURL() {
      const hashParams = new URLSearchParams(window.location.hash.replace('#', '?'));
      const token = hashParams.get('access_token');
      if (token) {
        this.accessToken = token;
        // Now you can use the token for Spotify API calls
        console.log("Access Token: ", this.accessToken);
      } else {
        console.error('No token found in the URL.');
      }
    },

    async fetchTracks(playlistId) {
      try {
        const token = localStorage.getItem('spotify_token');
        if (!token) {
          console.error('No Spotify token found');
          return;
        }
        const data = await SpotifyService.getPlaylistTracks(playlistId, token);
        this.tracks = data.items.map(item => item.track);
      } catch (error) {
        console.error('Error fetching tracks:', error);
      }
    },

    upvote() {
    this.upvotes++;
  },
  downvote() {
    this.downvotes++;
  },

  resetVotes() {
      this.upvotes = 0;
      this.downvotes = 0;
    },
  

  },

  

  created() {
    this.fetchMyEvents();
    this.fetchPlaylists();
    this.fetchGenres();
    
  }
};
</script>

<style scoped>
.dj-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #7f00ff, #e100ff);
  color: #fff;
  font-family: "Courier New", Courier, monospace;
  padding: 20px;
  display: flex;
  flex-direction: column;
}

/* HEADER */
.dj-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.dj-title {
  font-size: 3rem;
  text-shadow: 2px 2px 4px #000;
}
.back-btn {
  background: transparent;
  border: 2px solid #fff;
  color: #fff;
  padding: 10px 20px;
  border-radius: 4px;
  cursor: pointer;
  transition: background 0.3s;
}
.back-btn:hover {
  background: #fff;
  color: #7f00ff;
}

/* CHANGE: DJ Images Section */
.dj-images-section {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-bottom: 30px;
}
.dj-image {
  width: 500px; /* Adjust as needed for your layout */
  height: auto;
  border-radius: 8px;
  border: 2px solid #fff;
}

/* CREATE EVENT SECTION */
.create-event-section {
  text-align: center;
  margin-bottom: 30px;
}

.toggle-btn {
  background-color: #fff;
  color: #7f00ff;
  padding: 10px 20px;
  font-size: 1.1rem;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background 0.3s;
  margin-bottom: 15px;
}
.toggle-btn:hover {
  background-color: #7f00ff;
  color: #fff;
}
.create-event-wrapper {
  max-width: 700px;
  margin: 0 auto 30px auto;
}

/* CREATE PLAYLIST SECTION */

.create-playlist-section {
  text-align: center;
  margin-bottom: 30px;
}

/* EVENTS SECTION */
.events-section {
  text-align: center;
  margin-bottom: 30px;
}
.section-title {
  font-size: 2rem;
  margin-bottom: 15px;
  text-shadow: 2px 2px 4px #000;
}
.large-boxes-container {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 20px;
}
.large-box {
  background-color: black;
  color: white;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
  font-size: 18px;
  padding: 20px;
  height: 400px;
  width: calc(40% - 10px);
  box-sizing: border-box;
  border-radius: 12px;
  border: 2px solid white;
}
.large-box p {
  margin: 5px 0;
  font-size: 16px;
}
.large-box p:first-child {
  font-size: 20px;
  font-weight: bold;
}

/*SPOTIFY  */

.playlist-container {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  justify-content: center;
}

.playlist-card {
  display: flex;
  align-items: center;
  background: #1e1e1e;
  padding: 15px;
  border-radius: 10px;
  width: 600px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.2);
}

.album-cover {
  width: 80px;
  height: 80px;
  border-radius: 5px;
  margin-right: 15px;
}

.track-info {
  flex-grow: 1;
}

.track-name {
  font-size: 16px;
  color: #fff;
}

.track-artist,
.track-genre {
  font-size: 14px;
  color: #bbb;
}

.spotify-player {
  width: 300px;
  height: 80px;
}

/* Styling for the dropdown */
.playlist-select {
  width: 20%;
  padding: 10px;
  margin: 10px 0;
  font-size: 1rem;
  border: 1px solid #ccc;
  border-radius: 5px;
  background-color: #f9f9f9;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
  transition: border-color 0.3s ease;
}

.playlist-select:focus {
  outline: none;
  border-color: #007bff;
  background-color: #eaf4ff;
}

.dropdown-container {
  justify-content: center;
  display: flex;
  flex-direction: column;
  width: 100%;
}

.playlist-title {
  font-size: 1.5rem;
  font-weight: bold;
  margin-bottom: 10px;
}

<style scoped>
/* Event Box Styling with Background Image */
.large-box {
  background-image: url('/recordwhitesleeve.png'); /* Use the uploaded image as the background */
  background-size: cover;
  background-position: center;
  color: white;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: 20px;
  width: 300px;
  height: 200px;
  margin: 10px;
  border-radius: 8px;
  text-align: center;
}
/* HEADER */
.header {
  position: relative; /* Required for absolute positioning of filter-container */
  width: 100%;
  margin-bottom: 0px;
}
.header-gif {
  width: 100%;
  height: auto;
  object-fit: cover;  /* Ensures the GIF fills the container without distortion */
  will-change: transform; /* Improves performance when resizing or animating */
}

/* Optional: Add this keyframe to smooth the transition */
@keyframes smoothGif {
  0% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.05);  /* Adds a slight zoom effect */
  }
  100% {
    transform: scale(1);
  }
}

/* Import fonts from public folder */
@font-face {
  font-family: 'Lato';
  src: url('/Lato-Regular.ttf') format('truetype'),
       url('/Lato-Italic.ttf') format('truetype'),
       url('/Lato-Light.ttf') format('truetype'),
       url('/Lato-LightItalic.ttf') format('truetype'),
       url('/Lato-BoldItalic.ttf') format('truetype'),
       url('/Lato-Thin.ttf') format('truetype'),
       url('/Lato-ThinItalic.ttf') format('truetype');
}

@font-face {
  font-family: 'DANGSIK';
  src: url('/DANGSIKINKTRAP.otf') format('opentype'),
       url('/DANGSIKINKTRAP.ttf') format('truetype'),
       url('/DANGSIKINKTRAPOBLIQUE.otf') format('opentype'),
       url('/DANGSIKINKTRAPOBLIQUE.ttf') format('truetype'),
       url('/DANGSIKOBLIQUE.otf') format('opentype'),
       url('/DANGSIKOBLIQUE.ttf') format('truetype'),
       url('/DANGSIK.otf') format('opentype'),
       url('/DANGSIK.ttf') format('truetype');
}

/* Apply fonts to the landing page */
.landing-page {
  background-color: rgb(141, 103, 175);
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px;
  min-height: 100vh;
  box-sizing: border-box;
  font-family: 'Lato', Arial, Helvetica, sans-serif; /* Default font for the page */
  color: #FFFFFF; /* Default font color set to white for readability */
}

.filter-container {
  position: absolute;
  bottom: 50px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 15px;
  background-color: rgba(0, 0, 0, 0.6);
  padding: 20px;
  border-radius: 10px;
  width: 80%;
  max-width: 1000px;
  font-family: 'DANGSIK', sans-serif; /* Apply DANGSIK font to filter section */
}

.filter-title {
  width: 100%;
  text-align: center;
  font-size: 22px;
  font-weight: bold;
  margin-bottom: 10px;
  color: #FF00FF; /* Bright pink for the filter title */
}

.filter-row {
  display: flex;
  justify-content: center;
  gap: 15px;
  width: 100%;
}

.filter-input {
  padding: 12px;
  font-size: 16px;
  width: 250px;
  border-radius: 8px;
  border: 1px solid white;
  background-color: #2C2C2C; /* Dark background for inputs */
  color: #FFFFFF; /* White text inside input */
}
/* Event List Section */
.large-boxes-container {
  display: flex;
  flex-wrap: wrap; /* Ensures event boxes wrap into rows */
  justify-content: center; /* Centers the boxes horizontally */
  gap: 20px; /* Adds space between the event boxes */
  width: 100%;
}

/* Event Box Styling with Background Image */
.large-box {
  background-image: url('/recordwhitesleeve.png'); /* Use the uploaded image as the background */
  background-size: cover;
  background-position: center;
  color: rgb(251, 23, 23);
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: 20px;
  width: 300px; /* Fixed width for event boxes */
  height: 200px; /* Fixed height for event boxes */
  margin: 10px;
  border-radius: 8px;
  text-align: center;
  background-color: transparent; /* No additional background */
  border: none; /* No border for clean look */
  font-family: 'Lato', Arial, Helvetica, sans-serif;
}
/* Event Name Styling */
.event-name {
  font-size: 1.6rem; /* Slightly bigger font size for better emphasis */
  font-weight: bold;
  margin-bottom: 10px;
  color: #FFD700; /* Gold for event name for vibrancy */
  text-shadow: 2px 2px 10px rgba(0, 0, 0, 0.7); /* Adding shadow for visibility */
}

/* Event Date Styling */
.event-date {
  font-size: 1.2rem; /* Slightly smaller than the event name */
  font-style: italic;
  color: #00FA9A; /* Medium spring green for a clean look */
  text-shadow: 2px 2px 10px rgba(0, 0, 0, 0.7); /* Added shadow for readability */
}

/* Event Time Styling */
.event-time {
  font-size: 1.2rem; /* Same size as event date */
  color: #FF6347; /* Tomato red for event time */
  font-weight: bold;
  text-shadow: 2px 2px 10px rgba(0, 0, 0, 0.7); /* Added shadow for readability */
}

/* No Events Message */
.no-events {
  font-size: 1.2rem;
  font-style: italic;
  color: #e0e0e0;
  margin-top: 1rem;
}

/* My Events Button Styling */
.my-events-btn {
  background-color: #FF00FF; /* Pink color for the button */
  color: white;
  font-size: 1rem;
  padding: 10px 20px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.my-events-btn:hover {
  background-color: #FF4500; /* Red/orange color on hover */
}

/* "My events only" checkbox text */
.checkbox-label {
  color: white; /* White color for checkbox label */
  font-size: 16px;
  font-weight: bold;
  display: flex;
  align-items: center;
}
.playlist-title {
  color: #FF4500; /* Bright orange for playlist title */
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 20px;
  font-family: 'DANGSIK', sans-serif;
}

.playlist-container {
  display: flex;
  justify-content: center;
  flex-wrap: wrap;
  gap: 20px;
}
.playlist-card {
  background-color: white;
  padding: 10px;
  border-radius: 10px;
  text-align: center;
  width: 150px;
  box-shadow: 0px 4px 6px rgba(0, 0, 0, 0.1);
}
.playlist-image {
  width: 150px;
  height: 150px;
  border-radius: 10px;
  object-fit: cover;
}
.loading-text {
  color: white;
  font-size: 16px;
  margin-top: 10px;
}

/*SPOTIFY  */
.playlist-container {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  justify-content: center;
}

.playlist-card {
  display: flex;
  align-items: center;
  background: #1e1e1e;
  padding: 15px;
  border-radius: 10px;
  width: 600px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.2);
}

.album-cover {
  width: 80px;
  height: 80px;
  border-radius: 5px;
  margin-right: 15px;
}

.track-info {
  flex-grow: 1;
}

.track-name {
  font-size: 16px;
  color: #fff;
}

.track-artist,
.track-genre {
  font-size: 14px;
  color: #bbb;
}

.spotify-player {
  width: 300px;
  height: 80px;
}

/* Playlist Dropdown Section */
.playlist-dropdown-container {
  margin-top: 40px; /* Space between events and dropdown */
  width: 30%;
  display: flex;
  flex-direction: column;
  align-items: center;
  background-color: rgba(0, 0, 0, 0.6); /* Semi-transparent dark background */
  padding: 20px;
  border-radius: 10px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1); /* Slight shadow to make it stand out */
  max-width: 1000px; /* Same max width as filter-container */
  margin-bottom: 30px; /* Space below dropdown */
}

/* Label for Playlist Dropdown */
.playlist-dropdown-label {
  color: white;
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 15px;
  text-align: center;
}

/* Dropdown select for playlists */
.playlist-dropdown {
  width: 40%; /* Same width as filter input */
  padding: 12px;
  font-size: 16px;
  border-radius: 8px;
  border: 1px solid white;
  background-color: white;
}

/* Ensure all dropdown options have similar padding */
.playlist-dropdown option {
  padding: 10px;
}

.vote-section {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px;
}

.vote-button {
  background: none;
  border: none;
  font-size: 20px;
  cursor: pointer;
  transition: transform 0.2s ease;
}

.vote-button:hover {
  transform: scale(1.2);
}

.upvote {
  color: green;
}

.downvote {
  color: red;
}

.vote-count {
  font-weight: bold;
  margin: 0 5px;
}


</style>
