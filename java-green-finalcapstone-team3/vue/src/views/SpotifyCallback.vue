<template>
    <div>Logging in...</div>
  </template>
  
  <script setup>
  import { onMounted } from "vue";
  import { useRouter } from "vue-router";
  
  const router = useRouter();
  
  const getAccessTokenFromUrl = () => {
    const hash = window.location.hash.substring(1).split("&");
    const token = hash.find((item) => item.startsWith("access_token"))?.split("=")[1];
  
    if (token) {
      localStorage.setItem("spotify_token", token);
      router.push("/playlists"); // Redirect to playlists page
    } else {
      router.push("/login"); // Redirect back to login if failed
    }
  };
  
  onMounted(() => {
    getAccessTokenFromUrl();
  });
  </script>