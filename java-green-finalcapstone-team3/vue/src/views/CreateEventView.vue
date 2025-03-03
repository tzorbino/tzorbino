<template>
    <div class="create-event">
      <h1>Create Event</h1>
      <form @submit.prevent="submitEvent">
        <div class="form-group">
          <label for="name">Event Name:</label>
          <input type="text" id="name" v-model="event.name" required />
        </div>
        <div class="form-group">
          <label for="eventDate">Event Date:</label>
          <input type="date" id="eventDate" v-model="event.eventDate" required />
        </div>
        <div class="form-group">
          <label for="startTime">Start Time:</label>
          <input type="time" id="startTime" v-model="event.startTime" required />
        </div>
        <div class="form-group">
          <label for="endTime">End Time:</label>
          <input type="time" id="endTime" v-model="event.endTime" required />
        </div>
        <div class="form-group">
          <label for="hosts">Assign Host(s):</label>
          <select id="hosts" v-model="selectedHosts" multiple required>
            <option v-for="host in hosts" :key="host.id" :value="host.id">
              {{ host.username }}
            </option>
          </select>
        </div>
        <button type="submit">Create Event</button>
      </form>
      <div v-if="errorMessage" class="error">{{ errorMessage }}</div>
      <div v-if="successMessage" class="success">{{ successMessage }}</div>
    </div>
  </template>
  
  <script>
  import axios from 'axios';
import EventService from '../services/EventService'; // Ensure the path is correct
  
  export default {
    name: 'CreateEventView',
    data() {
      return {
        event: {
          name: '',
          eventDate: '',
          startTime: '',
          endTime: ''
        },
        selectedHosts: [],
        hosts: [],
        errorMessage: '',
        successMessage: ''
      };
    },
    methods: {
      async fetchHosts() {
        try {
          // Fetch hosts from the backend endpoint
          const response = await axios.get(`${import.meta.env.VITE_REMOTE_API}/users/hosts`);
          this.hosts = response.data;
        } catch (error) {
          this.errorMessage = 'Failed to load hosts.';
          console.error('Error fetching hosts:', error);
        }
      },
      async submitEvent() {
        this.errorMessage = '';
        this.successMessage = '';
  
        // Validate that at least one host is selected
        if (this.selectedHosts.length === 0) {
          this.errorMessage = 'Please assign at least one host.';
          return;
        }
  
        // Build payload matching your CreateEventDto
        const payload = {
          name: this.event.name,
          eventDate: this.event.eventDate,
          startTime: this.event.startTime,
          endTime: this.event.endTime,
          hostIds: this.selectedHosts
        };
  
        try {
          const response = await EventService.createEvent(payload);
          this.successMessage = 'Event created successfully!';
          // Optionally, reset the form after success
          this.event = { name: '', eventDate: '', startTime: '', endTime: '' };
          this.selectedHosts = [];
        } catch (error) {
          console.error('Error creating event:', error);
          this.errorMessage = error.response && error.response.data
            ? error.response.data
            : 'Error creating event.';
        }
      }
    },
    mounted() {
      this.fetchHosts();
    }
  };
  </script>
  
  <style scoped>
  .create-event {
    max-width: 600px;
    margin: 0 auto;
    padding: 1rem;
  }
  
  .form-group {
    margin-bottom: 1rem;
    display: flex;
    flex-direction: column;
  }
  
  label {
    margin-bottom: 0.5rem;
  }
  
  input, select {
    padding: 0.5rem;
    font-size: 1rem;
  }
  
  button {
    padding: 0.75rem 1.5rem;
    font-size: 1rem;
    cursor: pointer;
  }
  
  .error {
    color: red;
    margin-top: 1rem;
  }
  
  .success {
    color: green;
    margin-top: 1rem;
  }
  </style>
  