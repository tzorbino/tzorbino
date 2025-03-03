<template>
    <div>
        {{ playListData.tracks.items }}
    </div>
</template>

<script>

import SpotifyService from '../services/SpotifyService.js';
export default {
    data() {
        return {
            token : '',
            playListData : []
        }
    },
    created() {
        SpotifyService.getToken().then( 
            (response)=> {
                console.log(response.data)
                this.token = response.data.access_token;
                
                

                return SpotifyService.getPlaylist(this.token);
            })
            .then(playlistResponse => {
                this.playListData = playlistResponse.data;
            })
            .catch(error => {
                console.error("Error:", error);
            });
                /**
                 * TODOs:
                 * 1. On SpotifyService.js make a method that calls playlists/3gV9fNBHxYroyN2xGp91tN
                 *    You need to bake into the header, the token (this.token)
                 * 2. Call the method from #1, and save the results into the array playlistData
                 */
            
            
        


    }
}
</script>

<style>

</style>