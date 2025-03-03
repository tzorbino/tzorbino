<template>
  <div id="register" class="text-center">
    <!-- Logo at the top of the page -->
    <div class="logo-container">
      <img src="/contrastlogowhite.png" alt="Logo" class="logo" />
    </div>

    <!-- Replaced background video with the new image -->
    <div class="background-image">
      <img src="/rock2.png" alt="Rock Background" />
    </div>

    <!-- Wallpaper overlay with transparency -->
    <div class="wallpaper-overlay"></div>

    <!-- Wrapped the form in an overlay container for readability -->
    <div class="register-overlay">
      <form @submit.prevent="register">
        <h1>SIGN UP. LOG IN. TAP IN.</h1>
        <div role="alert" v-if="registrationErrors">
          {{ registrationErrorMsg }}
        </div>
        <div class="form-input-group">
          <label for="username">Username</label>
          <input type="text" id="username" v-model="user.username" required autofocus />
        </div>
        <div class="form-input-group">
          <label for="password">Password</label>
          <input type="password" id="password" v-model="user.password" required />
        </div>
        <div class="form-input-group">
          <label for="confirmPassword">Confirm Password</label>
          <input type="password" id="confirmPassword" v-model="user.confirmPassword" required />
        </div>
        <div class="form-input-group">
          <label for="role">Select Role</label>
          <select id="role" v-model="user.role" required>
            <option value="" disabled>Select a role</option>
            <option value="user">User</option>
            <option value="host">Host</option>
            <option value="dj">DJ</option>
          </select>
        </div>
        <button type="submit">Create Account</button>
        <p>
          <router-link :to="{ name: 'login' }">You look familiar. Log in!</router-link>
        </p>
      </form>
    </div>
  </div>
</template>

<script>
import authService from '../services/AuthService';

export default {
  name: "RegisterView",
  data() {
    return {
      user: {
        username: '',
        password: '',
        confirmPassword: '',
        role: 'user',
      },
      registrationErrors: false,
      registrationErrorMsg: 'There were problems registering this user.',
    };
  },
  methods: {
    register() {
      if (this.user.password !== this.user.confirmPassword) {
        this.registrationErrors = true;
        this.registrationErrorMsg = 'Password & Confirm Password do not match.';
      } else {
        authService.register(this.user)
          .then((response) => {
            if (response.status === 201) {
              this.$router.push({
                path: '/login',
                query: { registration: 'success' },
              });
            }
          })
          .catch((error) => {
            const response = error.response;
            this.registrationErrors = true;
            if (response && response.status === 400) {
              this.registrationErrorMsg = 'Bad Request: Validation Errors';
            }
          });
      }
    },
    clearErrors() {
      this.registrationErrors = false;
      this.registrationErrorMsg = 'There were problems registering this user.';
    },
  },
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

#register {
  position: relative;
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  font-family: Arial, Helvetica, sans-serif;
  color: black;
}

/* Overlay container to hold form content */
.register-overlay {
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
