<template>
    <div>hi</div>
</template>

<script>
export default {

}
</script>

<style>

</style>

<!-- <template>
    <div>
      <h1>My Playlists</h1>
      <button @click="logout">Logout</button>
      <ul>
        <li v-for="playlist in playlists" :key="playlist.id">
          <img :src="playlist.images[0]?.url" width="100" />
          <strong>{{ playlist.name }}</strong>
        </li>
      </ul>
    </div>
  </template>
  
  <script setup>
  import { ref, onMounted } from "vue";
  import { getPlaylists } from "@/services/spotify";
  import { useAuthStore } from "@/store/auth";
  
  const authStore = useAuthStore();
  const playlists = ref([]);
  
  const logout = () => {
    authStore.logout();
    window.location.href = "/spotify-login"; // Redirect to login
  };
  
  onMounted(async () => {
    playlists.value = await getPlaylists();
  });
  </script> -->