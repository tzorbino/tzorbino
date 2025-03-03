<template>
  <div id="login" class="login-container">
    <!-- Logo at the top of the page -->
    <div class="logo-container">
      <img src="/contrastlogowhite.png" alt="Logo" class="logo" />
    </div>

    <!-- Replaced background video with the new image -->
    <div class="background-image">
      <img src="/MAKE LOUNGE 2.png" alt="Rock Background" />
    </div>

    <!-- Wallpaper overlay with transparency -->
    <div class="wallpaper-overlay"></div>

    <!-- Login Form -->
    <div class="login-overlay">
      <form @submit.prevent="login">
        <h1>SIGN IN. LOG IN. TAP IN.</h1>
        <div role="alert" v-if="invalidCredentials">
          Invalid username and password!
        </div>
        <div role="alert" v-if="$route.query.registration">
          Thank you for registering, please sign in.
        </div>
        <div class="form-input-group">
          <label for="username">Username</label>
          <input type="text" id="username" v-model="user.username" required autofocus />
        </div>
        <div class="form-input-group">
          <label for="password">Password</label>
          <input type="password" id="password" v-model="user.password" required />
        </div>
        <button type="submit">Sign in</button>
        <p>
          <router-link :to="{ name: 'register' }">You look new. Sign Up!</router-link>
        </p>
      </form>
    </div>
  </div>
</template>

<script>
import authService from "../services/AuthService";

export default {
  name: "LoginView",
  data() {
    return {
      user: {
        username: "",
        password: ""
      },
      invalidCredentials: false
    };
  },
  methods: {
    login() {
      authService.login(this.user)
        .then(response => {
          if (response.status === 200) {
            // Store token and user in Vuex/localStorage
            this.$store.commit("SET_AUTH_TOKEN", response.data.token);
            this.$store.commit("SET_USER", response.data.user);

            // Role-based redirection
            const userRole = response.data.user.authorities[0].name;
            if (userRole === "ROLE_DJ") {
              this.$router.push({ name: "djPage" });
            } else if (userRole === "ROLE_HOST") {
              this.$router.push({ name: "hostPage" });
            } else {
              this.$router.push({ name: "landing" });
            }
          }
        })
        .catch(error => {
          const response = error.response;
          if (response && response.status === 401) {
            this.invalidCredentials = true;
          }
        });
    }
  }
};
</script>

<style scoped>
/* Logo Styling */
.logo-container {
  position: absolute;
  top: 20px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 1; /* Ensures the logo stays on top */
}

.logo {
  width: 150px; /* Adjust based on your design */
  height: auto;
}

/* Background image styling */
.background-image img {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  z-index: -1; /* Keep the background image behind the form */
}

/* Wallpaper overlay with transparency */
.wallpaper-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.4); /* Semi-transparent black background */
  z-index: -1; /* Positioned behind the form */
}

#login {
  position: relative;
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  font-family: Arial, Helvetica, sans-serif;
  color: black;
}

/* Overlay container to hold form content */
.login-overlay {
  position: relative;
  z-index: 1;
  background: rgba(255, 255, 255, 0.8); /* Semi-transparent background for readability */
  padding: 2rem;
  max-width: 400px;
  width: 100%;
  border-radius: 8px;
}

/* Form styling remains unchanged */
.form-input-group {
  margin-bottom: 1rem;
}
label {
  margin-right: 0.5rem;
}
</style>
