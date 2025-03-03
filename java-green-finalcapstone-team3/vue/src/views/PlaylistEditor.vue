<template>
  <div>hi?</div>
</template>

<script>
export default {

}
</script>

<style>

</style>

<!-- <template>
    <div>
      <h1>Playlist Editor</h1>
      
      <input v-model="playlistName" placeholder="Enter playlist name" />
      <button @click="createNewPlaylist">Create Playlist</button>
  
      <h2>Add Tracks</h2>
      <input v-model="trackUri" placeholder="Enter track URI" />
      <button @click="addTrack">Add Track</button>
  
      <h3>Created Playlists</h3>
      <ul>
        <li v-for="playlist in playlists" :key="playlist.id">
          <strong>{{ playlist.name }}</strong>
        </li>
      </ul>
    </div>
  </template>
  
  <script setup>
  import { ref, onMounted } from "vue";
  import { getPlaylists, createPlaylist, addTracksToPlaylist } from "@/services/spotify";
  
  const playlists = ref([]);
  const playlistName = ref("");
  const trackUri = ref("");
  const createdPlaylistId = ref(null);
  const userId = "your_spotify_user_id"; 
  
  const fetchPlaylists = async () => {
    playlists.value = await getPlaylists();
  };
  
  const createNewPlaylist = async () => {
    const newPlaylist = await createPlaylist(userId, playlistName.value);
    if (newPlaylist) {
      createdPlaylistId.value = newPlaylist.id;
      fetchPlaylists();
    }
  };
  
  const addTrack = async () => {
    if (!createdPlaylistId.value) {
      alert("Create a playlist first!");
      return;
    }
    await addTracksToPlaylist(createdPlaylistId.value, [trackUri.value]);
    trackUri.value = "";
  };
  
  onMounted(fetchPlaylists);
  </script> -->