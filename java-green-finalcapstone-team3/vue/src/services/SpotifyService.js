import axios from 'axios';

const CLIENT_ID = "223feb65c9eb4d549b0d4b44eb859bda"; // Replace with your actual Client ID
const REDIRECT_URI = "http://localhost:5173/dj"; // Update with your frontend URL
const SCOPES = [
    "playlist-modify-public",
    "playlist-modify-private",
    "user-read-private"
];

const http = axios.create({

    baseURL: "https://accounts.spotify.com/"
});

const apiHttp = axios.create({
    baseURL: "https://api.spotify.com/v1/"
})

export default{

    getToken() {

        const grantsBody = new URLSearchParams();
        grantsBody.append('grant_type', 'client_credentials');

        return http.post('api/token', grantsBody, 
        { 
            auth: {
                username : '223feb65c9eb4d549b0d4b44eb859bda',
                password: '0f1f4b5129e74e9f88cecd96a7c461fa'
            }
        });
    },

    getPlaylist(token, playlistId) {
        return apiHttp.get(`playlists/${playlistId}`, {
          headers: {
            Authorization: `Bearer ${token}`
          }
        });
      },

      async getPlaylistTracks(playlistId, token) {
        try {
          const response = await axios.get(`https://api.spotify.com/v1/playlists/${playlistId}/tracks`, {
            headers: { Authorization: `Bearer ${token}` },
          });
          return response.data;
        } catch (error) {
          console.error('Error fetching tracks:', error);
          throw error;
        }
      },

    getArtistGenres(token, artistId) {
        return apiHttp.get(`artists/${artistId}`,{
            headers: {
                Authorization: `Bearer ${token}`,
            },
        });
    },
    loginWithSpotify() {
        const authUrl = `https://accounts.spotify.com/authorize?client_id=${CLIENT_ID}&response_type=token&redirect_uri=${encodeURIComponent(
            REDIRECT_URI
        )}&scope=${SCOPES.join("%20")}`;

        window.location.href = authUrl;
    },

    /** 🔹 Get User's Spotify Access Token */
    getUserAccessToken() {
        const hash = window.location.hash.substring(1);
        const params = new URLSearchParams(hash);
        return params.get("access_token");
    },

    /** 🔹 Get User ID from Spotify */
    async getUserId(token) {
        const response = await apiHttp.get("me", {
            headers: { Authorization: `Bearer ${token}` }
        });
        return response.data.id;
    },

    /** 🔹 Create a Playlist for the Authenticated User */
    createPlaylist(userId, playlistName, token) {

        console.log('what we are sending')
        console.log(playlistName)
        console.log(token)

        console.log("Creating playlist for user:", userId, "with name:", playlistName); // Debugging log
      
        // Make API call to create playlist
        apiHttp.post(
          `https://api.spotify.com/v1/users/1223676492/playlists`,
          {
            name: playlistName,
            public: true,
          },
          {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          }
        );
        console.log("Playlist created response:", response); // Debugging log
        return response;
      },

    /** 🔹 Add Tracks to a Playlist */
    async addTracksToPlaylist(playlistId, trackUris, token) {
        await apiHttp.post(
            `playlists/${playlistId}/tracks`,
            { uris: trackUris },
            {
                headers: {
                    Authorization: `Bearer ${token}`,
                    "Content-Type": "application/json"
                }
            }
        );
    },

    searchTracks(query, token) {
        return apiHttp.get('search', {
          params: {
            q: query,
            type: 'track',
            limit: 10, // Number of results
          },
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });
      },

      getAvailableGenres(token) {
  return apiHttp.get('recommendations/available-genre-seeds', {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });
}

}