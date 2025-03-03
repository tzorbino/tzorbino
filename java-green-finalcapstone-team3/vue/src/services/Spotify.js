// import axios from "axios";
// import { useAuthStore } from "../store/spotifyAuth.js";

// export const getPlaylists = async () => {
//     const authStore = useAuthStore();
//     try {
//       const response = await axios.get("https://api.spotify.com/v1/me/playlists", {
//         headers: { Authorization: `Bearer ${authStore.token}` },
//       });
//       return response.data.items;
//     } catch (error) {
//       console.error("Error fetching playlists:", error);
//       return [];
//     }
//   };

//   export const createPlaylist = async (userId, name) => {
//     const authStore = useAuthStore();
//     try {
//       const response = await axios.post(
//         `https://api.spotify.com/v1/users/${userId}/playlists`,
//         { name, public: false },
//         { headers: { Authorization: `Bearer ${authStore.token}`, "Content-Type": "application/json" } }
//       );
//       return response.data;
//     } catch (error) {
//       console.error("Error creating playlist:", error);
//       return null;
//     }
//   };

//   export const addTracksToPlaylist = async (playlistId, trackUris) => {
//     const authStore = useAuthStore();
//     try {
//       const response = await axios.post(
//         `https://api.spotify.com/v1/playlists/${playlistId}/tracks`,
//         { uris: trackUris },
//         { headers: { Authorization: `Bearer ${authStore.token}`, "Content-Type": "application/json" } }
//       );
//       return response.data;
//     } catch (error) {
//       console.error("Error adding tracks:", error);
//       return null;
//     }
//   };
