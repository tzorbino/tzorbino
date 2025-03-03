import { defineStore } from "pinia";

export const useAuthStore = defineStore("auth", {
  state: () => ({
    token: localStorage.getItem("spotify_token") || null,
  }),
  actions: {
    setToken(token) {
      this.token = token;
      localStorage.setItem("spotify_token", token);
    },
    logout() {
      this.token = null;
      localStorage.removeItem("spotify_token");
    },
  },
});