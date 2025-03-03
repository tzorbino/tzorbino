<template>
  <div id="logout" class="logout-container">
    <!-- Record player animation as hero -->
    <div class="record-player-wrapper">
      <img 
        src="/record-player.gif" 
        alt="Record Player"
        class="record-player"
        v-bind:style="{ animation: playRecordAnimation ? 'playRecord 3s forwards' : '' }"
      />
    </div>

    <!-- Funny "Leaving a Party Without Saying Bye" GIF in the bottom-left corner -->
    <div class="funny-gif-wrapper">
      <img src="/logout.gif" alt="Leaving Party GIF" class="funny-gif" />
    </div>

    <h1 class="logout-title">You have been logged out!</h1>
    <button @click="goToLogin" class="logout-button">Go to Login</button>
  </div>
</template>

<script>
export default {
  data() {
    return {
      playRecordAnimation: true, // Flag to trigger record animation for 3 seconds
    };
  },
  methods: {
    goToLogin() {
      this.$router.push("/login"); // Redirect to login page
    }
  },
  created() {
    setTimeout(() => {
      this.playRecordAnimation = false; // Stops the animation after 3 seconds
    }, 3000);
  }
};
</script>

<style scoped>
/* Overall container for logout page */
.logout-container {
  position: relative;
  height: 100vh;
  background: #1e1e1e; /* Dark background to highlight the content */
  color: white;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  text-align: center;
  font-family: 'Arial', sans-serif;
}

/* Record Player Animation */
.record-player-wrapper {
  position: relative;
  margin-bottom: 20px;
}

.record-player {
  width: 600px; /* Larger record player */
  height: auto;
  object-fit: cover;
}

/* Animation for the record player */
@keyframes playRecord {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg); /* Make the record spin */
  }
}

/* Funny GIF styling with a cool frame */
.funny-gif-wrapper {
  position: absolute;
  bottom: 10px; /* Positioned at the bottom */
  left: 10px; /* Positioned to the left */
  width: 250px; /* Increased size of the GIF */
  padding: 10px;
  border: 6px solid #FF6347; /* Tomato red border for the frame */
  border-radius: 12px; /* Rounded corners */
  box-shadow: 0px 4px 15px rgba(0, 0, 0, 0.5); /* Shadow to make it pop */
}

.funny-gif {
  width: 100%;
  border-radius: 8px;
}

/* Button Styling */
.logout-title {
  font-size: 2rem;
  font-weight: bold;
  margin-top: 30px;
}

.logout-button {
  background-color: #FF6347; /* Hover effect with a tomato red color */ 
  color: white;
  font-size: 1.2rem;
  padding: 15px 30px;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: background-color 0.3s ease;
  margin-top: 20px;
}

.logout-button:hover {
  background-color: #FF00FF; /* Changed to a vibrant pink */
}
</style>
