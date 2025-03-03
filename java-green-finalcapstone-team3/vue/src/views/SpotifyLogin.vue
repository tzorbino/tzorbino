<template>
    <div class="login-container">
      <button @click="loginWithSpotify">Login with Spotify</button>
    </div>
  </template>
  
  <script setup>
  const loginWithSpotify = () => {
    const clientId = "223feb65c9eb4d549b0d4b44eb859bda";
    const redirectUri = "http://localhost:5173/callback"; // Must match Spotify app settings
    const scopes = [
      "playlist-read-private",
      "playlist-modify-public",
      "playlist-modify-private"
    ].join(" ");
  
    window.location.href = `https://accounts.spotify.com/authorize?client_id=${clientId}&response_type=token&redirect_uri=${redirectUri}&scope=${scopes}`;
  };
  </script>
  
  <style scoped>
  .login-container {
    text-align: center;
    margin-top: 50px;
  }
  </style>