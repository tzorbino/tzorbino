<template>
  <div>
    <h1>Upcoming Events</h1>
    <ul>
      <li v-for="event in events" :key="event.id">
        {{ event.name }} - {{ event.date }}
      </li>
    </ul>

    <h2>Create a New Event</h2>
    <form @submit.prevent="createEvent">
      <input v-model="newEvent.name" placeholder="Event Name" required />
      <input v-model="newEvent.date" type="date" required />
      <button type="submit">Add Event</button>
    </form>
  </div>
</template>

<script>

import eventService from '../services/EventService';

export default {
    data(){
        return{
            events: [],
            newEvent: {name: '', date: ''}
        };
    },
    methods: {
    async fetchEvents() {
      try {
        const response = await eventService.getEvents();
        this.events = response.data;
      } catch (error) {
        console.error('Error fetching events:', error);
      }
    },
    async createEvent() {
      try {
        await eventService.createEvent(this.newEvent);
        this.newEvent = { name: '', date: '' }; // Reset form
        this.fetchEvents(); // Refresh list
      } catch (error) {
        console.error('Error creating event:', error);
      }
    }
  },
  mounted() {
    this.fetchEvents();
  }

}
</script>

<style>

</style>